package com.lingsb.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoTest {
    @GetMapping
    public String test() {
        return "test";
    }
}
