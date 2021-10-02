package com.pb.apitesting;

import io.restassured.authentication.FormAuthConfig;
import io.restassured.response.Response;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.Assert;
import org.testng.annotations.Test;

import static com.pb.apitesting.com.pb.constants.Constants.*;
import static io.restassured.RestAssured.given;

public class VerifyAccountIdAndAccountType {
    Long checkingAccountId;
    Long savingAccountId;

    @Test(priority = 0)
    public void createCheckedAccount() throws ParseException {
        String response = given().auth().form(USERNAME, PASSWORD,
                new FormAuthConfig("/parabank/login.htm",
                        "username", "password"))
                .queryParam("customerId", "12212")
                .queryParam("newAccountType", "0")
                .queryParam("fromAccountId", "12345")
                .when().post(CREATE_ACCOUNT_URL)
                .body().asPrettyString();

        JSONParser parser = new JSONParser();
        JSONObject parse = (JSONObject) parser.parse(response);
        checkingAccountId = (Long) parse.get(ID);
        String checkingtype = (String) parse.get(TYPE);

        Assert.assertEquals(checkingtype, CHECKING);
    }


    @Test(priority = 1)
    public void createSavingAccount() throws ParseException {
        String response = given().auth().form(USERNAME, PASSWORD, new FormAuthConfig("/parabank/login.htm",
                "username", "password"))
                .queryParam("customerId", "12212")
                .queryParam("newAccountType", "1")
                .queryParam("fromAccountId", "12345")
                .when().post(CREATE_ACCOUNT_URL)
                .body().asPrettyString();

        JSONParser parser = new JSONParser();
        JSONObject parse = (JSONObject) parser.parse(response);
        savingAccountId = (Long) parse.get(ID);

        String savingAccountType = (String) parse.get(TYPE);
        Assert.assertEquals(savingAccountType, SAVINGS);
    }

    @Test(priority = 2)
    public void verifyBillPay() throws ParseException {
        JSONObject requestParams = createBillPayeeAccountObject();

        Response response = given().auth().form(USERNAME, PASSWORD,
                new FormAuthConfig("/parabank/login.htm",
                "username", "password"))
                .queryParam("accountId", checkingAccountId)
                .queryParam("amount", "100")
                .header("Content-type", "application/json")
                .and()
                .body(requestParams.toJSONString())
                .when().post(BILL_PAY_URL)
                .then().extract().response();

        Assert.assertEquals(200, response.statusCode());
        Assert.assertEquals("checkingPayee", response.jsonPath().getString("payeeName"));
        Assert.assertEquals("100", response.jsonPath().getString("amount"));
        Assert.assertEquals(checkingAccountId.toString(), response.jsonPath().getString("accountId"));
    }

    @Test(priority = 4)
    public void checkSavingAccountIsCredited() throws ParseException {

        Response response = given().auth().form(USERNAME, PASSWORD, new FormAuthConfig("/parabank/login.htm",
                "username", "password"))
                .pathParam("saving", savingAccountId).
                        when().get(TRANSACTION_URL)
                .then().extract().response();

        Assert.assertEquals(200, response.statusCode());
        Assert.assertEquals("Credit", response.jsonPath().getString(TYPE).toString()
                .replaceAll(LEFT_REGEX, "").replaceAll(RIGHT_REGEX, ""));
        Assert.assertEquals("100.0", response.jsonPath().getString("amount")
                .replaceAll(LEFT_REGEX, "").replaceAll(RIGHT_REGEX, ""));
        Assert.assertEquals(savingAccountId.toString(), response.jsonPath().getString("accountId")
                .replaceAll(LEFT_REGEX, "").replaceAll(RIGHT_REGEX, ""));
    }

    private JSONObject createBillPayeeAccountObject() {
        JSONObject address = new JSONObject();
        address.put("street", "kolar");
        address.put("city", "bhopal");
        address.put("state", "mp");
        address.put("zipCode", "400001");


        JSONObject requestParams = new JSONObject();
        requestParams.put("accountNumber", savingAccountId);
        requestParams.put("name", "checkingPayee");
        requestParams.put("phoneNumber", "9876543210");
        requestParams.put("address", address);
        return requestParams;
    }
}
