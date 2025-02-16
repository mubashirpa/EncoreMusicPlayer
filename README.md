# 🎵 Encore Music Player

**Encore** is a modern music player app built with Kotlin, featuring seamless Spotify API
integration and user authentication via Firebase (Google Sign-In). The app communicates with a Ktor
server to manage Spotify API data and leverages ExoPlayer (Media3) for high-quality music playback.
The UI is designed using Material3 for a clean, intuitive experience, and user playlists and recent
songs are stored locally using Room database.

## 🛠️ Features

- **Spotify Integration**: Fetch and play tracks, and playlists from Spotify using the Spotify API.
- **Firebase Authentication**: Secure login with Google Sign-In using Firebase Authentication.
- **ExoPlayer (Media3)**: Advanced music playback using ExoPlayer for smooth streaming.
- **Ktor Backend**: The app interacts with a Ktor server to handle Spotify API data fetching.
- **Room Database**: Stores user data such as playlists, recent songs, and favorites locally for
  offline access.
- **Material3 Design**: Implements Material3 UI components for a modern and responsive user
  interface.
- **Clean Architecture**: Follows the MVVM pattern with a clean architecture approach to ensure
  scalability and maintainability.

## 🚀 Tech Stack

### Frontend (Mobile App)

- **Kotlin**: The main programming language for Android development.
- **Media3 ExoPlayer**: Used for media playback and supports features like background playback,
  notifications, and MediaSession integration.
- **Firebase Authentication**: Google Sign-In via Firebase Authentication for secure user login.
- **Room Database**: For persisting user data such as playlists and recently played songs.
- **Material3**: Provides the UI components and theming system for a modern and consistent design.
- **Ktor Client**: Used for network communication with the Ktor server to fetch Spotify data.
- **Coroutines**: For managing asynchronous tasks and ensuring smooth performance.
- **Koin**: For dependency injection, allowing modular and testable architecture.
- **Kotlinx Serialization**: For handling JSON serialization and deserialization of API responses.

### Backend (Ktor Server)

- **Ktor**: A Kotlin-based framework to handle API requests from the Android app and communicate
  with the Spotify Web API.
- **Spotify Web API**: Fetches user playlists, track details, and playback controls.
- **Firebase Admin SDK**: Manages Firebase services (e.g., authentication) on the server.
- **Ktor Client**: Handles requests to the Spotify API from the server.
- **Environment Variables**: Stores sensitive data such as Spotify API keys and Firebase service
  account credentials.

## 🏗️ Architecture

### App Architecture

Encore follows the **MVVM** (Model-View-ViewModel) pattern with **Clean Architecture**, ensuring
separation of concerns and scalability:

- **ViewModel**: Responsible for handling UI-related logic and interacting with the Use Cases.
- **Use Cases**: Business logic and application-specific operations are encapsulated in use cases,
  separating the domain layer from the presentation layer.
- **Repository**: Interacts with data sources, including the Ktor server (via the Ktor Client) and
  the local Room database.
- **Data Sources**: Manages Spotify API requests through the Ktor Client and stores user data in the
  Room database.

### Backend Architecture

The Ktor server is the intermediary between the Encore mobile app and the Spotify API. It handles
API key management, token refresh, and data processing, allowing the mobile client to remain
lightweight. It also manages user authentication with Firebase Admin SDK.

## 🔧 Project Setup

### Prerequisites

1. **Spotify Developer Account**: Create a Spotify Developer account and set up an app to get your
   `CLIENT_ID` and `CLIENT_SECRET` for the Ktor server.
2. **Firebase Project**: Set up a Firebase project and enable Firebase Authentication with Google
   Sign-In.
3. **Android Studio**: Ensure you have the latest version of Android Studio installed.

### Clone the Repository

```bash
git clone https://github.com/mubashirpa/EncoreMusicPlayer.git
cd EncoreMusicPlayer
```

### Android App Configuration

1. **Firebase Setup**:

    - Download the `google-services.json` file from your Firebase project and place it in the `app/`
      directory.
    - Enable Google Sign-In in the Firebase Authentication section in your Firebase console.

2. **Room Database**:

    - Room is used to store user playlists, recently played songs, and favorites. It is
      pre-configured in the app for easy local data storage.

3. **Ktor Client Setup**:
    - The app communicates with a Ktor server for all Spotify-related operations using the Ktor
      Client. The base URL for the server is defined in the `BuildConfig` or environment variables.

### Ktor Server Configuration

1. **Spotify API Keys**:

    - Set up environment variables `ENCORE_SPOTIFY_CLIENT_ID` and `ENCORE_SPOTIFY_CLIENT_SECRET` for
      the Ktor server to handle Spotify API authentication.
    - These can be stored in the environment configuration of your server.

   Example:

   ```bash
   ENCORE_SPOTIFY_CLIENT_ID="your-client-id"
   ENCORE_SPOTIFY_CLIENT_SECRET="your-client-secret"
   ```

2. **Firebase Admin Setup**:

    - Download the Firebase Admin SDK service account key (
      `service-account.json`) from your Firebase project.
    - Place this file in the server's etc/secrets directory.

3. **Run the Ktor Server**:

    - After configuring the environment variables and Firebase Admin SDK, you can start the Ktor
      server:

   ```bash
   ./gradlew run
   ```

4. The Ktor server will handle API requests, manage Spotify tokens, and interact with Firebase for
   user authentication.

## 🎨 UI/UX with Material3

The app's user interface follows **Material3** guidelines for a consistent, modern, and intuitive
design:

- **Material3 Components**: Buttons, Text Fields, Cards, and Navigation are based on Material3
  guidelines.
- **Dynamic Theming**: The app supports both light and dark themes, providing a responsive and
  visually appealing experience.

## 🤝 Contributions

Contributions are welcome! If you'd like to contribute to **Encore**, feel free to submit a pull
request or open an issue on GitHub to discuss changes or report bugs.

## 📄 License

This project is licensed under the **GNU Affero General Public License v3.0**. You can find the full
license text in the [LICENSE](LICENSE) file.
