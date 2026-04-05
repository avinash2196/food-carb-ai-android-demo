package com.example.foodCarbCalculator.Service;

import com.example.foodCarbCalculator.Model.OpenAIRequest;
import com.example.foodCarbCalculator.Model.OpenAIResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Retrofit service interface for the OpenAI Chat Completions API.
 *
 * <p><b>Purpose:</b> Declares the HTTP endpoints used to communicate with OpenAI.
 * Retrofit generates a concrete implementation of this interface at runtime using
 * dynamic proxies.</p>
 *
 * <p><b>System Role:</b> Defines the network boundary — what HTTP calls the app
 * can make. {@link RetrofitClient} provides the Retrofit instance that implements
 * this interface.</p>
 *
 * <p><b>Learning Note:</b> Retrofit uses Java annotations ({@code @POST},
 * {@code @Header}, {@code @Body}) to describe the HTTP contract declaratively.
 * At runtime it translates those annotations into an actual {@code OkHttp} request.</p>
 *
 * <p><b>Security Note:</b> The {@code Authorization} header carries the API key in
 * {@code "Bearer sk-..."}  format. Never commit real API keys to source control.
 * In production, route requests through a backend service that holds the key
 * server-side rather than embedding it in the APK.</p>
 */
public interface OpenAIService {

    /**
     * Sends a Chat Completions request to OpenAI and returns the model's response.
     *
     * <p>Retrofit automatically sets {@code Content-Type: application/json} when
     * a {@code @Body} parameter is used with {@code GsonConverterFactory}, so
     * no explicit Content-Type header is needed.</p>
     *
     * @param authorization The API key in {@code "Bearer <key>"} format.
     * @param request       The request payload (model, messages, token limit).
     * @return A Retrofit {@link Call} wrapping the deserialized {@link OpenAIResponse}.
     */
    @POST("v1/chat/completions")
    Call<OpenAIResponse> getCarbEstimate(
            @Header("Authorization") String authorization,
            @Body OpenAIRequest request
    );
}
