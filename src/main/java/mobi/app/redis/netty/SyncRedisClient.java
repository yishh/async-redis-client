package mobi.app.redis.netty;

import mobi.app.redis.RedisClient;
import mobi.app.redis.ZEntity;
import mobi.app.redis.ZSetAggregate;
import mobi.app.redis.netty.reply.Reply;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * User: thor
 * Date: 12-12-28
 * Time: 下午3:52
 */
public class SyncRedisClient implements RedisClient {
    final NettyRedisClient asyncClient;
    final int timeout;
    final TimeUnit timeUnit;

    public SyncRedisClient(String address, int db, String password, int timeout, TimeUnit timeUnit) {
        asyncClient = new NettyRedisClient(address, db, password);
        this.timeout = timeout;
        this.timeUnit = timeUnit;
    }

    @Override
    public void close() {
        try {
            asyncClient.close();
        } catch (InterruptedException e) {
            throw new RedisException(e);
        }
    }

    public static interface AsyncClientRunnable<T> {
        T call() throws InterruptedException, ExecutionException, TimeoutException;
    }

    private <T> T callAsyncClient(AsyncClientRunnable<T> runnable) {
        try {
            return runnable.call();
        } catch (InterruptedException e) {
            throw new RedisException(e);
        } catch (ExecutionException e) {
            throw new RedisException(e);
        } catch (TimeoutException e) {
            throw new RedisException(e);
        }
    }

    @Override
    public String auth(final String password) {
        return callAsyncClient(new AsyncClientRunnable<String>() {
            @Override
            public String call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.auth(password).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public String echo(final String message) {
        return callAsyncClient(new AsyncClientRunnable<String>() {
            @Override
            public String call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.echo(message).get(timeout, timeUnit);
            }
        });

    }

    @Override
    public String ping() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String select(int db) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String quit() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long delete(String key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long delete(String... key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public byte[] dump(String key) {
        return new byte[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long exists(String key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long expire(String key, int seconds) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long expireAt(String key, int timestamp) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long ttl(String key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<String> keys(String pattern) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String migrate(String host, int port, String key, int db, int timeOut) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long move(String key, int db) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long presist(String key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long pexpire(String key, long milliseconds) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long pexpireAt(String key, long timestamp) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long pttl(String key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String randomKey() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String rename(String key, String newKey) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long renameNx(String key, String newKey) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String restore(String key, int expire, byte[] v) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String type(String key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String set(String key, int o) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String set(String key, long o) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String set(String key, double o) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String set(String key, Object o) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long setNx(String key, int o) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long setNx(String key, long o) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long setNx(String key, double o) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long setNx(String key, Object o) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long msetIntNx(Map<String, Integer> value) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long msetLongNx(Map<String, Long> value) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long msetDoubleNx(Map<String, Double> value) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long msetObjectNx(Map<String, ?> value) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String setEx(String key, int o, int seconds) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String setEx(String key, long o, int seconds) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String setEx(String key, double o, int seconds) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String setEx(String key, Object o, int seconds) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String msetInt(Map<String, Integer> value) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String msetLong(Map<String, Long> value) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String msetDouble(Map<String, Double> value) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String msetObject(Map<String, ?> value) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object get(String key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Integer getInt(String key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long getLong(String key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Double getDouble(String key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<?> mget(String[] key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Integer> mgetInt(String[] key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Long> mgetLong(String[] key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Double> mgetDouble(String[] key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long decr(String key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long decrBy(String key, int decrement) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long incr(String key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long incrBy(String key, int decrement) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Integer getAndSet(String key, int v) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long getAndSet(String key, long v) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Double getAndSet(String key, double v) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object getAndSet(String key, Object v) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long hdel(String key, String... fields) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long hexists(String key, String field) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long hset(String key, String field, int o) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long hset(String key, String field, long o) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long hset(String key, String field, double o) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long hset(String key, String field, Object o) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long hsetNx(String key, String field, int o) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long hsetNx(String key, String field, long o) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long hsetNx(String key, String field, double o) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long hsetNx(String key, String field, Object o) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object hget(String key, String field) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Integer hgetInt(String key, String field) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long hgetLong(String key, String field) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Double hgetDouble(String key, String field) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Map<String, Integer> hgetAllInt(String key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Map<String, Long> hgetAllLong(String key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Map<String, Double> hgetAllDouble(String key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Map<String, ?> hgetAll(String key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long hincrBy(String key, String field, int decrement) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<String> hkeys(String key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long hlen(String key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<?> hmget(String key, String[] fields) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Integer> hmgetInt(String key, String[] fields) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Long> hmgetLong(String key, String[] fields) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Double> hmgetDouble(String key, String[] fields) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String hmsetInt(String key, Map<String, Integer> value) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String hmsetLong(String key, Map<String, Long> value) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String hmsetDouble(String key, Map<String, Double> value) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String hmset(String key, Map<String, ?> value) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Integer> hvalsInt(String key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Long> hvalsLong(String key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Double> hvalsDouble(String key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<?> hvals(String key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object lindex(String key, int index) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long linsertBefore(String key, Object before, Object value) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long linsertAfter(String key, Object after, Object value) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long llen(String key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object lpop(String key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long lpush(String key, Object... values) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long lpushx(String key, Object value) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<?> lrange(String key, int start, int stop) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long lrem(String key, int count, Object value) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String lset(String key, int index, Object value) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String ltrim(String key, int start, int stop) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object rpop(String key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long rpush(String key, Object... values) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long rpushx(String key, Object value) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object rpoplpush(String source, String destination) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long sadd(String key, Object... values) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long scard(String key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<?> sdiff(String key, String... diffs) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long sdiffStore(String destination, String key, String... diffs) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<?> sinter(String key, String... keys) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long sinterStore(String destination, String key, String... keys) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long sisMember(String key, Object member) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<?> smembers(String key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long smove(String source, String destination, Object member) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object spop(String key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object srandomMember(String key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<?> srandomMember(String key, int count) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long srem(String key, Object... members) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<?> sunion(String key, String... keys) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long sunionStore(String destination, String key, String... keys) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long zadd(String key, double score, Object member, ZEntity... others) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long zcard(String key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long zcount(String key, String min, String max) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Double zincrBy(String key, double increment, Object member) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long zinterStore(String destination, String[] keys, int[] weights, ZSetAggregate aggregate) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long zunionStore(String destination, String[] keys, int[] weights, ZSetAggregate aggregate) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<?> zrange(String key, int start, int stop) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<ZEntity<?>> zrangeWithScores(String key, int start, int stop) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<?> zrangeByScore(String key, String min, String max) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<?> zrangeByScore(String key, String min, String max, int offset, int count) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<ZEntity<?>> zrangeByScoreWithScores(String key, String min, String max) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<ZEntity<?>> zrangeByScoreWithScores(String key, String min, String max, int offset, int count) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long zrank(String key, Object member) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long zrem(String key, Object... members) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long zremRangeByRank(String key, int start, int stop) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long zremRangeByScore(String key, String min, String max) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<?> zrevRange(String key, int start, int stop) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<ZEntity<?>> zrevRangeWithScores(String key, int start, int stop) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<?> zrevRangeByScore(String key, String max, String min) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<?> zrevRangeByScore(String key, String max, String min, int offset, int count) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<ZEntity<?>> zrevRangeByScoreWithScores(String key, String max, String min) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<ZEntity<?>> zrevRangeByScoreWithScores(String key, String max, String min, int offset, int count) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long zrevRank(String key, Object member) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Double zscore(String key, Object member) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Reply eval(String script, String[] keys, byte[]... args) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Reply evalSha(String sha1, String[] keys, byte[]... args) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String scriptLoad(String script) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long scriptExists(String script) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Integer> scriptExists(String[] scripts) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String scriptFlush() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String scriptKill() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String bgRewriteAOF() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String bgSave() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String clientKill(String ip, int port) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String clientList() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String configGet(String parameter) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String configResetStat() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String configSet(String config, String parameter) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long dbSize() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String flushAll() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String flushDB() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String info() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Long lastSave() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String save() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String shutdown(boolean save) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String slaveOf(String host, int port) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
