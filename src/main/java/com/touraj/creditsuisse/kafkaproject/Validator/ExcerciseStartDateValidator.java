package com.touraj.creditsuisse.kafkaproject.Validator;

import com.touraj.creditsuisse.kafkaproject.util.Utility;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by toraj on 06/08/2017.
 */
public class ExcerciseStartDateValidator implements IValidator {

    private String message = null;
    JSONArray validationMessages;

    public ExcerciseStartDateValidator(JSONArray validationMessages) {
        this.validationMessages = validationMessages;
    }

    @Override
    public boolean processValidation(JSONObject jsonObj, int tradeNumber) {

        boolean isValidationSuccessfull = true;

        if (!jsonObj.get("type").equals("VanillaOption")) {
//                [Touraj] :: Discard , Because only VanillaOption has style
            return true;
        }

        if (!jsonObj.get("style").toString().equalsIgnoreCase("AMERICAN")) {
//                [Touraj] :: Discard , Because only AMERICAN Style has excerciseStartDate
            return true;
        }

        String tradeDate = (String) jsonObj.get("tradeDate");
        String expiryDate = (String) jsonObj.get("expiryDate");
        String excerciseStartDate = (String) jsonObj.get("excerciseStartDate");

        //[Touraj] tradeDate < excerciseStartDate < expiryDate
        boolean res1 = Utility.checkBeforeDate(excerciseStartDate, tradeDate);
        boolean res2 = Utility.checkBeforeDate(excerciseStartDate, expiryDate);

        JSONObject jsonObjValidationMSG = new JSONObject();
        if (res1 || !res2) {
            isValidationSuccessfull = false;

            jsonObjValidationMSG.put("ErrorType", "InvalidExcerciseStartDate");
            jsonObjValidationMSG.put("TradeNumber", tradeNumber);

            System.out.printf("ExcerciseStartDate:%s is not Valid\n", excerciseStartDate);
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