package com.shoppingmall.project_shoppingmall.controller;

import com.shoppingmall.project_shoppingmall.dto.*;
import com.shoppingmall.project_shoppingmall.service.*;
import lombok.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;
import org.springframework.ui.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RequiredArgsConstructor
@Controller
public class MainController {

    private final ItemService itemService;

    @GetMapping(value = "/")
    public String main(){

        return "main";
    }
    @GetMapping(value ="/notification")
    public String notification(){
        return "mockup";
    }

    @GetMapping(value = "/mobile")
    public String mobilemain(){return "mobile/mobile_main";}


}
