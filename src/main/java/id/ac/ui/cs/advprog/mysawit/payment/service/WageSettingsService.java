package id.ac.ui.cs.advprog.mysawit.payment.service;

import id.ac.ui.cs.advprog.mysawit.payment.dto.WageSettingsRequest;
import id.ac.ui.cs.advprog.mysawit.payment.dto.WageSettingsResponse;
import id.ac.ui.cs.advprog.mysawit.payment.model.WageSettings;

public interface WageSettingsService {
    /**
     * Get the current wage settings.
     * If no settings exist, create default ones.
     * 
     * @return current wage settings
     */
    WageSettingsResponse getWageSettings();

    /**
     * Update wage settings with new values.
     * 
     * @param request wage settings update request
     * @return updated wage settings
     */
    WageSettingsResponse updateWageSettings(WageSettingsRequest request);

    /**
     * Get wage settings entity (for internal use).
     * 
     * @return WageSettings entity
     */
    WageSettings getWageSettingsEntity();
}
