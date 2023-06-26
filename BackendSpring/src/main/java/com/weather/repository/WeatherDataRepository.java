package com.weather.repository;


import com.weather.model.WeatherDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// The WeatherDataRepository is an interface in the application that extends JpaRepository.
/** This interface is used to interact with the database.*/
@Repository // The @Repository annotation is used to indicate that the interface is a repository. Spring will automatically create a bean for this interface at startup.
public interface WeatherDataRepository extends JpaRepository<WeatherDataEntity, Long> {
    /** The WeatherDataRepository, has one method, findByCity, which is used to find a weather data entity by its city.*/
    // Spring Data JPA will automatically create an implementation for this method, so we don't need to implement it ourself.
    WeatherDataEntity findByCity(String city);
}