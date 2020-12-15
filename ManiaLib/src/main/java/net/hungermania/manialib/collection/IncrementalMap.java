package net.hungermania.manialib.collection;

import java.util.TreeMap;

/**
 * Custom collection for maps who's integer keys auto-increment.
 * @param <T>
 */
public class IncrementalMap<T> extends TreeMap<Integer, T> {
    public int add(T value) {
        int index = 0;
        if (!isEmpty()) {
            index = lastKey() + 1;
        }
        
        put(index, value);
        return index;
    }
}