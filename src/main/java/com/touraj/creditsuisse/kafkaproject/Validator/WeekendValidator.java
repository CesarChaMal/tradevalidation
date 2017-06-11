package com.touraj.creditsuisse.kafkaproject.Validator;

import com.touraj.creditsuisse.kafkaproject.util.Utility;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by toraj on 06/09/2017.
 */
public class WeekendValidator implements IValidator {

    private String message = null;
    JSONArray validationMessages;

    public WeekendValidator(JSONArray validationMessages) {
        this.validationMessages = validationMessages;
    }

    @Override
    public boolean processValidation(JSONObject jsonObj, int tradeNumber) {

        boolean isValidationSuccessfull = true;

        if (!(jsonObj.get("type").equals("Spot") || jsonObj.get("type").equals("Forward"))) {
//                [Touraj] :: Discard , Because only Spot and Forward types have valueDate
            return true;
        }

        String valueDate = (String) jsonObj.get("valueDate");

        boolean result = Utility.isDateFallinWeekend(valueDate);

        JSONObject jsonObjValidationMSG = new JSONObject();
        if (result) {
            isValidationSuccessfull = false;

            jsonObjValidationMSG.put("ErrorType", "valueDateFallinWeekend");
            jsonObjValidationMSG.put("TradeNumber", tradeNumber);

            System.out.printf("valueDate:%s fall in Weekend\n", valueDate);
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
