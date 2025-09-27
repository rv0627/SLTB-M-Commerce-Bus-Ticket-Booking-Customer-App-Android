package lk.jiat.sltb;

public class TimeSlot {
    private String startTime;
    private String endTime;
    private boolean isAvailable;

    public TimeSlot(String startTime, String endTime, boolean isAvailable) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.isAvailable = isAvailable;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public boolean isAvailable() {
        return isAvailable;
    }
}