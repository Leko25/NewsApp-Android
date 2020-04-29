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
}
