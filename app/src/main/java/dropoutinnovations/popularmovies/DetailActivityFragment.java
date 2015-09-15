package dropoutinnovations.popularmovies;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

public class DetailActivityFragment extends Fragment {
    String spin = MainActivity.getspin();
    List < Movie > popular;
    Bitmap img;
    TextView title;
    static String youtube1, youtube2;
    String review1, review2;
    String user1, user2;
    int Count;
    LinearLayout titlelayout;
    ImageButton star;
    int toggle = 0;
    int Color;
    SharedPreferences sharedpreferences;
    Context context;
    int position = 0;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_detail, container, false);

        sharedpreferences = this.getActivity().getSharedPreferences("Prefs", Context.MODE_PRIVATE);
        if (spin == "0") {
            popular = MainActivity.getpopular();
        } else if (spin == "Not0") {
            popular = MainActivity.gettop();
        } else {
            popular = MainActivity.getfavorites();
        }
        context = getActivity();
        if (!MainActivity.getboolean()) {
            Bundle extras = this.getActivity().getIntent().getExtras();

            position = extras.getInt("position");
        } else {
            position = getArguments().getInt("position");
        }


        Movie movie = popular.get(position);
        String url = movie.getDetailPoster();
        getbitmap get = new getbitmap();
        get.execute(url);
        ImageView detailimage = (ImageView) view.findViewById(R.id.image_detail);
        Glide.with(this).load(url).placeholder(R.drawable.image1).into(detailimage);
        titlelayout = (LinearLayout) view.findViewById(R.id.title_layout);
        star = (ImageButton) view.findViewById(R.id.star);
        star.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Movie movie = popular.get(position);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                toggle = movie.getFavorite();
                if (toggle == 0) {
                    editor.putInt(movie.getTitle(), movie.getID());
                    editor.commit();
                    toggle = 1;
                    star.setImageResource(R.drawable.abc_btn_rating_star_on_mtrl_alpha);
                    star.setColorFilter(0xffFFD700);
                    movie.setFavorite(1);
                    return;
                }
                if (toggle == 1 && !MainActivity.getboolean()) {
                    editor.remove(movie.getTitle());
                    editor.commit();
                    star.setImageResource(R.drawable.abc_btn_rating_star_off_mtrl_alpha);
                    star.setColorFilter(0xffffffff);
                    toggle = 0;
                    movie.setFavorite(0);
                    return;
                }

                if (toggle == 1 && MainActivity.getboolean()) {
                    editor.remove(movie.getTitle());
                    editor.commit();
                    star.setImageResource(R.drawable.abc_btn_rating_star_off_mtrl_alpha);
                    star.setColorFilter(0xffffffff);
                    toggle = 0;
                    movie.setFavorite(0);
                    if (MainActivity.getspin() == "one") {
                        new Handler().post(new Runnable() {

                            @Override
                            public void run() {
                                SharedPreferences sharedpreferences1 = getActivity().getSharedPreferences("3", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor1 = sharedpreferences1.edit();
                                editor1.putBoolean("3", true);
                                editor1.commit();
                                Intent intent = getActivity().getIntent();
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                getActivity().overridePendingTransition(0, 0);
                                getActivity().finish();

                                getActivity().overridePendingTransition(0, 0);
                                startActivity(intent);
                            }
                        });

                    }
                }

            }
        });

        int isFavorite = movie.getFavorite();
        if (isFavorite == 0) {
            star.setImageResource(R.drawable.abc_btn_rating_star_off_mtrl_alpha);
            star.setColorFilter(0xffffffff);
        }
        if (sharedpreferences.contains(movie.getTitle())) {
            star.setImageResource(R.drawable.abc_btn_rating_star_on_mtrl_alpha);
            star.setColorFilter(0xffFFD700);
            movie.setFavorite(1);
        } else {
            star.setImageResource(R.drawable.abc_btn_rating_star_off_mtrl_alpha);
            star.setColorFilter(0xffffffff);
            movie.setFavorite(0);
        }
        TextView date = (TextView) view.findViewById(R.id.release);
        title = (TextView) view.findViewById(R.id.movie_name);
        TextView plot = (TextView) view.findViewById(R.id.plot);
        TextView rating = (TextView) view.findViewById(R.id.rating);
        date.setText(movie.getDate());
        rating.setText(movie.getRating());
        title.setText(movie.getTitle());
        plot.setText(movie.getPlot());
        int id = movie.getID();
        trailer trailer = new trailer();
        trailer.execute("" + id);
        reviews reviews = new reviews();
        reviews.execute("" + id);

        return view;
    }

    public static String getYoutube1(){
        return youtube1;
    }


    public class reviews extends AsyncTask < String, Void, Integer > {


        @Override
        protected Integer doInBackground(String...params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader;
            String json;
            String website = "http://api.themoviedb.org/3/movie/" + params[0] + "/reviews?api_key=";
            Uri uri = Uri.parse(website);
            URL url = null;
            try {
                url = new URL(uri.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                urlConnection.setRequestMethod("GET");
            } catch (ProtocolException e) {
                e.printStackTrace();
            }
            try {
                urlConnection.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }
            InputStream inputStream = null;
            try {
                inputStream = urlConnection.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (buffer.length() == 0) {
                return null;
            }
            json = buffer.toString();
            try {
                return getreviews(json);
            } catch (JSONException e) {
                Log.e("Error", e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }


        private int getreviews(String json) throws JSONException {
            int count;
            JSONObject object = new JSONObject(json);
            count = object.getInt("total_results");
            JSONArray results = object.getJSONArray("results");
            if (count == 0) {}
            if (count == 1) {
                JSONObject review = results.getJSONObject(0);
                review1 = review.getString("content");
                user1 = review.getString("author");
            }
            if (count == 2) {
                JSONObject review = results.getJSONObject(0);
                review1 = review.getString("content");
                user1 = review.getString("author");
                JSONObject reviews = results.getJSONObject(1);
                review2 = reviews.getString("content");
                user2 = reviews.getString("author");
            }
            return count;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            if (integer == 0) {}
            if (integer == 1) {
                TextView reviewtitle = (TextView) view.findViewById(R.id.review_title);
                reviewtitle.setVisibility(View.VISIBLE);
                reviewtitle.setText("User Reviews :");
                TextView r1 = (TextView) view.findViewById(R.id.review1);
                r1.setVisibility(View.VISIBLE);
                r1.setText("\"" + review1 + "\"" + " - " + user1);
            }
            if (integer == 2) {
                TextView reviewtitle = (TextView) view.findViewById(R.id.review_title);
                reviewtitle.setVisibility(View.VISIBLE);
                reviewtitle.setText("User Reviews :");
                TextView r1 = (TextView) view.findViewById(R.id.review1);
                r1.setVisibility(View.VISIBLE);
                r1.setText("\"" + review1 + "\"" + " - " + user1);
                View view1 = view.findViewById(R.id.line1);
                view1.setVisibility(View.VISIBLE);
                TextView r2 = (TextView) view.findViewById(R.id.review2);
                r2.setVisibility(View.VISIBLE);
                r2.setText("\"" + review2 + "\"" + " - " + user2);

            }
        }

    }


    public class trailer extends AsyncTask < String, Void, Integer > {

        @Override
        protected Integer doInBackground(String...params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader;
            String json;
            String website = "http://api.themoviedb.org/3/movie/" + params[0] + "/videos?api_key=";
            Uri uri = Uri.parse(website);
            URL url = null;
            try {
                url = new URL(uri.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                urlConnection.setRequestMethod("GET");
            } catch (ProtocolException e) {
                e.printStackTrace();
            }
            try {
                urlConnection.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }
            InputStream inputStream = null;
            try {
                inputStream = urlConnection.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (buffer.length() == 0) {
                return null;
            }
            json = buffer.toString();
            try {
                return gettrailer(json);
            } catch (JSONException e) {
                Log.e("Error", e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }


        private int gettrailer(String json) throws JSONException {
            String string = "Trailer";
            int count = 0;
            JSONObject object = new JSONObject(json);
            JSONArray results = object.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject trailer = results.getJSONObject(i);
                String type = trailer.getString("type");
                if (count == 2) {
                    break;
                }
                if (type.equals(string) && count == 0) {
                    youtube1 = trailer.getString("key");
                    count++;
                    continue;
                }
                if (type.equals(string) && count == 1) {
                    youtube2 = trailer.getString("key");
                    count++;
                }
            }
            return count;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            Count = integer;

            if (Count == 1) {
                Button trailer1 = (Button) view.findViewById(R.id.trailer1);
                trailer1.setVisibility(View.VISIBLE);
                trailer1.setText("Play Trailer");
                trailer1.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + youtube1))
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT));
                    }
                });
            }
            if (Count == 2) {
                Button trailer1 = (Button) view.findViewById(R.id.trailer1);
                trailer1.setVisibility(View.VISIBLE);
                trailer1.setText("Play Trailer 1");
                Button trailer2 = (Button) view.findViewById(R.id.trailer2);
                trailer2.setVisibility(View.VISIBLE);
                trailer2.setText("Play Trailer 2");
                trailer1.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + youtube1))
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT));
                    }
                });
                trailer2.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + youtube2))
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT));
                    }
                });
            }
        }

    }


    public class getbitmap extends AsyncTask < String, Void, Bitmap > {
        Bitmap myBitmap;

        @Override
        protected Bitmap doInBackground(String...params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            img = bitmap;
            int color = 0;
            int titlecolor = 0;
            Palette palette = Palette.generate(img);
            List < Palette.Swatch > list = palette.getSwatches();
            int population = 0;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getPopulation() > population) {
                    population = list.get(i).getPopulation();
                    color = list.get(i).getRgb();
                    titlecolor = list.get(i).getTitleTextColor();
                }
            }
            Color = color;
            title.setTextColor(titlecolor);
            star.setBackgroundColor(color);
            titlelayout.setBackgroundColor(color);
        }
    }
}