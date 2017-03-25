/*
 * Copyright (c) 2016 Krumbs Inc
 * All rights reserved.
 *
 */
package io.krumbs.sdk.starter;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.algolia.search.saas.APIClient;
import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.TaskParams;
import com.algolia.search.saas.listeners.IndexingListener;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.krumbs.sdk.KrumbsSDK;
import io.krumbs.sdk.krumbscapture.KCaptureCompleteListener;


public class MainActivity extends AppCompatActivity implements IndexingListener {
    DatabaseHelper dh = new DatabaseHelper(this);
    private View cameraView;
    private View startCaptureButton;
    private View viewPics;
    private View textSearch;
    private EditText autoCompleteTextView;
    String username;
    JSONWriter jw = new JSONWriter();
    String total_string="";
    Index index;
    IndexingListener indexingListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        APIClient client = new APIClient("60E5NAPN1Y", "26386416ba1ff4b2fa056ee727d249c6");
        index = client.initIndex("Viznotes4");



        startCaptureButton = findViewById(R.id.kcapturebutton);

        viewPics = findViewById(R.id.buttonView);
        cameraView = findViewById(R.id.camera_container);
        textSearch = findViewById(R.id.searchText);
        autoCompleteTextView = (EditText) findViewById(R.id.autoCompleteTextView);


        Bundle bundle = getIntent().getExtras();

        username = bundle.getString("username");

        startCaptureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCaptureButton.setVisibility(View.INVISIBLE);
                cameraView.setVisibility(View.VISIBLE);
                viewPics.setVisibility(View.INVISIBLE);
                textSearch.setVisibility(View.INVISIBLE);
                autoCompleteTextView.setVisibility(View.INVISIBLE);
                startCapture();
            }
        });

    }
    @Override
    public void indexingResult(Index index, TaskParams.Indexing indexing, JSONObject jsonObject) {

    }

    @Override
    public void indexingError(Index index, TaskParams.Indexing indexing, AlgoliaException e) {

    }
    private void startCapture() {

        int containerId = R.id.camera_container;
// SDK usage step 4 - Start the K-Capture component and add a listener to handle returned images and URLs
        KrumbsSDK.startCapture(containerId, this, new KCaptureCompleteListener() {
            @Override
            public void captureCompleted(CompletionState completionState, boolean audioCaptured, Map<String, Object> map) {
                // DEBUG LOG
                if (completionState != null) {
                    Log.d("KRUMBS-CALLBACK", completionState.toString());
                }
                if (completionState == CompletionState.CAPTURE_SUCCESS) {
// The local image url for your capture
                    String imagePath = (String) map.get(KCaptureCompleteListener.CAPTURE_MEDIA_IMAGE_PATH);
                    if (audioCaptured) {
// The local audio url for your capture (if user decided to record audio)
                        String audioPath = (String) map.get(KCaptureCompleteListener.CAPTURE_MEDIA_AUDIO_PATH);
                        String mediaJSONUrl = (String) map.get(KCaptureCompleteListener.CAPTURE_MEDIA_JSON_URL);
                        Log.i("KRUMBS-CALLBACK", mediaJSONUrl + ", " + imagePath);

                        cameraView.setVisibility(View.INVISIBLE);
                        startCaptureButton.setVisibility(View.VISIBLE);
                        viewPics.setVisibility(View.VISIBLE);
                        textSearch.setVisibility(View.VISIBLE);
                        autoCompleteTextView.setVisibility(View.VISIBLE);
                        new JSONTask().execute(mediaJSONUrl);

                    }
                    if (!audioCaptured) {
                        String mediaJSONUrl = (String) map.get(KCaptureCompleteListener.CAPTURE_MEDIA_JSON_URL);
                        Log.i("KRUMBS-CALLBACK", mediaJSONUrl + ", " + imagePath);

                        cameraView.setVisibility(View.INVISIBLE);
                        startCaptureButton.setVisibility(View.VISIBLE);
                        viewPics.setVisibility(View.VISIBLE);
                        textSearch.setVisibility(View.VISIBLE);
                        autoCompleteTextView.setVisibility(View.VISIBLE);
                        new JSONTask().execute(mediaJSONUrl);
                    }


                } else if (completionState == CompletionState.CAPTURE_CANCELLED ||
                        completionState == CompletionState.SDK_NOT_INITIALIZED) {
                    cameraView.setVisibility(View.INVISIBLE);
                    startCaptureButton.setVisibility(View.VISIBLE);
                    viewPics.setVisibility(View.VISIBLE);

                    textSearch.setVisibility(View.VISIBLE);
                    autoCompleteTextView.setVisibility(View.VISIBLE);

                }
            }
        });

    }

    public void View_Pictures(View view) {

        String query = autoCompleteTextView.getText().toString();
        query = query.trim().toLowerCase();
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        //bundle.putString("query", query);
        Intent i = new Intent(MainActivity.this, View_Pictures.class);

        i.putExtras(bundle);
        startActivity(i);
    }
    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Sorry cannot go back", Toast.LENGTH_LONG).show();
    }

    public void goBack(View view) {
        Intent i = new Intent(this, SignUp.class);
        startActivity(i);
    }




    public class JSONTask extends AsyncTask<String, String, ArrayList<String> > {
        Context context;

        @Override
        protected ArrayList<String> doInBackground(String... params) {
            ArrayList<String> list = new ArrayList<String>();
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            StringBuffer buffer = null;
            try{
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));
                buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                String finalJson = buffer.toString();
                JSONObject parentObject = new JSONObject(finalJson);
                JSONArray media = parentObject.getJSONArray("media");
                JSONObject location_array = media.getJSONObject(0);

                JSONObject location_array2 = media.getJSONObject(1);
                JSONObject where = location_array2.getJSONObject("where");
                JSONArray place = where.getJSONArray("revgeo_places");
                JSONObject place_category = place.getJSONObject(0);
                String exact_location = place_category.getString("unformatted_address");

                JSONObject picture_url = location_array.getJSONObject("media_source");

                String picture_URL = picture_url.getString("default_src").toString();
                Log.i("KRUMBS-PIC", picture_URL);

                String description = location_array.getString("caption");

                JSONObject time = location_array.getJSONObject("when");
                String start_time = time.getString("start_time");

                JSONArray why = location_array.getJSONArray("why");
                JSONObject category_name = why.getJSONObject(0);

                String user_category_name = category_name.getString("intent_category_name").toString();
                String intent_name = category_name.getString("intent_name").toString();
                String id = category_name.getString("intent_emoji_unicode");
                JSONObject location_array_vid = media.getJSONObject(1);
                JSONObject audio_url = location_array_vid.getJSONObject("media_source");

                String audio_URL = audio_url.getString("default_src").toString();
                String exact_date = start_time.substring(0, 10);
                String searchable_data = picture_URL + " " + id  + " "+ user_category_name+ " " + exact_location + " " + audio_URL + " " + description + " " + start_time + " " + intent_name;
                list.add(0,picture_URL);
                list.add(1,audio_URL);
                list.add(2, description);
                list.add(3, user_category_name);
                list.add(4, start_time);
                list.add(5, intent_name);
                list.add(6, id);
                list.add(7,exact_location);
                list.add(8,exact_date);
                list.add(9, searchable_data);
                list.add(10, parentObject.toString());

                return list;



            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }



        @Override
        protected void onPostExecute(ArrayList<String> s) {
            super.onPostExecute(s);

            String picture_URL = s.get(0);
            String audio_URL = s.get(1);
            String user_category_name = s.get(3);
            String description = s.get(2);
            String date = s.get(4);
            String intent_name = s.get(5);
            String id = s.get(6);
            String exact_location = s.get(7);
            String exact_date = s.get(8);
            String searchable_data= s.get(9);
            String json_Object = s.get(10);

            dh.insertPhotoData(picture_URL, audio_URL, user_category_name, description, username, date, 0,intent_name, exact_location,exact_date, searchable_data,id,json_Object);

            List objects = new ArrayList();
            ArrayList<String> jsonObject = dh.allJsonObjects();
            for(int i=0; i<jsonObject.size(); i++){
                String sss = jsonObject.get(i);
                try {
                    JSONObject jsonObject1 = new JSONObject(sss);
                    jsonObject1.put("objectID", jsonObject1.get("id"));
                    objects.add(jsonObject1);
                    index.addObjectASync(jsonObject1, indexingListener);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if(!objects.isEmpty()){
                JSONArray jsonArray = new JSONArray(objects);
                index.saveObjectsASync(jsonArray, indexingListener);
            }


        }


    }
}