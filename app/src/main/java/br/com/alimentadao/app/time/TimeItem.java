package br.com.alimentadao.app.time;

import java.util.Locale;
import java.util.Objects;

public class TimeItem {

    private Long id;

    private int hour;

    private int minute;

    public TimeItem() {
    }

    public TimeItem(Long id, int hour, int minute) {
        this.id = id;
        this.hour = hour;
        this.minute = minute;
    }

    public static TimeItem create(int hour, int minute) {
        return new TimeItem(null, hour, minute);
    }

    public Long getId() {
        return id;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public String getFormattedTime() {
        return String.format(
                Locale.getDefault(),
                "%02d:%02d",
                hour, minute
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeItem timeItem = (TimeItem) o;
        return getHour() == timeItem.getHour() && getMinute() == timeItem.getMinute();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getHour(), getMinute());
    }

    @Override
    public String toString() {
        return "TimeItem{" +
                "hour=" + hour +
                ", minute=" + minute +
                '}';
    }
}
