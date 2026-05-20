package id.ac.ui.cs.advprog.mysawit.payment.service.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Map;
import java.util.UUID;

@Service
@Primary
public class XenditPaymentGateway implements PaymentGateway {

    private static final Logger logger = LoggerFactory.getLogger(XenditPaymentGateway.class);

    private final RestTemplate restTemplate;
    private final String apiUrl;
    private final String authHeader;

    public XenditPaymentGateway(
            @Value("${xendit.api.key}") String apiKey,
            @Value("${xendit.api.url}") String apiUrl) {

        this.restTemplate = new RestTemplate();
        this.apiUrl = apiUrl;
        String encoded = Base64.getEncoder()
                .encodeToString((apiKey + ":").getBytes());
        this.authHeader = "Basic " + encoded;
    }

    @Override
    public boolean processPayment(Double amount, String destinationAccount) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.AUTHORIZATION, authHeader);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> requestBody = Map.of(
                    "external_id", UUID.randomUUID().toString(),
                    "amount", amount.intValue(),
                    "bank_code", "BCA",
                    "account_holder_name", destinationAccount,
                    "account_number", destinationAccount.replace("ACC-", ""),
                    "description", "MySawit Payroll Disbursement"
            );

            HttpEntity<Map<String, Object>> entity =
                    new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    apiUrl + "/disbursements",
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            String status = response.getBody() != null
                    ? (String) response.getBody().get("status")
                    : null;

            logger.info("Xendit disbursement status: {}", status);
            return "PENDING".equals(status) || "COMPLETED".equals(status);

        } catch (Exception e) {
            logger.error("Xendit payment failed: {}", e.getMessage());
            return false;
        }
    }
}