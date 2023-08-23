package com.shoppingmall.project_shoppingmall.controller;

import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

@Controller
public class MainController {

    @GetMapping(value="/")
    public String main(){
        return "main";
    }
}
