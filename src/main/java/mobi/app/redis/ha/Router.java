package mobi.app.redis.ha;

import mobi.app.redis.RedisClient;

import java.util.Collection;
import java.util.List;

/**
 * User: thor
 * Date: 13-1-7
 * Time: 上午10:24
 */
public interface Router {
    RedisClient route(String key);

    Collection<RedisClient> getClients();
}
