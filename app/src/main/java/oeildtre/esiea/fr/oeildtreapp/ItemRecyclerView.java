package oeildtre.esiea.fr.oeildtreapp;

public class ItemRecyclerView {
    private String text;
    private String imageUrl;

    public ItemRecyclerView(String text, String imageUrl) {
        this.text = text;
        this.imageUrl = imageUrl;
    }

    public String getText() {
        return text;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    //getters & setters
}