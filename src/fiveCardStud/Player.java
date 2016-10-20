package fiveCardStud;

import java.util.Scanner;

public class Player extends People {
    private static final int moneyDefault = 50000;

    private int seat;
    private int money;
    private Card[] cards;
    private int cardAmount;
    private boolean isGiveUp;
    private GameRoom gameRoom;
    private boolean isReady = false;

    public Player(String name){
        super(name);
        money = moneyDefault;
        cards = new Card[Card.cardsPerPlayer];
        for (int i = 0; i < Card.cardsPerPlayer; ++i){
            cards[i] = null;
        }
        seat = -1;
        cardAmount = 0;
        isGiveUp = false;
        gameRoom = null;
    }
    public static int getMoneyDefault(){
        return moneyDefault;
    }

    GameRoom getGameRoom() {
        return gameRoom;
    }
    void setGameRoom(GameRoom gameRoom) {
        this.gameRoom = gameRoom;
    }
    int getCardAmount() {
        return cardAmount;
    }
    void setCardAmount(int cardAmount) {
        this.cardAmount = cardAmount;
    }
    int getSeat() {
        return seat;
    }
    void setSeat(int seat) {
        this.seat = seat;
    }
    boolean getGiveUp(){
        return isGiveUp;
    }
    void setGiveUp(boolean giveUp) {
        isGiveUp = giveUp;
    }
    Card[] getCards() {
        return cards;
    }
    void setCards(Card[] cards) {
        this.cards = cards;
    }
    Card[] getUpCards(Player player){
        Card[] otherCards;
        if (player == this) {
            otherCards = new Card[gameRoom.getRound()];
            System.arraycopy(player.getCards(), 0, otherCards, 0, gameRoom.getRound());
        }
        else {
            otherCards = new Card[gameRoom.getRound() - 1];
            System.arraycopy(player.getCards(), 1, otherCards, 0, gameRoom.getRound() - 1);
        }
        return otherCards;
    }
    int getMoney() {
        return money;
    }
    boolean getReady(){
        return isReady;
    }
    void setReady() {
        System.out.println("输入\"y\"准备开始");
        System.out.println(getName() + ", are you ready?");
        Scanner readyScanner = new Scanner(System.in);
        String ready = readyScanner.next();
        isReady = ready.equals("y") || ready.equals("Y");
    }

    // 选择行为
    void action(){
        int wager;
        boolean noError;
        do {
            noError= true;
            showGame();
            Scanner action = new Scanner(System.in);
            System.out.println("当前下注金额： " + gameRoom.getWager());
            System.out.println("你的余额： " + money);
            System.out.println();
            System.out.println("输入下注金额，输入0则放弃，输入1则all-in");
            System.out.println("小于当前注数得下注金额将无效");
            System.out.println("少于100的部分按100下注");
            wager = action.nextInt();
            if (wager == 0)
                giveUp();
            else if (wager == 1)
                noError = allIn();
            else
                noError = makeBet(wager);

            if (!noError){
                System.out.println();
                System.out.println("请重新输入");
                System.out.println();
            }
            // 假装是清屏233333
            System.out.print("\n\n\n\n\n\n\n\n");
        } while (!noError);
    }
    // 以某位玩家视角，展示某人的牌
    private void showCards(Player player){
        if (player == this)
            System.out.print("你的牌（第一张是暗牌）：");
        else{
            System.out.print(player.getName() + "的牌（第一张是暗牌）：");
            System.out.print("  **");
        }

        for (Card card : getUpCards(player)) {
            System.out.print("  ");
            card.show();
        }
        System.out.println();
    }
    // 以某位玩家视角，展示整个牌局
    private void showGame(){
        Player player = this;
        for (int i = 0; i < gameRoom.getPlayerAlive(); ++i) {
            showCards(player);
            player = gameRoom.getNextPlayer(player);
        }
    }

    // 下注
    boolean makeBet(int wager){
        wager = (wager + 99) / 100 * 100;
        if (money < wager) {
            System.out.println("余额不足");
            return false;
        }
        if (wager < gameRoom.getWager()){
            System.out.println("下注金额不足");
            return false;
        }
        if (wager > gameRoom.getWagerMax())
            wager = gameRoom.getWagerMax();

        money -= wager;
        if (wager > gameRoom.getWager())
            gameRoom.setWager(wager);
        gameRoom.setPot(0);
        //gameRoom.setPot(gameRoom.getPot() + wager);
        return true;
    }
    // 全压
    boolean allIn(){
        return makeBet(gameRoom.getWagerMax());
    }
    // 盖牌（放弃）
    void giveUp(){
        cardAmount = 0;
        gameRoom.setPlayerAlive(gameRoom.getPlayerAlive() - 1);
        isGiveUp = true;
    }
    // 赢
    void winBet(){
        money += gameRoom.getPot();
        System.out.println("Winner is " + getName());
        System.out.println();
    }
    void endGame(){
        cardAmount = 0;
        isGiveUp = false;
        isReady = false;
        cards = new Card[Card.cardsPerPlayer];
    }
}
