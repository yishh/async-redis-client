package mobi.app.redis.ha;

import mobi.app.redis.AsyncRedisClient;
import mobi.app.redis.RedisClient;
import mobi.app.redis.ZEntity;
import mobi.app.redis.ZSetAggregate;
import mobi.app.redis.netty.reply.Reply;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * User: thor
 * Date: 13-1-7
 * Time: 上午11:33
 */
public class RoutedRedisClient implements RedisClient {
    final Router router;

    public RoutedRedisClient(Router router) {
        this.router = router;
    }

    @Override
    public void setClosedHandler(AsyncRedisClient.ClosedHandler handler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setConnectedHandler(AsyncRedisClient.ConnectedHandler handler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AsyncRedisClient.ClosedHandler getClosedHandler() {
        throw new UnsupportedOperationException();
    }

    @Override
    public AsyncRedisClient.ConnectedHandler getConnectedHandler() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String hashKey() {
        return null;
    }

    @Override
    public boolean isAvailable() {
        return false;
    }

    @Override
    public void close() {
        Collection<RedisClient> clients = router.getClients();
        for (RedisClient redisClient : clients) {
            redisClient.close();
        }
    }

    @Override
    public String auth(String password) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String echo(String message) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String ping() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String select(int db) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String quit() {
        Collection<RedisClient> clients = router.getClients();
        for (RedisClient redisClient : clients) {
            redisClient.quit();
        }
        return "OK";
    }

    @Override
    public Long delete(String key) {
        return router.route(key).delete(key);
    }

    @Override
    public Long delete(String... key) {
        long count = 0;
        for(String k : key){
            count += router.route(k).delete(k);
        }
        return count;
    }

    @Override
    public byte[] dump(String key) {
        return router.route(key).dump(key);
    }

    @Override
    public Long exists(String key) {
        return router.route(key).exists(key);
    }

    @Override
    public Long expire(String key, int seconds) {
        return router.route(key).expire(key, seconds);
    }

    @Override
    public Long expireAt(String key, int timestamp) {
        return router.route(key).expireAt(key, timestamp);
    }

    @Override
    public Long ttl(String key) {
        return router.route(key).ttl(key);
    }

    @Override
    public List<String> keys(String pattern) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String migrate(String host, int port, String key, int db, int timeOut) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Long move(String key, int db) {
        return router.route(key).move(key, db);
    }

    @Override
    public Long presist(String key) {
        return router.route(key).presist(key);
    }

    @Override
    public Long pexpire(String key, long milliseconds) {
        return router.route(key).pexpire(key, milliseconds);
    }

    @Override
    public Long pexpireAt(String key, long timestamp) {
        return router.route(key).pexpireAt(key, timestamp);
    }

    @Override
    public Long pttl(String key) {
        return router.route(key).pttl(key);
    }

    @Override
    public String randomKey() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String rename(String key, String newKey) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Long renameNx(String key, String newKey) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String restore(String key, int expire, byte[] v) {
        return router.route(key).restore(key, expire, v);
    }

    @Override
    public String type(String key) {
        return router.route(key).type(key);
    }

    @Override
    public String set(String key, int o) {
        return router.route(key).set(key, o);
    }

    @Override
    public String set(String key, long o) {
        return router.route(key).set(key, o);
    }

    @Override
    public String set(String key, double o) {
        return router.route(key).set(key, o);
    }

    @Override
    public String set(String key, Object o) {
        return router.route(key).set(key, o);
    }

    @Override
    public Long setNx(String key, int o) {
        return router.route(key).setNx(key, o);
    }

    @Override
    public Long setNx(String key, long o) {
        return router.route(key).setNx(key, o);
    }

    @Override
    public Long setNx(String key, double o) {
        return router.route(key).setNx(key, o);
    }

    @Override
    public Long setNx(String key, Object o) {
        return router.route(key).setNx(key, o);
    }

    @Override
    public Long msetIntNx(Map<String, Integer> value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Long msetLongNx(Map<String, Long> value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Long msetDoubleNx(Map<String, Double> value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Long msetObjectNx(Map<String, ?> value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String setEx(String key, int o, int seconds) {
        return router.route(key).setEx(key, o, seconds);
    }

    @Override
    public String setEx(String key, long o, int seconds) {
        return router.route(key).setEx(key, o, seconds);
    }

    @Override
    public String setEx(String key, double o, int seconds) {
        return router.route(key).setEx(key, o, seconds);
    }

    @Override
    public String setEx(String key, Object o, int seconds) {
        return router.route(key).setEx(key, o, seconds);
    }

    @Override
    public String msetInt(Map<String, Integer> value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String msetLong(Map<String, Long> value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String msetDouble(Map<String, Double> value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String msetObject(Map<String, ?> value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object get(String key) {
        return router.route(key).get(key);
    }

    @Override
    public Integer getInt(String key) {
        return router.route(key).getInt(key);
    }

    @Override
    public Long getLong(String key) {
        return router.route(key).getLong(key);
    }

    @Override
    public Double getDouble(String key) {
        return router.route(key).getDouble(key);
    }

    @Override
    public List<?> mget(String[] key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Integer> mgetInt(String[] key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Long> mgetLong(String[] key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Double> mgetDouble(String[] key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Long decr(String key) {
        return router.route(key).decr(key);
    }

    @Override
    public Long decrBy(String key, int decrement) {
        return router.route(key).decrBy(key, decrement);
    }

    @Override
    public Long incr(String key) {
        return router.route(key).incr(key);
    }

    @Override
    public Long incrBy(String key, int decrement) {
        return router.route(key).incrBy(key, decrement);
    }

    @Override
    public Integer getAndSet(String key, int v) {
        return router.route(key).getAndSet(key, v);
    }

    @Override
    public Long getAndSet(String key, long v) {
        return router.route(key).getAndSet(key, v);
    }

    @Override
    public Double getAndSet(String key, double v) {
        return router.route(key).getAndSet(key, v);
    }

    @Override
    public Object getAndSet(String key, Object v) {
        return router.route(key).getAndSet(key, v);
    }

    @Override
    public Long hdel(String key, String... fields) {
        return router.route(key).hdel(key, fields);
    }

    @Override
    public Long hexists(String key, String field) {
        return router.route(key).hexists(key, field);
    }

    @Override
    public Long hset(String key, String field, int o) {
        return router.route(key).hset(key, field, o);
    }

    @Override
    public Long hset(String key, String field, long o) {
        return router.route(key).hset(key, field, o);
    }

    @Override
    public Long hset(String key, String field, double o) {
        return router.route(key).hset(key, field, o);
    }

    @Override
    public Long hset(String key, String field, Object o) {
        return router.route(key).hset(key, field, o);
    }

    @Override
    public Long hsetNx(String key, String field, int o) {
        return router.route(key).hsetNx(key, field, o);
    }

    @Override
    public Long hsetNx(String key, String field, long o) {
        return router.route(key).hsetNx(key, field, o);
    }

    @Override
    public Long hsetNx(String key, String field, double o) {
        return router.route(key).hsetNx(key, field, o);
    }

    @Override
    public Long hsetNx(String key, String field, Object o) {
        return router.route(key).hsetNx(key, field, o);
    }

    @Override
    public Object hget(String key, String field) {
        return router.route(key).hget(key, field);
    }

    @Override
    public Integer hgetInt(String key, String field) {
        return router.route(key).hgetInt(key, field);
    }

    @Override
    public Long hgetLong(String key, String field) {
        return router.route(key).hgetLong(key, field);
    }

    @Override
    public Double hgetDouble(String key, String field) {
        return router.route(key).hgetDouble(key, field);
    }

    @Override
    public Map<String, Integer> hgetAllInt(String key) {
        return router.route(key).hgetAllInt(key);
    }

    @Override
    public Map<String, Long> hgetAllLong(String key) {
        return router.route(key).hgetAllLong(key);
    }

    @Override
    public Map<String, Double> hgetAllDouble(String key) {
        return router.route(key).hgetAllDouble(key);
    }

    @Override
    public Map<String, ?> hgetAll(String key) {
        return router.route(key).hgetAll(key);
    }

    @Override
    public Long hincrBy(String key, String field, int decrement) {
        return router.route(key).hincrBy(key, field, decrement);
    }

    @Override
    public List<String> hkeys(String key) {
        return router.route(key).hkeys(key);
    }

    @Override
    public Long hlen(String key) {
        return router.route(key).hlen(key);
    }

    @Override
    public List<?> hmget(String key, String[] fields) {
        return router.route(key).hmget(key, fields);
    }

    @Override
    public List<Integer> hmgetInt(String key, String[] fields) {
        return router.route(key).hmgetInt(key, fields);
    }

    @Override
    public List<Long> hmgetLong(String key, String[] fields) {
        return router.route(key).hmgetLong(key, fields);
    }

    @Override
    public List<Double> hmgetDouble(String key, String[] fields) {
        return router.route(key).hmgetDouble(key, fields);
    }

    @Override
    public String hmsetInt(String key, Map<String, Integer> value) {
        return router.route(key).hmsetInt(key, value);
    }

    @Override
    public String hmsetLong(String key, Map<String, Long> value) {
        return router.route(key).hmsetLong(key, value);
    }

    @Override
    public String hmsetDouble(String key, Map<String, Double> value) {
        return router.route(key).hmsetDouble(key, value);
    }

    @Override
    public String hmset(String key, Map<String, ?> value) {
        return router.route(key).hmset(key, value);
    }

    @Override
    public List<Integer> hvalsInt(String key) {
        return router.route(key).hvalsInt(key);
    }

    @Override
    public List<Long> hvalsLong(String key) {
        return router.route(key).hvalsLong(key);
    }

    @Override
    public List<Double> hvalsDouble(String key) {
        return router.route(key).hvalsDouble(key);
    }

    @Override
    public List<?> hvals(String key) {
        return router.route(key).hvals(key);
    }

    @Override
    public Object lindex(String key, int index) {
        return router.route(key).lindex(key, index);
    }

    @Override
    public Long linsertBefore(String key, Object before, Object value) {
        return router.route(key).linsertBefore(key, before, value);
    }

    @Override
    public Long linsertAfter(String key, Object after, Object value) {
        return router.route(key).linsertAfter(key, after, value);
    }

    @Override
    public Long llen(String key) {
        return router.route(key).llen(key);
    }

    @Override
    public Object lpop(String key) {
        return router.route(key).lpop(key);
    }

    @Override
    public Long lpush(String key, Object... values) {
        return router.route(key).lpush(key, values);
    }

    @Override
    public Long lpushx(String key, Object value) {
        return router.route(key).lpushx(key, value);
    }

    @Override
    public List<?> lrange(String key, int start, int stop) {
        return router.route(key).lrange(key, start, stop);
    }

    @Override
    public Long lrem(String key, int count, Object value) {
        return router.route(key).lrem(key, count, value);
    }

    @Override
    public String lset(String key, int index, Object value) {
        return router.route(key).lset(key, index, value);
    }

    @Override
    public String ltrim(String key, int start, int stop) {
        return router.route(key).ltrim(key, start, stop);
    }

    @Override
    public Object rpop(String key) {
        return router.route(key).rpop(key);
    }

    @Override
    public Long rpush(String key, Object... values) {
        return router.route(key).rpush(key, values);
    }

    @Override
    public Long rpushx(String key, Object value) {
        return router.route(key).rpushx(key, value);
    }

    @Override
    public Object rpoplpush(String source, String destination) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Long sadd(String key, Object... values) {
        return router.route(key).sadd(key, values);
    }

    @Override
    public Long scard(String key) {
        return router.route(key).scard(key);
    }

    @Override
    public List<?> sdiff(String key, String... diffs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Long sdiffStore(String destination, String key, String... diffs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<?> sinter(String key, String... keys) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Long sinterStore(String destination, String key, String... keys) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Long sisMember(String key, Object member) {
        return router.route(key).sisMember(key, member);
    }

    @Override
    public List<?> smembers(String key) {
        return router.route(key).smembers(key);
    }

    @Override
    public Long smove(String source, String destination, Object member) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object spop(String key) {
        return router.route(key).spop(key);
    }

    @Override
    public Object srandomMember(String key) {
        return router.route(key).srandomMember(key);
    }

    @Override
    public List<?> srandomMember(String key, int count) {
        return router.route(key).srandomMember(key, count);
    }

    @Override
    public Long srem(String key, Object... members) {
        return router.route(key).srem(key, members);
    }

    @Override
    public List<?> sunion(String key, String... keys) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Long sunionStore(String destination, String key, String... keys) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Long zadd(String key, double score, Object member, ZEntity... others) {
        return router.route(key).zadd(key, score, member, others);
    }

    @Override
    public Long zcard(String key) {
        return router.route(key).zcard(key);
    }

    @Override
    public Long zcount(String key, String min, String max) {
        return router.route(key).zcount(key, min, max);
    }

    @Override
    public Double zincrBy(String key, double increment, Object member) {
        return router.route(key).zincrBy(key, increment, member);
    }

    @Override
    public Long zinterStore(String destination, String[] keys, int[] weights, ZSetAggregate aggregate) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Long zunionStore(String destination, String[] keys, int[] weights, ZSetAggregate aggregate) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<?> zrange(String key, int start, int stop) {
        return router.route(key).zrange(key, start, stop);
    }

    @Override
    public List<ZEntity<?>> zrangeWithScores(String key, int start, int stop) {
        return router.route(key).zrangeWithScores(key, start, stop);
    }

    @Override
    public List<?> zrangeByScore(String key, String min, String max) {
        return router.route(key).zrangeByScore(key, min, max);
    }

    @Override
    public List<?> zrangeByScore(String key, String min, String max, int offset, int count) {
        return router.route(key).zrangeByScore(key, min, max, offset, count);
    }

    @Override
    public List<ZEntity<?>> zrangeByScoreWithScores(String key, String min, String max) {
        return router.route(key).zrangeByScoreWithScores(key, min, max);
    }

    @Override
    public List<ZEntity<?>> zrangeByScoreWithScores(String key, String min, String max, int offset, int count) {
        return router.route(key).zrangeByScoreWithScores(key, min, max, offset, count);
    }

    @Override
    public Long zrank(String key, Object member) {
        return router.route(key).zrank(key, member);
    }

    @Override
    public Long zrem(String key, Object... members) {
        return router.route(key).zrem(key, members);
    }

    @Override
    public Long zremRangeByRank(String key, int start, int stop) {
        return router.route(key).zremRangeByRank(key, start, stop);
    }

    @Override
    public Long zremRangeByScore(String key, String min, String max) {
        return router.route(key).zremRangeByScore(key, min, max);
    }

    @Override
    public List<?> zrevRange(String key, int start, int stop) {
        return router.route(key).zrevRange(key, start, stop);
    }

    @Override
    public List<ZEntity<?>> zrevRangeWithScores(String key, int start, int stop) {
        return router.route(key).zrevRangeWithScores(key, start, stop);
    }

    @Override
    public List<?> zrevRangeByScore(String key, String max, String min) {
        return router.route(key).zrevRangeByScore(key, max, min);
    }

    @Override
    public List<?> zrevRangeByScore(String key, String max, String min, int offset, int count) {
        return router.route(key).zrevRangeByScore(key, max, min, offset, count);
    }

    @Override
    public List<ZEntity<?>> zrevRangeByScoreWithScores(String key, String max, String min) {
        return router.route(key).zrevRangeByScoreWithScores(key, max, min);
    }

    @Override
    public List<ZEntity<?>> zrevRangeByScoreWithScores(String key, String max, String min, int offset, int count) {
        return router.route(key).zrevRangeByScoreWithScores(key, max, min, offset, count);
    }

    @Override
    public Long zrevRank(String key, Object member) {
        return router.route(key).zrevRank(key, member);
    }

    @Override
    public Double zscore(String key, Object member) {
        return router.route(key).zscore(key, member);
    }

    @Override
    public Reply eval(String script, String[] keys, byte[]... args) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Reply evalSha(String sha1, String[] keys, byte[]... args) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String scriptLoad(String script) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Long scriptExists(String script) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Integer> scriptExists(String[] scripts) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String scriptFlush() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String scriptKill() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String bgRewriteAOF() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String bgSave() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String clientKill(String ip, int port) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String clientList() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String configGet(String parameter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String configResetStat() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String configSet(String config, String parameter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Long dbSize() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String flushAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String flushDB() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String info() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Long lastSave() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String save() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String shutdown(boolean save) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String slaveOf(String host, int port) {
        throw new UnsupportedOperationException();
    }
}
