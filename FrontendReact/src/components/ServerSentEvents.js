import React, { useState, useEffect, useRef } from "react";
import axios from "axios";
import { Chart } from "react-google-charts";

function WeatherChart() {
    /**useState: The useState hook is used to create state variables in functional components.
    // Each state variable also has a corresponding setter function*/
    const [city, setCity] = useState("");
    const [temperature, setTemperature] = useState("");
    const [humidity, setHumidity] = useState("");
    const [windSpeed, setWindSpeed] = useState("");
    const [error, setError] = useState("");
    const [chartData, setChartData] = useState(null);
//useRef: The useRef hook is used to create a mutable ref object where .current is initialized to null.
// Here, source is a ref that's used to hold the instance of EventSource.
    const source = useRef(null);
    const reloadInterval = 5000; /** Time interval for auto-reloading in milliseconds, e.g. 60000 ms = 1 minute*/
   /**The useEffect hook is used to perform side effects in function components.
    The  useEffect hook is used to update chartData whenever city, temperature, humidity, or windSpeed changes.*/
    useEffect(() => {
        if (temperature)  {
            setChartData([
                ["Category", "Value", { role: "style" }],
                ["Temperature °C", temperature, "#ffea00"],
                ["Humidity %", humidity, "#8798eb"],
                ["Wind Speed m/s", windSpeed, "#eede90"],
            ]);
        }
//It also sets up an interval to automatically reload weather data every 5 seconds (or whatever is set in reloadInterval)*/
        const autoReload = setInterval(() => {
            if (city) {
                handleReload();
            }
        }, reloadInterval);

        return () => clearInterval(autoReload);
    }, [city, temperature, humidity,windSpeed]);
/**The  sets up an EventSource for server-sent events (SSE) from the server at the /subscribe endpoint.*/
    useEffect(() => {
        if (source.current !== null) {
            source.current.close();
        }
//EventSource (SSE): It uses the useEffect hook to set up an EventSource for server-sent events (SSE) from
// the server (Backend). This is done to receive real-time updates whenever weather data changes in backend.
        source.current = new EventSource(`http://localhost:8080/weather/subscribe`);
        source.current.onmessage = (event) => {
            const weatherData = JSON.parse(event.data);
            setTemperature(weatherData.temperature);
            setHumidity(weatherData.humidity);
            setWindSpeed(weatherData.windSpeed);
            console.log(weatherData); // do something with the SSE data
        };
        return () => {
            source.current.close();
        };
    }, []);
/**This async function makes a GET request to the /weather/{city} endpoint of the server using axios.
// It is used to get  weather data (the state variables temperature, humidity, and windSpeed)  based on the city .*/
    const fetchWeatherData = async () => {
        try {
            const response = await axios.get(`http://localhost:8080/weather/${city}`);
            //
            setTemperature(response.data.main.temp);
            setHumidity(response.data.main.humidity);
            setWindSpeed(response.data.wind.speed);
            setError("");
        } catch (error) {
            setTemperature("");
            setHumidity("");
            setWindSpeed("");
            setError("Error fetching weather data.");
        }
    };
/**handleReload: This async function makes a GET request to the /reload endpoint of the server using axios.
 It updates the state variables temperature, humidity, and windSpeed based on the response.
     It is used to update  weather data (the state variables temperature, humidity, and windSpeed)  based on the city .*/
    const handleReload = async () => {
        try {
            const response = await axios.get(`http://localhost:8080/weather/reload?city=${city}`);
            setTemperature(response.data.main.temp);
            setHumidity(response.data.main.humidity);
            setWindSpeed(response.data.wind.speed);
            setError("");
            // alert("Weather data reloaded successfully!");
        } catch (error) {
            setError("Error reloading weather data.");
        }
    };
    /**handleDelete: This async function makes a DELETE request to the /weather/{city} endpoint of the server using axios.
    it is used to delete the weather data for a specific city.*/
    const handleDelete = async () => {
        try {
            await axios.delete(`http://localhost:8080/weather/${city}`);
            setTemperature("");
            setHumidity("");
            setWindSpeed("");
            setChartData(null);
            setError("");
            alert("Weather data deleted successfully!");
        } catch (error) {
            setError("Error deleting weather data.");
        }
    };
//handleSubmit: This function is called when the form is submitted.
// It prevents the default form submission behavior and calls fetchWeatherData.
    const handleSubmit = (event) => {
        event.preventDefault();
        fetchWeatherData();
    };
    /**Rendering: The component returns a JSX that includes a form for the city input and buttons to get the weather,
    // delete weather data. It also includes a Chart component to display weather data and error messages if any.*/
    return (
        <div>
            <h1>Weather App</h1>

            <form onSubmit={handleSubmit}>
                <label>
                    City:
                    <input type="text" value={city} onChange={(event) => setCity(event.target.value)} />
                </label>
                <button type="submit">Get Weather</button>
            </form>

            <div>
                <button onClick={handleDelete} style={{ marginRight: "10px" }}>
                    Delete
                </button>
            </div>
            {error && <div style={{ color: "red" }}>{error}</div>}
            {chartData ? (
                <Chart
                    chartType="ColumnChart"
                    data={chartData}
                    options={{
                        title: " Weather Data",
                        legend: { position: "none" },
                        vAxis: {
                            title: "Value",
                        },
                        hAxis: {
                            title: "Category",
                        },
                    }}
                    width={"100%"}
                    height={"400px"}
                />
            ) : (
                <div style={{ color: "red" }}><b>No data available</b></div>
            )}
        </div>
    );
}
//The WeatherChart component is exported at the end, so it can be imported and used in other components.
export default WeatherChart;
