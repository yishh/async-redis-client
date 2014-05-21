package mobi.app.test;

import junit.framework.TestCase;
import mobi.app.redis.RedisClient;
import mobi.app.redis.netty.SyncRedisClient;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * User: thor
 * Date: 14-5-21
 * Time: 下午1:16
 */
public class TestMuiltThread extends TestCase {
    static RedisClient client = new SyncRedisClient("172.16.3.214:6379", 1, null, 1, TimeUnit.SECONDS);

    public void testHgetAndHset() throws ExecutionException, InterruptedException {
       final  CountDownLatch latch = new CountDownLatch(1);
        final  CountDownLatch resultLatch = new CountDownLatch(100);
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            final int id = i;
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    for (int i=0;i <100; i++) {
                        String key = "HASH_KEY" + id + "-" + i;
                        client.delete(key);
                        String field = "TEST_FIELD";
                        long reply = client.hset(key, field, "OK");
//                        assertEquals(1, reply);
                        reply = client.hset(key, field, "KO");
//                        assertEquals(0, reply);
                        String cached = (String) client.hget(key, field);
//                        assertEquals("KO", cached);
                        reply = client.hset(key, field, 1.01);
//                        assertEquals(0, reply);
                        double cachedDouble = client.hgetDouble(key, field);
//                        assertEquals(1.01, cachedDouble);

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

}
