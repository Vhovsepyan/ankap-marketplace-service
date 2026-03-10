package com.ankap.platform.orderservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class TestController {

    @GetMapping("/test")
    public String testRouting() {
        return "🚀 SUCCESS: Order Service is up, running on Port 8083, and routed through the Gateway!";
    }
}