package lasdot.com.ui.headlines;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class SectionListObject {
    public ArrayList<String> newsTitle, newsTime, newsSection, articleId, newsTitleLong, webURL;
    public ArrayList<Bitmap> newsImage;

    public SectionListObject() {
        newsTitle = new ArrayList<>();
        newsTime = new ArrayList<>();
        newsSection = new ArrayList<>();
        articleId = new ArrayList<>();
        newsTitleLong = new ArrayList<>();
        newsImage = new ArrayList<>();
        webURL = new ArrayList<>();
    }
}
