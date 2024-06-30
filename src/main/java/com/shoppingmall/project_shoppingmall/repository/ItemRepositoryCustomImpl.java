package com.shoppingmall.project_shoppingmall.repository;

import com.querydsl.core.*;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.*;
import com.shoppingmall.project_shoppingmall.constant.*;
import com.shoppingmall.project_shoppingmall.domain.*;
import com.shoppingmall.project_shoppingmall.dto.*;
import org.springframework.data.domain.*;
import org.thymeleaf.util.*;



import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

public class ItemRepositoryCustomImpl implements ItemRepositoryCustom {

    private JPAQueryFactory queryFactory;

    public ItemRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Long countAllItems() {
        QItem item = QItem.item;
        return queryFactory
                .select(item.count())
                .from(item)
                .fetchOne();
    }

    @Override
    public Long countItemsBySellStatus(ItemSellStatus sellStatus) {
        QItem item = QItem.item;
        return queryFactory
                .select(item.count())
                .from(item)
                .where(item.itemSellStatus.eq(sellStatus))
                .fetchOne();
    }

    @Override
    public Long countItemsByDisplayStatus(ItemDisplayStatus displayStatus) {
        QItem item = QItem.item;
        return queryFactory
                .select(item.count())
                .from(item)
                .where(item.itemDisplayStatus.eq(displayStatus))
                .fetchOne();
    }

    @Override
    public List<Item> findItemsBySellStatus(ItemSellStatus sellStatus) {
        QItem item = QItem.item;
        return queryFactory
                .selectFrom(item)
                .where(item.itemSellStatus.eq(sellStatus))
                .fetch();
    }


    @Override
    public List<Item> findItemsByDisplayStatus(ItemDisplayStatus displayStatus) {
        QItem item = QItem.item;
        return queryFactory
                .selectFrom(item)
                .where(item.itemDisplayStatus.eq(displayStatus))
                .fetch();
    }

    public static BooleanExpression mainCategoryEq(String mainCategory) {
        if (!org.springframework.util.StringUtils.hasText(mainCategory)) {
            return null;
        }
        return QItem.item.mainCategory.eq(mainCategory);
    }

    public static BooleanExpression subCategoryEq(String subCategory) {
        if (!org.springframework.util.StringUtils.hasText(subCategory)) {
            return null;
        }
        return QItem.item.subCategory.eq(subCategory);
    }

    public static BooleanExpression subSubCategoryEq(String subSubCategory) {
        if (!org.springframework.util.StringUtils.hasText(subSubCategory)) {
            return null;
        }
        return QItem.item.subSubCategory.eq(subSubCategory);
    }


    private BooleanExpression searchSellStatusEq(ItemSellStatus searchSellStatus) {
        return searchSellStatus == null ? null : QItem.item.itemSellStatus.eq(searchSellStatus);
    }

    private BooleanExpression regDtsAfter(String searchDateType) {

        LocalDateTime dateTime = LocalDateTime.now();

        if (StringUtils.equals("all", searchDateType) || searchDateType == null) {
            return null;
        } else if (StringUtils.equals("1d", searchDateType)) {
            dateTime = dateTime.minusDays(1);
        } else if (StringUtils.equals("1w", searchDateType)) {
            dateTime = dateTime.minusWeeks(1);
        } else if (StringUtils.equals("1m", searchDateType)) {
            dateTime = dateTime.minusMonths(1);
        } else if (StringUtils.equals("6m", searchDateType)) {
            dateTime = dateTime.minusMonths(6);
        }

        return QItem.item.regTime.after(dateTime);
    }

    private BooleanExpression searchByLike(String searchBy, String searchQuery) {

        if (StringUtils.equals("itemNm", searchBy)) {
            return QItem.item.itemNm.like("%" + searchQuery + "%");
        } else if (StringUtils.equals("createdBy", searchBy)) {
            return QItem.item.createdBy.like("%" + searchQuery + "%");
        }

        return null;
    }

    @Override
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {

        List<Item> content = queryFactory
                .selectFrom(QItem.item)
                .where(regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(),
                                itemSearchDto.getSearchQuery()))
                .orderBy(QItem.item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory.select(Wildcard.count).from(QItem.item)
                .where(regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery()))
                .fetchOne()
                ;

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression itemNmLike(String searchQuery){
        return StringUtils.isEmpty(searchQuery) ? null : QItem.item.itemNm.like("%" + searchQuery + "%");
    }

    @Override
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        QItem item = QItem.item;
        QItemImg itemImg = QItemImg.itemImg;

        QueryResults<MainItemDto> results = queryFactory
                .select(
                        new QMainItemDto(
                                item.id,
                                item.itemNm,
                                item.itemDetail,
                                itemImg.imgUrl,
                                item.price)
                )
                .from(itemImg)
                .join(itemImg.item, item)
                .where(itemImg.repimgYn.eq("Y"))
                .where(itemNmLike(itemSearchDto.getSearchQuery()))
                .orderBy(item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<MainItemDto> content = results.getResults();
        long total = results.getTotal();
        return new PageImpl<>(content, pageable, total);
    }
}
