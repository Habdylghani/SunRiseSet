# Sunrise and Sunset Time App

This Android app, developed using Kotlin and Android Studio. The app fetches data from the Sunrise-Sunset API and allows users to view this information in their preferred language.

## Features

- **Sunrise and Sunset Times:** The app retrieves and displays the current sunrise and sunset times for a predefined location (latitude 37.7749, longitude -122.4194).

- **Language Selection:** Users can choose their preferred language from a list of options provided in a Spinner. The app supports English, French, and Chinese languages.

  - 🇺🇸 English
  - 🇫🇷 Français
  - 🇨🇳 中文

## Code Overview

### Main Activity (`PlanetInfoActivity`)

- **UI Setup:** The activity sets up the user interface, including a Spinner for language selection and TextViews to display sunrise and sunset times.

- **Asynchronous Data Fetching:** The app asynchronously fetches sunrise and sunset times using Kotlin coroutines and the Sunrise-Sunset API.

- **Language Selection:** Users can select their preferred language using a Spinner. The selected language is used to update the app's UI components.

- **Time Localization:** The app localizes sunrise and sunset times based on the user's preferred language and 12/24-hour format.

### XML Layout (`activity_main.xml`)

- **Spinner:** Allows users to select their preferred language from the available options.

- **TextViews:** Display the localized sunrise and sunset times.

## Screenshots

![English](screenshot-1.png) | ![Français](screenshot-2.png) | ![中文](screenshot-3.png)



## Dependencies

- Kotlin Coroutines: Asynchronous programming for background tasks.
- Sunrise-Sunset API: Provides sunrise and sunset times based on location.

## Notes

- The app fetches sunrise and sunset times for the location (latitude 37.7749, longitude -122.4194).
