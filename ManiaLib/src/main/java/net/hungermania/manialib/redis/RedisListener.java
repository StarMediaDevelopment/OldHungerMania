package net.hungermania.manialib.redis;

/**
 * Copyright (C) 2015-2019 z609, all rights reserved. Created by albert: December 30, 2019 - 20:39
 */
public interface RedisListener {

	void onCommand(String cmd, String[] args);

}
