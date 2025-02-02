package com.example.wallets.controller;

import com.example.wallets.dto.request.WalletRequest;
import com.example.wallets.dto.response.WalletResponse;
import com.example.wallets.exceptions.InsufficientFundsException;
import com.example.wallets.exceptions.NotValidJsonException;
import com.example.wallets.exceptions.WalletNotFoundException;
import com.example.wallets.service.WalletServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(WalletController.class)
public class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WalletServiceImpl service;

    private UUID walletId;
    private BigDecimal amount;
    private WalletResponse getResponse;

    @BeforeEach
    public void setUp() {
        walletId = UUID.randomUUID();
        amount = BigDecimal.valueOf(1000.00);
        getResponse = new WalletResponse(walletId, amount);
    }

    @Test
    @DisplayName("Checking if a response with wallet information has been received")
    void whenGetWalletByUuid_thanReturnWalletResponse() throws Exception {
        Mockito.when(service.getWalletByUuid(walletId)).thenReturn(Mono.just(getResponse));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/wallets/{walletId}", walletId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.walletId").value(walletId.toString()))
                .andExpect(jsonPath("$.amount").value(1000.00));
    }

    @Test
    @DisplayName("Throw exception when passing an unknown UUID")
    void whenGetWalletByUuid_thanThrowException() throws Exception {
        Mockito.when(service.getWalletByUuid(walletId))
                .thenThrow(new WalletNotFoundException("Wallet not found with id: " + walletId));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/wallets/{walletId}", walletId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Wallet not found"))
                .andExpect(jsonPath("$.message").value("Wallet not found with id: " + walletId));
    }

    @Test
    @DisplayName("Throw exception when invalid type in JSON request")
    void whenPostWalletOperationWithFailedTypeInJson_thanThrowException() throws Exception {
        String jsonNotValidType = "{\"walletId\":\"" + walletId + "\"," +
                "\"amount\":1000.00," +
                "\"type\":null}";

        Mockito.when(service.createOperationByWallet(Mockito.any(WalletRequest.class)))
                .thenThrow(new NotValidJsonException("Invalid JSON: type must be either DEPOSIT or WITHDRAW"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonNotValidType))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid JSON"))
                .andExpect(jsonPath("$.message").value("Invalid JSON: type must be either DEPOSIT or WITHDRAW"));
    }

    @Test
    @DisplayName("Throw exception when invalid amount in JSON request")
    void whenPostWalletOperationWithFailedAmountInJson_thanThrowException() throws Exception {
        String jsonNotValidAmount = "{\"walletId\":\"" + walletId + "\"," +
                "\"amount\":0," +
                "\"type\":\"DEPOSIT\"}";

        Mockito.when(service.createOperationByWallet(Mockito.any(WalletRequest.class)))
                .thenThrow(new NotValidJsonException("Invalid JSON: amount must be greater than zero"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonNotValidAmount))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid JSON"))
                .andExpect(jsonPath("$.message").value("Invalid JSON: amount must be greater than zero"));
    }

    @Test
    @DisplayName("Throw exception when invalid wallet ID in JSON request")
    void whenPostWalletOperationWithFailedWalletIdInJson_thanThrowException() throws Exception {
        String jsonNotValidWalledId = "{\"walletId\":null," +
                "\"amount\":1000.00," +
                "\"type\":\"DEPOSIT\"}";

        Mockito.when(service.createOperationByWallet(Mockito.any(WalletRequest.class)))
                .thenThrow(new NotValidJsonException("Invalid JSON: walletId cannot be null"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonNotValidWalledId))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid JSON"))
                .andExpect(jsonPath("$.message").value("Invalid JSON: walletId cannot be null"));
    }

    @Test
    @DisplayName("Checking not enough founds for transaction")
    void whenPostWalletOperationNotEnoughFounds_thanReturnException() throws Exception {
        String jsonRequest = "{\"walletId\":\"" + walletId + "\"," +
                "\"amount\":1000.00," +
                "\"type\":\"WITHDRAW\"}";

        Mockito.when(service.createOperationByWallet(Mockito.any(WalletRequest.class)))
                .thenThrow(new InsufficientFundsException("Not enough funds for this transaction"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Not enough funds for this transaction"))
                .andExpect(jsonPath("$.message").value("Not enough funds for this transaction"));
    }
}