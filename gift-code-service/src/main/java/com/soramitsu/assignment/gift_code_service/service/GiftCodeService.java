package com.soramitsu.assignment.gift_code_service.service;

import com.soramitsu.assignment.gift_code_service.dto.CreateGiftCodeRequest;
import com.soramitsu.assignment.gift_code_service.dto.GiftCodeDto;
import com.soramitsu.assignment.gift_code_service.dto.RedeemGiftCodeRequest;

import java.util.List;

public interface GiftCodeService {

    public GiftCodeDto createGiftCode(CreateGiftCodeRequest request);

    public GiftCodeDto getGiftCode(String code);

    public GiftCodeDto redeemGiftCode(String code, RedeemGiftCodeRequest request);

    public List<GiftCodeDto> getGiftCodesByCreator(Long userId);

    public List<GiftCodeDto> getActiveGiftCodesByCreator(Long userId);

    public List<GiftCodeDto> getRedeemedGiftCodes(Long userId);
}
