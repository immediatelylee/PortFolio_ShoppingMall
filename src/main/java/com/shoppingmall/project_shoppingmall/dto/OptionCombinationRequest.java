package com.shoppingmall.project_shoppingmall.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;


@Data
public class OptionCombinationRequest {
    // "optionNames": ["색상", "사이즈", "소재"]
//    private List<String> optionNames;
    // 각 옵션 그룹의 선택된 값들의 리스트 (예: [["블랙", "화이트"], ["S", "M", "L"], ["면", "폴리에스터"]])
    private List<List<String>> groups;
}
