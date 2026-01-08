package main;

import logic.*;
import pieces.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class TestRunner {

    public static void main(String[] args) {
        System.out.println("=========================================");
        System.out.println("       RULARE SUITE DE TESTE AUTOMATE    ");
        System.out.println("=========================================\n");

        testUserLogin();
        testPieceMovement();
        testPawnPromotion();
        testCheckMateLogic();
        testComputerMove();

        System.out.println("\n=========================================");
        System.out.println("       TOATE TESTELE S-AU FINALIZAT      ");
        System.out.println("=========================================");
    }

    private static void testUserLogin() {
        System.out.println("--- TEST 1: Login și Încărcare Date ---");
        try {
            Path accPath = Paths.get( "src", "input", "accounts.json");
            List<User> users = JsonReaderUtil.readUsers(accPath);

            if (users.isEmpty()) {
                System.out.println("[FAIL] Nu s-au încărcat utilizatori din fișier.");
            } else {
                System.out.println("[PASS] S-au încărcat " + users.size() + " utilizatori.");

                boolean found = false;
                for(User u : users) {
                    if(u.getEmail().contains("@")) { // Verificare simplă
                        found = true;
                        break;
                    }
                }
                if (found) System.out.println("[PASS] Formatul utilizatorilor pare corect.");
            }
        } catch (Exception e) {
            System.out.println("[FAIL] Eroare la citire: " + e.getMessage());
        }
        System.out.println();
    }

    private static void testPieceMovement() {
        System.out.println("--- TEST 2: Mutare și Captură ---");
        try {
            Board board = new Board();
            board.clear();

            Position startPos = new Position('A', 1);
            Position endPos = new Position('A', 5);

            Piece rook = new Rook(Colors.WHITE, startPos);
            Piece pawn = new Pawn(Colors.BLACK, endPos);

            board.setPiece(rook);
            board.setPiece(pawn);

            Player p = new Player("Tester", Colors.WHITE);

            p.makeMove(startPos, endPos, board);


            Piece pieceAtDest = board.getPieceAt(endPos);
            if (pieceAtDest != null && pieceAtDest.type() == 'R' && pieceAtDest.getColor() == Colors.WHITE) {
                System.out.println("[PASS] Turnul a capturat Pionul cu succes.");
            } else {
                System.out.println("[FAIL] Turnul nu se află la destinație.");
            }

        } catch (Exception e) {
            System.out.println("[FAIL] Eroare la mutare: " + e.getMessage());
        }
        System.out.println();
    }

    // --- TEST 3: PROMOVARE PION ---
    private static void testPawnPromotion() {
        System.out.println("--- TEST 3: Promovare Pion la Regină ---");
        try {
            Board board = new Board();
            board.clear();
            Position start = new Position('A', 7);
            Position end = new Position('A', 8);
            Piece pawn = new Pawn(Colors.WHITE, start);
            board.setPiece(pawn);

            Player p = new Player("Tester", Colors.WHITE);
            p.makeMove(start, end, board);
            Piece movedPiece = board.getPieceAt(end);
            if (movedPiece != null && movedPiece.type() == 'P' && end.getRow() == 8) {
                board.promoteToQueen(end, Colors.WHITE);
            }

            Piece promoted = board.getPieceAt(end);
            if (promoted != null && promoted.type() == 'Q') {
                System.out.println("[PASS] Pionul a devenit Regină.");
            } else {
                System.out.println("[FAIL] Promovarea a eșuat. Piesa este: " + (promoted == null ? "null" : promoted.type()));
            }

        } catch (Exception e) {
            System.out.println("[FAIL] Eroare: " + e.getMessage());
        }
        System.out.println();
    }

    private static void testCheckMateLogic() {
        System.out.println("--- TEST 4: Logică Șah Mat ---");
        try {
            // SCENARIU 1: Ladder Mate (Mat cu două turnuri de la distanță)
            // Rege Alb: H1 (prins în colț)
            // Turn Negru 1: G8 (Taie coloana G, deci regele nu poate fugi la stânga)
            // Turn Negru 2: H8 (Dă șah pe coloana H de la distanță mare)

            Game game = new Game(new Player("W", Colors.WHITE), new Player("B", Colors.BLACK));
            Board board = game.getBoard();
            board.clear();

            board.setPiece(new King(Colors.WHITE, new Position('H', 1)));
            board.setPiece(new King(Colors.BLACK, new Position('A', 8))); // Regele negru departe

            // Turnuri negre
            board.setPiece(new Rook(Colors.BLACK, new Position('G', 8)));
            board.setPiece(new Rook(Colors.BLACK, new Position('H', 8))); // Atacă de la distanță!

            game.setCurrentPlayerIndex(0); // Tura albului

            if (game.checkForMate()) {
                System.out.println("[PASS] Scenariul 1 (Ladder Mate): Mat detectat corect.");
            } else {
                System.out.println("[FAIL] Scenariul 1: Sistemul nu a detectat matul.");
            }

            // SCENARIU 2: Fool's Mate simplificat
            board.clear();
            board.setPiece(new King(Colors.WHITE, new Position('E', 1)));
            board.setPiece(new Queen(Colors.BLACK, new Position('E', 2))); // Regina în față
            board.setPiece(new King(Colors.BLACK, new Position('E', 3))); // Regele negru apără regina

            game.setCurrentPlayerIndex(0);

            if (game.checkForMate()) {
                System.out.println("[PASS] Scenariul 2 (Fool's Mate): Mat detectat corect.");
            } else {
                System.out.println("[FAIL] Scenariul 2: Matul nu a fost detectat.");
            }

        } catch (Exception e) {
            System.out.println("[FAIL] Eroare logică mat: " + e.getMessage());
        }
        System.out.println();
    }

    private static void testComputerMove() {
        System.out.println("--- TEST 5: Generare Mutare Computer ---");
        try {
            Game game = new Game(new Player("Human", Colors.WHITE), new Player("Computer", Colors.BLACK));
            game.getBoard().initializeBoard();

            game.setCurrentPlayerIndex(1);
            Player comp = game.getCurrentPlayer();

            if (!comp.getName().equalsIgnoreCase("Computer")) {
                System.out.println("[FAIL] Jucătorul curent nu este Computer.");
                return;
            }
            List<ChessPair<Position, Piece>> pieces = game.getBoard().getPiecesByColor(Colors.BLACK);
            boolean hasMoves = false;
            for(ChessPair<Position, Piece> p : pieces) {
                if (!p.getValue().getPossibleMoves(game.getBoard()).isEmpty()) {
                    hasMoves = true;
                    break;
                }
            }

            if (hasMoves) {
                System.out.println("[PASS] Computerul are mutări valide disponibile la începutul jocului.");
            } else {
                System.out.println("[FAIL] Computerul nu găsește nicio mutare validă.");
            }

        } catch (Exception e) {
            System.out.println("[FAIL] Eroare Computer: " + e.getMessage());
        }
        System.out.println();
    }
}
