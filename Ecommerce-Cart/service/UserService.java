package service;

import java.util.List;

import Actors.User;

public class UserService {

    /*
     * 1) Add User If Not there
     */
    public String addUser(String name) {

        if (User.getUserByName(name) == null) {
            User.addUser(name);
            return String.format("New User(%s) added: %d", name, User.getUsers().size());
        }

        return String.format("User(%s) is already present in the list", name);

    }

    /*
     * 2) Return the UserList
     */
    public List<User> getUsers() {
        return User.getUsers();
    }

}
