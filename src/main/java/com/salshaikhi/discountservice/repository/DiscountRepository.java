package com.salshaikhi.discountservice.repository;

import com.salshaikhi.discountservice.entity.Discount;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscountRepository extends MongoRepository<Discount, String> {
}
