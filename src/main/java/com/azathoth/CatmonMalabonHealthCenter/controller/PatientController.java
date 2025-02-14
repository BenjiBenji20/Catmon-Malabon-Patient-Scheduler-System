package com.azathoth.CatmonMalabonHealthCenter.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RestController
@RequestMapping("/api/patient")
public class PatientController {

    @GetMapping("/greet")
    public String greet() {
        return "Hello World!";
    }
}
