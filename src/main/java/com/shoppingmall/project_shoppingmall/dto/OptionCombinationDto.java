package com.shoppingmall.project_shoppingmall.dto;

import com.shoppingmall.project_shoppingmall.domain.OptionCombination;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OptionCombinationDto {
//    ajax통해서 다시 클라이언트에 전달하기 위함
    private String combination;  // 예: "블랙/S/면"

    public static OptionCombinationDto fromEntity(OptionCombination entity) {
        return new OptionCombinationDto(entity.getCombination());
    }
}
