package com.thida.movieshow.Service;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("/api/users")
    Call<ResponseBody> getData();

}
