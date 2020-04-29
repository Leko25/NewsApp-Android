package lasdot.com.ui.headlines;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import lasdot.com.R;

public class HeadlineCustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private HeadlineListObject headlineListObject;
    public Context context;

    public HeadlineCustomAdapter(Context context, HeadlineListObject headlineListObject) {
        this.context = context;
        this.headlineListObject = headlineListObject;
    }

    public static class HeadlineViewHolder extends RecyclerView.ViewHolder {
        public TextView newsTitleView, newsTimeView, newsSectionView;
        public ImageView newsImageView, bookmarkImageView;

        public HeadlineViewHolder(@NonNull View itemView) {
            super(itemView);
            newsTitleView = itemView.findViewById(R.id.newsTitleTextView);
            newsTimeView = itemView.findViewById(R.id.newsTimeTextView);
            newsSectionView = itemView.findViewById(R.id.newsSectionTextView);
            newsImageView = itemView.findViewById(R.id.newImageView);
            bookmarkImageView = itemView.findViewById(R.id.bookmarkImageView);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new HeadlineViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_home_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final HeadlineViewHolder headlineViewHolder = (HeadlineViewHolder) holder;
        headlineViewHolder.newsTitleView.setText(headlineListObject.newsTitle.get(position));
        headlineViewHolder.newsTimeView.setText(headlineListObject.newsTime.get(position));
        headlineViewHolder.newsSectionView.setText(headlineListObject.newsSection.get(position));
        headlineViewHolder.newsImageView.setImageBitmap(headlineListObject.newsImage.get(position));

        //Set click listener on bookmark icon
        headlineViewHolder.bookmarkImageView.setImageResource(R.drawable.ic_bookmark_border_black_24dp);
    }

    @Override
    public int getItemCount() {
        return headlineListObject.newsTitle.size();
    }
}
