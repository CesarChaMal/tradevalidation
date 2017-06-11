package com.touraj.creditsuisse.kafkaproject.Validator;

import com.touraj.creditsuisse.kafkaproject.util.Utility;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by toraj on 06/09/2017.
 */
public class ISO4217Validator implements IValidator {

    private String message = null;
    JSONArray validationMessages;

    public ISO4217Validator(JSONArray validationMessages) {
        this.validationMessages = validationMessages;
    }

    @Override
    public boolean processValidation(JSONObject jsonObj, int tradeNumber) {

        boolean isValidationSuccessfull = true;

        if (!jsonObj.get("type").equals("VanillaOption")) {
//                [Touraj] :: Discard , Because only VanillaOption has Currency
            return true;
        }

        String payCcy = (String) jsonObj.get("payCcy");
        String premiumCcy = (String) jsonObj.get("premiumCcy");

        boolean res1 = Utility.isValidCurrencyISO4217(payCcy);
        boolean res2 = Utility.isValidCurrencyISO4217(premiumCcy);

        JSONObject jsonObjValidationMSG1 = new JSONObject();
        JSONObject jsonObjValidationMSG2 = new JSONObject();

        if (!res1) {
            isValidationSuccessfull = false;

            jsonObjValidationMSG1.put("ErrorType", "payCcyNotValidISO4217");
            jsonObjValidationMSG1.put("TradeNumber", tradeNumber);

            System.out.printf("payCcy:%s is not valid ISO 4217\n", payCcy);

            validationMessages.put(jsonObjValidationMSG1);
        }

        if (!res2) {

            isValidationSuccessfull = false;

            jsonObjValidationMSG2.put("ErrorType", "premiumCcyNotValidISO4217");
            jsonObjValidationMSG2.put("TradeNumber", tradeNumber);

            System.out.printf("premiumCcy:%s is not valid ISO 4217\n", premiumCcy);

            validationMessages.put(jsonObjValidationMSG2);
        }

        if (!isValidationSuccessfull) {
            setMessage(jsonObjValidationMSG1.toString() + "\n" + jsonObjValidationMSG2.toString());

        }
        //[Touraj] :: Adding Validation Message to Validation Store

        return isValidationSuccessfull;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public void setMessage(String message) {

        this.message = message;
    }
}
