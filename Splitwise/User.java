import java.util.ArrayList;
import java.util.List;

public class User {

    private final int id;
    private String name;
    private static int userCount = 0;
    public static List<User> users = new ArrayList<>();

    public User(String name) {
        this.id = ++userCount;
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public Integer getId() {
        return this.id;
    }

    public static void addUser(User u){
        users.add(u);
    }

    public static User getuserByName(String name){
        for(User user : users){
            if(user.getName().equalsIgnoreCase(name)){
                return user;
            }
        }

        return null;
    }

    // Looked In by List .contains()
    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }
        if(!(o instanceof User)){
            return false;
        }

        User user = (User)o;

        return this.id == user.getId();
    }

    // Looked In by HashMap when using .contains()
    @Override
    public int hashCode(){
        return Integer.hashCode(id);
    }

    @Override
    public String toString(){
        return String.format("Id: %d -> Name: %s", this.id,this.name);
    }

}
