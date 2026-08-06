package com.vryez.backendlab.lab13;

import org.springframework.stereotype.Service;

@Service
public class GiftReceiptService {

    private final GiftRepository giftRepository;

    public GiftReceiptService(GiftRepository giftRepository) {
        this.giftRepository = giftRepository;
    }

    public GiftReceipt issue(String viewerName, long amount) {
        giftRepository.save(viewerName, amount);
        String grade = grade(amount);
        return new GiftReceipt(viewerName, amount, grade);
    }

    private String grade(long amount) {
        if (amount >= 100_000) return "다이아";
        if (amount >= 10_000) return "골드";
        return "브론즈";
    }
}
