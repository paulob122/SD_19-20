package app.model;

import app.model.users.User;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class UsersDB {

    //------------------------------------------------------------------------------------------------------------------

    private Map<String, User> users;

    private Map<String, User> current_authenticated_users;

    private ReentrantLock lock;

    //------------------------------------------------------------------------------------------------------------------

    public UsersDB () {

        this.users = new HashMap<>();
        this.current_authenticated_users = new HashMap<>();
        this.lock = new ReentrantLock();
    }

    //------------------------------------------------------------------------------------------------------------------

    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append("\tregisted: ").append(this.users.toString()).append("\n");
        sb.append("\tin session: ").append(this.current_authenticated_users.keySet().toString()).append("\n");

        return sb.toString();
    }

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

    public boolean is_user_authenticated(String name) {

        return this.current_authenticated_users.containsKey(name);
    }

    public void add_user_authenticated(String name) {

        this.lock.lock();

        this.current_authenticated_users.put(name, this.get(name));

        this.lock.unlock();
    }

    public boolean logout_user(String name) {

        this.lock.lock();

        if (this.current_authenticated_users.remove(name) == null)
            return false;

        this.lock.unlock();

        return true;
    }
}
