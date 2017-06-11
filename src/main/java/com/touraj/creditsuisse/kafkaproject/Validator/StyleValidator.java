package com.touraj.creditsuisse.kafkaproject.Validator;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Arrays;
import java.util.List;

/**
 * Created by toraj on 06/08/2017.
 */
public class StyleValidator implements IValidator {

    private String message = null;
    JSONArray validationMessages;

    public StyleValidator(JSONArray validationMessages) {
        this.validationMessages = validationMessages;
    }

    @Override
    public boolean processValidation(JSONObject jsonObj, int tradeNumber) {

        List<String> validStylesList = Arrays.asList("AMERICAN", "EUROPEAN");

        boolean isValidationSuccessfull = true;

        if (!jsonObj.get("type").equals("VanillaOption")) {
//                [Touraj] :: Discard , Because only VanillaOption has style
            return true;
        }

        String style = (String) jsonObj.get("style").toString().toUpperCase();

        boolean res = validStylesList.contains(style);

        JSONObject jsonObjValidationMSG = new JSONObject();

        if (!res) {
            isValidationSuccessfull = false;

            jsonObjValidationMSG.put("ErrorType", "StyleNotValid");
            jsonObjValidationMSG.put("TradeNumber", tradeNumber);

            System.out.printf("Style:%s is not Valid\n", style);
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