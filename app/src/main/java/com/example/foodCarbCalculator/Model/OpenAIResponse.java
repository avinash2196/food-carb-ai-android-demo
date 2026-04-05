package com.example.foodCarbCalculator.Model;

import java.util.List;

/**
 * Represents the response body returned by the OpenAI Chat Completions API.
 *
 * <p><b>Purpose:</b> Deserializes the JSON response from
 * {@code POST /v1/chat/completions} into a structured Java object.</p>
 *
 * <p><b>System Role:</b> Gson (via Retrofit's {@code GsonConverterFactory}) maps
 * the JSON response fields to these Java fields using name matching.</p>
 *
 * <p><b>Learning Note:</b> The OpenAI response wraps the model's reply inside a
 * {@code choices} array. Even for single-turn requests, you must access
 * {@code choices.get(0).message.content} to extract the text.</p>
 *
 * <p><b>Design Note:</b> Fields such as {@code id}, {@code created}, and
 * {@code usage} are intentionally omitted — Gson ignores unknown JSON fields
 * by default, so the app only deserializes what it actually uses.</p>
 */
public class OpenAIResponse {

    private List<Choice> choices;

    public List<Choice> getChoices() {
        return choices;
    }

    /**
     * Represents one completion choice returned by the model.
     *
     * <p>OpenAI can return multiple choices when {@code n > 1} is set in the
     * request. This app always uses the default ({@code n = 1}).</p>
     */
    public static class Choice {

        private Message message;

        public Message getMessage() {
            return message;
        }
    }

    /**
     * The message object within a choice, containing the model's response text
     * and the role of the speaker ({@code "assistant"}).
     */
    public static class Message {

        private String role;
        private String content;

        public String getRole() {
            return role;
        }

        public String getContent() {
            return content;
        }
    }
}
