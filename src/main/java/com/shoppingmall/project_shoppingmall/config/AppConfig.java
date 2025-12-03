package com.shoppingmall.project_shoppingmall.config;

import com.siot.IamportRestClient.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;

@Configuration
public class AppConfig {
//    @Value("${apiKey}")
    String apiKey;

//    @Value("${secretKey}")
    String secretKey;

//    @Bean
//    public IamportClient iamportClient() {
//        return new IamportClient(apiKey, secretKey);
//    }

}
