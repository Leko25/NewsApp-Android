package lasdot.com.ui.home;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.ArrayList;

public class NewsWeatherListObject {
    public static ArrayList<String> newsTitle, newsTime, newsSection, articleId, newsTitleLong;
    public static ArrayList<Bitmap> newsImage;
    public static String city, state, description;
    public static int temperature;
    public static Bitmap weatherImage;
    public static boolean isWeather = false;


    public NewsWeatherListObject() {
        newsTitle = new ArrayList<>();
        newsTime = new ArrayList<>();
        newsSection = new ArrayList<>();
        articleId = new ArrayList<>();
        newsTitleLong = new ArrayList<>();
        newsImage = new ArrayList<>();
    }
}
