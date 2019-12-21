package app.model.content;

import java.util.HashMap;
import java.util.Map;

public class FileSharingSystem {

    private int last_id;
    private Map<Integer, Music> musics;

    public FileSharingSystem() {

        last_id = 0;
        musics = new HashMap<>();
    }


}
