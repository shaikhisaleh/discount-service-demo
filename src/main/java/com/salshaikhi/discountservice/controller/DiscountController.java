package com.salshaikhi.discountservice.controller;

import com.salshaikhi.discountservice.dto.BillDto;
import com.salshaikhi.discountservice.dto.DiscountRequest;
import com.salshaikhi.discountservice.dto.DiscountResponse;
import com.salshaikhi.discountservice.service.DiscountService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("v1/api/discounts")
@Validated
public class DiscountController {
    private final DiscountService discountService;

    @GetMapping()
    public List<DiscountResponse> getAll() {
        return discountService.getAll();
    }

    @GetMapping("/{id}")
    public DiscountResponse getById(@PathVariable String id) {
        return discountService.getDiscountResponse(id);
    }

    @PostMapping()
    @ResponseStatus(code = HttpStatus.CREATED)
    public DiscountResponse create(@Valid @RequestBody DiscountRequest discount) {
        return discountService.create(discount);
    }

    @PutMapping("/{id}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public DiscountResponse update(@PathVariable String id, @Valid @RequestBody DiscountRequest discount) {
        return discountService.update(id, discount);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        discountService.delete(id);
    }

    @PostMapping("/apply")
    public BillDto applyDiscount(@RequestBody @Valid BillDto bill) {
        return discountService.applyDiscount(bill);
    }
}

