# My Notes
My Notes is a sample Android application project intended to display my programming capabilities and knowledge. It doesn't cover all Android basics as such application would have to have tons of activities and modules. Instead it displays my capabilities to:
* Use latest cutting edge architectures, libraries and patterns
* Write code that is clean, readable and testable
* Create applications and components that are reusable and easily scalable
## Overview
My Notes is a simple notes management application. In version `1.0` it covers:
* Adding a new note
* Editing existing note
* Deleting note with _swipe-to-side_ gesture
* Refreshing notes by _swipe-to-refresh_ gesture

Data is received from mock endpoint hosted in Apiary.
## Limitations
Mock endpoint in Apiary doesn't actually support any active modification of notes. It always returns same data no matter what. Which means that if user removes any of mocked notes retrieved from backend and refreshes data he retrieves them back.
## Language, architecture and patterns
#### Kotlin
This application is written completely in **Kotlin** language.
It uses Kotlin **coroutines** for orchestrating asynchronous operations. Coroutines rapidly replace **RxJava** that used to be an industry standard for this purpose.
#### MVVM
Google's **Model-View-ViewModel** pattern is used for separating UI code from business logic and data layer.
#### LiveData
Google's **LiveData** architecture is used in conjunction with **Room Database** and **Android Architecture Components** for local storage and caching of the data and interoperation between View and ViewModel.
#### AndroidX
Android backward compatibility package that brings features of the latest Android versions to older ones with little or no effort.
#### Material Design
Material design is an industry standard for all modern Android applications. It features cleanliness and best user experience through UI patterns and standardised gestures (swipe-to-refresh, swipe-to-dismiss).
#### Dagger
Dagger is a state of the art dependency injection framework. Dependency injection is a must for a robust, scalable and easily testable application.
## Libraries
Aside from above mentioned components this application uses also following libraries that are also considered a standard today:
#### Retrofit
A type-safe HTTP client framework for Android (and Java). It makes creating a REST API client a breeze.
#### GSON
A **JSON** to Java (Kotlin) type conversion library. Very handy for API request/response data representation.
## Unit tests
All business logic is thoroughly unit tested. This ensures that not only the current code does what's expected from it, but also ensures that any future modification of the code doesn't break its expected functionality and the contract the business object has with its clients.
# Download, compile, test
Clone this repository to a dedicated directory, open **Android Studio**, pick `Import project` option and navigate to the directory. Then, use Android Studio tools to build and deploy the application onto your device or emulator.
Or use **Gradle** to build, test and deploy:
### Build project
To build project, navigate to the project directory and use following command:
`./gradlew assembleDebug`
to compile the project and build APK. This step produces and APK file in following project folder: `/app/build/outputs/apk/debug/app-debug.apk`
To rebuild the project completely (in case there are invalid cached artifacts that can sometimes break whole compile process) use following command:
`./gradlew clean assembleDebug --rerun-tasks`
### Run unit tests
To run all unit tests within the project use following command:
`./gradlew testDebug`
### Deploy
To deploy the application to a device or emulator first find the device within connected devices using **ADB**:
`adb devices`
When the device is connected it should be visible in the list of connected devices. Then you can install the APK on the device using this command:
`adb install ./app/build/outputs/apk/debug/app-debug.apk`
