package com.linkedin.replica.notifier.cache.impl;

import com.google.gson.Gson;
import com.linkedin.replica.notifier.cache.Cache;
import com.linkedin.replica.notifier.cache.NotifierCacheHandler;
import com.linkedin.replica.notifier.config.Configuration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

import java.io.IOException;

public class JedisCacheHandler implements NotifierCacheHandler {

    private static Gson gson;
    private static JedisPool cachePool;
    private Configuration configuration = Configuration.getInstance();
    private int databaseIndex = Integer.parseInt(configuration.getAppConfigProp("cache.notifier.index"));
    private int fixedCacheSizePerUser = Integer.parseInt(configuration.getAppConfigProp("cache.notifier.size"));

    public JedisCacheHandler() {
        cachePool = Cache.getInstance().getRedisPool();
        gson = new Gson();
    }

    @Override
    public void saveNotification(String userId, Object Notification) {
        try(Jedis cacheInstance = cachePool.getResource()) {
            cacheInstance.select(databaseIndex);
            cacheInstance.lpush(userId, gson.toJson(Notification, Notification.getClass()));
            cacheInstance.ltrim(userId, 0, fixedCacheSizePerUser);
        } catch (JedisException jedisException) {
            jedisException.printStackTrace();
        }
    }
}
