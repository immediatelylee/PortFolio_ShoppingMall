package com.shoppingmall.project_shoppingmall.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${uploadPath}")
    String uploadPath;



    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                // classpath의 정적 이미지 제공 (초기 이미지)
                .addResourceLocations("classpath:/static/images/");


        // 업로드된 이미지 제공 (aws에서는 /tmp 폴더 사용)
        registry.addResourceHandler("/uploaded_images/**")
                .addResourceLocations(uploadPath); // uploadPath = file:/tmp/shop/


        // 업로드된 모든 이미지 제공 (AWS /tmp/shop/ 디렉토리 사용)
        registry.addResourceHandler("/uploaded_images/itemimg/**")
                .addResourceLocations(uploadPath + "/itemimg/");

        registry.addResourceHandler("/uploaded_images/itemdetailimg/**")
                .addResourceLocations(uploadPath + "/itemdetailimg/");

        registry.addResourceHandler("/uploaded_images/itemthumbnail/**")
                .addResourceLocations(uploadPath + "/itemthumbnail/");
    }

}
