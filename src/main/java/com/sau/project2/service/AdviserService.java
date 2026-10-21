package com.sau.project2.service;

import com.sau.project2.model.Adviser;
import com.sau.project2.repository.AdviserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdviserService {

    private final AdviserRepository adviserRepository;

    @Cacheable(value = "advisers", key = "'all'")
    public List<Adviser> findAll() {
        return new ArrayList<>(adviserRepository.findAll());
    }

    @Cacheable(value = "adviser", key = "#id", unless = "#result == null")
    public Adviser findById(Integer id) {
        return adviserRepository.findById(id).orElse(null);
    }

    // Adviser name/photo also appear on the JOIN page -> evict that too
    @Caching(put = @CachePut(value = "adviser", key = "#result.id"),
            evict = { @CacheEvict(value = "advisers", allEntries = true),
                    @CacheEvict(value = "supervisionView", allEntries = true) })
    public Adviser save(Adviser a) {
        return adviserRepository.save(a);
    }

    // CASCADE also deletes their supervisions -> clear every related cache
    @Caching(evict = { @CacheEvict(value = "adviser", key = "#id"),
            @CacheEvict(value = "advisers", allEntries = true),
            @CacheEvict(value = "supervision", allEntries = true),
            @CacheEvict(value = "supervisionView", allEntries = true) })
    public void delete(Integer id) {
        adviserRepository.deleteById(id);
    }
}