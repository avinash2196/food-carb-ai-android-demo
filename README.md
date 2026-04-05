> **This repository is intended for learning, experimentation, and reference purposes. It is not designed as a production-grade system.**

# food-carb-ai-android-demo

An Android app that estimates the carbohydrate content of food using the OpenAI Chat Completions API — a hands-on learning reference for Retrofit-based REST API integration, async networking, and AI-powered features in Android.

---

## Overview

**What this project does:**  
The user types a food description (e.g. "1 cup of cooked white rice"), taps a button, and the app sends that text to OpenAI's `gpt-3.5-turbo` model. The model replies with a concise carbohydrate estimate, which is displayed on screen.

**Why this problem matters:**  
Calling third-party AI/ML APIs from mobile apps is a common real-world pattern — whether for semantic search, content generation, or smart recommendations. This project walks through the end-to-end flow: building a typed HTTP request, sending it asynchronously, parsing the JSON response, and displaying the result safely on the UI thread.

---

## Real-World Context

**Where this pattern is used in production:**
- Nutrition and health apps querying AI for food information
- Chatbot UIs embedded in mobile applications
- Any Android app that consumes a REST API returning structured JSON

**Example Use Cases:**
- "How many carbs in a banana?" → direct answer from GPT
- "Estimate macros for 200g boiled chicken and 150g pasta" → structured model response
- Template for integrating any OpenAI-compatible API endpoint into Android

---

## What This Repo Demonstrates

- Declaring a REST API contract with a **Retrofit service interface**
- Serializing a Java POJO into a JSON request body using **Gson + GsonConverterFactory**
- Making **non-blocking (async) HTTP calls** with Retrofit's `enqueue` / `Callback` pattern
- Structuring an Android project with clear **Model / Service / Activity layers**
- Singleton HTTP client management via a `RetrofitClient` factory
- Writing **JVM unit tests** for model construction and JSON (de)serialization
- Communicating with the **OpenAI Chat Completions API** from Android
- Safe API key usage pattern with a placeholder in `strings.xml`

---

## Architecture / Component Flow

```
User Input (EditText)
       │
       ▼
 MainActivity          — UI controller; reads input, triggers API call, renders result
       │
       │  creates service via
       ▼
 RetrofitClient        — Singleton Retrofit instance (shared OkHttpClient)
       │
       │  implements interface
       ▼
 OpenAIService         — Retrofit interface: @POST v1/chat/completions
       │
       │  serializes via Gson
       ▼
 OpenAIRequest         — Request POJO: model, messages[], max_tokens
       │
       │  HTTP POST ──────────────────────────────────► OpenAI API
       │                                               (api.openai.com)
       │  ◄─────────────────────────────── JSON response
       ▼
 OpenAIResponse        — Response POJO: choices[].message.content
       │
       ▼
 MainActivity          — Callback on main thread → update TextView
```

**Step-by-step request lifecycle:**
1. User enters food text and taps **Calculate Carbs**.
2. `MainActivity.calculateCarbs()` validates input and builds an `OpenAIRequest` with a system prompt and the user's query.
3. `RetrofitClient.getInstance()` returns the shared Retrofit instance.
4. Retrofit serializes `OpenAIRequest` to JSON and fires an async HTTP POST to `https://api.openai.com/v1/chat/completions`.
5. On success, Gson deserializes the response body into `OpenAIResponse`.
6. The callback (delivered on the Android main thread) extracts `choices[0].message.content` and updates the `TextView`.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 11 (Android), Kotlin (tests & Compose theme) |
| HTTP Client | Retrofit 2 + OkHttp 4 |
| JSON | Gson via GsonConverterFactory |
| AI Model | OpenAI `gpt-3.5-turbo` (Chat Completions API) |
| UI | XML layouts + ConstraintLayout + AppCompat |
| Build | Gradle 8.9 with Kotlin DSL (.kts) |
| Min SDK | API 24 (Android 7.0) |
| Target SDK | API 34 (Android 14) |
| Unit Tests | JUnit 4 + Gson (JVM, no emulator needed) |

---

## Project Structure

```
android-openai-learning-lab/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/foodCarbCalculator/
│   │   │   │   ├── Activity/
│   │   │   │   │   ├── MainActivity.java       # UI controller; triggers API call
│   │   │   │   │   └── SplashActivity.java     # Launch screen with timed transition
│   │   │   │   ├── Model/
│   │   │   │   │   ├── OpenAIRequest.java      # Request POJO (serialized to JSON)
│   │   │   │   │   └── OpenAIResponse.java     # Response POJO (deserialized from JSON)
│   │   │   │   └── Service/
│   │   │   │       ├── OpenAIService.java      # Retrofit interface (@POST endpoint)
│   │   │   │       └── RetrofitClient.java     # Singleton Retrofit/OkHttpClient factory
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   │   ├── activity_main.xml       # Input field, button, result view
│   │   │   │   │   └── activity_splash.xml     # Splash screen layout
│   │   │   │   └── values/
│   │   │   │       └── strings.xml             # App name + API key placeholder
│   │   │   └── AndroidManifest.xml
│   │   └── test/
│   │       └── java/com/example/foodCarbCalculator/
│   │           └── ExampleUnitTest.kt          # JVM unit tests (model + singleton)
│   └── build.gradle.kts
├── gradle/
│   └── libs.versions.toml                      # Centralized dependency versions
├── .gitignore
├── build.gradle.kts
├── settings.gradle.kts
├── LICENSE
└── README.md
```

---

## How to Run Locally

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- Android SDK with API 34 platform installed
- A valid [OpenAI API key](https://platform.openai.com/api-keys)

### 1 — Add your API key

Open `app/src/main/res/values/strings.xml` and replace the placeholder:

```xml
<string name="openai_api_key">Your_API_KEY_Here</string>
```

> **Security:** Never commit a real API key to source control. For a more secure approach, see [Advanced — Protecting your API key](#advanced--protecting-your-api-key) below.

### 2 — Run the app

Open the project in Android Studio, connect a device or start an emulator (API 24+), then click **Run ▶**.

Or from the command line:

```bash
# macOS / Linux
./gradlew installDebug

# Windows
gradlew.bat installDebug
```

### Advanced — Protecting your API key

For any shared or public repository, move the key out of `strings.xml`:

1. Create a `secrets.properties` file in the project root (already in `.gitignore`):
   ```
   OPENAI_API_KEY=sk-...
   ```
2. Read it in `app/build.gradle.kts` with `buildConfigField` and access it via `BuildConfig.OPENAI_API_KEY` in Java code.

---

## How to Run Tests

Unit tests run on the JVM — no device or emulator required:

```bash
# macOS / Linux
./gradlew test

# Windows
gradlew.bat test
```

HTML test report is generated at:
```
app/build/reports/tests/testDebugUnitTest/index.html
```

**What is tested:**
- `OpenAIRequest` — construction, field values, JSON snake_case serialization
- `OpenAIResponse` — JSON deserialization, field access, graceful handling of unknown fields
- `RetrofitClient` — non-null instance, singleton identity

Instrumented (on-device) tests:
```bash
./gradlew connectedAndroidTest
```

---

## Example Usage

**User interaction flow:**

1. Launch the app — the splash screen appears for 2 seconds, then `MainActivity` opens.
2. Enter a food description in the input field:
   ```
   2 slices of whole wheat bread and 1 tablespoon of peanut butter
   ```
3. Tap **Calculate Carbs**.
4. The app sends the following request to OpenAI:
   ```json
   {
     "model": "gpt-3.5-turbo",
     "messages": [
       { "role": "system", "content": "You are a nutrition assistant that only responds with the number of carbs in food items. Provide a short, direct answer without explanations." },
       { "role": "user",   "content": "How many carbs are in: 2 slices of whole wheat bread and 1 tablespoon of peanut butter" }
     ],
     "max_tokens": 150
   }
   ```
5. The model responds; the app displays the extracted content, e.g.:
   ```
   Approximately 30g of carbohydrates
   ```

---

## Learning Outcomes

After studying this project you should be able to:

- **Define a Retrofit interface** and understand how annotations map to HTTP requests
- **Use `GsonConverterFactory`** to serialize/deserialize Java objects to/from JSON
- **Make asynchronous network calls** in Android using `enqueue` and handle both success and failure callbacks
- **Structure an Android project** with clean separation of Model, Service, and Activity layers
- **Work with the OpenAI Chat Completions API** — request format, system prompts, and response structure
- **Write JVM unit tests** that exercise model and serialization logic without needing an Android device
- **Manage a singleton HTTP client** to avoid the pitfalls of creating redundant `OkHttpClient` instances

---

## Limitations

This project is intentionally simplified for learning purposes:

| What is simplified | Why / Production alternative |
|---|---|
| API key in `strings.xml` | In production, use a backend proxy or secrets manager so the key is never in the APK |
| No ViewModel / LiveData | A real app uses MVVM to survive configuration changes (e.g. screen rotation) |
| No loading state indicator | Production apps show a progress spinner during network calls |
| No error detail parsing | OpenAI error bodies contain helpful messages — parsing them improves UX |
| Single-activity architecture | Larger apps use Navigation Component with multiple fragments or screens |
| `max_tokens = 150` hardcoded | Token limits should be configurable based on use case |
| No request caching | Repeated identical queries hit the API every time; consider a simple in-memory cache |
| No offline handling | Network unavailability produces a generic error message |

---

## Future Improvements

- [ ] Migrate to ViewModel + LiveData / StateFlow (MVVM)
- [ ] Add a `BuildConfig` field for the API key, loaded from `secrets.properties`
- [ ] Parse OpenAI error response bodies for clearer user-facing messages
- [ ] Add a loading spinner / progress indicator
- [ ] Support multiple models (gpt-4o, gpt-4-turbo) via a dropdown selector
- [ ] Add a history screen showing past queries and results (Room database)
- [ ] Expand unit tests with Mockito to mock Retrofit calls in `MainActivity`

---

## License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.
