package com.thida.movieshow;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.thida.movieshow.Service.ApiService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder>{
    private List<MovieItem> movieList;
    String server = "http://172.16.4.110:7070";

    public MovieAdapter(List<MovieItem> movieList) {
        this.movieList = movieList;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View movieView = inflater.inflate(R.layout.show_view,parent,false);
        return new MovieViewHolder(movieView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MovieViewHolder holder, final int position) {
        MovieItem movieItem = movieList.get(position);
        holder.imageView.setImageBitmap(movieItem.getImage());
        holder.textView.setText(movieItem.getMovieName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                //Toast.makeText(view.getContext(),"Hello",Toast.LENGTH_LONG).show();
                new AlertDialog.Builder(view.getContext())
                        .setMessage("Image will be saved.")
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(holder.imageView.getDrawable()==null) Toast.makeText(view.getContext(),"Image is null",Toast.LENGTH_LONG).show();

                              else {
                                    BitmapDrawable drawable = (BitmapDrawable) holder.imageView.getDrawable();
                                    Bitmap bitmap = drawable.getBitmap();
                                    File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/MovieImage");
                                    boolean b = dir.mkdir();
                                    Log.e("Image Dir ",dir+"");
                                    File file = new File(dir, "Movie" + (position + 1) + ".jpg");
                                    Log.e("Image file name ",file+"");
                                    if (file.exists()) {
                                        Toast.makeText(view.getContext(), "Image is already saved.", Toast.LENGTH_LONG).show();
                                    } else {
                                        try {
                                            FileOutputStream out = new FileOutputStream(file);
                                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                                            out.flush();
                                            out.close();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        view.getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                                        // sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                                        Toast.makeText(view.getContext(), "Image is saved successfully.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).show();

            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {
                //Toast.makeText(view.getContext(),"hello",Toast.LENGTH_LONG).show();
                new AlertDialog.Builder(view.getContext())
                        .setTitle("DELETE PHOTO")
                        .setIcon(R.drawable.ic_delete_forever_black_24dp)
                        .setMessage("Are you sure to delete?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl(server)
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build();
                                ApiService apiService = retrofit.create(ApiService.class);
                                Call<ResponseBody> call = apiService.deleteMovie(position+1);
                                call.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        if(response.isSuccessful()) {

                                            Toast.makeText(view.getContext(),"Deleting is Success",Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(view.getContext(),MainActivity.class);
                                            view.getContext().startActivity(intent);
                                        }
                                        else Toast.makeText(view.getContext(),"Error " +response.body().toString(),Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        Toast.makeText(view.getContext(),"Failure "+t.getMessage(),Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView textView;

        private MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.movie_image);
            textView = itemView.findViewById(R.id.movie_name);
            /*itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });*/
        }


    }
}
