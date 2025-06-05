package avx.aibuilder.ai;

import avx.aibuilder.AIBuilderPlugin;
import avx.aibuilder.data.BuildRecipe;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class OpenAIService {
    
    private final AIBuilderPlugin plugin;
    private final HttpClient httpClient;
    private final Gson gson;
    private final String apiKey;
    private final String model;
    private final PromptBuilder promptBuilder;
    
    public OpenAIService(AIBuilderPlugin plugin) {
        this.plugin = plugin;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.gson = new Gson();
        this.apiKey = plugin.getConfigManager().getOpenAIApiKey();
        this.model = plugin.getConfigManager().getOpenAIModel();
        this.promptBuilder = new PromptBuilder();
    }
    
    public CompletableFuture<BuildRecipe> generateBuildRecipe(String userPrompt) {
        if (apiKey == null || apiKey.isEmpty()) {
            return CompletableFuture.failedFuture(
                new IllegalStateException("OpenAI API key not configured")
            );
        }
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                String systemPrompt = promptBuilder.buildSystemPrompt();
                String userMessage = promptBuilder.buildUserPrompt(userPrompt);
                
                JsonObject requestBody = createRequestBody(systemPrompt, userMessage);
                
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + apiKey)
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                        .timeout(Duration.ofSeconds(30))
                        .build();
                
                HttpResponse<String> response = httpClient.send(request, 
                    HttpResponse.BodyHandlers.ofString());
                
                if (response.statusCode() != 200) {
                    throw new RuntimeException("OpenAI API error: " + response.statusCode() + " - " + response.body());
                }
                
                return parseResponse(response.body());
                
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException("Failed to communicate with OpenAI API", e);
            }
        });
    }
    
    private JsonObject createRequestBody(String systemPrompt, String userPrompt) {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", model);
        requestBody.addProperty("temperature", 0.3); // Lower temperature for more consistent results
        requestBody.addProperty("max_tokens", 1000);
        
        // Add response format for structured output
        JsonObject responseFormat = new JsonObject();
        responseFormat.addProperty("type", "json_object");
        requestBody.add("response_format", responseFormat);
        
        // Create messages array
        com.google.gson.JsonArray messages = new com.google.gson.JsonArray();
        
        // System message
        JsonObject systemMessage = new JsonObject();
        systemMessage.addProperty("role", "system");
        systemMessage.addProperty("content", systemPrompt);
        messages.add(systemMessage);
        
        // User message
        JsonObject userMessage = new JsonObject();
        userMessage.addProperty("role", "user");
        userMessage.addProperty("content", userPrompt);
        messages.add(userMessage);
        
        requestBody.add("messages", messages);
        
        return requestBody;
    }
    
    private BuildRecipe parseResponse(String responseBody) {
        try {
            JsonObject response = JsonParser.parseString(responseBody).getAsJsonObject();
            
            if (response.has("error")) {
                throw new RuntimeException("OpenAI API error: " + response.get("error").getAsJsonObject().get("message").getAsString());
            }
            
            String content = response
                .getAsJsonArray("choices")
                .get(0).getAsJsonObject()
                .getAsJsonObject("message")
                .get("content").getAsString();
            
            // Parse the JSON content into BuildRecipe
            JsonObject recipeJson = JsonParser.parseString(content).getAsJsonObject();
            
            return gson.fromJson(recipeJson, BuildRecipe.class);
            
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to parse OpenAI response: " + e.getMessage());
            plugin.getLogger().severe("Response body: " + responseBody);
            throw new RuntimeException("Failed to parse OpenAI response", e);
        }
    }
    
    public boolean isConfigured() {
        return apiKey != null && !apiKey.isEmpty();
    }
    
    public String getModel() {
        return model;
    }
} 