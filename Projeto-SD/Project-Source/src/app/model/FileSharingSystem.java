package app.model;

import app.model.content.music.Music;
import app.model.users.User;

import java.util.List;

public class FileSharingSystem {

    private ContentDB content;
    private UsersDB users;

    public FileSharingSystem() {

        this.content = new ContentDB();
        this.users = new UsersDB();
    }


    public boolean authenticate(String name, String pass) {

        boolean exists = false;

        if (this.users.containsKey(name)) {

            exists = true;

            boolean checks = this.users.check_user_password(name, pass);

            if (checks) {

                this.users.add_user_authenticated(name);
            }

            return checks;
        }

        return exists;
    }

    public User get_user(String name) {

        return this.users.get(name);
    }

    public boolean register_user(String name, String password) {

        boolean ok = false;

        if (this.users.containsKey(name)) {

            ok = false;

        } else {

            ok = true;

            User NEW = new User(name, password);

            this.users.put(name, NEW);
        }

        return ok;
    }

    public boolean is_user_in_session(String name) {

        return this.users.is_user_authenticated(name);
    }

    public boolean logout_user(String name) {

        return this.users.logout_user(name);
    }

    public String toString () {

        StringBuilder sb = new StringBuilder();

        sb.append("Users:\n").append(this.users.toString());
        sb.append("\nContent:\n").append(this.content.toString());

        return sb.toString();
    }

    public List<Music> search(String tag) {

        return this.content.filter_tag(tag);
    }

    public int add_content(String title, String artist, int year, List<String> list_of_tags) {

        return this.content.add_content(title, artist, year, list_of_tags);
    }

    public Music get_content(int id) {

        return this.content.get(id);
    }
}
