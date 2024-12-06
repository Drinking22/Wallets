package com.example.wallets.controller;

import com.example.wallets.dto.request.WalletRequest;
import com.example.wallets.dto.response.WalletResponse;
import com.example.wallets.service.WalletServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("api/v1")
@RequiredArgsConstructor
@Slf4j
public class WalletController {

    private final WalletServiceImpl service;

    @GetMapping("/wallets/{walletId}")
    public ResponseEntity<WalletResponse> getWalletByUuid(@PathVariable("walletId") UUID walletId) {
        log.info("Request to get wallet with id: {}", walletId);
        WalletResponse response = service.getWalletByUuid(walletId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/wallet")
    public ResponseEntity<WalletResponse> postWalletOperation(@RequestBody WalletRequest request) {
        log.info("Processing json file and balance operation");
        WalletResponse response = service.createOperationByWallet(request);
        return ResponseEntity.ok(response);
    }
}
