package fiveCardStud;

public class GameRoom {
    private static final int playerAmountMax = 4;
    private boolean[][] cardIsOut = new boolean[CardSuit.getAmount()][CardValue.getAmount()];

    private int playerAmount = 0;
    private Player[] players = new Player[playerAmountMax];
    private final int wagerMax = 2000;
    private final int wagerMin = 200;
    private int playerAlive = 0;
    private int wager = 0;
    private int pot = 0;
    private int round = 0;
    private Player winner = null;

    public void addPlayer(Player player){
        if (playerAmount ==  playerAmountMax){
            System.out.println("The room is full");
            return;
        }
        players[playerAmount] = player;
        player.setSeat(playerAmount++);
        ++playerAlive;
        player.setGameRoom(this);
    }
    public void reducePlayer(Player player){
        int i = 0;
        while (i < playerAmount){
            if (players[i] == player){
                players[i].setSeat(-1);
                players[i].setGameRoom(null);
                break;
            }
            ++i;
        }
        while (i < playerAmount){
            if (i == playerAmount - 1) {
                players[i] = null;
                break;
            }
            players[i] = players[i + 1];
            players[i].setSeat(i);
        }
        --playerAmount;
        --playerAlive;
    }

    int getWagerMax() {
        return wagerMax;
    }
    int getPlayerAlive() {
        return playerAlive;
    }
    void setPlayerAlive(int playerAlive) {
        this.playerAlive = playerAlive;
    }
    int getPot(){
        return pot;
    }
    void setPot(int pot) {
        this.pot = pot;
    }
    int getWager(){
        return wager;
    }
    void setWager(int wager) {
        this.wager = wager;
    }
    int getRound() {
        return round;
    }
    public int getPlayerAmount() {
        return playerAmount;
    }

    // 发牌
    void sendCard(Player player){
        if (player.getCardAmount() >= Card.cardsPerPlayer){
            System.out.println("Too many cards");
            return;
        }
        Card card = new Card();
        while (cardIsOut(card))
            card = new Card();
        setCardOut(card);
        player.getCards()[player.getCardAmount()] = card;
        player.getCards()[player.getCardAmount()].setPlayer(player);
        player.setCardAmount(player.getCardAmount() + 1);
    }
    void sendCard(Player player, Card card){
        if (cardIsOut(card))
            System.out.println("aaaaaaa");
        setCardOut(card);
        player.getCards()[player.getCardAmount()] = card;
        player.getCards()[player.getCardAmount()].setPlayer(player);
        player.setCardAmount(player.getCardAmount() + 1);
    }
    // 查看一张牌是否已发出
    private boolean cardIsOut(Card card){
        return cardIsOut[card.getCardSuit().getIndex()][card.getCardValue().getIndex()];
    }
    // 将一张牌设为已发出
    private void setCardOut(Card card) {
        cardIsOut[card.getCardSuit().getIndex()][card.getCardValue().getIndex()] = true;
    }

    // 选择发牌后，牌面最大的玩家
    private Player getBiggestCardPlayer(){
        Player player = players[0];
        for (int i = 1; i < playerAmount; ++i)
            player = Card.getBiggerCard(player.getCards()[round - 1], players[i].getCards()[round - 1]).getPlayer();
        return player;
    }
    // 选择两玩家中手牌大的玩家
    private Player getBiggerPlayer(Player player1, Player player2){
        Player winner = null;
        Card[] cards1 = player1.getCards();
        Card[] cards2 = player2.getCards();

        Card biggerCard;
        Card[] biggerCards;

        biggerCards = CardType.getBiggerCards(cards1, cards2);
        if (biggerCards != null)
            winner = biggerCards[0].getPlayer();

        // 牌型相同
        if (winner == null){
            switch (CardType.getCardType(cards1)) {
                case HIGH_CARD:
                    biggerCards = Card.getBiggerCards(cards1, cards2);
                    if (biggerCards != null)
                        winner = biggerCards[0].getPlayer();
                    else {
                        biggerCard = CardSuit.getBiggerCard(cards1[0], cards2[0]);
                        if (biggerCard != null)
                            winner = biggerCard.getPlayer();
                        else
                            System.out.println("ERROR");
                    }
                    break;
                case ONE_PAIR:
                    Card pairCard1 = CardType.getCardPair(cards1);
                    Card pairCard2 = CardType.getCardPair(cards2);
                    biggerCard = CardValue.getBiggerCard(pairCard1, pairCard2);
                    if (biggerCard != null)
                        winner = biggerCard.getPlayer();
                    else{
                        CardValue pairValue = null;
                        if (pairCard1 != null)
                            pairValue = pairCard1.getCardValue();
                        else
                            System.out.println("ERROR");
                        int index1 = 0;
                        int index2 = 0;
                        Card card1;
                        Card card2;
                        for (int i = 0; i < 3; ++i){
                            do {
                                card1 = cards1[index1++];
                            } while (card1.getCardValue() == pairValue);
                            do {
                                card2 = cards2[index2++];
                            } while (card2.getCardValue() == pairValue);
                            biggerCard = CardValue.getBiggerCard(card1, card2);
                            if (biggerCard != null)
                                break;
                        }
                        if (biggerCard != null)
                            winner = biggerCard.getPlayer();
                        else {
                            biggerCard = CardSuit.getBiggerCard(pairCard1, pairCard2);
                            if (biggerCard != null)
                                winner = biggerCard.getPlayer();
                            else
                                System.out.println("ERROR");
                        }
                    }
                    break;
                case TWO_PAIRS:
                    Card pairBigCard1 = CardType.getCardPair(cards1);
                    Card pairBigCard2 = CardType.getCardPair(cards2);
                    biggerCard = CardValue.getBiggerCard(pairBigCard1, pairBigCard2);
                    if (biggerCard != null)
                        winner = biggerCard.getPlayer();
                    else{
                        Card pairSmallCard1 = CardType.getCardPairSmall(cards1);
                        Card pairSmallCard2 = CardType.getCardPairSmall(cards2);
                        biggerCard = CardValue.getBiggerCard(pairSmallCard1, pairSmallCard2);
                        if (biggerCard != null)
                            winner = biggerCard.getPlayer();
                        else{
                            Card card1 = null;
                            Card card2 = null;
                            for (int i = 0; i < Card.cardsPerPlayer; ++i){
                                if (cards1[i] != pairBigCard1 && cards1[i] != pairSmallCard1)
                                    card1 = cards1[i];
                                if (cards2[i] != pairBigCard2 && cards2[i] != pairSmallCard2)
                                    card2 = cards2[i];
                                if (card1 != null && card2 != null)
                                    break;
                            }
                            biggerCard = CardValue.getBiggerCard(card1, card2);
                            if (biggerCard != null)
                                winner = biggerCard.getPlayer();
                            else{
                                biggerCard = CardSuit.getBiggerCard(pairBigCard1, pairBigCard2);
                                if (biggerCard != null)
                                    winner = biggerCard.getPlayer();
                                else
                                    System.out.println("ERROR");
                            }
                        }
                    }
                    break;
                case THREE_OF_A_KIND:
                    Card cardSameThree1 = CardType.getCardSame(cards1);
                    Card cardSameThree2 = CardType.getCardSame(cards2);
                    biggerCard = CardValue.getBiggerCard(cardSameThree1, cardSameThree2);
                    if (biggerCard != null)
                        winner = biggerCard.getPlayer();
                    else
                        System.out.println("ERROR");
                    break;
                case STRAIGHT:
                    biggerCard = CardValue.getBiggerCard(cards1[0], cards2[0]);
                    if (biggerCard != null)
                        winner = biggerCard.getPlayer();
                    else{
                        biggerCard = CardSuit.getBiggerCard(cards1[0], cards2[0]);
                        if (biggerCard != null)
                            winner = biggerCard.getPlayer();
                        else
                            System.out.println("ERROR");
                    }
                    break;
                case FLUSH:
                    biggerCards = Card.getBiggerCards(cards1, cards2);
                    if (biggerCards != null)
                        winner = biggerCards[0].getPlayer();
                    else
                        System.out.println("ERROR");
                    break;
                case FULL_HOUSE:
                    Card cardSame1 = CardType.getCardSame(cards1);
                    Card cardSame2 = CardType.getCardSame(cards2);
                    biggerCard = CardValue.getBiggerCard(cardSame1, cardSame2);
                    if (biggerCard != null)
                        winner = biggerCard.getPlayer();
                    else
                        System.out.println("ERROR");
                    break;
                case FOUR_OF_A_KIND:
                    Card cardSameFour1 = CardType.getCardSame(cards1);
                    Card cardSameFour2 = CardType.getCardSame(cards2);
                    biggerCard = CardValue.getBiggerCard(cardSameFour1, cardSameFour2);
                    if (biggerCard != null)
                        winner = biggerCard.getPlayer();
                    else
                        System.out.println("ERROR");
                    break;
                case STRAIGHT_FLUSH:
                    winner = Card.getBiggerCard(cards1[0], cards2[0]).getPlayer();
            }
        }

        return winner;
    }
    // 结束游戏
    private void endGame(){
        pot = 0;
        wager = 0;
        winner = null;
        round = 0;
        playerAlive = playerAmount;
        for (int i = 0; i < CardSuit.getAmount(); ++i)
            for (int j = 0; j < CardValue.getAmount(); ++j)
                cardIsOut[i][j] = false;

        int i = 0;
        while (i < playerAmount)
            if (players[i] != null) {
                players[i].endGame();
                ++i;
            }
    }
    // 下一个未放弃的玩家
    Player getNextPlayer(Player player){
        if (playerAlive == 1)
            for (int i = 0; i < playerAmountMax; ++i)
                if (!players[i].getGiveUp())
                    return players[i];
        do {
            player = players[(player.getSeat() + 1) % playerAlive];
        } while (player.getGiveUp());
        return player;
    }

    Player getGameWinner(){
        Player player1 = players[0];
        Player player2;
        while (player1.getGiveUp())
            player1 = getNextPlayer(player1);

        Player winner = player1;
        if (playerAlive != 1) {
            player2 = getNextPlayer(player1);
            // 每次与下一个人比较
            while (playerAlive != 1) {
                winner = getBiggerPlayer(player1, player2);
                if (winner == player1)
                    player2.giveUp();
                else if (winner == player2)
                    player1.giveUp();
            }
        }
        return winner;
    }

    public void startGame(){
        for (int i = playerAmount - 1; i >= 0; --i) {
            if (players[i].getMoney() < wagerMin)
                reducePlayer(players[i]);
            while (!players[i].getReady()){
                players[i].setReady();
            }
        }
        if(playerAmount < 2){
            System.out.println("Too short player amounts");
            return;
        }

        // 发一张暗牌，并下底注
        for (int i = 0; i < playerAmount; ++i) {
            players[i].makeBet(200);
            sendCard(players[i]);
        }
        ++round;

        // 每人发牌到5张
        while (round < Card.cardsPerPlayer){
            // 发一张明牌
            for (int i = 0; i < playerAmount; ++i)
                sendCard(players[i]);
            ++round;

            Player player = getBiggestCardPlayer();
            for (int i = 0; i < playerAlive; ++i) {
                player.action();
                player = getNextPlayer(player);
            }
            // 只剩一个人，胜者直接产生
            if (playerAlive == 1){
                winner = player;
                while (winner.getGiveUp())
                    winner = getNextPlayer(winner);
                break;
            }
        }

        // 胜者在剩下存活的人中产生
        for (int i = 0; i < playerAmount; ++i) {
            Player player = players[i];
            if (!player.getGiveUp()) {
                System.out.print(player.getName() + "的牌：");
                for (int j = 0; j < player.getCardAmount(); ++j){
                    System.out.print("  ");
                    player.getCards()[j].show();
                }
            }
            else {
                System.out.print(player.getName() + "认输");
            }
            System.out.println();
        }
        winner = getGameWinner();
        winner.winBet();
        endGame();
    }
}
