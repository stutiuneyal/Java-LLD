package Actors;

import java.util.ArrayList;
import java.util.List;

public class User {

    private Integer id;
    private String name;
    private Cart cart; // each user has it's own cart
    private static List<User> users = new ArrayList<>();

    public User(String name) {
        this.id = users.size()+1; // because we are not deleting the users but the cartItems
        this.name = name;
        this.cart = new Cart(); // empty cart
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public static List<User> getUsers() {
        return users;
    }

    public static void setUsers(List<User> users) {
        User.users = users;
    }

    public static User getUserByName(String name){
        for(User user:users){
            if(user.getName().equalsIgnoreCase(name)){
                return user;
            }
        }
        return null;
    }

    public static void addUser(String name){
        users.add(new User(name));
    }

    @Override
    public String toString() {
        return "User [id=" + id + ", name=" + name + ", cart=" + cart + "]";
    }

}