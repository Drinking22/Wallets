package com.example.wallets.service;

import com.example.wallets.dto.request.WalletRequest;
import com.example.wallets.dto.response.WalletResponse;
import com.example.wallets.exceptions.InsufficientFundsException;
import com.example.wallets.exceptions.NotValidJsonException;
import com.example.wallets.exceptions.WalletNotFoundException;
import com.example.wallets.model.Wallet;
import com.example.wallets.repository.WalletRepository;
import com.example.wallets.utils.BaseLoggerService;
import com.example.wallets.utils.JsonValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

import static com.example.wallets.dto.request.OperationType.DEPOSIT;
import static com.example.wallets.dto.request.OperationType.WITHDRAW;

@Service
public class WalletServiceImpl extends BaseLoggerService implements WalletService {

    private final WalletRepository repository;

    public WalletServiceImpl(WalletRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<WalletResponse> getWalletByUuid(UUID walletId) {
        logger.info("Get wallet by uuid id, or exception response");
        return repository.findByWalletId(walletId)
                .switchIfEmpty(Mono.error(new WalletNotFoundException("Wallet not found with id: " + walletId)))
                .map(wallet -> new WalletResponse(wallet.getWalletId(), wallet.getBalance()));
    }

    @Override
    public Mono<WalletResponse> createOperationByWallet(WalletRequest request) {
        logger.info("Processing of deposits and withdrawals of cash");
        return repository.findByWalletId(request.getWalletId())
                .switchIfEmpty(Mono.error(new WalletNotFoundException("Wallet not found with id: " + request.getWalletId())))
                .flatMap(wallet -> {
                    checkNotValidJson(request);
                    return switch (request.getType()) {
                        case DEPOSIT -> handleDeposit(wallet, request);
                        case WITHDRAW -> handleWithdraw(wallet, request);
                    };
                });
    }

    @Transactional
    public Mono<WalletResponse> handleDeposit(Wallet wallet, WalletRequest request) {
        logger.info("Depositing amount: {}", request.getAmount());
        wallet.setBalance(wallet.getBalance().add(request.getAmount()));

        return repository.save(wallet).
                map(savedWallet -> new WalletResponse(savedWallet.getWalletId(), savedWallet.getBalance()));
    }

    @Transactional
    public Mono<WalletResponse> handleWithdraw(Wallet wallet, WalletRequest request) {
        logger.info("Withdraw amount: {}", request.getAmount());
        checkSufficientFunds(wallet, request);
        wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));

        return repository.save(wallet)
                .map(savedWallet -> new WalletResponse(savedWallet.getWalletId(), savedWallet.getBalance()));
    }

    public void checkNotValidJson(WalletRequest request) {
        logger.info("Checking not valid JSON");

        JsonValidator.checkJsonRequestNotNull(request);
        JsonValidator.checkJsonRequestAmount(request);
        JsonValidator.checkJsonRequestType(request);
    }

    public void checkSufficientFunds(Wallet wallet, WalletRequest request) {
        logger.info("Checking remaining balance");
        if (wallet.getBalance().subtract(request.getAmount()).compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientFundsException("Not enough funds for this transaction");
        }
    }
}
