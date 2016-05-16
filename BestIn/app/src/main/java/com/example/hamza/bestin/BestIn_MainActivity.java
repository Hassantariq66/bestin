package com.example.hamza.bestin;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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


public class BestIn_MainActivity extends ActionBarActivity {


    public static ArrayList<TopMovies> array = new ArrayList<TopMovies>();
    public static ArrayList<Bitmap>images=new ArrayList<Bitmap>();
    public static ArrayAdapter movieArrayAdapter;
    String typeOf="";
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_best_in__main);

        if(isConnected()){
            getData();
            createList();
        }
        else{
            Toast.makeText(getBaseContext(), "You are not connected to internet!", Toast.LENGTH_LONG).show();
        }

    }
    public void  Search(View view)
    {
        Intent intent = new Intent(getApplicationContext(),Serach.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_best_in__main, menu);
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

            row = getLayoutInflater().inflate(this.layoutID, parent, false);


            TextView title = (TextView)row.findViewById(R.id.titleMovie);
            TextView rating = (TextView)row.findViewById(R.id.ratingMovie);
            TextView ranking = (TextView)row.findViewById(R.id.ranking);
            final ImageView image = (ImageView)row.findViewById(R.id.movieImage);
            final TopMovies p = this.movieLIst.get(position);
            title.setText(p.getTitle());
            rating.setText("Rating:"+String.valueOf(p.getRating()));
            ranking.setText("Ranking:" + String.valueOf(p.getRanking()));
            //setting the image


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

            return row;
        }
    }
    public void createList() {
        try {
            ListView listView = (ListView) findViewById(R.id.TopMovies);
            movieArrayAdapter = new ProArrayAdapter(this, R.layout.topmovie_cell, array);
            listView.setAdapter(movieArrayAdapter );
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                   // Toast.makeText(getBaseContext(), array.get(position).getTitle(), Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(getApplicationContext(),MovieDescription.class);
                    intent.putExtra("id",array.get(position).getIdIMDB());
                    startActivity(intent);

                }
            });


        } catch (Exception e) {
            Log.d("ERROR", "LIST", e);
        }

    }


    //getting data Code


    public void getData() {
        //  new HttpAsyncTask().execute("http://bsef12m515metro.apphb.com/api/API?type="+ty);
        //    new HttpAsyncTask().execute("http://www.omdbapi.com/?t=sherlock&y=&plot=short&r=json");
        new HttpAsyncTask().execute("http://www.myapifilms.com/imdb/top?format=JSON&data=S&token=cc0a3ead-6375-4e89-adf8-e6fbd8f2bc59");


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
        String result = "{ \"TopMovies\":";
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
            progressDialog = ProgressDialog.show(BestIn_MainActivity.this, "Loading", "Fetching Top Movies", true);
        }
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

       //     Toast.makeText(getBaseContext(), result, Toast.LENGTH_LONG).show();

            progressDialog.dismiss();
            progressDialog = null;

            try
            {
                JSONObject jObject = new JSONObject(result);
                JSONArray jArray = jObject.getJSONArray("TopMovies");
                array.clear();

                for (int i=0; i < jArray.length(); i++)
                {
                    try {
                        JSONObject oneObject = jArray.getJSONObject(i);
                        TopMovies pTemp = new TopMovies();
                        // Pulling items from the array

                        pTemp.setTitle(oneObject.getString("title"));
                        pTemp.setRanking(Integer.parseInt(oneObject.getString("ranking")));
                        pTemp.setRating(Double.parseDouble(oneObject.getString("rating")));
                        pTemp.setIdIMDB(oneObject.getString("idIMDB"));
                        pTemp.setUrlPoster(oneObject.getString("urlPoster"));
                        pTemp.setYear(oneObject.getString("year"));


                        array.add(pTemp);

                    } catch (Exception e) {

                        // Oops
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
