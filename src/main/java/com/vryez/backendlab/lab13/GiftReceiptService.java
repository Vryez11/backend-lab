package com.vryez.backendlab.lab13;

import org.springframework.stereotype.Service;

@Service
public class GiftReceiptService {

    private String viewerName;
    private long amount;

    private final GiftRepository giftRepository;

    public GiftReceiptService(GiftRepository giftRepository) {
        this.giftRepository = giftRepository;
    }

    public GiftReceipt issue(String viewerName, long amount) {
        this.viewerName = viewerName;
        this.amount = amount;
        giftRepository.save(this.viewerName, this.amount);
        String grade = grade();
        return new GiftReceipt(this.viewerName, this.amount, grade);
    }

    private String grade() {
        if (this.amount >= 100_000) return "다이아";
        if (this.amount >= 10_000) return "골드";
        return "브론즈";
    }
}
