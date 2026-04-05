package com.example.foodCarbCalculator.Activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodCarbCalculator.Model.OpenAIRequest;
import com.example.foodCarbCalculator.Model.OpenAIResponse;
import com.example.foodCarbCalculator.Service.OpenAIService;
import com.example.foodCarbCalculator.Service.RetrofitClient;
import com.example.foodCarbCalculator.R;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * The main screen of the Food Carb Calculator app.
 *
 * <p><b>Purpose:</b> Accepts a food description from the user, sends a prompt to the
 * OpenAI Chat Completions API via Retrofit, and displays the estimated carbohydrate
 * content in the response area.</p>
 *
 * <p><b>System Role:</b> Acts as the UI controller in a simple MVC-style layout.
 * Networking is delegated to {@link OpenAIService} (declared as a Retrofit interface),
 * and the HTTP client is provided by the {@link RetrofitClient} singleton.</p>
 *
 * <p><b>Learning Note:</b> Retrofit's {@code enqueue} dispatches the HTTP call on a
 * background thread and delivers the result callback on the Android main thread,
 * so UI updates inside {@code onResponse} and {@code onFailure} are safe.</p>
 *
 * <p><b>Design Note:</b> In a production app this logic would move into a ViewModel
 * (MVVM pattern), with LiveData/StateFlow managing UI state and surviving configuration
 * changes such as screen rotation.</p>
 */
public class MainActivity extends AppCompatActivity {

    private EditText queryEditText;
    private Button calculateCarbButton;
    private TextView queryResponseTextView;
    private OpenAIService openAIService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        queryEditText = findViewById(R.id.queryEditText);
        calculateCarbButton = findViewById(R.id.calculateCarbButton);
        queryResponseTextView = findViewById(R.id.responseTextView);

        // Use shared RetrofitClient — avoids creating multiple OkHttpClient instances
        openAIService = RetrofitClient.getInstance().create(OpenAIService.class);

        calculateCarbButton.setOnClickListener(v -> calculateCarbs());
    }

    /**
     * Reads the user's food query, constructs an OpenAI chat request, and fires the
     * asynchronous API call.
     *
     * <p>A system prompt instructs the model to reply with only the carb count,
     * keeping the response short and easy to display.</p>
     *
     * <p><b>Learning Note:</b> {@code max_tokens} is set low (150) because the response
     * is intentionally brief. Higher values cost more API credits and are unnecessary here.</p>
     */
    private void calculateCarbs() {
        String foodQuery = queryEditText.getText().toString().trim();

        if (foodQuery.isEmpty()) {
            queryResponseTextView.setText("Please enter a food item or description.");
            return;
        }

        queryResponseTextView.setText("Calculating\u2026");
        calculateCarbButton.setEnabled(false);

        List<OpenAIRequest.Message> messages = Arrays.asList(
                new OpenAIRequest.Message(
                        "system",
                        "You are a nutrition assistant that only responds with the number of carbs "
                        + "in food items. Provide a short, direct answer without explanations."
                ),
                new OpenAIRequest.Message("user", "How many carbs are in: " + foodQuery)
        );

        OpenAIRequest request = new OpenAIRequest("gpt-3.5-turbo", messages, 150);

        // API key is stored in res/values/strings.xml — replace with your key before running
        String apiKey = "Bearer " + getString(R.string.openai_api_key);

        openAIService.getCarbEstimate(apiKey, request).enqueue(new Callback<OpenAIResponse>() {
            @Override
            public void onResponse(Call<OpenAIResponse> call, Response<OpenAIResponse> response) {
                calculateCarbButton.setEnabled(true);
                if (response.isSuccessful()
                        && response.body() != null
                        && !response.body().getChoices().isEmpty()) {
                    String result = response.body().getChoices().get(0).getMessage().getContent();
                    queryResponseTextView.setText(result);
                } else {
                    queryResponseTextView.setText(
                            "Error " + response.code() + " — check your API key in strings.xml."
                    );
                }
            }

            @Override
            public void onFailure(Call<OpenAIResponse> call, Throwable t) {
                calculateCarbButton.setEnabled(true);
                queryResponseTextView.setText("Network error: " + t.getMessage());
            }
        });
    }
}
