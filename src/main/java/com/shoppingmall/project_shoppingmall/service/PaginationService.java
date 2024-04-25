package com.shoppingmall.project_shoppingmall.service;

import org.springframework.stereotype.*;

import java.util.*;
import java.util.stream.*;

@Service
public class PaginationService {

    private static final int BAR_LENGTH = 5;

    public List<Integer> getPaginationBarNumbers(int currentPageNumber, int totalPages) {
        int startNumber = Math.max(currentPageNumber - (BAR_LENGTH / 2), 0);
        int endNumber = Math.min(startNumber + BAR_LENGTH, totalPages);

        return IntStream.range(startNumber, endNumber).boxed().collect(Collectors.toList());
    }

    public int currentBarLength() {
        return BAR_LENGTH;
    }
}
