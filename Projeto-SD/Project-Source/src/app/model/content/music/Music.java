

package app.model.content.music;

import app.model.content.properties.MetaData;

public class Music {

    private int id;
    private int download_times;
    private final MetaData metadata;

    public Music(int id, int download_times, MetaData metadata) {
        this.id = id;
        this.download_times = download_times;
        this.metadata = metadata;
    }

    public void add_tag(String tag) {

        this.metadata.add_tag(tag);
    }

    public boolean hasTag(String tag) {

        return this.metadata.hasTag(tag);
    }

    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append("ID: ").append(id).append(", DOWNL: ").append(this.download_times).append(", Metadata: ");
        sb.append(metadata.toString());

        return sb.toString();
    }

    public String getTitle() {

        String res;

        res = this.metadata.getTitle();

        return res;
    }

    public void download() {

        synchronized (this) {

            this.download_times++;
        }
    }
}
