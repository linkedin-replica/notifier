package com.linkedin.replica.notifier.cache;

import com.linkedin.replica.notifier.config.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;

public class Cache {
    private Configuration configuration = Configuration.getInstance();
    private final String REDIS_IP = configuration.getAppConfigProp("redis.ip");
    private final int REDIS_PORT = Integer.parseInt(configuration.getAppConfigProp("redis.port"));
    private JedisPool redisPool;

    private static Cache cache;

    private Cache() {
        redisPool = new JedisPool(new JedisPoolConfig(), REDIS_IP, REDIS_PORT);
    }

    /**
     * Get a singleton cache instance
     *
     * @return The cache instance
     */
    public static Cache getInstance() {
        if (cache == null) {
            synchronized (Cache.class) {
                if (cache == null)
                    cache = new Cache();
            }
        }
        return cache;
    }

    public JedisPool getRedisPool() {
        return redisPool;
    }

    /** Destroys pool */
    public void destroyRedisPool(){
        redisPool.destroy();
    }
}

