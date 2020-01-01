

package app.model.content.music;

import app.model.content.properties.MetaData;

/**
 * Music class implements a music object that stores its properties and its id.
 *
 * @author Grupo 19
 * @version 2020/01/01
 */
public class Music {

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Stores the music id based, incremented each time a new music is uploaded.
     */
    private int id;
    /**
     * Number of times the music was downloaded.
     */
    private int download_times;
    /**
     * Music metadata.
     */
    private final MetaData metadata;

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Music parametrized constructor.
     *
     * @param id Music ID.
     * @param download_times Nr. of times downloaded. Normally set to 0 at start.
     * @param metadata Metadata properties of the music.
     */
    public Music(int id, int download_times, MetaData metadata) {
        this.id = id;
        this.download_times = download_times;
        this.metadata = metadata;
    }

    /**
     * Adds a tag to a music.
     * @param tag string representing the tag.
     */
    public void add_tag(String tag) {

        this.metadata.add_tag(tag);
    }

    /**
     * Says if a music has a certain tag.
     * @param tag string representing the tag.
     * @return true if the music has that tag.
     */
    public boolean hasTag(String tag) {

        return this.metadata.hasTag(tag);
    }

    /**
     * @return a representation of the music as a string.
     */
    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append("ID: ").append(id).append(", DOWNL: ").append(this.download_times).append(", Metadata: ");
        sb.append(metadata.toString());

        return sb.toString();
    }

    /**
     * Gets the music title.
     * @return music title.
     */
    public String getTitle() {

        String res;

        res = this.metadata.getTitle();

        return res;
    }

    /**
     * Increases music download times by 1.
     */
    public void download() {

        synchronized (this) {

            this.download_times++;
        }
    }
}
