package mobi.app.test;

import junit.framework.TestCase;
import mobi.app.redis.RedisClient;
import mobi.app.redis.netty.SyncRedisClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * User: thor
 * Date: 14-5-21
 * Time: 下午1:16
 */
public class TestMuiltThread extends TestCase {
    static RedisClient client = new SyncRedisClient("127.0.0.1:6379", 1, null, 10, TimeUnit.SECONDS, 1);

    public void testHgetAndHset() throws ExecutionException, InterruptedException {
       final  CountDownLatch latch = new CountDownLatch(1);
        final  CountDownLatch resultLatch = new CountDownLatch(10);
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            final int id = i;
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    for (int i=0;i <10000; i++) {
                        String key = "HASH_KEY" + id + "-" + i;
                        client.delete(key);
                        String field = "TEST_FIELD";
                        long reply = client.hset(key, field, "OK");
                        assertEquals(1, reply);
                        reply = client.hset(key, field, "KO");
                        assertEquals(0, reply);
                        String cached = (String) client.hget(key, field);
                        assertEquals("KO", cached);
                        reply = client.hset(key, field, 1.01);
                        assertEquals(0, reply);
                        double cachedDouble = client.hgetDouble(key, field);
                        assertEquals(1.01, cachedDouble);

                    }
                    resultLatch.countDown();
                }
            });
            thread.start();
        }
        latch.countDown();
        resultLatch.await();
        System.out.println(System.currentTimeMillis() - start);
    }

    public void testHgetAndHset1() throws Exception {
       final JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");
//        final Jedis jedis = new Jedis("localhost");
//        jedis.connect();
        final  CountDownLatch latch = new CountDownLatch(1);
        final  CountDownLatch resultLatch = new CountDownLatch(10);
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            final int id = i;
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Jedis jedis = pool.getResource();
                    for (int i=0;i <10000; i++) {
                        String key = "HASH_KEY" + id + "-" + i;
                        jedis.del(key);
                        String field = "TEST_FIELD";
                        long reply = jedis.hset(key, field, "OK");
//            assertEquals(1, reply);
                        reply = jedis.hset(key, field, "KO");
//            assertEquals(0, reply);
                        String cached =  jedis.hget(key, field);
//            assertEquals("KO", cached);

                        reply = jedis.hset(key, field, "1.01");
//            assertEquals(0, reply);
                        String cachedDouble = jedis.hget(key, field);
                        double cachedDoubleV = Double.valueOf(cachedDouble);
//            assertEquals("1.01", cachedDouble);

                    }
                    resultLatch.countDown();
                    pool.returnResource(jedis);
                }
            });
            thread.start();
        }
        latch.countDown();
        resultLatch.await();
        System.out.println(System.currentTimeMillis() - start);
    }

}
