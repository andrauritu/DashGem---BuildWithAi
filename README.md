# 🚘 DashGem – AI Car Dashboard Assistant

DashGem is a sleek Android app powered by **Gemini (Google Generative AI)** that helps drivers understand the warning lights and indicators on their car dashboard. Just snap a photo of your dashboard, ask a question, and get smart, context-aware answers — all in a conversational interface.

---

## ✨ Features

- 📸 **Image Analysis** – Upload or capture your dashboard image and get real-time insights.
- 🧠 **Gemini AI Integration** – Leverages the Gemini 2.0 Flash model to analyze visuals and respond intelligently.
- 💬 **Conversational UI** – Have multi-turn chats with AI for better understanding, not just one-off answers.
- 🖼️ **Persistent Visual Context** – The AI always sees the image throughout the conversation.
- 🛡️ **Grounded & Helpful AI** – Custom prompt ensures professional, useful, and hallucination-free responses.

---

## 🧱 Tech Stack

| Layer            | Tech                                |
|------------------|-------------------------------------|
| Language         | Kotlin                              |
| UI               | Jetpack Compose + Material 3        |
| State Management | ViewModel + StateFlow               |
| AI Integration   | Gemini Generative AI SDK (Google)   |
| Image Handling   | Bitmap Compression + Memory Storage |

---

## 🔐 Secure API Key Setup

- Store your Gemini API key in `local.properties`:
  ```properties
  geminiApiKey=YOUR_API_KEY
  ```
- Access it in code via `BuildConfig.apiKey`, set up in `build.gradle`.
- **Do not hardcode** the key or commit it to version control.

---

## 🧹 App Structure

### 📁 `MainActivity.kt`
- Manages UI for image selection (camera/gallery)
- Handles chat input, message list, and API interactions

### 📁 `BakingViewModel.kt`
- Holds chat state and dashboard image
- Sends requests to Gemini with proper formatting and context
- Compresses image and attaches it to every prompt

### 📁 `UiState.kt`
- Represents UI state: `Initial`, `Loading`, `Success`, or `Error`

---

## 🧠 Gemini System Prompt

The assistant is instructed to:
- Act as a **professional automotive assistant**
- Avoid simulating users or generating fake queries
- Only respond based on the user input + image
- Stay **helpful, grounded, and accurate**

---

## ✅ Completed Improvements

- ✅ Switched from single-prompt to chat-based UX
- ✅ Persisted dashboard image throughout the conversation
- ✅ Fixed image reattachment on follow-up prompts
- ✅ Tuned system prompt to reduce hallucination
- ✅ Implemented secure API key storage via `BuildConfig`

---

## 📦 Getting Started

1. Clone the repo:
   ```bash
   git clone https://github.com/your-username/DashGem.git
   ```
2. Add your Gemini API key to `local.properties`:
   ```properties
   geminiApiKey=YOUR_API_KEY
   ```
3. Build & run the app on Android Studio.

---

## 🤝 Contributing

Pull requests are welcome! If you’d like to add features or fix bugs, feel free to open an issue first to discuss your idea.

---

## 🚀 Future Ideas

- 🔎 OCR for identifying specific icons or text on the dashboard
- 🌐 Language translation for non-English users
- 🛠️ Model fallback or offline mode for limited connectivity
- 📈 Analytics to track most common warning lights

---


