package com.shoppingmall.project_shoppingmall.service;

import com.shoppingmall.project_shoppingmall.domain.Item;
import com.shoppingmall.project_shoppingmall.domain.OptionCombination;
import com.shoppingmall.project_shoppingmall.domain.UsedOption;
import com.shoppingmall.project_shoppingmall.repository.OptionCombinationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OptionCombinationService {

    private final OptionCombinationRepository combinationRepository;

    /**
     * 선택된 옵션 그룹들을 받아 데카르트 곱(모든 조합)을 생성하고,
     * OptionCombination 엔티티로 저장한 후 반환합니다.
     *
     * @param item        해당 상품(Item) 엔티티
     * @param usedOptions 각 옵션 그룹의 선택된 값들을 포함하는 UsedOption 리스트
     * @return 생성된 OptionCombination 엔티티 리스트
     */

    @Transactional
    public List<OptionCombination> generateCombinations(Item item, List<UsedOption> usedOptions) {
        // 1) 각 옵션 그룹별 선택된 값 목록을 Map으로 그룹핑 (키: 옵션명, 값: 해당 옵션값 목록)
        Map<String, List<String>> optionsMap = usedOptions.stream()
                .collect(Collectors.groupingBy(
                        UsedOption::getOptionName,
                        Collectors.mapping(UsedOption::getOptionValue, Collectors.toList())
                ));

        // 2) Map의 값들(List<String>)만 추출 → List<List<String>>
        List<List<String>> valuesList = new ArrayList<>(optionsMap.values());

        // 3) 데카르트 곱(모든 조합) 생성
        List<String> combinations = cartesianItem(valuesList);

        // 4) 생성된 조합 문자열을 OptionCombination 엔티티로 변환
        List<OptionCombination> combinationEntities = new ArrayList<>();
        for (String combination : combinations) {
            OptionCombination entity = new OptionCombination();
            entity.setCombination(combination);
            entity.setStock(0);  // 기본 재고 0 설정 (예시)
            entity.setItem(item);
            combinationEntities.add(entity);
        }

        // 5) DB에 저장 후 반환
        return combinationRepository.saveAll(combinationEntities);
    }

    // 데카르트 곱을 생성하는 재귀 함수 (각 리스트의 값을 "/"로 연결)
    private List<String> cartesianItem(List<List<String>> lists) {
        List<String> result = new ArrayList<>();
        cartesianItemHelper(lists, 0, "", result);
        return result;
    }

    private void cartesianItemHelper(List<List<String>> lists, int depth, String current, List<String> result) {
        if (depth == lists.size()) {
            // 첫번째 "/" 제거 후 결과 추가
            result.add(current.substring(1));
            return;
        }
        for (String value : lists.get(depth)) {
            cartesianItemHelper(lists, depth + 1, current + "/" + value, result);
        }
    }

    // 이미 생성된 조합을 조회하는 메서드 (필요 시)
    @Transactional(readOnly = true)
    public List<OptionCombination> getCombinationsByItem(Item item) {
        return combinationRepository.findByItem(item);
    }

}
