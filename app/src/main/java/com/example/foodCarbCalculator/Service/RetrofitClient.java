package com.example.foodCarbCalculator.Service;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Singleton factory that provides a shared {@link Retrofit} instance configured
 * for the OpenAI API.
 *
 * <p><b>Purpose:</b> Ensures the app maintains a single Retrofit (and therefore a
 * single underlying {@code OkHttpClient}) rather than constructing a new one on
 * every API call.</p>
 *
 * <p><b>System Role:</b> Acts as the HTTP layer factory. Any component that needs
 * to call OpenAI should call {@link #getInstance()} and then create the desired
 * service interface via {@code retrofit.create(OpenAIService.class)}.</p>
 *
 * <p><b>Learning Note:</b> Every {@code Retrofit.Builder().build()} call creates
 * a new {@code OkHttpClient} with its own thread pool and connection pool. Reusing
 * a single instance is important for performance and resource efficiency.</p>
 *
 * <p><b>Design Note:</b> Thread-safety on the lazy initialisation is not implemented
 * here because this is a single-Activity app where initialisation always happens on
 * the main thread. In a multi-threaded or DI-managed context, prefer double-checked
 * locking or a dependency injection framework such as Hilt.</p>
 */
public class RetrofitClient {

    private static final String BASE_URL = "https://api.openai.com/";

    private static Retrofit instance;

    private RetrofitClient() {
        // Prevent instantiation — use getInstance()
    }

    /**
     * Returns the shared {@link Retrofit} instance, creating it on first access.
     *
     * @return A {@link Retrofit} instance pointing at {@code https://api.openai.com/}
     *         with Gson conversion enabled.
     */
    public static Retrofit getInstance() {
        if (instance == null) {
            instance = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return instance;
    }
}
