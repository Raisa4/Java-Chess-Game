import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    private static Main instance;

    private List<User> users;
    private Map<Integer, Game> gamesMap;
    private User currentUser;

    private JFrame window;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private GamePanel gamePanel;

    private Main() {
        this.users = new ArrayList<>();
        this.gamesMap = new HashMap<>();

        read(); //loads data

        initializeGUI();
    }

    public static synchronized Main getInstance() {
        if (instance == null) {
            instance = new Main();
        }
        return instance;
    }

    private void initializeGUI() {
        window = new JFrame("Chess Master - POO 2025");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(900, 700);
        window.setLocationRelativeTo(null); //center on screen

        //save data
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                write();
            }
        });

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(new LoginPanel(), "LOGIN");
        mainPanel.add(new RegisterPanel(), "REGISTER");
        mainPanel.add(new GamesListPanel(), "GAMES_LIST");

        MenuPanel menuPanel = new MenuPanel();
        mainPanel.add(menuPanel, "MENU");

        gamePanel = new GamePanel();
        mainPanel.add(gamePanel, "GAME");


        window.add(mainPanel);
        window.setVisible(true);
    }
    public void resumeGame(Game game) {
        if (game == null) return;

        try {
            gamePanel.setGame(game);
            showScreen("GAME");
        } catch (Exception e) {
            System.out.println("Error resuming game: " + e.getMessage());
        }
    }

    public void deleteGame(Game game) {
        if (currentUser != null) {
            currentUser.removeGame(game);
            gamesMap.remove(game.getID());
            write();
        }
    }

    public void showScreen(String screenName) {
        cardLayout.show(mainPanel, screenName);

        if (screenName.equals("MENU")) {
            for (Component comp : mainPanel.getComponents()) {
                if (comp instanceof MenuPanel) ((MenuPanel) comp).refreshStats();
            }
        }
        if (screenName.equals("GAMES_LIST")) {
            for (Component comp : mainPanel.getComponents()) {
                if (comp instanceof GamesListPanel) ((GamesListPanel) comp).refreshGameList();
            }
        }
    }


    public boolean login(String email, String password) {
        for (User u : users) {
            if (u.getEmail().equals(email) && u.getPassword().equals(password)) {
                this.currentUser = u;
                System.out.println("Login successful: " + u.getEmail());
                return true;
            }
        }
        return false;
    }

    public boolean register(String email, String password) {
        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                return false;
            }
        }
        User newUser = new User(email, password, 0, new ArrayList<>());
        this.users.add(newUser);
        System.out.println("Account created: " + email);
        return true;
    }


    public void startNewGame() {

        Player p1 = new Player(currentUser.getEmail().split("@")[0], Colors.WHITE); // Use simplified name
        Player p2 = new Player("Computer", Colors.BLACK);

        Game newGame = new Game(p1, p2);


        int newId = gamesMap.isEmpty() ? 1 : java.util.Collections.max(gamesMap.keySet()) + 1;
        newGame.setId(newId);

        newGame.start();

        gamesMap.put(newId, newGame);
        currentUser.addGame(newGame);

        try {
            gamePanel.setGame(newGame);
            showScreen("GAME");
        } catch (Exception e) {
            System.out.println("Error starting game: " + e.getMessage());
        }
    }

    public void read() {
        try {
            Path accPath = Paths.get("src", "input", "accounts.json");
            Path gamePath = Paths.get("src", "input", "games.json");

            if (Files.exists(accPath)) {
                this.users = JsonReaderUtil.readUsers(accPath);
            }
            if (Files.exists(gamePath)) {
                this.gamesMap = JsonReaderUtil.readGamesAsMap(gamePath);
            }

            System.out.println("Loaded " + users.size() + " users.");

            if (users != null && gamesMap != null) {
                for (User user : users) {
                    for (Integer gameId : user.getGameIDs()) {
                        Game g = gamesMap.get(gameId);
                        if (g != null) {
                            user.addGame(g);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading files: " + e.getMessage());

            this.users = new ArrayList<>();
            this.gamesMap = new HashMap<>();
        }
    }

    public void write() {
        try {
            Path accPath = Paths.get("src", "input", "accounts.json");
            Path gamePath = Paths.get("src", "input", "games.json");

            System.out.println("Saving data...");
            JsonWriterUtil.writeUsers(accPath, users);
            JsonWriterUtil.writeGames(gamePath, gamesMap);
            System.out.println("Data saved successfully!");
        } catch (Exception e) {
            System.out.println("Error saving files: " + e.getMessage());
        }
    }

    public User getCurrentUser() { return currentUser; }
    public Map<Integer, Game> getGamesMap() { return gamesMap; }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main.getInstance();
        });
    }
}