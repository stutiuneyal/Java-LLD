public class Player {

    private String name;
    private int currentPos;

    public Player(String name) {
        this.name = name;
        this.currentPos = 0;
    }

    public String getName() {
        return name;
    }

    public int getCurrentPos() {
        return currentPos;
    }

    public void setCurrentPos(int currentPos){
        this.currentPos = currentPos;
    }

}
