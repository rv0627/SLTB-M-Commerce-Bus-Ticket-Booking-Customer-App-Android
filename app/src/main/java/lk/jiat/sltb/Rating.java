package lk.jiat.sltb;

public class Rating {
    private String username;
    private double rating;
    private String comment;
    private String timestamp;

    public Rating(String username, double rating, String comment, String timestamp) {
        this.username = username;
        this.rating = rating;
        this.comment = comment;
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return username;
    }

    public double getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
