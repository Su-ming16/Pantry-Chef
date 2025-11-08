# PantryChef

Pantry Chef is an Android app that helps users find recipes based on ingredients they already have. The core problem: users open their fridge and see a bunch of ingredients but don't know what to cook. This app lets users input their available ingredients and automatically recommends the most suitable recipes, reducing food waste and improving cooking efficiency.

## Current Version

**Alpha (Week 9)**

## Core Features

1. **My Pantry**
   - Add/remove ingredients from your pantry
   - Ingredients stored in local database (Room)
   - Auto-complete input support

2. **Discover Recipes**
   - Click "Cook Now" button to get recipe recommendations
   - View recipe details with ingredients and instructions
   - Navigate to recipe detail page

3. **My Favorites**
   - Save favorite recipes locally
   - View favorites without internet connection
   - Remove favorites

## Tech Stack

- **Language**: Kotlin
- **Architecture**: MVVM
- **UI**: Material Design 3, ViewBinding
- **Database**: Room
- **Image Loading**: Glide
- **Async**: Kotlin Coroutines + Flow

## Project Structure

```
app/src/main/java/com/example/pantrychef/
├── data/
│   ├── model/
│   ├── database/
│   └── repository/
└── ui/
    ├── common/
    ├── pantry/
    ├── discover/
    ├── detail/
    └── favorites/
```

## Setup

1. Clone the repository
```bash
git clone https://github.com/Su-ming16/Pantry-Chef.git
cd Pantry-Chef
```

2. Open in Android Studio
   - Wait for Gradle sync to complete

3. Run the app
   - Connect device or start emulator
   - Click Run button

## Requirements

- Android Studio Hedgehog or later
- JDK 11 or later
- Android SDK 24 or later
