package com.shoppingmall.project_shoppingmall.service;

import com.shoppingmall.project_shoppingmall.domain.*;
import com.shoppingmall.project_shoppingmall.repository.UsedOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsedOptionService {
    private final UsedOptionRepository usedOptionRepository;

    public List<UsedOption> generateUsedOptions(Item item) {
        List<UsedOption> usedOptions = new ArrayList<>();
        OptionSet optionSet = item.getOptionSet();

        for (Option option : optionSet.getOptions()) {
            for (String value : option.getValues()) {
                UsedOption usedOption = new UsedOption();
                usedOption.setOptionName(option.getName());
                usedOption.setOptionValue(value);
                usedOption.setItem(item);
                usedOptions.add(usedOption);
            }
        }

        return usedOptionRepository.saveAll(usedOptions);
    }

    // 특정 Item에 연관된 UsedOption 조회
    public List<UsedOption> getUsedOptionsByItem(Item item) {
        return usedOptionRepository.findByItem(item);
    }
}
