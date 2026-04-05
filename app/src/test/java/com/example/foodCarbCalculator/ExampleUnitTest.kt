package com.example.foodCarbCalculator

import com.example.foodCarbCalculator.Model.OpenAIRequest
import com.example.foodCarbCalculator.Model.OpenAIResponse
import com.example.foodCarbCalculator.Service.RetrofitClient
import com.google.gson.Gson
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for core model classes and the RetrofitClient singleton.
 *
 * These tests run on the JVM (no Android device or emulator required).
 * Run with: `./gradlew test`
 *
 * Coverage:
 * - [OpenAIRequest] construction and JSON serialization
 * - [OpenAIResponse] JSON deserialization
 * - [RetrofitClient] singleton behaviour
 */
class OpenAIModelTest {

    private val gson = Gson()

    // ── OpenAIRequest ─────────────────────────────────────────────────────────

    @Test
    fun `OpenAIRequest stores model name correctly`() {
        val request = OpenAIRequest("gpt-3.5-turbo", emptyList(), 150)
        assertEquals("gpt-3.5-turbo", request.model)
    }

    @Test
    fun `OpenAIRequest stores max tokens correctly`() {
        val request = OpenAIRequest("gpt-3.5-turbo", emptyList(), 150)
        assertEquals(150, request.maxTokens)
    }

    @Test
    fun `OpenAIRequest preserves message list`() {
        val messages = listOf(
            OpenAIRequest.Message("system", "You are a nutrition assistant."),
            OpenAIRequest.Message("user", "How many carbs in an apple?")
        )
        val request = OpenAIRequest("gpt-3.5-turbo", messages, 150)

        assertEquals(2, request.messages.size)
        assertEquals("system", request.messages[0].role)
        assertEquals("user", request.messages[1].role)
    }

    @Test
    fun `OpenAIRequest Message stores role and content`() {
        val message = OpenAIRequest.Message("user", "How many carbs in 100g of rice?")
        assertEquals("user", message.role)
        assertEquals("How many carbs in 100g of rice?", message.content)
    }

    @Test
    fun `OpenAIRequest serializes max_tokens as snake_case JSON field`() {
        val request = OpenAIRequest("gpt-3.5-turbo", emptyList(), 100)
        val json = gson.toJson(request)
        // Verifies the field name matches the OpenAI API contract exactly
        assertTrue("JSON must contain snake_case max_tokens field", json.contains("\"max_tokens\""))
    }

    @Test
    fun `OpenAIRequest JSON contains model and messages fields`() {
        val messages = listOf(OpenAIRequest.Message("user", "test"))
        val request = OpenAIRequest("gpt-3.5-turbo", messages, 100)
        val json = gson.toJson(request)

        assertTrue(json.contains("\"model\""))
        assertTrue(json.contains("\"messages\""))
    }

    // ── OpenAIResponse ────────────────────────────────────────────────────────

    @Test
    fun `OpenAIResponse deserializes choices array from JSON`() {
        val json = """
            {
              "choices": [
                { "message": { "role": "assistant", "content": "27g of carbs" } }
              ]
            }
        """.trimIndent()

        val response = gson.fromJson(json, OpenAIResponse::class.java)
        assertNotNull(response.choices)
        assertEquals(1, response.choices.size)
    }

    @Test
    fun `OpenAIResponse choice message content is accessible`() {
        val json = """
            {
              "choices": [
                { "message": { "role": "assistant", "content": "Approximately 27g" } }
              ]
            }
        """.trimIndent()

        val response = gson.fromJson(json, OpenAIResponse::class.java)
        assertEquals("Approximately 27g", response.choices[0].message.content)
    }

    @Test
    fun `OpenAIResponse choice message role is accessible`() {
        val json = """
            {
              "choices": [
                { "message": { "role": "assistant", "content": "27g" } }
              ]
            }
        """.trimIndent()

        val response = gson.fromJson(json, OpenAIResponse::class.java)
        assertEquals("assistant", response.choices[0].message.role)
    }

    @Test
    fun `OpenAIResponse handles empty choices list`() {
        val json = """{"choices": []}"""
        val response = gson.fromJson(json, OpenAIResponse::class.java)
        assertTrue(response.choices.isEmpty())
    }

    @Test
    fun `OpenAIResponse ignores unknown JSON fields gracefully`() {
        // Gson silently ignores extra fields — this mirrors real API responses
        // that also return id, object, created, usage, etc.
        val json = """
            {
              "id": "chatcmpl-abc123",
              "object": "chat.completion",
              "created": 1700000000,
              "choices": [
                { "message": { "role": "assistant", "content": "27g" }, "finish_reason": "stop" }
              ],
              "usage": { "prompt_tokens": 30, "completion_tokens": 5, "total_tokens": 35 }
            }
        """.trimIndent()

        val response = gson.fromJson(json, OpenAIResponse::class.java)
        assertEquals(1, response.choices.size)
        assertEquals("27g", response.choices[0].message.content)
    }

    // ── RetrofitClient ────────────────────────────────────────────────────────

    @Test
    fun `RetrofitClient getInstance returns non-null Retrofit instance`() {
        assertNotNull(RetrofitClient.getInstance())
    }

    @Test
    fun `RetrofitClient getInstance returns the same singleton on repeated calls`() {
        val first = RetrofitClient.getInstance()
        val second = RetrofitClient.getInstance()
        assertSame("Should return the same singleton instance", first, second)
    }
}