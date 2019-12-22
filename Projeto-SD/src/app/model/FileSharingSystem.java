package app.model;

import app.model.users.User;

public class FileSharingSystem {

    private ContentDB content;
    private UsersDB users;

    public FileSharingSystem() {

        this.content = new ContentDB();
        this.users = new UsersDB();
    }


    public boolean authenticate(String nome, String pass) {

        boolean exists = false;

        if (this.users.containsKey(nome)) {

            exists = true;

            boolean checks = this.users.check_user_password(nome, pass);

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

    public String toString () {

        StringBuilder sb = new StringBuilder();

        sb.append("Users:\n").append(this.users.toString());
        sb.append("\nContent:\n").append(this.content.toString());

        return sb.toString();
    }
}
