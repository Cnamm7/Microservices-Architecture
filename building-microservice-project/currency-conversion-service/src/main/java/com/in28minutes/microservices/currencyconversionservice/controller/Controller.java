package com.in28minutes.microservices.currencyconversionservice.controller;

import com.in28minutes.microservices.currencyconversionservice.bean.CurrencyCoversion;
import com.in28minutes.microservices.currencyconversionservice.bean.CurrencyExchangeProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
public class Controller {

    @Autowired
    private CurrencyExchangeProxy proxy;

    @GetMapping("/currency-conversion/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyCoversion calculateCurrencyConversion(
            @PathVariable String from,
            @PathVariable String to,
            @PathVariable BigDecimal quantity
            ) {
        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("from", from);
        uriVariables.put("to", to);
        ResponseEntity<CurrencyCoversion> responseEntity = new RestTemplate().getForEntity("http://localhost:8000/currency-exchange/from/{from}/to/{to}", CurrencyCoversion.class, uriVariables);

        CurrencyCoversion currencyCoversion = responseEntity.getBody();

        return new CurrencyCoversion(currencyCoversion.getId(),
                from, to, quantity,
                currencyCoversion.getConversionMultiple(),
                quantity.multiply(currencyCoversion.getConversionMultiple()),
                currencyCoversion.getEnvironment() + " " + "rest template");
    }

    @GetMapping("/currency-conversion-feign/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyCoversion calculateCurrencyConversionFeign(
            @PathVariable String from,
            @PathVariable String to,
            @PathVariable BigDecimal quantity
    ) {

        CurrencyCoversion currencyCoversion = proxy.retriveExchnageValue(from, to);

        return new CurrencyCoversion(currencyCoversion.getId(),
                from, to, quantity,
                currencyCoversion.getConversionMultiple(),
                quantity.multiply(currencyCoversion.getConversionMultiple()),
                currencyCoversion.getEnvironment() + " " + "feign");
    }
}
