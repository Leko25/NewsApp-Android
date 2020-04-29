package lasdot.com.ui.headlines;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import lasdot.com.DateToZoneTimeString;
import lasdot.com.TruncateString;

import lasdot.com.R;

public class TechnologyFragment extends Fragment {

    private static int WORD_LENGTH = 13;

    protected RecyclerView technologyListView;

    HeadlineListObject technologyListObject;

    public TechnologyFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_technology, container, false);
        technologyListView = view.findViewById(R.id.technologyListView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        technologyListView.setLayoutManager(linearLayoutManager);

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, "http://35.188.11.46:3000/results/technology?source=guardian", null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // the response is already constructed as a JSONObject!
                        try {
                            response = response.getJSONObject("response");
                            JSONArray resultItems = response.getJSONArray("results");
                            for (int i = 0; i < resultItems.length(); i++) {
                                JSONObject resultItem = (JSONObject) resultItems.get(i);
                                Log.i("SECTION NAME", resultItem.getString("sectionName"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new  Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(view.getContext());
        requestQueue.add(jsonRequest);

        return view;
    }

    private String fetImageURL(JSONObject resultItem) {
        try {
            JSONArray elements = resultItem.getJSONObject("blocks")
                    .getJSONObject("main")
                    .getJSONArray("elements");

            for (int j = 0; j < elements.length(); j++) {
                JSONObject elementItem = (JSONObject) elements.get(j);
                JSONArray assets = elementItem.getJSONArray("assets");
                for (int k = 0; k < assets.length(); k++) {
                    JSONObject asset = (JSONObject) assets.get(k);
                    JSONObject typeData = asset.getJSONObject("typeData");
                    if (typeData.getInt("width") >= 2000) {
                        return asset.getString("file");
                    }
                }
            }

            return "https://assets.guim.co.uk/images/eada8aa27c12fe2d5afa3a89d3fbae0d/fallback-logo.png";
        } catch (Exception e) {
            e.printStackTrace();
            return "https://assets.guim.co.uk/images/eada8aa27c12fe2d5afa3a89d3fbae0d/fallback-logo.png";
        }
    }

    public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                InputStream in = conn.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(in);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

}
