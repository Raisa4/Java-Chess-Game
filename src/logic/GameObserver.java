package logic;

public interface GameObserver {
    void onMoveMade(Move move);
    void onTurnChanged(Colors newColor);
    void onGameEnd(String result, int points);
}