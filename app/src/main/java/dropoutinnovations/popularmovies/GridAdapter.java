package dropoutinnovations.popularmovies;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GridAdapter extends RecyclerView.Adapter < GridAdapter.ViewHolder > {
    private Context context;
    String spin = MainActivity.getspin();
    List < Movie > popular = Collections.emptyList();
    Movie movie;

    public GridAdapter(Context context) {
        super();
        this.context = context;
        if (spin == "0") {
            popular = MainActivity.getpopular();
        } else if (spin == "Not0") {
            popular = MainActivity.gettop();
        } else {
            popular = MainActivity.getfavorites();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.grid, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v, context);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        movie = popular.get(i);
        Uri uri = Uri.parse(movie.getPoster());
        Context c = viewHolder.imageView.getContext();
        Glide.with(c).load(uri).placeholder(R.drawable.image).into(viewHolder.imageView);
        viewHolder.setPosition(i);
    }

    @Override
    public int getItemCount() {
        return popular.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Context context;
        public ImageView imageView;
        int position;

        public void setPosition(int i) {
            position = i;
        }

        public ViewHolder(View itemView, Context context) {
            super(itemView);
            itemView.setOnClickListener(this);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            if (MainActivity.getboolean()) {
                Bundle bundle = new Bundle();
                bundle.putInt("position", position);
                DetailActivityFragment fragment = new DetailActivityFragment();
                fragment.setArguments(bundle);
                switchContent(R.id.main_fragment, fragment);

            } else {
                Intent myIntent = new Intent(context, DetailActivity.class);
                myIntent.putExtra("position", position);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(myIntent);
            }
        }
        public void switchContent(int id, Fragment fragment) {
            if (context == null) return;
            if (context instanceof MainActivity) {
                MainActivity mainActivity = (MainActivity) context;
                Fragment frag = fragment;
                mainActivity.switchContent(id, frag);
            }

        }
    }
}