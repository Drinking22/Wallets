package com.example.wallets.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

public class WalletRequest {
    private UUID walletId;
    private OperationType type;
    private BigDecimal amount;

    public WalletRequest(UUID walletId, OperationType type, BigDecimal amount) {
        this.walletId = walletId;
        this.type = type;
        this.amount = amount;
    }

    public UUID getWalletId() {
        return walletId;
    }

    public void setWalletId(UUID walletId) {
        this.walletId = walletId;
    }

    public OperationType getType() {
        return type;
    }

    public void setType(OperationType type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}

