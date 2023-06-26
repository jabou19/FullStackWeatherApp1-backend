package com.weather.controller;

import com.weather.model.WeatherData;
import com.weather.service.WeatherService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


/**We have created a RESTful API with the following endpoints:
 GET /weather/{city}: Get weather data for a city.
 POST /weather/{city}: Add weather data for a city.
 GET /weather/reload: Reload weather data for a city.
 PUT /weather/{city}: Update weather data for a city.
 DELETE /weather/{city}: Delete weather data for a city.*/

/** WeatherController class in our code defines a RESTful API with the endpoints that you can see in the codebase.*/
/** The @RequestMapping annotation at the class level is used to map web requests onto specific handler classes and/or handler methods.*/
// In this case, it maps all requests starting with /weather to this controller.
/**The @CrossOrigin annotation at the class level allows requests from the specified origins,
// in this case, "http://localhost:3000/". This is necessary for cross-origin resource sharing (CORS) when our API is accessed*/
// from a different domain, which is a common scenario in web development.
@RestController
@RequestMapping("/weather")
@CrossOrigin(origins = "http://localhost:3000")
public class WeatherController {

    // An instance of the WeatherService class.
    private final WeatherService weatherService;

    // The Constructor.
    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    // Handles a GET request and returns an SSE (Server-Sent Event) emitter.
    // Clients can subscribe to receive weather updates through this endpoint.
    @GetMapping("/subscribe")
    public SseEmitter subscribe() {
        return weatherService.register();
    }

    //  Handles a GET request and retrieves weather data for the specified city
    @GetMapping("/{city}")
    public WeatherData getWeatherData(@PathVariable String city) {
        return weatherService.getWeatherData(city);
    }

    // Handles a POST request and adds weather data for the specified city.
    @PostMapping("/{city}")
    public WeatherData addWeatherData(@PathVariable String city) {
        return weatherService.addWeatherData(city);
    }

    // Handles a GET request and reloads weather data for the specified city.
    @GetMapping("/reload")
    public ResponseEntity<WeatherData> reload(@RequestParam String city) {
        WeatherData updatedWeatherData = weatherService.reloadWeatherData(city);
        if (updatedWeatherData == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(updatedWeatherData);
    }

    // Handles a PUT request and updates weather data for the specified city.
    @PutMapping("/{city}")
    public WeatherData updateWeatherData(@PathVariable String city) {
        return weatherService.updateWeatherData(city);
    }

    // Handles a DELETE request and deletes weather data for the specified city.
    // Returns a ResponseEntity with a 204 status code indicating successful deletion.
    @DeleteMapping("/{city}")
    public ResponseEntity<Void> deleteWeatherData(@PathVariable String city) {
        weatherService.deleteWeatherData(city);
        return ResponseEntity.noContent().build();
    }


}