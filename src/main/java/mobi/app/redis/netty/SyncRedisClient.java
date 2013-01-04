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
        return callAsyncClient(new AsyncClientRunnable<String>() {
            @Override
            public String call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.ping().get(timeout, timeUnit);
            }
        });
    }

    @Override
    public String select(final int db) {
        return callAsyncClient(new AsyncClientRunnable<String>() {
            @Override
            public String call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.select(db).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public String quit() {
        return callAsyncClient(new AsyncClientRunnable<String>() {
            @Override
            public String call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.quit().get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long delete(final String key) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.delete(key).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long delete(final String... key) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.delete(key).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public byte[] dump(final String key) {
        return callAsyncClient(new AsyncClientRunnable<byte[]>() {
            @Override
            public byte[] call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.dump(key).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long exists(final String key) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.exists(key).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long expire(final String key, final int seconds) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.expire(key, seconds).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long expireAt(final String key, final int timestamp) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.expireAt(key, timestamp).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long ttl(final String key) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.ttl(key).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public List<String> keys(final String pattern) {
        return callAsyncClient(new AsyncClientRunnable<List<String>>() {
            @Override
            public List<String> call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.keys(pattern).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public String migrate(final String host, final int port, final String key, final int db, final int timeOut) {
        return callAsyncClient(new AsyncClientRunnable<String>() {
            @Override
            public String call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.migrate(host, port, key, db, timeOut).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long move(final String key, final int db) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.move(key, db).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long presist(final String key) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.presist(key).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long pexpire(final String key, final long milliseconds) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.pexpire(key, milliseconds).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long pexpireAt(final String key, final long timestamp) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.pexpireAt(key, timestamp).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long pttl(final String key) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.pttl(key).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public String randomKey() {
        return callAsyncClient(new AsyncClientRunnable<String>() {
            @Override
            public String call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.randomKey().get(timeout, timeUnit);
            }
        });
    }

    @Override
    public String rename(final String key, final String newKey) {
        return callAsyncClient(new AsyncClientRunnable<String>() {
            @Override
            public String call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.rename(key, newKey).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long renameNx(final String key, final String newKey) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.renameNx(key, newKey).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public String restore(final String key, final int expire, final byte[] v) {
        return callAsyncClient(new AsyncClientRunnable<String>() {
            @Override
            public String call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.restore(key, expire, v).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public String type(final String key) {
        return callAsyncClient(new AsyncClientRunnable<String>() {
            @Override
            public String call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.type(key).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public String set(final String key, final int o) {
        return callAsyncClient(new AsyncClientRunnable<String>() {
            @Override
            public String call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.set(key, o).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public String set(final String key, final long o) {
        return callAsyncClient(new AsyncClientRunnable<String>() {
            @Override
            public String call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.set(key, o).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public String set(final String key, final double o) {
        return callAsyncClient(new AsyncClientRunnable<String>() {
            @Override
            public String call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.set(key, o).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public String set(final String key, final Object o) {
        return callAsyncClient(new AsyncClientRunnable<String>() {
            @Override
            public String call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.set(key, o).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long setNx(final String key, final int o) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.setNx(key, o).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long setNx(final String key, final long o) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.setNx(key, o).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long setNx(final String key, final double o) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.setNx(key, o).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long setNx(final String key, final Object o) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.setNx(key, o).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long msetIntNx(final Map<String, Integer> value) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.msetIntNx(value).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long msetLongNx(final Map<String, Long> value) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.msetLongNx(value).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long msetDoubleNx(final Map<String, Double> value) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.msetDoubleNx(value).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long msetObjectNx(final Map<String, ?> value) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.msetObjectNx(value).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public String setEx(final String key, final int o, final int seconds) {
        return callAsyncClient(new AsyncClientRunnable<String>() {
            @Override
            public String call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.setEx(key, o, seconds).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public String setEx(final String key, final long o, final int seconds) {
        return callAsyncClient(new AsyncClientRunnable<String>() {
            @Override
            public String call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.setEx(key, o, seconds).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public String setEx(final String key, final double o, final int seconds) {
        return callAsyncClient(new AsyncClientRunnable<String>() {
            @Override
            public String call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.setEx(key, o, seconds).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public String setEx(final String key, final Object o, final int seconds) {
        return callAsyncClient(new AsyncClientRunnable<String>() {
            @Override
            public String call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.setEx(key, o, seconds).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public String msetInt(final Map<String, Integer> value) {
        return callAsyncClient(new AsyncClientRunnable<String>() {
            @Override
            public String call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.msetInt(value).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public String msetLong(final Map<String, Long> value) {
        return callAsyncClient(new AsyncClientRunnable<String>() {
            @Override
            public String call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.msetLong(value).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public String msetDouble(final Map<String, Double> value) {
        return callAsyncClient(new AsyncClientRunnable<String>() {
            @Override
            public String call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.msetDouble(value).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public String msetObject(final Map<String, ?> value) {
        return callAsyncClient(new AsyncClientRunnable<String>() {
            @Override
            public String call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.msetObject(value).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Object get(final String key) {
        return callAsyncClient(new AsyncClientRunnable<Object>() {
            @Override
            public Object call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.get(key).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Integer getInt(final String key) {
        return callAsyncClient(new AsyncClientRunnable<Integer>() {
            @Override
            public Integer call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.getInt(key).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long getLong(final String key) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.getLong(key).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Double getDouble(final String key) {
        return callAsyncClient(new AsyncClientRunnable<Double>() {
            @Override
            public Double call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.getDouble(key).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public List<?> mget(final String[] key) {
        return callAsyncClient(new AsyncClientRunnable<List<?>>() {
            @Override
            public List<?> call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.mget(key).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public List<Integer> mgetInt(final String[] key) {
        return callAsyncClient(new AsyncClientRunnable<List<Integer>>() {
            @Override
            public List<Integer> call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.mgetInt(key).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public List<Long> mgetLong(final String[] key) {
        return callAsyncClient(new AsyncClientRunnable<List<Long>>() {
            @Override
            public List<Long> call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.mgetLong(key).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public List<Double> mgetDouble(final String[] key) {
        return callAsyncClient(new AsyncClientRunnable<List<Double>>() {
            @Override
            public List<Double> call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.mgetDouble(key).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long decr(final String key) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.decr(key).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long decrBy(final String key, final int decrement) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.decrBy(key, decrement).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long incr(final String key) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.incr(key).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long incrBy(final String key, final int decrement) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.incrBy(key, decrement).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Integer getAndSet(final String key, final int v) {
        return callAsyncClient(new AsyncClientRunnable<Integer>() {
            @Override
            public Integer call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.getAndSet(key, v).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long getAndSet(final String key, final long v) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.getAndSet(key, v).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Double getAndSet(final String key, final double v) {
        return callAsyncClient(new AsyncClientRunnable<Double>() {
            @Override
            public Double call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.getAndSet(key, v).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Object getAndSet(final String key, final Object v) {
        return callAsyncClient(new AsyncClientRunnable<Object>() {
            @Override
            public Object call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.getAndSet(key, v).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long hdel(final String key, final String... fields) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.hdel(key, fields).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long hexists(final String key, final String field) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.hexists(key, field).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long hset(final String key, final String field, final int o) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.hset(key, field, o).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long hset(final String key, final String field, final long o) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.hset(key, field, o).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long hset(final String key, final String field, final double o) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.hset(key, field, o).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long hset(final String key, final String field, final Object o) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.hset(key, field, o).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long hsetNx(final String key, final String field, final int o) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.hsetNx(key, field, o).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long hsetNx(final String key, final String field, final long o) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.hsetNx(key, field, o).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long hsetNx(final String key, final String field, final double o) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.hsetNx(key, field, o).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long hsetNx(final String key, final String field, final Object o) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.hsetNx(key, field, o).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Object hget(final String key, final String field) {
        return callAsyncClient(new AsyncClientRunnable<Object>() {
            @Override
            public Object call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.hget(key, field).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Integer hgetInt(final String key, final String field) {
        return callAsyncClient(new AsyncClientRunnable<Integer>() {
            @Override
            public Integer call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.hgetInt(key, field).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long hgetLong(final String key, final String field) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.hgetLong(key, field).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Double hgetDouble(final String key, final String field) {
        return callAsyncClient(new AsyncClientRunnable<Double>() {
            @Override
            public Double call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.hgetDouble(key, field).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Map<String, Integer> hgetAllInt(final String key) {
        return callAsyncClient(new AsyncClientRunnable<Map<String, Integer>>() {
            @Override
            public Map<String, Integer> call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.hgetAllInt(key).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Map<String, Long> hgetAllLong(final String key) {
        return callAsyncClient(new AsyncClientRunnable<Map<String, Long>>() {
            @Override
            public Map<String, Long> call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.hgetAllLong(key).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Map<String, Double> hgetAllDouble(final String key) {
        return callAsyncClient(new AsyncClientRunnable<Map<String, Double>>() {
            @Override
            public Map<String, Double> call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.hgetAllDouble(key).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Map<String, ?> hgetAll(final String key) {
        return callAsyncClient(new AsyncClientRunnable<Map<String, ?>>() {
            @Override
            public Map<String, ?> call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.hgetAll(key).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long hincrBy(final String key, final String field, final int decrement) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.hincrBy(key, field, decrement).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public List<String> hkeys(final String key) {
        return callAsyncClient(new AsyncClientRunnable<List<String>>() {
            @Override
            public List<String> call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.hkeys(key).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long hlen(final String key) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.hlen(key).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public List<?> hmget(final String key, final String[] fields) {
        return callAsyncClient(new AsyncClientRunnable<List<?>>() {
            @Override
            public List<?> call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.hmget(key, fields).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public List<Integer> hmgetInt(final String key, final String[] fields) {
        return callAsyncClient(new AsyncClientRunnable<List<Integer>>() {
            @Override
            public List<Integer> call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.hmgetInt(key, fields).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public List<Long> hmgetLong(final String key, final String[] fields) {
        return callAsyncClient(new AsyncClientRunnable<List<Long>>() {
            @Override
            public List<Long> call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.hmgetLong(key, fields).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public List<Double> hmgetDouble(final String key, final String[] fields) {
        return callAsyncClient(new AsyncClientRunnable<List<Double>>() {
            @Override
            public List<Double> call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.hmgetDouble(key, fields).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public String hmsetInt(final String key, final Map<String, Integer> value) {
        return callAsyncClient(new AsyncClientRunnable<String>() {
            @Override
            public String call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.hmsetInt(key, value).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public String hmsetLong(final String key, final Map<String, Long> value) {
        return callAsyncClient(new AsyncClientRunnable<String>() {
            @Override
            public String call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.hmsetLong(key, value).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public String hmsetDouble(final String key, final Map<String, Double> value) {
        return callAsyncClient(new AsyncClientRunnable<String>() {
            @Override
            public String call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.hmsetDouble(key, value).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public String hmset(final String key, final Map<String, ?> value) {
        return callAsyncClient(new AsyncClientRunnable<String>() {
            @Override
            public String call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.hmset(key, value).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public List<Integer> hvalsInt(final String key) {
        return callAsyncClient(new AsyncClientRunnable<List<Integer>>() {
            @Override
            public List<Integer> call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.hvalsInt(key).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public List<Long> hvalsLong(final String key) {
        return callAsyncClient(new AsyncClientRunnable<List<Long>>() {
            @Override
            public List<Long> call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.hvalsLong(key).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public List<Double> hvalsDouble(final String key) {
        return callAsyncClient(new AsyncClientRunnable<List<Double>>() {
            @Override
            public List<Double> call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.hvalsDouble(key).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public List<?> hvals(final String key) {
        return callAsyncClient(new AsyncClientRunnable<List<?>>() {
            @Override
            public List<?> call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.hvals(key).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Object lindex(final String key, final int index) {
        return callAsyncClient(new AsyncClientRunnable<Object>() {
            @Override
            public Object call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.lindex(key, index).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long linsertBefore(final String key, final Object before, final Object value) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.linsertBefore(key, before, value).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long linsertAfter(final String key, final Object after, final Object value) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.linsertAfter(key, after, value).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long llen(final String key) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.llen(key).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Object lpop(final String key) {
        return callAsyncClient(new AsyncClientRunnable<Object>() {
            @Override
            public Object call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.lpop(key).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long lpush(final String key, final Object... values) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.lpush(key, values).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long lpushx(final String key, final Object value) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.lpushx(key, value).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public List<?> lrange(final String key, final int start, final int stop) {
        return callAsyncClient(new AsyncClientRunnable<List<?>>() {
            @Override
            public List<?> call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.lrange(key, start, stop).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long lrem(final String key, final int count, final Object value) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.lrem(key, count, value).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public String lset(final String key, final int index, final Object value) {
        return callAsyncClient(new AsyncClientRunnable<String>() {
            @Override
            public String call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.lset(key, index, value).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public String ltrim(final String key, final int start, final int stop) {
        return callAsyncClient(new AsyncClientRunnable<String>() {
            @Override
            public String call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.ltrim(key, start, stop).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Object rpop(final String key) {
        return callAsyncClient(new AsyncClientRunnable<Object>() {
            @Override
            public Object call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.rpop(key).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long rpush(final String key, final Object... values) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.rpush(key, values).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long rpushx(final String key, final Object value) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.rpushx(key, value).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Object rpoplpush(final String source, final String destination) {
        return callAsyncClient(new AsyncClientRunnable<Object>() {
            @Override
            public Object call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.rpoplpush(source, destination).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long sadd(final String key, final Object... values) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.sadd(key, values).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long scard(final String key) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.scard(key).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public List<?> sdiff(final String key, final String... diffs) {
        return callAsyncClient(new AsyncClientRunnable<List<?>>() {
            @Override
            public List<?> call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.sdiff(key, diffs).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long sdiffStore(final String destination, final String key, final String... diffs) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.sdiffStore(destination, key, diffs).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public List<?> sinter(final String key, final String... keys) {
        return callAsyncClient(new AsyncClientRunnable<List<?>>() {
            @Override
            public List<?> call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.sinter(key, keys).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long sinterStore(final String destination, final String key, final String... keys) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.sinterStore(destination, key, keys).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long sisMember(final String key, final Object member) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.sisMember(key, member).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public List<?> smembers(final String key) {
        return callAsyncClient(new AsyncClientRunnable<List<?>>() {
            @Override
            public List<?> call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.smembers(key).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long smove(final String source, final String destination, final Object member) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.smove(source, destination, member).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Object spop(final String key) {
        return callAsyncClient(new AsyncClientRunnable<Object>() {
            @Override
            public Object call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.spop(key).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Object srandomMember(final String key) {
        return callAsyncClient(new AsyncClientRunnable<Object>() {
            @Override
            public Object call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.srandomMember(key).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public List<?> srandomMember(final String key, final int count) {
        return callAsyncClient(new AsyncClientRunnable<List<?>>() {
            @Override
            public List<?> call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.srandomMember(key, count).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long srem(final String key, final Object... members) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.srem(key, members).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public List<?> sunion(final String key, final String... keys) {
        return callAsyncClient(new AsyncClientRunnable<List<?>>() {
            @Override
            public List<?> call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.sunion(key, keys).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long sunionStore(final String destination, final String key, final String... keys) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.sunionStore(destination, key, keys).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long zadd(final String key, final double score, final Object member, final ZEntity... others) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.zadd(key, score, member, others).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long zcard(final String key) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.zcard(key).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long zcount(final String key, final String min, final String max) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.zcount(key, min, max).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Double zincrBy(final String key, final double increment, final Object member) {
        return callAsyncClient(new AsyncClientRunnable<Double>() {
            @Override
            public Double call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.zincrBy(key, increment, member).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long zinterStore(final String destination, final String[] keys, final int[] weights, final ZSetAggregate aggregate) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.zinterStore(destination, keys, weights, aggregate).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long zunionStore(final String destination, final String[] keys, final int[] weights, final ZSetAggregate aggregate) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.zunionStore(destination, keys, weights, aggregate).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public List<?> zrange(final String key, final int start, final int stop) {
        return callAsyncClient(new AsyncClientRunnable<List<?>>() {
            @Override
            public List<?> call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.zrange(key, start, stop).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public List<ZEntity<?>> zrangeWithScores(final String key, final int start, final int stop) {
        return callAsyncClient(new AsyncClientRunnable<List<ZEntity<?>>>() {
            @Override
            public List<ZEntity<?>> call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.zrangeWithScores(key, start, stop).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public List<?> zrangeByScore(final String key, final String min, final String max) {
        return callAsyncClient(new AsyncClientRunnable<List<?>>() {
            @Override
            public List<?> call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.zrangeByScore(key, min, max).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public List<?> zrangeByScore(final String key, final String min, final String max, final int offset, final int count) {
        return callAsyncClient(new AsyncClientRunnable<List<?>>() {
            @Override
            public List<?> call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.zrangeByScore(key, min, max, offset, count).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public List<ZEntity<?>> zrangeByScoreWithScores(final String key, final String min, final String max) {
        return callAsyncClient(new AsyncClientRunnable<List<ZEntity<?>>>() {
            @Override
            public List<ZEntity<?>> call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.zrangeByScoreWithScores(key, min, max).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public List<ZEntity<?>> zrangeByScoreWithScores(final String key, final String min, final String max, final int offset, final int count) {
        return callAsyncClient(new AsyncClientRunnable<List<ZEntity<?>>>() {
            @Override
            public List<ZEntity<?>> call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.zrangeByScoreWithScores(key, min, max, offset, count).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long zrank(final String key, final Object member) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.zrank(key, member).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long zrem(final String key, final Object... members) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.zrem(key, members).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long zremRangeByRank(final String key, final int start, final int stop) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.zremRangeByRank(key, start, stop).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long zremRangeByScore(final String key, final String min, final String max) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.zremRangeByScore(key, min, max).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public List<?> zrevRange(final String key, final int start, final int stop) {
        return callAsyncClient(new AsyncClientRunnable<List<?>>() {
            @Override
            public List<?> call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.zrevRange(key, start, stop).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public List<ZEntity<?>> zrevRangeWithScores(final String key, final int start, final int stop) {
        return callAsyncClient(new AsyncClientRunnable<List<ZEntity<?>>>() {
            @Override
            public List<ZEntity<?>> call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.zrevRangeWithScores(key, start, stop).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public List<?> zrevRangeByScore(final String key, final String max, final String min) {
        return callAsyncClient(new AsyncClientRunnable<List<?>>() {
            @Override
            public List<?> call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.zrevRangeByScore(key, max, min).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public List<?> zrevRangeByScore(final String key, final String max, final String min, final int offset, final int count) {
        return callAsyncClient(new AsyncClientRunnable<List<?>>() {
            @Override
            public List<?> call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.zrevRangeByScore(key, max, min, offset, count).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public List<ZEntity<?>> zrevRangeByScoreWithScores(final String key, final String max, final String min) {
        return callAsyncClient(new AsyncClientRunnable<List<ZEntity<?>>>() {
            @Override
            public List<ZEntity<?>> call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.zrevRangeByScoreWithScores(key, max, min).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public List<ZEntity<?>> zrevRangeByScoreWithScores(final String key, final String max, final String min, final int offset, final int count) {
        return callAsyncClient(new AsyncClientRunnable<List<ZEntity<?>>>() {
            @Override
            public List<ZEntity<?>> call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.zrevRangeByScoreWithScores(key, max, min, offset, count).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long zrevRank(final String key, final Object member) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.zrevRank(key, member).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Double zscore(final String key, final Object member) {
        return callAsyncClient(new AsyncClientRunnable<Double>() {
            @Override
            public Double call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.zscore(key, member).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Reply eval(final String script, final String[] keys, final byte[]... args) {
        return callAsyncClient(new AsyncClientRunnable<Reply>() {
            @Override
            public Reply call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.eval(script, keys, args).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Reply evalSha(final String sha1, final String[] keys, final byte[]... args) {
        return callAsyncClient(new AsyncClientRunnable<Reply>() {
            @Override
            public Reply call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.evalSha(sha1, keys, args).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public String scriptLoad(final String script) {
        return callAsyncClient(new AsyncClientRunnable<String>() {
            @Override
            public String call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.scriptLoad(script).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long scriptExists(final String script) {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.scriptExists(script).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public List<Integer> scriptExists(final String[] scripts) {
        return callAsyncClient(new AsyncClientRunnable<List<Integer>>() {
            @Override
            public List<Integer> call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.scriptExists(scripts).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public String scriptFlush() {
        return callAsyncClient(new AsyncClientRunnable<String>() {
            @Override
            public String call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.scriptFlush().get(timeout, timeUnit);
            }
        });
    }

    @Override
    public String scriptKill() {
        return callAsyncClient(new AsyncClientRunnable<String>() {
            @Override
            public String call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.scriptKill().get(timeout, timeUnit);
            }
        });
    }

    @Override
    public String bgRewriteAOF() {
        return callAsyncClient(new AsyncClientRunnable<String>() {
            @Override
            public String call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.bgRewriteAOF().get(timeout, timeUnit);
            }
        });
    }

    @Override
    public String bgSave() {
        return callAsyncClient(new AsyncClientRunnable<String>() {
            @Override
            public String call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.bgSave().get(timeout, timeUnit);
            }
        });
    }

    @Override
    public String clientKill(final String ip, final int port) {
        return callAsyncClient(new AsyncClientRunnable<String>() {
            @Override
            public String call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.clientKill(ip, port).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public String clientList() {
        return callAsyncClient(new AsyncClientRunnable<String>() {
            @Override
            public String call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.clientList().get(timeout, timeUnit);
            }
        });
    }

    @Override
    public String configGet(final String parameter) {
        return callAsyncClient(new AsyncClientRunnable<String>() {
            @Override
            public String call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.configGet(parameter).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public String configResetStat() {
        return callAsyncClient(new AsyncClientRunnable<String>() {
            @Override
            public String call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.configResetStat().get(timeout, timeUnit);
            }
        });
    }

    @Override
    public String configSet(final String config, final String parameter) {
        return callAsyncClient(new AsyncClientRunnable<String>() {
            @Override
            public String call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.configSet(config, parameter).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long dbSize() {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.dbSize().get(timeout, timeUnit);
            }
        });
    }

    @Override
    public String flushAll() {
        return callAsyncClient(new AsyncClientRunnable<String>() {
            @Override
            public String call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.flushAll().get(timeout, timeUnit);
            }
        });
    }

    @Override
    public String flushDB() {
        return callAsyncClient(new AsyncClientRunnable<String>() {
            @Override
            public String call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.flushDB().get(timeout, timeUnit);
            }
        });
    }

    @Override
    public String info() {
        return callAsyncClient(new AsyncClientRunnable<String>() {
            @Override
            public String call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.info().get(timeout, timeUnit);
            }
        });
    }

    @Override
    public Long lastSave() {
        return callAsyncClient(new AsyncClientRunnable<Long>() {
            @Override
            public Long call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.lastSave().get(timeout, timeUnit);
            }
        });
    }

    @Override
    public String save() {
        return callAsyncClient(new AsyncClientRunnable<String>() {
            @Override
            public String call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.save().get(timeout, timeUnit);
            }
        });
    }

    @Override
    public String shutdown(final boolean save) {
        return callAsyncClient(new AsyncClientRunnable<String>() {
            @Override
            public String call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.shutdown(save).get(timeout, timeUnit);
            }
        });
    }

    @Override
    public String slaveOf(final String host, final int port) {
        return callAsyncClient(new AsyncClientRunnable<String>() {
            @Override
            public String call() throws InterruptedException, ExecutionException, TimeoutException {
                return asyncClient.slaveOf(host, port).get(timeout, timeUnit);
            }
        });
    }
}
