package com.salshaikhi.discountservice.service;

import com.salshaikhi.discountservice.dto.BillDto;
import com.salshaikhi.discountservice.dto.DiscountRequest;
import com.salshaikhi.discountservice.dto.DiscountResponse;
import com.salshaikhi.discountservice.dto.ItemDto;
import com.salshaikhi.discountservice.entity.Discount;
import com.salshaikhi.discountservice.entity.User;
import com.salshaikhi.discountservice.entity.enums.UserType;
import com.salshaikhi.discountservice.exception.DuplicateCodeException;
import com.salshaikhi.discountservice.exception.NotFoundException;
import com.salshaikhi.discountservice.mapper.DiscountMapper;
import com.salshaikhi.discountservice.repository.DiscountRepository;
import com.salshaikhi.discountservice.security.SecurityUtil;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
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

        Discount newDiscount = new Discount();
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

        User user = new User(); //Assuming user is fetched from some source
        user.setUserType(UserType.EMPLOYEE);
        user.setCreatedAt(java.time.Instant.now().minus(365 * 3, ChronoUnit.DAYS)); //3 years old account

        List<Discount> applicableFlatDiscounts = getApplicableFlatDiscounts(currentUser, billPrice);
        Discount highestPercentDiscount = getHighestApplicablePercentDiscount(currentUser, billPrice);

        //No applicable discounts
        if (applicableFlatDiscounts.isEmpty() && highestPercentDiscount == null) {
            billDto.setAmountAfterDiscount(billPrice - discountAmountSum);
            return billDto;
        }
        else if (!applicableFlatDiscounts.isEmpty()) {
            for (Discount discount : applicableFlatDiscounts) {
                Integer flatDiscountMultiplesPerAmount  = (int) Math.floor(billPrice / discount.getCondition().getPerAmountSpent());
                Double flatDiscount = flatDiscountMultiplesPerAmount * discount.getAmount();
                discountAmountSum += flatDiscount;
                billDto.getAppliedDiscounts().add(discount.getCode());
            }
        }

        //Apply highest percent discount on eligible items
        if (highestPercentDiscount != null) {
            Boolean discountApplied = false;
            for (ItemDto item : billDto.getItems()) {
                if(!highestPercentDiscount.getCondition().getExcludedCategories().contains(item.getCategory())) {
                    Double percentDiscountAmount = (item.getPrice() * item.getQuantity()) * (highestPercentDiscount.getAmount() / 100);
                    discountAmountSum += percentDiscountAmount;
                    discountApplied = true;
                }
            }
            if (discountApplied) {
                billDto.getAppliedDiscounts().add(highestPercentDiscount.getCode());
            }
        }

        billDto.setAmountAfterDiscount(billPrice - discountAmountSum);

        return billDto;
    }

    private List<Discount> getApplicableFlatDiscounts(User user, Double billPrice) {
        List<Discount> flatDiscounts = discountRepository.findByIsPercentageOrderByAmount(false);
        List<Discount> applicableDiscounts = checkDiscountAppliesToUser(user, billPrice, flatDiscounts);
        return applicableDiscounts;
    }


    private Discount getHighestApplicablePercentDiscount(User user, Double billPrice) {
        List<Discount> percentDiscounts = discountRepository.findByIsPercentageOrderByAmount(true);
        List<Discount> applicableDiscounts =checkDiscountAppliesToUser(user, billPrice, percentDiscounts);
        return applicableDiscounts.stream()
                .max(java.util.Comparator.comparing(Discount::getAmount))
                .orElse(null);
    }

    private List <Discount> checkDiscountAppliesToUser(User user, Double  billPrice, List<Discount> discounts) {
        LocalDate created = user.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDate();
        Long accountAgeYears = ChronoUnit.YEARS.between(created, LocalDate.now());
        List <Discount> applicableDiscounts = new java.util.ArrayList<>();
        for (Discount discount : discounts) {
            // Check discount applies on user type
            boolean userTypeMatches = discount.getCondition().getUserType() == null
                    || user.getUserType().equals(discount.getCondition().getUserType());
            // Check Discount applies on per amount spent
            boolean perAmountMatches = discount.getCondition().getPerAmountSpent() == null
                    || billPrice >= discount.getCondition().getPerAmountSpent();
            // Check Discount applies on account age
            boolean accountAgeMatches = discount.getCondition().getMinAccountAgeYears() == null
                    || accountAgeYears.intValue()  >= discount.getCondition().getMinAccountAgeYears();
            // Check Discount is active and not expired
            Boolean discountActive = discount.isActive() && discount.getExpiryDate().isAfter(Instant.now());
            if (userTypeMatches && perAmountMatches && accountAgeMatches && discountActive) {
                applicableDiscounts.add(discount);
            }
        }
        return applicableDiscounts;
    }
}
