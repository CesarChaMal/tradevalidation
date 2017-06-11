package com.touraj.creditsuisse.kafkaproject.Validator;

import com.touraj.creditsuisse.kafkaproject.util.Utility;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by toraj on 06/08/2017.
 */
public class BeforeDateValidator implements IValidator {

    private String message = null;
    JSONArray validationMessages;

    public BeforeDateValidator(JSONArray validationMessages) {
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
        String tradeDate = (String) jsonObj.get("tradeDate");

        boolean res = Utility.checkBeforeDate(valueDate, tradeDate);

        JSONObject jsonObjValidationMSG = new JSONObject();
        if (res) {
            isValidationSuccessfull = false;

            jsonObjValidationMSG.put("ErrorType", "valueDateNotbeforeTradeDate");
            jsonObjValidationMSG.put("TradeNumber", tradeNumber);

            System.out.printf("valueDate:%s is Before tradeDate:%s\n", valueDate, tradeDate);
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