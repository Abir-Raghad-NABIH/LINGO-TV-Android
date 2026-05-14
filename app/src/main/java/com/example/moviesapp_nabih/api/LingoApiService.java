package com.example.moviesapp_nabih.api;

import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface LingoApiService {

    @POST("register")
    Call<ResponseBody> register(@Body JsonObject body);

    @POST("login")
    Call<ResponseBody> login(@Body JsonObject body);

    @POST("favorites/add")
    Call<ResponseBody> addFavorite(@Body JsonObject body);

    @GET("favorites/{email}")
    Call<ResponseBody> getFavorites(@Path("email") String email);

    @DELETE("favorites/{email}/{movie_id}")
    Call<ResponseBody> removeFavorite(
            @Path("email") String email,
            @Path("movie_id") int movieId
    );
}