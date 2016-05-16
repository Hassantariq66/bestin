package com.example.hamza.bestin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class MovieDescription extends ActionBarActivity {


    public static ArrayList<TopMovies> array = new ArrayList<TopMovies>();
    public static ArrayAdapter movieArrayAdapter;
    String title="";
    ProgressDialog progressDialog;
    public  static Bitmap bitimage;
   static public String id=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_description);


         id=getIntent().getStringExtra("id");
        if(id!=null)
        {
            getData();
        }




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_description, menu);
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
    public void ChangeContent() {
    }




    //get Data Code

    public void getData( ) {
        //  new HttpAsyncTask().execute("http://bsef12m515metro.apphb.com/api/API?type="+ty);
        //    new HttpAsyncTask().execute("http://www.omdbapi.com/?t=sherlock&y=&plot=short&r=json");
        String search="http://www.myapifilms.com/imdb?idIMDB="+id+"&format=JSON&aka=0&business=0&seasons=0&seasonYear=0&technical=0&lang=en-us&actors=N&biography=0&trailer=0&uniqueName=0&filmography=0&bornDied=0&starSign=0&actorActress=0&actorTrivia=0&movieTrivia=0&awards=0&moviePhotos=N&movieVideos=N&token=cc0a3ead-6375-4e89-adf8-e6fbd8f2bc59&similarMovies=0";
        new HttpAsyncTask().execute(search);


    }
    public void gotoSearch(View view)
    {

        Intent intent = new Intent(getApplicationContext(),Serach.class);
        startActivity(intent);
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
        String result = "{ \"Movie\":[";
        while ((line = bufferedReader.readLine()) != null)
        {
            result += line;
        }
        result=result+"]}";


        inputStream.close();
        return result;

    }

    public class HttpAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(MovieDescription.this, "Loading", "Fetching Movie", true);
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
                    try {
                        JSONObject oneObject = jArray.getJSONObject(i);
                        TopMovies pTemp = new TopMovies();
                        // Pulling items from the array

                        pTemp.setTitle(oneObject.getString("title"));
                        //     pTemp.setRanking(Integer.parseInt(oneObject.getString("ranking")));
                        pTemp.setRating(Double.parseDouble(oneObject.getString("rating")));
                        pTemp.setIdIMDB(oneObject.getString("idIMDB"));
                        pTemp.setUrlPoster(oneObject.getString("urlPoster"));
                        pTemp.setYear(oneObject.getString("year"));
                        pTemp.setSimplePlot(oneObject.getString("plot"));
                        pTemp.setVotes(oneObject.getString("votes"));

                        array.add(pTemp);

                    } catch (Exception e) {


                    }
            }
            catch(Exception e)
            {
            }
            changeContent();
        }
        public void changeContent() {
            if(array.size()>0)
            {
                TopMovies movie=array.get(0);
                TextView title=(TextView)findViewById(R.id.mymovietitle);
               ImageView image=(ImageView)findViewById(R.id.mymovieImage);
                TextView rating=(TextView)findViewById(R.id.mymovieRating);
                TextView votes=(TextView)findViewById(R.id.mymovieVotes);
                TextView plot=(TextView)findViewById(R.id.mymoviePlot);
                TextView year=(TextView)findViewById(R.id.mymovieYear);

                title.setText(movie.getTitle());
                rating.setText(rating.getText() + Double.toString(movie.getRating()));
                votes.setText(votes.getText() + movie.getVotes());
                plot.setText(plot.getText()+ movie.getSimplePlot());
                year.setText(year.getText()+ movie.getYear());
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
                                ImageView image=(ImageView)findViewById(R.id.mymovieImage);
                                image.setImageDrawable(new BitmapDrawable(getResources(), bitmap));
                            }
                        }
                    }.execute(movie.getUrlPoster());
                }

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




}
