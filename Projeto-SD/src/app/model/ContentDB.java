

package app.model;

import app.model.content.music.Music;

import java.util.HashMap;
import java.util.Map;

public class ContentDB {

    private Map<Integer, Music> musics;

    public ContentDB () {

        this.musics = new HashMap<>();
    }

    public String toString() {

        return this.musics.toString();
    }

}

