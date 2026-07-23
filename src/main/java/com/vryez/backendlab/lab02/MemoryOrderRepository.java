package com.vryez.backendlab.lab02;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

@Repository
public class MemoryOrderRepository implements OrderRepository {

    private final Map<Long, Integer> store = new ConcurrentHashMap<>();

    @Override
    public void save(Long memberId, int amount) {
        store.put(memberId, amount);
    }

    public Integer findLast(Long memberId) {
        return store.get(memberId);
    }
}
