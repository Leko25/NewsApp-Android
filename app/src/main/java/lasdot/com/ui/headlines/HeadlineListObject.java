package lasdot.com.ui.headlines;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class HeadlineListObject {
    public ArrayList<String> newsTitle, newsTime, newsSection, articleId, newsTitleLong;
    public ArrayList<Bitmap> newsImage;

    public HeadlineListObject() {
        newsTitle = new ArrayList<>();
        newsTime = new ArrayList<>();
        newsSection = new ArrayList<>();
        articleId = new ArrayList<>();
        newsTitleLong = new ArrayList<>();
        newsImage = new ArrayList<>();
    }
}
