package com.kmug.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Adrian Wolny
 */
@Controller
public class KmugController {
    @RequestMapping("kmug")
    public String loadKmugHome() {
        return "kmugHome";
    }
}
