public class Position implements Comparable<Position> {
    private char column;
    private int row;
    public char getColumn() {
        return column;
    }
    public void setColumn(char column) throws InvalidMoveException {
        char normalizedColumn = Character.toUpperCase(column);
        if (normalizedColumn < 'A' || normalizedColumn > 'H') {
            throw new InvalidMoveException("Invalid column: " + column);
        }
        this.column = normalizedColumn;
    }
    public int getRow() {
        return row;
    }
    public void setRow(int row) throws InvalidMoveException {
        if (row < 1 || row > 8) {
            throw new InvalidMoveException("Invalid row: " + row);
        }
        this.row = row;
    }
    public Position(char column, int row) throws InvalidMoveException {
        setColumn(column);
        setRow(row);
    }
    @Override
    public String toString() {
        return "" + getColumn() + getRow();
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return getColumn() == position.getColumn() && getRow() == position.getRow();
    }
    @Override
    public int compareTo(Position other) {
        int rowDifference = this.getRow() - other.getRow();
        if (rowDifference != 0) {
            return rowDifference;
        }
        return this.getColumn() - other.getColumn();
    }

}
