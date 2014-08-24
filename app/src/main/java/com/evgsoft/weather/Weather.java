package com.evgsoft.weather;

class Weather {
    String city;
    String day;
    String temperature;
    String weathrCondtns;

    Weather(String city, String day, String temperature, String weathrCondtns) {
        this.city = city;
        this.day = day;
        this.temperature = temperature;
        this.weathrCondtns = weathrCondtns;
    }

    @Override
    public String toString() {
        return "Weather{" +
                "city='" + city + '\'' +
                ", day=" + day +
                ", temperature='" + temperature + '\'' +
                ", weathrCondtns='" + weathrCondtns + '\'' +
                '}';
    }
}
