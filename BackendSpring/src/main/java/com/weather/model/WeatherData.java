package com.weather.model;

/** The WeatherData class is used as a data transfer object (DTO) to hold the weather data fetched from the OpenWeatherMap API.
 The WeatherData class has two fields, main and wind, which are instances of the inner classes Main and Wind.
These inner classes also represent the structure of the weather data.*/
public class WeatherData {
    private Main main;
    private Wind wind;

    public Wind getWind() {
        return wind;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }


    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    // The Wind inner class has one field named speed, which represents the wind speed.
    public static class Wind {

        private double speed;

        // getters and setters
        public double getSpeed() {
            return speed;
        }

        public void setSpeed(double speed) {
            this.speed = speed;
        }

    }

    // The Main inner class has two fields, temp and humidity, which represent the temperature and humidity respectively.
    public static class Main {

        private double temp;
        private double humidity;

        // getters and setters.
        public double getHumidity() {
            return humidity;
        }

        public void setHumidity(double humidity) {
            this.humidity = humidity;
        }

        public double getTemp() {
            return temp;
        }

        public void setTemp(double temp) {
            this.temp = temp;
        }

    }
}
