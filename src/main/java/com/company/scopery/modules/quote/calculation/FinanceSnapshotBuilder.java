package com.company.scopery.modules.quote.calculation;

import com.company.scopery.modules.projectfinance.phasefinance.domain.model.ProjectPhaseFinance;
import com.company.scopery.modules.projectfinance.scenario.domain.model.ProjectFinanceScenario;
import com.company.scopery.modules.projectfinance.summary.domain.model.ProjectFinanceSummary;
import com.company.scopery.modules.quote.quoteline.domain.enums.QuoteLineType;
import com.company.scopery.modules.quote.quoteline.domain.model.QuoteLine;
import com.company.scopery.modules.quote.quoteversion.domain.enums.PricingMethod;
import com.company.scopery.modules.quote.shared.error.QuoteExceptions;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class FinanceSnapshotBuilder {

    private static final int SCALE = 4;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;

    private final ObjectMapper objectMapper;
    private final TargetMarginSolver solver;

    public FinanceSnapshotBuilder(ObjectMapper objectMapper, TargetMarginSolver solver) {
        this.objectMapper = objectMapper;
        this.solver = solver;
    }

    public String buildSnapshotJson(
            ProjectFinanceScenario scenario,
            ProjectFinanceSummary summary,
            List<ProjectPhaseFinance> phases) {
        try {
            ObjectNode root = objectMapper.createObjectNode();
            root.put("financeScenarioId", scenario.id().toString());
            root.put("status", scenario.status().name());
            root.put("currencyCode", scenario.currencyCode());
            root.put("formulaVersion", scenario.formulaVersion());
            root.put("plannedRevenue", nz(scenario.plannedRevenue()));
            if (summary != null) {
                ObjectNode s = root.putObject("summary");
                s.put("budgetOfCosts", nz(summary.budgetOfCosts()));
                s.put("totalDirectCost", nz(summary.totalDirectCost()));
                s.put("totalOverhead", nz(summary.totalOverhead()));
                s.put("plannedRevenue", nz(summary.plannedRevenue()));
                s.put("grossMargin", summary.grossMargin());
                s.put("profitBeforeTax", summary.profitBeforeTax());
            }
            ArrayNode phasesNode = root.putArray("phases");
            for (ProjectPhaseFinance phase : phases) {
                ObjectNode p = phasesNode.addObject();
                p.put("phaseFinanceId", phase.id().toString());
                p.put("projectPhaseId", phase.projectPhaseId().toString());
                p.put("phaseName", phase.phaseNameSnapshot());
                p.put("phaseOrder", phase.phaseOrder() == null ? 0 : phase.phaseOrder());
                p.put("plannedRevenue", nz(phase.plannedRevenue()));
                p.put("directCost", nz(phase.directCost()));
                p.put("overheadAmount", nz(phase.overheadAmount()));
                p.put("budgetOfCosts", nz(phase.budgetOfCosts()));
                p.put("revenuePercent", phase.revenuePercent());
            }
            return objectMapper.writeValueAsString(root);
        } catch (JsonProcessingException ex) {
            throw QuoteExceptions.financeSnapshotInvalid("Unable to serialize finance snapshot");
        }
    }

    public QuoteCalculationService.FinanceSnapshotAmounts parseAmounts(String financeSnapshotJson) {
        try {
            JsonNode root = objectMapper.readTree(financeSnapshotJson);
            JsonNode summary = root.path("summary");
            if (summary.isMissingNode() || summary.isNull()) {
                throw QuoteExceptions.financeSnapshotInvalid("Finance snapshot missing summary");
            }
            return new QuoteCalculationService.FinanceSnapshotAmounts(
                    decimal(summary, "budgetOfCosts"),
                    decimal(summary, "totalDirectCost"),
                    decimal(summary, "totalOverhead"),
                    null);
        } catch (JsonProcessingException ex) {
            throw QuoteExceptions.financeSnapshotInvalid("Unable to parse finance snapshot");
        }
    }

    public List<QuoteLine> generatePhaseLines(
            UUID quoteVersionId,
            UUID projectId,
            String currencyCode,
            PricingMethod pricingMethod,
            BigDecimal targetMarginPercent,
            String financeSnapshotJson) {
        try {
            JsonNode root = objectMapper.readTree(financeSnapshotJson);
            JsonNode summary = root.path("summary");
            BigDecimal budgetOfCosts = decimal(summary, "budgetOfCosts");
            BigDecimal totalPlannedRevenue = decimal(summary, "plannedRevenue");
            BigDecimal requiredContractValue = null;
            if (pricingMethod == PricingMethod.TARGET_MARGIN_SOLVER) {
                if (targetMarginPercent == null) {
                    throw QuoteExceptions.solverInvalidTargetMargin();
                }
                requiredContractValue = solver.solveRequiredContractValue(budgetOfCosts, targetMarginPercent);
            }

            ArrayNode phases = (ArrayNode) root.path("phases");
            List<QuoteLine> lines = new ArrayList<>();
            int order = 0;
            BigDecimal totalDirect = BigDecimal.ZERO;
            for (JsonNode phase : phases) {
                totalDirect = totalDirect.add(decimal(phase, "directCost"));
            }

            for (JsonNode phase : phases) {
                BigDecimal unitPrice;
                if (pricingMethod == PricingMethod.FROM_FINANCE_PLANNED_REVENUE
                        || pricingMethod == PricingMethod.PHASE_LINE_SUM) {
                    unitPrice = decimal(phase, "plannedRevenue");
                } else if (pricingMethod == PricingMethod.TARGET_MARGIN_SOLVER) {
                    unitPrice = allocateRequired(
                            requiredContractValue,
                            decimal(phase, "plannedRevenue"),
                            totalPlannedRevenue,
                            decimal(phase, "directCost"),
                            totalDirect);
                } else {
                    unitPrice = decimal(phase, "plannedRevenue");
                }

                String phaseName = phase.path("phaseName").asText("Phase");
                UUID phaseFinanceId = uuidOrNull(phase.path("phaseFinanceId").asText(null));
                UUID projectPhaseId = uuidOrNull(phase.path("projectPhaseId").asText(null));
                lines.add(QuoteLine.create(
                        quoteVersionId, projectId, phaseFinanceId, projectPhaseId,
                        QuoteLineType.PHASE, phaseName, null,
                        BigDecimal.ONE, unitPrice, currencyCode, order++, true, null));
            }
            return lines;
        } catch (JsonProcessingException ex) {
            throw QuoteExceptions.financeSnapshotInvalid("Unable to parse finance snapshot for lines");
        }
    }

    private BigDecimal allocateRequired(
            BigDecimal required,
            BigDecimal phaseRevenue,
            BigDecimal totalRevenue,
            BigDecimal phaseDirect,
            BigDecimal totalDirect) {
        if (required == null) {
            return BigDecimal.ZERO.setScale(SCALE, ROUNDING);
        }
        if (totalRevenue.signum() > 0) {
            return required.multiply(phaseRevenue).divide(totalRevenue, SCALE, ROUNDING);
        }
        if (totalDirect.signum() > 0) {
            return required.multiply(phaseDirect).divide(totalDirect, SCALE, ROUNDING);
        }
        return BigDecimal.ZERO.setScale(SCALE, ROUNDING);
    }

    private static BigDecimal decimal(JsonNode node, String field) {
        JsonNode v = node.path(field);
        if (v.isMissingNode() || v.isNull()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(v.asText()).setScale(SCALE, ROUNDING);
    }

    private static BigDecimal nz(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    private static UUID uuidOrNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return UUID.fromString(value);
    }
}
