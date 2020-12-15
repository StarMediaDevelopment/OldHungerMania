package net.hungermania.manialib.util;

public class Pair<K, V> {
    
    private K value1;
    private V value2;
    
    public Pair(K value1, V value2) {
        this.value1 = value1;
        this.value2 = value2;
    }
    
    public K getValue1() {
        return value1;
    }
    
    public V getValue2() {
        return value2;
    }
}