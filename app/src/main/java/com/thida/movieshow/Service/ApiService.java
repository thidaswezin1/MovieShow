package com.thida.movieshow.Service;

import com.google.gson.JsonObject;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @GET("/api/users")
    Call<ResponseBody> getData();

    @POST("/api/user")
    Call<ResponseBody> postData(@Body JsonObject body);

    @DELETE("/api/movies/{id}")
    Call<ResponseBody> deleteMovie(@Path("id") int movieId);

    @POST("/api/checkUser")
    Call<ResponseBody> postUser(@Body JsonObject body);


}
