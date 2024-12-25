package com.shoppingmall.project_shoppingmall.service;

import com.shoppingmall.project_shoppingmall.domain.Option;
import com.shoppingmall.project_shoppingmall.domain.OptionSet;
import com.shoppingmall.project_shoppingmall.repository.OptionSetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OptionSetService {
    private final OptionSetRepository optionSetRepository;

    public OptionSet getOptionSetById(Long id) {
        return optionSetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("OptionSet not found"));
    }

    public OptionSet createOptionSet(String name, List<Option> options) {
        OptionSet optionSet = new OptionSet();
        optionSet.setName(name);
        optionSet.setOptions(options);
        return optionSetRepository.save(optionSet);
    }
}
