package com.corely.corely_backend.util;

import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class OrderCodeGenerator {

    public String generate() {

        return "ORD-" + UUID.randomUUID()
                        .toString()
                        .substring(0,8)
                        .toUpperCase();
    }

}