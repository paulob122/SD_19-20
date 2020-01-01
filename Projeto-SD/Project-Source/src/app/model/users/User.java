

package app.model.users;

import java.util.concurrent.locks.ReentrantLock;

/**
 * User class implements a user for the system given its credentials, name and password.
 *
 * @author Grupo 19
 * @version 2020/01/01
 */
public class User {

    //---------------------------------------------------------------------

    /**
     * User name.
     */
    private String name;
    /**
     * User password.
     */
    private String password;
    /**
     * Monitor for the User.
     */
    private ReentrantLock lock;

    //---------------------------------------------------------------------

    /**
     * Empty constructor for User. Sets both name and password to "".
     */
    public User () {

        this.name = "";
        this.password = "";
        this.lock = new ReentrantLock();
    }

    /**
     * Parametrized constructor for User.
     * @param name username
     * @param password password
     */
    public User (String name, String password) {

        this.name = name;
        this.password = password;
        this.lock = new ReentrantLock();
    }

    /**
     * Copy constructor for User.
     * @param u user to copy
     */
    public User (User u) {

        this.name = u.getName();
        this.password = u.getPassword();
        this.lock = u.getLock();
    }

    //---------------------------------------------------------------------

    /**
     * @return name of the user.
     */
    public synchronized String getName() {
        return name;
    }

    /**
     * @return user password.
     */
    private synchronized String getPassword() {
        return password;
    }

    /**
     * @return gets the user monitor lock
     */
    private ReentrantLock getLock() {

        return this.lock;
    }

    //---------------------------------------------------------------------

    /**
     * checks if the given string matches user password
     * @param pass password
     * @return true if it matches
     */
    public boolean check_password (String pass) {

        this.lock.lock();

        boolean ok = false;

        if (this.password.equals(pass)) {

                ok = true; //user credentials match
        }

        this.lock.unlock();

        return ok;
    }

    //---------------------------------------------------------------------

    /**
     * @return shows a user representation as a string
     */
    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append(name).append("@").append(password);

        return sb.toString();
    }

}
