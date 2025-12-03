package com.shoppingmall.project_shoppingmall.service;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class IamportClientService {
    // 결제 검증을 위한 자체 코드
    // 공식 코드가 아님.

    @Value("${apiKey}")
    private String apiKey;

    @Value("${secretKey}")
    private String apiSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    // 1) 토큰 발급
    public String getAccessToken() {
        String url = "https://api.iamport.kr/users/getToken";

        Map<String, String> body = new HashMap<>();
        body.put("imp_key", apiKey);
        body.put("imp_secret", apiSecret);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException("iamport 토큰 발급 실패");
        }

        Map responseBody = response.getBody();
        Map responseData = (Map) responseBody.get("response");

        return (String) responseData.get("access_token");
    }

    // 2) imp_uid로 결제 정보 조회
    public Map<String, Object> getPaymentData(String impUid, String accessToken) {
        String url = "https://api.iamport.kr/payments/" + impUid;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException("iamport 결제 조회 실패");
        }

        Map body = response.getBody();
        Map<String, Object> data = (Map<String, Object>) body.get("response");
        return data;
    }
}
