package com.touraj.creditsuisse.kafkaproject.Validator;

import org.json.JSONArray;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by toraj on 06/09/2017.
 */
public class ChainofValidators {

    List<IValidator> validatorList = new LinkedList<>();
    JSONArray validationMessages;

    JSONArray jsonArr;

    public ChainofValidators(JSONArray validationMessages, JSONArray jsonArr) {
        this.validationMessages = validationMessages;
        this.jsonArr = jsonArr;
        initValidators();
    }

    public void initValidators()
    {
        BeforeDateValidator bdv = new BeforeDateValidator(validationMessages);
        WeekendValidator wv = new WeekendValidator(validationMessages);
        ISO4217Validator iso = new ISO4217Validator(validationMessages);
        CustomerValidator cuv = new CustomerValidator(validationMessages);
        StyleValidator sv = new StyleValidator(validationMessages);
        ExcerciseStartDateValidator esdv = new ExcerciseStartDateValidator(validationMessages);
        ExpiryAndPrimiumDateValidator eapdv = new ExpiryAndPrimiumDateValidator(validationMessages);

        validatorList.add(bdv);
        validatorList.add(wv);
        validatorList.add(iso);
        validatorList.add(cuv);
        validatorList.add(sv);
        validatorList.add(esdv);
        validatorList.add(eapdv);

    }

    public void executeChain()
    {
        for (int i = 0; i <jsonArr.length() ; i++) {

            for (IValidator iValidator : validatorList) {
                iValidator.processValidation(jsonArr.getJSONObject(i), i+1);

            }
        }
    }
}