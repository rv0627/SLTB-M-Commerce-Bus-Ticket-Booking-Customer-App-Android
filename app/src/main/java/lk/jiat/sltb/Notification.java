package lk.jiat.sltb;

public class Notification {
    private String title;
    private String content;
    private boolean isRead;

    public Notification(String title, String content, boolean isRead) {
        this.title = title;
        this.content = content;
        this.isRead = isRead;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}