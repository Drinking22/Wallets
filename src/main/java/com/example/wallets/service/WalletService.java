package com.example.wallets.service;

import com.example.wallets.dto.request.WalletRequest;
import com.example.wallets.dto.response.WalletResponse;

import java.util.UUID;

public interface WalletService {
    WalletResponse getWalletByUuid(UUID walletId);
    WalletResponse createOperationByWallet (WalletRequest request);
}
