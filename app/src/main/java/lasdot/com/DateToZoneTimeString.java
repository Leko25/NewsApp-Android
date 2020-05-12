package lasdot.com;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DateToZoneTimeString {
    private String dateString;

    public DateToZoneTimeString(String dateString){
        this.dateString = dateString;
    }

    public String getZoneTimeString() {
        LocalDateTime ltd = LocalDateTime.now();

        ZonedDateTime zdtAtArticle = ZonedDateTime.parse(dateString);
        ZoneId zoneId = ZoneId.of("America/Los_Angeles");
        ZonedDateTime zdtAtLos = zdtAtArticle.withZoneSameInstant(zoneId);

        int diff = 0;

        if (ltd.getHour() - zdtAtLos.getHour() > 1) {
            diff = ltd.getHour() - zdtAtLos.getHour();
            return Integer.toString(diff) + "h ago";
        }

        else if (ltd.getMinute() - zdtAtLos.getMinute() > 1) {
            diff = ltd.getMinute() - zdtAtLos.getMinute();
            return Integer.toString(diff) + "m ago";
        }

        else {
            diff = ltd.getSecond() - zdtAtLos.getSecond();
            diff = Math.abs(diff);
            return Integer.toString(diff) + "s ago";
        }
    }

    public String getDayMonthZoneTimeString() {
        ZonedDateTime zdtAtArticle = ZonedDateTime.parse(dateString);
        ZoneId zoneId = ZoneId.of("America/Los_Angeles");
        ZonedDateTime zdtAtLos = zdtAtArticle.withZoneSameInstant(zoneId);

        String month = zdtAtLos.getMonth().toString();
        month = getAbbrMonth(month);

        String day;
        if (zdtAtLos.getDayOfMonth() < 10)
            day = "0" + zdtAtLos.getDayOfMonth();
        else
            day = Integer.toString(zdtAtLos.getDayOfMonth());

        return day + " " + month;
    }

    public String getFullDateZoneTimeString() {
        ZonedDateTime zdtAtArticle = ZonedDateTime.parse(dateString);
        ZoneId zoneId = ZoneId.of("America/Los_Angeles");
        ZonedDateTime zdtAtLos = zdtAtArticle.withZoneSameInstant(zoneId);

        String month = zdtAtLos.getMonth().toString();
        month = getAbbrMonth(month);

        String day;
        if (zdtAtLos.getDayOfMonth() < 10)
            day = "0" + zdtAtLos.getDayOfMonth();
        else
            day = Integer.toString(zdtAtLos.getDayOfMonth());

        return day + " " + month + " " + zdtAtLos.getYear();
    }

    private String getAbbrMonth(String month) {
        switch (month) {
            case "JANUARY":
                month = "Jan";
                break;
            case "FEBRUARY":
                month = "Feb";
                break;
            case "MARCH":
                month = "Mar";
                break;
            case "APRIL":
                month = "Apr";
                break;
            case "MAY":
                month = "May";
                break;
            case "JUNE":
                month = "Jun";
                break;
            case "JULY":
                month = "Jul";
                break;
            case "AUGUST":
                month = "Aug";
                break;
            case "SEPTEMBER":
                month = "Sep";
                break;
            case "OCTOBER":
                month = "Oct";
                break;
            case "NOVEMBER":
                month = "Nov";
                break;
            case "DECEMBER":
                month = "Dec";
                break;
        }
        return month;
    }

}
