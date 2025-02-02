package com.example.wallets.exceptions;

import com.example.wallets.exceptions.response.ErrorResponse;
import com.example.wallets.utils.BaseLoggerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler extends BaseLoggerService {

    @ExceptionHandler(WalletNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleWalletNotFound(WalletNotFoundException ex) {
        logger.error("Wallet not found: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Wallet not found", ex.getMessage());
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientFunds(InsufficientFundsException ex) {
        logger.error("Not enough funds for this transaction: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Not enough funds for this transaction", ex.getMessage());
    }

    @ExceptionHandler(NotValidJsonException.class)
    public ResponseEntity<ErrorResponse> handleNotValidJson(NotValidJsonException ex) {
        logger.error("Invalid JSON: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid JSON", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
        logger.error("Internal Server Error: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage());
    }

    public ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String error, String message) {
        ErrorResponse response = new ErrorResponse(error, message);
        return ResponseEntity.status(status).body(response);
    }
}
