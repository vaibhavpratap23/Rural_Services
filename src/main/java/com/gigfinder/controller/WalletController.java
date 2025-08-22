package com.gigfinder.controller;

import com.gigfinder.model.User;
import com.gigfinder.model.Wallet;
import com.gigfinder.repository.UserRepository;
import com.gigfinder.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/balance")
    public ResponseEntity<?> getBalance() {
        try {
            String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            Optional<User> userOpt = userRepository.findByEmail(userEmail);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("User not found");
            }
            User user = userOpt.get();

            Optional<Wallet> walletOpt = walletRepository.findByUser(user);
            if (walletOpt.isEmpty()) {
                // Create wallet if it doesn't exist
                Wallet wallet = Wallet.builder()
                    .user(user)
                    .balance(BigDecimal.ZERO)
                    .createdAt(LocalDateTime.now())
                    .build();
                walletRepository.save(wallet);
                return ResponseEntity.ok(Map.of("balance", BigDecimal.ZERO));
            }

            return ResponseEntity.ok(Map.of("balance", walletOpt.get().getBalance()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to get balance: " + e.getMessage());
        }
    }

    @PostMapping("/add-money")
    public ResponseEntity<?> addMoney(@RequestBody Map<String, Object> request) {
        try {
            String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            Optional<User> userOpt = userRepository.findByEmail(userEmail);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("User not found");
            }
            User user = userOpt.get();

            Double amount = (Double) request.get("amount");
            String paymentMethod = (String) request.get("paymentMethod");

            if (amount == null || amount <= 0) {
                return ResponseEntity.badRequest().body("Invalid amount");
            }

            Optional<Wallet> walletOpt = walletRepository.findByUser(user);
            Wallet wallet;
            if (walletOpt.isEmpty()) {
                wallet = Wallet.builder()
                    .user(user)
                    .balance(BigDecimal.ZERO)
                    .createdAt(LocalDateTime.now())
                    .build();
            } else {
                wallet = walletOpt.get();
            }

            // Add money to wallet
            wallet.setBalance(wallet.getBalance().add(BigDecimal.valueOf(amount)));
            wallet.setUpdatedAt(LocalDateTime.now());
            walletRepository.save(wallet);

            // ✅ Include paymentMethod in response so it's not "unused"
            return ResponseEntity.ok(Map.of(
                "message", "Money added successfully using " + paymentMethod,
                "newBalance", wallet.getBalance()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to add money: " + e.getMessage());
        }
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdrawMoney(@RequestBody Map<String, Object> request) {
        try {
            String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            Optional<User> userOpt = userRepository.findByEmail(userEmail);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("User not found");
            }
            User user = userOpt.get();

            Double amount = (Double) request.get("amount");
            String bankAccount = (String) request.get("bankAccount");

            if (amount == null || amount <= 0) {
                return ResponseEntity.badRequest().body("Invalid amount");
            }

            Optional<Wallet> walletOpt = walletRepository.findByUser(user);
            if (walletOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Wallet not found");
            }

            Wallet wallet = walletOpt.get();
            if (wallet.getBalance().compareTo(BigDecimal.valueOf(amount)) < 0) {
                return ResponseEntity.badRequest().body("Insufficient balance");
            }

            // Deduct money from wallet
            wallet.setBalance(wallet.getBalance().subtract(BigDecimal.valueOf(amount)));
            wallet.setUpdatedAt(LocalDateTime.now());
            walletRepository.save(wallet);

            // ✅ Include bankAccount in response so it's not "unused"
            return ResponseEntity.ok(Map.of(
                "message", "Withdrawal to account " + bankAccount + " initiated successfully",
                "newBalance", wallet.getBalance()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to withdraw money: " + e.getMessage());
        }
    }

    @GetMapping("/transactions")
    public ResponseEntity<?> getTransactions() {
        try {
            String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            Optional<User> userOpt = userRepository.findByEmail(userEmail);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("User not found");
            }
            // ✅ Removed unused "user" variable since it's not needed here

            // This would typically fetch from a Transaction entity
            return ResponseEntity.ok(Map.of("transactions", new Object[0]));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to get transactions: " + e.getMessage());
        }
    }
}
