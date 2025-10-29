package com.salshaikhi.discountservice.service;

import com.salshaikhi.discountservice.dto.BillDto;
import com.salshaikhi.discountservice.dto.DiscountRequest;
import com.salshaikhi.discountservice.dto.DiscountResponse;
import com.salshaikhi.discountservice.entity.Discount;
import com.salshaikhi.discountservice.entity.FlatRateDiscount;
import com.salshaikhi.discountservice.entity.PercentageBasedDiscount;
import com.salshaikhi.discountservice.entity.User;
import com.salshaikhi.discountservice.entity.enums.DiscountType;
import com.salshaikhi.discountservice.exception.DuplicateCodeException;
import com.salshaikhi.discountservice.exception.NotFoundException;
import com.salshaikhi.discountservice.mapper.DiscountMapper;
import com.salshaikhi.discountservice.repository.DiscountRepository;
import com.salshaikhi.discountservice.security.SecurityUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@AllArgsConstructor
public class DiscountService {
    private final DiscountRepository discountRepository;
    private final DiscountMapper discountMapper;
    private final UserService userService;

    public List<DiscountResponse> getAll() {
        return discountRepository.findAll().stream()
                .map(discountMapper::discountToResponse).toList();
    }

    public Discount getOrNotFound(String id) {
        return discountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Discount not found"));
    }

    public DiscountResponse getDiscountResponse(String id) {
        Discount discount = getOrNotFound(id);
        return discountMapper.discountToResponse(discount);
    }

    public DiscountResponse create(DiscountRequest request) {
        String code = request.getCode().trim();
        if (discountRepository.existsByCodeIgnoreCase(code)) {
            throw new DuplicateCodeException("Discount code: " + code + " already exists");
        }

        Discount newDiscount;
        if (DiscountType.FLAT_RATE.equals(request.getDiscountType())) {
            newDiscount = new FlatRateDiscount();
        } else if (DiscountType.PERCENTAGE_BASED.equals(request.getDiscountType())) {
            newDiscount = new PercentageBasedDiscount();
        } else {
            throw new IllegalArgumentException("Invalid discount type: " + request.getDiscountType());
        }

        discountMapper.requestToDiscount(request, newDiscount);
        return discountMapper.discountToResponse(discountRepository.save(newDiscount));
    }

    public DiscountResponse update(String id, DiscountRequest request) {
        Discount discount = getOrNotFound(id);
        discountMapper.requestToDiscount(request, discount);
        return discountMapper.discountToResponse(discountRepository.save(discount));
    }

    public void delete(String id) {
        discountRepository.deleteById(id);
    }

    public BillDto applyDiscount(BillDto billDto) {
        User currentUser = userService.getByEmail(SecurityUtil.getUserEmail());

        Double billPrice = billDto.getItems().stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();
        billDto.setTotalPrice(billPrice);
        Double discountAmountSum = 0.0;

        // Apply all flat rate discounts
        discountAmountSum += applyAllFlatRateDiscounts(billDto, currentUser);

        // Apply highest percentage discount
        discountAmountSum += applyHighestPercentageDiscount(billDto, currentUser);
        billDto.setPriceAfterDiscount(billPrice - discountAmountSum);
        return billDto;
    }

    private double applyAllFlatRateDiscounts(BillDto billDto, User currentUser) {
        List<FlatRateDiscount> flatRateDiscounts = discountRepository.findAllFlatRateDiscounts();
        double totalFlatDiscountAmount = 0.0;

        for (Discount discount : flatRateDiscounts) {
            double discountAmount = discount.applyDiscount(billDto, currentUser);
            if (discountAmount > 0) {
                totalFlatDiscountAmount += discountAmount;
                billDto.getAppliedDiscounts().add(discount.getCode());
            }
        }
        return totalFlatDiscountAmount;
    }

    private double applyHighestPercentageDiscount(BillDto billDto, User currentUser) {
        List<PercentageBasedDiscount> percentageDiscounts = discountRepository.findAllPercentageBasedDiscounts();

        PercentageBasedDiscount highestPercentDiscount = percentageDiscounts.stream()
                .filter(discount -> discount.getAmount() > 0)
                .max(Comparator.comparingDouble(Discount::getAmount))
                .orElse(null);

        if (highestPercentDiscount != null) {
            double percentDiscountAmount = highestPercentDiscount.applyDiscount(billDto, currentUser);
            billDto.getAppliedDiscounts().add(highestPercentDiscount.getCode());
            return percentDiscountAmount;
        }
        return 0.0;
    }
}