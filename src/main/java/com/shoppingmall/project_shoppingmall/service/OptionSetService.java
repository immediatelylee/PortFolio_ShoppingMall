package com.shoppingmall.project_shoppingmall.service;

import com.shoppingmall.project_shoppingmall.domain.Option;
import com.shoppingmall.project_shoppingmall.domain.OptionSet;
import com.shoppingmall.project_shoppingmall.repository.OptionSetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


    @Transactional
    public List<OptionSet> getAllOptionSets() {
        // OptionSet을 fetch 할 때, 내부 Option, OptionValue도 한번에 필요하면
        // fetch join or @EntityGraph를 고려가능
        return optionSetRepository.findAll();
    }

    @Transactional
    public OptionSet findByIdFetch(Long setId){
        // 실제로는 fetch join, @EntityGraph, or other approach
        // 여기서는 단순히 findById
        return optionSetRepository.findByIdFetchAll(setId);
    }
}
