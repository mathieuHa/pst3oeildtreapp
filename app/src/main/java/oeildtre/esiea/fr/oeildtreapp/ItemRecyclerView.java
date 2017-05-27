package oeildtre.esiea.fr.oeildtreapp;

public class ItemRecyclerView {
    private String text;
    private String imageUrlTh;
    private String imageUrl;


    public ItemRecyclerView(String text, String imageUrl, String imageUrlTh) {
        this.text = text;
        this.imageUrlTh = imageUrlTh;
        this.imageUrl = imageUrl;
    }

    public String getText() {
        return text;
    }
    public String getImageUrlTh() {
        return imageUrlTh;
    }
    public String getImageUrl() { return imageUrl; }

}