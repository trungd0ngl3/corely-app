package com.corely.corely_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.corely.corely_backend.service.MailService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test")
public class MailController {

    private final MailService mailService;

    @GetMapping("/mail")
    public String test() {
        mailService.send(
                "your_email@gmail.com",
                "Test Email",
                "Hello from Corely!");

        return "Sent";
    }
}
