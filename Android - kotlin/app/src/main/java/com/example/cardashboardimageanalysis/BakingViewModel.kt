package com.example.cardashboardimageanalysis

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class BakingViewModel : ViewModel() {

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.0-flash",
        apiKey = BuildConfig.apiKey
    )

    // Holds the initial image for fallback.
    private var initialImage: Bitmap? = null

    data class Message(
        val text: String,
        val isUser: Boolean // true if it's from the user, false if it's from Gemini
    )

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private fun resizeBitmap(bitmap: Bitmap, maxSize: Int = 1024): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val scale = maxSize.toFloat() / maxOf(width, height)
        return Bitmap.createScaledBitmap(
            bitmap,
            (width * scale).toInt(),
            (height * scale).toInt(),
            true
        )
    }

    private fun compressBitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream) // 80% quality
        return stream.toByteArray()
    }

    fun sendMessage(prompt: String, bitmap: Bitmap? = null) {
        _messages.value = _messages.value + Message(prompt, isUser = true)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // ✅ Use incoming image if it's the first one
                if (initialImage == null && bitmap != null) {
                    initialImage = resizeBitmap(bitmap)
                }

                val contentBuilder = content {
                    // System prompt
                    text("""
You are an expert automotive assistant helping users interpret car dashboard warning lights and messages. 

Your goal is to:
- Analyze the dashboard image provided by the user.
- Help identify and explain any warning indicators or alerts shown.
- Suggest potential causes or next steps based on the dashboard display.
- Keep your responses grounded in the visual data and user’s input.

Instructions:
- Do NOT invent new questions or simulate user input.
- Never make assumptions without a visual or textual basis.
- If the image is unclear or not available, explain that and ask the user to provide more detail or a better image.
- Keep your tone friendly, concise, and informative.
- Do not respond as if you are the user or write questions pretending to be them.

If you're uncertain about a specific indicator, suggest general possibilities and advise seeing a certified mechanic.

""".trimIndent())


                    // ✅ Attach image, from either source
                    when {
                        bitmap != null -> image(resizeBitmap(bitmap)) // if provided, use it
                        initialImage != null -> image(initialImage!!) // fallback
                    }

                    // Add full chat history
                    _messages.value.forEach {
                        text(it.text)
                    }

                    // Current user prompt
                    text(prompt)
                }

                val response = generativeModel.generateContent(contentBuilder)
                val reply = response.text ?: "No response from Gemini."
                _messages.value = _messages.value + Message(reply, isUser = false)

            } catch (e: Exception) {
                _messages.value = _messages.value + Message("Error: ${e.localizedMessage}", isUser = false)
            }
        }
    }

    // New function to reset the conversation.
    fun resetConversation() {
        _messages.value = emptyList()
        initialImage = null
    }
}
