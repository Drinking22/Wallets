package com.example.wallets.service;

import com.example.wallets.dto.request.WalletRequest;
import com.example.wallets.dto.response.WalletResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface WalletService {
    Mono<WalletResponse> getWalletByUuid(UUID walletId);
    Mono<WalletResponse> createOperationByWallet (WalletRequest request);
}
