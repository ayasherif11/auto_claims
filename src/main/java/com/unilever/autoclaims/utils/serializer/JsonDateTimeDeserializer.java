/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unilever.autoclaims.utils.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.unilever.autoclaims.exceptions.JsonParseException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Mahmoud.Elsaka
 */
public class JsonDateTimeDeserializer extends JsonDeserializer<Date> {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(JsonDateTimeDeserializer.class);

    @Override
    public Date deserialize(JsonParser jsonparser, DeserializationContext deserializationcontext) throws JsonProcessingException, IOException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd-HH:mm:ss");
        String dateString = jsonparser.getText().replaceAll("[^0-9\\.\\:\\-]", "");
        format.setLenient(false);
        int index = jsonparser.getParsingContext().getParent().getCurrentIndex();
        try {
            Date date = format.parse(dateString);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);

            if (cal.get(Calendar.YEAR) > 9999) {
                throw new JsonParseException("Error in Row Number "
                        + String.valueOf(index + 1)
                        + " (in screen) Or "
                        + String.valueOf(index + 2)
                        + " (in excel sheet) => " + "Invalid date in '" + jsonparser.getCurrentName() + "',Are you sure that you want to plan for year " + cal.get(Calendar.YEAR) + " ahead ? ");
            }

            return date;
        } catch (ParseException e) {
            LOGGER.error("ERROR: ", e);
            throw new JsonParseException("Error in Row Number "
                    + String.valueOf(index + 1)
                    + " (in screen) Or "
                    + String.valueOf(index + 2)
                    + " (in excel sheet) => " + "Invalid date format detected in '" + jsonparser.getCurrentName() + "', Date format for all values must be like this 'yyyy.MM.dd HH:mm:ss'. Example 2017.10.24 ");
        }
    }

}
