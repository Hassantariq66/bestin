package com.example.hamza.bestin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;


public class Serach extends ActionBarActivity {

    MovieSqLiteOpenHelper openHelper;
   public static SQLiteDatabase db;
    public static ArrayList<TopMovies> array = new ArrayList<TopMovies>();
    public static ArrayList<TopMovies> temparray = new ArrayList<TopMovies>();
    public static ArrayAdapter movieArrayAdapter;

    String title="";
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serach);
        //   movieArrayAdapter.getCount();
        Button b = (Button) findViewById(R.id.mylistButton);
        b.setText("My List("+Integer.toString(temparray.size())+")");
        openHelper = new MovieSqLiteOpenHelper(this);               //////
        db = openHelper.getWritableDatabase();



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_serach, menu);
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

public static ArrayList<TopMovies> getdbData()
{
    Cursor cursor = db.rawQuery("SELECT * FROM " + MovieSqLiteOpenHelper.TABLE_MOVIE, null);
    // db.rawQuery("SELECT * FROM {0}", params);

    ArrayList<TopMovies> temp = new ArrayList<TopMovies>();

    while (cursor.moveToNext()){
        TopMovies movie = new TopMovies();
        int movieIDIndex = cursor.getColumnIndex(MovieSqLiteOpenHelper.COLUMN_MOVIE_ID);
        int movieID = cursor.getInt(movieIDIndex);
        movie.setId(movieID);

        int movieid_imdbIndex = cursor.getColumnIndex(MovieSqLiteOpenHelper.COLUMN_MOVIE_ID_IMDB);
        String movieid_imdb = cursor.getString(movieid_imdbIndex);
        movie.setIdIMDB(movieid_imdb);

        int movieRankingIndex = cursor.getColumnIndex(MovieSqLiteOpenHelper.COLUMN_MOVIE_RANKING);
        int movieRanking = cursor.getInt(movieRankingIndex);
        movie.setRanking(movieRanking);

        int movieRatingIndex = cursor.getColumnIndex(MovieSqLiteOpenHelper.COLUMN_MOVIE_RATING);
        Double movieRating = cursor.getDouble(movieRatingIndex);
        movie.setRating(movieRating);

        int movieTitleIndex = cursor.getColumnIndex(MovieSqLiteOpenHelper.COLUMN_MOVIE_TITLE);
        String movieTitle = cursor.getString(movieTitleIndex);
        movie.setTitle(movieTitle);

        int moviePosterIndex = cursor.getColumnIndex(MovieSqLiteOpenHelper.COLUMN_MOVIE_URL_POSTER);
        String moviePoster = cursor.getString(moviePosterIndex);
        movie.setUrlPoster(moviePoster);



        int movieYearIndex = cursor.getColumnIndex(MovieSqLiteOpenHelper.COLUMN_MOVIE_YEAR);
        String movieYear = cursor.getString(movieYearIndex);
        movie.setYear(movieYear);

        int moviePlotIndex = cursor.getColumnIndex(MovieSqLiteOpenHelper.COLUMN_MOVIE_SIMPLE_PLOT);
        String moviePlot = cursor.getString(moviePlotIndex);
        movie.setSimplePlot(moviePlot);

        int movieVotesIndex = cursor.getColumnIndex(MovieSqLiteOpenHelper.COLUMN_MOVIE_VOTES);
        String movieVotes = cursor.getString(movieVotesIndex);
        movie.setVotes(movieVotes);

        temp.add(movie);
    }
    return temp;
}
    //button click search function..
    public void  Search(View view)
    {
          if(array!=null)
              array.clear();

            ArrayList<TopMovies> temp=getdbData();

            EditText search =(EditText)findViewById(R.id.search_movie);
            String str  =search.getText().toString();
            title=str;


            ArrayList<TopMovies> similar=new ArrayList<TopMovies>();
            boolean found_in_db=false;
            for(TopMovies m : temp)
            {
                if(m.getTitle().equalsIgnoreCase(title))
                { array=new ArrayList<TopMovies>();
                    array.add(m);
                    found_in_db=true;
                    createList();
                }
                else if(m.getTitle().contains(title))
                {
                    similar.add(m);
                }

            }
            if(!found_in_db)
            {
                if(isConnected()){
                    getData();
                    for(TopMovies m : similar)
                        movieArrayAdapter.add(m);
                    createList();
                }
                else{
                    Toast.makeText(getBaseContext(), "You are not connected to internet!", Toast.LENGTH_LONG).show();
                }



            }
    }
    public void  OpenMyList(View view)
    {

        Intent intent = new Intent(getApplicationContext(), MyList.class);
        intent.putExtra("list",temparray);
        startActivity(intent);
    }
    //List Making Code
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
            if (row == null) {
                row = getLayoutInflater().inflate(this.layoutID, parent, false);
            }


            TextView title = (TextView)row.findViewById(R.id.titleMovie);
            TextView rating = (TextView)row.findViewById(R.id.ratingMovie);
            TextView ranking = (TextView)row.findViewById(R.id.ranking);
           final ImageView image = (ImageView)row.findViewById(R.id.movieImage);

            final TopMovies p = this.movieLIst.get(position);
            title.setText(p.getTitle());


            //code of image

            if ( isConnected() ) {
                new AsyncTask<String, String, Bitmap>() {
                    @Override
                    protected Bitmap doInBackground(String... params) {
                        Bitmap b = null;
                        try {
                            b = BitmapFactory.decodeStream((InputStream) new URL(params[0]).getContent());
                        } catch (Exception e) {

                            e.printStackTrace();
                        }
                        return b;
                    }

                    @Override
                    protected void onPostExecute(Bitmap bitmap) {
                        super.onPostExecute(bitmap);
                        if (bitmap != null) {

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
    public void createList() {
        try {
            ListView listView = (ListView) findViewById(R.id.searchList);
            movieArrayAdapter = new ProArrayAdapter(this, R.layout.topmovie_cell, array);
            listView.setAdapter(movieArrayAdapter );
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    //     Toast.makeText(getBaseContext(), array.get(position).getTitle(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), MovieDescription.class);
                    intent.putExtra("id", array.get(position).getIdIMDB());
                    startActivity(intent);

                }
            });
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                    // Toast.makeText(getBaseContext(), array.get(position).getTitle(), Toast.LENGTH_LONG).show();
                    if (checkList(array.get(position))) {
                        temparray.add(array.get(position));
                        Button mylist = (Button) findViewById(R.id.mylistButton);
                      //  String str = mylist.getText().toString();
                       // int incriment = Integer.parseInt(str);
                       // incriment++;
                       // mylist.setText(Integer.toString(incriment));
                    mylist.setText("My List("+temparray.size()+")");
                    } else
                        Toast.makeText(getBaseContext(), "Already in the list...", Toast.LENGTH_LONG).show();


                    return true;
                }
            });
            listView.setLongClickable(true);

        } catch (Exception e) {
            Log.d("ERROR", "LIST", e);
        }

    }
    public boolean checkList(TopMovies movie)
    {
        for(TopMovies m:temparray)
        {
            if(m.getIdIMDB().equals(movie.getIdIMDB()))
            {
                return false;
            }
        }
        return true;
    }



    public void getData( ) {
        //  new HttpAsyncTask().execute("http://bsef12m515metro.apphb.com/api/API?type="+ty);
        //    new HttpAsyncTask().execute("http://www.omdbapi.com/?t=sherlock&y=&plot=short&r=json");
        title=title.replaceAll(" ","");
        String search="http://www.myapifilms.com/imdb?title="+title+"&format=JSON&aka=0&business=0&seasons=0&seasonYear=0&technical=0&filter=M&exactFilter=0&limit=10&forceYear=0&lang=en-us&actors=N&biography=0&trailer=0&uniqueName=0&filmography=0&bornDied=0&starSign=0&actorActress=0&actorTrivia=0&movieTrivia=1&awards=1&moviePhotos=N&movieVideos=N&token=cc0a3ead-6375-4e89-adf8-e6fbd8f2bc59&similarMovies=0";
        new HttpAsyncTask().execute(search);


    }

    public static String GET(String url) {
        InputStream inputStream = null;
        String result = "";
        try {

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if (inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "{ \"Movie\":";
        while ((line = bufferedReader.readLine()) != null)
        {
            result += line;
        }
        result=result+"}";


        inputStream.close();
        return result;

    }

    public class HttpAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(Serach.this, "Loading", "Fetching Movie", true);
        }
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
         //   Toast.makeText(getBaseContext(), result, Toast.LENGTH_LONG).show();

            progressDialog.dismiss();
            progressDialog = null;

            try
            {
                JSONObject jObject = new JSONObject(result);
                JSONArray jArray = jObject.getJSONArray("Movie");
                array.clear();

                for (int i=0; i < jArray.length(); i++)
                {
                    try {
                        JSONObject oneObject = jArray.getJSONObject(i);
                        TopMovies pTemp = new TopMovies();
                        // Pulling items from the array

                        pTemp.setTitle(oneObject.getString("title"));

                        pTemp.setRating(Double.parseDouble(oneObject.getString("rating")));
                        pTemp.setIdIMDB(oneObject.getString("idIMDB"));
                        pTemp.setUrlPoster(oneObject.getString("urlPoster"));
                        pTemp.setYear(oneObject.getString("year"));
                        array.add(pTemp);

                    } catch (Exception e) {


                    }
                }
            }
            catch(Exception e)
            {
            }


            movieArrayAdapter.notifyDataSetChanged();
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






