package mobi.app.redis.netty;

import mobi.app.redis.AsyncRedisClient;
import mobi.app.redis.ZEntity;
import mobi.app.redis.ZSetAggregate;
import mobi.app.redis.netty.command.Command;
import mobi.app.redis.netty.command.Commands;
import mobi.app.redis.netty.reply.Reply;
import mobi.app.redis.transcoders.Transcoder;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * User: thor
 * Date: 12-12-19
 * Time: 上午10:37
 */
public class NettyRedisClient extends SimpleChannelHandler implements AsyncRedisClient {
    Logger logger = LoggerFactory.getLogger(NettyRedisClient.class);

    final static int DEFAULT_TIMEOUT = 5;
    final String address;
    final int db;
    final String password;
    final String host;
    final int port;

    NioClientSocketChannelFactory nioClientSocketChannelFactory;
    Channel channel;
    ClientBootstrap bootstrap;
    ChannelPipeline pipeline;
    Timer timer;

    public NettyRedisClient(String address, int db, String password) {
        this.address = address;
        this.db = db;
        this.password = password;
        String[] parts = address.split(":");
        host = parts[0];
        port = Integer.parseInt(parts[1]);
        init();

    }


    protected void connect() {
        if (nioClientSocketChannelFactory == null) {
            nioClientSocketChannelFactory = new NioClientSocketChannelFactory(
                    Executors.newCachedThreadPool(),
                    Executors.newCachedThreadPool());
        }
        if (bootstrap == null) {
            bootstrap = new ClientBootstrap(nioClientSocketChannelFactory);
        }
        if (pipeline == null) {
            timer = new HashedWheelTimer();
            pipeline = Channels.pipeline(new ReconnectHandler(this, timer), new ReplyDecoder(), this);
        }
        bootstrap.setPipeline(pipeline);
        ChannelFuture connectFuture = bootstrap.connect(new InetSocketAddress(host, port));
        connectFuture.awaitUninterruptibly(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        channel = connectFuture.getChannel();
    }

    protected void init() {
        connect();
        if (password != null) {
            logger.info("try auth command:");
            auth(password);
            try {
                String authReply = auth(password).get(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
                logger.info("auth db reply: {}", authReply);
            } catch (InterruptedException e) {
                throw new RedisException(e);
            } catch (ExecutionException e) {
                throw new RedisException(e);
            } catch (TimeoutException e) {
                throw new RedisException(e);
            }

        }
        if (db > 0) {
            logger.debug("try to select db: {} ", db);
            try {
                String selectReply = select(db).get(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
                logger.info("select db reply: {}", selectReply);
            } catch (InterruptedException e) {
                throw new RedisException(e);
            } catch (ExecutionException e) {
                throw new RedisException(e);
            } catch (TimeoutException e) {
                throw new RedisException(e);
            }
        }
    }

    public void close() throws InterruptedException {
        channel.close().await(500);
        timer.stop();
        nioClientSocketChannelFactory.releaseExternalResources();
    }


    private Future sendCommand(Commands commands, Transcoder transcoder, Object... args) {
        @SuppressWarnings("unchecked") Command command = commands.getCommand(transcoder, args);
        channel.write(command);
        return command.getReply();
    }


    @Override
    public Future<String> auth(String password) {
        //noinspection unchecked
        return sendCommand(Commands.AUTH, null, password);
    }


    @Override
    public Future<String> echo(String message) {
        //noinspection unchecked
        return sendCommand(Commands.ECHO, null, message);
    }

    @Override
    public Future<String> ping() {
        //noinspection unchecked
        return sendCommand(Commands.PING, null);
    }

    @Override
    public Future<String> select(int db) {
        //noinspection unchecked
        return sendCommand(Commands.SELECT, null, db);
    }

    @Override
    public Future<String> quit() {
        //noinspection unchecked
        return sendCommand(Commands.QUIT, null);
    }

    @Override
    public Future<Long> delete(String key) {
        return delete(new String[]{key});
    }

    @Override
    public Future<Long> delete(String... key) {
        //noinspection unchecked
        return sendCommand(Commands.DEL, null, key);
    }

    @Override
    public Future<byte[]> dump(String key) {
        //noinspection unchecked
        return sendCommand(Commands.DUMP, Transcoder.BYTES_TRANSCODER, key);
    }

    @Override
    public Future<Long> exists(String key) {
        //noinspection unchecked
        return sendCommand(Commands.EXISTS, null, key);
    }

    @Override
    public Future<Long> expire(String key, int seconds) {
        //noinspection unchecked
        return sendCommand(Commands.EXPIRE, null, key, seconds);
    }

    @Override
    public Future<Long> expireAt(String key, int timestamp) {
        //noinspection unchecked
        return sendCommand(Commands.EXPIREAT, null, key, timestamp);
    }

    @Override
    public Future<Long> ttl(String key) {
        //noinspection unchecked
        return sendCommand(Commands.TTL, null, key);
    }

    @Override
    public Future<List<String>> keys(String pattern) {
        //noinspection unchecked
        return sendCommand(Commands.KEYS, Transcoder.STRING_TRANSCODER, pattern);
    }

    @Override
    public Future<String> migrate(String host, int port, String key, int db, int timeOut) {
        //noinspection unchecked
        return sendCommand(Commands.MIGRATE, Transcoder.STRING_TRANSCODER, host, port, key, db, timeOut);
    }

    @Override
    public Future<Long> move(String key, int db) {
        //noinspection unchecked
        return sendCommand(Commands.MOVE, Transcoder.LONG_TRANSCODER, key, db);
    }

    @Override
    public Future<Long> presist(String key) {
        //noinspection unchecked
        return sendCommand(Commands.PERSIST, Transcoder.LONG_TRANSCODER, key);
    }

    @Override
    public Future<Long> pexpire(String key, long milliseconds) {
        //noinspection unchecked
        return sendCommand(Commands.PEXPIRE, null, key, milliseconds);
    }

    @Override
    public Future<Long> pexpireAt(String key, long timestamp) {
        //noinspection unchecked
        return sendCommand(Commands.PEXPIREAT, null, key, timestamp);
    }

    @Override
    public Future<Long> pttl(String key) {
        //noinspection unchecked
        return sendCommand(Commands.PTTL, null, key);
    }

    @Override
    public Future<String> randomKey() {
        //noinspection unchecked
        return sendCommand(Commands.RANDOMKEY, Transcoder.STRING_TRANSCODER);
    }

    @Override
    public Future<String> rename(String key, String newKey) {
        //noinspection unchecked
        return sendCommand(Commands.RENAME, Transcoder.STRING_TRANSCODER, key, newKey);
    }

    @Override
    public Future<Long> renameNx(String key, String newKey) {
        //noinspection unchecked
        return sendCommand(Commands.RENAMENX, Transcoder.LONG_TRANSCODER, key, newKey);
    }

    @Override
    public Future<String> restore(String key, int expire, byte[] v) {
        //noinspection unchecked
        return sendCommand(Commands.RESTORE, null, key, expire, v);
    }

    @Override
    public Future<String> type(String key) {
        //noinspection unchecked
        return sendCommand(Commands.TYPE, Transcoder.STRING_TRANSCODER, key);
    }

    @Override
    public Future<String> set(String key, int o) {
        //noinspection unchecked
        return sendCommand(Commands.SET, Transcoder.INTEGER_TRANSCODER, key, o);
    }

    @Override
    public Future<String> set(String key, long o) {
        //noinspection unchecked
        return sendCommand(Commands.SET, Transcoder.LONG_TRANSCODER, key, o);
    }

    @Override
    public Future<String> set(String key, double o) {
        //noinspection unchecked
        return sendCommand(Commands.SET, Transcoder.DOUBLE_TRANSCODER, key, o);
    }

    @Override
    public Future<String> set(String key, Object o) {
        //noinspection unchecked
        return sendCommand(Commands.SET, Transcoder.SERIALIZING_TRANSCODER, key, o);
    }

    @Override
    public Future<Long> setNx(String key, int o) {
        //noinspection unchecked
        return sendCommand(Commands.SETNX, Transcoder.INTEGER_TRANSCODER, key, o);
    }

    @Override
    public Future<Long> setNx(String key, long o) {
        //noinspection unchecked
        return sendCommand(Commands.SETNX, Transcoder.LONG_TRANSCODER, key, o);
    }

    @Override
    public Future<Long> setNx(String key, double o) {
        //noinspection unchecked
        return sendCommand(Commands.SETNX, Transcoder.DOUBLE_TRANSCODER, key, o);
    }

    @Override
    public Future<Long> setNx(String key, Object o) {
        //noinspection unchecked
        return sendCommand(Commands.SETNX, Transcoder.SERIALIZING_TRANSCODER, key, o);
    }

    @Override
    public Future<Long> msetIntNx(Map<String, Integer> value) {
        //noinspection unchecked
        return sendCommand(Commands.MSETNX, Transcoder.INTEGER_TRANSCODER, value);
    }

    @Override
    public Future<Long> msetLongNx(Map<String, Long> value) {
        //noinspection unchecked
        return sendCommand(Commands.MSETNX, Transcoder.LONG_TRANSCODER, value);
    }

    @Override
    public Future<Long> msetDoubleNx(Map<String, Double> value) {
        //noinspection unchecked
        return sendCommand(Commands.MSETNX, Transcoder.DOUBLE_TRANSCODER, value);
    }

    @Override
    public Future<Long> msetObjectNx(Map<String, ?> value) {
        //noinspection unchecked
        return sendCommand(Commands.MSETNX, Transcoder.SERIALIZING_TRANSCODER, value);
    }

    @Override
    public Future<String> setEx(String key, int o, int seconds) {
        //noinspection unchecked
        return sendCommand(Commands.SETEX, Transcoder.INTEGER_TRANSCODER, key, seconds, o);
    }

    @Override
    public Future<String> setEx(String key, long o, int seconds) {
        //noinspection unchecked
        return sendCommand(Commands.SETEX, Transcoder.LONG_TRANSCODER, key, seconds, o);
    }

    @Override
    public Future<String> setEx(String key, double o, int seconds) {
        //noinspection unchecked
        return sendCommand(Commands.SETEX, Transcoder.DOUBLE_TRANSCODER, key, seconds, o);
    }

    @Override
    public Future<String> setEx(String key, Object o, int seconds) {
        //noinspection unchecked
        return sendCommand(Commands.SETEX, Transcoder.SERIALIZING_TRANSCODER, key, seconds, o);
    }

    @Override
    public Future<String> msetInt(Map<String, Integer> value) {
        //noinspection unchecked
        return sendCommand(Commands.MSET, Transcoder.INTEGER_TRANSCODER, value);
    }

    @Override
    public Future<String> msetLong(Map<String, Long> value) {
        //noinspection unchecked
        return sendCommand(Commands.MSET, Transcoder.LONG_TRANSCODER, value);
    }

    @Override
    public Future<String> msetDouble(Map<String, Double> value) {
        //noinspection unchecked
        return sendCommand(Commands.MSET, Transcoder.DOUBLE_TRANSCODER, value);
    }

    @Override
    public Future<String> msetObject(Map<String, ?> value) {
        //noinspection unchecked
        return sendCommand(Commands.MSET, Transcoder.SERIALIZING_TRANSCODER, value);
    }

    @Override
    public Future<Object> get(String key) {
        //noinspection unchecked
        return sendCommand(Commands.GET, Transcoder.SERIALIZING_TRANSCODER, key);
    }

    @Override
    public Future<Integer> getInt(String key) {
        //noinspection unchecked
        return sendCommand(Commands.GET, Transcoder.INTEGER_TRANSCODER, key);
    }

    @Override
    public Future<Long> getLong(String key) {
        //noinspection unchecked
        return sendCommand(Commands.GET, Transcoder.LONG_TRANSCODER, key);
    }

    @Override
    public Future<Double> getDouble(String key) {
        //noinspection unchecked
        return sendCommand(Commands.GET, Transcoder.DOUBLE_TRANSCODER, key);
    }

    @Override
    public Future<List<Object>> mget(String[] key) {
        //noinspection unchecked
        return sendCommand(Commands.MGET, Transcoder.SERIALIZING_TRANSCODER, key);
    }

    @Override
    public Future<List<Integer>> mgetInt(String[] key) {
        //noinspection unchecked
        return sendCommand(Commands.MGET, Transcoder.INTEGER_TRANSCODER, key);
    }

    @Override
    public Future<List<Long>> mgetLong(String[] key) {
        //noinspection unchecked
        return sendCommand(Commands.MGET, Transcoder.LONG_TRANSCODER, key);
    }

    @Override
    public Future<List<Double>> mgetDouble(String[] key) {
        //noinspection unchecked
        return sendCommand(Commands.MGET, Transcoder.DOUBLE_TRANSCODER, key);
    }

    @Override
    public Future<Long> decr(String key) {
        //noinspection unchecked
        return sendCommand(Commands.DECR, Transcoder.LONG_TRANSCODER, key);
    }

    @Override
    public Future<Long> decrBy(String key, int decrement) {
        //noinspection unchecked
        return sendCommand(Commands.DECRBY, Transcoder.LONG_TRANSCODER, key, decrement);
    }

    @Override
    public Future<Long> incr(String key) {
        //noinspection unchecked
        return sendCommand(Commands.INCR, Transcoder.LONG_TRANSCODER, key);
    }

    @Override
    public Future<Long> incrBy(String key, int decrement) {
        //noinspection unchecked
        return sendCommand(Commands.INCRBY, Transcoder.LONG_TRANSCODER, key, decrement);
    }

    @Override
    public Future<Integer> getAndSet(String key, int v) {
        //noinspection unchecked
        return sendCommand(Commands.GETSET, Transcoder.INTEGER_TRANSCODER, key, v);
    }

    @Override
    public Future<Long> getAndSet(String key, long v) {
        //noinspection unchecked
        return sendCommand(Commands.GETSET, Transcoder.LONG_TRANSCODER, key, v);
    }

    @Override
    public Future<Double> getAndSet(String key, double v) {
        //noinspection unchecked
        return sendCommand(Commands.GETSET, Transcoder.DOUBLE_TRANSCODER, key, v);
    }

    @Override
    public Future<Object> getAndSet(String key, Object v) {
        //noinspection unchecked
        return sendCommand(Commands.GETSET, Transcoder.SERIALIZING_TRANSCODER, key, v);
    }

    @Override
    public Future<Long> hdel(String key, String... fields) {
        //noinspection unchecked
        return sendCommand(Commands.HDEL, Transcoder.LONG_TRANSCODER, key, fields);
    }

    @Override
    public Future<Long> hexists(String key, String field) {
        //noinspection unchecked
        return sendCommand(Commands.HEXISTS, Transcoder.LONG_TRANSCODER, key, field);
    }

    @Override
    public Future<Long> hset(String key, String field, int o) {
        //noinspection unchecked
        return sendCommand(Commands.HSET, Transcoder.INTEGER_TRANSCODER, key, field, o);
    }

    @Override
    public Future<Long> hset(String key, String field, long o) {
        //noinspection unchecked
        return sendCommand(Commands.HSET, Transcoder.LONG_TRANSCODER, key, field, o);
    }

    @Override
    public Future<Long> hset(String key, String field, double o) {
        //noinspection unchecked
        return sendCommand(Commands.HSET, Transcoder.DOUBLE_TRANSCODER, key, field, o);
    }

    @Override
    public Future<Long> hset(String key, String field, Object o) {
        //noinspection unchecked
        return sendCommand(Commands.HSET, Transcoder.SERIALIZING_TRANSCODER, key, field, o);
    }

    @Override
    public Future<Long> hsetNx(String key, String field, int o) {
        //noinspection unchecked
        return sendCommand(Commands.HSETNX, Transcoder.INTEGER_TRANSCODER, key, field, o);
    }

    @Override
    public Future<Long> hsetNx(String key, String field, long o) {
        //noinspection unchecked
        return sendCommand(Commands.HSETNX, Transcoder.LONG_TRANSCODER, key, field, o);
    }

    @Override
    public Future<Long> hsetNx(String key, String field, double o) {
        //noinspection unchecked
        return sendCommand(Commands.HSETNX, Transcoder.DOUBLE_TRANSCODER, key, field, o);
    }

    @Override
    public Future<Long> hsetNx(String key, String field, Object o) {
        //noinspection unchecked
        return sendCommand(Commands.HSETNX, Transcoder.SERIALIZING_TRANSCODER, key, field, o);
    }

    @Override
    public Future<Object> hget(String key, String field) {
        //noinspection unchecked
        return sendCommand(Commands.HGET, Transcoder.SERIALIZING_TRANSCODER, key, field);
    }

    @Override
    public Future<Integer> hgetInt(String key, String field) {
        //noinspection unchecked
        return sendCommand(Commands.HGET, Transcoder.INTEGER_TRANSCODER, key, field);
    }

    @Override
    public Future<Long> hgetLong(String key, String field) {
        //noinspection unchecked
        return sendCommand(Commands.HGET, Transcoder.LONG_TRANSCODER, key, field);
    }

    @Override
    public Future<Double> hgetDouble(String key, String field) {
        //noinspection unchecked
        return sendCommand(Commands.HGET, Transcoder.DOUBLE_TRANSCODER, key, field);
    }

    @Override
    public Future<Map<String, Integer>> hgetAllInt(String key) {
        //noinspection unchecked
        return sendCommand(Commands.HGETALL, Transcoder.INTEGER_TRANSCODER, key);
    }

    @Override
    public Future<Map<String, Long>> hgetAllLong(String key) {
        //noinspection unchecked
        return sendCommand(Commands.HGETALL, Transcoder.LONG_TRANSCODER, key);
    }

    @Override
    public Future<Map<String, Double>> hgetAllDouble(String key) {
        //noinspection unchecked
        return sendCommand(Commands.HGETALL, Transcoder.DOUBLE_TRANSCODER, key);
    }

    @Override
    public Future<Map<String, ?>> hgetAll(String key) {
        //noinspection unchecked
        return sendCommand(Commands.HGETALL, Transcoder.SERIALIZING_TRANSCODER, key);
    }

    @Override
    public Future<Long> hincrBy(String key, String field, int decrement) {
        //noinspection unchecked
        return sendCommand(Commands.HINCRBY, Transcoder.LONG_TRANSCODER, key, field, decrement);
    }

    @Override
    public Future<List<String>> hkeys(String key) {
        //noinspection unchecked
        return sendCommand(Commands.HKEYS, Transcoder.STRING_TRANSCODER, key);
    }

    @Override
    public Future<Long> hlen(String key) {
        //noinspection unchecked
        return sendCommand(Commands.HLEN, Transcoder.LONG_TRANSCODER, key);
    }

    @Override
    public Future<List<?>> hmget(String key, String[] fields) {
        //noinspection unchecked
        return sendCommand(Commands.HMGET, Transcoder.SERIALIZING_TRANSCODER, key, fields);
    }

    @Override
    public Future<List<Integer>> hmgetInt(String key, String[] fields) {
        //noinspection unchecked
        return sendCommand(Commands.HMGET, Transcoder.INTEGER_TRANSCODER, key, fields);
    }

    @Override
    public Future<List<Long>> hmgetLong(String key, String[] fields) {
        //noinspection unchecked
        return sendCommand(Commands.HMGET, Transcoder.LONG_TRANSCODER, key, fields);
    }

    @Override
    public Future<List<Double>> hmgetDouble(String key, String[] fields) {
        //noinspection unchecked
        return sendCommand(Commands.HMGET, Transcoder.DOUBLE_TRANSCODER, key, fields);
    }

    @Override
    public Future<String> hmsetInt(String key, Map<String, Integer> value) {
        //noinspection unchecked
        return sendCommand(Commands.HMSET, Transcoder.INTEGER_TRANSCODER, key, value);
    }

    @Override
    public Future<String> hmsetLong(String key, Map<String, Long> value) {
        //noinspection unchecked
        return sendCommand(Commands.HMSET, Transcoder.LONG_TRANSCODER, key, value);
    }

    @Override
    public Future<String> hmsetDouble(String key, Map<String, Double> value) {
        //noinspection unchecked
        return sendCommand(Commands.HMSET, Transcoder.DOUBLE_TRANSCODER, key, value);
    }

    @Override
    public Future<String> hmset(String key, Map<String, ?> value) {
        //noinspection unchecked
        return sendCommand(Commands.HMSET, Transcoder.SERIALIZING_TRANSCODER, key, value);
    }

    @Override
    public Future<List<Integer>> hvalsInt(String key) {
        //noinspection unchecked
        return sendCommand(Commands.HVALS, Transcoder.INTEGER_TRANSCODER, key);
    }

    @Override
    public Future<List<Long>> hvalsLong(String key) {
        //noinspection unchecked
        return sendCommand(Commands.HVALS, Transcoder.LONG_TRANSCODER, key);
    }

    @Override
    public Future<List<Double>> hvalsDouble(String key) {
        //noinspection unchecked
        return sendCommand(Commands.HVALS, Transcoder.DOUBLE_TRANSCODER, key);
    }

    @Override
    public Future<List<?>> hvals(String key) {
        //noinspection unchecked
        return sendCommand(Commands.HVALS, Transcoder.SERIALIZING_TRANSCODER, key);
    }

    @Override
    public Future<Object> lindex(String key, int index) {
        //noinspection unchecked
        return sendCommand(Commands.LINDEX, Transcoder.SERIALIZING_TRANSCODER, key, index);
    }

    @Override
    public Future<Long> linsertBefore(String key, Object before, Object value) {
        //noinspection unchecked
        return sendCommand(Commands.LINSERT, Transcoder.SERIALIZING_TRANSCODER, key, "BEFORE", before, value);
    }

    @Override
    public Future<Long> linsertAfter(String key, Object after, Object value) {
        //noinspection unchecked
        return sendCommand(Commands.LINSERT, Transcoder.SERIALIZING_TRANSCODER, key, "AFTER", after, value);
    }

    @Override
    public Future<Long> llen(String key) {
        //noinspection unchecked
        return sendCommand(Commands.LLEN, Transcoder.LONG_TRANSCODER, key);
    }

    @Override
    public Future<Object> lpop(String key) {
        //noinspection unchecked
        return sendCommand(Commands.LPOP, Transcoder.SERIALIZING_TRANSCODER, key);
    }

    @Override
    public Future<Long> lpush(String key, Object... values) {
        assert values.length > 0;
        //noinspection unchecked
        return sendCommand(Commands.LPUSH, Transcoder.SERIALIZING_TRANSCODER, key, values);
    }

    @Override
    public Future<Long> lpushx(String key, Object value) {
        //noinspection unchecked
        return sendCommand(Commands.LPUSHX, Transcoder.SERIALIZING_TRANSCODER, key, value);
    }

    @Override
    public Future<List<?>> lrange(String key, int start, int stop) {
        //noinspection unchecked
        return sendCommand(Commands.LRANGE, Transcoder.SERIALIZING_TRANSCODER, key, start, stop);
    }

    @Override
    public Future<Long> lrem(String key, int count, Object value) {
        //noinspection unchecked
        return sendCommand(Commands.LREM, Transcoder.SERIALIZING_TRANSCODER, key, count, value);
    }

    @Override
    public Future<String> lset(String key, int index, Object value) {
        //noinspection unchecked
        return sendCommand(Commands.LSET, Transcoder.SERIALIZING_TRANSCODER, key, index, value);
    }

    @Override
    public Future<String> ltrim(String key, int start, int stop) {
        //noinspection unchecked
        return sendCommand(Commands.LTRIM, Transcoder.SERIALIZING_TRANSCODER, key, start, stop);
    }

    @Override
    public Future<Object> rpop(String key) {
        //noinspection unchecked
        return sendCommand(Commands.RPOP, Transcoder.SERIALIZING_TRANSCODER, key);
    }

    @Override
    public Future<Long> rpush(String key, Object... values) {
        assert values.length > 0;
        //noinspection unchecked
        return sendCommand(Commands.RPUSH, Transcoder.SERIALIZING_TRANSCODER, key, values);
    }

    @Override
    public Future<Long> rpushx(String key, Object value) {
        //noinspection unchecked
        return sendCommand(Commands.RPUSHX, Transcoder.SERIALIZING_TRANSCODER, key, value);
    }

    @Override
    public Future<Object> rpoplpush(String source, String destination) {
        //noinspection unchecked
        return sendCommand(Commands.RPOPLPUSH, Transcoder.SERIALIZING_TRANSCODER, source, destination);
    }

    @Override
    public Future<Long> sadd(String key, Object... values) {
        assert values.length > 0;
        //noinspection unchecked
        return sendCommand(Commands.SADD, Transcoder.SERIALIZING_TRANSCODER, key, values);
    }

    @Override
    public Future<Long> scard(String key) {
        //noinspection unchecked
        return sendCommand(Commands.SCARD, Transcoder.LONG_TRANSCODER, key);
    }

    @Override
    public Future<List<?>> sdiff(String key, String... diffs) {
        assert diffs.length > 0;
        //noinspection unchecked
        return sendCommand(Commands.SDIFF, Transcoder.SERIALIZING_TRANSCODER, key, diffs);
    }

    @Override
    public Future<Long> sdiffStore(String destination, String key, String... diffs) {
        assert diffs.length > 0;
        //noinspection unchecked
        return sendCommand(Commands.SDIFFSTORE, Transcoder.LONG_TRANSCODER, destination, key, diffs);
    }

    @Override
    public Future<List<?>> sinter(String key, String... keys) {
        assert keys.length > 0;
        //noinspection unchecked
        return sendCommand(Commands.SINTER, Transcoder.SERIALIZING_TRANSCODER, key, keys);
    }

    @Override
    public Future<Long> sinterStore(String destination, String key, String... keys) {
        assert keys.length > 0;
        //noinspection unchecked
        return sendCommand(Commands.SINTERSTORE, Transcoder.LONG_TRANSCODER, destination, key, keys);
    }

    @Override
    public Future<Long> sisMember(String key, Object member) {
        //noinspection unchecked
        return sendCommand(Commands.SISMEMBER, Transcoder.SERIALIZING_TRANSCODER, key, member);
    }

    @Override
    public Future<List<?>> smembers(String key) {
        //noinspection unchecked
        return sendCommand(Commands.SMEMBERS, Transcoder.SERIALIZING_TRANSCODER, key);
    }

    @Override
    public Future<Long> smove(String source, String destination, Object member) {
        //noinspection unchecked
        return sendCommand(Commands.SMOVE, Transcoder.SERIALIZING_TRANSCODER, source, destination, member);
    }

    @Override
    public Future<?> spop(String key) {
        //noinspection unchecked
        return sendCommand(Commands.SPOP, Transcoder.SERIALIZING_TRANSCODER, key);
    }

    @Override
    public Future<?> srandomMember(String key) {
        //noinspection unchecked
        return sendCommand(Commands.SRANDMEMBER, Transcoder.SERIALIZING_TRANSCODER, key);
    }

    @Override
    public Future<List<?>> srandomMember(String key, int count) {
        assert count > 1;
        //noinspection unchecked
        return sendCommand(Commands.SRANDMEMBER, Transcoder.SERIALIZING_TRANSCODER, key, count);
    }

    @Override
    public Future<Long> srem(String key, Object... members) {
        //noinspection unchecked
        return sendCommand(Commands.SREM, Transcoder.SERIALIZING_TRANSCODER, key, members);
    }

    @Override
    public Future<List<?>> sunion(String key, String... keys) {
        assert keys.length > 0;
        //noinspection unchecked
        return sendCommand(Commands.SUNION, Transcoder.SERIALIZING_TRANSCODER, key, keys);
    }

    @Override
    public Future<Long> sunionStore(String destination, String key, String... keys) {
        assert keys.length > 0;
        //noinspection unchecked
        return sendCommand(Commands.SUNIONSTORE, Transcoder.LONG_TRANSCODER, destination, key, keys);
    }

    //sorted sets
    @Override
    public Future<Long> zadd(String key, double score, Object member, ZEntity... others) {
        //noinspection unchecked
        return sendCommand(Commands.ZADD, Transcoder.SERIALIZING_TRANSCODER, key, score, member, others);
    }

    @Override
    public Future<Long> zcard(String key) {
        //noinspection unchecked
        return sendCommand(Commands.ZCARD, Transcoder.LONG_TRANSCODER, key);
    }

    @Override
    public Future<Long> zcount(String key, String min, String max) {
        //noinspection unchecked
        return sendCommand(Commands.ZCOUNT, Transcoder.LONG_TRANSCODER, key, min, max);
    }

    @Override
    public Future<Double> zincrBy(String key, double increment, Object member) {
        //noinspection unchecked
        return sendCommand(Commands.ZINCRBY, Transcoder.SERIALIZING_TRANSCODER, key, increment, member);
    }

    @Override
    public Future<Long> zinterStore(String destination, String[] keys, int[] weights, ZSetAggregate aggregate) {
        if (weights != null) assert keys.length == weights.length;
        if (aggregate == null) aggregate = ZSetAggregate.SUM;
        //noinspection unchecked
        return sendCommand(Commands.ZINTERSTORE, Transcoder.LONG_TRANSCODER, destination, keys.length, keys, weights, aggregate.name());
    }

    @Override
    public Future<Long> zunionStore(String destination, String[] keys, int[] weights, ZSetAggregate aggregate) {
        if (weights != null) assert keys.length == weights.length;
        if (aggregate == null) aggregate = ZSetAggregate.SUM;
        //noinspection unchecked
        return sendCommand(Commands.ZUNIONSTORE, Transcoder.LONG_TRANSCODER, destination, keys.length, keys, weights, aggregate.name());

    }

    @Override
    public Future<List<?>> zrange(String key, int start, int stop) {
        //noinspection unchecked
        return sendCommand(Commands.ZRANGE, Transcoder.SERIALIZING_TRANSCODER, key, start, stop);
    }

    @Override
    public Future<List<ZEntity<?>>> zrangeWithScores(String key, int start, int stop) {
        //noinspection unchecked
        return sendCommand(Commands.ZRANGE, Transcoder.SERIALIZING_TRANSCODER, key, start, stop, "WITHSCORES");
    }

    @Override
    public Future<List<?>> zrangeByScore(String key, String min, String max) {
        //noinspection unchecked
        return sendCommand(Commands.ZRANGEBYSCORE, Transcoder.SERIALIZING_TRANSCODER, key, min, max, null);
    }

    @Override
    public Future<List<?>> zrangeByScore(String key, String min, String max, int offset, int count) {
        //noinspection unchecked
        return sendCommand(Commands.ZRANGEBYSCORE, Transcoder.SERIALIZING_TRANSCODER, key, min, max, null, offset, count);
    }

    @Override
    public Future<List<ZEntity<?>>> zrangeByScoreWithScores(String key, String min, String max) {
        //noinspection unchecked
        return sendCommand(Commands.ZRANGEBYSCORE, Transcoder.SERIALIZING_TRANSCODER, key, min, max, "WITHSCORES");
    }

    @Override
    public Future<List<ZEntity<?>>> zrangeByScoreWithScores(String key, String min, String max, int offset, int count) {
        //noinspection unchecked
        return sendCommand(Commands.ZRANGEBYSCORE, Transcoder.SERIALIZING_TRANSCODER, key, min, max, "WITHSCORES", offset, count);
    }

    @Override
    public Future<Long> zrank(String key, Object member) {
        //noinspection unchecked
        return sendCommand(Commands.ZRANK, Transcoder.SERIALIZING_TRANSCODER, key, member);
    }

    @Override
    public Future<Long> zrem(String key, Object... members) {
        //noinspection unchecked
        return sendCommand(Commands.ZREM, Transcoder.SERIALIZING_TRANSCODER, key, members);
    }

    @Override
    public Future<Long> zremRangeByRank(String key, int start, int stop) {
        //noinspection unchecked
        return sendCommand(Commands.ZREMRANGEBYRANK, Transcoder.LONG_TRANSCODER, key, start, stop);
    }

    @Override
    public Future<Long> zremRangeByScore(String key, String min, String max) {
        //noinspection unchecked
        return sendCommand(Commands.ZREMRANGEBYSCORE, Transcoder.LONG_TRANSCODER, key, min, max);
    }

    @Override
    public Future<List<?>> zrevRange(String key, int start, int stop) {
        //noinspection unchecked
        return sendCommand(Commands.ZREVRANGE, Transcoder.SERIALIZING_TRANSCODER, key, start, stop);
    }

    @Override
    public Future<List<ZEntity<?>>> zrevRangeWithScores(String key, int start, int stop) {
        //noinspection unchecked
        return sendCommand(Commands.ZREVRANGE, Transcoder.SERIALIZING_TRANSCODER, key, start, stop, "WITHSCORES");
    }

    @Override
    public Future<List<?>> zrevRangeByScore(String key, String max, String min) {
        //noinspection unchecked
        return sendCommand(Commands.ZREVRANGEBYSCORE, Transcoder.SERIALIZING_TRANSCODER, key, max, min, null);
    }

    @Override
    public Future<List<?>> zrevRangeByScore(String key, String max, String min, int offset, int count) {
        //noinspection unchecked
        return sendCommand(Commands.ZREVRANGEBYSCORE, Transcoder.SERIALIZING_TRANSCODER, key, max, min, null, offset, count);
    }

    @Override
    public Future<List<ZEntity<?>>> zrevRangeByScoreWithScores(String key, String max, String min) {
        //noinspection unchecked
        return sendCommand(Commands.ZREVRANGEBYSCORE, Transcoder.SERIALIZING_TRANSCODER, key, max, min, "WITHSCORES");
    }

    @Override
    public Future<List<ZEntity<?>>> zrevRangeByScoreWithScores(String key, String max, String min, int offset, int count) {
        //noinspection unchecked
        return sendCommand(Commands.ZREVRANGEBYSCORE, Transcoder.SERIALIZING_TRANSCODER, key, max, min, "WITHSCORES", offset, count);
    }

    @Override
    public Future<Long> zrevRank(String key, Object member) {
        //noinspection unchecked
        return sendCommand(Commands.ZREVRANK, Transcoder.SERIALIZING_TRANSCODER, key, member);
    }

    @Override
    public Future<Double> zscore(String key, Object member) {
        //noinspection unchecked
        return sendCommand(Commands.ZSCORE, Transcoder.SERIALIZING_TRANSCODER, key, member);
    }

    final BlockingQueue<Command> commandQueue = new LinkedBlockingQueue<Command>();

    @Override
    public void writeRequested(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        Command message = (Command) e.getMessage();
        logger.debug("send command [{}]", message.getName());
        Channels.write(ctx, e.getFuture(), CommandEncoder.encode(message));
        commandQueue.put(message);
//        ctx.sendDownstream(e);
    }


    public void closeForTest() {
        try {
            channel.close().await(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public void messageReceived(
            ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        logger.debug("messageReceived");
        Reply reply = (Reply) e.getMessage();
        Command command = commandQueue.take();
        command.setResult(reply);
        logger.debug("receive command [{}] 's reply", command.getName());
        ctx.sendUpstream(e);

    }

    @Override
    public synchronized void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        channel = ctx.getChannel();
        commandQueue.clear();
    }

    @Override
    public synchronized void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        //连接断开后清空当前的commandQueue ,
        commandQueue.clear();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        logger.error("exception : ", e.getCause());
        //TODO 是不是所有的异常都需要关闭连接？
        if (ctx.getChannel().isOpen())
            ctx.getChannel().close();
    }
}
