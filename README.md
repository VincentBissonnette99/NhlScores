# NHL Live Scores

NHL Live Scores is an Android application that provides real time score updates for National Hockey League games. The app uses the public NHL stats API to display the games of the current day, including team names, live status, scores, and period information. Users can refresh the scores manually with a simple swipe gesture.

## Features

- Displays all NHL games scheduled for the current day
- Live score updates including:
  - Current period
  - Time remaining
  - Final status when the game ends
- Pull to refresh to manually update scores
- Works on physical Android devices

## Technologies Used

- Kotlin
- Jetpack Compose
- MVVM Architecture (ViewModel, StateFlow)
- Retrofit and OkHttp for networking
- Moshi for JSON serialization
- Coroutines for asynchronous operations

## API Source

The app uses public NHL data endpoints. Example:
```
https://api-web.nhle.com/v1/score/now
```

No API key or authentication is required to use these public endpoints.

## Project Structure

```
/data
    /remote
        HttpClient.kt
        NHLApi.kt
        DTO models and mapping functions

/model
    Game.kt
    GameStatus.kt

/ui
    TodayScreen.kt

/viewmodel
    TodayViewModel.kt
```

## Requirements

- Android Studio Ladybug or newer
- Android 8.0 (API level 26) or higher

## Installation

1. Clone the repository or download the project files
2. Open the project in Android Studio
3. Build and run the app on a physical Android device or emulator

You can also generate an APK:

```
Build > Build Bundle(s) / APK(s) > Build APK(s)
```

Then install the generated APK on your device.

## Future Improvements

- Add a detailed game screen with stats, lineups, or scoring plays
- Favorite teams and notifications
- Dark mode and improved UI design
- Scheduled background refresh

## License

This project is for educational and portfolio purposes. It is not affiliated with or endorsed by the NHL.

