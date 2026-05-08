package id.ac.ui.cs.advprog.mysawit.payment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.mysawit.payment.config.SecurityConfig;
import id.ac.ui.cs.advprog.mysawit.payment.service.PayrollService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PayrollController.class)
@Import(SecurityConfig.class)   // ← import konfigurasi security agar csrf disable
class PayrollControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PayrollService payrollService;

    @Test
    void testRejectPayrollWithReason() throws Exception {
        UUID id = UUID.randomUUID();
        Map<String, String> payload = Map.of("reason", "Data tidak valid");
        String json = new ObjectMapper().writeValueAsString(payload);

        mockMvc.perform(put("/api/payroll/" + id + "/reject")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .with(csrf()))   // ← tetap tambahkan csrf untuk aman
                .andExpect(status().isOk());

        verify(payrollService, times(1)).rejectPayroll(id, "Data tidak valid");
    }
    @Test
    void testApprovePayroll() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(put("/api/payroll/" + id + "/approve")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(payrollService, times(1)).approvePayroll(id);
    }
    @Test
    void testGetPayrollList() throws Exception {
        mockMvc.perform(get("/api/payroll/list"))
                .andExpect(status().isOk());
        verify(payrollService, times(1)).findAll();
    }
}