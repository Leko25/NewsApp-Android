package lasdot.com;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;

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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class Detailed extends AppCompatActivity {
    private CardView detailedCard;

    private LinearLayout progressLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);

        Toolbar detailedToolbar = findViewById(R.id.detailedToolbar);
        setSupportActionBar(detailedToolbar);

        //add back arrow to toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        detailedCard = findViewById(R.id.detailedCardView);
        progressLayout = findViewById(R.id.detailedProgressLinearLayout);
        detailedCard.setVisibility(View.INVISIBLE);

        //Set Title
        String title = "";
        String webURL = "";
        String id = "";
        if (getIntent().getSerializableExtra("DETAIL_MAP") != null) {
            HashMap<String, String> map = (HashMap<String, String>) getIntent().getSerializableExtra("DETAIL_MAP");
            title = map.get("title");
            webURL = map.get("url");
            id = map.get("id");
        }
        final String toastText = title;
        final String favId = id;
        setTitle(title);

        final String itemURL = webURL;

        //Twitter event listener
        ImageButton twitterButton = findViewById(R.id.detailedTwitterImageButton);
        twitterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tweet = "Check out this Link:" + itemURL + "%23CSCI571NewsSearch";
                String url = "https://twitter.com/intent/tweet?text=" + tweet;
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        //View Full Article Button
        Button viewFullButton = findViewById(R.id.detailedViewFullButton);
        viewFullButton.setPaintFlags(viewFullButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        viewFullButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent((Intent.ACTION_VIEW));
                intent.setData(Uri.parse((itemURL)));
                startActivity(intent);
            }
        });

        //Bookmark event listener
        final ImageButton bookmarkButton = findViewById(R.id.detailedBookmarkImageButton);

        final SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("lasdot.com", Context.MODE_PRIVATE);
        Set<String> favorites = sharedPreferences.getStringSet("Favorites", null);
        if (favorites != null) {
            if (getIndex(favorites, favId) != -1) {
                bookmarkButton.setImageResource(R.drawable.ic_bookmark_black_button_24dp);
            }
        }

        bookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("lasdot.com", Context.MODE_PRIVATE);
                Set<String> favorites = sharedPreferences.getStringSet("Favorites", null);

                if (favorites == null) {
                    bookmarkButton.setImageResource(R.drawable.ic_bookmark_black_button_24dp);

                    Toast.makeText(getApplicationContext(),
                            getToastMsg(toastText, "added"),
                            Toast.LENGTH_SHORT).show();
                    favorites = new HashSet<>();
                    favorites.add(favId);
                    sharedPreferences.edit().putStringSet("Favorites", favorites).apply();
                } else {
                    if (getIndex(favorites, favId) != -1) {
                        bookmarkButton.setImageResource(R.drawable.ic_bookmark_border_black_button_24dp);

                        favorites = new HashSet<>(favorites);
                        favorites.remove(favId);

                        Toast.makeText(getApplicationContext(),
                                getToastMsg(toastText, "removed"),
                                Toast.LENGTH_SHORT).show();

                        if (favorites.size() == 0) {
                            sharedPreferences.edit().clear().apply();
                        } else {
                            sharedPreferences.edit().putStringSet("Favorites", favorites).apply();
                        }
                    } else {
                        bookmarkButton.setImageResource(R.drawable.ic_bookmark_black_button_24dp);

                        Toast.makeText(getApplicationContext(),
                                getToastMsg(toastText, "added"),
                                Toast.LENGTH_SHORT).show();
                        favorites = new HashSet<>(favorites);
                        favorites.add(favId);
                        sharedPreferences.edit().putStringSet("Favorites", favorites).apply();
                    }
                }
            }
        });

        detailedToolbar.setNavigationIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_arrow_back_black_24dp));
        detailedToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //Fetch Data
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, "http://35.188.11.46:3000/article?id=" + id + "&source=guardian", null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // the response is already constructed as a JSONObject!
                        try {
                            response = response.getJSONObject("response").getJSONObject("content");

                            //Add title
                            TextView title = findViewById(R.id.detailedTitleTextView);
                            title.setText(response.getString("webTitle"));

                            //Add Time
                            TextView time = findViewById(R.id.detailedTimeTextView);
                            DateToZoneTimeString dateToZoneTimeString = new DateToZoneTimeString(response.getString("webPublicationDate"));
                            time.setText(dateToZoneTimeString.getFullDateZoneTimeString());

                            //Add Section
                            TextView section = findViewById(R.id.detailedSectionTextView);
                            section.setText(response.getString("sectionName"));

                            //Get image
                            ImageView detailedImg = findViewById(R.id.detailedImageView);
                            String image = fetchImageURL(response);
                            ImageDownloader imageDownloader = new ImageDownloader();
                            Bitmap img = imageDownloader.execute(image).get();
                            detailedImg.setImageBitmap(img);

                            //Add description
                            JSONObject body = (JSONObject) response
                                    .getJSONObject("blocks")
                                    .getJSONArray("body")
                                    .get(0);
                            String content = body.getString("bodyHtml");
                            content = HtmlCompat.fromHtml(content, HtmlCompat.FROM_HTML_MODE_LEGACY).toString();
                            TruncateString truncate = new TruncateString(content, 210);
                            content = truncate.getTruncation();

                            TextView description = findViewById(R.id.detailedDescriptionTextView);
                            description.setText(content);

                            detailedCard.setVisibility(View.VISIBLE);
                            progressLayout.setVisibility(View.GONE);
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
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
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

    private int getIndex(Set<String> set, String articleId) {
        int result = 0;
        for (String articleIds: set) {
            if (articleIds.equals(articleId))
                return result;
            result++;
        }
        return -1;
    }

    private static String getToastMsg(String title, String action) {
        if (action.equals("added"))
            return "\"" + title + "\"" + " was " + action + " to Bookmarks";
        return "\"" + title + "\"" + " was " + action + " from Bookmarks";
    }
}
