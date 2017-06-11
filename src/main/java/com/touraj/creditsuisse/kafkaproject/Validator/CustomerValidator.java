package com.touraj.creditsuisse.kafkaproject.Validator;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Arrays;
import java.util.List;

/**
 * Created by toraj on 06/09/2017.
 */
public class CustomerValidator implements IValidator {

    private String message = null;
    JSONArray validationMessages;

    public CustomerValidator(JSONArray validationMessages) {
        this.validationMessages = validationMessages;
    }

    @Override
    public boolean processValidation(JSONObject jsonObj, int tradeNumber) {

        List<String> validCustomersList = Arrays.asList("PLUTO1", "PLUTO2");

        boolean isValidationSuccessfull = true;

        String customer = (String) jsonObj.get("customer");

        boolean res = validCustomersList.contains(customer);

        JSONObject jsonObjValidationMSG = new JSONObject();

        if (!res) {
            isValidationSuccessfull = false;

            jsonObjValidationMSG.put("ErrorType", "CustomerNotValid");
            jsonObjValidationMSG.put("TradeNumber", tradeNumber);

            System.out.printf("Customer:%s is not valid\n", customer);
        }

        if (!isValidationSuccessfull) {
            setMessage(jsonObjValidationMSG.toString());
        }
        //[Touraj] :: Adding Validation Message to Validation Store

        if (!isValidationSuccessfull) {
            validationMessages.put(jsonObjValidationMSG);
        }

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
