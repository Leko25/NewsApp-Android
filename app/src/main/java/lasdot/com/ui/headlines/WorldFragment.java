package lasdot.com.ui.headlines;

import android.annotation.SuppressLint;
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

import android.os.Handler;
import android.os.Message;
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


import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import lasdot.com.DateToZoneTimeString;
import lasdot.com.R;
import lasdot.com.TruncateString;

public class WorldFragment extends Fragment {
    private static int WORD_LENGTH = 13;

    protected RecyclerView worldListView;

    HeadlineListObject worldListObject;

    public WorldFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_world, container, false);

        worldListView = view.findViewById(R.id.worldListView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        worldListView.setLayoutManager(linearLayoutManager);

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, "http://35.188.11.46:3000/results/world?source=guardian", null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // the response is already constructed as a JSONObject!
                        try {
                            response = response.getJSONObject("response");
                            JSONArray resultItems = response.getJSONArray("results");
                            worldListObject = new HeadlineListObject();
                            for (int i = 0; i < resultItems.length(); i++) {
                                JSONObject resultItem = (JSONObject) resultItems.get(i);
                                worldListObject.newsTitleLong.add(resultItem.getString("webTitle"));
                                TruncateString truncateString = new TruncateString(resultItem.getString("webTitle"), WORD_LENGTH);
                                worldListObject.newsTitle.add(truncateString.getTruncation());

                                //Add Time
                                DateToZoneTimeString dateToZoneTimeString = new DateToZoneTimeString(resultItem.getString("webPublicationDate"));
                                worldListObject.newsTime.add(dateToZoneTimeString.getZoneTimeString());

                                //Add Section
                                worldListObject.newsSection.add(resultItem.getString("sectionName"));

                                //Get image
                                String image = fetchImageURL(resultItem);
                                ImageDownloader imageDownloader = new ImageDownloader();
                                Bitmap img = imageDownloader.execute(image).get();
                                worldListObject.newsImage.add(img);

                                //Get articleId
                                worldListObject.articleId.add(resultItem.getString("id"));
                            }
                            HeadlineCustomAdapter headlineCustomAdapter = new HeadlineCustomAdapter(getContext(), worldListObject);
                            worldListView.setAdapter(headlineCustomAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
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

    private String fetchImageURL(JSONObject resultItem) {
        Log.i("RESULT ITEM", resultItem.toString());
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