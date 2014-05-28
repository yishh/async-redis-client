package mobi.app.test;

import mobi.app.redis.RedisClient;
import mobi.app.redis.ha.KetamaRouter;
import mobi.app.redis.netty.SyncRedisClient;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * User: thor
 * Date: 13-1-9
 * Time: 下午2:04
 */
public class TestKetamaRouter {

    public static void main(String[] args) {
        Map<RedisClient, Integer> map = new HashMap<RedisClient, Integer>();
        map.put(new SyncRedisClient("172.16.3.213:6379", 0, null, 1, TimeUnit.SECONDS, 1), 1);
        map.put(new SyncRedisClient("172.16.3.213:6379", 1, null, 1, TimeUnit.SECONDS, 1), 1);
        map.put(new SyncRedisClient("172.16.3.213:6379", 2, null, 1, TimeUnit.SECONDS, 1), 1);
        map.put(new SyncRedisClient("172.16.3.213:6379", 3, null, 1, TimeUnit.SECONDS, 1), 1);
        map.put(new SyncRedisClient("172.16.3.213:6379", 4, null, 1, TimeUnit.SECONDS, 1), 1);
        map.put(new SyncRedisClient("172.16.3.213:6379", 5, null, 1, TimeUnit.SECONDS, 1), 1);

        Map<String, Integer> countMap = new HashMap<String, Integer>();
        countMap.put("172.16.3.213:6379|0", 0);
        countMap.put("172.16.3.213:6379|1", 0);
        countMap.put("172.16.3.213:6379|2", 0);
        countMap.put("172.16.3.213:6379|3", 0);
        countMap.put("172.16.3.213:6379|4", 0);
        countMap.put("172.16.3.213:6379|5", 0);
        KetamaRouter router = new KetamaRouter(map);
        for (int i = 0; i < 10000; i++) {
            String key = router.route("TEST_KEYS_" + i).hashKey();
            countMap.put(key, countMap.get(key) + 1);
        }
        for (String key : countMap.keySet()) {
            System.out.println(key + ":" + countMap.get(key) / 10000.00);
        }

    }

}
