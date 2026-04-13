# FlightSearchApp (Android)

An Android flight search app built with Kotlin, Jetpack Compose, and Room.

## Features

- Search airports by name, city, or IATA code
- View possible routes from a selected departure airport
- Save and remove favorite routes
- Local persistence using Room database

## Tech Stack

- Kotlin
- Jetpack Compose
- Android Architecture Components (ViewModel, StateFlow)
- Room (SQLite)
- Gradle Kotlin DSL

## Project Structure

- `app/src/main/java/com/example/flightsearch/data`
  - Local database entities and DAO
  - Repository interfaces and implementations
- `app/src/main/java/com/example/flightsearch/ui`
  - UI screens, state models, and view models

## Getting Started

### Prerequisites

- Android Studio (latest stable recommended)
- JDK 17 or version configured by your Android Studio setup

### Run the App

1. Clone the repository:
   - `git clone https://github.com/FREDPENTA5/flightSearchApp-andrioid.git`
2. Open the project in Android Studio.
3. Let Gradle sync finish.
4. Run the app on an emulator or physical device.

## Build from Command Line

- Windows:
  - `gradlew.bat assembleDebug`
- macOS/Linux:
  - `./gradlew assembleDebug`

## Future Improvements

- Add UI/instrumentation tests
- Add loading and error states for all data flows
- Improve search ranking and filtering experience