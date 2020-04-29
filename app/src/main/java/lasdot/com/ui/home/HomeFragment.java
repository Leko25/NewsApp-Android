package lasdot.com.ui.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import lasdot.com.ApiKeys;
import lasdot.com.DateToZoneTimeString;
import lasdot.com.R;
import lasdot.com.TruncateString;

public class HomeFragment extends Fragment {
    protected LocationManager locationManager;

    protected LocationListener locationListener;

    protected RecyclerView newsList;

    private static int TITLELENGTH = 13;

    ApiKeys api = new ApiKeys();

    private NewsWeatherListObject newsWeatherListObject;

    HomeCustomAdapter homeAdapter;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60*1000, 0, locationListener);
            }
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        newsWeatherListObject = new NewsWeatherListObject();

        newsList = view.findViewById(R.id.newsListView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        newsList.setLayoutManager(linearLayoutManager);

        //Get user GPS location
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                String longitude = Double.toString(location.getLongitude());
                String latitude = Double.toString(location.getLatitude());
                String url = "http://ip-api.com/json?lat="+latitude+"&lon="+longitude;

                FetchJSON fetchJSON = new FetchJSON(getContext(), postExecute, "location");
                fetchJSON.execute(url);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        //Ask user permissions
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60*1000, 1000, locationListener);
        }
        return view;
    }

    @SuppressLint("HandlerLeak")
    private Handler postExecute = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            if (bundle != null) {
                switch (bundle.getString("Type")){
                    case "location":
                        parseLocationJSON(bundle.getString("result"));
                        break;
                    case "weather":
                        parseWeatherJSON(bundle.getString("result"));
                        break;
                    case "news":
                        parseNewsJSON(bundle.getString("result"));
                }
            }
        }
    };

    private void parseNewsJSON(String result) {
        try {
            JSONObject results = new JSONObject(result);
            results = results.getJSONObject("response");
            JSONArray resultsArray = results.getJSONArray("results");
            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject resultItem = (JSONObject) resultsArray.get(i);
                TruncateString truncateString = new TruncateString(resultItem.getString("webTitle"), TITLELENGTH);
                String resultTitle = truncateString.getTruncation();

                String imageURL = null;

                try {
                    imageURL = resultItem.getJSONObject("fields").getString("thumbnail");
                }
                catch (Exception e) {
                    imageURL = "https://assets.guim.co.uk/images/eada8aa27c12fe2d5afa3a89d3fbae0d/fallback-logo.png";
                }

                DateToZoneTimeString dateToZoneTimeString = new DateToZoneTimeString(resultItem.getString("webPublicationDate"));

                ImageDownloader imageDownloader = new ImageDownloader();
                Bitmap image = imageDownloader.execute(imageURL).get();

                newsWeatherListObject.newsTitle.add(resultTitle);
                newsWeatherListObject.newsTitleLong.add(resultItem.getString("webTitle"));

                newsWeatherListObject.articleId.add(resultItem.getString("id"));

                newsWeatherListObject.newsImage.add(image);

                newsWeatherListObject.newsSection.add(resultItem.getString("sectionName"));

                newsWeatherListObject.newsTime.add(dateToZoneTimeString.getZoneTimeString());

                newsWeatherListObject.isWeather = false;
            }
            homeAdapter.notifyDataSetChanged();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseLocationJSON(String result) {
        try {
            JSONObject jsonObj = new JSONObject(result);

            newsWeatherListObject.city = jsonObj.getString("city");

            newsWeatherListObject.state = jsonObj.getString("regionName");

            String url = "https://api.openweathermap.org/data/2.5/weather?q="+
                    URLEncoder.encode(jsonObj.getString("city"), "utf-8") +
                    "&units=metric&appid=" + api.WEATHER_KEY;
            FetchJSON fetchJSON = new FetchJSON(getContext(), postExecute, "weather");
            fetchJSON.execute(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseWeatherJSON(String result) {
        try {
            JSONObject jsonObj = new JSONObject(result);
            Double temp = jsonObj.getJSONObject("main").getDouble("temp");
            JSONObject descObj = (JSONObject) jsonObj.getJSONArray("weather").get(0);
            String desc = descObj.getString("main");

            newsWeatherListObject.description = desc;

            newsWeatherListObject.temperature = Integer.valueOf(temp.intValue());

            ImageDownloader imageDownloader = new ImageDownloader();

            String imageURL = getImageURL(desc);
            Bitmap image = imageDownloader.execute(imageURL).get();

            newsWeatherListObject.weatherImage = image;

            newsWeatherListObject.isWeather = true;

            homeAdapter = new HomeCustomAdapter(getContext(), newsWeatherListObject);
            newsList.setAdapter(homeAdapter);

            // Fetch news Headline data
            String newsURL = "https://content.guardianapis.com/search?order-by=newest&showfields=starRating,headline,thumbnail,short-url&api-key=" + api.GUARDIAN_KEY;

            FetchNewsJSON fetchNewsJSON = new FetchNewsJSON(getContext(), postExecute, "news");
            fetchNewsJSON.execute(newsURL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getImageURL(String desc) {
        String url = "";
        switch (desc) {
            case "Clouds":
               url =  "https://csci571.com/hw/hw9/images/android/cloudy_weather.jpg";
               break;
            case "Clear":
                url = "https://csci571.com/hw/hw9/images/android/clear_weather.jpg";
                break;
            case "Snow":
                url = "https://csci571.com/hw/hw9/images/android/snowy_weather.jpeg";
                break;
            case "Rain":
                url = "https://csci571.com/hw/hw9/images/android/rainy_weather.jpg";
                break;
            case "Drizzle":
                url = "https://csci571.com/hw/hw9/images/android/rainy_weather.jpg";
                break;
            case "Thunderstorm":
                url = " https://csci571.com/hw/hw9/images/android/thunder_weather.jpg";
                break;
            default:
                url = "https://csci571.com/hw/hw9/images/android/sunny_weather.jpg";
        }
        return url;
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
