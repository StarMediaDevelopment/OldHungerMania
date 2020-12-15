package net.hungermania.manialib.collection;

import java.util.TreeMap;

/**
 * Custom collection for maps who's integer keys auto-increment.
 * @param <T>
 */
public class IncrementalLongMap<T> extends TreeMap<Long, T> {
    public long add(T value) {
        long index = 0;
        if (!isEmpty()) {
            index = lastKey() + 1;
        }
        
        put(index, value);
        return index;
    }
}