package logic;

// We strictly need 'K extends Comparable' so we can sort by the Key (logic.Position)
public class ChessPair<K extends Comparable<K>, V> implements Comparable<ChessPair<K, V>> {
    private K key;
    private V value;

    public ChessPair(K key, V value) {
        this.key = key;
        this.value = value;
    }
    public K getKey() {
        return key;
    }
    public V getValue() {
        return value;
    }
    @Override
    public String toString() {
        return "(" + key + ", " + value + ")";
    }
    @Override
    public int compareTo(ChessPair<K, V> other) {
        return this.key.compareTo(other.getKey());
    }
}
