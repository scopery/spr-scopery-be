package com.company.scopery.modules.quote.calculation;

import com.company.scopery.common.exception.AppException;
import com.company.scopery.modules.quote.shared.error.QuoteErrorCatalog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TargetMarginSolverTest {

    private TargetMarginSolver solver;

    @BeforeEach
    void setUp() {
        solver = new TargetMarginSolver();
    }

    @Test
    void solver_costBaseAndTargetMargin_calculatesRequiredContractValue() {
        // 700M / (1 - 0.30) = 1B
        BigDecimal result = solver.solveRequiredContractValue(
                new BigDecimal("700000000"), new BigDecimal("30.0"));
        assertThat(result).isEqualByComparingTo("1000000000.0000");
    }

    @Test
    void solver_targetMarginZero_returnsCostBase() {
        BigDecimal result = solver.solveRequiredContractValue(
                new BigDecimal("700000000"), BigDecimal.ZERO);
        assertThat(result).isEqualByComparingTo("700000000.0000");
    }

    @Test
    void solver_targetMargin100_rejected() {
        assertThatThrownBy(() -> solver.solveRequiredContractValue(
                new BigDecimal("100"), new BigDecimal("100")))
                .isInstanceOf(AppException.class)
                .extracting(ex -> ((AppException) ex).getErrorCode())
                .isEqualTo(QuoteErrorCatalog.QUOTE_SOLVER_INVALID_TARGET_MARGIN.code());
    }

    @Test
    void solver_targetMarginAbove100_rejected() {
        assertThatThrownBy(() -> solver.solveRequiredContractValue(
                new BigDecimal("100"), new BigDecimal("120")))
                .isInstanceOf(AppException.class)
                .extracting(ex -> ((AppException) ex).getErrorCode())
                .isEqualTo(QuoteErrorCatalog.QUOTE_SOLVER_INVALID_TARGET_MARGIN.code());
    }

    @Test
    void solver_usesBigDecimal() {
        BigDecimal result = solver.solveRequiredContractValue(
                new BigDecimal("100"), new BigDecimal("25"));
        assertThat(result.scale()).isEqualTo(4);
        assertThat(result).isEqualByComparingTo("133.3333");
    }

    @Test
    void solver_negativeCostBase_rejected() {
        assertThatThrownBy(() -> solver.solveRequiredContractValue(
                new BigDecimal("-1"), new BigDecimal("30")))
                .isInstanceOf(AppException.class)
                .extracting(ex -> ((AppException) ex).getErrorCode())
                .isEqualTo(QuoteErrorCatalog.QUOTE_SOLVER_INVALID_COST_BASE.code());
    }
}
