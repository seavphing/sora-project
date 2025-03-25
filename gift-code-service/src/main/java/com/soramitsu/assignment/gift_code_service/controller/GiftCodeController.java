package com.soramitsu.assignment.gift_code_service.controller;

import com.soramitsu.assignment.gift_code_service.dto.CreateGiftCodeRequest;
import com.soramitsu.assignment.gift_code_service.dto.GiftCodeDto;
import com.soramitsu.assignment.gift_code_service.dto.RedeemGiftCodeRequest;
import com.soramitsu.assignment.gift_code_service.service.GiftCodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/gift-codes")
public class GiftCodeController {

    private final GiftCodeService giftCodeService;

    @PostMapping("/create")
    public ResponseEntity<GiftCodeDto> createGiftCode(@Valid @RequestBody CreateGiftCodeRequest request) {
        GiftCodeDto giftCode = giftCodeService.createGiftCode(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(giftCode);
    }

    @GetMapping("/{code}")
    public ResponseEntity<GiftCodeDto> getGiftCode(@PathVariable String code) {
        GiftCodeDto giftCode = giftCodeService.getGiftCode(code);
        return ResponseEntity.ok(giftCode);
    }

    @PostMapping("/redeem/{code}")
    public ResponseEntity<GiftCodeDto> redeemGiftCode(
            @PathVariable String code,
            @Valid @RequestBody RedeemGiftCodeRequest request) {
        GiftCodeDto giftCode = giftCodeService.redeemGiftCode(code, request);
        return ResponseEntity.ok(giftCode);
    }

    @GetMapping("/created-by/{userId}")
    public ResponseEntity<List<GiftCodeDto>> getGiftCodesByCreator(@PathVariable Long userId) {
        List<GiftCodeDto> giftCodes = giftCodeService.getGiftCodesByCreator(userId);
        return ResponseEntity.ok(giftCodes);
    }

    @GetMapping("/active/created-by/{userId}")
    public ResponseEntity<List<GiftCodeDto>> getActiveGiftCodesByCreator(@PathVariable Long userId) {
        List<GiftCodeDto> giftCodes = giftCodeService.getActiveGiftCodesByCreator(userId);
        return ResponseEntity.ok(giftCodes);
    }

    @GetMapping("/redeemed-by/{userId}")
    public ResponseEntity<List<GiftCodeDto>> getRedeemedGiftCodes(@PathVariable Long userId) {
        List<GiftCodeDto> giftCodes = giftCodeService.getRedeemedGiftCodes(userId);
        return ResponseEntity.ok(giftCodes);
    }
}
