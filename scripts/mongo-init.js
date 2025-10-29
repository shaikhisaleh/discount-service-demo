db = db.getSiblingDB('discount');

db.users.insertMany([
    {
        username: "employee",
        email: "employee@example.com",
        user_type: "EMPLOYEE",
        created_at: new Date(),
        updated_at: new Date()
    },
    {
        username: "affiliate",
        email: "affiliate@example.com",
        user_type: "AFFILIATE",
        created_at: new Date(Date.now() - 1000 * 60 * 60 * 24 * 365), // 1 year ago
        updated_at: new Date()
    },
    {
        username: "customer",
        email: "customer@example.com",
        user_type: "CUSTOMER",
        created_at: new Date(Date.now() - 1000 * 60 * 60 * 24 * 365 * 2), // 2 years ago
        updated_at: new Date()
    }
]);

db.discounts.insertMany([
    {
        _class: "PercentageBasedDiscount",
        code: "EMPLOYEE30",
        description: "30% off for employees",
        user_type: "EMPLOYEE",
        excluded_categories: [
            "Groceries"
        ],
        amount: 30,
        active: true,
        expiry_date: new Date(Date.now() + 1000 * 60 * 60 * 24 * 30),
        created_at: new Date(),
        updated_at: new Date()
    },
    {
        _class: "PercentageBasedDiscount",
        code: "AFFILIATE15",
        description: "15% off for affiliates",
        user_type: "AFFILIATE",
        excluded_categories: [
            "Groceries"
        ],
        amount: 15,
        active: true,
        expiry_date: new Date(Date.now() + 1000 * 60 * 60 * 24 * 30),
        created_at: new Date(),
        updated_at: new Date()
    },
    {
        _class: "FlatRateDiscount",
        code: "LOYALTY5",
        description: "5% off for loyal customers (minimum account age 2 years)",
        min_account_age_years: 2,
        per_amount_spent: null,
        amount: 5,
        active: true,
        expiry_date: new Date(Date.now() + 1000 * 60 * 60 * 24 * 30),
        created_at: new Date(),
        updated_at: new Date()
    },
    {
        _class: "FlatRateDiscount",
        code: "FLAT5",
        description: "5$ off for every 100$ spent",
        min_account_age_years: null,
        per_amount_spent: 100.0,
        amount: 5,
        active: true,
        expiry_date: new Date(Date.now() + 1000 * 60 * 60 * 24 * 30),
        created_at: new Date(),
        updated_at: new Date()
    }
]);
