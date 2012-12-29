package mobi.app.test;

import junit.framework.TestCase;
import mobi.app.redis.Sha1;
import mobi.app.redis.ZEntity;
import mobi.app.redis.ZNumbers;
import mobi.app.redis.netty.NettyRedisClient;
import mobi.app.redis.netty.reply.MultiBulkReply;
import mobi.app.redis.netty.reply.Reply;
import mobi.app.redis.netty.reply.SingleReply;
import mobi.app.redis.transcoders.Transcoder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * User: thor
 * Date: 12-12-20
 * Time: 上午11:36
 */
public class TestNettyRedisClient extends TestCase {

    NettyRedisClient client = new NettyRedisClient("172.16.3.213:6379", 1, null);


    public void testEcho() throws ExecutionException, InterruptedException {
        String result = client.echo("hello").get();
        assertEquals("hello", result);
    }

    public void testPing() throws ExecutionException, InterruptedException {
        String result = client.ping().get();
        assertEquals("PONG", result);
    }


    public void testSet() throws ExecutionException, InterruptedException {
        String result = client.set("TEST_KEY2", "CACHED").get();
        assertEquals("OK", result);
        String strCached = (String) client.get("TEST_KEY2").get();
        assertEquals("CACHED", strCached);
    }

    public void testSetInt() throws ExecutionException, InterruptedException {
        String result = client.set("TEST_KEY1", 1).get();
        assertEquals("OK", result);
        int cached = client.getInt("TEST_KEY1").get();
        assertEquals(1, cached);


    }

    public void testSetLongAndDouble() throws ExecutionException, InterruptedException {
        String result = client.set("TEST_KEY_LONG", 1l).get();
        assertEquals("OK", result);
        long cached = client.getLong("TEST_KEY_LONG").get();
        assertEquals(1l, cached);

        result = client.set("TEST_KEY_DOUBLE", 1.013).get();
        assertEquals("OK", result);
        double cachedDouble = client.getDouble("TEST_KEY_DOUBLE").get();
        assertEquals(1.013, cachedDouble);
    }

    public void testConcurrencyOpera() throws InterruptedException {
        int concurrencyCount = 20;
        ExecutorService executorService = Executors.newFixedThreadPool(concurrencyCount);
        final CountDownLatch latch = new CountDownLatch(concurrencyCount);
        final AtomicInteger successCount = new AtomicInteger(0);
        for (int i = 0; i < concurrencyCount; i++) {
            final int id = i;
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    String key = String.format("TESTKEY-%s", id);
                    String value = String.format("TESTVALUE-%s", id);
                    String result;
                    try {
                        result = client.set(key, value).get(1, TimeUnit.SECONDS);
                        assertEquals("OK", result);
                        String strCached = (String) client.get(key).get(1, TimeUnit.SECONDS);
//                        assert value.equals(strCached);
//                        assertEquals(value, strCached);
                        if (value.equals(strCached))
                            successCount.incrementAndGet();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                    }
                    latch.countDown();

                }
            });
        }
        latch.await();
        assertEquals(concurrencyCount, successCount.get());
        executorService.shutdownNow();
    }

    public void testExistsAndDel() throws ExecutionException, InterruptedException {
        String key1 = "DEL_KEY1";
        String key2 = "DEL_KEY2";
        client.delete(key1, key2);
        client.set(key1, 1);
        client.set(key2, 2);
        long reply = client.exists(key1).get();
        assertEquals(1, reply);
        reply = client.exists(key2).get();
        assertEquals(1, reply);
        long number = client.delete(key1, key2).get();
        assertEquals(2, number);
        reply = client.exists(key1).get();
        assertEquals(0, reply);
        reply = client.exists(key2).get();
        assertEquals(0, reply);
    }

    public void testSetNx() throws ExecutionException, InterruptedException {
        String key1 = "NX_KEY1";
        client.delete(key1);
        long reply = client.setNx(key1, "sss").get();
        assertEquals(1, reply);

        reply = client.setNx(key1, 2).get();
        assertEquals(0, reply);

        String cached = (String) client.get(key1).get();
        assertEquals("sss", cached);

    }

    public void testMset() throws ExecutionException, InterruptedException {
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("MAP_KEY1", 1);
        map.put("MAP_KEY2", 2);
        String result = client.msetInt(map).get();
        assertEquals("OK", result);
        int cached = client.getInt("MAP_KEY1").get();
        assertEquals(1, cached);
    }

    public void testMsetnx() throws ExecutionException, InterruptedException {
        Map<String, String> map = new HashMap<String, String>();
        map.put("MAPNX_KEY1", "TES");
        map.put("MAPNX_KEY2", "EST");

        client.delete("MAPNX_KEY1", "MAPNX_KEY2");
        long reply = client.msetObjectNx(map).get();
        assertEquals(1, reply);

        client.delete("MAPNX_KEY1");

        reply = client.msetObjectNx(map).get();
        assertEquals(0, reply);
    }

    public void testSetex() throws ExecutionException, InterruptedException {
        String result = client.setEx("TEST_SETEX", "TES", 10).get();
        assertEquals("OK", result);
        result = client.setEx("TEST_SETEX", 2l, 10).get();
        assertEquals("OK", result);

    }

    public void testMget() throws ExecutionException, InterruptedException {
        Map<String, String> map = new HashMap<String, String>();
        map.put("MAPMX_KEY1", "TES");
        map.put("MAPMX_KEY2", "EST");
        client.msetObjectNx(map).get();
        List<String> result = (List<String>) client.mget(new String[]{"MAPMX_KEY1", "MAPMX_KEY2", "NON_EXIST"}).get();
        assertEquals("TES", result.get(0));
        assertEquals("EST", result.get(1));
        assertEquals(null, result.get(2));
        Map<String, Integer> map2 = new HashMap<String, Integer>();
        map2.put("MAP_KEY1", 1);
        map2.put("MAP_KEY2", 2);
        String resultStr = client.msetInt(map2).get();
        assertEquals("OK", resultStr);
        List<Integer> cached = client.mgetInt(new String[]{"MAP_KEY1", "MAP_KEY2"}).get();
        assertEquals(2, cached.size());
        assertEquals(1, cached.get(0).intValue());
        assertEquals(2, cached.get(1).intValue());
    }

    public void testIncrAndDecr() throws ExecutionException, InterruptedException {
        String key = "NUMBER_KEY";
        client.delete(key).get();
        long reply = client.incr(key).get();
        assertEquals(1, reply);
        reply = client.incr(key).get();
        assertEquals(2, reply);
        reply = client.incrBy(key, 3).get();
        assertEquals(5, reply);
        reply = client.decrBy(key, 3).get();
        assertEquals(2, reply);
        reply = client.decrBy(key, 1).get();
        assertEquals(1, reply);
    }

    public void testGetAndSet() throws ExecutionException, InterruptedException {
        String key = "GETANDSET_KEY";
        client.delete(key).get();
        long reply = client.incr(key).get();
        assertEquals(1, reply);
        reply = client.getAndSet(key, 0).get();
        assertEquals(1, reply);
        reply = client.getInt(key).get();
        assertEquals(0, reply);
        client.set(key, "HEL");
        String replyStr = (String) client.getAndSet(key, "LLO").get();
        assertEquals("HEL", replyStr);
        replyStr = (String) client.get(key).get();
        assertEquals("LLO", replyStr);
    }

    public void testReconnect() throws ExecutionException, InterruptedException, TimeoutException {
        String key = "GET_KEY";
        client.set(key, "OK").get();
        client.closeForTest();
        try {
            client.get(key).get(1, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            assertTrue(true);
        }
        Thread.sleep(3000);
        String reply = (String) client.get(key).get();
        assertEquals("OK", reply);

    }

    public void testDumpAndRestore() throws ExecutionException, InterruptedException, TimeoutException {
        String key = "DUMP_KEY";
        client.delete(key).get();
        client.set(key, "OK").get();
        byte[] dump = client.dump(key).get(1, TimeUnit.SECONDS);

        client.delete(key).get();
        String reply = client.restore(key, 0, dump).get();
        assertEquals("OK", reply);
        reply = (String) client.get(key).get();
        assertEquals("OK", reply);

    }

    public void testExpire() throws ExecutionException, InterruptedException {
        String key = "EXPIRE_KEY";
        client.delete(key).get();
        client.set(key, "OK").get();
        long reply = client.expire(key, 10).get();
        assertEquals(1, reply);
        reply = client.ttl(key).get();
        assertEquals(10, reply);
        int timestamp = (int) (System.currentTimeMillis() / 1000 + 10);
        reply = client.expireAt(key, timestamp).get();
        assertEquals(1, reply);
        reply = client.ttl(key).get();
        assertTrue(reply <= 10 && reply >= 9);
    }

    public void testPexpire() throws ExecutionException, InterruptedException {
        String key = "PEXPIRE_KEY";
        client.delete(key).get();
        client.set(key, "OK").get();
        long reply = client.pexpire(key, 10000).get();
        assertEquals(1, reply);
        reply = client.pttl(key).get();

        assertTrue(reply <= 10000 && reply >= 9000);

        long timestamp = (System.currentTimeMillis() + 10000);
        reply = client.pexpireAt(key, timestamp).get();
        assertEquals(1, reply);
        reply = client.pttl(key).get();
        assertTrue(reply <= 10000 && reply >= 9000);
    }

    public void testKeys() throws ExecutionException, InterruptedException {
        Map<String, String> map = new HashMap<String, String>();
        map.put("ONE_KEY1", "TES");
        map.put("ONE_KEY2", "EST");
        client.msetObject(map).get();
        List<String> keys = client.keys("ONE_*").get();
        assertEquals("ONE_KEY1", keys.get(0));
        assertEquals(2, keys.size());
    }

    public void testMigrate() throws ExecutionException, InterruptedException {
        NettyRedisClient client2 = new NettyRedisClient("172.16.3.213:6378", 0, null);
        String key = "MIGRATE_KEY";
        client.delete(key).get();
        client2.delete(key).get();
        client.set(key, "OK").get();
        String reply = client.migrate("172.16.3.213", 6378, key, 0, 1000).get();
        assertEquals("OK", reply);
        long existed = client.exists(key).get();
        assertEquals(0, existed);

        reply = (String) client2.get(key).get();
        assertEquals("OK", reply);
    }

    public void testMove() throws ExecutionException, InterruptedException {
        NettyRedisClient client2 = new NettyRedisClient("172.16.3.213:6379", 0, null);
        String key = "MOVE_KEY";
        client2.delete(key).get();
        client.delete(key).get();
        client.set(key, "OK").get();
        long reply = client.move(key, 0).get();
        assertEquals(1, reply);
        long existed = client.exists(key).get();
        assertEquals(0, existed);

        String cached = (String) client2.get(key).get();
        assertEquals("OK", cached);
    }

    public void testPresist() throws ExecutionException, InterruptedException {
        client.setEx("PRESIST_KEY", "OK", 10);
        long reply = client.ttl("PRESIST_KEY").get();
        assertEquals(10, reply);
        reply = client.presist("PRESIST_KEY").get();
        assertEquals(1, reply);
        reply = client.ttl("PRESIST_KEY").get();
        assertEquals(-1, reply);

    }

    public void testRandomKey() throws ExecutionException, InterruptedException {
        String result = client.randomKey().get();
        System.out.println(result);
        assertNotNull(result);
    }

    public void testRename() throws ExecutionException, InterruptedException {
        String key1 = "SOURCE_KEY";
        String key2 = "DEST_KEY";
        client.delete(key1, key2).get();
        client.set(key1, "OK").get();
        String reply = client.rename(key1, key2).get();
        assertEquals("OK", reply);
        long existed = client.exists(key1).get();
        assertEquals(0, existed);
        String cached = (String) client.get(key2).get();
        assertEquals("OK", cached);
        client.set(key1, "OK").get();
        long result = client.renameNx(key1, key2).get();
        assertEquals(0, result);
        client.delete(key2).get();
        result = client.renameNx(key1, key2).get();
        assertEquals(1, result);
        existed = client.exists(key1).get();
        assertEquals(0, existed);
        cached = (String) client.get(key2).get();
        assertEquals("OK", cached);


    }

    public void testType() throws ExecutionException, InterruptedException {
        String key = "TYPE_KEY";
        client.set(key, "OK").get();
        String type = client.type(key).get();
        assertEquals("string", type);
    }

    public void testHgetAndHset() throws ExecutionException, InterruptedException {
        String key = "HASH_KEY";
        client.delete(key);
        String field = "TEST_FIELD";
        long reply = client.hset(key, field, "OK").get();
        assertEquals(1, reply);
        reply = client.hset(key, field, "KO").get();
        assertEquals(0, reply);
        String cached = (String) client.hget(key, field).get();
        assertEquals("KO", cached);
        reply = client.hset(key, field, 1.01).get();
        assertEquals(0, reply);
        double cachedDouble = client.hgetDouble(key, field).get();
        assertEquals(1.01, cachedDouble);
    }

    public void testHdelAndExists() throws ExecutionException, InterruptedException {
        String key = "HASH_KEY1";
        client.delete(key);
        String field1 = "TEST_FIELD1";
        String field2 = "TEST_FIELD2";
        long reply = client.hset(key, field1, "OK").get();
        assertEquals(1, reply);
        reply = client.hset(key, field2, "KO").get();
        assertEquals(1, reply);
        reply = client.hexists(key, field1).get();
        assertEquals(1, reply);
        reply = client.hexists(key, field2).get();
        assertEquals(1, reply);
        reply = client.hdel(key, field1, field2).get();
        assertEquals(2, reply);
        reply = client.hexists(key, field1).get();
        assertEquals(0, reply);
        reply = client.hexists(key, field2).get();
        assertEquals(0, reply);
    }

    public void testHgetAll() throws ExecutionException, InterruptedException {
        String key = "HASH_KEY2";
        client.delete(key);
        String field1 = "TEST_FIELD1";
        String field2 = "TEST_FIELD2";
        long reply = client.hset(key, field1, "OK").get();
        assertEquals(1, reply);
        reply = client.hset(key, field2, "KO").get();
        assertEquals(1, reply);
        Map<String, Object> cached = (Map<String, Object>) client.hgetAll(key).get();
        assertEquals(2, cached.size());
        assertEquals("OK", cached.get(field1));
        assertEquals("KO", cached.get(field2));

        reply = client.hset(key, field1, 1).get();
        assertEquals(0, reply);
        reply = client.hset(key, field2, 2).get();
        assertEquals(0, reply);
        Map<String, Integer> cachedInt = client.hgetAllInt(key).get();
        assertEquals(2, cachedInt.size());
        assertEquals(1, cachedInt.get(field1).intValue());
        assertEquals(2, cachedInt.get(field2).intValue());
    }

    public void testHincrBy() throws ExecutionException, InterruptedException {
        String key = "HASH_INCR_KEY2";
        client.delete(key);
        String field1 = "TEST_FIELD1";
        String field2 = "TEST_FIELD2";
        long reply = client.hset(key, field1, 1).get();
        assertEquals(1, reply);
        reply = client.hincrBy(key, field2, 1).get();
        assertEquals(1, reply);
        reply = client.hincrBy(key, field1, 1).get();
        assertEquals(2, reply);
        reply = client.hincrBy(key, field1, -3).get();
        assertEquals(-1, reply);
    }

    public void testHkeys() throws ExecutionException, InterruptedException {
        String key = "HASH_KEYS_KEY2";
        client.delete(key);
        String field1 = "TEST_FIELD1";
        String field2 = "TEST_FIELD2";
        long reply = client.hset(key, field1, 1).get();
        assertEquals(1, reply);
        reply = client.hset(key, field2, 2).get();
        assertEquals(1, reply);
        List<String> keys = client.hkeys(key).get();
        assertEquals(2, keys.size());
        assertEquals(field1, keys.get(0));
        assertEquals(field2, keys.get(1));

        keys = client.hkeys("NONNsd2").get();
        assertEquals(0, keys.size());

    }

    public void testHlen() throws ExecutionException, InterruptedException {
        String key = "HASH_KEYS_KEY2";
        client.delete(key);
        String field1 = "TEST_FIELD1";
        String field2 = "TEST_FIELD2";
        long reply = client.hset(key, field1, 1).get();
        assertEquals(1, reply);
        reply = client.hset(key, field2, 2).get();
        assertEquals(1, reply);
        long size = client.hlen(key).get();
        assertEquals(2, size);
        size = client.hlen("NONNsd2").get();
        assertEquals(0, size);
    }

    public void testHmget() throws ExecutionException, InterruptedException {
        String key = "HASH_HMGET_KEY2";
        client.delete(key);
        String field1 = "TEST_FIELD1";
        String field2 = "TEST_FIELD2";
        long reply = client.hset(key, field1, 1).get();
        assertEquals(1, reply);
        reply = client.hset(key, field2, 2).get();
        assertEquals(1, reply);
        List<Integer> cached = client.hmgetInt(key, new String[]{field1, field2, "dfd"}).get();
        assertEquals(3, cached.size());
        assertEquals(1, cached.get(0).intValue());
        assertEquals(2, cached.get(1).intValue());
        assertNull(cached.get(2));
    }

    public void testHmset() throws ExecutionException, InterruptedException {
        String key = "HASH_HMSET_KEY2";
        client.delete(key);
        String field1 = "TEST_FIELD1";
        String field2 = "TEST_FIELD2";
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put(field1, 1);
        map.put(field2, 2);
        String reply = client.hmsetInt(key, map).get();
        assertEquals("OK", reply);
        List<Integer> cached = client.hmgetInt(key, new String[]{field1, field2, "dfd"}).get();
        assertEquals(3, cached.size());
        assertEquals(1, cached.get(0).intValue());
        assertEquals(2, cached.get(1).intValue());
        assertNull(cached.get(2));

        Map<String, String> map2 = new HashMap<String, String>();
        map2.put(field1, "OK");
        map2.put(field2, "KO");
        reply = client.hmset(key, map2).get();
        assertEquals("OK", reply);
        List<String> cachedStr = (List<String>) client.hmget(key, new String[]{field1, field2, "dfd"}).get();
        assertEquals(3, cached.size());
        assertEquals("OK", cachedStr.get(0));
        assertEquals("KO", cachedStr.get(1));
        assertNull(cached.get(2));
    }

    public void testHvals() throws ExecutionException, InterruptedException {
        String key = "HASH_HVALS_KEY2";
        client.delete(key);
        String field1 = "TEST_FIELD1";
        String field2 = "TEST_FIELD2";
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put(field1, 1);
        map.put(field2, 2);
        String reply = client.hmsetInt(key, map).get();
        assertEquals("OK", reply);
        List<Integer> cached = client.hvalsInt(key).get();
        assertEquals(2, cached.size());
        assertEquals(1, cached.get(0).intValue());
        assertEquals(2, cached.get(1).intValue());


        Map<String, String> map2 = new HashMap<String, String>();
        map2.put(field1, "OK");
        map2.put(field2, "KO");
        reply = client.hmset(key, map2).get();
        assertEquals("OK", reply);
        List<String> cachedStr = (List<String>) client.hvals(key).get();
        assertEquals(2, cached.size());
        assertEquals("OK", cachedStr.get(0));
        assertEquals("KO", cachedStr.get(1));

    }

    public void testLpushAndIndex() throws ExecutionException, InterruptedException {
        String key = "LIST_PUSH_KEY2";
        client.delete(key);
        long reply = client.lpushx(key, "OK").get();
        assertEquals(0, reply);
        reply = client.lpush(key, "OK").get();
        assertEquals(1, reply);
        reply = client.lpush(key, "KO").get();
        assertEquals(2, reply);
        String cached = (String) client.lindex(key, 1).get();
        assertEquals("OK", cached);
        cached = (String) client.lindex(key, 0).get();
        assertEquals("KO", cached);

    }

    public void testLpop() throws ExecutionException, InterruptedException {
        String key = "LIST_POP_KEY2";
        client.delete(key);
        long reply = client.lpush(key, "OK").get();
        assertEquals(1, reply);
        reply = client.lpush(key, "KO").get();
        assertEquals(2, reply);
        String cached = (String) client.lpop(key).get();
        assertEquals("KO", cached);
        cached = (String) client.lpop(key).get();
        assertEquals("OK", cached);
        cached = (String) client.lpop(key).get();
        assertEquals(null, cached);
        cached = (String) client.lpop("gfgr4").get();
        assertEquals(null, cached);
    }


    public void testLinsert() throws ExecutionException, InterruptedException {
        String key = "LIST_INSERT_KEY2";
        client.delete(key);
        long reply = client.lpush(key, "KO", "OK").get();
        assertEquals(2, reply);
        reply = client.linsertAfter(key, "OKO", "KOK").get();
        assertEquals(-1, reply);
        reply = client.linsertAfter(key, "OK", "KK").get();
        assertEquals(3, reply);
        String cached = (String) client.lindex(key, 1).get();
        assertEquals("KK", cached);
        reply = client.linsertBefore(key, "KK", "OO").get();
        assertEquals(4, reply);
        cached = (String) client.lindex(key, 1).get();
        assertEquals("OO", cached);
        cached = (String) client.lindex(key, 3).get();
        assertEquals("KO", cached);
    }

    public void testLlen() throws ExecutionException, InterruptedException {
        String key = "LIST_LEN_KEY2";
        client.delete(key);
        long reply = client.lpush(key, "KO", "OK").get();
        assertEquals(2, reply);
        reply = client.llen(key).get();
        assertEquals(2, reply);
    }

    public void testLrange() throws ExecutionException, InterruptedException {
        String key = "LIST_RANGE_KEY2";
        client.delete(key);
        long reply = client.lpush(key, "KO", "OK", "1", "2", "3").get();
        assertEquals(5, reply);
        List<String> range = (List<String>) client.lrange(key, 1, 3).get();
        assertEquals(3, range.size());
        assertEquals("2", range.get(0));
        assertEquals("1", range.get(1));
        assertEquals("OK", range.get(2));

        range = (List<String>) client.lrange(key, 2, -1).get();
        assertEquals(3, range.size());
        assertEquals("1", range.get(0));
        assertEquals("OK", range.get(1));
        assertEquals("KO", range.get(2));
    }

    public void testLrem() throws ExecutionException, InterruptedException {
        String key = "LIST_REM_KEY2";
        client.delete(key);
        long reply = client.lpush(key, "1", "1", "OK", "2", "2").get();
        assertEquals(5, reply);
        reply = client.lrem(key, 1, "2").get();
        assertEquals(1, reply);
        String cached = (String) client.lindex(key, 0).get();
        assertEquals("2", cached);

        reply = client.lrem(key, -2, "1").get();
        assertEquals(2, reply);
        cached = (String) client.lindex(key, 1).get();
        assertEquals("OK", cached);
    }

    public void testLset() throws ExecutionException, InterruptedException {
        String key = "LIST_SET_KEY2";
        client.delete(key);
        long reply = client.lpush(key, "1", "1", "OK", "2", "2").get();
        assertEquals(5, reply);
        String result = client.lset(key, 1, "3").get();

        assertEquals("OK", result);
        String cached = (String) client.lindex(key, 1).get();
        assertEquals("3", cached);

        result = client.lset(key, 6, "3").get();

        assertEquals("ERR index out of range", result);

    }

    public void testLtrim() throws ExecutionException, InterruptedException {
        String key = "LIST_TRIM_KEY2";
        client.delete(key);
        long reply = client.lpush(key, "1", "1", "OK", "2", "2").get();
        assertEquals(5, reply);
        String result = client.ltrim(key, 1, 3).get();

        assertEquals("OK", result);
        List<String> range = (List<String>) client.lrange(key, 0, -1).get();
        assertEquals(3, range.size());
        assertEquals("2", range.get(0));
        assertEquals("OK", range.get(1));
        assertEquals("1", range.get(2));

    }

    public void testRpopAndPush() throws ExecutionException, InterruptedException {
        String key = "LIST_RPUSH_KEY2";
        client.delete(key);
        long reply = client.rpushx(key, "OK").get();
        assertEquals(0, reply);
        reply = client.rpush(key, "OK", "KO").get();
        assertEquals(2, reply);
        String cached = (String) client.rpop(key).get();
        assertEquals("KO", cached);
        cached = (String) client.rpop(key).get();
        assertEquals("OK", cached);
        cached = (String) client.rpop(key).get();
        assertEquals(null, cached);
        cached = (String) client.rpop("gfgr4").get();
        assertEquals(null, cached);
    }

    public void testRpoplpush() throws ExecutionException, InterruptedException {
        String key1 = "LIST_RPUSH_KEY1";
        String key2 = "LIST_RPUSH_KEY2";
        client.delete(key1, key2);

        long reply = client.rpush(key1, "OK", "KO").get();
        assertEquals(2, reply);
        reply = client.rpush(key2, "1", "2").get();
        assertEquals(2, reply);
        String poped = (String) client.rpoplpush(key1, key2).get();
        assertEquals("KO", poped);
        reply = client.llen(key1).get();
        assertEquals(1, reply);
        reply = client.llen(key2).get();
        assertEquals(3, reply);
        poped = (String) client.lindex(key1, 0).get();
        assertEquals("OK", poped);
        poped = (String) client.lindex(key2, 0).get();
        assertEquals("KO", poped);
        poped = (String) client.rpoplpush("ssss", key2).get();
        assertEquals(null, poped);

    }

    public void testSaddAndCardAndMembers() throws ExecutionException, InterruptedException {
        String key = "SET_ADD_KEY";
        client.delete(key).get();
        long reply = client.sadd(key, "OK", "KO").get();
        assertEquals(2, reply);
        reply = client.scard(key).get();
        assertEquals(2, reply);
        List<String> cached = (List<String>) client.smembers(key).get();
        assertEquals(2, cached.size());
        assertEquals("OK", cached.get(0));
        assertEquals("KO", cached.get(1));
    }

    public void testSdiffAndInterAndUnion() throws ExecutionException, InterruptedException {
        String key1 = "SET_DIFF_KEY1";
        String key2 = "SET_DIFF_KEY2";
        String key3 = "SET_DIFF_KEY3";
        client.delete(key1, key2, key3).get();
        long reply = client.sadd(key1, "a", "b", "c", "d").get();
        assertEquals(4, reply);
        reply = client.sadd(key2, "c").get();
        assertEquals(1, reply);
        reply = client.sadd(key3, "a", "c", "e").get();
        assertEquals(3, reply);
        List<String> diff = (List<String>) client.sdiff(key1, key2, key3).get();
        assertEquals(2, diff.size());
        assertEquals("b", diff.get(0));
        assertEquals("d", diff.get(1));
        reply = client.sdiffStore("SET_DIFF_RESULT", key1, key2, key3).get();
        assertEquals(2, reply);
        List<String> cached = (List<String>) client.smembers("SET_DIFF_RESULT").get();
        assertEquals(2, cached.size());
        assertEquals("b", cached.get(0));
        assertEquals("d", cached.get(1));
        diff = (List<String>) client.sinter(key1, key2, key3).get();
        assertEquals(1, diff.size());
        assertEquals("c", diff.get(0));
        reply = client.sinterStore("SET_DIFF_RESULT", key1, key2, key3).get();
        assertEquals(1, reply);
        cached = (List<String>) client.smembers("SET_DIFF_RESULT").get();
        assertEquals(1, cached.size());
        assertEquals("c", cached.get(0));
        diff = (List<String>) client.sunion(key1, key2, key3).get();
        assertEquals(5, diff.size());
        assertTrue(diff.contains("a"));
        assertTrue(diff.contains("b"));
        assertTrue(diff.contains("c"));
        assertTrue(diff.contains("d"));
        assertTrue(diff.contains("e"));

        reply = client.sunionStore("SET_DIFF_RESULT", key1, key2, key3).get();
        assertEquals(5, reply);
        cached = (List<String>) client.smembers("SET_DIFF_RESULT").get();
        assertEquals(5, cached.size());
        assertTrue(cached.contains("a"));
        assertTrue(cached.contains("b"));
        assertTrue(cached.contains("c"));
        assertTrue(cached.contains("d"));
        assertTrue(cached.contains("e"));


    }

    public void testSisMember() throws ExecutionException, InterruptedException {
        String key = "SET_ADD_KEY2";
        client.delete(key).get();
        long reply = client.sadd(key, "OK", "KO").get();
        assertEquals(2, reply);
        reply = client.sisMember(key, "OK").get();
        assertEquals(1, reply);
        reply = client.sisMember(key, "OKO").get();
        assertEquals(0, reply);
    }

    public void testSmove() throws ExecutionException, InterruptedException {
        String key1 = "SET_MOVE_KEY1";
        String key2 = "SET_MOVE_KEY2";
        client.delete(key1, key2).get();
        long reply = client.sadd(key1, "a", "b", "c", "d").get();
        assertEquals(4, reply);
        reply = client.sadd(key2, "e").get();
        assertEquals(1, reply);
        reply = client.smove(key1, key2, "d").get();
        assertEquals(1, reply);
        reply = client.scard(key1).get();
        assertEquals(3, reply);
        reply = client.scard(key2).get();
        assertEquals(2, reply);
        reply = client.smove(key1, key2, "e").get();
        assertEquals(0, reply);
    }

    public void testSpopAndRandomAndRem() throws ExecutionException, InterruptedException {
        String key1 = "SET_POP_KEY1";
        client.delete(key1).get();
        long reply = client.sadd(key1, "a", "b", "c", "d").get();
        assertEquals(4, reply);
        String pop = (String) client.spop(key1).get();
        assertNotNull(pop);
        reply = client.scard(key1).get();
        assertEquals(3, reply);
        pop = (String) client.srandomMember(key1).get();
        assertNotNull(pop);
        reply = client.scard(key1).get();
        assertEquals(3, reply);
        List<String> randoms = (List<String>) client.srandomMember(key1, 2).get();
        assertEquals(2, randoms.size());
        reply = client.sadd(key1, "a", "b", "c", "d").get();
        assertEquals(1, reply);
        reply = client.srem(key1, "a", "b").get();
        assertEquals(2, reply);
        reply = client.sisMember(key1, "a").get();
        assertEquals(0, reply);
        reply = client.sisMember(key1, "c").get();
        assertEquals(1, reply);


    }

    public void testZadd() throws ExecutionException, InterruptedException {
        String key = "ZSET_ADD_KEY1";
        client.delete(key).get();
        long reply = client.zadd(key, 1, "a").get();
        assertEquals(1, reply);
        reply = client.zadd(key, 2, "b", new ZEntity<String>("c", 3)).get();
        assertEquals(2, reply);
        reply = client.zcard(key).get();
        assertEquals(3, reply);
        reply = client.zcount(key, ZNumbers.MINIMUM, ZNumbers.MAXIMUM).get();
        assertEquals(3, reply);
        reply = client.zcount(key, ZNumbers.includeNumber(1), ZNumbers.MAXIMUM).get();
        assertEquals(3, reply);
        reply = client.zcount(key, ZNumbers.excludeNumber(1), ZNumbers.MAXIMUM).get();
        assertEquals(2, reply);
        List<String> cached = (List<String>) client.zrange(key, 0, 1).get();
        assertEquals(2, cached.size());
        assertEquals("a", cached.get(0));
        assertEquals("b", cached.get(1));
        List<ZEntity<?>> entitys = client.zrangeWithScores(key, 0, 1).get();
        assertEquals(2, entitys.size());
        assertEquals("a", entitys.get(0).member);
        assertEquals("b", entitys.get(1).member);
        assertEquals(1.0, entitys.get(0).score);
        assertEquals(2.0, entitys.get(1).score);

    }

    public void testZinterAndUnion() throws ExecutionException, InterruptedException {
        String key1 = "ZSET1";
        String key2 = "ZSET2";
        client.delete(key1, key2).get();
        client.zadd(key1, 1, "one");
        client.zadd(key1, 2, "two");
        client.zadd(key2, 1, "one");
        client.zadd(key2, 2, "two");
        client.zadd(key2, 3, "three");
        long reply = client.zinterStore("out", new String[]{key1, key2}, new int[]{2, 3}, null).get();
        assertEquals(2, reply);
        List<ZEntity<?>> entitys = client.zrangeWithScores("out", 0, -1).get();
        assertEquals(2, entitys.size());
        assertEquals("one", entitys.get(0).member);
        assertEquals("two", entitys.get(1).member);
        assertEquals(5.0, entitys.get(0).score);
        assertEquals(10.0, entitys.get(1).score);
        reply = client.zunionStore("out", new String[]{key1, key2}, new int[]{2, 3}, null).get();
        assertEquals(3, reply);
        entitys = client.zrangeWithScores("out", 0, -1).get();
        assertEquals(3, entitys.size());
        assertEquals("one", entitys.get(0).member);
        assertEquals("three", entitys.get(1).member);
        assertEquals("two", entitys.get(2).member);
        assertEquals(5.0, entitys.get(0).score);
        assertEquals(9.0, entitys.get(1).score);
        assertEquals(10.0, entitys.get(2).score);
    }

    public void testZIncrBy() throws ExecutionException, InterruptedException {
        String key = "ZSET_ADD_KEY2";
        client.delete(key).get();
        long reply = client.zadd(key, 1, "a").get();
        assertEquals(1, reply);
        double score = client.zincrBy(key, 1, "a").get();
        assertEquals(2.0, score);
        score = client.zscore(key, "a").get();
        assertEquals(2.0, score);
    }

    public void testZrangeByScore() throws ExecutionException, InterruptedException {
        String key = "ZSET";
        client.delete(key).get();
        client.zadd(key, 1, "one").get();
        client.zadd(key, 2, "two").get();
        client.zadd(key, 3, "three").get();
        List<String> cached = (List<String>) client.zrangeByScore(key, ZNumbers.MINIMUM, ZNumbers.MAXIMUM).get();
        assertEquals(3, cached.size());
        assertEquals("one", cached.get(0));
        assertEquals("two", cached.get(1));
        assertEquals("three", cached.get(2));
        cached = (List<String>) client.zrangeByScore(key, ZNumbers.includeNumber(1), ZNumbers.includeNumber(2)).get();
        assertEquals(2, cached.size());
        assertEquals("one", cached.get(0));
        assertEquals("two", cached.get(1));

        cached = (List<String>) client.zrangeByScore(key, ZNumbers.includeNumber(1), ZNumbers.includeNumber(2), 1, 1).get();
        assertEquals(1, cached.size());
//        assertEquals("one", cached.get(0));
        assertEquals("two", cached.get(0));

        cached = (List<String>) client.zrangeByScore(key, ZNumbers.excludeNumber(1), ZNumbers.includeNumber(2)).get();
        assertEquals(1, cached.size());
        assertEquals("two", cached.get(0));
        cached = (List<String>) client.zrangeByScore(key, ZNumbers.excludeNumber(1), ZNumbers.excludeNumber(2)).get();
        assertEquals(0, cached.size());

        List<ZEntity<?>> entitys = client.zrangeByScoreWithScores(key, ZNumbers.includeNumber(1), ZNumbers.includeNumber(2)).get();
        assertEquals(2, entitys.size());

        assertEquals("one", entitys.get(0).member);
        assertEquals("two", entitys.get(1).member);
        assertEquals(1.0, entitys.get(0).score);
        assertEquals(2.0, entitys.get(1).score);

        entitys = client.zrangeByScoreWithScores(key, ZNumbers.includeNumber(1), ZNumbers.includeNumber(2), 1, 1).get();
        assertEquals(1, entitys.size());

//        assertEquals("one", entitys.get(0).member);
        assertEquals("two", entitys.get(0).member);
//        assertEquals(1.0, entitys.get(0).score);
        assertEquals(2.0, entitys.get(0).score);

    }


    public void testZrank() throws ExecutionException, InterruptedException {
        String key = "ZSET";
        client.delete(key).get();
        client.zadd(key, 1, "one").get();
        client.zadd(key, 2, "two").get();
        client.zadd(key, 3, "three").get();
        long rank = client.zrank(key, "three").get();
        assertEquals(2, rank);
        rank = client.zrevRank(key, "three").get();
        assertEquals(0, rank);
    }

    public void testZrevRangeByScore() throws ExecutionException, InterruptedException {
        String key = "ZSET";
        client.delete(key).get();
        client.zadd(key, 1, "one").get();
        client.zadd(key, 2, "two").get();
        client.zadd(key, 3, "three").get();
        List<String> cached = (List<String>) client.zrevRange(key, 0, -1).get();
        assertEquals(3, cached.size());
        assertEquals("one", cached.get(2));
        assertEquals("two", cached.get(1));
        assertEquals("three", cached.get(0));

        cached = (List<String>) client.zrevRangeByScore(key, ZNumbers.includeNumber(2), ZNumbers.includeNumber(1)).get();
        assertEquals(2, cached.size());
        assertEquals("two", cached.get(0));
        assertEquals("one", cached.get(1));

        cached = (List<String>) client.zrevRangeByScore(key, ZNumbers.includeNumber(2), ZNumbers.includeNumber(1), 1, 1).get();
        assertEquals(1, cached.size());
//        assertEquals("one", cached.get(0));
        assertEquals("one", cached.get(0));

        cached = (List<String>) client.zrevRangeByScore(key, ZNumbers.excludeNumber(2), ZNumbers.includeNumber(1)).get();
        assertEquals(1, cached.size());
        assertEquals("one", cached.get(0));

        cached = (List<String>) client.zrevRangeByScore(key, ZNumbers.excludeNumber(2), ZNumbers.excludeNumber(1)).get();
        assertEquals(0, cached.size());

        List<ZEntity<?>> entitys = client.zrevRangeByScoreWithScores(key, ZNumbers.includeNumber(2), ZNumbers.includeNumber(1)).get();
        assertEquals(2, entitys.size());

        assertEquals("one", entitys.get(1).member);
        assertEquals("two", entitys.get(0).member);
        assertEquals(1.0, entitys.get(1).score);
        assertEquals(2.0, entitys.get(0).score);

        entitys = client.zrevRangeByScoreWithScores(key, ZNumbers.includeNumber(2), ZNumbers.includeNumber(1), 1, 1).get();
        assertEquals(1, entitys.size());

//        assertEquals("one", entitys.get(0).member);
        assertEquals("one", entitys.get(0).member);
//        assertEquals(1.0, entitys.get(0).score);
        assertEquals(1.0, entitys.get(0).score);

    }

    public void testZrem() throws ExecutionException, InterruptedException {
        String key = "ZSET";
        client.delete(key).get();
        client.zadd(key, 1, "one").get();
        client.zadd(key, 2, "two").get();
        client.zadd(key, 3, "three").get();
        long reply = client.zrem(key, "two").get();
        assertEquals(1, reply);
        List<ZEntity<?>> entitys = client.zrangeByScoreWithScores(key, ZNumbers.MINIMUM, ZNumbers.MAXIMUM).get();
        assertEquals(2, entitys.size());
        assertEquals("one", entitys.get(0).member);
        assertEquals("three", entitys.get(1).member);
        assertEquals(1.0, entitys.get(0).score);
        assertEquals(3.0, entitys.get(1).score);
        client.delete(key).get();
        client.zadd(key, 1, "one").get();
        client.zadd(key, 2, "two").get();
        client.zadd(key, 3, "three").get();
        reply = client.zremRangeByRank(key, 0, 1).get();
        assertEquals(2, reply);
        entitys = client.zrangeByScoreWithScores(key, ZNumbers.MINIMUM, ZNumbers.MAXIMUM).get();
        assertEquals(1, entitys.size());
        assertEquals("three", entitys.get(0).member);
        assertEquals(3.0, entitys.get(0).score);
        client.delete(key).get();
        client.zadd(key, 1, "one").get();
        client.zadd(key, 2, "two").get();
        client.zadd(key, 3, "three").get();
        reply = client.zremRangeByScore(key, ZNumbers.MINIMUM, ZNumbers.excludeNumber(2)).get();
        assertEquals(1, reply);
        entitys = client.zrangeByScoreWithScores(key, ZNumbers.MINIMUM, ZNumbers.MAXIMUM).get();
        assertEquals(2, entitys.size());
        assertEquals("two", entitys.get(0).member);
        assertEquals("three", entitys.get(1).member);
        assertEquals(2.0, entitys.get(0).score);
        assertEquals(3.0, entitys.get(1).score);


    }

    public void testEval() throws ExecutionException, InterruptedException {
        String script = "return {KEYS[1],KEYS[2],ARGV[1],ARGV[2]}";
        byte[] arg1 = Transcoder.STRING_TRANSCODER.encode("first");
        byte[] arg2 = Transcoder.STRING_TRANSCODER.encode("second");
        Reply reply = client.eval(script, new String[]{"key1", "key2"}, arg1, arg2).get();
        assertEquals(MultiBulkReply.class, reply.getClass());
        MultiBulkReply multiBulkReply = (MultiBulkReply) reply;
        List<byte[]> replys = (List<byte[]>) multiBulkReply.get();
        assertEquals(4, replys.size());
        assertEquals("key1", Transcoder.STRING_TRANSCODER.decode(replys.get(0)));
        assertEquals("key2", Transcoder.STRING_TRANSCODER.decode(replys.get(1)));
        assertEquals("first", Transcoder.STRING_TRANSCODER.decode(replys.get(2)));
        assertEquals("second", Transcoder.STRING_TRANSCODER.decode(replys.get(3)));
        script = "return redis.call('set','foo','bar')";
        reply = client.eval(script, new String[]{}).get();
        assertEquals(SingleReply.class, reply.getClass());
        SingleReply singleReply = (SingleReply) reply;
        assertEquals("OK", singleReply.decode(null));
        String sha1 = client.scriptLoad(script).get();
        String sha = Sha1.sha1(script);
        assertEquals(sha, sha1);
        long exists = client.scriptExists(sha1).get();
        assertEquals(1, exists);

//        script = "return {'1','2',{'3','Hello World!'}}";
//        reply = client.eval(script, new String[]{}).get();
//        assertEquals(MultiBulkReply.class, reply.getClass());
//        multiBulkReply = (MultiBulkReply) reply;
//        replys = (List<byte[]>) multiBulkReply.get();
//        assertEquals(4, replys.size());
    }

    public void testServer() throws ExecutionException, InterruptedException {
       System.out.println( client.info().get());
        System.out.println( client.dbSize().get());
        System.out.println( client.clientList().get());
    }

//    public void testQuit() throws ExecutionException, InterruptedException {
//        String result = client.quit().get();
//        assertEquals("OK", result);
//    }


}
