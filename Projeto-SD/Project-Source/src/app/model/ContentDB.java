

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

        MetaData md1 = new MetaData();
        MetaData md2 = new MetaData();
        MetaData md3 = new MetaData();
        MetaData md4 = new MetaData();

        Music m1 = new Music(1, 1981, md1);
        Music m2 = new Music(2, 1982, md2);
        Music m3 = new Music(3, 1983, md3);
        Music m4 = new Music(4, 1984, md4);

        m1.add_tag("rock");
        m1.add_tag("pop");
        m1.add_tag("blues");

        m2.add_tag("rock");
        m2.add_tag("pop");

        m3.add_tag("rock");

        m4.add_tag("jazz");

        this.musics.put(1, m1);
        this.musics.put(2, m2);
        this.musics.put(3, m3);
        this.musics.put(4, m4);

        this.lastID = 4;
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

        MetaData md = new MetaData(title, artist, year, new HashSet<>(list_of_tags));
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
}

