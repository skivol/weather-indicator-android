# Temperature/humidity indicator application for Android. Version 2

## Purpose of the project
1. Create a simple mobile client for home weather (temperature/humidity) indicator;
2. Checkout new technologies/approaches for building Android applications (see "Technologies/libraries" section).

## Screenshots
[available here](https://github.com/vania92/weather-indicator-android/wiki)

## Functionality:
 * simple gui that, while active, periodically (once in a minute, currently configurable) reloads the data from network temperature/humidity indicator;
 * home screen widget that shows temperature & humidity along with the date of loading and has a button for manual refresh;
 * settings screen with measurements url, update period configuration and "about" section;
 * English and Ukrainian translations;
 * couple of instrumentation tests with Espresso and MockWebServer.

## Technologies/libraries used:
 * Platform: Android (27)
 * Language: Kotlin (1.2.31)
 * Dependency injection: Dagger 2
 * Network: OkHttp
 * Utility: Anko (0.10.4)
 * Testing: Espresso / MockWebServer

 ## Limitations
 1. Could be not idiomatic Kotlin in some places.
 2. The libraries are, most likely, not used to their fullest potential.