package lasdot.com.ui.home;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import lasdot.com.Detailed;
import lasdot.com.R;
import lasdot.com.SearchResultActivity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class HomeCustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static NewsWeatherListObject newsWeatherListObject;
    public Context context;

    public static class NewsViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener{
        public TextView newsTitleView, newsTimeView, newsSectionView;
        public ImageView newsImageView, bookmarkImageView;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            newsTitleView = itemView.findViewById(R.id.newsTitleTextView);
            newsTimeView = itemView.findViewById(R.id.newsTimeTextView);
            newsSectionView = itemView.findViewById(R.id.newsSectionTextView);
            newsImageView = itemView.findViewById(R.id.newImageView);
            bookmarkImageView = itemView.findViewById(R.id.bookmarkImageView);
        }

        @Override
        public void onClick(View v) {
            HashMap<String, String> map = new HashMap<>();
            map.put("title", newsWeatherListObject.newsTitle.get(getAdapterPosition() - 1));
            map.put("id", newsWeatherListObject.articleId.get(getAdapterPosition() - 1));
            map.put("url", newsWeatherListObject.webURL.get(getAdapterPosition() - 1));

            Intent intent = new Intent(v.getContext(), Detailed.class);
            intent.putExtra("DETAIL_MAP", map);
            v.getContext().startActivity(intent);
        }

        @Override
        public boolean onLongClick(View v) {
            final Dialog dialog = new Dialog(v.getContext());
            dialog.setContentView(R.layout.dialog_layout);
            dialog.setCanceledOnTouchOutside(true);

            ImageView dialogImage = dialog.findViewById(R.id.dialogImageView);
            dialogImage.setImageBitmap(newsWeatherListObject.newsImage.get(getAdapterPosition() - 1));

            TextView dialogText = dialog.findViewById(R.id.dialogTitleTextView);
            dialogText.setText(newsWeatherListObject.newsTitleLong.get(getAdapterPosition() - 1));

            final ImageButton twitter = dialog.findViewById(R.id.dialogTwitterButton);
            twitter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String tweet = "Check out this Link:" + newsWeatherListObject.webURL.get(getAdapterPosition() - 1)
                            + "%23CSCI571NewsSearch";
                    String url = "https://twitter.com/intent/tweet?text=" +
                            tweet;
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    view.getContext().startActivity(intent);
                }
            });

            final ImageButton bookmark = dialog.findViewById(R.id.dialogBookmarkButton);

            final SharedPreferences sharedPreferences = v.getContext().getSharedPreferences("lasdot.com", Context.MODE_PRIVATE);
            Set<String> favorites = sharedPreferences.getStringSet("Favorites", null);
            if (favorites != null) {
                if (getIndex(favorites, newsWeatherListObject.articleId.get(getAdapterPosition() - 1)) != -1) {
                    bookmark.setImageResource(R.drawable.ic_bookmark_black_button_24dp);
                }
            }

            bookmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences sharedPreferences = v.getContext().getSharedPreferences("lasdot.com", Context.MODE_PRIVATE);
                    Set<String> favorites = sharedPreferences.getStringSet("Favorites", null);

                    if (favorites == null) {
                        bookmark.setImageResource(R.drawable.ic_bookmark_black_button_24dp);
                        bookmarkImageView.setImageResource(R.drawable.ic_bookmark_black_24dp);

                        Toast.makeText(v.getContext(),
                                getToastMsg(newsWeatherListObject.newsTitle.get(getAdapterPosition() - 1), "added"),
                                Toast.LENGTH_SHORT).show();

                        favorites = new HashSet<>();
                        favorites.add(newsWeatherListObject.articleId.get(getAdapterPosition() - 1));
                        sharedPreferences.edit().putStringSet("Favorites", favorites).apply();
                        return;
                    }

                    if (favorites != null) {
                        if (getIndex(favorites, newsWeatherListObject.articleId.get(getAdapterPosition() - 1)) != -1) {
                            bookmark.setImageResource(R.drawable.ic_bookmark_border_black_button_24dp);
                            bookmarkImageView.setImageResource(R.drawable.ic_bookmark_border_black_24dp);

                            favorites = new HashSet<>(favorites);
                            favorites.remove(newsWeatherListObject.articleId.get(getAdapterPosition() - 1));

                            Toast.makeText(v.getContext(),
                                    getToastMsg(newsWeatherListObject.newsTitle.get(getAdapterPosition() - 1), "removed"),
                                    Toast.LENGTH_SHORT).show();
                            if (favorites.size() == 0) {
                                sharedPreferences.edit().clear().apply();
                            } else {
                                sharedPreferences.edit().putStringSet("Favorites", favorites).apply();
                            }
                        } else {
                            bookmark.setImageResource(R.drawable.ic_bookmark_black_button_24dp);
                            bookmarkImageView.setImageResource(R.drawable.ic_bookmark_black_24dp);

                            Toast.makeText(v.getContext(),
                                    getToastMsg(newsWeatherListObject.newsTitle.get(getAdapterPosition() - 1), "added"),
                                    Toast.LENGTH_SHORT).show();

                            favorites = new HashSet<>(favorites);
                            favorites.add(newsWeatherListObject.articleId.get(getAdapterPosition() - 1));
                            sharedPreferences.edit().putStringSet("Favorites", favorites).apply();
                        }
                    }
                }
            });

            dialog.show();
            return true;
        }
    }

    public static class WeatherViewHolder extends RecyclerView.ViewHolder {
        public TextView city, state, description, temperature;
        public ImageView weatherImage;

        public WeatherViewHolder(@NonNull View itemView) {
            super(itemView);
            city = itemView.findViewById(R.id.weatherCityTextView);
            state = itemView.findViewById(R.id.weatherStateTextView);
            description = itemView.findViewById(R.id.weatherDescTextView);
            weatherImage = itemView.findViewById(R.id.weatherImageView);
            temperature = itemView.findViewById(R.id.weatherTempTextView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return 1 + newsWeatherListObject.newsTitle.size();
    }

    public HomeCustomAdapter(Context context, NewsWeatherListObject newsWeatherListAdapter) {
        this.context = context;
        this.newsWeatherListObject = newsWeatherListAdapter;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                return new WeatherViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_card, parent, false));
            default:
                return new NewsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_home_layout, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (position == 0) {
            WeatherViewHolder weatherViewHolder = (WeatherViewHolder) holder;
            weatherViewHolder.city.setText(newsWeatherListObject.city);
            weatherViewHolder.state.setText(newsWeatherListObject.state);
            weatherViewHolder.description.setText(newsWeatherListObject.description);
            weatherViewHolder.weatherImage.setImageBitmap(newsWeatherListObject.weatherImage);
            weatherViewHolder.temperature.setText(newsWeatherListObject.temperature + " \u2103");
        }
        else {
            final NewsViewHolder newsViewHolder = (NewsViewHolder) holder;
            newsViewHolder.newsTitleView.setText(newsWeatherListObject.newsTitle.get(position - 1));
            newsViewHolder.newsTimeView.setText(newsWeatherListObject.newsTime.get(position - 1));
            newsViewHolder.newsSectionView.setText(newsWeatherListObject.newsSection.get(position - 1));
            newsViewHolder.newsImageView.setImageBitmap(newsWeatherListObject.newsImage.get(position - 1));

            //Set click listener on bookmark icon
            newsViewHolder.bookmarkImageView.setImageResource(R.drawable.ic_bookmark_border_black_24dp);

            SharedPreferences sharedPreferences = context.getSharedPreferences("lasdot.com", Context.MODE_PRIVATE);
            Set<String> favorites = sharedPreferences.getStringSet("Favorites", null);
            if (favorites != null) {
                if (getIndex(favorites, newsWeatherListObject.articleId.get(position - 1)) != -1) {
                    newsViewHolder.bookmarkImageView.setImageResource(R.drawable.ic_bookmark_black_24dp);
                }
            }

            newsViewHolder.bookmarkImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences sharedPreferences = v.getContext().getSharedPreferences("lasdot.com", Context.MODE_PRIVATE);
                    Set<String> favorites = sharedPreferences.getStringSet("Favorites", null);
                    if (favorites == null) {
                        newsViewHolder.bookmarkImageView.setImageResource(R.drawable.ic_bookmark_black_24dp);

                        Toast.makeText(v.getContext(),
                                getToastMsg(newsWeatherListObject.newsTitle.get(position - 1), "added"),
                                Toast.LENGTH_SHORT).show();

                        favorites = new HashSet<>();
                        favorites.add(newsWeatherListObject.articleId.get(position - 1));
                        sharedPreferences.edit().putStringSet("Favorites", favorites).apply();
                        return;
                    }

                    if (favorites != null) {
                        if (getIndex(favorites, newsWeatherListObject.articleId.get(position - 1)) != -1) {
                            newsViewHolder.bookmarkImageView.setImageResource(R.drawable.ic_bookmark_border_black_24dp);

                            Toast.makeText(v.getContext(),
                                    getToastMsg(newsWeatherListObject.newsTitle.get(position - 1), "removed"),
                                    Toast.LENGTH_SHORT).show();

                            favorites = new HashSet<>(favorites);
                            favorites.remove(newsWeatherListObject.articleId.get(position - 1));
                            if (favorites.size() == 0) {
                                sharedPreferences.edit().clear().apply();
                            } else {
                                sharedPreferences.edit().putStringSet("Favorites", favorites).apply();
                            }
                        } else {
                            newsViewHolder.bookmarkImageView.setImageResource(R.drawable.ic_bookmark_black_24dp);

                            Toast.makeText(v.getContext(),
                                    getToastMsg(newsWeatherListObject.newsTitle.get(position - 1), "added"),
                                    Toast.LENGTH_SHORT).show();

                            favorites = new HashSet<>(favorites);
                            favorites.add(newsWeatherListObject.articleId.get(position - 1));
                            sharedPreferences.edit().putStringSet("Favorites", favorites).apply();
                        }
                    }
                }
            });
        }
    }

    private static int getIndex(Set<String> set, String articleId) {
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
