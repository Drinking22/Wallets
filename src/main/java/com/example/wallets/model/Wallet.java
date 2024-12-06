package com.example.wallets.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Entity
@Table(name = "wallet")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "wallet_id", nullable = false, unique = true)
    private UUID walletId;

    @Column(nullable = false)
    private BigDecimal balance;
}
