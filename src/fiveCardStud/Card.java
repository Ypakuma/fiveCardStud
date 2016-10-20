package fiveCardStud;

import java.util.Arrays;
import java.util.Random;

class Card {
    private static final Random cardCreator = new Random();
    static final int cardsPerPlayer = 5;

    private CardSuit cardSuit;
    private CardValue cardValue;
    private Player player;

    Card(){
        cardSuit = CardSuit.values()[cardCreator.nextInt(CardSuit.getAmount())];
        cardValue = CardValue.values()[cardCreator.nextInt(CardValue.getAmount())];
        player = null;
    }
    Card(Card card){
        cardSuit = card.getCardSuit();
        cardValue = card.getCardValue();
        player = null;
    }
    Card(CardSuit suit, CardValue value){
        cardSuit = suit;
        cardValue = value;
        player = null;
    }

    void setPlayer(Player player) {
        this.player = player;
    }
    Player getPlayer() {
        return player;
    }
    CardSuit getCardSuit() {
        return cardSuit;
    }
    CardValue getCardValue() {
        return cardValue;
    }
    // 返回两张牌中较大的，先比较数值，再比较花色
    static Card getBiggerCard(Card card1, Card card2){
        if (card1.cardValue.compareTo(card2.cardValue) < 0)
            return card2;
        else if (card1.cardValue.compareTo(card2.cardValue) > 0)
            return card1;
        else if (card1.cardSuit.compareTo((card2.cardSuit)) < 0)
            return card2;
        else
            return card1;
    }
    // 返回已经按降序排列的两副牌中点数较大的那副，如果点数完全相同，返回null
    static Card[] getBiggerCards(Card[] cards1, Card[] cards2){
        for (int i = 0; i < Card.cardsPerPlayer; ++i) {
            Card biggerCard = CardValue.getBiggerCard(cards1[i], cards2[i]);
            if (biggerCard == cards1[i])
                return cards1;
            else if (biggerCard == cards2[i])
                return cards2;
        }
        return null;
    }

    void show(){
        cardSuit.show();
        cardValue.show();
    }
}

enum CardSuit {
    DIAMOND,    //方片
    CLUB,       //草花
    HEART,      //红桃
    SPADE;      //黑桃
    static int getAmount(){
        return 4;
    }
    int getIndex(){
        int index = 0;
        switch (this){
            case DIAMOND:
                index = 0;
                break;
            case CLUB:
                index = 1;
                break;
            case HEART:
                index = 2;
                break;
            case SPADE:
                index = 3;
                break;
        }
        return index;
    }
    void show(){
        switch (this){
            case DIAMOND:
                System.out.print("方块♦");
                break;
            case CLUB:
                System.out.print("梅花♣");
                break;
            case HEART:
                System.out.print("红心♥");
                break;
            case SPADE:
                System.out.print("黑桃♠");
                break;
        }
    }
    // 返回两张牌花色较大的牌的玩家，如果相等，返回null
    static Card getBiggerCard(Card card1, Card card2){
        if (card1.getCardSuit().compareTo(card2.getCardSuit()) > 0)
            return card1;
        else if (card1.getCardSuit().compareTo(card2.getCardSuit()) < 0)
            return card2;
        else
            return null;
    }
}

enum CardValue {
    EIGHT(8),
    NINE(9),
    TEN(10),
    JOKER(11),
    QUEEN(12),
    KING(13),
    ACE(14);

    int getIndex(){
        return getValue() - EIGHT.getValue();
    }
    int value;
    CardValue(int value_){
        value = value_;
    }
    int getValue(){
        return value;
    }
    static int getAmount(){
        return 7;
    }
    void show(){
        switch (this){
            case EIGHT:
                System.out.print("8");
                break;
            case NINE:
                System.out.print("9");
                break;
            case TEN:
                System.out.print("10");
                break;
            case JOKER:
                System.out.print("J");
                break;
            case QUEEN:
                System.out.print("Q");
                break;
            case KING:
                System.out.print("K");
                break;
            case ACE:
                System.out.print("A");
                break;
        }
    }
    // 返回两张牌数值较大的牌的玩家，如果相等，返回null
    static Card getBiggerCard(Card card1, Card card2){
        if (card1.getCardValue().compareTo(card2.getCardValue()) > 0)
            return card1;
        else if (card1.getCardValue().compareTo(card2.getCardValue()) < 0)
            return card2;
        else
            return null;
    }
}

enum CardType{
    HIGH_CARD,          //高牌
    ONE_PAIR,           //一对
    TWO_PAIRS,          //两对
    THREE_OF_A_KIND,    //三条
    STRAIGHT,           //顺子
    FLUSH,              //同花
    FULL_HOUSE,         //葫芦
    FOUR_OF_A_KIND,     //四条
    STRAIGHT_FLUSH;     //同花顺

    static CardType getCardType(Card[] cards){
        boolean isFlush = true;
        boolean isStraight = true;
        int numEqual = 1;
        int numPair = 0;

        // 降序排列，大牌在前
        Arrays.sort(cards, (card1, card2)->(Card.getBiggerCard(card1, card2) == card1) ? -1 : 1);

        int[] difference = new int[4];
        CardSuit cardSuit = cards[0].getCardSuit();

        int tmpEqual = 1;
        for (int i = 0; i < 4; ++i){
            difference[i] = cards[i].getCardValue().getValue() - cards[i + 1].getCardValue().getValue();

            if (!cards[i + 1].getCardSuit().equals(cardSuit))
                isFlush = false;

            if (difference[i] != 1)
                isStraight = false;

            if (difference[i] == 0){
                ++tmpEqual;
                if (tmpEqual == 2)
                    ++numPair;
                else if (tmpEqual == 3)
                    --numPair;
            }
            else
                tmpEqual = 1;

            if (tmpEqual > numEqual)
                numEqual = tmpEqual;
        }

        // 返回结果
        if (isFlush && isStraight)
            return STRAIGHT_FLUSH;
        else if (numEqual == 4)
            return FOUR_OF_A_KIND;
        else if (numEqual == 3 && numPair == 1)
            return FULL_HOUSE;
        else if (isFlush)
            return FLUSH;
        else if (isStraight)
            return STRAIGHT;
        else if (numEqual == 3)
            return THREE_OF_A_KIND;
        else if (numPair == 2)
            return TWO_PAIRS;
        else if (numPair == 1)
            return ONE_PAIR;
        else
            return HIGH_CARD;
    }
    // 返回两组牌牌型较大的牌的玩家，如果相等，返回null
    static Card[] getBiggerCards(Card[] cards1, Card[] cards2){
        CardType cardType1 = getCardType(cards1);
        CardType cardType2 = getCardType(cards2);
        if (cardType1.compareTo(cardType2) > 0)
            return cards1;
        else if (cardType1.compareTo(cardType2) < 0)
            return cards2;
        else
            return null;
    }
    // 获取“一对”里对子或“两对”里大对子中较大的牌
    static Card getCardPair(Card[] cards){
        if (getCardType(cards) != ONE_PAIR && getCardType(cards) != TWO_PAIRS)
            return null;
        for (int i = 0; i < Card.cardsPerPlayer - 1; ++i)
            if (cards[i].getCardValue() == cards[i + 1].getCardValue())
                return cards[i];
        return null;
    }
    // 获取“两对”中小对子中较大的牌
    static Card getCardPairSmall(Card[] cards){
        if (getCardType(cards) != TWO_PAIRS)
            return null;
        int numPairs = 0;
        for (int i = 0; i < Card.cardsPerPlayer - 1; ++i) {
            if (cards[i].getCardValue() == cards[i + 1].getCardValue())
                ++numPairs;
            if (numPairs == 2)
                return cards[i];
        }
        return null;
    }
    // 获取三张一样牌的点数
    static Card getCardSame(Card[] cards){
        if (getCardType(cards) != THREE_OF_A_KIND && getCardType(cards) != FULL_HOUSE && getCardType(cards) != FOUR_OF_A_KIND)
            return null;
        return cards[2];
    }
}
