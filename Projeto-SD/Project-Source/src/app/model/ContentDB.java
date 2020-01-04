

package app.model;

import app.model.content.music.Music;
import app.model.content.properties.MetaData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * Stores methods and data from the server Content data-base.
 *
 * @author Grupo 19
 * @version 2020/01/01
 */
public class ContentDB {

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Stores the last id for a content.
     * This value is increased each time a content is uploaded.
     */
    private int lastID;
    /**
     * Stores a mapping id to music.
     */
    private Map<Integer, Music> musics;
    /**
     * Monitor to control access to the data on the Map<>.
     */
    private ReentrantLock lock;

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Empty constructor for ContentDB.
     * Used most of the time that creates the structures to store the musics.
     */
    public ContentDB () {

        this.musics = new HashMap<>();
        this.lock = new ReentrantLock();

        this.init_content_test();
    }

    /**
     * @return representation of the whole content as a string
     */
    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append("{\n");

        for (Map.Entry<Integer, Music> m: this.musics.entrySet()) {

            sb.append("\t").append(m.getValue().toString() + "\n");
        }

        sb.append("}\n");

        return sb.toString();
    }

    /**
     * Sets up a test situation storing 2 musics and its metadata for test purposes.
     */
    private void init_content_test() {

        MetaData md1 = new MetaData("music1", "artist1", 1920);
        MetaData md2 = new MetaData("music2", "artist2", 2002);

        Music m1 = new Music(1, 0, md1);
        Music m2 = new Music(2, 1, md2);

        m1.add_tag("rock");
        m1.add_tag("pop");
        m1.add_tag("blues");

        m2.add_tag("rock");
        m2.add_tag("pop");

        this.musics.put(1, m1);
        this.musics.put(2, m2);

        this.lastID = 2;
    }

    /**
     * Filters all the musics that have a certain tag.
     * @param tag tag to filter
     * @return list of musics that match that tag
     */
    public List<Music> filter_tag(String tag) {

        return this.musics.values().stream().filter(m -> m.hasTag(tag)).collect(Collectors.toList());
    }

    /**
     * Adds a content to the database given its parameters.
     * @param title music title
     * @param artist artist
     * @param year year
     * @param list_of_tags list of tags
     * @return last id in case the music dont exist. -1 in the opposite case.
     */
    public int add_content(String title, String artist, int year, List<String> list_of_tags) {

        this.lock.lock();

        for (Music m: this.musics.values()) {

            if (m.getTitle().equals(title)) {

                this.lock.unlock();

                return -1;
            }
        }

        this.lastID++;

        HashSet<String> tags = new HashSet<>();
        tags.add("*");
        tags.addAll(list_of_tags);
        MetaData md = new MetaData(title, artist, year, tags);
        Music m = new Music(this.lastID, 0, md);

        this.musics.put(this.lastID, m);

        this.lock.unlock();

        return this.lastID;
    }

    /**
     * Returns the music matching the id given as a parameter.
     * Note that this method uses get() in which could return null.
     * But, the use cases in this system always make sure there are no exceptions.
     * @param id music id
     * @return Music matching the id
     */
    public Music get(int id) {

        this.lock.lock();

        Music m = null;

        if (this.musics.containsKey(id)) {

            try {

                m = this.musics.get(id);

            } finally {

                this.lock.unlock();
            }

        }

        return m;
    }

    /**
     * Increases music download times by 1.
     * @param id music id
     */
    public void download(int id) {

        Music m = this.get(id);

        m.download();
    }

    /**
     * Checks if a music with an id exists on the system.
     * @param id music id
     * @return true in case it exists.
     */
    public boolean containsKey(int id) {

        this.lock.lock();

        boolean exists = this.musics.containsKey(id);

        this.lock.unlock();

        return exists;
    }
}

