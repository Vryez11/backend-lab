package com.vryez.backendlab.lab13;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GiftController {

    private final GiftReceiptService giftReceiptService;

    public GiftController(GiftReceiptService giftReceiptService) {
        this.giftReceiptService = giftReceiptService;
    }

    @PostMapping("/lab13/gifts")
    public GiftReceipt gift(@RequestBody @Valid GiftRequest request) {
        return giftReceiptService.issue(request.viewerName(), request.amount());
    }
}
