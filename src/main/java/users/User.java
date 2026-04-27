package users;

import util.Role;
import java.io.Serializable;

public abstract class User implements Serializable {
    private static final long serialVersionUID = 1L;

    protected int id;
    protected String name;
    protected String email;
    protected Role role;

    public User(int id, String name, String email, Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

    // Abstract methods to enforce Polymorphism in child classes
    public abstract void displayInfo();
    public abstract String toDataString();
}
