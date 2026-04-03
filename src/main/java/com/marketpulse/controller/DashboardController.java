// src/main/java/com/marketpulse/controller/DashboardController.java

package com.marketpulse.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DashboardController {

    @GetMapping("/")
    @ResponseBody
    public String home() {
        return "MarketPulse is running";
    }
}