package com.touraj.creditsuisse.kafkaproject;

import com.touraj.creditsuisse.kafkaproject.Validator.BeforeDateValidator;
import com.touraj.creditsuisse.kafkaproject.Validator.CustomerValidator;
import com.touraj.creditsuisse.kafkaproject.Validator.WeekendValidator;
import com.touraj.creditsuisse.kafkaproject.util.Utility;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

@RunWith(SpringRunner.class)
@SpringBootTest
public class KafkaCreditSuisseApplicationTests {

	@Test
	public void testBeforeDate() {

		String firstDate = "2016-08-11";
		String secondDate = "2016-08-18";

		boolean result = Utility.checkBeforeDate(firstDate, secondDate);

		Assert.isTrue(result);

	}

	@Test
	public void testIfDateFallInWeekend() {

		String date = "2017-06-11";

		boolean result = Utility.isDateFallinWeekend(date);

		Assert.isTrue(result);

	}

	@Test
	public void testIfCurrencyIsValidISO4217() {

		String currency = "USD";

		boolean result = Utility.isValidCurrencyISO4217(currency);

		Assert.isTrue(result);

	}

	@Test
	public void testBeforeDateValidator() {

		String tradeJson = "{\"customer\":\"PLUTO1\",\"ccyPair\":\"EURUSD\",\"type\":\"Spot\",\"direction\":\"BUY\",\"tradeDate\":\"2016-08-11\",\"amount1\":1000000.00,\"amount2\":1120000.00,\"rate\":1.12,\"valueDate\":\"2016-08-15\",\"legalEntity\":\"CS Zurich\",\"trader\":\"Johann Baumfiddler\"}, \n";

		JSONArray validationMessages = new JSONArray();
		BeforeDateValidator bdv = new BeforeDateValidator(validationMessages);

		try {
			JSONObject jo = new JSONObject(tradeJson);

			boolean result = bdv.processValidation(jo, 1);

			Assert.isTrue(result);

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testWeekendValidator() {

		String tradeJson = "{\"customer\":\"PLUTO1\",\"ccyPair\":\"EURUSD\",\"type\":\"Spot\",\"direction\":\"BUY\",\"tradeDate\":\"2016-08-11\",\"amount1\":1000000.00,\"amount2\":1120000.00,\"rate\":1.12,\"valueDate\":\"2016-08-15\",\"legalEntity\":\"CS Zurich\",\"trader\":\"Johann Baumfiddler\"}, \n";

		JSONArray validationMessages = new JSONArray();
		WeekendValidator wv = new WeekendValidator(validationMessages);

		boolean result = false;
		
		try {
			JSONObject jsonObject = new JSONObject(tradeJson);

			result = wv.processValidation(jsonObject, 1);


		} catch (JSONException e) {
			e.printStackTrace();
		}
			Assert.isTrue(result);

	}

	@Test
	public void testCustomerValidator() {

		String tradeJson = "{\"customer\":\"PLUTO1\",\"ccyPair\":\"EURUSD\",\"type\":\"Spot\",\"direction\":\"BUY\",\"tradeDate\":\"2016-08-11\",\"amount1\":1000000.00,\"amount2\":1120000.00,\"rate\":1.12,\"valueDate\":\"2016-08-15\",\"legalEntity\":\"CS Zurich\",\"trader\":\"Johann Baumfiddler\"}, \n";

		JSONArray validationMessages = new JSONArray();
		CustomerValidator cv = new CustomerValidator(validationMessages);
		boolean result = false;
		try {
			JSONObject jsonObject = new JSONObject(tradeJson);

			result = cv.processValidation(jsonObject, 1);


		} catch (JSONException e) {
			e.printStackTrace();
		}

		Assert.isTrue(result);

	}

	@Test
	public void testCustomerValidator2() {

		String tradeJson = "{\"customer\":\"Touraj\",\"ccyPair\":\"EURUSD\",\"type\":\"Spot\",\"direction\":\"BUY\",\"tradeDate\":\"2016-08-11\",\"amount1\":1000000.00,\"amount2\":1120000.00,\"rate\":1.12,\"valueDate\":\"2016-08-15\",\"legalEntity\":\"CS Zurich\",\"trader\":\"Johann Baumfiddler\"}, \n";

		JSONArray validationMessages = new JSONArray();
		CustomerValidator cv = new CustomerValidator(validationMessages);
		boolean result = false;
		try {
			JSONObject jsonObject = new JSONObject(tradeJson);

			result = cv.processValidation(jsonObject, 1);


		} catch (JSONException e) {
			e.printStackTrace();
		}
		System.out.println("Validation Message : "  + cv.getMessage());

		org.junit.Assert.assertEquals(false, result);

	}

}
