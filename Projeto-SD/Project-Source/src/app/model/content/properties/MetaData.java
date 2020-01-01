

package app.model.content.properties;

import java.util.HashSet;

/**
 * Metadata class implements a music object properties such as title, artist,...
 *
 * @author Grupo 19
 * @version 2020/01/01
 */
public class MetaData {

    //------------------------------------------------------------------------------------------------------------------

    /*
     * Music title.
     */
    private String title;
    /**
     * Artist name.
     */
    private String artist;
    /**
     * Music creating year.
     */
    private int year;
    /**
     * Set of music tags, by default "*" is added.
     */
    private HashSet<String> tags;

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Metadata parametrized construtor, specifing the tags.
     *
     * @param title Music title.
     * @param artist Music artist,
     * @param year Music year.
     * @param tags Music tags.
     */
    public MetaData(String title, String artist, int year, HashSet<String> tags) {
        this.title = title;
        this.artist = artist;
        this.year = year;
        this.tags = tags;
    }

    /**
     * Metadata parametrized construtor, non specifing the tags.
     *
     * @param title Music title.
     * @param artist Music artist.
     * @param year Music year.
     */
    public MetaData(String title, String artist, int year) {
        this.title = title;
        this.artist = artist;
        this.year = year;
        this.tags = new HashSet<>();
        this.tags.add("*");
    }

    /**
     * Music Metadata empty construtor.
     * Sets both title and artist as "empty" and year is set to 1234 (default).
     * Tags are set only to "*".
     */
    public MetaData() {

        this.title = "empty";
        this.artist = "empty";
        this.year = 1234;

        this.tags = new HashSet<>();
        this.tags.add("*");
    }

    /**
     * Adds tag to music.
     * @param tag new tag.
     */
    public synchronized void add_tag(String tag) {

        this.tags.add(tag.toLowerCase());
    }

    /**
     * @return shows a representation of Metadata as a string
     */
    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append("Title: ").append(this.title).append(" | ").append("Artist: ").append(this.artist).append(" | ").append("Year: ").append(this.year);
        sb.append(" | ").append("Tags: ").append(tags.toString());

        return sb.toString();
    }

    /**
     * Says if a content has a tag.
     * @param tag the tag
     * @return true if it has the tag
     */
    public synchronized boolean hasTag(String tag) {

        return this.tags.contains(tag.toLowerCase());
    }

    /**
     * @return title of the music.
     */
    public synchronized String getTitle() {

        return this.title;
    }
}
