package com.salshaikhi.discountservice.repository;

import com.salshaikhi.discountservice.entity.Bill;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillRepository extends MongoRepository<Bill, String> {
}
