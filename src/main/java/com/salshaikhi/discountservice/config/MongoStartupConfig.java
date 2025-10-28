package com.salshaikhi.discountservice.config;

import com.salshaikhi.discountservice.entity.Bill;
import org.springframework.context.event.EventListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoStartupConfig {
    private final MongoTemplate mongoTemplate;

    public MongoStartupConfig(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void createCollections() {
        if (!mongoTemplate.collectionExists(Bill.class)) {
            mongoTemplate.createCollection(Bill.class);
        }
    }
}