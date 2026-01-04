package main.util;

import java.util.Date;

public class TimeUtil {

    public static String formatTimeAgo(Date updatedAt) {
        long diffMillis = System.currentTimeMillis() - updatedAt.getTime();
        long seconds = diffMillis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (seconds < 30) return "Just finished";
        if (minutes < 1) return seconds + " secs ago";
        if (minutes < 2) return "1 min ago";
        if (minutes < 60) return minutes + " mins ago";
        if (hours < 2) return "1 hr ago";
        if (hours < 24) return hours + " hrs ago";
        if (days < 2) return "1 day ago";
        return days + " days ago";
    }
}
