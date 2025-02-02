package com.example.wallets.repository;

import com.example.wallets.model.Wallet;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface WalletRepository extends R2dbcRepository<Wallet, Long> {
    Mono<Wallet> findByWalletId(UUID walletId);
}
