package com.shoppingmall.project_shoppingmall.resolver;

import com.shoppingmall.project_shoppingmall.constant.ItemCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryResolver {

    // enum을 쓰는 경우 – 정적 인덱스 활용
    public int toId(String maybeIdOrTitle) {
        if (maybeIdOrTitle == null || maybeIdOrTitle.isBlank()) {
            throw new IllegalArgumentException("카테고리 입력이 비어있습니다.");
        }
        // 숫자면 그대로 id
        if (maybeIdOrTitle.chars().allMatch(Character::isDigit)) {
            return Integer.parseInt(maybeIdOrTitle);
        }
        // 한글(표시값) → enum 매핑
        var cat = ItemCategory.titleOf(maybeIdOrTitle);
        if (cat == null) {
            throw new IllegalArgumentException("알 수 없는 카테고리: " + maybeIdOrTitle);
        }
        return cat.getId();
    }

    public String toTitle(int id) {
        return ItemCategory.idOf(id).getTitle();
    }
}
