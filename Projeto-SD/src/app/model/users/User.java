

package app.model.users;

import java.util.concurrent.locks.ReentrantLock;

public class User {

    //---------------------------------------------------------------------

    private String name;
    private String password;

    private ReentrantLock lock;

    //---------------------------------------------------------------------

    public User () {

        this.name = "";
        this.password = "";
        this.lock = new ReentrantLock();
    }

    public User (String name, String password) {

        this.name = name;
        this.password = password;
        this.lock = new ReentrantLock();
    }

    public User (User u) {

        this.name = u.getName();
        this.password = u.getPassword();
        this.lock = u.getLock();
    }

    //---------------------------------------------------------------------

    private String getName() {
        return name;
    }

    private String getPassword() {
        return password;
    }

    private ReentrantLock getLock() {

        return this.lock;
    }

    //---------------------------------------------------------------------

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

    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append(name).append("@").append(password);

        return sb.toString();
    }

}
