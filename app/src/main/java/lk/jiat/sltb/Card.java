package lk.jiat.sltb;

public class Card {
    private String imageUrl;
    private String docname;
    private String specialization;
    private String emailPhone;


    public Card(String imageUrl, String docname, String specialization, String emailPhone) {
        this.imageUrl = imageUrl;
        this.docname = docname;
        this.specialization = specialization;
        this.emailPhone = emailPhone;

    }


    public String getImageUrl() {
        return imageUrl;
    }

    public String getTitle() {
        return docname;
    }

    public String getDescription() {
        return specialization;
    }


    public String getDocname() {
        return docname;
    }

    // Getter for specialization
    public String getSpecialization() {
        return specialization;
    }
    public String getEmailPhone() {
        return emailPhone;
    }
}