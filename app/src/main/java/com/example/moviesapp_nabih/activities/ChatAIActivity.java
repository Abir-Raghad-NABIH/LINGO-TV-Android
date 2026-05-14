package com.example.moviesapp_nabih.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.moviesapp_nabih.R;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import android.util.Base64;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatAIActivity extends AppCompatActivity {

    EditText etMessage;
    ImageButton btnSend;
    Button btnMic, btnCamera;
    TextView tvChat;
    ScrollView scrollView;
    StringBuilder chatHistory = new StringBuilder();

    private static final String GEMINI_API_KEY = "AIzaSyAjHtMGTrwicgBpM5doZ42mnqlYnTW0dlY";
    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=";
    private static final int REQUEST_MIC = 100;
    private static final int REQUEST_CAMERA = 101;
    private static final int REQUEST_CAMERA_PERMISSION = 102;

    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_ai);

        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        btnMic = findViewById(R.id.btnMic);
        btnCamera = findViewById(R.id.btnCamera);
        tvChat = findViewById(R.id.tvChat);
        scrollView = findViewById(R.id.scrollView);

        appendMessage("🤖 Assistant LINGO TV", "Bonjour ! Je suis votre assistant cinéma. Vous pouvez me parler avec le 🎤 micro, m'envoyer une 📷 photo d'affiche, ou taper votre question ! 🎬");

        // Bouton envoyer
        btnSend.setOnClickListener(v -> {
            String message = etMessage.getText().toString().trim();
            if (!message.isEmpty()) {
                sendMessage(message);
                etMessage.setText("");
            }
        });

        // Bouton micro
        btnMic.setOnClickListener(v -> startVoiceRecognition());

        // Bouton caméra
        btnCamera.setOnClickListener(v -> openCamera());

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    // ══════════════════════════════════════
    // RECONNAISSANCE VOCALE
    // ══════════════════════════════════════
    private void startVoiceRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fr-FR");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "fr-FR");
        intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, "fr-FR");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Parlez en français... 🎤");
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);

        try {
            startActivityForResult(intent, REQUEST_MIC);
            btnMic.setText("🎤 Écoute...");
            btnMic.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                            android.graphics.Color.parseColor("#e94560")));
        } catch (Exception e) {
            Toast.makeText(this,
                    "Reconnaissance vocale non disponible sur l'émulateur !",
                    Toast.LENGTH_LONG).show();
        }
    }

    // ══════════════════════════════════════
    // CAMÉRA
    // ══════════════════════════════════════
    private void openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            return;
        }

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, REQUEST_CAMERA);
            Toast.makeText(this, "📷 Prenez une photo d'une affiche de film !", Toast.LENGTH_SHORT).show();
        }
    }

    // ══════════════════════════════════════
    // RÉSULTATS
    // ══════════════════════════════════════
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Résultat micro
        if (requestCode == REQUEST_MIC && resultCode == RESULT_OK && data != null) {
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (results != null && !results.isEmpty()) {
                String spokenText = results.get(0);
                etMessage.setText(spokenText);
                btnMic.setText("🎤 Parler");
                btnMic.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(
                                android.graphics.Color.parseColor("#1a1a2e")));
                sendMessage(spokenText);
                etMessage.setText("");
            }
        }

        // Résultat caméra
        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK && data != null) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            if (photo != null) {
                appendMessage("👤 Vous", "📷 [Photo d'affiche envoyée]");
                analyzeImageWithAI(photo);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Permission caméra refusée", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // ══════════════════════════════════════
    // ANALYSE IMAGE AVEC GEMINI
    // ══════════════════════════════════════
    private void analyzeImageWithAI(Bitmap bitmap) {
        appendMessage("🤖 Assistant", "...");
        btnSend.setEnabled(false);

        // Convertir bitmap en base64
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] imageBytes = baos.toByteArray();
        String base64Image = Base64.encodeToString(imageBytes, Base64.NO_WRAP);

        try {
            // Construire requête avec image
            JSONObject textPart = new JSONObject();
            textPart.put("text", "Tu es un expert en cinéma. Analyse cette affiche de film et dis-moi : le titre du film si tu le reconnais, le genre, une brève description, et ta recommandation. Réponds en français.");

            JSONObject imagePart = new JSONObject();
            JSONObject inlineData = new JSONObject();
            inlineData.put("mime_type", "image/jpeg");
            inlineData.put("data", base64Image);
            imagePart.put("inline_data", inlineData);

            JSONArray partsArray = new JSONArray();
            partsArray.put(textPart);
            partsArray.put(imagePart);

            JSONObject contentObj = new JSONObject();
            contentObj.put("parts", partsArray);

            JSONArray contentsArray = new JSONArray();
            contentsArray.put(contentObj);

            JSONObject requestBody = new JSONObject();
            requestBody.put("contents", contentsArray);

            RequestBody body = RequestBody.create(
                    requestBody.toString(),
                    MediaType.parse("application/json")
            );

            // Utiliser gemini-2.0-flash qui supporte les images
            String imageUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + GEMINI_API_KEY;

            Request request = new Request.Builder()
                    .url(imageUrl)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> {
                        updateLastMessage("Erreur réseau: " + e.getMessage());
                        btnSend.setEnabled(true);
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseBody = response.body().string();
                    try {
                        JSONObject json = new JSONObject(responseBody);
                        String text = json
                                .getJSONArray("candidates")
                                .getJSONObject(0)
                                .getJSONObject("content")
                                .getJSONArray("parts")
                                .getJSONObject(0)
                                .getString("text");

                        runOnUiThread(() -> {
                            updateLastMessage("📷 Analyse de l'affiche :\n\n" + text);
                            btnSend.setEnabled(true);
                        });
                    } catch (Exception e) {
                        runOnUiThread(() -> {
                            updateLastMessage("Je n'arrive pas à analyser cette image. Essayez avec une affiche de film plus claire !");
                            btnSend.setEnabled(true);
                        });
                    }
                }
            });
        } catch (Exception e) {
            updateLastMessage("Erreur: " + e.getMessage());
            btnSend.setEnabled(true);
        }
    }

    // ══════════════════════════════════════
    // ENVOI MESSAGE TEXTE
    // ══════════════════════════════════════
    private void sendMessage(String userMessage) {
        appendMessage("👤 Vous", userMessage);
        btnSend.setEnabled(false);
        appendMessage("🤖 Assistant", "...");

        String lowerMsg = userMessage.toLowerCase();

        // Réponses intelligentes locales
        new android.os.Handler().postDelayed(() -> {
            String response = getAIResponse(lowerMsg);
            updateLastMessage(response);
            btnSend.setEnabled(true);
        }, 1000);
    }

    private String getAIResponse(String message) {
        if (message.contains("action")) {
            return "🎬 Films d'action recommandés :\n\n" +
                    "1. John Wick — Keanu Reeves dans un thriller haletant\n" +
                    "2. Mad Max: Fury Road — Action post-apocalyptique\n" +
                    "3. Mission Impossible — Tom Cruise à son meilleur\n" +
                    "4. The Dark Knight — Batman vs Joker\n" +
                    "5. Avengers: Endgame — L'ultime combat Marvel";
        } else if (message.contains("comédie") || message.contains("comedie") || message.contains("drôle")) {
            return "😂 Comédies recommandées :\n\n" +
                    "1. Intouchables — Film français touchant et drôle\n" +
                    "2. The Grand Budapest Hotel — Wes Anderson au top\n" +
                    "3. Superbad — Comédie adolescente culte\n" +
                    "4. La Vérité si je mens — Comédie française\n" +
                    "5. Bienvenue chez les Ch'tis — Humour du nord";
        } else if (message.contains("horreur") || message.contains("peur")) {
            return "👻 Films d'horreur recommandés :\n\n" +
                    "1. Get Out — Thriller psychologique\n" +
                    "2. Hereditary — Terreur familiale\n" +
                    "3. The Conjuring — Basé sur des faits réels\n" +
                    "4. A Quiet Place — Le silence comme survie\n" +
                    "5. It — La terreur de Pennywise";
        } else if (message.contains("romance") || message.contains("amour")) {
            return "❤️ Films romantiques recommandés :\n\n" +
                    "1. Titanic — La romance légendaire\n" +
                    "2. The Notebook — Amour éternel\n" +
                    "3. La La Land — Rêves et amour à Hollywood\n" +
                    "4. Amélie Poulain — Charme parisien\n" +
                    "5. Before Sunrise — Romance spontanée";
        } else if (message.contains("science") || message.contains("sci-fi") || message.contains("espace")) {
            return "🚀 Science-fiction recommandée :\n\n" +
                    "1. Interstellar — Voyage dans l'espace-temps\n" +
                    "2. The Matrix — Réalité virtuelle\n" +
                    "3. Dune — Épopée spatiale\n" +
                    "4. Inception — Rêves dans les rêves\n" +
                    "5. Blade Runner 2049 — Futur dystopique";
        } else if (message.contains("bonjour") || message.contains("salut") || message.contains("hello")) {
            return "👋 Bonjour ! Je suis votre assistant cinéma LINGO TV !\n\n" +
                    "Vous pouvez :\n" +
                    "🎤 Me parler avec le micro\n" +
                    "📷 M'envoyer une photo d'affiche\n" +
                    "⌨️ Taper votre question\n\n" +
                    "Que voulez-vous regarder ce soir ?";
        } else {
            return "🎬 Je suis votre assistant cinéma LINGO TV !\n\n" +
                    "Demandez-moi des recommandations par genre :\n" +
                    "• Films d'action\n• Comédies\n• Horreur\n• Romance\n• Science-fiction\n\n" +
                    "Ou utilisez le 🎤 micro pour parler !";
        }
    }

    private void appendMessage(String sender, String message) {
        chatHistory.append("\n").append(sender).append(":\n").append(message).append("\n");
        tvChat.setText(chatHistory.toString());
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }

    private void updateLastMessage(String newMessage) {
        String current = chatHistory.toString();
        int lastIndex = current.lastIndexOf("...");
        if (lastIndex != -1) {
            chatHistory = new StringBuilder(current.substring(0, lastIndex) + newMessage + "\n");
        } else {
            chatHistory.append(newMessage).append("\n");
        }
        tvChat.setText(chatHistory.toString());
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }
}