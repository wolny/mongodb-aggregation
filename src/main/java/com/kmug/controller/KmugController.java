package com.kmug.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Adrian Wolny
 */
@Controller
@RequestMapping("/")
public class KmugController {
    @RequestMapping
    public String loadHomePage() {
        return "index";
    }
}
