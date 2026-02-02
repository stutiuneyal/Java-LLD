public class Dice {
    
    private int min;
    private int max;

    public Dice(int min, int max){
        this.min= min;
        this.max= max;
    }

    public int roll(){
        return (int)(Math.random()*(max-min+1)+min);// Math.Random() -> [0,1), +min -> [1,7)
    }
}
