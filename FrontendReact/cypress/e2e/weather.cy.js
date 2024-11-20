// weather.cy.js

describe('WeatherChart', () => {
    it('Successfully loads weather data', () => {
        cy.visit('http://localhost:3000') // replace with your app url

        cy.intercept('GET', 'http://localhost:8080/weather/*').as('getWeather')

        cy.get('input[type="text"]').type('Odense')
        cy.get('button[type="submit"]').click()

        cy.wait('@getWeather').its('response.statusCode').should('eq', 200)

        cy.contains('Weather Data') // Check if Chart title is visible
    })
})
