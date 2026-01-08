package main;

import logic.*;
import pieces.Piece;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class JsonWriterUtil {
    public static void writeUsers(Path path, List<User> users) {
        JSONArray usersList = new JSONArray();

        for (User user : users) {
            JSONObject userObj = new JSONObject();
            userObj.put("email", user.getEmail());
            userObj.put("password", user.getPassword());
            userObj.put("points", user.getPoints());

            JSONArray gamesIds = new JSONArray();
            gamesIds.addAll(user.getGameIDs());
            userObj.put("games", gamesIds);

            usersList.add(userObj);
        }

        try (FileWriter file = new FileWriter(path.toString())) {
            file.write(usersList.toJSONString());
            file.flush();
        } catch (IOException e) {
            System.out.println("Error writing users: " + e.getMessage());
        }
    }
    //save games to games.json
    public static void writeGames(Path path, Map<Integer, Game> gamesMap) {
        JSONArray gamesList = new JSONArray();

        for (Game game : gamesMap.values()) {
            JSONObject gameObj = new JSONObject();
            gameObj.put("id", game.getID());

            //save players
            JSONArray playersArr = new JSONArray();
            addPlayerJson(playersArr, game.getPlayer1());
            addPlayerJson(playersArr, game.getPlayer2());
            gameObj.put("players", playersArr);

            //save current turn
            gameObj.put("currentPlayerColor", game.getCurrentPlayer().getColor().toString());

            //save board
            JSONArray boardArr = new JSONArray();
            savePieces(boardArr, game.getBoard(), Colors.WHITE);
            savePieces(boardArr, game.getBoard(), Colors.BLACK);
            gameObj.put("board", boardArr);

            //save moves
            JSONArray movesArr = new JSONArray();
            if (game.getMoves() != null) {
                for (Move m : game.getMoves()) {
                    JSONObject moveObj = new JSONObject();
                    moveObj.put("playerColor", m.getMoveColor().toString());
                    moveObj.put("from", m.getPosFrom().toString());
                    moveObj.put("to", m.getPosTo().toString());
                    movesArr.add(moveObj);
                }
            }
            gameObj.put("moves", movesArr);

            gamesList.add(gameObj);
        }

        try (FileWriter file = new FileWriter(path.toString())) {
            file.write(gamesList.toJSONString());
            file.flush();
        } catch (IOException e) {
            System.out.println("Error writing games: " + e.getMessage());
        }
    }

    private static void addPlayerJson(JSONArray arr, Player p) {
        JSONObject pObj = new JSONObject();
        pObj.put("email", p.getName());
        pObj.put("color", p.getColor().toString());
        arr.add(pObj);
    }

    private static void savePieces(JSONArray arr, Board board, Colors color) {
        List<ChessPair<Position, Piece>> pieces = board.getPiecesByColor(color);
        for (ChessPair<Position, Piece> pair : pieces) {
            Piece p = pair.getValue();
            JSONObject pObj = new JSONObject();
            pObj.put("type", String.valueOf(p.type()));
            pObj.put("color", p.getColor().toString());
            pObj.put("position", p.getPosition().toString());
            arr.add(pObj);
        }
    }
}
