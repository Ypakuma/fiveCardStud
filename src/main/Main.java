package main;
import fiveCardStud.ComputerPlayer;
import fiveCardStud.GameRoom;
import fiveCardStud.Player;

public class Main {
    public static void main(String[] args){
        Player player = new Player("玩家");
        ComputerPlayer computerPlayer = new ComputerPlayer("电脑");

        GameRoom gameRoom = new GameRoom();

        gameRoom.addPlayer(player);
        gameRoom.addPlayer(computerPlayer);
        while (gameRoom.getPlayerAmount() >= 2)
            gameRoom.startGame();
    }
}