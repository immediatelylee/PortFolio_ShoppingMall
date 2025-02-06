package com.shoppingmall.project_shoppingmall.dto;

import com.shoppingmall.project_shoppingmall.domain.Option;
import com.shoppingmall.project_shoppingmall.domain.OptionValue;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
public class OptionDto {
    private Long id;
    private String name; // ex: "색상"
    private List<String> values;

    public static OptionDto fromEntity(Option entity){
        OptionDto dto = new OptionDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        List<String> valList = entity.getOptionValues().stream()
                .map(OptionValue::getValue)
                .collect(Collectors.toList());
        dto.setValues(valList);
        return dto;
    }
}
