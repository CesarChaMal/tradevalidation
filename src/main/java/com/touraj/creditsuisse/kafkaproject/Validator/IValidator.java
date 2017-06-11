package com.touraj.creditsuisse.kafkaproject.Validator;

import org.json.JSONObject;

/**
 * Created by toraj on 06/08/2017.
 */
public interface IValidator {

    public boolean processValidation(JSONObject jsonObj, int tradeNumber);
    public String getMessage();
    public void setMessage(String message);

}
