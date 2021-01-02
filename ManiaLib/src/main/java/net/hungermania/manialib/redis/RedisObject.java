package net.hungermania.manialib.redis;

import java.util.Map;

//Must have a constructor taking in Map<String, String> for redis data
public interface RedisObject {
    
    Map<String, String> serialize();
}
