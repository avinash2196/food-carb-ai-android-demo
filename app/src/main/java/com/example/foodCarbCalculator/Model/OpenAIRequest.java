package com.example.foodCarbCalculator.Model;

import java.util.List;

/**
 * Represents the request payload sent to the OpenAI Chat Completions API.
 *
 * <p><b>Purpose:</b> Encapsulates the model identifier, conversation messages, and
 * token limit required by the {@code POST /v1/chat/completions} endpoint.</p>
 *
 * <p><b>System Role:</b> This is a plain data object (POJO) serialized to JSON by
 * Gson (via Retrofit's {@code GsonConverterFactory}) before being sent over the network.</p>
 *
 * <p><b>Learning Note:</b> Gson maps Java field names directly to JSON keys by default.
 * The field {@code max_tokens} intentionally uses snake_case to match the OpenAI API
 * contract exactly — Gson serializes it as {@code "max_tokens"} without needing
 * a {@code @SerializedName} annotation.</p>
 *
 * <p><b>API Reference:</b>
 * <a href="https://platform.openai.com/docs/api-reference/chat">OpenAI Chat API</a></p>
 */
public class OpenAIRequest {

    private final String model;
    private final List<Message> messages;
    private final int max_tokens;  // snake_case matches the OpenAI JSON field name

    /**
     * Constructs a new Chat Completions request.
     *
     * @param model      The OpenAI model to use (e.g., {@code "gpt-3.5-turbo"}).
     * @param messages   Ordered list of conversation messages (system prompt + user input).
     * @param max_tokens Maximum number of tokens allowed in the model's response.
     */
    public OpenAIRequest(String model, List<Message> messages, int max_tokens) {
        this.model = model;
        this.messages = messages;
        this.max_tokens = max_tokens;
    }

    public String getModel() {
        return model;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public int getMaxTokens() {
        return max_tokens;
    }

    /**
     * Represents a single message in an OpenAI chat conversation.
     *
     * <p>Each message has a {@code role} ({@code "system"}, {@code "user"}, or
     * {@code "assistant"}) and text {@code content}. The role tells the model how
     * to interpret the message — e.g., {@code "system"} sets the overall behaviour,
     * while {@code "user"} represents the human turn.</p>
     */
    public static class Message {

        private final String role;
        private final String content;

        /**
         * @param role    The sender role: {@code "system"}, {@code "user"}, or
         *                {@code "assistant"}.
         * @param content The text content of the message.
         */
        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() {
            return role;
        }

        public String getContent() {
            return content;
        }
    }
}
