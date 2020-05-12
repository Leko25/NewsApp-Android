package lasdot.com.ui.headlines;

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
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import lasdot.com.Detailed;
import lasdot.com.R;

public class SectionCustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private SectionListObject sectionListObject;
    public Context context;

    public SectionCustomAdapter(Context context, SectionListObject sectionListObject) {
        this.context = context;
        this.sectionListObject = sectionListObject;
    }

    public class SectionViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener{
        private TextView newsTitleView, newsTimeView, newsSectionView;
        private ImageView newsImageView, bookmarkImageView;

        private SectionViewHolder(@NonNull View itemView) {
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
        public void onClick(View view) {
            HashMap<String, String> map = new HashMap<>();
            map.put("title", sectionListObject.newsTitle.get(getAdapterPosition()));
            map.put("id", sectionListObject.articleId.get(getAdapterPosition()));
            map.put("url", sectionListObject.webURL.get(getAdapterPosition()));

            Intent intent = new Intent(view.getContext(), Detailed.class);
            intent.putExtra("DETAIL_MAP", map);
            view.getContext().startActivity(intent);
        }

        @Override
        public boolean onLongClick(View view) {
            final Dialog dialog = new Dialog(view.getContext());
            dialog.setContentView(R.layout.dialog_layout);
            dialog.setCanceledOnTouchOutside(true);

            ImageView dialogImage = dialog.findViewById(R.id.dialogImageView);
            dialogImage.setImageBitmap(sectionListObject.newsImage.get(getAdapterPosition()));

            TextView dialogText = dialog.findViewById(R.id.dialogTitleTextView);
            dialogText.setText(sectionListObject.newsTitleLong.get(getAdapterPosition()));

            final ImageButton twitter = dialog.findViewById(R.id.dialogTwitterButton);
            twitter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String tweet = "Check out this Link:" + sectionListObject.webURL.get(getAdapterPosition())
                            + "%23CSCI571NewsSearch";
                    String url = "https://twitter.com/intent/tweet?text=" +
                            tweet;
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    view.getContext().startActivity(intent);
                }
            });

            final ImageButton bookmark = dialog.findViewById(R.id.dialogBookmarkButton);

            final SharedPreferences sharedPreferences = view.getContext().getSharedPreferences("lasdot.com", Context.MODE_PRIVATE);
            Set<String> favorites = sharedPreferences.getStringSet("Favorites", null);
            if (favorites != null) {
                if (getIndex(favorites, sectionListObject.articleId.get(getAdapterPosition())) != -1) {
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
                                getToastMsg(sectionListObject.newsTitle.get(getAdapterPosition()), "added"),
                                Toast.LENGTH_SHORT).show();

                        favorites = new HashSet<>();
                        favorites.add(sectionListObject.articleId.get(getAdapterPosition()));
                        sharedPreferences.edit().putStringSet("Favorites", favorites).apply();
                        return;
                    }

                    if (favorites != null) {
                        if (getIndex(favorites, sectionListObject.articleId.get(getAdapterPosition())) != -1) {
                            bookmark.setImageResource(R.drawable.ic_bookmark_border_black_button_24dp);
                            bookmarkImageView.setImageResource(R.drawable.ic_bookmark_border_black_24dp);

                            favorites = new HashSet<>(favorites);
                            favorites.remove(sectionListObject.articleId.get(getAdapterPosition()));

                            Toast.makeText(v.getContext(),
                                    getToastMsg(sectionListObject.newsTitle.get(getAdapterPosition()), "removed"),
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
                                    getToastMsg(sectionListObject.newsTitle.get(getAdapterPosition()), "added"),
                                    Toast.LENGTH_SHORT).show();

                            favorites = new HashSet<>(favorites);
                            favorites.add(sectionListObject.articleId.get(getAdapterPosition()));
                            sharedPreferences.edit().putStringSet("Favorites", favorites).apply();
                        }
                    }
                }
            });

            dialog.show();
            return true;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new SectionViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_home_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final SectionViewHolder sectionViewHolder = (SectionViewHolder) holder;
        sectionViewHolder.newsTitleView.setText(sectionListObject.newsTitle.get(position));
        sectionViewHolder.newsTimeView.setText(sectionListObject.newsTime.get(position));
        sectionViewHolder.newsSectionView.setText(sectionListObject.newsSection.get(position));
        sectionViewHolder.newsImageView.setImageBitmap(sectionListObject.newsImage.get(position));

        //Set click listener on bookmark icon
        sectionViewHolder.bookmarkImageView.setImageResource(R.drawable.ic_bookmark_border_black_24dp);

        SharedPreferences sharedPreferences = context.getSharedPreferences("lasdot.com", Context.MODE_PRIVATE);
        Set<String> favorites = sharedPreferences.getStringSet("Favorites", null);
        if (favorites != null) {
            if (getIndex(favorites, sectionListObject.articleId.get(position)) != -1) {
                Log.i("SAVED", favorites.toString());
                sectionViewHolder.bookmarkImageView.setImageResource(R.drawable.ic_bookmark_black_24dp);
            }
        }

        sectionViewHolder.bookmarkImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = v.getContext().getSharedPreferences("lasdot.com", Context.MODE_PRIVATE);
                Set<String> favorites = sharedPreferences.getStringSet("Favorites", null);
                if (favorites == null) {
                    sectionViewHolder.bookmarkImageView.setImageResource(R.drawable.ic_bookmark_black_24dp);

                    Toast.makeText(v.getContext(),
                            getToastMsg(sectionListObject.newsTitle.get(position), "added"),
                            Toast.LENGTH_SHORT).show();

                    favorites = new HashSet<>();
                    favorites.add(sectionListObject.articleId.get(position));
                    sharedPreferences.edit().putStringSet("Favorites", favorites).apply();
                    return;
                }

                if (favorites != null) {
                    if (getIndex(favorites, sectionListObject.articleId.get(position)) != -1) {
                        sectionViewHolder.bookmarkImageView.setImageResource(R.drawable.ic_bookmark_border_black_24dp);

                        favorites = new HashSet<>(favorites);
                        favorites.remove(sectionListObject.articleId.get(position));

                        Toast.makeText(v.getContext(),
                                getToastMsg(sectionListObject.newsTitle.get(position), "removed"),
                                Toast.LENGTH_SHORT).show();

                        if (favorites.size() == 0) {
                            sharedPreferences.edit().clear().apply();
                        } else {
                            sharedPreferences.edit().putStringSet("Favorites", favorites).apply();
                        }
                    } else {
                        sectionViewHolder.bookmarkImageView.setImageResource(R.drawable.ic_bookmark_black_24dp);

                        Toast.makeText(v.getContext(),
                                getToastMsg(sectionListObject.newsTitle.get(position), "added"),
                                Toast.LENGTH_SHORT).show();

                        favorites = new HashSet<>(favorites);
                        favorites.add(sectionListObject.articleId.get(position));
                        sharedPreferences.edit().putStringSet("Favorites", favorites).apply();
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return sectionListObject.newsTitle.size();
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
