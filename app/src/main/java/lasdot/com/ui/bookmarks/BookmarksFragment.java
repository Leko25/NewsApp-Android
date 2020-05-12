package lasdot.com.ui.bookmarks;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import java.util.Set;
import java.util.concurrent.ExecutionException;

import lasdot.com.DateToZoneTimeString;
import lasdot.com.R;
import lasdot.com.TruncateString;
import lasdot.com.ui.headlines.SectionCustomAdapter;
import lasdot.com.ui.headlines.SectionListObject;
import lasdot.com.ui.headlines.WorldFragment;
import lasdot.com.ui.home.NewsWeatherListObject;

public class BookmarksFragment extends Fragment {
    private SectionListObject bookmarkListObject;

    private int WORD_LENGTH = 13;

    private RecyclerView bookmarkListView;

    private TextView fetchingFavorites;

    private ProgressBar progressBar;

    private int prevFavCount;

    private BookmarkCustomAdapter bookmarkCustomAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_bookmarks, container, false);

        SharedPreferences sharedPreferences = view.getContext().getSharedPreferences("lasdot.com", Context.MODE_PRIVATE);
        final Set<String> favorites = sharedPreferences.getStringSet("Favorites", null);

        bookmarkListView = view.findViewById(R.id.bookmarkListView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        bookmarkListView.setLayoutManager(gridLayoutManager);
        bookmarkListView.addItemDecoration(new DividerItemDecoration(bookmarkListView.getContext(), DividerItemDecoration.VERTICAL));

        TextView noBookmarkTextView = view.findViewById(R.id.noBookmarkTextView);

        fetchingFavorites = view.findViewById(R.id.bookmarksFetchingTextView);
        progressBar = view.findViewById(R.id.bookmarksProgressBar);

        if (favorites == null || favorites.isEmpty()) {
            bookmarkListView.setVisibility(View.GONE);
            noBookmarkTextView.setVisibility(View.VISIBLE);

            fetchingFavorites.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        } else {
            prevFavCount = favorites.size();
            fetchingFavorites.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            bookmarkListView.setVisibility(View.VISIBLE);

            noBookmarkTextView.setVisibility(View.GONE);

            bookmarkListObject = new SectionListObject();

            for (String id: favorites) {
                fetchFavorites(id, view, favorites.size());
            }
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("lasdot.com", Context.MODE_PRIVATE);
        final Set<String> favorites = sharedPreferences.getStringSet("Favorites", null);

        TextView noBookmarkTextView = getView().findViewById(R.id.noBookmarkTextView);

        if (favorites == null || favorites.isEmpty()) {
            bookmarkListView.setVisibility(View.GONE);
            noBookmarkTextView.setVisibility(View.VISIBLE);
        } else {
            if (bookmarkCustomAdapter != null) {
                Log.i("SIZE", Integer.toString(bookmarkListObject.newsTitle.size()));
                if (bookmarkListObject.newsTitle.size() != favorites.size()) {
                    //set visible
                    bookmarkListView.setVisibility(View.VISIBLE);
                    noBookmarkTextView.setVisibility(View.GONE);

                    //Clear Object contents
                    bookmarkListObject.newsTitle.clear();
                    bookmarkListObject.newsTime.clear();
                    bookmarkListObject.newsTitleLong.clear();
                    bookmarkListObject.newsImage.clear();
                    bookmarkListObject.newsSection.clear();
                    bookmarkListObject.articleId.clear();
                    bookmarkListObject.webURL.clear();

                    //Refresh object data
                    for (String id: favorites) {
                        refresh(id, favorites.size());
                    }
                }
            }
            else {
                if (bookmarkListObject == null) {
                    bookmarkListView.setVisibility(View.VISIBLE);
                    noBookmarkTextView.setVisibility(View.GONE);
                    bookmarkListObject = new SectionListObject();
                    for (String id: favorites) {
                        fetchFavorites(id, getView(), favorites.size());
                    }
                }
            }
        }
    }

    private void refresh(final String id, final int favoritesSize) {
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, "http://35.188.11.46:3000/article?id=" + id + "&source=guardian", null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // the response is already constructed as a JSONObject!
                        try {
                            response = response.getJSONObject("response").getJSONObject("content");

                            //Add truncated title and long title
                            bookmarkListObject.newsTitleLong.add(response.getString("webTitle"));
                            TruncateString truncateString = new TruncateString(response.getString("webTitle"), WORD_LENGTH);
                            bookmarkListObject.newsTitle.add(truncateString.getTruncation());

                            bookmarkListObject.webURL.add(response.getString("webUrl"));

                            //Add Time
                            DateToZoneTimeString dateToZoneTimeString = new DateToZoneTimeString(response.getString("webPublicationDate"));
                            bookmarkListObject.newsTime.add(dateToZoneTimeString.getDayMonthZoneTimeString());

                            //Add Section
                            bookmarkListObject.newsSection.add(response.getString("sectionName"));

                            //Get image
                            String image = fetchImageURL(response);
                            ImageDownloader imageDownloader = new ImageDownloader();
                            Bitmap img = imageDownloader.execute(image).get();
                            bookmarkListObject.newsImage.add(img);

                            //Add article Id
                            bookmarkListObject.articleId.add(id);
                            if (bookmarkListObject.newsTitle.size() == favoritesSize) {
                                bookmarkCustomAdapter.notifyDataSetChanged();
                            }
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
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonRequest);
    }

    private void fetchFavorites(final String id, final View view, final int favoritesSize) {
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, "http://35.188.11.46:3000/article?id=" + id + "&source=guardian", null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // the response is already constructed as a JSONObject!
                        try {
                            response = response.getJSONObject("response").getJSONObject("content");

                            //Add truncated title and long title
                            bookmarkListObject.newsTitleLong.add(response.getString("webTitle"));
                            TruncateString truncateString = new TruncateString(response.getString("webTitle"), WORD_LENGTH);
                            bookmarkListObject.newsTitle.add(truncateString.getTruncation());

                            bookmarkListObject.webURL.add(response.getString("webUrl"));

                            //Add Time
                            DateToZoneTimeString dateToZoneTimeString = new DateToZoneTimeString(response.getString("webPublicationDate"));
                            bookmarkListObject.newsTime.add(dateToZoneTimeString.getDayMonthZoneTimeString());

                            //Add Section
                            bookmarkListObject.newsSection.add(response.getString("sectionName"));

                            //Get image
                            String image = fetchImageURL(response);
                            ImageDownloader imageDownloader = new ImageDownloader();
                            Bitmap img = imageDownloader.execute(image).get();
                            bookmarkListObject.newsImage.add(img);

                            //Add article Id
                            bookmarkListObject.articleId.add(id);

                            if (bookmarkListObject.newsImage.size() == favoritesSize) {
                                bookmarkCustomAdapter = new BookmarkCustomAdapter(view, bookmarkListObject);
                                bookmarkListView.setAdapter(bookmarkCustomAdapter);
                                progressBar.setVisibility(View.GONE);
                                fetchingFavorites.setVisibility(View.GONE);
                            }
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
                Bitmap myBitmap = BitmapFactory.decodeStream(in);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
