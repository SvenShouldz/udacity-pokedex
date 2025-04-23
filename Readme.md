# Design Document: Simple Pokedex App

**Project:** Udacity Android Nanodegree Capstone Project
**Option Selected:** Option 1 - Custom Application
**Author:** Sven Schulz
**Date:** April 23, 2025

## 1. App Goal

Create a simple Pokedex app for Android. Users can:
* See a grid of Pokemon from the PokeAPI.
* Search this grid by Pokemon name.
* Tap a Pokemon to see more details (image, type, stats).
* Mark Pokemon as "Caught" and save them locally.
* See a list of your "Caught" Pokemon.
* Get a notification when you catch a Pokemon.

The app will use MVVM, Navigation Component, Retrofit, Glide, and Room.

## 2. How it Meets Requirements (Rubric Areas)

* **Architecture (MVVM):** Using `ViewModel`, `LiveData`, `Repository` pattern. Keeping logic out of `Fragments`.
* **UI & Layout:**
    * **3 Screens:** Pokemon List (Grid), Pokemon Details, Caught Pokemon List (Grid). Use Navigation Controller with SafeArgs for passing data (like Pokemon ID).
    * **Layouts:** Using `ConstraintLayout` for screens. Using `RecyclerView` with `GridLayoutManager` for the grids.
    * **Resources:** Using `strings.xml`, `colors.xml`, `dimens.xml` for values.
* **Animation (MotionLayout):**Simple animations using `MotionLayout` in a `MotionScene` on the Pokemon Detail screen.
* **Network & Data:**
    * **API:** Using `Retrofit` and `Moshi`/`Gson` to get data from `pokeapi.co`.
    * **Images:** Using `Glide` to load Pokemon images. Showing placeholders while loading.
    * **Local DB:** Using `Room` to save the IDs/names of caught Pokemon.
    * **Threading:** All network and database work in the background (using Coroutines).
* **Hardware & System:**
    * **Notifications:** You get a system notification when a Pokemon is caught.
    * **Permissions:** Ask for Notification permission (Android 13+) when needed.
    * **Lifecycle:** Using `ViewModel`s to handle orientation changes.

## 3. Features

* Fetch and display Pokemon list/grid from PokeAPI.
* Client-side search functionality on the Pokemon list.
* Display Pokemon details (name, image, type, stats) from PokeAPI.
* Save/Remove Pokemon from a local "Caught" list using Room.
* Display the "Caught" Pokemon list/grid from Room.
* Show loading indicators and handle network errors.
* Simple `MotionLayout` animation on detail screen.
* Notification when a Pokemon is caught.

## 4. Development Plan (Milestones)

*(Focus on functionality first, UI polish towards the end)*

1.  **Project Setup & Basic Navigation:**
    * Create project, add all needed libraries (Retrofit, Glide, Room, etc.).
    * Set up basic MVVM classes (Base ViewModel/Fragment if desired).
    * Define Navigation Graph (`nav_graph.xml`) with 3 fragments (List, Detail, Caught).
    * Make fragments navigate to each other.
    * **Goal:** App compiles, basic navigation works.

2.  **Pokemon List - API & Display:**
    * Set up Retrofit interface for PokeAPI (get Pokemon list).
    * Create `PokemonListViewModel` and `Repository`.
    * Fetch the first batch of Pokemon from the API.
    * Display Pokemon names (and maybe sprites) in the `PokemonListFragment`.
    * **Goal:** List screen shows data fetched from the internet.

3.  **Pokemon List - Search:**
    * Add a `SearchView` to the `PokemonListFragment`.
    * Implement client-side filtering logic in the `PokemonListViewModel` or Fragment to filter the displayed list based on user input.
    * **Goal:** User can search the Pokemon list by name.

4.  **Pokemon Detail - API & Display:**
    * Pass Pokemon ID or name from List to Detail fragment using SafeArgs.
    * Add Retrofit call to get specific Pokemon details.
    * Implement `PokemonDetailViewModel`.
    * Fetch details in ViewModel and display name, image(with Glide), types, stats in `PokemonDetailFragment`.
    * **Goal:** Detail screen shows correct data for the selected Pokemon.

5.  **Local Storage - Room Setup:**
    * Define Room `Entity` for caught Pokemon.
    * Create Room `DAO` (Data Access Object) with functions to add, delete, and get caught Pokemon.
    * Create Room `Database` class.
    * **Goal:** Room database is set up and accessible.

6.  **Catch/Release Logic & Caught List:**
    * Add "Catch" / "Release" button to `PokemonDetailFragment`.
    * Implement logic in `PokemonDetailViewModel` to add/remove Pokemon from Room DB via Repository/DAO when button is clicked.
    * Implement `CaughtListViewModel` to fetch the list of caught Pokemon from Room DB.
    * Display the caught Pokemon list/grid in `CaughtListFragment`.
    * **Goal:** User can save Pokemon, and saved Pokemon appear in the Caught list.

7.  **Animation & Hardware (Notifications):**
    * Implement the `MotionLayout` animation on the `PokemonDetailFragment`.
    * Implement the notification trigger logic (when "Catch" is successful).
    * Add Notification Channel creation (for Android O+).
    * Implement Notification permission request (for Android 13+).
    * **Goal:** Animation works, notification appears on catch (after permission granted).

8.  **Final Polish & Documentation:**
    * Add loading indicators / progress bars.
    * Handle network/API error states gracefully (show messages).
    * Test lifecycle handling (orientation changes, app backgrounding).
    * Refine UI layouts, spacing, styles (using `dimens.xml`, `styles.xml`).
    * Perform final testing.
    * Build release APK/Bundle.
    * **Goal:** App is stable, looks reasonably polished, and ready for submission.