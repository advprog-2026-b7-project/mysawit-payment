package id.ac.ui.cs.advprog.mysawit.payment.controller;

import id.ac.ui.cs.advprog.mysawit.payment.service.PayrollService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import java.util.Map;

@WebMvcTest(PayrollController.class)
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
                        .contentType(MediaType.APPLICATION_JSON) // Wajib karena di Controller pakai @RequestBody
                        .content(json)
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(payrollService, times(1)).rejectPayroll(id, "Data tidak valid");
    }
}