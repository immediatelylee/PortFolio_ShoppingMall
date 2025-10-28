package com.shoppingmall.project_shoppingmall.constant;

import com.fasterxml.jackson.annotation.*;

import java.text.Normalizer;
import java.util.*;
import java.util.stream.*;

public enum ItemCategory {
    ROOT               (   0, "카테고리",             null,                   0L),

    // L1
    FASHION            ( 100, "패션의류/잡화",        ROOT,                   1L),
    // L2
    FASHION_MEN        ( 110, "남성",                FASHION,                2L),
    // L3
    MEN_T_SHIRT            ( 111, "티셔츠",              FASHION_MEN,           3L),
    MEN_SWEATSHIRT_HOOD    ( 112, "스웻셔츠/후드",        FASHION_MEN,           3L),
    MEN_SHIRT              ( 113, "셔츠",                FASHION_MEN,           3L),
    MEN_SUIT               ( 114, "정장",                FASHION_MEN,           3L),
    MEN_PANTS              ( 115, "바지",                FASHION_MEN,           3L),

    FASHION_WOMEN      ( 120, "여성",                FASHION,                2L),
    WOMEN_T_SHIRT          ( 121, "티셔츠",              FASHION_WOMEN,         3L),
    WOMEN_BLOUSE           ( 122, "블라우스/셔츠",        FASHION_WOMEN,         3L),
    WOMEN_SWEATSHIRT_HOOD  ( 123, "스웻셔츠/후드",        FASHION_WOMEN,         3L),
    WOMEN_SUIT             ( 124, "정장",                FASHION_WOMEN,         3L),
    WOMEN_ONE_PIECE        ( 125, "원피스",              FASHION_WOMEN,         3L),
    WOMEN_SKIRT            ( 126, "치마",                FASHION_WOMEN,         3L),
    WOMEN_PANTS            ( 127, "바지",                FASHION_WOMEN,         3L),

    UNISEX             ( 130, "남녀공용",            FASHION,                2L),
    UNISEX_T_SHIRT         ( 131, "티셔츠",              UNISEX,                3L),
    UNISEX_PANTS           ( 132, "바지",                UNISEX,                3L),

    BAG_ACC            ( 140, "가방/잡화",           FASHION,                2L),
    BACKPACK               ( 141, "백팩",                BAG_ACC,               3L),
    CROSS_BAG              ( 142, "크로스백",            BAG_ACC,               3L),
    SHOULDER_BAG           ( 143, "숄더백",              BAG_ACC,               3L),
    MINI_BAG               ( 144, "미니백",              BAG_ACC,               3L),
    ECO_BAG                ( 145, "캔버스/에코백",        BAG_ACC,               3L),
    WALLET                 ( 146, "지갑",                BAG_ACC,               3L),
    BELT                   ( 147, "벨트",                BAG_ACC,               3L),

    SHOES              ( 150, "신발",                FASHION,                2L),
    SNEAKERS               ( 151, "운동화/스니커즈",      SHOES,                 3L),
    FLAT_SHOES             ( 152, "단화/플랫",            SHOES,                 3L),
    HEEL                   ( 153, "힐",                  SHOES,                 3L),
    BOOTS                  ( 154, "워커/부츠",            SHOES,                 3L),
    SLIPPER                ( 155, "슬리퍼",               SHOES,                 3L),

    FASHION_CHILDREN   ( 160, "아동",                FASHION,                2L),
    GIRL_FASHION           ( 161, "여아",                FASHION_CHILDREN,      3L),
    BOY_FASHION            ( 162, "남아",                FASHION_CHILDREN,      3L),

    FOOD               ( 200, "식품",                ROOT,                   1L),
    INSTANT              ( 210, "가공/즉석식품",       FOOD,                  2L),
    BEVERAGE             ( 220, "생수/음료",           FOOD,                  2L),
    FRESH                ( 230, "신선식품",            FOOD,                  2L),
    MEAT_EGG             ( 240, "축산/계란",           FOOD,                  2L),
    RICE                 ( 250, "쌀/잡곡",             FOOD,                  2L),

    DIGITAL            ( 300, "가전/디지털",          ROOT,                   1L),
    VIDEO                ( 310, "TV/영상가전",         DIGITAL,               2L),
    TV                      ( 311, "TV",                  VIDEO,                3L),
    PROJECTOR               ( 312, "프로젝터/스크린",      VIDEO,                3L),
    COMPUTER             ( 320, "컴퓨터/게임/SW",      DIGITAL,               2L);


    private final int id;
    // 카테고리 이름
    private final String title;

    private final String slug;

    // 부모 카테고리
    @JsonIgnore
    private final ItemCategory parentItemCategory;

    // 자식카테고리
    private final List<ItemCategory> childCategories;

    // depth
    private final Long depth;

//    ItemCategory(String title, ItemCategory parentItemCategory,Long depth) {
//        this.childCategories = new ArrayList<>();
//        this.title = title;
//        this.parentItemCategory = parentItemCategory;
//        if(Objects.nonNull(parentItemCategory)) {
//            parentItemCategory.childCategories.add(this);
//        }
//        this.depth = depth;
//    }
    // ───────── 생성자
    ItemCategory(int id, String title, ItemCategory parentItemCategory, Long depth) {
        this.id = id;
        this.title = title;
        this.slug = toSlug(title); // title로부터 slug 자동 부여
        this.parentItemCategory = parentItemCategory;
        this.childCategories = new ArrayList<>();
        if (Objects.nonNull(parentItemCategory)) {
            parentItemCategory.childCategories.add(this);
        }
        this.depth = depth;
    }

    // ───────── getters
    public int getId() { return id; }
    public String getTitle() {
        return title;
    }
    public String getSlug() { return slug; }
    // 부모카테고리 Getter
    public Optional<ItemCategory> getParentItemCategory() {
        return Optional.ofNullable(parentItemCategory);
    }

    // 자식카테고리 Getter
    public List<ItemCategory> getChildCategories() {
        return Collections.unmodifiableList(childCategories);
    }

    // 마지막 카테고리(상품추가 가능)인지 반환
    public boolean isLeafItemCategory() {
        return childCategories.isEmpty();
    }

    // 마지막 카테고리(상품추가 가능)들 반환
    public List<ItemCategory> getLeafCategories() {
        return Arrays.stream(ItemCategory.values())
                .filter(ItemCategory -> ItemCategory.isLeafItemCategoryOf(this))
                .collect(Collectors.toList());
    }

    private boolean isLeafItemCategoryOf(ItemCategory ItemCategory) {
        return this.isLeafItemCategory() && ItemCategory.contains(this);
    }

    private boolean contains(ItemCategory ItemCategory) {
        if(this.equals(ItemCategory)) return true;

        return Objects.nonNull(ItemCategory.parentItemCategory) &&
                this.contains(ItemCategory.parentItemCategory);
    }

    public Long getDepth() {
        return depth;
    }

    // ───────── 조회 유틸
    public static ItemCategory titleOf(String title) {
        for (ItemCategory Ltitle : ItemCategory.values()) {
            if (Ltitle.getTitle().equals(title)){
                return Ltitle;
            }
        }
        return null;
    }

    public static ItemCategory idOf(int id) {
        ItemCategory c = BY_ID.get(id);
        if (c == null) throw new IllegalArgumentException("Unknown category id: " + id);
        return c;
    }

//    public static ItemCategory slugOf(String slug) {
//        ItemCategory c = BY_SLUG.get(slug);
//        if (c == null) throw new IllegalArgumentException("Unknown category slug: " + slug);
//        return c;
//    }

    // ───────── 정적 인덱스
    private static final Map<Integer, ItemCategory> BY_ID =
            Arrays.stream(values()).collect(Collectors.toMap(ItemCategory::getId, x -> x));

//    private static final Map<String, ItemCategory> BY_SLUG =
//            Arrays.stream(values()).collect(Collectors.toMap(ItemCategory::getSlug, x -> x));


    // ───────── slugify 규칙(간단 버전: 필요시 개선)
    private static String toSlug(String s) {
        // 유니코드 정규화 → 공백류/슬래시 → 대시 → 연속 대시 정리 → 소문자
        String n = Normalizer.normalize(s, Normalizer.Form.NFKC)
                .trim()
                .replaceAll("[/\\s]+", "-")
                .replaceAll("[^\\p{IsAlphabetic}\\p{IsDigit}-]+", "-")
                .replaceAll("-{2,}", "-")
                .replaceAll("^-|-$", "");
        return n.toLowerCase(Locale.ROOT);
    }
}
