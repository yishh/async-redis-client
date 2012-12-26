package mobi.app.redis;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * User: thor
 * Date: 12-12-21
 * Time: 上午10:31
 */
public interface AsyncRedisClient {
    //connect commands
    Future<String> auth(String password);

    Future<String> echo(String message);

    Future<String> ping();

    Future<String> select(int db);

    Future<String> quit();

    //keys commands
    Future<Long> delete(String key);

    Future<Long> delete(String... key);

    Future<byte[]> dump(String key);

    Future<Long> exists(String key);

    Future<Long> expire(String key, int seconds);

    Future<Long> expireAt(String key, int timestamp);

    Future<Long> ttl(String key);

    Future<List<String>> keys(String pattern);

    Future<String> migrate(String host, int port, String key, int db, int timeOut);

    /**
     * Move a key to another database
     *
     * @param key cached key
     * @param db  dest db index
     * @return success = 1, fail = 0
     */
    Future<Long> move(String key, int db);

    /**
     * Remove the existing timeout on key,
     * turning the key from volatile (a key with an expire set)
     * to persistent (a key that will never expire as no timeout is associated).
     *
     * @param key cached key
     * @return success = 1, fail = 0
     */
    Future<Long> presist(String key);

    Future<Long> pexpire(String key, long milliseconds);

    Future<Long> pexpireAt(String key, long timestamp);

    Future<Long> pttl(String key);

    Future<String> randomKey();

    Future<String> rename(String key, String newKey);

    /**
     * Renames key to newkey if newkey does not yet exist. It returns an error under the same conditions as RENAME.
     *
     * @param key    cached key
     * @param newKey new  cached key
     * @return 1 if key was renamed to newkey. 0 if newkey already exists.
     */
    Future<Long> renameNx(String key, String newKey);

    Future<String> restore(String key, int expire, byte[] v);

    Future<String> type(String key);

    //strings commands
    Future<String> set(String key, int o);

    Future<String> set(String key, long o);

    Future<String> set(String key, double o);

    Future<String> set(String key, Object o);

    /**
     * Set key to hold string value if key does not exist. In that case, it is equal to SET.
     * When key already holds a value, no operation is performed. SETNX is short for "SET if N ot e X ists".
     *
     * @param key cached key
     * @param o   value to set
     * @return 1 if the key was set ,0 if the key was not set
     */
    Future<Long> setNx(String key, int o);

    Future<Long> setNx(String key, long o);

    Future<Long> setNx(String key, double o);

    Future<Long> setNx(String key, Object o);

    Future<Long> msetIntNx(Map<String, Integer> value);

    Future<Long> msetLongNx(Map<String, Long> value);

    Future<Long> msetDoubleNx(Map<String, Double> value);

    Future<Long> msetObjectNx(Map<String, ?> value);

    /**
     * Set key to hold the string value and set key to timeout after a given number of seconds.
     * This command is equivalent to executing the following commands:
     * SET mykey value
     * EXPIRE mykey seconds
     * SETEX is atomic, and can be reproduced by using the previous two commands inside an MULTI / EXEC block. It is provided as a faster alternative to the given sequence of operations, because this operation is very common when Redis is used as a cache.
     * An error is returned when seconds is invalid.
     *
     * @param key     cached key
     * @param o       value to set
     * @param seconds timeout seconds
     * @return status code
     */
    Future<String> setEx(String key, int o, int seconds);

    Future<String> setEx(String key, long o, int seconds);

    Future<String> setEx(String key, double o, int seconds);

    Future<String> setEx(String key, Object o, int seconds);

    Future<String> msetInt(Map<String, Integer> value);

    Future<String> msetLong(Map<String, Long> value);

    Future<String> msetDouble(Map<String, Double> value);

    Future<String> msetObject(Map<String, ?> value);


    Future<Object> get(String key);

    Future<Integer> getInt(String key);

    Future<Long> getLong(String key);

    Future<Double> getDouble(String key);


    Future<List<Object>> mget(String[] key);

    Future<List<Integer>> mgetInt(String[] key);

    Future<List<Long>> mgetLong(String[] key);

    Future<List<Double>> mgetDouble(String[] key);

    /**
     * Decrements the number stored at key by one.
     * If the key does not exist, it is set to 0 before performing the operation.
     * An error is returned if the key contains a value of the wrong type or contains a string that can not be represented as integer.
     * This operation is limited to 64 bit signed integers.
     *
     * @param key cached key
     * @return the value of key after the decrement
     */
    Future<Long> decr(String key);

    Future<Long> decrBy(String key, int decrement);

    /**
     * Increments the number stored at key by one.
     * If the key does not exist, it is set to 0 before performing the operation.
     * An error is returned if the key contains a value of the wrong type or contains a string that can not be represented as integer.
     * This operation is limited to 64 bit signed integers.
     * Note: this is a string operation because Redis does not have a dedicated integer type. The string stored at the key is interpreted as a base-10 64 bit signed integer to execute the operation.
     * Redis stores integers in their integer representation, so for string values that actually hold an integer, there is no overhead for storing the string representation of the integer.
     *
     * @param key cached key
     * @return the value of key after the increment
     */

    Future<Long> incr(String key);

    Future<Long> incrBy(String key, int decrement);

    /**
     * Atomically sets key to value and returns the old value stored at key. Returns an error when key exists but does not hold a string value.
     *
     * @param key cached key
     * @param v   the value to set
     * @return the old value stored at key, or nil when key did not exist.
     */
    Future<Integer> getAndSet(String key, int v);

    Future<Long> getAndSet(String key, long v);

    Future<Double> getAndSet(String key, double v);

    Future<Object> getAndSet(String key, Object v);

    //hashes

    /**
     * Removes the specified fields from the hash stored at key. Specified fields that do not exist within this hash are ignored.
     * If key does not exist, it is treated as an empty hash and this command returns 0.
     *
     * @param key    cached key
     * @param fields cached field
     * @return the number of fields that were removed from the hash, not including specified but non existing fields.
     */
    Future<Long> hdel(String key, String... fields);

    /**
     * @param key   cached key
     * @param field cached field
     * @return 1 if the hash contains field.  0 if the hash does not contain field, or key does not exist.
     */
    Future<Long> hexists(String key, String field);

    /**
     * Sets field in the hash stored at key to value.
     * If key does not exist, a new key holding a hash is created.
     * If field already exists in the hash, it is overwritten.
     *
     * @param key   cached key
     * @param field cached field
     * @param o     set value
     * @return 1 if field is a new field in the hash and value was set. 0 if field already exists in the hash and the value was updated.
     */
    Future<Long> hset(String key, String field, int o);

    Future<Long> hset(String key, String field, long o);

    Future<Long> hset(String key, String field, double o);

    Future<Long> hset(String key, String field, Object o);

    Future<Long> hsetNx(String key, String field, int o);

    Future<Long> hsetNx(String key, String field, long o);

    Future<Long> hsetNx(String key, String field, double o);

    Future<Long> hsetNx(String key, String field, Object o);

    Future<Object> hget(String key, String field);

    Future<Integer> hgetInt(String key, String field);

    Future<Long> hgetLong(String key, String field);

    Future<Double> hgetDouble(String key, String field);

    Future<Map<String, Integer>> hgetAllInt(String key);

    Future<Map<String, Long>> hgetAllLong(String key);

    Future<Map<String, Double>> hgetAllDouble(String key);

    Future<Map<String, ?>> hgetAll(String key);

    Future<Long> hincrBy(String key, String field, int decrement);

    Future<List<String>> hkeys(String key);

    Future<Long> hlen(String key);


    Future<List<?>> hmget(String key, String[] fields);

    Future<List<Integer>> hmgetInt(String key, String[] fields);

    Future<List<Long>> hmgetLong(String key, String[] fields);

    Future<List<Double>> hmgetDouble(String key, String[] fields);

    Future<String> hmsetInt(String key, Map<String, Integer> value);

    Future<String> hmsetLong(String key, Map<String, Long> value);

    Future<String> hmsetDouble(String key, Map<String, Double> value);

    Future<String> hmset(String key, Map<String, ?> value);

    Future<List<Integer>> hvalsInt(String key);

    Future<List<Long>> hvalsLong(String key);

    Future<List<Double>> hvalsDouble(String key);

    Future<List<?>> hvals(String key);

    //lists
    Future<Object> lindex(String key, int index);

    Future<Long> linsertBefore(String key, Object before, Object value);

    Future<Long> linsertAfter(String key, Object after, Object value);

    Future<Long> llen(String key);

    Future<Object> lpop(String key);

    /**
     * @param key    cache key
     * @param values push list value
     * @return the length of the list after the push operations.
     */
    Future<Long> lpush(String key, Object... values);

    Future<Long> lpushx(String key, Object value);

    Future<List<?>> lrange(String key, int start, int stop);

    /**
     * Removes the first count occurrences of elements equal to value from the list stored at key
     *
     * @param key   cache key
     * @param count count > 0: Remove elements equal to value moving from head to tail.
     *              count < 0: Remove elements equal to value moving from tail to head.
     *              count = 0: Remove all elements equal to value.
     * @param value removed value
     * @return the number of removed elements.
     */
    Future<Long> lrem(String key, int count, Object value);

    Future<String> lset(String key, int index, Object value);

    Future<String> ltrim(String key, int start, int stop);

    Future<Object> rpop(String key);

    /**
     * @param key    cache key
     * @param values push list value
     * @return the length of the list after the push operations.
     */
    Future<Long> rpush(String key, Object... values);

    Future<Long> rpushx(String key, Object value);

    /**
     * Atomically returns and removes the last element (tail) of the list stored at source, and pushes the element at the first element (head) of the list stored at destination.
     * For example: consider source holding the list a,b,c, and destination holding the list x,y,z. Executing RPOPLPUSH results in source holding a,b and destination holding c,x,y,z.
     * If source does not exist, the value nil is returned and no operation is performed.
     * If source and destination are the same, the operation is equivalent to removing the last element from the list and pushing it as first element of the list, so it can be considered as a list rotation command.
     *
     * @param source      source list key
     * @param destination destination list key
     * @return the element being popped and pushed.
     */
    Future<Object> rpoplpush(String source, String destination);
}
