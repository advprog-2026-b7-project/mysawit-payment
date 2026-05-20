package id.ac.ui.cs.advprog.mysawit.payment.service;

import id.ac.ui.cs.advprog.mysawit.payment.dto.WageSettingsRequest;
import id.ac.ui.cs.advprog.mysawit.payment.dto.WageSettingsResponse;
import id.ac.ui.cs.advprog.mysawit.payment.model.WageSettings;
import id.ac.ui.cs.advprog.mysawit.payment.repository.WageSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.Optional;

@Service
public class WageSettingsServiceImpl implements WageSettingsService {

    @Autowired
    private WageSettingsRepository wageSettingsRepository;

    @Override
    public WageSettingsResponse getWageSettings() {
        WageSettings settings = getWageSettingsEntity();
        return toResponse(settings);
    }

    @Override
    public WageSettingsResponse updateWageSettings(WageSettingsRequest request) {
        WageSettings settings = getWageSettingsEntity();
        settings.setBuruhWagePerKg(request.getBuruhWagePerKg());
        settings.setSupirTruckWagePerKg(request.getSupirTruckWagePerKg());
        settings.setMandorWagePerKg(request.getMandorWagePerKg());
        WageSettings updated = wageSettingsRepository.save(settings);
        return toResponse(updated);
    }

    @Override
    public WageSettings getWageSettingsEntity() {
        Optional<WageSettings> existing = wageSettingsRepository.findFirst();
        if (existing.isPresent()) {
            return existing.get();
        }
        // Create default settings if none exist
        WageSettings defaults = new WageSettings(
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(1000),
                BigDecimal.valueOf(1000)
        );
        return wageSettingsRepository.save(defaults);
    }

    private WageSettingsResponse toResponse(WageSettings settings) {
        return new WageSettingsResponse(
                settings.getId(),
                settings.getBuruhWagePerKg(),
                settings.getSupirTruckWagePerKg(),
                settings.getMandorWagePerKg()
        );
    }
}
