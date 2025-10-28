# YourCookbok - Recipe Finder App

YourCookbok is a modern, offline-first Android application built to showcase a robust, scalable, and testable architecture. It allows users to search for recipes, view their details, and maintain a local collection of their favorites.

This project was developed as a technical interview showcase, prioritizing architectural quality, defensibility, and adherence to modern Android development best practices over a wide feature set.

## Features

- **Search:** Find recipes from TheMealDB API with a reactive, debounced search field.
- **Offline-First:** Search results are seamlessly supplemented with locally saved recipes when the network is unavailable.
- **Favorites:** Save and remove recipes to a local "favorites" list that is available offline.
- **Recipe Details:** View comprehensive recipe details, including ingredients, measurements, and step-by-step instructions.
- **Modern UI:** A clean, responsive, and intuitive user interface built entirely with Jetpack Compose and Material 3.

## Technology Stack

This project leverages a modern, industry-standard technology stack:

- **UI:** 100% [Jetpack Compose](https://developer.android.com/jetpack/compose) for declarative UI development.
- **Architecture:** Clean Architecture + MVVM (Model-View-ViewModel).
- **Dependency Injection:** [Hilt](https://dagger.dev/hilt/) for managing dependencies.
- **Asynchronicity:** [Kotlin Coroutines & Flow](https://kotlinlang.org/docs/coroutines-guide.html) for all asynchronous operations.
- **Networking:** [Retrofit](https://square.github.io/retrofit/) for consuming TheMealDB API, with [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization) for JSON parsing.
- **Local Database:** [Room](https://developer.android.com/training/data-storage/room) for persisting favorite recipes.
- **Navigation:** [Jetpack Compose Navigation](https://developer.android.com/jetpack/compose/navigation) with a type-safe, serializable routing system.
- **Image Loading:** [Coil](https://coil-kt.github.io/coil/) for efficient image loading in Compose.
- **Unit Testing:** [JUnit5](https://junit.org/junit5/), [MockK](https://mockk.io/) (for mocking), and [Strikt](https://strikt.io/) (for assertions).
- **UI Testing:** Jetpack Compose Test Suite (`createAndroidComposeRule`).

## Architectural Highlights & Decisions

The architecture of this application was designed to be clean, modular, and highly defensible. The key decisions are documented in the `ADR.md` file, with the most important highlights summarized below.

### 1. Clean Architecture with MVVM

The project follows a strict Clean Architecture pattern, separating the code into three primary layers:

- **Domain:** Contains the core business models (`Recipe.kt`) the `RecipeRepository` interface and UseCases that are shared across features (`GetSavedRecipesUseCase`,`DeleteRecipeUseCase`, `IsRecipeSavedUseCase`, `SaveRecipeUseCase`). This layer is pure Kotlin and has no external dependencies.
- **Data:** Implements the `RecipeRepository` interface. It is responsible for all data operations, including network requests (`Retrofit`) and local database access (`Room`). It uses the DTO (Data Transfer Object) pattern to map API responses to our clean domain models.
- **Presentation (UI):** Built with Jetpack Compose, this layer consists of ViewModels and Composable screens. ViewModels consume UseCases from the domain layer to drive the UI state.

### 2. Package-by-Feature Structure

To enhance modularity and scalability, the project uses a "package-by-feature" structure (e.g., `/search`, `/saved`, `/detail`). This keeps all code related to a single feature (UI, ViewModel, UseCases) in one place, making the codebase easier to navigate and maintain.

### 3. Unidirectional Data Flow (UDF) in the UI

The UI layer strictly follows MVI-inspired principles:
- **Single Source of Truth:** The ViewModel is the sole owner of the screen's state, exposed as a `StateFlow<UiState>`.
- **State Down, Events Up:** The UI observes this state and sends all user actions to the ViewModel via a sealed `Event` class, creating a predictable, unidirectional data flow.
- **Stateless Composables:** All screens are implemented with a stateful "Route" composable (which connects to the ViewModel) and a stateless "Screen" composable (which only renders the UI). This makes the UI components highly testable and previewable.

### 4. Offline-First Repository

The `RecipeRepository` is designed to be offline-first. When a user searches for a recipe, the repository first attempts to fetch fresh data from the network. If the network call fails, it automatically falls back to searching the local database of saved recipes, providing a seamless and resilient user experience.

### 5. Type-Safe, Encapsulated Navigation

Navigation is implemented using a fully type-safe system built on `kotlinx.serialization`.
- **Routes as Objects:** Navigation destinations are defined as `@Serializable sealed class` objects, not strings, which prevents runtime errors from typos or incorrect arguments.
- **Encapsulation:** All navigation logic (the `NavHost`, `BottomNavigationBar`, and route definitions) is encapsulated in a dedicated `/presentation/navigation` package, keeping the `MainActivity` clean and focused.
- **Context-Aware UI:** The bottom navigation bar is context-aware, hiding itself on detail screens to provide a clearer and more intuitive navigation hierarchy.

## Getting Started

1.  Clone the repository.
2.  Open the project in a recent version of Android Studio.
3.  Let Gradle sync the dependencies.
4.  Run the `app` configuration on an emulator or a physical device.

## Running Tests

- To run all unit tests, execute the `./gradlew testDebugUnitTest` command.
- To run all instrumented (UI) tests, execute the `./gradlew connectedDebugAndroidTest` command.
