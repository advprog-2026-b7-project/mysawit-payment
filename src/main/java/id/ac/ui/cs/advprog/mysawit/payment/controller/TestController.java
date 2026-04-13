package id.ac.ui.cs.advprog.mysawit.payment.controller;

import id.ac.ui.cs.advprog.mysawit.payment.model.PayrollEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @GetMapping("/test/pay")
    public String triggerPayment(@RequestParam String workerId, @RequestParam Double amount) {
        PayrollEvent event = new PayrollEvent(workerId, amount, "REF-" + System.currentTimeMillis());

        eventPublisher.publishEvent(event);

        return "Event sent to the worker: " + workerId + ". Check the console and the database!";
    }
}