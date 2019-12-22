package app.model.content.properties;

import java.util.HashSet;

public class MetaData {

    private String title;
    private String artist;
    private int year;
    private HashSet<Tag> tags;

    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append(title).append(":").append(artist).append("|").append(year);
        sb.append("TAGS: ").append(tags.toString()).append("\n");

        return sb.toString();
    }

}
