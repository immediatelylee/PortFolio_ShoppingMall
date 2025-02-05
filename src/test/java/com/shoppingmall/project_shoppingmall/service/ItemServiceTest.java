package com.shoppingmall.project_shoppingmall.service;

import com.shoppingmall.project_shoppingmall.constant.*;
import com.shoppingmall.project_shoppingmall.domain.*;
import com.shoppingmall.project_shoppingmall.dto.*;
import com.shoppingmall.project_shoppingmall.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.*;
import org.springframework.security.test.context.support.*;
import org.springframework.test.context.*;
import org.springframework.transaction.annotation.*;
import org.springframework.web.multipart.*;
import static org.assertj.core.api.Assertions.assertThat;

import javax.persistence.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(locations="classpath:application-test.properties")
class ItemServiceTest {

    @Autowired
    ItemService itemService;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    ItemImgRepository itemImgRepository;

    @Autowired
    ItemDetailImgRepository itemDetailImgRepository;

    @Autowired
    private OptionSetRepository optionSetRepository;

    @Autowired
    private OptionCombinationRepository optionCombinationRepository;

    @Test
    @DisplayName("상품 + 옵션 + 이미지 등록 통합 테스트 (조합 일체형)")
    void testSaveItemWithCombinedOptionsAndImages() throws Exception {
        // 1) DTO 생성
        ItemFormDto itemFormDto = new ItemFormDto();
        itemFormDto.setItemNm("조합형 옵션 상품");
        itemFormDto.setPrice(20000);
        itemFormDto.setStockNumber(40);
        itemFormDto.setItemDetail("조합 일체형 옵션 테스트");
        itemFormDto.setDisplayType(OptionDisplayType.COMBINED);
        // brandId, etc.

        // [조합 목록]
        itemFormDto.setCombinationList(Arrays.asList("블랙-S", "블랙-M", "화이트-S"));

        // 2) Mock 이미지 파일 준비 (동일 or 별도)
        List<MultipartFile> itemImgFileList = new ArrayList<>();
        List<MultipartFile> itemDetailImgFileList = new ArrayList<>();
        byte[] mainImageBytes = Files.readAllBytes(Paths.get("src/test/resources/test.png"));
        itemImgFileList.add(new MockMultipartFile(
                "itemImgFile",
                "test_main.png",
                "image/png",
                mainImageBytes
        ));
        // ...sub images
        byte[] detailImageBytes = Files.readAllBytes(Paths.get("src/test/resources/detailtest.png"));
        itemDetailImgFileList.add(new MockMultipartFile(
                "itemDetailImgFile",
                "detail.png",
                "image/png",
                detailImageBytes
        ));

        // 3) Service 호출
        Long savedItemId = itemService.saveItem(itemFormDto, itemImgFileList, itemDetailImgFileList);

        // 4) 검증
        Item savedItem = itemRepository.findById(savedItemId)
                .orElseThrow(() -> new IllegalArgumentException("Item should exist"));

        assertThat(savedItem.getItemNm()).isEqualTo("조합형 옵션 상품");
        assertThat(savedItem.getDisplayType()).isEqualTo(OptionDisplayType.COMBINED);

        // 4-1) 조합 일체형 -> OptionCombination 으로 저장됐는지 확인
        List<OptionCombination> combos = optionCombinationRepository.findByItem(savedItem);
        assertThat(combos).hasSize(3);
        // 블랙-S, 블랙-M, 화이트-S
        List<String> comboStrings = combos.stream().map(OptionCombination::getCombination).collect(Collectors.toList());
        assertThat(comboStrings).containsExactlyInAnyOrder("블랙-S", "블랙-M", "화이트-S");

        // 4-2) 이미지 검증
        List<ItemImg> itemImgs = itemImgRepository.findByItemIdOrderByIdAsc(savedItemId);
        assertThat(itemImgs).isNotEmpty();
        // ...

        // (상세 이미지 등도 마찬가지로 체크)

    }
    @Test
    @DisplayName("조합 일체형 상품 저장 테스트")
    void testSaveItemWithCombinedOptions() throws Exception {
        // GIVEN
        // 1) ItemFormDto 생성 (조합 일체형)
        ItemFormDto dto = new ItemFormDto();
        dto.setItemNm("테스트 상품");
        dto.setPrice(10000);
        dto.setStockNumber(50); // etc.
        dto.setItemDetail("조합 일체형 상품 테스트");
        dto.setDisplayType(OptionDisplayType.COMBINED);

        // 2) 조합 목록
        List<String> combinationList = Arrays.asList("블랙-S", "블랙-M", "화이트-S");
        dto.setCombinationList(combinationList);

        // (이미지 리스트를 간단히 null로)
        List<MultipartFile> itemImgFileList = Collections.emptyList();
        List<MultipartFile> itemDetailImgFileList = Collections.emptyList();

        // WHEN
        // 3) saveItem 호출
        Long savedItemId = itemService.saveItem(dto, itemImgFileList, itemDetailImgFileList);

        // THEN
        // 4) DB에서 확인
        Item savedItem = itemRepository.findById(savedItemId)
                .orElseThrow(() -> new IllegalArgumentException("상품이 저장되지 않았음"));

        // displayType 확인
        assertThat(savedItem.getDisplayType()).isEqualTo(OptionDisplayType.COMBINED);

        // OptionCombination 체크
        List<OptionCombination> combos = optionCombinationRepository.findByItem(savedItem);
        // 혹은 savedItem.getOptionCombinations()
        assertThat(combos).hasSize(3);

        // 조합 문자열 확인
        List<String> actualCombos = combos.stream()
                .map(OptionCombination::getCombination)
                .collect(Collectors.toList());
        assertThat(actualCombos).containsExactlyInAnyOrder("블랙-S","블랙-M","화이트-S");
    }


}
