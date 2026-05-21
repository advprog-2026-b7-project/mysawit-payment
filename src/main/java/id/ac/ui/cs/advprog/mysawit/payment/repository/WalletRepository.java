package id.ac.ui.cs.advprog.mysawit.payment.repository;

import id.ac.ui.cs.advprog.mysawit.payment.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WalletRepository extends JpaRepository<Wallet, UUID> {
    Optional<Wallet> findByUserId(String userId);
}