package com.company.scopery.modules.quote.quoteline.domain.model;

import com.company.scopery.modules.quote.quoteline.domain.enums.QuoteLineType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.UUID;

public record QuoteLine(
        UUID id,
        UUID quoteVersionId,
        UUID projectId,
        UUID sourcePhaseFinanceId,
        UUID sourceProjectPhaseId,
        QuoteLineType lineType,
        String name,
        String description,
        BigDecimal quantity,
        BigDecimal unitPrice,
        BigDecimal amount,
        String currencyCode,
        int displayOrder,
        boolean clientVisible,
        String internalNote,
        int version,
        Instant createdAt,
        Instant updatedAt
) {
    public static QuoteLine create(
            UUID quoteVersionId,
            UUID projectId,
            UUID sourcePhaseFinanceId,
            UUID sourceProjectPhaseId,
            QuoteLineType lineType,
            String name,
            String description,
            BigDecimal quantity,
            BigDecimal unitPrice,
            String currencyCode,
            int displayOrder,
            boolean clientVisible,
            String internalNote) {
        BigDecimal qty = quantity == null ? BigDecimal.ONE : quantity;
        BigDecimal price = unitPrice == null ? BigDecimal.ZERO : unitPrice;
        return new QuoteLine(
                UUID.randomUUID(), quoteVersionId, projectId, sourcePhaseFinanceId, sourceProjectPhaseId,
                lineType, name, description, qty, price, amountOf(qty, price), currencyCode,
                displayOrder, clientVisible, internalNote, 0, null, null);
    }

    public QuoteLine update(
            QuoteLineType lineType,
            String name,
            String description,
            BigDecimal quantity,
            BigDecimal unitPrice,
            Integer displayOrder,
            Boolean clientVisible,
            String internalNote) {
        BigDecimal qty = quantity == null ? this.quantity : quantity;
        BigDecimal price = unitPrice == null ? this.unitPrice : unitPrice;
        return new QuoteLine(
                id, quoteVersionId, projectId, sourcePhaseFinanceId, sourceProjectPhaseId,
                lineType == null ? this.lineType : lineType,
                name == null ? this.name : name,
                description,
                qty, price, amountOf(qty, price), currencyCode,
                displayOrder == null ? this.displayOrder : displayOrder,
                clientVisible == null ? this.clientVisible : clientVisible,
                internalNote, version, createdAt, updatedAt);
    }

    public QuoteLine withDisplayOrder(int order) {
        return new QuoteLine(
                id, quoteVersionId, projectId, sourcePhaseFinanceId, sourceProjectPhaseId,
                lineType, name, description, quantity, unitPrice, amount, currencyCode,
                order, clientVisible, internalNote, version, createdAt, updatedAt);
    }

    private static BigDecimal amountOf(BigDecimal qty, BigDecimal price) {
        return qty.multiply(price).setScale(4, RoundingMode.HALF_UP);
    }
}
