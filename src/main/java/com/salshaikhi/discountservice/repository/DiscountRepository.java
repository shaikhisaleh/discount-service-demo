package com.salshaikhi.discountservice.repository;

import com.salshaikhi.discountservice.entity.Discount;
import com.salshaikhi.discountservice.entity.FlatRateDiscount;
import com.salshaikhi.discountservice.entity.PercentageBasedDiscount;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiscountRepository extends MongoRepository<Discount, String> {
    Boolean existsByCodeIgnoreCase(String code);

    @Query("{ '_class' : 'FlatRateDiscount' }")
    List<FlatRateDiscount> findAllFlatRateDiscounts();

    @Query("{ '_class' : 'PercentageBasedDiscount' }")
    List<PercentageBasedDiscount> findAllPercentageBasedDiscounts();
}
