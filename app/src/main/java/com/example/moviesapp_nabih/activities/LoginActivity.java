package com.example.moviesapp_nabih.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.moviesapp_nabih.R;
import com.example.moviesapp_nabih.api.LingoApiService;
import com.example.moviesapp_nabih.api.LingoRetrofitClient;
import com.google.gson.JsonObject;
import java.io.IOException;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin;
    TextView tvRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Remplis tous les champs !", Toast.LENGTH_SHORT).show();
                return;
            }

            loginUser(email, password);
        });

        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    private void loginUser(String email, String password) {
        JsonObject body = new JsonObject();
        body.addProperty("email", email);
        body.addProperty("password", password);

        LingoRetrofitClient.getInstance().getApiService()
                .login(body)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            // Sauvegarder email
                            getSharedPreferences("user_prefs", MODE_PRIVATE)
                                    .edit()
                                    .putString("user_email", email)
                                    .apply();

                            Toast.makeText(LoginActivity.this, "Connexion réussie !", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            overridePendingTransition(R.anim.fade_in, R.anim.slide_out_left);
                            finish();
                        } else {
                            try {
                                String error = response.errorBody().string();
                                if (error.contains("non trouvé")) {
                                    Toast.makeText(LoginActivity.this, "Email non trouvé !", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Mot de passe incorrect !", Toast.LENGTH_SHORT).show();
                                }
                            } catch (IOException e) {
                                Toast.makeText(LoginActivity.this, "Erreur connexion", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(LoginActivity.this, "Erreur réseau : " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}