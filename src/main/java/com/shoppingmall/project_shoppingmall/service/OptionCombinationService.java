package com.shoppingmall.project_shoppingmall.service;

import com.shoppingmall.project_shoppingmall.domain.Item;
import com.shoppingmall.project_shoppingmall.domain.OptionCombination;
import com.shoppingmall.project_shoppingmall.domain.UsedOption;
import com.shoppingmall.project_shoppingmall.dto.OptionCombinationRequest;
import com.shoppingmall.project_shoppingmall.repository.ItemRepository;
import com.shoppingmall.project_shoppingmall.repository.OptionCombinationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OptionCombinationService {

    private final OptionCombinationRepository combinationRepository;
    private final ItemRepository itemRepository;

    /**
     * 옵션 조합을 생성(또는 재생성)하는 통합 메서드.
     * 상품에 이미 생성된 옵션 조합이 있다면 삭제하고, 요청으로 전달받은 옵션 그룹 데이터(UsedOption)를 바탕으로
     * 데카르트 곱을 이용하여 새로운 조합을 생성한 후 저장합니다.
     *
     * @param itemId  상품 ID
     * @param request  클라이언트에서 전달된 OptionCombinationRequest (예: 그룹별 선택된 옵션값 목록)
     * @return 새롭게 생성된 OptionCombination 엔티티 리스트
     */

    // ===========================================
    //  (A) 생성
    //  -- 레거시 코드 현재 뷰에서 optioncombination을 생성하고 있으므로 주석처리함
    //  추후에 옵션 분리형까지 진행하고 사용처가 없으면 삭제
    // ===========================================
//    @Transactional
//    public List<OptionCombination> generateCombinations(Long itemId, OptionCombinationRequest request) {
//        // 1. 상품(Item) 조회
//        Item item = itemRepository.findById(itemId)
//                .orElseThrow(() -> new IllegalArgumentException("Invalid item id: " + itemId));
//        // 옵션 재고를 사용하지 않는 경우, 조합 생성 없이 빈 리스트 반환
//
////        if (!item.isUseOptionStock()) {  boolean값으로 쓸경우 이것으로 변경
//        if (Boolean.FALSE.equals(item.getUseOptionStock())) {
//            // 옵션재고 미사용: 상품 전체 재고(stock)를 사용하므로, 조합은 생성하지 않음.
//            // 예를 들어, 별도의 로직으로 상품 재고를 관리하거나, 기존 조합들을 삭제할 수 있음.
//            return Collections.emptyList();
//        }
//
//        // 3. 기존 옵션 조합 삭제 (상품에 이미 등록된 조합이 있으면 삭제)
//        combinationRepository.deleteByItem(item);
//
//        // 4. 클라이언트로부터 전달된 groups 데이터 (예: [["블랙", "화이트"], ["S", "M", "L"], ["면", "폴리에스터"]])
//        List<UsedOption> usedOptions = new ArrayList<>();
//        // 예시: request.getGroups()가 [["블랙", "화이트"], ["S", "M", "L"], ["면", "폴리에스터"]] 라면,
//        // 실제 옵션명은 미리 정해져 있다고 가정하고 (예: ["색상", "사이즈", "소재"]) - 실제로는 사용안함
//        // optioncombination생성용도로 optionName을 받음 generateCombinations을 해도 실제 optionName을 사용하지 않고
//        // usedopiton도 아직 db에 저장하지 않음 추후에 사용할 예정
//        List<String> optionNames = request.getOptionNames(); // 뷰에서 전달받은 옵션명 목록
//        List<List<String>> groups = request.getGroups();
//        // groups의 크기만큼 반복
//        for (int i = 0; i < groups.size(); i++) {
//            // optionNames의 인덱스가 있는지 체크 (없으면 예외 처리 혹은 무시)
//            if (i >= optionNames.size()) {
//                throw new IllegalArgumentException("옵션명 목록의 개수가 옵션 그룹의 개수보다 적습니다.");
//            }
//            String optionName = optionNames.get(i);
//            for (String optionValue : groups.get(i)) {
//                UsedOption usedOption = new UsedOption();
//                usedOption.setOptionName(optionName);
//                usedOption.setOptionValue(optionValue);
//                usedOptions.add(usedOption);
//            }
//        }
//
//
//        // 1) 옵션 그룹별로 UsedOption들을 그룹핑하여 Map(키: 옵션명, 값: 옵션값 목록)으로 변환
//        Map<String, List<String>> optionsMap = usedOptions.stream()
//                .collect(Collectors.groupingBy(
//                        UsedOption::getOptionName,
//                        Collectors.mapping(UsedOption::getOptionValue, Collectors.toList())
//                ));
//
//        // 2) Map의 값들(List<String>)만 추출 → List<List<String>>
//        List<List<String>> valuesList = new ArrayList<>(optionsMap.values());
//
//        // 3) 데카르트 곱(모든 조합) 생성
//        List<String> combinations = cartesianItem(valuesList);
//
//        // 4) 생성된 조합 문자열을 OptionCombination 엔티티로 변환
//        List<OptionCombination> combinationEntities = new ArrayList<>();
//        for (String combination : combinations) {
//            OptionCombination entity = new OptionCombination();
//            entity.setCombination(combination);
//            entity.setStock(0);  // 기본 재고 0 설정 (예시)
//            entity.setItem(item);
//            combinationEntities.add(entity);
//        }
//
//        // 5) DB에 저장 후 반환
//        return combinationRepository.saveAll(combinationEntities);
//    }
//
//    // 데카르트 곱을 생성하는 재귀 함수 (각 리스트의 값을 "/"로 연결)
//    private List<String> cartesianItem(List<List<String>> lists) {
//        List<String> result = new ArrayList<>();
//        cartesianItemHelper(lists, 0, "", result);
//        return result;
//    }
//
//    private void cartesianItemHelper(List<List<String>> lists, int depth, String current, List<String> result) {
//        if (depth == lists.size()) {
//            // 첫번째 "/" 제거 후 결과 추가
//            result.add(current.substring(1));
//            return;
//        }
//        for (String value : lists.get(depth)) {
//            cartesianItemHelper(lists, depth + 1, current + "/" + value, result);
//        }
//    }
//
//    // ===========================================
//    //  (B) 조회
//   // ===========================================
//
//    // 이미 생성된 조합을 조회하는 메서드 (필요 시)
//    @Transactional(readOnly = true)
//    public List<OptionCombination> getCombinationsByItem(Item item) {
//        return combinationRepository.findByItem(item);
//    }
//
}
