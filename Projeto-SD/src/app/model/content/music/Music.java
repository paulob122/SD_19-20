

package app.model.content.music;

import app.model.content.properties.MetaData;

public class Music {

    private int id;
    private int download_times;
    private MetaData metadata;

    public Music(int id, int download_times, MetaData metadata) {
        this.id = id;
        this.download_times = download_times;
        this.metadata = metadata;
    }

    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append("ID: ").append(id).append("\n");
        sb.append(metadata.toString());

        return sb.toString();
    }
}
