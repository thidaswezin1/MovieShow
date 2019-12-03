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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder>{
    private List<MovieItem> movieList;

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
