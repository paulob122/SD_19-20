package app.model;

import app.model.users.User;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Stores the Users of the system and the logic around them.
 *
 * @author Grupo 19
 * @version 2020/01/01
 */
public class UsersDB {

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Registred users.
     */
    private Map<String, User> users;
    /**
     * Users in session.
     */
    private Map<String, User> current_authenticated_users;
    /**
     * Monitor to control access to user information.
     */
    private ReentrantLock lock;

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Empty constructor for UserDB
     */
    public UsersDB () {

        this.users = new HashMap<>();
        this.current_authenticated_users = new HashMap<>();
        this.lock = new ReentrantLock();
    }

    //------------------------------------------------------------------------------------------------------------------

    /**
     * @return a representation of the users structures as a string
     */
    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append("{\n");
        sb.append("\tRegisted: ").append(this.users.toString()).append("\n");
        sb.append("\tIn session: ").append(this.current_authenticated_users.keySet().toString());
        sb.append("\n}");

        return sb.toString();
    }

    /**
     * Says if a username exists on the system
     * @param name user name
     * @return true in case it exists
     */
    public boolean containsKey(String name) {

        this.lock.lock();

        boolean exists = this.users.containsKey(name);

        this.lock.unlock();

        return exists;
    }

    /**
     * Gets the user matching a name.
     * @param name user name
     * @return the user
     */
    public User get(String name) {

        User u = null;

        try {

            this.lock.lock();

            u = this.users.get(name);

        } finally {

            this.lock.unlock();
        }

        return this.users.get(name);
    }

    /**
     * Checks if the password given as input matches the user password
     * @param name username
     * @param password password to check
     * @return true in case password matches
     */
    public boolean check_user_password(String name, String password) {

        User u = this.get(name);

        return u.check_password(password);
    }

    /**
     * Adds user to users data structures.
     * @param name username
     * @param NEW user
     */
    public void put(String name, User NEW) {

        try {

            this.lock.lock();

            this.users.put(name, NEW);

        } finally {

            this.lock.unlock();
        }
    }

    /**
     * Says if a user is already in session
     * @param name username
     * @return true in case it is logged
     */
    public boolean is_user_authenticated(String name) {

        this.lock.lock();

        boolean is_authenticated = this.current_authenticated_users.containsKey(name);

        this.lock.unlock();

        return is_authenticated;
    }

    /**
     * Adds username to the current users logged
     * @param name username
     */
    public void add_user_authenticated(String name) {

        this.lock.lock();

        this.current_authenticated_users.put(name, this.get(name));

        this.lock.unlock();
    }

    /**
     * Takes user out of the users in session set
     * @param name username
     * @return true
     */
    public boolean logout_user(String name) {

        this.lock.lock();

        this.current_authenticated_users.remove(name);

        this.lock.unlock();

        return true;
    }
}
