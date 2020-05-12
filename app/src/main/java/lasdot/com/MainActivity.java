package lasdot.com;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;



import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    ApiKeys api = new ApiKeys();
    private int autoPosition;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        //Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        final SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        final SearchView.SearchAutoComplete searchAutoComplete = searchView.findViewById(R.id.search_src_text);
//        searchAutoComplete.setTextColor(Color.WHITE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Intent intent = new Intent(getApplicationContext(), SearchResultActivity.class);
                intent.putExtra("QUERY", s);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                String url = api.BING_ENDPOINT + "?q=" + s;
                JsonObjectRequest jsonRequest = new JsonObjectRequest
                        (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    response = (JSONObject) response.getJSONArray("suggestionGroups").get(0);
                                    JSONArray suggestions = response.getJSONArray("searchSuggestions");
                                    final ArrayList<String> suggestionList = new ArrayList<>();
                                    for (int i = 0; i < suggestions.length(); i++) {
                                        JSONObject suggestion = (JSONObject) suggestions.get(i);
                                        suggestionList.add(suggestion.getString("displayText"));
                                    }
                                    ArrayAdapter<String> suggestionAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, suggestionList) {
                                        @NonNull
                                        @Override
                                        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                                            View view = super.getView(position, convertView, parent);
                                            if (position == autoPosition) {
                                                view.setBackgroundColor(Color.parseColor("#d7d7d7"));
                                                TextView textView = view.findViewById(android.R.id.text1);

                                                /*YOUR CHOICE OF COLOR*/
                                                textView.setTextColor(Color.BLACK);
                                            }
                                            return view;
                                        }
                                    };

                                    searchAutoComplete.setAdapter(suggestionAdapter);
                                    searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                            searchView.setQuery(suggestionList.get(i), false);
                                            autoPosition = i;
                                        }
                                    });
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                            }
                        }){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String>  params = new HashMap<String, String>();
                        params.put("Ocp-Apim-Subscription-Key", api.BING_KEY);
                        return params;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(jsonRequest);
                return false;
            }
        });
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Splash Screen
        Intent intent = new Intent(this, SplashScreen.class);
        startActivity(intent);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_headlines, R.id.navigation_trending, R.id.navigation_bookmarks)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

    }
}
