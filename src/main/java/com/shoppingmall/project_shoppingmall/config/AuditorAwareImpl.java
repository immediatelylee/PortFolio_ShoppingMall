package com.shoppingmall.project_shoppingmall.config;

import org.springframework.data.domain.*;
import org.springframework.security.core.*;
import org.springframework.security.core.context.*;

import java.util.*;

public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = "";
        if(authentication != null){
            userId = authentication.getName();
        }
        return Optional.of(userId);
    }

}
