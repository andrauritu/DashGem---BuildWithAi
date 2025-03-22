# 🚘 DashGem – AI Car Dashboard Assistant

**DashGem** is an AI-powered app that helps drivers understand warning lights on their car's dashboard by snapping or uploading a photo. Powered by **Gemini 1.5 Pro**, DashGem delivers accurate, grounded, and chat-style explanations with actionable next steps.

**You can find our demos here:**
Android (Kotlin) Version https://www.youtube.com/watch?v=hJiWXm2iC38
IOS & Web (Flutter) Version https://www.youtube.com/watch?v=yGHNkooMWwQ

---

🛠️ Built with:
- 🤖 **Kotlin (Jetpack Compose)** for Android (uses Gemini SDK directly)
- 📱 **Flutter** for Web & iOS *(connects to the Node.js backend)*
- 🌐 **Node.js (Express)** backend with Gemini API (used by Web & iOS)

## ✨ Features

- 📸 **Dashboard Photo Input**
  - Upload or capture a dashboard photo from your device
  - AI analyzes the lights and symbols shown

- 🧠 **Gemini 2.0 Flash Integration**
  - Android uses the Gemini SDK directly
  - Flutter Web/iOS sends requests to a Node.js server which forwards to Gemini

- 💬 **Conversational Chat UI**
  - Interact naturally with multi-turn conversations
  - The AI remembers previous messages and attached images

- 🖼️ **Persistent Image Context**
  - The dashboard image stays available throughout the session, enriching follow-up questions

- 🛠️ **Mechanic-Inspired Prompting**
  - Custom Gemini system prompt instructs AI to behave like a car diagnostics expert
  - Grounded, clear, non-technical responses

## 🧱 Architecture & Data Flow

### Android (Kotlin)
- Built natively using Jetpack Compose
- Uses the **Gemini Generative AI SDK** directly from the device
- Compresses and sends dashboard image + user message in each prompt
- Secure API key setup via `local.properties` and `BuildConfig`

### Flutter (Web & iOS)
- Frontend built with Flutter
- Sends multipart POST requests to the Node.js server
- Includes optional dashboard image and user question
- Uses animated chat bubbles, image previews, and a custom chat layout

### Node.js Server (Express)


- Handles CORS, uploads (via `multer`), and Gemini API calls
- Validates and detects MIME type with `mmmagic`
- Sends prompt + optional image to **Gemini 2.0 Flash**
- Returns structured AI response to the Flutter frontend

## 🌐 Backend API (Node.js + Gemini)

The backend server handles requests from the Flutter Web/iOS frontend, processes dashboard images and user messages, and returns AI-powered responses.

### 🔁 Endpoint

**POST** `/analyzeDashboardPic`

#### Request (multipart/form-data):
- `image` (optional): a JPEG or PNG image of the dashboard
- `text`: the user's message (combined with memory of prior chat)

> Example request from Flutter:
> ```
> POST heroku-link
> Content-Type: multipart/form-data
> ```

#### Successful Response:
> ```json
> {
>   "response": "This indicator usually means low tire pressure. Please check all tires."
> }
> ```

### 📍 Hosting

- ✅ Hosted on **Heroku**
- 🌐 Public endpoint used by Flutter Web/iOS:
  > `https://dash-gem-ef3cd0583e98.herokuapp.com/analyzeDashboardPic`
- ⚠️ **Note**: GCP hosting setup is in progress and currently non-functional.

## 🧪 Running DashGem Locally

You can run DashGem's backend, Android app, and Flutter frontend separately.

---

### ▶️ Android App (Kotlin)

> ✅ Requires Android Studio

1. Open the `android/` project in Android Studio.
2. In `local.properties`, add your Gemini API key:
> ```
> geminiApiKey=YOUR_API_KEY
> ```
3. Run the app on an emulator or physical device.

---

### ▶️ Backend Server (Node.js)

> ✅ Requires Node.js v18+ and Heroku CLI (optional)

1. From the `server/` folder:
> ```bash
> npm install
> ```
2. Add your API key to a `.env` file:
> ```
> GEMINI_API_KEY=your_key_here
> ```
3. Start the server locally:
> ```bash
> node app.js
> ```
4. The API will be live at:
> `http://localhost:8080/analyzeDashboardPic`

To deploy to Heroku:
> ```bash
> heroku login
> heroku create
> git push heroku main
> ```

---

### ▶️ Flutter Web/iOS App

> ✅ Requires Flutter 3.x with web/iOS enabled

1. Navigate to the `web/` project folder:
> ```bash
> cd web/
> flutter pub get
> ```
2. Make sure the server URL in code points to Heroku:
> ```dart
> const String kBackendUrl = 'https://dash-gem-ef3cd0583e98.herokuapp.com/analyzeDashboardPic';
> ```
3. Run the app:
> ```bash
> flutter run -d chrome
> ```
4. For iOS (macOS only):
> ```bash
> flutter run -d ios
> ```

## 🧠 Gemini AI System Prompt

DashGem uses a custom-crafted prompt to guide the Gemini model's behavior and tone.

---

### 🔧 Prompt Purpose

The AI is instructed to:
- Act like a **professional car mechanic**
- Avoid off-topic, hallucinated, or fictional responses
- Be **helpful, accurate, and easy to understand**
- Remember prior user messages and any attached images

---

### 📝 Prompt Snippet

> ```
> You are a highly skilled car mechanic who interprets icons on car dashboards.
> Your task is to analyze the dashboard lights, identify possible issues,
> and explain what each illuminated icon means. Be brief, accurate, and clear.
> You will always remember past user messages and images. Avoid going off-topic.
> ```

This ensures that users get consistent, grounded explanations — even across multiple messages in a single chat session.

## 🚀 Roadmap & Future Enhancements

Here are some ideas and planned upgrades for upcoming versions:

- 🔎 **OCR (Optical Character Recognition)**
  - Automatically detect and extract dashboard text and icons from photos

- 🌐 **Multilingual Support**
  - Translate responses for non-English-speaking users

- 📶 **Offline / Fallback Mode**
  - Offer limited local guidance when the user has no internet

- 📈 **Analytics Dashboard**
  - Track the most common dashboard warning lights across users

- 📱 **Push Notifications (Mobile)**
  - Notify users of unresolved warnings or follow-up suggestions

- 💾 **Local Chat History**
  - Let users revisit previous sessions with persistent storage

---

We’re always open to ideas! Feel free to open an issue or discussion to suggest new features or improvements. 🚗✨
