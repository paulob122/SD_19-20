

package app.model.content.properties;

import java.util.HashSet;

public class MetaData {

    private String title;
    private String artist;
    private int year;
    private HashSet<String> tags;

    public MetaData(String title, String artist, int year, HashSet<String> tags) {
        this.title = title;
        this.artist = artist;
        this.year = year;
        this.tags = tags;
    }

    public MetaData(String title, String artist, int year) {
        this.title = title;
        this.artist = artist;
        this.year = year;
        this.tags = new HashSet<>();
        this.tags.add("*");
    }

    public MetaData() {

        this.title = "empty";
        this.artist = "empty";
        this.year = 1234;

        this.tags = new HashSet<>();
        this.tags.add("*");
    }

    public void add_tag(String tag) {

        this.tags.add(tag.toLowerCase());
    }

    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append("Title: ").append(this.title).append(" | ").append("Artist: ").append(this.artist).append(" | ").append("Year: ").append(this.year);
        sb.append(" | ").append("Tags: ").append(tags.toString());

        return sb.toString();
    }

    public boolean hasTag(String tag) {

        return this.tags.contains(tag.toLowerCase());
    }

    public synchronized String getTitle() {

        return this.title;
    }
}
