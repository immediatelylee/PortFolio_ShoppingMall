package com.shoppingmall.project_shoppingmall.service;

import com.shoppingmall.project_shoppingmall.domain.Item;
import com.shoppingmall.project_shoppingmall.domain.OptionCombination;
import com.shoppingmall.project_shoppingmall.domain.UsedOption;
import com.shoppingmall.project_shoppingmall.repository.OptionCombinationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OptionCombinationService {

    private final OptionCombinationRepository combinationRepository;

    public List<OptionCombination> generateCombinations(Item item, List<UsedOption> usedOptions) {
        // 옵션 조합 생성 로직
        List<List<String>> valuesList = new ArrayList<>();

        // UsedOption을 기반으로 값 목록을 생성
        Map<String, List<String>> optionsMap = usedOptions.stream()
                .collect(Collectors.groupingBy(
                        UsedOption::getOptionName,
                        Collectors.mapping(UsedOption::getOptionValue, Collectors.toList())
                ));

        valuesList.addAll(optionsMap.values());

        // 모든 조합 생성
        List<String> combinations = cartesianItem(valuesList);
        List<OptionCombination> combinationEntities = new ArrayList<>();

        for (String combination : combinations) {
            OptionCombination entity = new OptionCombination();
            entity.setCombination(combination);
            entity.setStock(0); // 기본 재고는 0
            entity.setItem(item);
            combinationEntities.add(entity);
        }

        return combinationRepository.saveAll(combinationEntities);
    }

    // Cartesian Item (데이터 조합 생성 로직)
    private List<String> cartesianItem(List<List<String>> lists) {
        List<String> result = new ArrayList<>();
        cartesianItemHelper(lists, 0, "", result);
        return result;
    }

    private void cartesianItemHelper(List<List<String>> lists, int depth, String current, List<String> result) {
        if (depth == lists.size()) {
            result.add(current.substring(1)); // 첫 "-" 제거
            return;
        }
        for (String value : lists.get(depth)) {
            cartesianItemHelper(lists, depth + 1, current + "-" + value, result);
        }
    }

    public List<OptionCombination> getCombinationsByItem(Item item) {
        return combinationRepository.findByItem(item);
    }
}
