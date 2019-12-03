package com.thida.movieshow.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @GET("/api/users")
    Call<ResponseBody> getData();

    @POST("/api/user")
    Call<ResponseBody> postData(@Body JsonObject body);


}
