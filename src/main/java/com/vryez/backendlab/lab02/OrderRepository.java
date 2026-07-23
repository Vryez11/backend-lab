package com.vryez.backendlab.lab02;

public interface OrderRepository {
    void save(Long memberId, int amount);
}
