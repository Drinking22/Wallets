package com.example.wallets.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public class WalletResponse {
    private UUID walletId;
    private BigDecimal amount;

    public WalletResponse(UUID walletId, BigDecimal amount) {
        this.walletId = walletId;
        this.amount = amount;
    }

    public UUID getWalletId() {
        return walletId;
    }

    public void setWalletId(UUID walletId) {
        this.walletId = walletId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
