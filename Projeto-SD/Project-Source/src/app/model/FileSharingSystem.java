package app.model;

import app.model.content.music.Music;
import app.model.users.User;

import java.util.List;

/**
 * Main business Class that stores all the methods that implement all
 * the logic operations on the data stored on the system.
 *
 * @author Grupo 19
 * @version 2020/01/01
 */
public class FileSharingSystem {

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Stores all the content of the system.
     */
    private ContentDB content;
    /**
     * Stores all the users registred/in session of the system.
     */
    private UsersDB users;

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Empty constructor of the system. Creates empty Data Structures.
     */
    public FileSharingSystem() {

        this.content = new ContentDB();
        this.users = new UsersDB();
    }


    /**
     * Authenticates an user given its name and password.
     * @param name user name
     * @param pass password
     * @return true in case user exists.
     */
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

    /**
     * Gets the user given its name.
     * @param name user name
     * @return the user
     */
    public User get_user(String name) {

        return this.users.get(name);
    }

    /**
     * Creates a new user given its name and password.
     * @param name user name
     * @param password password
     * @return true in case the account was created successfully
     */
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

    /**
     * Says if an user is in session.
     * @param name user name
     * @return true in case it is in session.
     */
    public boolean is_user_in_session(String name) {

        return this.users.is_user_authenticated(name);
    }

    /**
     * Logs out user from users in session list
     * @param name username
     * @return true in case user is logged before logout
     */
    public boolean logout_user(String name) {

        return this.users.logout_user(name);
    }

    /**
     * @return a representation of the system structures as a string.
     */
    public String toString () {

        StringBuilder sb = new StringBuilder();

        sb.append("Users:\n").append(this.users.toString());
        sb.append("\nContent:\n").append(this.content.toString());

        return sb.toString();
    }

    /**
     * Searchs a tag on the systems.
     * @param tag tag
     * @return a list of musics matching the tag
     */
    public List<Music> search(String tag) {

        return this.content.filter_tag(tag);
    }

    /**
     * Adds a content to the system given its parameters
     * @param title title
     * @param artist artist
     * @param year year
     * @param list_of_tags list of tags
     * @return true in case content does not already exists
     */
    public int add_content(String title, String artist, int year, List<String> list_of_tags) {

        return this.content.add_content(title, artist, year, list_of_tags);
    }

    /**
     * Gets the content matching a certain id
     * @param id content id
     * @return music
     */
    public Music get_content(int id) {

        return this.content.get(id);
    }

    /**
     * Increases music download times by 1
     * @param id music id
     */
    public void download_content(int id) {

        this.content.download(id);
    }

    /**
     * Says if a content exists given its name
     * @param id content id
     * @return true in case it exists
     */
    public boolean has_content(int id) {

        return this.content.containsKey(id);
    }
}
