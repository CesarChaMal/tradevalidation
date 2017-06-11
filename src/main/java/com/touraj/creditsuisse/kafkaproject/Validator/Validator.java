package com.touraj.creditsuisse.kafkaproject.Validator;

import org.json.JSONArray;

/**
 * Created by toraj on 06/09/2017.
 */
public class Validator {

    JSONArray validationMessages;

    public Validator(JSONArray validationMessages) {
        this.validationMessages = validationMessages;
    }

    public void startValidation(String jsonArray) {

        JSONArray jsonArr = new JSONArray(jsonArray);
        ChainofValidators cv = new ChainofValidators(validationMessages, jsonArr);

        cv.executeChain();
    }
}
