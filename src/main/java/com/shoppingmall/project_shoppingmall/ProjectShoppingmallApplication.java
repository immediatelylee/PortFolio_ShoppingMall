package com.shoppingmall.project_shoppingmall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProjectShoppingmallApplication {

    public static void main(String[] args) {
//        System.out.println(">>> main 들어옴");
//
//        try {
//            SpringApplication app = new SpringApplication(ProjectShoppingmallApplication.class);
//            // 혹시라도 웹 모드가 NONE으로 잡히는 걸 대비
//            app.setWebApplicationType(WebApplicationType.SERVLET);
//            app.run(args);
//        } catch (Throwable e) {
//            // 여기로 오면 진짜 원인이 찍힘
//            e.printStackTrace();
//        }


        SpringApplication.run(ProjectShoppingmallApplication.class, args);
    }

}
