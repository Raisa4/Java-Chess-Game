package logic;

import java.util.ArrayList;
import java.util.List;

public class User{
    private String email;
    private String password;
    private int points;
    private List<Integer> gameIDs;
    private List<Game> activeGames;
    public User(String email, String password, int points, List<Integer> gameIds) {
        this.email = email;
        this.password = password;
        this.points = points;
        this.gameIDs = gameIds;
        this.activeGames = new ArrayList<>();
    }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public int getPoints() { return points; }
    public List<Integer> getGameIDs() { return gameIDs; }
    public List<Game> getActiveGames() { return activeGames; }
    public void addGame(Game game) {
        activeGames.add(game);
        if (!gameIDs.contains(game.getID())) {
            gameIDs.add(game.getID());
        }
    }
    public void setPoints(int i) {
        this.points = i;
    }
    public void removeGame(Game game) {
        if (this.activeGames != null) {
            this.activeGames.remove(game);
        }
        if (this.gameIDs != null) {
            this.gameIDs.remove((Integer) game.getID());
        }
    }
}
