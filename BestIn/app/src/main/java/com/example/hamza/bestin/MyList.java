package com.example.hamza.bestin;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;


public class MyList extends ActionBarActivity {
    MovieSqLiteOpenHelper openHelper;
    SQLiteDatabase db;

    public static ArrayList<TopMovies> array = new ArrayList<TopMovies>();
    public static ArrayAdapter movieArrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_list);
        openHelper = new MovieSqLiteOpenHelper(this);
        db = openHelper.getWritableDatabase();
        Intent i = getIntent();

        array = (ArrayList<TopMovies>)(i.getSerializableExtra("list"));
        createList();



    }
    public void randomselect(View view)
    {
        if(array.size()>0) {
            int randomMovie = randInt(0, array.size());

            Intent intent = new Intent(getApplicationContext(), MovieDescription.class);
            intent.putExtra("id", ((TopMovies) array.get(randomMovie)).getIdIMDB());
          //  MyList.movieArrayAdapter.clear();
          //  Serach.temparray.clear();
            startActivity(intent);
        }
        else
            Toast.makeText(getBaseContext(), "Your List is Empty", Toast.LENGTH_LONG).show();
    }

    public boolean isExists(TopMovies movieObj)
    {

        ArrayList<TopMovies> lst=Serach.getdbData();
        for(TopMovies m : lst)
        {
            if(movieObj.getIdIMDB().equals(m.getIdIMDB()))
                return true;
        }
        return false;
    }

    public void givesuggestion(View view)
    {
        Intent intent = new Intent(getApplicationContext(), MovieDescription.class);
        if(array.size()>0) {
            TopMovies movieObj=gethighrank();


if(!isExists(movieObj)) {
    ContentValues values = new ContentValues();
    values.put(MovieSqLiteOpenHelper.COLUMN_MOVIE_ID_IMDB, movieObj.getIdIMDB());
    values.put(MovieSqLiteOpenHelper.COLUMN_MOVIE_RANKING, movieObj.getRanking());
    values.put(MovieSqLiteOpenHelper.COLUMN_MOVIE_RATING, movieObj.getRating());
    values.put(MovieSqLiteOpenHelper.COLUMN_MOVIE_TITLE, movieObj.getTitle());
    values.put(MovieSqLiteOpenHelper.COLUMN_MOVIE_URL_POSTER, movieObj.getUrlPoster());
    values.put(MovieSqLiteOpenHelper.COLUMN_MOVIE_YEAR, movieObj.getYear());
    values.put(MovieSqLiteOpenHelper.COLUMN_MOVIE_SIMPLE_PLOT, movieObj.getSimplePlot());
    values.put(MovieSqLiteOpenHelper.COLUMN_MOVIE_VOTES, movieObj.getVotes());

    long movieId = db.insert(MovieSqLiteOpenHelper.TABLE_MOVIE, null, values);
    movieObj.setId((int) movieId);
}
            // fetchAllTodos();

            // movieArrayAdapter.add(movieObj);
            MyList.movieArrayAdapter.remove(movieObj);
            for(TopMovies m : Serach.temparray) {
                if(m.getIdIMDB().equals(movieObj.getIdIMDB()))
                Serach.temparray.remove(m);
            }
            intent.putExtra("id", movieObj.getIdIMDB());
             startActivity(intent);
        }
        else
            Toast.makeText(getBaseContext(), "Your List is Empty", Toast.LENGTH_LONG).show();

    }

    public static int randInt(int min, int max) {

        // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum -1;
    }
    public static TopMovies gethighrank() {


        TopMovies movie=new TopMovies();
        if(array.size()>0)
        {
            movie=array.get(0);

        }
        for(TopMovies a:array )
        {
            if(a.getRating()>movie.getRating())
            {
                movie=a;
            }
        }
        return movie;
    }





    public void createList() {
        try {
            ListView listView = (ListView) findViewById(R.id.myMovieList);
            movieArrayAdapter = new ProArrayAdapter(this, R.layout.topmovie_cell, array);
            listView.setAdapter(movieArrayAdapter);

        } catch (Exception e) {
            Log.d("ERROR", "LIST", e);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    class ProArrayAdapter extends ArrayAdapter<TopMovies> {
        private int layoutID;
        private ArrayList<TopMovies> movieLIst;

        public ProArrayAdapter(Context context, int layoutID, ArrayList<TopMovies> arrayList) {
            super(context, layoutID, arrayList);
            this.layoutID = layoutID;
            this.movieLIst= arrayList;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            View row = convertView;

            row = getLayoutInflater().inflate(this.layoutID, parent, false);


            TextView title = (TextView)row.findViewById(R.id.titleMovie);
            TextView rating = (TextView)row.findViewById(R.id.ratingMovie);
            TextView ranking = (TextView)row.findViewById(R.id.ranking);
           final ImageView image=(ImageView)row.findViewById(R.id.movieImage);

            final TopMovies p = this.movieLIst.get(position);
            title.setText(p.getTitle());

            //image code


            if ( isConnected() ) {
                new AsyncTask<String, String, Bitmap>() {
                    @Override
                    protected Bitmap doInBackground(String... params) {
                        Bitmap b = null;
                        try {
                            b = BitmapFactory.decodeStream((InputStream) new URL(params[0]).getContent());
                        } catch (Exception e) {
                            // Toast.makeText, "Image Loading Failed", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                        return b;
                    }

                    @Override
                    protected void onPostExecute(Bitmap bitmap) {
                        super.onPostExecute(bitmap);
                        if (bitmap != null) {
                            //  ImageView image=(ImageView)findViewById(R.id.movieImage);
                            image.setImageDrawable(new BitmapDrawable(getResources(), bitmap));
                        }
                    }
                }.execute(p.getUrlPoster());
            }


            //  rating.setText("Rating:"+String.valueOf(p.getRating()));
            /// ranking.setText("Ranking:" + String.valueOf(p.getRanking()));




            return row;
        }
    }
    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }
}
