import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Game {

    private Board board;
    private Queue<Player> players;
    private Dice dice;
    private Player winner;
    private GameStatus status;

    public Game(Board board, Dice dice, List<Player> players) {
        this.board = board;
        this.dice = dice;
        this.players = new LinkedList<>(players);
        this.status = GameStatus.NOT_STARTED;
    }

    public void play(){

        if(this.players.size()<2){
            System.out.println("Cannot start game atleast 2 players are required");
            return;
        }

        if(this.status.name().equals(GameStatus.COMPLETED.name())){
            System.out.println("This game is already completed and the winner is: "+this.winner.getName());
            return;
        }

        this.status = GameStatus.STARTED;

        while(this.status.name().equals(GameStatus.STARTED.name())){

            Player currentPlayer = this.players.poll();
            turn(currentPlayer);

            if(status == GameStatus.STARTED){
                this.players.add(currentPlayer);
            }
        }

        System.out.println("Game Finished");
        if(this.winner!=null){
            System.out.println("Winner is: "+this.winner.getName());
        }
    }

    private void turn(Player currentPlayer){

        int steps = dice.roll();
        System.out.println("Player "+currentPlayer.getName()+" rolled "+steps);

        int playerPosition = currentPlayer.getCurrentPos()+steps;

        // check if the player has won
        if(playerPosition>this.board.getSize()){
            System.out.println("Player "+currentPlayer.getName()+" overshoot");
            return;
        }

        if(playerPosition == this.board.getSize()){
            System.out.println("Winner! "+currentPlayer.getName());
            this.status = GameStatus.COMPLETED;
            this.winner = currentPlayer;
            return;
        }


        // check if at this playerPosition I encounter a snake or a ladder
        int finalPosition = this.board.getFinalPosition(playerPosition);

        if(finalPosition<playerPosition){
            System.out.println(currentPlayer.getName()+" Encounterd a snake at: "+playerPosition+". Moving the player down to: "+finalPosition);
        }else if(finalPosition>playerPosition){
            System.out.println(currentPlayer.getName()+" Encounterd a ladder at: "+playerPosition+". Moving the player up to: "+finalPosition);
        }else{
            System.out.println(currentPlayer.getName()+" Moved from: "+currentPlayer.getCurrentPos()+" to: "+ finalPosition);
        }

        currentPlayer.setCurrentPos(finalPosition);

        // handle extra turn
        if(steps == 6){
            System.out.println("Player "+currentPlayer.getName()+" rolled a 6, and gets another chance");
            turn(currentPlayer);
        }

    }
}
