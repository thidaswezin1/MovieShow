package com.thida.movieshow;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.thida.movieshow.Service.ApiService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ImageView imageView = findViewById(R.id.imageView);
        final TextView text = findViewById(R.id.textView);
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.show();

        String server = "http://172.16.4.110:7070";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(server)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService service = retrofit.create(ApiService.class);
        Call<ResponseBody> call = service.getData();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.body()!=null){
                    dialog.dismiss();
                    Log.e("data from Server ",response.body().toString());
                    try {
                        /*JSONObject object = new JSONObject(response.body().string());
                        JSONArray jsonarray  = object.getJSONArray("result");
                        for(int i=0;i<jsonarray.length();i++){
                            JSONObject jsonObject = jsonarray.getJSONObject(i);
                            text.setText(jsonObject.getString("name"));
                            String image= jsonObject.getString("image");
                            byte[] decodedString = Base64.decode(image, Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            imageView.setImageBitmap(bitmap);

                        }*/

                        Gson gson = new Gson();
                        JsonObject jsonObject = gson.fromJson(response.body().string(), JsonElement.class).getAsJsonObject();
                        JsonArray jsonArray = jsonObject.getAsJsonArray("result");
                        for(int i=0;i<jsonArray.size();i++){
                            JsonObject jsonObject1 = jsonArray.get(i).getAsJsonObject();
                            text.setText(jsonObject1.get("name").getAsString());
                            String image=jsonObject1.get("image").getAsString();
                            byte[] decodedString = Base64.decode(image, Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            imageView.setImageBitmap(bitmap);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(),"Server Error",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Error"+t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });




    }
}
