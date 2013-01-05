package mobi.app.redis.ha;

import mobi.app.redis.*;
import mobi.app.redis.netty.SyncRedisClient;
import mobi.app.redis.netty.reply.Reply;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * User: thor
 * Date: 13-1-4
 * Time: 下午2:04
 * <p/>
 * Master-Slave Redis Client.write to master node and read from slave node by default.
 */
public class MSRedisClient implements RedisClient {
    final RedisConnection masterConnection;
    final List<RedisConnection> slaveConnections;
    final RedisClient master;
    final Queue<RedisClient> slaves;
    final List<RedisClient> virtualSlaves;
    final Random random = new Random();

    public MSRedisClient(RedisConnection masterConnection, List<RedisConnection> slaveConnections) {
        this.masterConnection = masterConnection;
        this.slaveConnections = slaveConnections;
        master = new SyncRedisClient(masterConnection);
        virtualSlaves = new CopyOnWriteArrayList<RedisClient>();
        slaves = new LinkedBlockingQueue<RedisClient>(slaveConnections.size());
        for (final RedisConnection connection : slaveConnections) {
            final RedisClient redisClient = new SyncRedisClient(connection);
            slaves.add(redisClient);

            //根据权重将slave node放到虚拟节点中，权重高的放的越多。被访问的次数也就越多
            for (int i = 0; i < connection.weight; i++) {
                virtualSlaves.add(redisClient);
            }
            //redis 连接异常时自动从slave列表中删除
            redisClient.setClosedHandler(new AsyncRedisClient.ClosedHandler() {
                @Override
                public void onClosed(AsyncRedisClient client) {
                    slaves.remove(redisClient);
                    while (virtualSlaves.remove(redisClient)) {
                    }
                }
            });
            //redis 重连成功时重新加入slave列表
            redisClient.setConnectedHandler(new AsyncRedisClient.ConnectedHandler() {
                @Override
                public void onConnected(AsyncRedisClient client) {
                    slaves.add(redisClient);
                    for (int i = 0; i < connection.weight; i++) {
                        virtualSlaves.add(redisClient);
                    }
                }
            });
        }

    }

    protected RedisClient takeSlave() {
        int idx = random.nextInt(virtualSlaves.size());
        return virtualSlaves.get(idx);
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
    public void close() {
        master.close();
        for (RedisClient slave : slaves) {
            slave.close();
        }
    }

    @Override
    public String auth(String password) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String echo(String message) {
        return master.echo(message);
    }

    @Override
    public String ping() {
        return master.ping();
    }

    @Override
    public String select(int db) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String quit() {
        for (RedisClient slave : slaves) {
            slave.quit();
        }
        return master.quit();
    }

    @Override
    public Long delete(String key) {
        return master.delete(key);
    }

    @Override
    public Long delete(String... key) {
        return master.delete(key);
    }

    @Override
    public byte[] dump(String key) {
        return master.dump(key);
    }

    @Override
    public Long exists(String key) {
        return master.exists(key);
    }

    @Override
    public Long expire(String key, int seconds) {
        return master.expire(key, seconds);
    }

    @Override
    public Long expireAt(String key, int timestamp) {
        return master.expireAt(key, timestamp);
    }

    @Override
    public Long ttl(String key) {
        return master.ttl(key);
    }

    @Override
    public List<String> keys(String pattern) {
        return master.keys(pattern);
    }

    @Override
    public String migrate(String host, int port, String key, int db, int timeOut) {
        return master.migrate(host, port, key, db, timeOut);
    }

    @Override
    public Long move(String key, int db) {
        return master.move(key, db);
    }

    @Override
    public Long presist(String key) {
        return master.presist(key);
    }

    @Override
    public Long pexpire(String key, long milliseconds) {
        return master.pexpire(key, milliseconds);
    }

    @Override
    public Long pexpireAt(String key, long timestamp) {
        return master.pexpireAt(key, timestamp);
    }

    @Override
    public Long pttl(String key) {
        return master.pttl(key);
    }

    @Override
    public String randomKey() {
        return master.randomKey();
    }

    @Override
    public String rename(String key, String newKey) {
        return master.rename(key, newKey);
    }

    @Override
    public Long renameNx(String key, String newKey) {
        return master.renameNx(key, newKey);
    }

    @Override
    public String restore(String key, int expire, byte[] v) {
        return master.restore(key, expire, v);
    }

    @Override
    public String type(String key) {
        return master.type(key);
    }

    @Override
    public String set(String key, int o) {
        return master.set(key, o);
    }

    @Override
    public String set(String key, long o) {
        return master.set(key, o);
    }

    @Override
    public String set(String key, double o) {
        return master.set(key, o);
    }

    @Override
    public String set(String key, Object o) {
        return master.set(key, o);
    }

    @Override
    public Long setNx(String key, int o) {
        return master.setNx(key, o);
    }

    @Override
    public Long setNx(String key, long o) {
        return master.setNx(key, o);
    }

    @Override
    public Long setNx(String key, double o) {
        return master.setNx(key, o);
    }

    @Override
    public Long setNx(String key, Object o) {
        return master.setNx(key, o);
    }

    @Override
    public Long msetIntNx(Map<String, Integer> value) {
        return master.msetIntNx(value);
    }

    @Override
    public Long msetLongNx(Map<String, Long> value) {
        return master.msetLongNx(value);
    }

    @Override
    public Long msetDoubleNx(Map<String, Double> value) {
        return master.msetDoubleNx(value);
    }

    @Override
    public Long msetObjectNx(Map<String, ?> value) {
        return master.msetObjectNx(value);
    }

    @Override
    public String setEx(String key, int o, int seconds) {
        return master.setEx(key, o, seconds);
    }

    @Override
    public String setEx(String key, long o, int seconds) {
        return master.setEx(key, o, seconds);
    }

    @Override
    public String setEx(String key, double o, int seconds) {
        return master.setEx(key, o, seconds);
    }

    @Override
    public String setEx(String key, Object o, int seconds) {
        return master.setEx(key, o, seconds);
    }

    @Override
    public String msetInt(Map<String, Integer> value) {
        return master.msetInt(value);
    }

    @Override
    public String msetLong(Map<String, Long> value) {
        return master.msetLong(value);
    }

    @Override
    public String msetDouble(Map<String, Double> value) {
        return master.msetDouble(value);
    }

    @Override
    public String msetObject(Map<String, ?> value) {
        return master.msetObject(value);
    }

    @Override
    public Object get(String key) {
        return takeSlave().get(key);
    }

    @Override
    public Integer getInt(String key) {
        return takeSlave().getInt(key);
    }

    @Override
    public Long getLong(String key) {
        return takeSlave().getLong(key);
    }

    @Override
    public Double getDouble(String key) {
        return takeSlave().getDouble(key);
    }

    @Override
    public List<?> mget(String[] key) {
        return takeSlave().mget(key);
    }

    @Override
    public List<Integer> mgetInt(String[] key) {
        return takeSlave().mgetInt(key);
    }

    @Override
    public List<Long> mgetLong(String[] key) {
        return takeSlave().mgetLong(key);
    }

    @Override
    public List<Double> mgetDouble(String[] key) {
        return takeSlave().mgetDouble(key);
    }

    @Override
    public Long decr(String key) {
        return master.decr(key);
    }

    @Override
    public Long decrBy(String key, int decrement) {
        return master.decrBy(key, decrement);
    }

    @Override
    public Long incr(String key) {
        return master.incr(key);
    }

    @Override
    public Long incrBy(String key, int decrement) {
        return master.incrBy(key, decrement);
    }

    @Override
    public Integer getAndSet(String key, int v) {
        return master.getAndSet(key, v);
    }

    @Override
    public Long getAndSet(String key, long v) {
        return master.getAndSet(key, v);
    }

    @Override
    public Double getAndSet(String key, double v) {
        return master.getAndSet(key, v);
    }

    @Override
    public Object getAndSet(String key, Object v) {
        return master.getAndSet(key, v);
    }

    @Override
    public Long hdel(String key, String... fields) {
        return master.hdel(key, fields);
    }

    @Override
    public Long hexists(String key, String field) {
        return master.hexists(key, field);
    }

    @Override
    public Long hset(String key, String field, int o) {
        return master.hset(key, field, o);
    }

    @Override
    public Long hset(String key, String field, long o) {
        return master.hset(key, field, o);
    }

    @Override
    public Long hset(String key, String field, double o) {
        return master.hset(key, field, o);
    }

    @Override
    public Long hset(String key, String field, Object o) {
        return master.hset(key, field, o);
    }

    @Override
    public Long hsetNx(String key, String field, int o) {
        return master.hsetNx(key, field, o);
    }

    @Override
    public Long hsetNx(String key, String field, long o) {
        return master.hsetNx(key, field, o);
    }

    @Override
    public Long hsetNx(String key, String field, double o) {
        return master.hsetNx(key, field, o);
    }

    @Override
    public Long hsetNx(String key, String field, Object o) {
        return master.hsetNx(key, field, o);
    }

    @Override
    public Object hget(String key, String field) {
        return takeSlave().hget(key, field);
    }

    @Override
    public Integer hgetInt(String key, String field) {
        return takeSlave().hgetInt(key, field);
    }

    @Override
    public Long hgetLong(String key, String field) {
        return takeSlave().hgetLong(key, field);
    }

    @Override
    public Double hgetDouble(String key, String field) {
        return takeSlave().hgetDouble(key, field);
    }

    @Override
    public Map<String, Integer> hgetAllInt(String key) {
        return takeSlave().hgetAllInt(key);
    }

    @Override
    public Map<String, Long> hgetAllLong(String key) {
        return takeSlave().hgetAllLong(key);
    }

    @Override
    public Map<String, Double> hgetAllDouble(String key) {
        return takeSlave().hgetAllDouble(key);
    }

    @Override
    public Map<String, ?> hgetAll(String key) {
        return takeSlave().hgetAll(key);
    }

    @Override
    public Long hincrBy(String key, String field, int decrement) {
        return master.hincrBy(key, field, decrement);
    }

    @Override
    public List<String> hkeys(String key) {
        return master.hkeys(key);
    }

    @Override
    public Long hlen(String key) {
        return master.hlen(key);
    }

    @Override
    public List<?> hmget(String key, String[] fields) {
        return takeSlave().hmget(key, fields);
    }

    @Override
    public List<Integer> hmgetInt(String key, String[] fields) {
        return takeSlave().hmgetInt(key, fields);
    }

    @Override
    public List<Long> hmgetLong(String key, String[] fields) {
        return takeSlave().hmgetLong(key, fields);
    }

    @Override
    public List<Double> hmgetDouble(String key, String[] fields) {
        return takeSlave().hmgetDouble(key, fields);
    }

    @Override
    public String hmsetInt(String key, Map<String, Integer> value) {
        return master.hmsetInt(key, value);
    }

    @Override
    public String hmsetLong(String key, Map<String, Long> value) {
        return master.hmsetLong(key, value);
    }

    @Override
    public String hmsetDouble(String key, Map<String, Double> value) {
        return master.hmsetDouble(key, value);
    }

    @Override
    public String hmset(String key, Map<String, ?> value) {
        return master.hmset(key, value);
    }

    @Override
    public List<Integer> hvalsInt(String key) {
        return takeSlave().hvalsInt(key);
    }

    @Override
    public List<Long> hvalsLong(String key) {
        return takeSlave().hvalsLong(key);
    }

    @Override
    public List<Double> hvalsDouble(String key) {
        return takeSlave().hvalsDouble(key);
    }

    @Override
    public List<?> hvals(String key) {
        return takeSlave().hvals(key);
    }

    @Override
    public Object lindex(String key, int index) {
        return master.lindex(key, index);
    }

    @Override
    public Long linsertBefore(String key, Object before, Object value) {
        return master.linsertBefore(key, before, value);
    }

    @Override
    public Long linsertAfter(String key, Object after, Object value) {
        return master.linsertAfter(key, after, value);
    }

    @Override
    public Long llen(String key) {
        return master.llen(key);
    }

    @Override
    public Object lpop(String key) {
        return master.lpop(key);
    }

    @Override
    public Long lpush(String key, Object... values) {
        return master.lpush(key, values);
    }

    @Override
    public Long lpushx(String key, Object value) {
        return master.lpushx(key, value);
    }

    @Override
    public List<?> lrange(String key, int start, int stop) {
        return takeSlave().lrange(key, start, stop);
    }

    @Override
    public Long lrem(String key, int count, Object value) {
        return master.lrem(key, count, value);
    }

    @Override
    public String lset(String key, int index, Object value) {
        return master.lset(key, index, value);
    }

    @Override
    public String ltrim(String key, int start, int stop) {
        return master.ltrim(key, start, stop);
    }

    @Override
    public Object rpop(String key) {
        return master.rpop(key);
    }

    @Override
    public Long rpush(String key, Object... values) {
        return master.rpush(key, values);
    }

    @Override
    public Long rpushx(String key, Object value) {
        return master.rpushx(key, value);
    }

    @Override
    public Object rpoplpush(String source, String destination) {
        return master.rpoplpush(source, destination);
    }

    @Override
    public Long sadd(String key, Object... values) {
        return master.sadd(key, values);
    }

    @Override
    public Long scard(String key) {
        return master.scard(key);
    }

    @Override
    public List<?> sdiff(String key, String... diffs) {
        return master.sdiff(key, diffs);
    }

    @Override
    public Long sdiffStore(String destination, String key, String... diffs) {
        return master.sdiffStore(destination, key, diffs);
    }

    @Override
    public List<?> sinter(String key, String... keys) {
        return master.sinter(key, keys);
    }

    @Override
    public Long sinterStore(String destination, String key, String... keys) {
        return master.sinterStore(destination, key, keys);
    }

    @Override
    public Long sisMember(String key, Object member) {
        return master.sisMember(key, member);
    }

    @Override
    public List<?> smembers(String key) {
        return takeSlave().smembers(key);
    }

    @Override
    public Long smove(String source, String destination, Object member) {
        return master.smove(source, destination, member);
    }

    @Override
    public Object spop(String key) {
        return master.spop(key);
    }

    @Override
    public Object srandomMember(String key) {
        return master.srandomMember(key);
    }

    @Override
    public List<?> srandomMember(String key, int count) {
        return master.srandomMember(key, count);
    }

    @Override
    public Long srem(String key, Object... members) {
        return master.srem(key, members);
    }

    @Override
    public List<?> sunion(String key, String... keys) {
        return master.sunion(key, keys);
    }

    @Override
    public Long sunionStore(String destination, String key, String... keys) {
        return master.sunionStore(destination, key, keys);
    }

    @Override
    public Long zadd(String key, double score, Object member, ZEntity... others) {
        return master.zadd(key, score, member, others);
    }

    @Override
    public Long zcard(String key) {
        return master.zcard(key);
    }

    @Override
    public Long zcount(String key, String min, String max) {
        return master.zcount(key, min, max);
    }

    @Override
    public Double zincrBy(String key, double increment, Object member) {
        return master.zincrBy(key, increment, member);
    }

    @Override
    public Long zinterStore(String destination, String[] keys, int[] weights, ZSetAggregate aggregate) {
        return master.zinterStore(destination, keys, weights, aggregate);
    }

    @Override
    public Long zunionStore(String destination, String[] keys, int[] weights, ZSetAggregate aggregate) {
        return master.zunionStore(destination, keys, weights, aggregate);
    }

    @Override
    public List<?> zrange(String key, int start, int stop) {
        return takeSlave().zrange(key, start, stop);
    }

    @Override
    public List<ZEntity<?>> zrangeWithScores(String key, int start, int stop) {
        return takeSlave().zrangeWithScores(key, start, stop);
    }

    @Override
    public List<?> zrangeByScore(String key, String min, String max) {
        return takeSlave().zrangeByScore(key, min, max);
    }

    @Override
    public List<?> zrangeByScore(String key, String min, String max, int offset, int count) {
        return takeSlave().zrangeByScore(key, min, max, offset, count);
    }

    @Override
    public List<ZEntity<?>> zrangeByScoreWithScores(String key, String min, String max) {
        return takeSlave().zrangeByScoreWithScores(key, min, max);
    }

    @Override
    public List<ZEntity<?>> zrangeByScoreWithScores(String key, String min, String max, int offset, int count) {
        return takeSlave().zrangeByScoreWithScores(key, min, max, offset, count);
    }

    @Override
    public Long zrank(String key, Object member) {
        return takeSlave().zrank(key, member);
    }

    @Override
    public Long zrem(String key, Object... members) {
        return master.zrem(key, members);
    }

    @Override
    public Long zremRangeByRank(String key, int start, int stop) {
        return master.zremRangeByRank(key, start, stop);
    }

    @Override
    public Long zremRangeByScore(String key, String min, String max) {
        return master.zremRangeByScore(key, min, max);
    }

    @Override
    public List<?> zrevRange(String key, int start, int stop) {
        return takeSlave().zrevRange(key, start, stop);
    }

    @Override
    public List<ZEntity<?>> zrevRangeWithScores(String key, int start, int stop) {
        return takeSlave().zrevRangeWithScores(key, start, stop);
    }

    @Override
    public List<?> zrevRangeByScore(String key, String max, String min) {
        return takeSlave().zrevRangeByScore(key, max, min);
    }

    @Override
    public List<?> zrevRangeByScore(String key, String max, String min, int offset, int count) {
        return takeSlave().zrevRangeByScore(key, max, min, offset, count);
    }

    @Override
    public List<ZEntity<?>> zrevRangeByScoreWithScores(String key, String max, String min) {
        return takeSlave().zrevRangeByScoreWithScores(key, max, min);
    }

    @Override
    public List<ZEntity<?>> zrevRangeByScoreWithScores(String key, String max, String min, int offset, int count) {
        return takeSlave().zrevRangeByScoreWithScores(key, max, min, offset, count);
    }

    @Override
    public Long zrevRank(String key, Object member) {
        return takeSlave().zrevRank(key, member);
    }

    @Override
    public Double zscore(String key, Object member) {
        return master.zscore(key, member);
    }

    @Override
    public Reply eval(String script, String[] keys, byte[]... args) {
        return master.eval(script, keys, args);
    }

    @Override
    public Reply evalSha(String sha1, String[] keys, byte[]... args) {
        return master.evalSha(sha1, keys, args);
    }

    @Override
    public String scriptLoad(String script) {
        return master.scriptLoad(script);
    }

    @Override
    public Long scriptExists(String script) {
        return master.scriptExists(script);
    }

    @Override
    public List<Integer> scriptExists(String[] scripts) {
        return master.scriptExists(scripts);
    }

    @Override
    public String scriptFlush() {
        return master.scriptFlush();
    }

    @Override
    public String scriptKill() {
        return master.scriptKill();
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
        return master.flushAll();
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
