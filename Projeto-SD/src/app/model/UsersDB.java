package app.model;

import app.model.users.User;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class UsersDB {

    //------------------------------------------------------------------------------------------------------------------

    private Map<String, User> users;
    private ReentrantLock lock;

    //------------------------------------------------------------------------------------------------------------------

    public UsersDB () {

        this.users = new HashMap<>();
        this.lock = new ReentrantLock();
    }

    //------------------------------------------------------------------------------------------------------------------

    public boolean containsKey(String name) {

        this.lock.lock();

        boolean exists = this.users.containsKey(name);

        this.lock.unlock();

        return exists;
    }

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

    public boolean check_user_password(String name, String password) {

        User u = get(name);

        return u.check_password(password);
    }

    public void put(String name, User NEW) {

        try {

            this.lock.lock();

            this.users.put(name, NEW);

        } finally {

            this.lock.unlock();
        }
    }

    public String toString() {

        return this.users.toString();
    }
}
