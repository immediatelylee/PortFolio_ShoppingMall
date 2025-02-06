package com.shoppingmall.project_shoppingmall.controller;

import com.shoppingmall.project_shoppingmall.domain.Item;
import com.shoppingmall.project_shoppingmall.domain.OptionCombination;
import com.shoppingmall.project_shoppingmall.domain.UsedOption;
import com.shoppingmall.project_shoppingmall.dto.OptionCombinationDto;
import com.shoppingmall.project_shoppingmall.dto.OptionCombinationRequest;
import com.shoppingmall.project_shoppingmall.repository.ItemRepository;
import com.shoppingmall.project_shoppingmall.service.OptionCombinationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/optionCombination")
public class OptionCombinationController {

    private final OptionCombinationService optionCombinationService;
    private final ItemRepository itemRepository; // 상품 조회용

    @PostMapping("/generate/{itemId}")
    public ResponseEntity<List<OptionCombinationDto>> generateCombinations(
            @PathVariable Long itemId,
            @RequestBody OptionCombinationRequest request) {

        // 해당 아이템 조회 (없으면 404 응답)
        Item item = itemRepository.findById(itemId).orElse(null);
        if(item == null) {
            return ResponseEntity.notFound().build();
        }

        List<UsedOption> usedOptions = new ArrayList<>();
        // 예시: request.getGroups()가 [["블랙", "화이트"], ["S", "M", "L"], ["면", "폴리에스터"]] 라면,
        // 실제 옵션명은 미리 정해져 있다고 가정하고 (예: ["색상", "사이즈", "소재"]) - 실제로는 사용안함
        // optioncombination생성용도로 optionName을 받음 generateCombinations을 해도 실제 optionName을 사용하지 않고
        // usedopiton도 아직 db에 저장하지 않음 추후에 사용할 예정
        List<String> optionNames = Arrays.asList("색상", "사이즈", "소재");
        List<List<String>> groups = request.getGroups();
        for (int i = 0; i < groups.size(); i++) {
            String optionName = optionNames.get(i);
            for(String optionValue : groups.get(i)) {
                UsedOption usedOption = new UsedOption();
                usedOption.setOptionName(optionName);
                usedOption.setOptionValue(optionValue);
                usedOptions.add(usedOption);
            }
        }

        // 데카르트 곱 조합 생성 및 저장
        List<OptionCombination> combinationEntities = optionCombinationService.generateCombinations(item, usedOptions);
        List<OptionCombinationDto> dtos = combinationEntities.stream()
                .map(OptionCombinationDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}
