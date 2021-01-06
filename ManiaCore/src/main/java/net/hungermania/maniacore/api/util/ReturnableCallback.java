package net.hungermania.maniacore.api.util;

public interface ReturnableCallback<T, R> {
    R callback(T t);
}
