package com.ayush.googlecalendarnotifications.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.UUID;

@Controller
public class KeyGenerator {

    @GetMapping("/key")
    public String keyPage(Model model) {
        model.addAttribute("key", UUID.randomUUID().toString());
        return "key";
    }
}
