package com.weather.service;

import com.weather.model.*;
import com.weather.repository.WeatherDataRepository;;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

import java.util.concurrent.CopyOnWriteArrayList;

/** The WeatherService class is an integral part of the service layer in our application.*/
/**It contains business logic to handle operations related to weather data.
 The @Service annotation is used to indicate that the class is a service, which means it holds business logic.*/
// It's a specialization of the @Component annotation, allowing for automatic discovery and wiring.
@Service
public class WeatherService {

    // The API key
    private static final String API_KEY = "b9b867c8f3d45a939791626442db7773";

    // The URL for the API
    private static final String API_URL = "http://api.openweathermap.org/data/2.5/weather?q={city}&units=metric&appid="+API_KEY;

    /**An instance of RestTemplate used for making REST API calls to retrieve weather data.*/
    private final RestTemplate restTemplate;

    /** An instance of WeatherDataRepository used for accessing and manipulating weather data entities in the repository.*/
    private final WeatherDataRepository weatherDataRepository;


    /** A collection that stores instances of SseEmitter for registered clients.
     It allows broadcasting weather data events to all registered clients.*/
    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    // The constructor method.
    public WeatherService(RestTemplate restTemplate, WeatherDataRepository weatherDataRepository) {
        this.restTemplate = restTemplate;
        this.weatherDataRepository = weatherDataRepository;
    }

    // Registers a new SseEmitter and adds it to the collection of emitters.
    /**register(): This method is used to register a new SseEmitter (Server Sent Events Emitter).
     *  emitter is added to the list of emitters and returns it. The onCompletion() method removes
     *  the emitter from the list when it completes.*/
    public SseEmitter register() {
        SseEmitter sseEmitter = new SseEmitter();
        emitters.add(sseEmitter);
        sseEmitter.onCompletion(() -> emitters.remove(sseEmitter));
        return sseEmitter;
    }

    // Publishes a weather data event to all registered SseEmitter instances, and sends the weather data entity
    // as a server-sent event to each emitter.
    /**publishEvent():  This method is used to publish an event to all registered clients.
     *  It iterates through all SseEmitter instances in emitters and sends the weatherDataEntity to each of them..*/
    private void publishEvent(WeatherDataEntity weatherDataEntity) {
        emitters.forEach(emitter -> {
            try {
                emitter.send(weatherDataEntity, MediaType.APPLICATION_JSON);
            } catch (IOException e) {
                emitter.complete();
                emitters.remove(emitter);
            }
        });
    }

    // Retrieves weather data for a specific city by making a REST API call.
    // Saves the retrieved data as a WeatherDataEntity in the repository.
    // Publishes the event to update the registered clients.
    public WeatherData getWeatherData(String city) {
        // Retrieves weather data for a specific city by making a REST API call.
        WeatherData weatherData = restTemplate.getForObject(API_URL, WeatherData.class, city, API_KEY);
        WeatherDataEntity weatherDataEntity = new WeatherDataEntity(city, weatherData.getMain().getTemp(),weatherData.getMain().getHumidity(),weatherData.getWind().getSpeed());
        // Saves the retrieved data as a WeatherDataEntity in the repository and then to the database.
        weatherDataRepository.save(weatherDataEntity);
        System.out.println("Get Weather data with city "+weatherDataEntity.getCity()+" that have, Temperature: "+weatherData.getMain().getTemp()+", Humidity: "+weatherData.getMain().getHumidity()+", and Speed of wind: "+weatherData.getWind().getSpeed());
        // Publishes the event to update the registered clients.
        publishEvent(weatherDataEntity);
        return weatherData;
    }

    /**reloadWeatherData(): This method is scheduled to run at a fixed rate to update the weather data for
     * a specific city. It fetches the new data from the OpenWeatherMap API, updates the database, and publishes an event.*/
    @Scheduled(fixedRate = 5000) //runs every five Second
    public WeatherData reloadWeatherData(String city) {
        //It fetches the new data from the OpenWeatherMap API,to update the weather data for a specific city
        WeatherData weatherData = restTemplate.getForObject(API_URL, WeatherData.class, city, API_KEY);
        WeatherDataEntity weatherDataEntity = weatherDataRepository.findByCity(city);
        if (weatherDataEntity == null) {
            return null;
        }
        weatherDataEntity.setTemperature(weatherData.getMain().getTemp());
        weatherDataEntity.setHumidity(weatherData.getMain().getHumidity());
        weatherDataEntity.setWindSpeed(weatherData.getWind().getSpeed());
        //updates and saves in  the database
        weatherDataRepository.save(weatherDataEntity);
        System.out.println(" Weather data with city  "+ weatherDataEntity.getCity()+ " is updated that have, Temperature: " +
                ""+ weatherData.getMain().getTemp() +", Humidity: " + weatherData.getMain().getHumidity()+", and Speed of wind: " +
                weatherData.getWind().getSpeed());
        // Publishes the event to update the registered clients.
        publishEvent(weatherDataEntity);
        return weatherData;
    }
    /** not used*/
    // Retrieves weather data for a specific city, creates a new WeatherDataEntity object, saves it in the repository.
    public WeatherData addWeatherData(String city) {
        WeatherData weatherData = restTemplate.getForObject(API_URL, WeatherData.class, city, API_KEY);
        WeatherDataEntity weatherDataEntity = new WeatherDataEntity(city, weatherData.getMain().getTemp(),
                                                  weatherData.getMain().getHumidity(),weatherData.getWind().getSpeed());
        weatherDataRepository.save(weatherDataEntity);
        publishEvent(weatherDataEntity);
        return weatherData;
    }
/** not used*/
    // Retrieves weather data for a specific city, updates the corresponding WeatherDataEntity in the repository.
    @Scheduled(fixedRate = 5000) // runs every few Second
    public WeatherData updateWeatherData(String city) {
        WeatherData weatherData = restTemplate.getForObject(API_URL, WeatherData.class, city, API_KEY);
        WeatherDataEntity weatherDataEntity = weatherDataRepository.findByCity(city);
        if (weatherDataEntity == null) {
            throw new IllegalArgumentException("City not found in the database: " + city);
        }
        weatherDataEntity.setTemperature(weatherData.getMain().getTemp());
        weatherDataEntity.setHumidity(weatherData.getMain().getHumidity());
        weatherDataEntity.setWindSpeed(weatherData.getWind().getSpeed());
        weatherDataRepository.save(weatherDataEntity);
        System.out.println(" Weather data with city "+weatherDataEntity.getCity()+" is updated that have, Temperature: "+weatherData.getMain().getTemp()+", Humidity: "+weatherData.getMain().getHumidity()+", and Speed of wind: "+weatherData.getWind().getSpeed());
        publishEvent(weatherDataEntity);
        return weatherData;
    }


    /**deleteWeatherData(): This method deletes weather data for a city from the database and publishes an event.*/
    /** Deletes weather data for a specific city from the repository if it exists.*/
    public void deleteWeatherData(String city) {
        WeatherDataEntity weatherDataEntity = weatherDataRepository.findByCity(city);
        if (weatherDataEntity == null) {
            return;
        }
        weatherDataRepository.delete(weatherDataEntity);
        System.out.println(" Weather data with city "+weatherDataEntity.getCity()+ " is deleted");
        // Publish the event
        publishEvent(weatherDataEntity);
    }
}