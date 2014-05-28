package mobi.app.test;

import mobi.app.redis.RedisClient;
import mobi.app.redis.netty.SyncRedisClient;

import java.util.concurrent.TimeUnit;

/**
 * User: thor
 * Date: 13-1-6
 * Time: 下午3:53
 */
public class TestReconnect {
    public static void main(String[] args){
        RedisClient client = new SyncRedisClient("172.16.3.214:6379", 1, null, 1, TimeUnit.SECONDS, 1);
    }
}
