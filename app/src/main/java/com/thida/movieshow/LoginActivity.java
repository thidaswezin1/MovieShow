package com.thida.movieshow;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.thida.movieshow.Service.ApiService;
import java.io.IOException;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.email)
    EditText email;

    @BindView(R.id.password)
    EditText password;

    @BindView(R.id.btn_login)
    Button login;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.checkbox)
    CheckBox checkBox;

    Gson gson = new Gson();
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        ButterKnife.bind(this);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(sharedPreferences.getBoolean("logged",false)){
            goToMainActivity();
        }
        login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                email.setEnabled(false);
                password.setEnabled(false);
                login.setEnabled(false);

                JsonObject object = new JsonObject();
                object.addProperty("email",email.getText().toString());
                object.addProperty("password",password.getText().toString());
                Log.e("json object ",object+"");

                String server = "http://172.16.4.110:7070";
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(server)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                ApiService apiService = retrofit.create(ApiService.class);
                Call<ResponseBody> call = apiService.postUser(object);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.isSuccessful()) {

                            try {
                                JsonObject jsonObject = gson.fromJson(response.body().string(), JsonElement.class).getAsJsonObject();
                                String status = jsonObject.get("status").getAsString();
                                boolean error = jsonObject.get("error").getAsBoolean();
                                if(status.equals("ok") && error) {
                                    progressBar.setVisibility(View.GONE);
                                    if(checkBox.isChecked()) sharedPreferences.edit().putBoolean("logged",true).apply();
                                    goToMainActivity();
                                }
                                else{
                                    progressBar.setVisibility(View.GONE);
                                    email.setEnabled(true);
                                    password.setEnabled(true);
                                    login.setEnabled(true);
                                    Toast.makeText(LoginActivity.this,"Login Error!\nCheck email and password!",Toast.LENGTH_LONG).show();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        else {
                            progressBar.setVisibility(View.GONE);
                            email.setEnabled(true);
                            password.setEnabled(true);
                            login.setEnabled(true);
                            Toast.makeText(LoginActivity.this, "Response Error " , Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(LoginActivity.this,"Error: "+t.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
    private void goToMainActivity(){
        startActivity(new Intent(LoginActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }
}
