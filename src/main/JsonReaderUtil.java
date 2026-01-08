package main;

import logic.*;
import pieces.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for reading JSON documents using JSON.simple ("simple-json").
 *
 * ###### IMPORTANT: This is just an example of how to read JSON documents using the library.
 * Your classes might differ slightly, so don’t hesitate to update this class as needed.
 *
 * Expected structures:
 * - accounts.json: an array of objects with fields: email (String), password (String), points (Number), games (array of numbers)
 * - games.json: an array of objects with fields matching the JSON provided:
 * id (Number), players (array of {email, color}), currentPlayerColor (String),
 * board (array of {type, color, position}), moves (array of {playerColor, from, to})
 */

public final class JsonReaderUtil {

    private JsonReaderUtil() {
    }

    public static List<User> readUsers(Path path) throws IOException, ParseException {
        if (path == null || !Files.exists(path)) {
            return new ArrayList<>();
        }
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            JSONParser parser = new JSONParser();
            Object root = parser.parse(reader);
            JSONArray arr = asArray(root);
            List<User> result = new ArrayList<>();

            if (arr == null) {
                return result;
            }

            for (Object item : arr) {
                JSONObject obj = asObject(item);
                if (obj == null) continue;

                String email = asString(obj.get("email"));
                String password = asString(obj.get("password"));
                int points = asInt(obj.get("points"), 0);

                List<Integer> gameIds = new ArrayList<>();
                JSONArray games = asArray(obj.get("games"));
                if (games != null) {
                    for (Object gid : games) {
                        gameIds.add(asInt(gid, 0));
                    }
                }

                result.add(new User(email, password, points, gameIds));
            }
            return result;
        }
    }

    public static Map<Integer, Game> readGamesAsMap(Path path) throws IOException, ParseException {
        Map<Integer, Game> map = new HashMap<>();
        if (path == null || !Files.exists(path)) {
            return map;
        }
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            JSONParser parser = new JSONParser();
            Object root = parser.parse(reader);
            JSONArray arr = asArray(root);
            if (arr == null) return map;

            for (Object item : arr) {
                JSONObject obj = asObject(item);
                if (obj == null) continue;

                int id = asInt(obj.get("id"), -1);
                if (id < 0) continue;

                Player p1 = null;
                Player p2 = null;
                JSONArray playersArr = asArray(obj.get("players"));
                if (playersArr != null && playersArr.size() >= 2) {
                    JSONObject p1Obj = asObject(playersArr.get(0));
                    JSONObject p2Obj = asObject(playersArr.get(1));

                    Colors c1 = Colors.valueOf(asString(p1Obj.get("color")));
                    Colors c2 = Colors.valueOf(asString(p2Obj.get("color")));

                    p1 = new Player(asString(p1Obj.get("email")), c1);
                    p2 = new Player(asString(p2Obj.get("email")), c2);
                }

                Game g = new Game(p1, p2);
                g.setId(id);

                String turnColorStr = asString(obj.get("currentPlayerColor"));

                if (p1 != null && p1.getColor().toString().equals(turnColorStr)) {
                    g.setCurrentPlayerIndex(0);
                } else {
                    g.setCurrentPlayerIndex(1);
                }

                JSONArray boardArr = asArray(obj.get("board"));
                if (boardArr != null) {
                    Board board = g.getBoard();
                    board.clear();

                    for (Object bItem : boardArr) {
                        JSONObject bObj = asObject(bItem);
                        if (bObj == null) continue;

                        String type = asString(bObj.get("type"));
                        String colorStr = asString(bObj.get("color"));
                        String posStr = asString(bObj.get("position"));

                        try {
                            Colors color = Colors.valueOf(colorStr);
                            char colChar = posStr.charAt(0);
                            int rowInt = Character.getNumericValue(posStr.charAt(1));
                            Position pos = new Position(colChar, rowInt);

                            Piece piece = null;
                            switch (type) {
                                case "R": piece = new Rook(color, pos); break;
                                case "N": piece = new Knight(color, pos); break;
                                case "B": piece = new Bishop(color, pos); break;
                                case "Q": piece = new Queen(color, pos); break;
                                case "K": piece = new King(color, pos); break;
                                case "P": piece = new Pawn(color, pos); break;
                            }

                            if (piece != null) {
                                board.setPiece(piece);
                            }
                        } catch (Exception e) {
                            System.out.println("Err loading piece: " + e.getMessage());
                        }
                    }
                }

                JSONArray movesArr = asArray(obj.get("moves"));
                if (movesArr != null) {
                    List<Move> loadedMoves = new ArrayList<>();

                    for (Object mItem : movesArr) {
                        JSONObject mObj = asObject(mItem);
                        if (mObj == null) continue;

                        try {
                            String colorStr = asString(mObj.get("playerColor"));
                            Colors moveColor = Colors.valueOf(colorStr);

                            String fromStr = asString(mObj.get("from"));
                            String toStr = asString(mObj.get("to"));

                            Position from = new Position(fromStr.charAt(0), Character.getNumericValue(fromStr.charAt(1)));
                            Position to = new Position(toStr.charAt(0), Character.getNumericValue(toStr.charAt(1)));

                            Move move = new Move(moveColor, from, to, null);
                            loadedMoves.add(move);

                        } catch (Exception e) {
                            System.out.println("Skipping invalid move in JSON: " + e.getMessage());
                        }
                    }

                    g.setMoves(loadedMoves);
                }

                map.put(id, g);
            }
        }
        return map;
    }

    // -------- helper converters --------

    private static JSONArray asArray(Object o) {
        return (o instanceof JSONArray) ? (JSONArray) o : null;
    }

    private static JSONObject asObject(Object o) {
        return (o instanceof JSONObject) ? (JSONObject) o : null;
    }

    private static String asString(Object o) {
        return o == null ? null : String.valueOf(o);
    }

    private static int asInt(Object o, int def) {
        if (o instanceof Number) return ((Number) o).intValue();
        try {
            return o != null ? Integer.parseInt(String.valueOf(o)) : def;
        } catch (NumberFormatException e) {
            return def;
        }
    }
}