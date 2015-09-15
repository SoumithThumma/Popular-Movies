package dropoutinnovations.popularmovies;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    static List < Movie > popular = new ArrayList < Movie > ();
    static List < Movie > top = new ArrayList < Movie > ();
    static List < Movie > favorites = new ArrayList < Movie > ();
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    private Spinner spinner;
    static String spin = "0";
    Calendar calendar = Calendar.getInstance();
    int year = calendar.get(Calendar.YEAR);
    List < String > list = new ArrayList < > ();
    static boolean fragment;
    int oncreate = 0;
    boolean favorites1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        popular.clear();
        top.clear();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        spinner = (Spinner) findViewById(R.id.spinner);

        top get = new top();
        get.execute();

        getpopularmovies popularmovies = new getpopularmovies();
        popularmovies.execute();

        ArrayAdapter < CharSequence > adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner, R.layout.spinner);
        adapter.setDropDownViewResource(R.layout.spinnerdialog);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
        if (findViewById(R.id.main_fragment) != null) {
            SharedPreferences sharedpreferences1 = getSharedPreferences("3", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor1 = sharedpreferences1.edit();
            favorites1 = sharedpreferences1.getBoolean("3", false);

            SharedPreferences sharedpreferences = getSharedPreferences("Prefs", Context.MODE_PRIVATE);
            List < String > newlist = new ArrayList < > ();
            newlist.clear();
            Map < String, ?> keys = sharedpreferences.getAll();
            for (Map.Entry < String, ?> entry: keys.entrySet()) {
                newlist.add(entry.getValue().toString());
            }


            if (favorites1) {
                if (newlist.size() == 0) {

                    editor1.putBoolean("3", false);
                    editor1.commit();
                    favorites1 = sharedpreferences1.getBoolean("3", false);

                } else {
                    spinner.setSelection(2);
                    editor1.putBoolean("3", false);
                    editor1.commit();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if( findViewById(R.id.main_fragment) != null ){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);}
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_share_trailer:
                String youtube = DetailActivityFragment.getYoutube1();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "http://www.youtube.com/watch?v="+youtube);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static boolean getboolean() {
        return fragment;
    }

    @Override
    protected void onRestart() {
        SharedPreferences sharedpreferences = getSharedPreferences("Prefs", Context.MODE_PRIVATE);
        List < String > newlist = new ArrayList < > ();
        newlist.clear();
        Map < String, ?> keys = sharedpreferences.getAll();
        for (Map.Entry < String, ?> entry: keys.entrySet()) {
            newlist.add(entry.getValue().toString());
        }
        if (spinner.getSelectedItemPosition() == 2 && (list.size() != newlist.size())) {

            getfavorites favorites = new getfavorites();
            favorites.execute();
        }

        super.onRestart();
    }

    public class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView <? > parent, View view, int pos, long id) {
            if (pos == 0) {
                spin = "0";
                mAdapter = new GridAdapter(MainActivity.this);
                mRecyclerView.setAdapter(mAdapter);

                if (findViewById(R.id.main_fragment) != null && oncreate != 0) {
                    FrameLayout frameLayout = (FrameLayout) findViewById(R.id.main_fragment);
                    frameLayout.setVisibility(View.VISIBLE);
                    fragment = true;
                    int position = 0;
                    Bundle bundle = new Bundle();
                    bundle.putInt("position", position);
                    Fragment fragment = new DetailActivityFragment();
                    fragment.setArguments(bundle);
                    getFragmentManager().beginTransaction()
                            .add(R.id.main_fragment, fragment)
                            .commit();

                }
            } else if (pos == 1) {
                spin = "Not0";
                mAdapter = new GridAdapter(MainActivity.this);
                mRecyclerView.setAdapter(mAdapter);

                if (findViewById(R.id.main_fragment) != null) {
                    FrameLayout frameLayout = (FrameLayout) findViewById(R.id.main_fragment);
                    frameLayout.setVisibility(View.VISIBLE);
                    fragment = true;
                    int position = 0;
                    Bundle bundle = new Bundle();
                    bundle.putInt("position", position);
                    Fragment fragment = new DetailActivityFragment();
                    fragment.setArguments(bundle);
                    getFragmentManager().beginTransaction()
                            .add(R.id.main_fragment, fragment)
                            .commit();

                }
            } else if (pos == 2) {
                spin = "one";
                getfavorites favorites = new getfavorites();
                favorites.execute();
            }
        }

        @Override
        public void onNothingSelected(AdapterView <? > arg0) {}

    }

    public class getfavorites extends AsyncTask < Void, Void, Void > {

        SharedPreferences sharedpreferences = getSharedPreferences("Prefs", Context.MODE_PRIVATE);
        int count;@Override
                  protected Void doInBackground(Void...params) {
            favorites.clear();
            list.clear();
            count = 0;
            Map < String, ?> keys = sharedpreferences.getAll();
            for (Map.Entry < String, ?> entry: keys.entrySet()) {
                list.add(entry.getValue().toString());
                count++;
            }
            for (int i = 0; i < list.size(); i++) {
                HttpURLConnection urlConnection = null;
                BufferedReader reader;
                String moviejson;
                String website = "http://api.themoviedb.org/3/movie/" + list.get(i) + "?api_key=";
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
                moviejson = buffer.toString();
                try {
                    getmoviedata(moviejson);
                } catch (JSONException e) {
                    Log.e("Error", e.getMessage(), e);
                    e.printStackTrace();
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void nothing) {
            mAdapter = new GridAdapter(MainActivity.this);
            mRecyclerView.setAdapter(mAdapter);
            if (findViewById(R.id.main_fragment) != null && count == 0) {
                FrameLayout frameLayout = (FrameLayout) findViewById(R.id.main_fragment);
                frameLayout.setVisibility(View.INVISIBLE);
            }
            if (findViewById(R.id.main_fragment) != null && count != 0) {
                fragment = true;
                int position = 0;
                Bundle bundle = new Bundle();
                bundle.putInt("position", position);
                Fragment fragment = new DetailActivityFragment();
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction()
                        .add(R.id.main_fragment, fragment)
                        .commit();

            }

        }

        private Void getmoviedata(String moviejson) throws JSONException {
            Movie movie1;
            JSONObject movie = new JSONObject(moviejson);
            String poster = movie.getString("poster_path");
            String title = movie.getString("title");
            String rating = movie.getString("vote_average");
            String date = movie.getString("release_date");
            String plot = movie.getString("overview");
            String detailposter = movie.getString("backdrop_path");
            int movieid = movie.getInt("id");
            if (sharedpreferences.contains(title)) {
                movie1 = new Movie(poster, title, rating, date, plot, detailposter, movieid, 1);
            } else {
                movie1 = new Movie(poster, title, rating, date, plot, detailposter, movieid, 0);
            }
            favorites.add(movie1);


            return null;
        }
    }

    public static List getpopular() {
        return popular;
    }

    public static List gettop() {
        return top;
    }

    public static List getfavorites() {
        return favorites;
    }

    public static String getspin() {
        return spin;
    }

    public void switchContent(int id, Fragment fragment) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(id, fragment, fragment.toString());
        ft.addToBackStack(null);
        ft.commit();
    }

    public class getpopularmovies extends AsyncTask < Void, Void, Void > {


        @Override
        protected Void doInBackground(Void...params) {
            popular.clear();
            HttpURLConnection urlConnection = null;
            BufferedReader reader;
            String moviejson;
            String website = "http://api.themoviedb.org/3/discover/movie?primary_release_year=" + year + "&sort_by=popularity.desc&language=en&include_video=false&api_key=";
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
            moviejson = buffer.toString();
            try {
                return getmoviedata(moviejson);
            } catch (JSONException e) {
                Log.e("Error", e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void nothing) {
            mAdapter = new GridAdapter(MainActivity.this);
            mRecyclerView.setAdapter(mAdapter);
            oncreate = 1;
            if (findViewById(R.id.main_fragment) != null && !favorites1) {
                FrameLayout frameLayout = (FrameLayout) findViewById(R.id.main_fragment);
                frameLayout.setVisibility(View.VISIBLE);
                fragment = true;
                int position = 0;
                Bundle bundle = new Bundle();
                bundle.putInt("position", position);
                Fragment fragment = new DetailActivityFragment();
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction()
                        .add(R.id.main_fragment, fragment)
                        .commit();

            }
        }

        private Void getmoviedata(String moviejson) throws JSONException {
            SharedPreferences sharedpreferences = getSharedPreferences("Prefs", Context.MODE_PRIVATE);
            Movie movie1;
            JSONObject bigobject = new JSONObject(moviejson);
            JSONArray results = bigobject.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject movie = results.getJSONObject(i);
                String poster = movie.getString("poster_path");
                String title = movie.getString("title");
                String rating = movie.getString("vote_average");
                String date = movie.getString("release_date");
                String plot = movie.getString("overview");
                String detailposter = movie.getString("backdrop_path");
                int movieid = movie.getInt("id");
                if (sharedpreferences.contains(title)) {
                    movie1 = new Movie(poster, title, rating, date, plot, detailposter, movieid, 1);
                } else {
                    movie1 = new Movie(poster, title, rating, date, plot, detailposter, movieid, 0);
                }
                popular.add(movie1);

            }
            return null;
        }

    }

    public class top extends AsyncTask < Void, Void, Void > {

        @Override
        protected Void doInBackground(Void...params) {
            top.clear();
            HttpURLConnection urlConnection = null;
            BufferedReader reader;
            String moviejson;
            String website = "http://api.themoviedb.org/3/discover/movie?primary_release_year=" + year + "&vote_count.gte=50&vote_average.gte=7.0&include_video=false&sort_by=vote_average.desc&language=en&api_key=";
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
            moviejson = buffer.toString();
            try {
                return getmoviedata(moviejson);
            } catch (JSONException e) {
                Log.e("Error", e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void nothing) {}

        private Void getmoviedata(String moviejson) throws JSONException {
            SharedPreferences sharedpreferences = getSharedPreferences("Prefs", Context.MODE_PRIVATE);
            Movie movie1;
            JSONObject bigobject = new JSONObject(moviejson);
            JSONArray results = bigobject.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject movie = results.getJSONObject(i);
                String poster = movie.getString("poster_path");
                String title = movie.getString("title");
                String rating = movie.getString("vote_average");
                String date = movie.getString("release_date");
                String plot = movie.getString("overview");
                String detailposter = movie.getString("backdrop_path");
                int movieid = movie.getInt("id");
                if (sharedpreferences.contains(title)) {
                    movie1 = new Movie(poster, title, rating, date, plot, detailposter, movieid, 1);
                } else {
                    movie1 = new Movie(poster, title, rating, date, plot, detailposter, movieid, 0);
                }
                top.add(movie1);

            }
            return null;
        }

    }
}