package com.pb.apitesting.com.pb.constants;

public class Constants {
    public static final String ID = "id";
    public static final String TYPE = "type";
    public static final String CHECKING = "CHECKING";
    public static final String SAVINGS = "SAVINGS";
    public static final String PASSWORD = "demo";
    public static final String USERNAME = "john";
    public static final String CREATE_ACCOUNT_URL = "https://parabank.parasoft.com/parabank/services_proxy/bank/createAccount";
    public static final String BILL_PAY_URL = "https://parabank.parasoft.com/parabank/services_proxy/bank/billpay?accountId=12567&amount=100";
    public static final String TRANSACTION_URL = "https://parabank.parasoft.com/parabank/services_proxy/bank/accounts/{saving}/transactions";

    public static final String LEFT_REGEX = "\\[";
    public static final String RIGHT_REGEX = "\\]";
}
