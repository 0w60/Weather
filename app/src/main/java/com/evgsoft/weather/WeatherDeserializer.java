package com.evgsoft.weather;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Date;

class WeatherDeserializer implements JsonDeserializer<Weather[]> {

    static final String[] DAYS = {
            "Monday",
            "Tuesday",
            "Wednesday",
            "Thursday",
            "Friday",
            "Saturday",
            "Sunday"};

    Weather[] weatherArray;

    @Override
    public Weather[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String city;
        String temperature;
        String weatherCondtns;
        JsonObject jObject = json.getAsJsonObject();
        city = jObject.get("city").getAsJsonObject().get("name").getAsString();
        JsonArray listJArray = jObject.get("list").getAsJsonArray();

        weatherArray = new Weather[listJArray.size()];
        int index = 0;
        for (JsonElement jElem : listJArray) {
            long dateInMilisecnds = jElem.getAsJsonObject().get("dt").getAsLong();
            int dayNumber = new Date(dateInMilisecnds *= 1000L).getDay();
            temperature = jElem.getAsJsonObject().get("temp").getAsJsonObject().get("day").getAsString();

            JsonArray weatherJArray = jElem.getAsJsonObject().get("weather").getAsJsonArray();
            weatherCondtns = weatherJArray.get(0).getAsJsonObject().get("description").getAsString();
            weatherArray[index++] = new Weather(city, DAYS[dayNumber], temperature, weatherCondtns);
        }
        return weatherArray;
    }
}
