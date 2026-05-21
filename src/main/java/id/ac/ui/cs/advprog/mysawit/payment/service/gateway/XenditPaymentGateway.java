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

@Service
@Primary
public class XenditPaymentGateway implements PaymentGateway {

    private static final Logger logger = LoggerFactory.getLogger(XenditPaymentGateway.class);

    private final RestTemplate restTemplate;
    private final String authHeader;
    private final String callbackToken;

    private static final String XENDIT_API_URL = "https://api.xendit.co";

    public XenditPaymentGateway(
            @Value("${xendit.api.key}") String apiKey,
            @Value("${xendit.callback.token}") String callbackToken) {

        this.restTemplate = new RestTemplate();
        this.callbackToken = callbackToken;

        // Xendit auth: Basic base64(apiKey + ":")
        String encoded = Base64.getEncoder()
                .encodeToString((apiKey + ":").getBytes());
        this.authHeader = "Basic " + encoded;
    }

    /**
     * Xendit docs: POST https://api.xendit.co/v2/invoices
     */
    @Override
    public String createInvoice(Double amount, String customerName, String externalId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.AUTHORIZATION, authHeader);
            headers.setContentType(MediaType.APPLICATION_JSON);

            // amount dalam Rupiah (1 SawitDollar = Rp 10.000)
            Map<String, Object> requestBody = Map.of(
                    "external_id", externalId,
                    "amount", amount.intValue(),
                    "payer_email", customerName,      // bisa email user
                    "description", "MySawit Wallet Top-Up - " + customerName,
                    "currency", "IDR",
                    "invoice_duration", 86400           // 24 jam
            );

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    XENDIT_API_URL + "/v2/invoices",
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            if (response.getBody() != null) {
                String invoiceUrl = (String) response.getBody().get("invoice_url");
                String invoiceId  = (String) response.getBody().get("id");
                logger.info("Xendit invoice created: id={}, url={}", invoiceId, invoiceUrl);
                return invoiceUrl;   // ini yang dikirim ke frontend
            }

            logger.warn("Xendit response body null untuk externalId={}", externalId);
            return null;

        } catch (Exception e) {
            logger.error("Gagal membuat Xendit invoice: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public boolean verifyCallback(String xenditCallbackToken, String status) {
        boolean tokenValid  = callbackToken.equals(xenditCallbackToken);
        boolean statusPaid  = "PAID".equalsIgnoreCase(status)
                || "SETTLED".equalsIgnoreCase(status);

        if (!tokenValid) {
            logger.warn("Xendit callback token tidak valid!");
        }

        return tokenValid && statusPaid;
    }
}