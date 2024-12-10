package com.example.wallets.service;

import com.example.wallets.dto.request.WalletRequest;
import com.example.wallets.dto.response.WalletResponse;
import com.example.wallets.exceptions.InsufficientFundsException;
import com.example.wallets.exceptions.NotValidJsonException;
import com.example.wallets.exceptions.WalletNotFoundException;
import com.example.wallets.dto.request.OperationType;
import com.example.wallets.model.Wallet;
import com.example.wallets.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/*
The author's fantasy on the topic of multithreading
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletServiceImpl implements WalletService {
    private static final int MAX_REQUEST_IN_SECOND = 1000;
    private static final long RESET_TIME_INTERVAL = 1000;

    private final WalletRepository repository;

    private final Semaphore semaphore = new Semaphore(MAX_REQUEST_IN_SECOND);
    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(50,
            100,
            60,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>());

    private long lastResetTime = System.currentTimeMillis();
    private AtomicInteger requestCount = new AtomicInteger(0);

    @Override
    public CompletableFuture<WalletResponse> getWalletByUuid(UUID walletId) {
        return CompletableFuture.supplyAsync(() -> {
            manageRateLimiting();
            log.info("Get wallet by uuid id, or exception response");
            Wallet wallet = getWallet(walletId);
            return new WalletResponse(wallet.getWalletId(), wallet.getBalance());
        }, executor);
    }

    @Override
    public CompletableFuture<WalletResponse> createOperationByWallet(WalletRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            manageRateLimiting();
            log.info("Processing of deposits and withdrawals of cash");
            Wallet wallet = getWallet(request.getWalletId());
            checkNotValidJson(request);

            return switch (request.getType()) {
                case DEPOSIT -> handleDeposit(wallet, request);
                case WITHDRAW -> handleWithdraw(wallet, request);
            };
        }, executor);
    }

    private void manageRateLimiting() {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastResetTime > RESET_TIME_INTERVAL) {
            requestCount.set(0);
            lastResetTime = currentTime;
        }

        if (requestCount.incrementAndGet() > MAX_REQUEST_IN_SECOND) {
            requestCount.decrementAndGet();
            throw new RuntimeException("Too many requests, please try again later");
        }

        try {
            semaphore.acquire();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Request interrupted", ex);
        }
    }

    public WalletResponse handleDeposit(Wallet wallet, WalletRequest request) {
        log.info("Depositing amount: {}", request.getAmount());
        wallet.setBalance(wallet.getBalance().add(request.getAmount()));
        repository.save(wallet);
        return new WalletResponse(wallet.getWalletId(), wallet.getBalance());
    }

    public WalletResponse handleWithdraw(Wallet wallet, WalletRequest request) {
        log.info("Withdraw amount: {}", request.getAmount());
        checkSufficientFunds(wallet, request);
        wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
        repository.save(wallet);
        return new WalletResponse(wallet.getWalletId(), wallet.getBalance());
    }

    public void checkNotValidJson(WalletRequest request) {
        log.info("Checking not valid JSON");

        if (request.getWalletId() == null) {
            throw new NotValidJsonException("Invalid JSON: walletId cannot be null");
        }

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new NotValidJsonException("Invalid JSON: amount must be greater than zero");
        }

        if (request.getType() == null || (!request.getType().equals(OperationType.DEPOSIT)
                && !request.getType().equals(OperationType.WITHDRAW))) {
            throw new NotValidJsonException("Invalid JSON: type must be either DEPOSIT or WITHDRAW");
        }
    }

    public void checkSufficientFunds(Wallet wallet, WalletRequest request) {
        log.info("Checking remaining balance");
        if (wallet.getBalance().subtract(request.getAmount()).compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientFundsException("Not enough funds for this transaction");
        }
    }

    public Wallet getWallet(UUID walletId) {
        log.info("Checking wallet in data base");
        return repository.findByWalletId(walletId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found with id: " + walletId));
    }
}
