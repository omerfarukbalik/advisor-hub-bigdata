package com.sau.project2.service;

import com.sau.project2.dto.SupervisionView;
import com.sau.project2.model.Supervision;
import com.sau.project2.repository.SupervisionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SupervisionService {

    private final SupervisionRepository supervisionRepository;

    // READ list page (JOIN) -> Redis key "supervisionView::all"
    @Cacheable(value = "supervisionView", key = "'all'")
    public List<SupervisionView> findAllView() {
        return supervisionRepository.findAllWithAdviserAndStudy();
    }

    // READ one (edit form) -> Redis key "supervision::<id>"
    @Cacheable(value = "supervision", key = "#id", unless = "#result == null")
    public Supervision findById(Integer id) {
        return supervisionRepository.findById(id).orElse(null);
    }

    // CREATE / UPDATE -> write DB, refresh this entry, drop the stale list
    @Caching(put = @CachePut(value = "supervision", key = "#result.id"),
            evict = @CacheEvict(value = "supervisionView", allEntries = true))
    public Supervision save(Supervision s) {
        return supervisionRepository.save(s);
    }

    // DELETE -> remove from DB and from both caches
    @Caching(evict = { @CacheEvict(value = "supervision", key = "#id"),
            @CacheEvict(value = "supervisionView", allEntries = true) })
    public void delete(Integer id) {
        supervisionRepository.deleteById(id);
    }
}