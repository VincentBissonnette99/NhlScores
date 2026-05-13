# NHL Live Scores

NHL Live Scores is a beautifully designed Android application that provides real-time score updates for National Hockey League games. The app features a modern Material 3 design with NHL-themed colors, smooth animations, and an intuitive user interface.

## Features

- **Real-time NHL Scores**: Displays all NHL games scheduled for the current day with live score updates
- **Beautiful UI**: Modern Material 3 design with NHL-themed red and blue color scheme
- **Game Details**: Tap on any game to see detailed information including goals, assists, and shots on goal
- **Date Navigation**: Easily navigate between different days to see past or future games
- **Pull to Refresh**: Swipe down to manually refresh scores
- **Live Game Indicators**: Clear visual indicators for live, final, and scheduled games
- **Responsive Design**: Works beautifully on all Android device sizes

## Technologies Used

- **Kotlin** - Modern Android development
- **Jetpack Compose** - Declarative UI framework
- **Material 3** - Latest Material Design components
- **MVVM Architecture** - Clean separation of concerns
- **Retrofit & OkHttp** - Robust networking
- **Moshi** - JSON serialization
- **Coroutines & Flow** - Asynchronous programming
- **Accompanist** - Additional Compose components

## API Source

The app uses public NHL data endpoints provided by the official NHL API. No API key or authentication is required.

Example endpoint: `https://api-web.nhle.com/v1/score/now`

## Project Structure

```
/data
    /remote
        HttpClient.kt          # Network client setup
        NHLApi.kt             # API service interface
        DTO models and mapping functions

/model
    Game.kt                  # Domain models
    GameDetail.kt

/ui
    TodayScreen.kt           # Main games list screen
    GameDetailScreen.kt      # Individual game details
    theme/                   # Material 3 theming

/viewmodel
    TodayViewModel.kt        # Main screen logic
    GameDetailViewModel.kt   # Game detail logic
```

## Screenshots

_(Add screenshots here showing the beautiful UI)_

## Getting Started

1. Clone the repository
2. Open in Android Studio
3. Build and run on device or emulator
4. Enjoy live NHL scores!

## Contributing

Contributions are welcome! Please feel free to submit pull requests or open issues for bugs and feature requests.

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

```
