package net.hungermania.manialib.util;

import java.util.Objects;

public class Range<T> {
    private int min, max;
    private T object;
    
    public Range(int min, int max, T object) {
        this.min = min;
        this.max = max;
        this.object = object;
    }
    
    public T getObject() {
        return object;
    }
    
    public boolean contains(int number) {
        return (number >= min && number <= max);
    }
    
    public int getMin() {
        return min;
    }
    
    public int getMax() {
        return max;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Range<?> range = (Range<?>) o;
        return min == range.min && max == range.max;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(min, max);
    }
}