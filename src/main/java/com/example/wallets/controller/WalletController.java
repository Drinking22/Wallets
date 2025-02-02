package com.example.wallets.controller;

import com.example.wallets.dto.request.WalletRequest;
import com.example.wallets.dto.response.WalletResponse;
import com.example.wallets.service.WalletServiceImpl;
import com.example.wallets.utils.BaseLoggerService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class WalletController extends BaseLoggerService {

    private final WalletServiceImpl service;

    public WalletController(WalletServiceImpl service) {
        this.service = service;
    }

    @GetMapping("/wallets/{walletId}")
    public Mono<WalletResponse> getWalletByUuid(@PathVariable("walletId") UUID walletId) {
        logger.info("Request to get wallet with id: {}", walletId);
        return service.getWalletByUuid(walletId);
    }

    @PostMapping("/wallet")
    public Mono<WalletResponse> postWalletOperation(@RequestBody WalletRequest request) {
        logger.info("Processing json file and balance operation");
        return service.createOperationByWallet(request);
    }
}
