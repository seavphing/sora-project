package com.soramitsu.assignment.gift_code_service.service.impl;

import com.soramitsu.assignment.gift_code_service.dto.CreateGiftCodeRequest;
import com.soramitsu.assignment.gift_code_service.dto.GiftCodeDto;
import com.soramitsu.assignment.gift_code_service.dto.RedeemGiftCodeRequest;
import com.soramitsu.assignment.gift_code_service.exception.GiftCodeAlreadyRedeemedException;
import com.soramitsu.assignment.gift_code_service.exception.GiftCodeExpiredException;
import com.soramitsu.assignment.gift_code_service.exception.ResourceNotFoundException;
import com.soramitsu.assignment.gift_code_service.messaging.GiftCodeEventPublisher;
import com.soramitsu.assignment.gift_code_service.model.GiftCode;
import com.soramitsu.assignment.gift_code_service.repository.GiftCodeRepository;
import com.soramitsu.assignment.gift_code_service.service.GiftCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GiftCodeServiceImpl implements GiftCodeService {

    private final GiftCodeRepository giftCodeRepository;
    private final GiftCodeEventPublisher giftCodeEventPublisher;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 12;
    private static final int DEFAULT_VALIDITY_DAYS = 30;

    @Override
    @Transactional
    public GiftCodeDto createGiftCode(CreateGiftCodeRequest request) {

        String code = generateUniqueCode();

        // Calculate expiration date
        int validityDays = (request.getValidityDays() != null && request.getValidityDays() > 0)
                ? request.getValidityDays()
                : DEFAULT_VALIDITY_DAYS;

        LocalDateTime expiresAt = LocalDateTime.now().plusDays(validityDays);

        // Create gift code
        GiftCode giftCode = GiftCode.builder()
                .code(code)
                .amount(request.getAmount())
                .currencyCode(request.getCurrencyCode())
                .createdByUserId(request.getUserId())
                .redeemed(false)
                .expiresAt(expiresAt)
                .build();

        GiftCode savedGiftCode = giftCodeRepository.save(giftCode);

        // Publish event
        giftCodeEventPublisher.publishGiftCodeCreatedEvent(savedGiftCode);

        return mapToDto(savedGiftCode);
    }

    @Override
    @Transactional
//    @Cacheable(value = "giftCodes", key = "#code")
    public GiftCodeDto getGiftCode(String code) {
        GiftCode giftCode = giftCodeRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Gift code not found"));

        return mapToDto(giftCode);
    }

    @Override
    @Transactional
//    @CacheEvict(value = "giftCodes", key = "#code")
    public GiftCodeDto redeemGiftCode(String code, RedeemGiftCodeRequest request) {
        GiftCode giftCode = giftCodeRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Gift code not found"));

        // Check if already redeemed
        if (giftCode.isRedeemed()) {
            throw new GiftCodeAlreadyRedeemedException("Gift code has already been redeemed");
        }

        // Check if expired
        if (giftCode.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new GiftCodeExpiredException("Gift code has expired");
        }

        // Update gift code as redeemed
        giftCode.setRedeemed(true);
        giftCode.setRedeemedByUserId(request.getUserId());
        giftCode.setRedeemedAt(LocalDateTime.now());

        GiftCode updatedGiftCode = giftCodeRepository.save(giftCode);

        // Publish event
        giftCodeEventPublisher.publishGiftCodeRedeemedEvent(updatedGiftCode, request.getWalletId());

        return mapToDto(updatedGiftCode);
    }

    @Override
    public List<GiftCodeDto> getGiftCodesByCreator(Long userId) {
        List<GiftCode> giftCodes = giftCodeRepository.findByCreatedByUserId(userId);

        return giftCodes.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<GiftCodeDto> getActiveGiftCodesByCreator(Long userId) {
        List<GiftCode> giftCodes = giftCodeRepository.findByCreatedByUserIdAndExpiresAtAfterAndRedeemedFalse(
                userId, LocalDateTime.now());

        return giftCodes.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<GiftCodeDto> getRedeemedGiftCodes(Long userId) {
        List<GiftCode> giftCodes = giftCodeRepository.findByRedeemedByUserId(userId);

        return giftCodes.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private String generateUniqueCode() {
        Random random = new Random();
        boolean isUnique = false;
        String code = "";

        while (!isUnique) {
            StringBuilder sb = new StringBuilder(CODE_LENGTH);
            for (int i = 0; i < CODE_LENGTH; i++) {
                int randomIndex = random.nextInt(CHARACTERS.length());
                sb.append(CHARACTERS.charAt(randomIndex));
            }

            code = sb.toString();

            // Check if code already exists
            if (!giftCodeRepository.existsById(code)) {
                isUnique = true;
            }
        }

        return code;
    }

    private GiftCodeDto mapToDto(GiftCode giftCode) {
        return GiftCodeDto.builder()
                .code(giftCode.getCode())
                .amount(giftCode.getAmount())
                .currencyCode(giftCode.getCurrencyCode())
                .redeemed(giftCode.isRedeemed())
                .createdByUserId(giftCode.getCreatedByUserId())
                .redeemedByUserId(giftCode.getRedeemedByUserId())
                .expiresAt(giftCode.getExpiresAt())
                .createdAt(giftCode.getCreatedAt())
                .redeemedAt(giftCode.getRedeemedAt())
                .build();
    }
}
