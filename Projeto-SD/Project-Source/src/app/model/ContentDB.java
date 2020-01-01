

package app.model;

import app.model.content.music.Music;
import app.model.content.properties.MetaData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class ContentDB {

    private int lastID;
    private Map<Integer, Music> musics;
    private ReentrantLock lock;

    public ContentDB () {

        this.musics = new HashMap<>();
        this.lock = new ReentrantLock();

        this.init_content_test();
    }

    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append("{\n");

        for (Map.Entry<Integer, Music> m: this.musics.entrySet()) {

            sb.append("\t").append(m.getValue().toString() + "\n");
        }

        sb.append("}\n");

        return sb.toString();
    }

    private void init_content_test() {

        MetaData md1 = new MetaData("music1", "jose malhoa", 1920);
        MetaData md2 = new MetaData("music2", "metallica", 2002);

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

    public List<Music> filter_tag(String tag) {

        return this.musics.values().stream().filter(m -> m.hasTag(tag)).collect(Collectors.toList());
    }

    public int add_content(String title, String artist, int year, List<String> list_of_tags) {

        this.lock.lock();

        for (Music m: this.musics.values()) {

            if (m.getTitle().equals(title)) {

                return -1;
            }
        }

        this.lastID++;

        HashSet<String> tags = new HashSet();
        tags.add("*");
        MetaData md = new MetaData(title, artist, year, tags);
        Music m = new Music(this.lastID, 0, md);

        this.musics.put(this.lastID, m);

        this.lock.unlock();

        return this.lastID;
    }

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

    public void download(int id) {

        Music m = this.get(id);

        m.download();
    }

    public boolean containsKey(int id) {

        this.lock.lock();

        boolean exists = this.musics.containsKey(id);

        this.lock.unlock();

        return exists;
    }
}

