package io.krumbs.sdk.starter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.algolia.search.saas.APIClient;
import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.IndexQuery;
import com.algolia.search.saas.Query;
import com.algolia.search.saas.TaskParams;
import com.algolia.search.saas.listeners.APIClientListener;
import com.algolia.search.saas.listeners.GetObjectsListener;
import com.algolia.search.saas.listeners.IndexingListener;
import com.algolia.search.saas.listeners.SearchListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class View_Pictures extends Activity implements SearchListener {
    DatabaseHelper dh = new DatabaseHelper(this);
    ListView list;
    LazyAdapter adapter;

    String[] namesArr = {""};
    String[] descriptionArr = {""};
    String[] userTypeArr = {""};
    String[] dates = {""};

    String user_query;
    Index index;
    String username;

    SearchResultsJsonParser resultsParser;

    Query query2;

    ArrayList<String> imageUrls= new ArrayList<>();
    ArrayList<String> descriptionTxt= new ArrayList<>();
    ArrayList<String> userType= new ArrayList<>();
    ArrayList<String> dateOfPic= new ArrayList<>();
    ArrayList<String> userNames = new ArrayList<>();
    TextView textView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view__pictures);

        APIClient client = new APIClient("60E5NAPN1Y", "4ec446a188edc67cb1ed3d212cb23217");
        index = client.initIndex("Viznotes4");
        Bundle bundle = getIntent().getExtras();

        username = bundle.getString("username");
        user_query = bundle.getString("query");

        resultsParser = new SearchResultsJsonParser();
        query2 = new Query(user_query);


        list = (ListView) findViewById(R.id.listView1);

        textView = (TextView) findViewById(R.id.textViewQuery);

        search();

    }


    @Override
    public void onDestroy() {
        list.setAdapter(null);
        super.onDestroy();
    }


    private void search() {
        query2.setQueryString(user_query);
        index.searchASync(query2, new SearchListener() {
            @Override
            public void searchResult(Index index, Query query, JSONObject jsonObject) {
                JSONArray finalJson = resultsParser.parseResults(jsonObject);
                try {
                    for(int i=0; i<finalJson.length();i++){
                        JSONObject jo = finalJson.getJSONObject(i);

                            JSONArray media = jo.getJSONArray("media");
                            JSONObject des = media.getJSONObject(0);
                            ///GET THE CAPTION
                            String description = des.getString("caption");

                            ///HIGHLIGHT RESULT.
                            JSONObject highlightResult = jo.getJSONObject("_highlightResult");
                            JSONArray media_array = highlightResult.getJSONArray("media");
                            JSONObject totalJSON = media_array.getJSONObject(0);

                            JSONObject mediaSourceObject = totalJSON.getJSONObject("media_source");
                            JSONObject default_source = mediaSourceObject.getJSONObject("default_src");
                            String picture_url = default_source.getString("value");
                            Log.i("KRUMBS_PICURL", dh.allURLSFROMUSERNAME(picture_url).trim());
                            Log.i("KRUMBS_USER",username.trim());
                            if(username.trim().equals(dh.allURLSFROMUSERNAME(picture_url).trim())) {
                                descriptionTxt.add(description);
                                ///GET THE TIME OF THE PICTURE
                                JSONObject when_object = totalJSON.getJSONObject("when");
                                JSONObject start_time = when_object.getJSONObject("start_time");
                                String valueOfStartTime = start_time.getString("value");
                                dateOfPic.add(valueOfStartTime);
                                ///GET THE LOCATION OF THE PICTURE
                                JSONObject whereObject = totalJSON.getJSONObject("where");
                                JSONArray revegoPlaces = whereObject.getJSONArray("revgeo_places");
                                JSONObject addressBlock = revegoPlaces.getJSONObject(0);
                                JSONObject unformattedAddress = addressBlock.getJSONObject("unformatted_address");
                                String valueOfAddress = unformattedAddress.getString("value");


                                //GET THE EMOJI/INTENT USED
                                JSONArray why = totalJSON.getJSONArray("why");
                                JSONObject intent_used_synonym = why.getJSONObject(0);
                                JSONObject intent_used_synonyms = intent_used_synonym.getJSONObject("intent_used_synonym");
                                String valueOfSynonym = intent_used_synonyms.getString("value");
                                userType.add(valueOfSynonym);
                                JSONObject intent_category_name = intent_used_synonym.getJSONObject("intent_category_name");
                                String valueOfCategory = intent_category_name.getString("value");


                                imageUrls.add(picture_url);
                                Log.i("KRUMBS_", imageUrls.toString());
                            }

                    }
                    descriptionArr = descriptionTxt.toArray(new String[descriptionTxt.size()]);
                    namesArr = imageUrls.toArray(new String[imageUrls.size()]);
                    userTypeArr = userType.toArray(new String[userType.size()]);
                    dates = dateOfPic.toArray(new String[dateOfPic.size()]);
                    textView.setText("Total pictures found: " + String.valueOf(descriptionArr.length));

                    adapter = new LazyAdapter(View_Pictures.this, namesArr, descriptionArr, userTypeArr, dates);
                    Toast.makeText(View_Pictures.this,"Hello",Toast.LENGTH_LONG);
                    list.setAdapter(adapter);
                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View view,
                                                final int position, long id) {
                            dh.updateTimesClicked(namesArr[position]);
                            Intent i = new Intent(View_Pictures.this, ViewSelectedPicture.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("url", namesArr[position]);
                            bundle.putString("username", username);
                            bundle.putString("date", dates[position]);
                            bundle.putString("query", user_query);
                            i.putExtras(bundle);
                            list.setVisibility(View.INVISIBLE);
                            startActivity(i);
                            finish();

                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void searchError(Index index, Query query, AlgoliaException e) {

            }
        });
    }

    @Override
    public void searchResult(Index index, Query query, JSONObject jsonObject) {

    }

    @Override
    public void searchError(Index index, Query query, AlgoliaException e) {

    }
}