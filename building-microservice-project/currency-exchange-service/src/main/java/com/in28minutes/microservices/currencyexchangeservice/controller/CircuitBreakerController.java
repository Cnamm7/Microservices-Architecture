package com.in28minutes.microservices.currencyexchangeservice.controller;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class CircuitBreakerController {

    private Logger logger =
            LoggerFactory.getLogger(CircuitBreakerController.class);

    @GetMapping("/sample-api")
    @Retry(name = "sample-api", fallbackMethod = "hardcodedResponse")
    public String sampleApi() {
        logger.info("sample Api call received");
        ResponseEntity<String> forEntity = new RestTemplate().getForEntity("http://localhost:8080/some-dummy-url",
                String.class);
        return forEntity.getBody();
    }

    @GetMapping("/sample-api-circuit-breaker")
    //circuit breaker basically after certain fail change the status and don't call microservice,
    //instead it returns default answer
    @CircuitBreaker(name = "default", fallbackMethod = "hardcodedResponse")
    public String sampleApiCircuitBreaker() {
        logger.info("sample Api call received");
        ResponseEntity<String> forEntity = new RestTemplate().getForEntity("http://localhost:8080/some-dummy-url",
                String.class);
        return forEntity.getBody();
    }

    @GetMapping("/sample-api-rate-limiter")
    //rate limiter is in a certain amount of time how many calls is allowed
    @RateLimiter(name = "default")
    public String sampleApiRateLimiter() {
        logger.info("sample Api call received");
//        ResponseEntity<String> forEntity = new RestTemplate().getForEntity("http://localhost:8080/some-dummy-url",
//                String.class);
        return "sample-api";
    }

    @GetMapping("/sample-api-bulk-head")
    // we use this again for setting specific limits for concurrent calls for instance
    @Bulkhead(name = "sample-api-bulk-head")
    public String sampleApiBulkHead() {
        logger.info("sample Api call received");
//        ResponseEntity<String> forEntity = new RestTemplate().getForEntity("http://localhost:8080/some-dummy-url",
//                String.class);
        return "sample-api";
    }

    public String hardcodedResponse(Exception ex) {
        return "fallback-response";
    }
}
