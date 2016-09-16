package com.feridgoranatanas.projectpinkifinki;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AllRunsActivity extends AppCompatActivity {

    private List<Run> runs;
    private String username;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_runs);
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "All runs will be filled when an Internet connection is available", Toast.LENGTH_LONG).show();
        }
        handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (!isNetworkAvailable())
                    handler.postDelayed(this, 1000);
                else
                {
                    runs = new ArrayList<>();
                    username = getSharedPreferences("username", Context.MODE_PRIVATE).getString("username", "");

                    MyAsyncTask myAsyncTask = new MyAsyncTask(AllRunsActivity.this, ServiceClient.BASE_URL + "get.php?username=" + username);
                    myAsyncTask.execute();
                }
            }
        };
        handler.post(runnable);
    }

    private class MyAsyncTask extends AsyncTask<Void, Void, String> {
        private Context mContext;
        private String mUrl;

        public MyAsyncTask(Context context, String url) {
            mContext = context;
            mUrl = url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            String json = getJSON(mUrl);
            return json;
        }

        @Override
        protected void onPostExecute(String strings) {
            super.onPostExecute(strings);
            fillList(strings);
            Collections.reverse(runs);
            ListView runsList = (ListView) findViewById(R.id.lvRuns);
            runsList.setAdapter(new CustomAdapter(AllRunsActivity.this, runs));
            runsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent viewRun = new Intent(AllRunsActivity.this, ViewRunActivity.class);
                    viewRun.putExtra("run", runs.get(position));
                    startActivity(viewRun);
                }
            });
        }

        private String getJSON(String url) {
            HttpURLConnection c = null;
            try {
                URL u = new URL(url);
                c = (HttpURLConnection) u.openConnection();
                c.connect();
                int status = c.getResponseCode();
                switch (status) {
                    case 200:
                    case 201:
                        BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            sb.append(line+"\n");
                        }
                        br.close();
                        return sb.toString();
                }

            } catch (Exception ex) {
                return ex.toString();
            } finally {
                if (c != null) {
                    try {
                        c.disconnect();
                    } catch (Exception ex) {
                        //disconnect error
                    }
                }
            }
            return null;
        }

        private void fillList(String jsonArrayString) {
            try {
                JSONArray response = new JSONArray((jsonArrayString));
                for (int i = 0; i < response.length(); ++i) {
                    JSONObject run = response.getJSONObject(i);
                    Run newRun = new Run();

                    String date = run.getString("date");
                    newRun.setDate(date);

                    double distance = run.getDouble("distance");
                    newRun.setDistance(distance);

                    int time = run.getInt("time");
                    newRun.setSeconds(time);

                    String jsonArray = run.getString("coords");
                    jsonArray = jsonArray.replace("\\", "");
                    if (jsonArray.charAt(0) != '[') {
                        runs.add(newRun);
                        continue;
                    }
                    JSONArray coords = new JSONArray(jsonArray);
                    for (int j = 0; j < coords.length(); ++j) {
                        JSONObject coord = coords.getJSONObject(j);
                        double lat = coord.getDouble("lat");
                        double lng = coord.getDouble("lng");
                        LatLng coordinate = new LatLng(lat, lng);
                        newRun.addCoord(coordinate);
                    }
                    runs.add(newRun);
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
