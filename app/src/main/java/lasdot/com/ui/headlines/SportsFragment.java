package lasdot.com.ui.headlines;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

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

public class SportsFragment extends Fragment {

    private SectionListObject sportsListObject;

    private int WORD_LENGTH = 13;

    private RecyclerView sportsListView;

    private SwipeRefreshLayout swipeRefreshLayout;

    private ProgressBar progressBar;

    private TextView fetchingNews;

    public SportsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_sports, container, false);
        sportsListView = view.findViewById(R.id.sportsListView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        sportsListView.setLayoutManager(linearLayoutManager);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshSportsFrag);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchSportsNews(view);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        progressBar = view.findViewById(R.id.sportsProgressBar);
        fetchingNews = view.findViewById(R.id.sportsFetchingTextView);

        fetchSportsNews(view);
        return view;
    }

    private void fetchSportsNews(View view) {
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, "http://35.188.11.46:3000/results/sport?source=guardian", null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // the response is already constructed as a JSONObject!
                        try {
                            response = response.getJSONObject("response");
                            JSONArray resultItems = response.getJSONArray("results");
                            sportsListObject = new SectionListObject();
                            for (int i = 0; i < resultItems.length(); i++) {
                                JSONObject resultItem = (JSONObject) resultItems.get(i);

                                // Add Title and Long title
                                sportsListObject.newsTitleLong.add(resultItem.getString("webTitle"));
                                TruncateString truncateString = new TruncateString(resultItem.getString("webTitle"), WORD_LENGTH);
                                sportsListObject.newsTitle.add(truncateString.getTruncation());

                                sportsListObject.webURL.add(resultItem.getString("webUrl"));

                                //Add Time
                                DateToZoneTimeString dateToZoneTimeString = new DateToZoneTimeString(resultItem.getString("webPublicationDate"));
                                sportsListObject.newsTime.add(dateToZoneTimeString.getZoneTimeString());

                                //Add Section
                                sportsListObject.newsSection.add(resultItem.getString("sectionName"));

                                //Get image
                                String image = fetchImageURL(resultItem);
                                ImageDownloader imageDownloader = new ImageDownloader();
                                Bitmap img = imageDownloader.execute(image).get();
                                sportsListObject.newsImage.add(img);

                                //Get articleId
                                sportsListObject.articleId.add(resultItem.getString("id"));
                            }
                            if (sportsListObject.newsTitle.size() != 0) {
                                progressBar.setVisibility(View.GONE);
                                fetchingNews.setVisibility(View.GONE);
                            }
                            SectionCustomAdapter headlineCustomAdapter = new SectionCustomAdapter(getContext(), sportsListObject);
                            sportsListView.setAdapter(headlineCustomAdapter);
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
    }

    private String fetchImageURL(JSONObject resultItem) {
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
                return BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
