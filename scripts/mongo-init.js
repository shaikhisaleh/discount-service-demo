db = db.getSiblingDB('discount');

db.users.insertMany([
    {
        username: "employee1",
        email: "employee1@example.com",
        user_type: "EMPLOYEE",
        created_at: new Date(),
        updated_at: new Date()
    },
    {
        username: "affiliate1",
        email: "affiliate1@example.com",
        user_type: "AFFILIATE",
        created_at: new Date(Date.now() - 1000 * 60 * 60 * 24 * 365), // 1 year ago
        updated_at: new Date()
    },
    {
        username: "customer1",
        email: "customer1@example.com",
        user_type: "CUSTOMER",
        created_at: new Date(Date.now() - 1000 * 60 * 60 * 24 * 365 * 2), // 2 years ago
        updated_at: new Date()
    }
]);

db.discounts.insertMany([
    {
        code: "EMPLOYEE30",
        description: "30% off for employees",
        condition: {
            min_account_age_years: null,
            user_type: "EMPLOYEE",
            per_amount_spent: null,
            excluded_categories: [
                "Groceries"
            ]
        },
        amount: 30,
        is_percentage: true,
        active: true,
        expiry_date: new Date(Date.now() + 1000 * 60 * 60 * 24 * 30),
        created_at: new Date(),
        updated_at: new Date()
    },
    {
        code: "AFFILIATE15",
        condition: {
            min_account_age_years: null,
            user_type: "AFFILIATE",
            per_amount_spent: null,
            excluded_categories: [
                "Groceries"
            ]
        },
        description: "15% off for affiliates",
        amount: 15,
        is_percentage: false,
        active: true,
        expiry_date: new Date(Date.now() + 1000 * 60 * 60 * 24 * 30),
        created_at: new Date(),
        updated_at: new Date()
    },
    {
        code: "LOYALTY5",
        condition: {
            min_account_age_years: 2,
            user_type: null,
            per_amount_spent: null,
            excluded_categories: [
                "Groceries"
            ]
        },
        description: "5% off for loyal customers",
        amount: 5,
        is_percentage: true,
        active: true,
        expiry_date: new Date(Date.now() + 1000 * 60 * 60 * 24 * 30),
        created_at: new Date(),
        updated_at: new Date()
    },
    {
        code: "FLAT5",
        condition: {
            min_account_age_years: null,
            user_type: null,
            per_amount_spent: 100.0,
            excluded_categories: []
        },
        description: "5$ off for every 100$ spent",
        amount: 5,
        is_percentage: false,
        active: true,
        expiry_date: new Date(Date.now() + 1000 * 60 * 60 * 24 * 30),
        created_at: new Date(),
        updated_at: new Date()
    }
]);
