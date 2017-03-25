package io.krumbs.sdk.starter;

/**
 * Created by Rahul on 3/10/2016.
 */

        import com.google.gson.JsonArray;
        import com.google.gson.JsonObject;

        import org.json.JSONArray;
        import org.json.JSONObject;

        import java.util.ArrayList;
        import java.util.List;



/**
 * Parses the JSON output of a search query.
 */
public class SearchResultsJsonParser
{
    public JSONArray parseResults(JSONObject jsonObject)
    {
        if (jsonObject == null)
            return null;

        JSONArray hits = jsonObject.optJSONArray("hits");
        if (hits == null)
            return null;
        return hits;
    }

}
