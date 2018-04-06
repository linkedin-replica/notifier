package com.linkedin.replica.notifier.cache;

import java.io.IOException;

public interface NotifierCacheHandler extends CacheHandler{

    /**
     * Adds the list of recommended jobs to the cache
     * related to a specific user
     */
    void saveNotification(String userId, Object Notification) throws IOException;
}
