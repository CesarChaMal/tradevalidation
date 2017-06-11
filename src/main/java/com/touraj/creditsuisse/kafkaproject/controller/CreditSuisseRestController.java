package com.touraj.creditsuisse.kafkaproject.controller;

import com.touraj.creditsuisse.kafkaproject.Validator.Validator;
import org.json.JSONArray;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by toraj on 06/08/2017.
 */
@RestController
public class CreditSuisseRestController {

    /**
     * @param tradeJSON consumes a JSON array including tardes information
     * @return validate trades information and returns validation results to the client
     * @throws Exception
     */
    @RequestMapping(
            value = "/validatetrades",
            method = RequestMethod.POST,
            consumes = "text/plain")
    public String validatetrades(@RequestBody String tradeJSON) throws Exception {

        System.out.println("In validatetrades method...");

        JSONArray validationMessages = new JSONArray();

        Validator validator = new Validator(validationMessages);
        validator.startValidation(tradeJSON);

        if (validationMessages.length() == 0) {
            System.out.println("Validation Successful :: No error found in trade data");
            return "Validation Successful :: No error found in trade data";
        } else {

        return validationMessages.toString();
        }
    }
}
