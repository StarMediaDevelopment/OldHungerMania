package net.hungermania.manialib.collection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ListMap<K, V> extends HashMap<K, List<V>> {

    public void add(K key, V value) {
        if (get(key) != null) {
            get(key).add(value);
        } else {
            put(key, new ArrayList<>(Collections.singletonList(value)));
        }
    }
}