package lasdot.com.ui.bookmarks;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import lasdot.com.ui.headlines.SectionListObject;

import lasdot.com.R;

public class BookmarkCustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private SectionListObject sectionListObject;
    private View fragView;

    public BookmarkCustomAdapter(View fragView, SectionListObject sectionListObject) {
        this.fragView = fragView;
        this.sectionListObject = sectionListObject;
    }

    public class BookmarkViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {
        private TextView newsTitleView, newsTimeView, newsSectionView;
        private ImageView newsImageView, bookmarkImageView;

        public BookmarkViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            newsTitleView = itemView.findViewById(R.id.bookmarkTitle);
            newsTimeView = itemView.findViewById(R.id.bookmarkTimeTextView);
            newsSectionView = itemView.findViewById(R.id.bookmarkSectionTextView);
            newsImageView = itemView.findViewById(R.id.customBookmarkImageView);
            bookmarkImageView = itemView.findViewById(R.id.bookmarkBookmarkImageView);

            bookmarkImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Remove object from shared preferences
                    SharedPreferences sharedPreferences = view.getContext().getSharedPreferences("lasdot.com", Context.MODE_PRIVATE);
                    Set<String> favorites = sharedPreferences.getStringSet("Favorites", null);

                    favorites = new HashSet<>(favorites);
                    favorites.remove(sectionListObject.articleId.get(getAdapterPosition()));

                    Toast.makeText(view.getContext(),
                            getToastMsg(sectionListObject.newsTitle.get(getAdapterPosition())),
                            Toast.LENGTH_SHORT).show();

                    if (favorites.isEmpty()) {
                        sharedPreferences.edit().clear().apply();

                        TextView noBookmarkTextView = fragView.findViewById(R.id.noBookmarkTextView);
                        RecyclerView bookmarkListView = fragView.findViewById(R.id.bookmarkListView);

                        noBookmarkTextView.setVisibility(View.VISIBLE);
                        bookmarkListView.setVisibility(View.GONE);
                    } else {
                        sharedPreferences.edit().putStringSet("Favorites", favorites).apply();
                    }

                    // Remove item from object array list
                    sectionListObject.newsSection.remove(getAdapterPosition());
                    sectionListObject.articleId.remove(getAdapterPosition());
                    sectionListObject.newsTitle.remove(getAdapterPosition());
                    sectionListObject.newsTitleLong.remove(getAdapterPosition());
                    sectionListObject.newsImage.remove(getAdapterPosition());
                    sectionListObject.newsTime.remove(getAdapterPosition());

                    notifyItemRemoved(getAdapterPosition());
                    notifyItemRangeChanged(getAdapterPosition(),sectionListObject.newsTitle.size());
                }
            });
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
            bookmark.setImageResource(R.drawable.ic_bookmark_black_button_24dp);

            bookmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Remove object from shared preferences
                    SharedPreferences sharedPreferences = view.getContext().getSharedPreferences("lasdot.com", Context.MODE_PRIVATE);
                    Set<String> favorites = sharedPreferences.getStringSet("Favorites", null);

                    favorites = new HashSet<>(favorites);
                    favorites.remove(sectionListObject.articleId.get(getAdapterPosition()));

                    Toast.makeText(view.getContext(),
                            getToastMsg(sectionListObject.newsTitle.get(getAdapterPosition())),
                            Toast.LENGTH_SHORT).show();

                    if (favorites.isEmpty()) {
                        sharedPreferences.edit().clear().apply();
                    } else {
                        sharedPreferences.edit().putStringSet("Favorites", favorites).apply();
                    }

                    // Remove item from object array list
                    sectionListObject.newsSection.remove(getAdapterPosition());
                    sectionListObject.articleId.remove(getAdapterPosition());
                    sectionListObject.newsTitle.remove(getAdapterPosition());
                    sectionListObject.newsTitleLong.remove(getAdapterPosition());
                    sectionListObject.newsImage.remove(getAdapterPosition());
                    sectionListObject.newsTime.remove(getAdapterPosition());

                    notifyItemRemoved(getAdapterPosition());
                    notifyItemRangeChanged(getAdapterPosition(),sectionListObject.newsTitle.size());

                    dialog.cancel();
                }
            });
            dialog.show();
            return true;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new BookmarkViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_card_detail_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
       final BookmarkViewHolder bookmarkViewHolder = (BookmarkViewHolder) holder;
       bookmarkViewHolder.newsTitleView.setText(sectionListObject.newsTitle.get(position));
       bookmarkViewHolder.newsTimeView.setText(sectionListObject.newsTime.get(position));
       bookmarkViewHolder.newsSectionView.setText(sectionListObject.newsSection.get(position));
       bookmarkViewHolder.newsImageView.setImageBitmap(sectionListObject.newsImage.get(position));
    }

    @Override
    public int getItemCount() {
        return sectionListObject.newsTitle.size();
    }

    private static String getToastMsg(String title) {
        return "\"" + title + "\"" + " was removed from Bookmarks";
    }
}
