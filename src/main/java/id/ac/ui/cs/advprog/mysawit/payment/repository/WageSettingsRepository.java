package id.ac.ui.cs.advprog.mysawit.payment.repository;

import id.ac.ui.cs.advprog.mysawit.payment.model.WageSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WageSettingsRepository extends JpaRepository<WageSettings, Long> {
    /**
     * Find the first wage settings record (assuming only one exists in the system).
     */
    @Query(value = "SELECT * FROM wage_settings LIMIT 1", nativeQuery = true)
    Optional<WageSettings> findFirst();
}
