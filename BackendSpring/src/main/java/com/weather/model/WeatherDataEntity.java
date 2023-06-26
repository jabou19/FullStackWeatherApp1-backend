package com.weather.model;


import javax.persistence.*;

/** This model class represents weather data entities with properties such as city, temperature, humidity, and wind speed,
 allowing storage and manipulation of weather-related information in an application.*/
@Entity // The @Entity tells JPA that instances of this class will be automatically mapped to rows in a database table.
public class WeatherDataEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id",unique = true)
    private Long id;
    @Column(name = "city",nullable = false)
    private String city;
    @Column(name = "temperature")
    private double temperature;
    @Column(name = "humidity")
    private double humidity;
    @Column(name = "windSpeed")
    private double windSpeed;

    // Constructor
    public WeatherDataEntity() {

    }

    // Constructor
    public WeatherDataEntity(String city, Double temperature, Double humidity, double windSpeed) {
        this.city = city;
        this.temperature = temperature;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
    }


    // All of the following methods are the getter and setter methods
    // which are used to retrieve and change the values of these fields.
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double wind) {
        this.windSpeed = wind;
    }


}
