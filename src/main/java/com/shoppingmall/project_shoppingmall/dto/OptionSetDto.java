package com.shoppingmall.project_shoppingmall.dto;

import com.shoppingmall.project_shoppingmall.domain.OptionSet;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
public class OptionSetDto {
    private Long id;
    private String name;
    private List<OptionDto> options;

    public static OptionSetDto fromEntity(OptionSet entity){
        OptionSetDto dto = new OptionSetDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        List<OptionDto> optDtos = entity.getOptions().stream()
                .map(OptionDto::fromEntity)
                .collect(Collectors.toList());
        dto.setOptions(optDtos);
        return dto;
    }
}
