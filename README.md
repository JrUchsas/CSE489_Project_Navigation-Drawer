# Navigation Drawer Android Project

This is a native Android application built with Kotlin that demonstrates the use of a Navigation Drawer for navigating between different sections of the app. The project follows a modern, single-activity architecture using Fragments for each screen and implements the MVVM (Model-View-ViewModel) design pattern.

## Features

*   **Navigation Drawer**: A slide-in menu for easy navigation between different screens.
*   **Single-Activity Architecture**: A modern Android app structure with one main activity hosting multiple fragments.
*   **Fragments**: Each screen in the app is implemented as a self-contained Fragment.
*   **MVVM Architecture**: The project follows the Model-View-ViewModel pattern, separating UI from business logic for better maintainability and testability.
*   **View Binding**: Safely interacts with UI elements, preventing null pointer exceptions.
*   **Navigation Component**: Manages all in-app navigation, including fragment transactions and app bar titles.
*   **Material Design**: Utilizes Material Design components for a modern and consistent user interface.

## Technologies Used

*   **Kotlin**: The primary programming language for the project.
*   **Android Jetpack**:
    *   **View Binding**: For safe and concise interaction with UI elements.
    *   **Lifecycle (ViewModel & LiveData)**: To manage UI-related data in a lifecycle-conscious way.
    *   **Navigation Component**: To handle all in-app navigation.
*   **Material Design**: For UI components and styling.

## Getting Started

To get a local copy up and running, follow these simple steps.

### Prerequisites

*   Android Studio installed on your machine.
*   An Android device or emulator to run the app.

### Installation

1.  Clone the repo
    ```sh
    git clone https://github.com/your_username_/NavigationDrawer.git
    ```
2.  Open the project in Android Studio.
3.  Build the project to download the necessary dependencies.
4.  Run the app on an Android device or emulator.

## Project Structure

The project is organized into the following key directories and files:

*   `app/src/main/java/com/example/navigationdrawer`: Contains the Kotlin source code for the application.
    *   `MainActivity.kt`: The single activity that hosts all the fragments.
    *   `ui/`: A directory for each screen's Fragment and ViewModel (e.g., `HomeFragment.kt`, `HomeViewModel.kt`).
*   `app/src/main/res`: Contains all the resources for the application.
    *   `layout/`: XML layout files for the activity and fragments.
    *   `menu/`: XML file defining the items in the navigation drawer.
    *   `navigation/`: The navigation graph (`mobile_navigation.xml`) that defines the app's navigation flow.
*   `app/build.gradle.kts`: The build script for the app module, where all the dependencies are defined.

## MVVM Architecture

The project follows the MVVM (Model-View-ViewModel) architecture to separate the UI from the business logic.

*   **Model**: Represents the data and business logic of the application. In this project, the "Model" is implicitly represented by the data sources that the ViewModels would interact with (though in this simple example, the data is often generated within the ViewModel itself).
*   **View**: The UI of the application, which is represented by the Fragments. The Views are responsible for displaying the data and sending user actions to the ViewModel.
*   **ViewModel**: Acts as a bridge between the Model and the View. It holds the UI-related data and exposes it to the View through LiveData. The ViewModel is not aware of the View, which makes it highly testable.

This separation of concerns makes the code more modular, scalable, and easier to maintain.
