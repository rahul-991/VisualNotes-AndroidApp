package io.krumbs.sdk.starter;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Message;
import android.util.JsonWriter;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.krumbs.sdk.data.model.User;

/**
 * Created by Rahul on 3/8/2016.
 */
public class JSONWriter {
    public JSONObject stringsToJson(String id, String intent_name, String photo_url, String description) throws FileNotFoundException {
        JSONObject jsonObj = new JSONObject();

        try {
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(jsonObj.put("id", id)); // Set the first name/pair
            jsonArray.put(jsonObj.put("intent_name", intent_name));
            jsonArray.put(jsonObj.put("photo_url", photo_url));
            jsonArray.put(jsonObj.put("description", description));

        } catch (JSONException e) {

        }

        return jsonObj;


    }

    }


