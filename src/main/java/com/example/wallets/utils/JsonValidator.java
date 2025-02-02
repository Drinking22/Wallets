package com.example.wallets.utils;

import com.example.wallets.dto.request.WalletRequest;
import com.example.wallets.exceptions.NotValidJsonException;

import java.math.BigDecimal;

import static com.example.wallets.dto.request.OperationType.DEPOSIT;
import static com.example.wallets.dto.request.OperationType.WITHDRAW;

public class JsonValidator {

    public static void checkJsonRequestNotNull(WalletRequest request) {
        if (request.getWalletId() == null) {
            throw new NotValidJsonException("Invalid JSON: walletId cannot be null");
        }
    }

    public static void checkJsonRequestAmount(WalletRequest request) {
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new NotValidJsonException("Invalid JSON: amount must be greater than zero");
        }
    }

    public static void checkJsonRequestType(WalletRequest request) {
        if (request.getType() == null || (!request.getType().equals(DEPOSIT)
                && !request.getType().equals(WITHDRAW))) {
            throw new NotValidJsonException("Invalid JSON: type must be either DEPOSIT or WITHDRAW");
        }
    }
}
