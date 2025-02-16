package com.shoppingmall.project_shoppingmall.service;

import com.shoppingmall.project_shoppingmall.domain.*;
import com.shoppingmall.project_shoppingmall.repository.UsedOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsedOptionService {
    private final UsedOptionRepository usedOptionRepository;

//    public List<UsedOption> generateUsedOptions(Item item) {
//        List<UsedOption> usedOptions = new ArrayList<>();
//        OptionSet optionSet = item.getOptionSet();
//
// TODO:레거시 코드 추후에 확인후 사용안하면 삭제하기

//        // optionSet -> 여러 Option(옵션명) 목록
//        for (Option option : optionSet.getOptions()) {
//            // 분리 구조에서는 'optionValues'라는 엔티티 리스트가 있을 것
//            for (OptionValue ov : option.getOptionValues()) {
//                UsedOption usedOption = new UsedOption();
//                usedOption.setOptionName(option.getName());    // 예: "색상"
//                usedOption.setOptionValue(ov.getValue());      // 예: "블랙", "화이트"
//                usedOption.setItem(item);
//
//                usedOptions.add(usedOption);
//            }
//        }
//
//        return usedOptionRepository.saveAll(usedOptions);
//    }

    // 특정 Item에 연관된 UsedOption 조회
    public List<UsedOption> getUsedOptionsByItem(Item item) {
        return usedOptionRepository.findByItem(item);
    }
}
