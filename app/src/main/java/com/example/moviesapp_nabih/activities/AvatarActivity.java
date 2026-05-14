package com.example.moviesapp_nabih.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.moviesapp_nabih.FavoritesManager;
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

public class AvatarActivity extends AppCompatActivity {

    ImageView ivAvatar;
    TextView tvAvatarEmoji, tvAvatarName, tvAvatarStatus;
    Button btnTakePhoto, btnGallery, btnMic, btnSend;
    EditText etMessage;
    ScrollView scrollView;
    LinearLayout chatContainer;

    SharedPreferences prefs;
    FavoritesManager favoritesManager;
    OkHttpClient client = new OkHttpClient();

    private static final String GEMINI_API_KEY = "AIzaSyAjHtMGTrwicgBpM5doZ42mnqlYnTW0dlY";
    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=";
    private static final int REQUEST_CAMERA = 100;
    private static final int REQUEST_GALLERY = 101;
    private static final int REQUEST_MIC = 102;
    private static final int REQUEST_CAMERA_PERMISSION = 103;

    Bitmap avatarBitmap = null;
    String username = "Mon Avatar";
    String userEmail = "";
    int favCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar);

        ivAvatar = findViewById(R.id.ivAvatar);
        tvAvatarEmoji = findViewById(R.id.tvAvatarEmoji);
        tvAvatarName = findViewById(R.id.tvAvatarName);
        tvAvatarStatus = findViewById(R.id.tvAvatarStatus);
        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        btnGallery = findViewById(R.id.btnGallery);
        btnMic = findViewById(R.id.btnMic);
        btnSend = findViewById(R.id.btnSend);
        etMessage = findViewById(R.id.etMessage);
        scrollView = findViewById(R.id.scrollView);
        chatContainer = findViewById(R.id.chatContainer);

        prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        favoritesManager = new FavoritesManager(this);

        userEmail = prefs.getString("user_email", "utilisateur@lingo.tv");
        username = userEmail.split("@")[0];
        favCount = favoritesManager.getFavorites().size();

        tvAvatarName.setText(username);

        // Message de bienvenue
        addAvatarMessage("Bonjour ! Je suis " + username + ", ton avatar IA ! 👋 Tu peux me poser n'importe quelle question sur les films, le cinéma, ou autre chose. Je suis là pour toi ! 😊");

        // Prendre photo
        btnTakePhoto.setOnClickListener(v -> openCamera());

        // Galerie
        btnGallery.setOnClickListener(v -> openGallery());

        // Envoyer message
        btnSend.setOnClickListener(v -> {
            String message = etMessage.getText().toString().trim();
            if (!message.isEmpty()) {
                addUserMessage(message);
                etMessage.setText("");
                respondAsHuman(message);
            }
        });

        // Micro
        btnMic.setOnClickListener(v -> startVoiceRecognition());

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
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
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CAMERA);
        }
    }

    // ══════════════════════════════════════
    // GALERIE
    // ══════════════════════════════════════
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_GALLERY);
    }

    // ══════════════════════════════════════
    // MICRO
    // ══════════════════════════════════════
    private void startVoiceRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fr-FR");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "fr-FR");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Parlez en français... 🎤");
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);

        try {
            startActivityForResult(intent, REQUEST_MIC);
            btnMic.setText("👂");
            btnMic.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                            Color.parseColor("#e94560")));
        } catch (Exception e) {
            Toast.makeText(this, "Micro non disponible sur l'émulateur", Toast.LENGTH_SHORT).show();
        }
    }

    // ══════════════════════════════════════
    // RÉSULTATS
    // ══════════════════════════════════════
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK && data != null) {
            avatarBitmap = (Bitmap) data.getExtras().get("data");
            if (avatarBitmap != null) {
                ivAvatar.setImageBitmap(avatarBitmap);
                tvAvatarEmoji.setVisibility(View.GONE);
                ivAvatar.setVisibility(View.VISIBLE);
                Toast.makeText(this, "Photo prise ! Ton avatar est mis à jour ✅", Toast.LENGTH_SHORT).show();
                addAvatarMessage("Super ! Maintenant tu peux voir mon vrai visage ! 😊 Pose-moi une question !");
            }
        }

        if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            try {
                avatarBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                ivAvatar.setImageBitmap(avatarBitmap);
                tvAvatarEmoji.setVisibility(View.GONE);
                ivAvatar.setVisibility(View.VISIBLE);
                Toast.makeText(this, "Photo sélectionnée ! ✅", Toast.LENGTH_SHORT).show();
                addAvatarMessage("Voilà mon vrai visage ! 🥰 Comment puis-je t'aider ?");
            } catch (Exception e) {
                Toast.makeText(this, "Erreur lors du chargement", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == REQUEST_MIC && resultCode == RESULT_OK && data != null) {
            ArrayList<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            if (results != null && !results.isEmpty()) {
                String spokenText = results.get(0);
                addUserMessage(spokenText);
                respondAsHuman(spokenText);
                btnMic.setText("🎤");
                btnMic.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(
                                Color.parseColor("#333333")));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        }
    }

    // ══════════════════════════════════════
    // RÉPONSE IA COMME HUMAIN
    // ══════════════════════════════════════
    private void respondAsHuman(String userMessage) {
        tvAvatarStatus.setText("💭 En train de réfléchir...");

        String prompt = "Tu es un avatar IA qui représente " + username +
                ", une étudiante passionnée de cinéma qui utilise l'application LINGO TV. " +
                "Tu as " + favCount + " films dans tes favoris. " +
                "Réponds de manière naturelle, chaleureuse et humaine en français. " +
                "Parle à la première personne comme si tu étais vraiment cette personne. " +
                "Sois concise (2-3 phrases max) et utilise des emojis. " +
                "Question/message reçu : " + userMessage;

        try {
            JSONObject textPart = new JSONObject();
            textPart.put("text", prompt);

            JSONArray partsArray = new JSONArray();
            partsArray.put(textPart);

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

            Request request = new Request.Builder()
                    .url(GEMINI_URL + GEMINI_API_KEY)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> {
                        addAvatarMessage(getFallbackResponse(userMessage));
                        tvAvatarStatus.setText("🟢 En ligne — Prête à discuter");
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
                            addAvatarMessage(text);
                            tvAvatarStatus.setText("🟢 En ligne — Prête à discuter");
                        });
                    } catch (Exception e) {
                        runOnUiThread(() -> {
                            addAvatarMessage(getFallbackResponse(userMessage));
                            tvAvatarStatus.setText("🟢 En ligne — Prête à discuter");
                        });
                    }
                }
            });
        } catch (Exception e) {
            addAvatarMessage(getFallbackResponse(userMessage));
            tvAvatarStatus.setText("🟢 En ligne — Prête à discuter");
        }
    }

    // Réponses de secours si API indisponible
    private String getFallbackResponse(String message) {
        String msg = message.toLowerCase();
        if (msg.contains("film") || msg.contains("cinéma") || msg.contains("movie")) {
            return "J'adore le cinéma ! 🎬 J'ai déjà " + favCount + " films dans mes favoris sur LINGO TV. Tu veux des recommandations ?";
        } else if (msg.contains("bonjour") || msg.contains("salut") || msg.contains("hello")) {
            return "Bonjour ! 😊 Je suis " + username + ", ton avatar IA. Comment puis-je t'aider aujourd'hui ?";
        } else if (msg.contains("comment") && msg.contains("vas")) {
            return "Je vais très bien merci ! 🌟 Toujours prête à discuter de films et de cinéma avec toi !";
        } else if (msg.contains("action")) {
            return "J'adore les films d'action ! 💥 John Wick et Mission Impossible sont mes préférés !";
        } else {
            return "C'est une bonne question ! 😊 En tant que passionnée de cinéma, je suis toujours prête à en discuter avec toi !";
        }
    }

    // ══════════════════════════════════════
    // AFFICHAGE MESSAGES
    // ══════════════════════════════════════
    private void addAvatarMessage(String message) {
        LinearLayout msgLayout = new LinearLayout(this);
        msgLayout.setOrientation(LinearLayout.HORIZONTAL);
        msgLayout.setGravity(Gravity.START);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 8, 60, 8);
        msgLayout.setLayoutParams(params);

        // Avatar image ou emoji
        if (avatarBitmap != null) {
            ImageView avatarImg = new ImageView(this);
            avatarImg.setImageBitmap(avatarBitmap);
            LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(50, 50);
            imgParams.setMargins(0, 0, 8, 0);
            avatarImg.setLayoutParams(imgParams);
            avatarImg.setScaleType(ImageView.ScaleType.CENTER_CROP);
            msgLayout.addView(avatarImg);
        } else {
            TextView avatarEmoji = new TextView(this);
            avatarEmoji.setText("👩");
            avatarEmoji.setTextSize(24);
            avatarEmoji.setPadding(0, 0, 8, 0);
            msgLayout.addView(avatarEmoji);
        }

        // Bulle message
        TextView tv = new TextView(this);
        tv.setText(message);
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(14);
        tv.setPadding(16, 12, 16, 12);
        tv.setBackground(createBubble("#1a1a2e"));

        LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        tv.setLayoutParams(tvParams);
        msgLayout.addView(tv);

        chatContainer.addView(msgLayout);
        scrollToBottom();
    }

    private void addUserMessage(String message) {
        LinearLayout msgLayout = new LinearLayout(this);
        msgLayout.setOrientation(LinearLayout.HORIZONTAL);
        msgLayout.setGravity(Gravity.END);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(60, 8, 0, 8);
        msgLayout.setLayoutParams(params);

        TextView tv = new TextView(this);
        tv.setText(message);
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(14);
        tv.setPadding(16, 12, 16, 12);
        tv.setBackground(createBubble("#e94560"));

        LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        tv.setLayoutParams(tvParams);
        msgLayout.addView(tv);

        TextView userEmoji = new TextView(this);
        userEmoji.setText("🙋‍♀️");
        userEmoji.setTextSize(24);
        userEmoji.setPadding(8, 0, 0, 0);
        msgLayout.addView(userEmoji);

        chatContainer.addView(msgLayout);
        scrollToBottom();
    }

    private android.graphics.drawable.GradientDrawable createBubble(String colorHex) {
        android.graphics.drawable.GradientDrawable shape =
                new android.graphics.drawable.GradientDrawable();
        shape.setShape(android.graphics.drawable.GradientDrawable.RECTANGLE);
        shape.setCornerRadius(24f);
        shape.setColor(Color.parseColor(colorHex));
        return shape;
    }

    private void scrollToBottom() {
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }
}