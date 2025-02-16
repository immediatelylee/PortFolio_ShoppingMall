package com.shoppingmall.project_shoppingmall.controller;

import com.shoppingmall.project_shoppingmall.domain.OptionSet;
import com.shoppingmall.project_shoppingmall.dto.OptionSetDto;
import com.shoppingmall.project_shoppingmall.service.OptionSetService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OptionSetRestController {
    private final OptionSetService optionSetService;

    @GetMapping("/optionSet/{id}")
    public OptionSetDto getOptionSetData(@PathVariable Long id){
        OptionSet set = optionSetService.findByIdFetch(id);
        if(set == null) return null;
        // 여기서 LazyInitializationException 없이 set.getOptions()와
        // 각 option.getOptionValues() 접근 가능
        return OptionSetDto.fromEntity(set);
    }
}
