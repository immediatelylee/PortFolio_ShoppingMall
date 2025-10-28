package com.shoppingmall.project_shoppingmall.controller;

import com.shoppingmall.project_shoppingmall.constant.ItemCategory;
import com.shoppingmall.project_shoppingmall.dto.ItemWithImgDto;
import com.shoppingmall.project_shoppingmall.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class CategoryItemQueryApiController {

    private final ItemService itemService;

    // 1뎁스: /api/categories/{main}/items
    @GetMapping("/{mainId}/items")
    public ResponseEntity<List<ItemWithImgDto>> byMain(@PathVariable("mainId") int mainCategoryId) {
        ItemCategory m = ItemCategory.idOf(mainCategoryId);
        System.out.println(m.getTitle());
        System.out.println(itemService.getItemsWithImgsByMainCategory(m.getTitle()));
        return ResponseEntity.ok(itemService.getItemsWithImgsByMainCategory(m.getTitle()));
    }

    // 2뎁스: /api/categories/{main}/{sub}/items
    @GetMapping("/{mainId}/{subId}/items")
    public ResponseEntity<List<ItemWithImgDto>> byMainSub(@PathVariable("mainId") int mainCategoryId,
                                                          @PathVariable("subId") int subCategoryId) {

        ItemCategory m = ItemCategory.idOf(mainCategoryId);
        ItemCategory s = ItemCategory.idOf(subCategoryId);

        System.out.println(m.getTitle());
        System.out.println(s.getTitle());
        System.out.println(itemService.getItemsWithImgsBySubCategory(m.getTitle(), s.getTitle()));
        return ResponseEntity.ok(itemService.getItemsWithImgsBySubCategory(m.getTitle(), s.getTitle()));
    }



    // 3뎁스: /api/categories/{main}/{sub}/{subSub}/items
    @GetMapping("/{main}/{sub}/{subSub}/items")
    public ResponseEntity<List<ItemWithImgDto>> byMainSubSub(@PathVariable("main") int mainCategoryId,
                                                             @PathVariable("sub") int subCategoryId,
                                                             @PathVariable("subSub") int subSubCategoryId) {

        ItemCategory m = ItemCategory.idOf(mainCategoryId);
        ItemCategory s = ItemCategory.idOf(subCategoryId);
        ItemCategory t = ItemCategory.idOf(subSubCategoryId);
        return ResponseEntity.ok(itemService.getItemsWithImgsBySubSubCategory(m.getTitle(), s.getTitle(), t.getTitle()));
    }

    // 테스트: /api/categories/test/{sub}/items
    @GetMapping("/test/{sub}/items")
    public ResponseEntity<List<ItemWithImgDto>> byOnlySub(@PathVariable("sub") String subCategory) {
        return ResponseEntity.ok(itemService.getItemsWithImgsByOnlySubCategory(subCategory));
    }

}
