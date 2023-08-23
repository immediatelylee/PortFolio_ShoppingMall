package com.shoppingmall.project_shoppingmall.controller;

import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

@Controller
public class ItemController {

    @GetMapping(value = "/admin/item/new")
    public String itemForm(){
        return "/item/itemForm";
    }

}
