package lasdot.com.ui.trending;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import lasdot.com.R;

public class TrendingFragment extends Fragment {
    private EditText searchText;
    private ArrayList<Entry> values;
    private LineChart lineChart;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trending, container, false);

        searchText = view.findViewById(R.id.trendingSearchEditText);

        lineChart = view.findViewById(R.id.trendingLineChart);

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, "https://leko25-google-trends-server.herokuapp.com/trends?keyword=coronavirus", null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // the response is already constructed as a JSONObject!
                        try {
                            response = response.getJSONObject("default");
                            JSONArray timelineData = response.getJSONArray("timelineData");

                            values = new ArrayList<>();
                            for (int i = 0; i < timelineData.length(); i++) {
                                JSONObject valueItem = (JSONObject) timelineData.get(i);
                                int value = (int) valueItem.getJSONArray("value").get(0);
                                values.add(new Entry(i, value));
                            }
                            LineDataSet data = new LineDataSet(values, "Trending Chart for Coronavirus");
                            data.setCircleColor(Color.rgb(75, 0, 130));
                            data.setValueTextColor(Color.rgb(75, 0, 130));
                            data.setColor(Color.rgb(75, 0, 130));

                            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                            dataSets.add(data);

                            LineData lineData = new LineData(dataSets);
                            lineChart.getAxisLeft().setDrawAxisLine(false);
                            lineChart.getXAxis().setDrawGridLines(false);
                            lineChart.getAxisLeft().setDrawGridLines(false);
                            lineChart.getAxisRight().setDrawGridLines(false);
                            lineChart.setData(lineData);
                            lineChart.invalidate();
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
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonRequest);

        searchText.setOnEditorActionListener(editorActionListener);

        searchText.setOnClickListener(editTextClickListener);
        return view;
    }

    private TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (EditorInfo.IME_ACTION_SEND == i) {
                String keyword = searchText.getText().toString();
                final String labelText = keyword;
                try {
                    keyword = URLEncoder.encode(keyword, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                final String query = keyword;
                JsonObjectRequest jsonRequest = new JsonObjectRequest
                        (Request.Method.GET, "https://leko25-google-trends-server.herokuapp.com/trends?keyword=" + query, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // the response is already constructed as a JSONObject!
                                try {
                                    response = response.getJSONObject("default");
                                    JSONArray timelineData = response.getJSONArray("timelineData");

                                    values = new ArrayList<>();
                                    for (int i = 0; i < timelineData.length(); i++) {
                                        JSONObject valueItem = (JSONObject) timelineData.get(i);
                                        int value = (int) valueItem.getJSONArray("value").get(0);
                                        values.add(new Entry(i, value));
                                    }
                                    LineDataSet data = new LineDataSet(values, "Trending Chart for " + labelText);
                                    data.setCircleColor(Color.rgb(75, 0, 130));
                                    data.setValueTextColor(Color.rgb(75, 0, 130));
                                    data.setColor(Color.rgb(75, 0, 130));

                                    ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                                    dataSets.add(data);

                                    LineData lineData = new LineData(dataSets);
                                    lineChart.getAxisLeft().setDrawAxisLine(false);
                                    lineChart.getXAxis().setDrawGridLines(false);
                                    lineChart.getAxisLeft().setDrawGridLines(false);
                                    lineChart.getAxisRight().setDrawGridLines(false);
                                    lineChart.setData(lineData);
                                    lineChart.invalidate();
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
                RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                requestQueue.add(jsonRequest);
            }
            return false;
        }
    };

    private TextView.OnClickListener editTextClickListener = new TextView.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == searchText.getId()) {
                searchText.setCursorVisible(true);
                searchText.getBackground().setColorFilter(Color.rgb(75, 0, 130), PorterDuff.Mode.SRC_ATOP);
            }
        }
    };
}
