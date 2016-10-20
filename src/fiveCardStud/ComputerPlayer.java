package fiveCardStud;

public class ComputerPlayer extends Player {
    private boolean isReady = true;
    public ComputerPlayer(String name){
        super(name);
    }

    @Override
    boolean getReady() {
        return isReady;
    }

    @Override
    void action() {
        double gameTotal = 0;
        double gameWin = 0;
        double playerAmount = getGameRoom().getPlayerAlive();

        for (int i = 0; i < 1000; i++) {
            if (tryGame())
                ++gameWin;
            ++gameTotal;
        }
        double winRate = gameWin / gameTotal;
        double winRateFix = winRate * playerAmount / 2.0;

        // 平均两人对决胜率不到一成，放弃
        if (winRateFix < 0.1)
            giveUp();
        else{
            // 当前注数
            double wagerNow = getGameRoom().getWager();
            // 当前注数到梭哈的差值
            double wagerToAllIn = getGameRoom().getWagerMax() - wagerNow;


            // 平均两人对决胜率不到三成
            if (winRateFix < 0.3)
                makeBet((int)wagerNow);
            // 平均两人对决胜率达到九成
            else if (winRateFix > 0.9)
                allIn();
            else {
                // 增量 = 差值 * （修正胜率 - 0.3） ^ 2 / （0.9 - 0.3) ^ 2
                // 修正胜率 < 0.3,  增量 = 0;
                // 修正胜率 > 0.9,  增量 = 1（梭哈）；
                double wagerAdd = wagerToAllIn * (winRateFix - 0.3) * (winRateFix - 0.3) / 0.36;
                makeBet((int) (wagerNow + wagerAdd));
            }
        }
    }

    private boolean tryGame() {
        int round = getGameRoom().getRound();
        int playerAlive = getGameRoom().getPlayerAlive();
        GameRoom tmpGameRoom = new GameRoom();
        Player[] players = new Player[playerAlive];

        for (int i = 0; i < playerAlive; i++) {
            players[i] = new Player("player " + i);
            tmpGameRoom.addPlayer(players[i]);
        }

        // 获取每个人的牌，并随机发完五张牌
        Card card;
        Player player = this;
        for (int i = 0; i < round; i++) {
            // 获取自己的牌
            card = new Card(getCards()[i]);
            tmpGameRoom.sendCard(players[0], card);
        }
        for (int i = 1; i < playerAlive; i++) {
            player = getGameRoom().getNextPlayer(player);
            for (int j = 0; j < round - 1; ++j) {
                // 获取其他人的牌
                card = new Card(getUpCards(player)[j]);
                tmpGameRoom.sendCard(players[i], card);
            }
        }


        for (int i = round; i < Card.cardsPerPlayer; i++) {
            // 发自己剩下的牌
            tmpGameRoom.sendCard(players[0]);
        }
        for (int i = 1; i < playerAlive; i++) {
            player = getGameRoom().getNextPlayer(player);
            for (int j = round - 1; j < Card.cardsPerPlayer; ++j) {
                // 发其他人剩下的牌
                tmpGameRoom.sendCard(players[i]);
            }
        }

        return tmpGameRoom.getGameWinner() == players[0];
    }

    @Override
    void endGame(){
        setCardAmount(0);
        setGiveUp(false);
        isReady = true;
        setCards(new Card[Card.cardsPerPlayer]);
    }
}
