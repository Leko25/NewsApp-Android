package lasdot.com.ui.home;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import lasdot.com.R;

import java.util.HashSet;
import java.util.Set;

public class HomeCustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static NewsWeatherListObject newsWeatherListAdapter;
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

        }

        @Override
        public boolean onLongClick(View v) {
            Log.i("LongClick", "Here");
            final Dialog dialog = new Dialog(v.getContext());
            dialog.setContentView(R.layout.dialog_layout);
            dialog.setCanceledOnTouchOutside(true);

            ImageView dialogImage = dialog.findViewById(R.id.dialogImageView);
            dialogImage.setImageBitmap(newsWeatherListAdapter.newsImage.get(getAdapterPosition() - 1));

            TextView dialogText = dialog.findViewById(R.id.dialogTitleTextView);
            dialogText.setText(newsWeatherListAdapter.newsTitleLong.get(getAdapterPosition() - 1));

            final ImageButton bookmark = dialog.findViewById(R.id.dialogBookmarkButton);

            final SharedPreferences sharedPreferences = v.getContext().getSharedPreferences("lasdot.com", Context.MODE_PRIVATE);
            Set<String> favorites = sharedPreferences.getStringSet("Favorites", null);
            if (favorites != null) {
                if (getIndex(favorites, newsWeatherListAdapter.articleId.get(getAdapterPosition() - 1)) != -1) {
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

                        favorites = new HashSet<>();
                        favorites.add(newsWeatherListAdapter.articleId.get(getAdapterPosition() - 1));
                        sharedPreferences.edit().putStringSet("Favorites", favorites).apply();
                        return;
                    }

                    if (favorites != null) {
                        if (getIndex(favorites, newsWeatherListAdapter.articleId.get(getAdapterPosition() - 1)) != -1) {
                            bookmark.setImageResource(R.drawable.ic_bookmark_border_black_button_24dp);
                            bookmarkImageView.setImageResource(R.drawable.ic_bookmark_border_black_24dp);

                            favorites.remove(newsWeatherListAdapter.articleId.get(getAdapterPosition() - 1));
                            Log.i("article", newsWeatherListAdapter.articleId.get(getAdapterPosition() - 1));
                            if (favorites.size() == 0) {
                                sharedPreferences.edit().clear().apply();
                            } else {
                                sharedPreferences.edit().putStringSet("Favorites", favorites).apply();
                            }
                        } else {
                            bookmark.setImageResource(R.drawable.ic_bookmark_black_button_24dp);
                            bookmarkImageView.setImageResource(R.drawable.ic_bookmark_black_24dp);

                            favorites.add(newsWeatherListAdapter.articleId.get(getAdapterPosition() - 1));
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
        return 1 + newsWeatherListAdapter.newsTitle.size();
    }

    public HomeCustomAdapter(Context context, NewsWeatherListObject newsWeatherListAdapter) {
        this.context = context;
       this.newsWeatherListAdapter = newsWeatherListAdapter;
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
            weatherViewHolder.city.setText(newsWeatherListAdapter.city);
            weatherViewHolder.state.setText(newsWeatherListAdapter.state);
            weatherViewHolder.description.setText(newsWeatherListAdapter.description);
            weatherViewHolder.weatherImage.setImageBitmap(newsWeatherListAdapter.weatherImage);
            weatherViewHolder.temperature.setText(newsWeatherListAdapter.temperature + " \u2103");
        }
        else {
            final NewsViewHolder newsViewHolder = (NewsViewHolder) holder;
            newsViewHolder.newsTitleView.setText(newsWeatherListAdapter.newsTitle.get(position - 1));
            newsViewHolder.newsTimeView.setText(newsWeatherListAdapter.newsTime.get(position - 1));
            newsViewHolder.newsSectionView.setText(newsWeatherListAdapter.newsSection.get(position - 1));
            newsViewHolder.newsImageView.setImageBitmap(newsWeatherListAdapter.newsImage.get(position - 1));

            //Set click listener on bookmark icon
            newsViewHolder.bookmarkImageView.setImageResource(R.drawable.ic_bookmark_border_black_24dp);

            SharedPreferences sharedPreferences = context.getSharedPreferences("lasdot.com", Context.MODE_PRIVATE);
            Set<String> favorites = sharedPreferences.getStringSet("Favorites", null);
            if (favorites != null) {
                Log.i("OUTSIDE NULL CHECK", "HERE");
                if (getIndex(favorites, newsWeatherListAdapter.articleId.get(position - 1)) != -1) {
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

                        favorites = new HashSet<>();
                        favorites.add(newsWeatherListAdapter.articleId.get(position - 1));
                        sharedPreferences.edit().putStringSet("Favorites", favorites).apply();
                        return;
                    }

                    if (favorites != null) {
                        if (getIndex(favorites, newsWeatherListAdapter.articleId.get(position - 1)) != -1) {
                            newsViewHolder.bookmarkImageView.setImageResource(R.drawable.ic_bookmark_border_black_24dp);

                            favorites.remove(newsWeatherListAdapter.articleId.get(position - 1));
                            Log.i("article", newsWeatherListAdapter.articleId.get(position - 1));
                            if (favorites.size() == 0) {
                                sharedPreferences.edit().clear().apply();
                            } else {
                                sharedPreferences.edit().putStringSet("Favorites", favorites).apply();
                            }
                        } else {
                            newsViewHolder.bookmarkImageView.setImageResource(R.drawable.ic_bookmark_black_24dp);

                            favorites.add(newsWeatherListAdapter.articleId.get(position - 1));
                            sharedPreferences.edit().putStringSet("Favorites", favorites).apply();
                        }
                    }
                }
            });
        }
    }

    public static int getIndex(Set<String> set, String articleId) {
        int result = 0;
        for (String articleIds: set) {
            if (articleIds.equals(articleId))
                return result;
            result++;
        }
        return -1;
    }
}
