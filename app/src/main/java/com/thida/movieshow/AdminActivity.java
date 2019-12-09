package com.thida.movieshow;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.gson.JsonObject;
import com.thida.movieshow.Service.ApiService;

import java.io.ByteArrayOutputStream;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdminActivity extends AppCompatActivity {
    ImageView image;
    EditText movieName;
    ProgressBar progressBar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin);
        final Button browse = findViewById(R.id.browse_photo);
        final Button send = findViewById(R.id.send);
        image = findViewById(R.id.image);
        movieName = findViewById(R.id.movie_name);
        progressBar = findViewById(R.id.progressBar);


        browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                String[] mimeTypes = {"image/jpeg", "image/png"};
                intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
                startActivityForResult(intent,1);
            }
        });

        send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (movieName.getText().toString().equals("")) {
                    movieName.setError("Empty Movie Name is not allowed!");
                } else if (image.getDrawable() == null) {
                    Toast.makeText(getApplicationContext(), "Browse Movie Image", Toast.LENGTH_LONG).show();
                } else {

                    progressBar.setVisibility(View.VISIBLE);
                    browse.setEnabled(false);
                    send.setEnabled(false);
                    movieName.setEnabled(false);

                    //covert image to Base 64
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    BitmapDrawable drawable = (BitmapDrawable) image.getDrawable();
                    Bitmap bitmap = drawable.getBitmap();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                    byte[] imageBytes = baos.toByteArray();
                    String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                    String server = "http://172.16.4.110:7070";

                    try {
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(server)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();
                        JsonObject object = new JsonObject();
                        object.addProperty("id", 16);
                        object.addProperty("name", movieName.getText().toString());
                        object.addProperty("image", imageString);
                    /*JsonArray array = new JsonArray();
                    array.add(object);
                    Log.e("Json Array ", array.toString());*/

                        ApiService service = retrofit.create(ApiService.class);
                        Call<ResponseBody> call = service.postData(object);
                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()) {
                                    browse.setEnabled(true);
                                    send.setEnabled(true);
                                    movieName.setEnabled(true);
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(), "Uploading is Success", Toast.LENGTH_LONG).show();
                                } else {
                                    browse.setEnabled(true);
                                    send.setEnabled(true);
                                    movieName.setEnabled(true);
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(), "Error " + response.body().toString(), Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                browse.setEnabled(true);
                                send.setEnabled(true);
                                movieName.setEnabled(true);
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK){
            Uri selectedImage = data.getData();
            image.setImageURI(selectedImage);
        }
    }
}
