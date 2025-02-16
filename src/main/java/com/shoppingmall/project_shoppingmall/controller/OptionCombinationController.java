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

//    @PostMapping("/generate/{itemId}")
//    public ResponseEntity<List<OptionCombinationDto>> generateCombinations(
//            @PathVariable Long itemId,
//            @RequestBody OptionCombinationRequest request) {
//
//        // 해당 아이템 조회 (없으면 404 응답)
//        Item item = itemRepository.findById(itemId).orElse(null);
//        if(item == null) {
//            return ResponseEntity.notFound().build();
//        }
//
//
//
//         데카르트 곱 조합 생성 및 저장
//         현재 뷰에서 상품옵션을 생성해서 itemsave때 저장하고 있으므로
//         레거시 코드로 남김 추후에 옵션 분리형 구현한 이후에도 사용처 없으면 삭제
//        List<OptionCombination> combinationEntities = optionCombinationService.generateCombinations(itemId, request);
//        // OptionCombinationDto로 변환
//        List<OptionCombinationDto> dtos = combinationEntities.stream()
//                .map(OptionCombinationDto::fromEntity)
//                .collect(Collectors.toList());
//        return ResponseEntity.ok(dtos);
//
//    }
}
