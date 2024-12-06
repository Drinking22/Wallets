package com.example.wallets.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
public class WalletRequest {
    private UUID walletId;
    private OperationType type;
    private BigDecimal amount;
}
