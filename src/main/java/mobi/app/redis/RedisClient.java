package mobi.app.redis;

import mobi.app.redis.netty.reply.Reply;

import java.util.List;
import java.util.Map;

/**
 * User: thor
 * Date: 12-12-28
 * Time: 下午3:41
 */
public interface RedisClient {
    void setClosedHandler(AsyncRedisClient.ClosedHandler handler);
    void setConnectedHandler(AsyncRedisClient.ConnectedHandler handler);
    AsyncRedisClient.ClosedHandler getClosedHandler();
    AsyncRedisClient.ConnectedHandler getConnectedHandler();

    void close();
    //connect commands
    String auth(final String password) ;

    String echo(final String message);

    String ping();

    String select(final int db);

    String quit();

    //keys commands
    Long delete(String key);

    Long delete(String... key);

    byte[] dump(String key);

    Long exists(String key);

    Long expire(String key, int seconds);

    Long expireAt(String key, int timestamp);

    Long ttl(String key);

    List<String> keys(String pattern);

    String migrate(String host, int port, String key, int db, int timeOut);

    /**
     * Move a key to another database
     *
     * @param key cached key
     * @param db  dest db index
     * @return success = 1, fail = 0
     */
    Long move(String key, int db);

    /**
     * Remove the existing timeout on key,
     * turning the key from volatile (a key with an expire set)
     * to persistent (a key that will never expire as no timeout is associated).
     *
     * @param key cached key
     * @return success = 1, fail = 0
     */
    Long presist(String key);

    Long pexpire(String key, long milliseconds);

    Long pexpireAt(String key, long timestamp);

    Long pttl(String key);

    String randomKey();

    String rename(String key, String newKey);

    /**
     * Renames key to newkey if newkey does not yet exist. It returns an error under the same conditions as RENAME.
     *
     * @param key    cached key
     * @param newKey new  cached key
     * @return 1 if key was renamed to newkey. 0 if newkey already exists.
     */
    Long renameNx(String key, String newKey);

    String restore(String key, int expire, byte[] v);

    String type(String key);

    //strings commands
    String set(String key, int o);

    String set(String key, long o);

    String set(String key, double o);

    String set(String key, Object o);

    /**
     * Set key to hold string value if key does not exist. In that case, it is equal to SET.
     * When key already holds a value, no operation is performed. SETNX is short for "SET if N ot e X ists".
     *
     * @param key cached key
     * @param o   value to set
     * @return 1 if the key was set ,0 if the key was not set
     */
    Long setNx(String key, int o);

    Long setNx(String key, long o);

    Long setNx(String key, double o);

    Long setNx(String key, Object o);

    Long msetIntNx(Map<String, Integer> value);

    Long msetLongNx(Map<String, Long> value);

    Long msetDoubleNx(Map<String, Double> value);

    Long msetObjectNx(Map<String, ?> value);

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
    String setEx(String key, int o, int seconds);

    String setEx(String key, long o, int seconds);

    String setEx(String key, double o, int seconds);

    String setEx(String key, Object o, int seconds);

    String msetInt(Map<String, Integer> value);

    String msetLong(Map<String, Long> value);

    String msetDouble(Map<String, Double> value);

    String msetObject(Map<String, ?> value);


    Object get(String key);

    Integer getInt(String key);

    Long getLong(String key);

    Double getDouble(String key);


    List<?> mget(String[] key);

    List<Integer> mgetInt(String[] key);

    List<Long> mgetLong(String[] key);

    List<Double> mgetDouble(String[] key);

    /**
     * Decrements the number stored at key by one.
     * If the key does not exist, it is set to 0 before performing the operation.
     * An error is returned if the key contains a value of the wrong type or contains a string that can not be represented as integer.
     * This operation is limited to 64 bit signed integers.
     *
     * @param key cached key
     * @return the value of key after the decrement
     */
    Long decr(String key);

    Long decrBy(String key, int decrement);

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

    Long incr(String key);

    Long incrBy(String key, int decrement);

    /**
     * Atomically sets key to value and returns the old value stored at key. Returns an error when key exists but does not hold a string value.
     *
     * @param key cached key
     * @param v   the value to set
     * @return the old value stored at key, or nil when key did not exist.
     */
    Integer getAndSet(String key, int v);

    Long getAndSet(String key, long v);

    Double getAndSet(String key, double v);

    Object getAndSet(String key, Object v);

    //hashes

    /**
     * Removes the specified fields from the hash stored at key. Specified fields that do not exist within this hash are ignored.
     * If key does not exist, it is treated as an empty hash and this command returns 0.
     *
     * @param key    cached key
     * @param fields cached field
     * @return the number of fields that were removed from the hash, not including specified but non existing fields.
     */
    Long hdel(String key, String... fields);

    /**
     * @param key   cached key
     * @param field cached field
     * @return 1 if the hash contains field.  0 if the hash does not contain field, or key does not exist.
     */
    Long hexists(String key, String field);

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
    Long hset(String key, String field, int o);

    Long hset(String key, String field, long o);

    Long hset(String key, String field, double o);

    Long hset(String key, String field, Object o);

    Long hsetNx(String key, String field, int o);

    Long hsetNx(String key, String field, long o);

    Long hsetNx(String key, String field, double o);

    Long hsetNx(String key, String field, Object o);

    Object hget(String key, String field);

    Integer hgetInt(String key, String field);

    Long hgetLong(String key, String field);

    Double hgetDouble(String key, String field);

    Map<String, Integer> hgetAllInt(String key);

    Map<String, Long> hgetAllLong(String key);

    Map<String, Double> hgetAllDouble(String key);

    Map<String, ?> hgetAll(String key);

    Long hincrBy(String key, String field, int decrement);

    List<String> hkeys(String key);

    Long hlen(String key);


    List<?> hmget(String key, String[] fields);

    List<Integer> hmgetInt(String key, String[] fields);

    List<Long> hmgetLong(String key, String[] fields);

    List<Double> hmgetDouble(String key, String[] fields);

    String hmsetInt(String key, Map<String, Integer> value);

    String hmsetLong(String key, Map<String, Long> value);

    String hmsetDouble(String key, Map<String, Double> value);

    String hmset(String key, Map<String, ?> value);

    List<Integer> hvalsInt(String key);

    List<Long> hvalsLong(String key);

    List<Double> hvalsDouble(String key);

    List<?> hvals(String key);

    //lists
    Object lindex(String key, int index);

    Long linsertBefore(String key, Object before, Object value);

    Long linsertAfter(String key, Object after, Object value);

    Long llen(String key);

    Object lpop(String key);

    /**
     * @param key    cache key
     * @param values push list value
     * @return the length of the list after the push operations.
     */
    Long lpush(String key, Object... values);

    Long lpushx(String key, Object value);

    List<?> lrange(String key, int start, int stop);

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
    Long lrem(String key, int count, Object value);

    String lset(String key, int index, Object value);

    String ltrim(String key, int start, int stop);

    Object rpop(String key);

    /**
     * @param key    cache key
     * @param values push list value
     * @return the length of the list after the push operations.
     */
    Long rpush(String key, Object... values);

    Long rpushx(String key, Object value);

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
    Object rpoplpush(String source, String destination);

    //sets

    /**
     * Add the specified members to the set stored at key. Specified members that are already a member of this set are ignored. If key does not exist, a new set is created before adding the specified members.
     * An error is returned when the value stored at key is not a set.
     *
     * @param key    cache key
     * @param values the value to add
     * @return the number of elements that were added to the set, not including all the elements already present into the set.
     */
    Long sadd(String key, Object... values);

    /**
     * Returns the set cardinality (number of elements) of the set stored at key.
     *
     * @param key cache key
     * @return the cardinality (number of elements) of the set, or 0 if key does not exist.
     */
    Long scard(String key);

    /**
     * Returns the members of the set resulting from the difference between the first set and all the successive sets.
     * For example:
     *
     * @param key   key
     * @param diffs diff keys
     * @return list with members of the resulting set.
     */
    List<?> sdiff(String key, String... diffs);

    /**
     * This command is equal to SDIFF, but instead of returning the resulting set, it is stored in destination.
     * If destination already exists, it is overwritten.
     *
     * @param destination destination key
     * @param key         key
     * @param diffs       diff keys
     * @return the number of elements in the resulting set.
     */
    Long sdiffStore(String destination, String key, String... diffs);

    /**
     * Returns the members of the set resulting from the intersection of all the given sets.
     * For example:
     * key1 = {a,b,c,d}
     * key2 = {c}
     * key3 = {a,c,e}
     * SINTER key1 key2 key3 = {c}
     *
     * @param key  key
     * @param keys inner keys
     * @return list with members of the resulting set.
     */
    List<?> sinter(String key, String... keys);

    /**
     * This command is equal to SINTER, but instead of returning the resulting set, it is stored in destination.
     * If destination already exists, it is overwritten.
     *
     * @param destination destination key
     * @param key         key
     * @param keys        inner keys
     * @return the number of elements in the resulting set.
     */
    Long sinterStore(String destination, String key, String... keys);

    /**
     * Returns if member is a member of the set stored at key.
     *
     * @param key    key
     * @param member member
     * @return 1 if the element is a member of the set. 0 if the element is not a member of the set, or if key does not exist.
     */
    Long sisMember(String key, Object member);

    /**
     * Returns all the members of the set value stored at key.
     * This has the same effect as running SINTER with one argument key.
     *
     * @param key key
     * @return all elements of the set.
     */
    List<?> smembers(String key);

    /**
     * Move member from the set at source to the set at destination.
     * This operation is atomic.
     * In every given moment the element will appear to be a member of source or destination for other clients.
     *
     * @param source      source set
     * @param destination destination set
     * @param member      moved member
     * @return 1 if the element is moved.
     *         0 if the element is not a member of source and no operation was performed.
     */
    Long smove(String source, String destination, Object member);

    /**
     * Removes and returns a random element from the set value stored at key.
     * This operation is similar to SRANDMEMBER, that returns a random element from a set but does not remove it.
     *
     * @param key key
     * @return the removed element, or nil when key does not exist.
     */
    Object spop(String key);

    Object srandomMember(String key);

    /**
     * When called with just the key argument, return a random element from the set value stored at key.
     * Starting from Redis version 2.6, when called with the additional count argument, return an array of count distinct elements if count is positive. If called with a negative count the behavior changes and the command is allowed to return the same element multiple times. In this case the numer of returned elements is the absolute value of the specified count.
     *
     * @param key   key
     * @param count count
     * @return : when the additional count argument is passed the command returns an array of elements, or an empty array when key does not exist.
     */
    List<?> srandomMember(String key, int count);

    /**
     * Remove the specified members from the set stored at key. Specified members that are not a member of this set are ignored.
     * If key does not exist, it is treated as an empty set and this command returns 0.
     *
     * @param key     key
     * @param members members
     * @return the number of members that were removed from the set, not including non existing members.
     */
    Long srem(String key, Object... members);

    /**
     * Returns the members of the set resulting from the union of all the given sets.
     * For example:
     * key1 = {a,b,c,d}
     * key2 = {c}
     * key3 = {a,c,e}
     * SUNION key1 key2 key3 = {a,b,c,d,e}
     *
     * @param key  key
     * @param keys keys
     * @return list with members of the resulting set.
     */
    List<?> sunion(String key, String... keys);

    /**
     * This command is equal to SUNION, but instead of returning the resulting set, it is stored in destination.
     * If destination already exists, it is overwritten.
     *
     * @param destination destination key
     * @param key         key
     * @param keys        keys
     * @return the number of elements in the resulting set.
     */
    Long sunionStore(String destination, String key, String... keys);

    //sorted sets

    /**
     * Adds all the specified members with the specified scores to the sorted set stored at key.
     *
     * @param key    key
     * @param score  first member score
     * @param member first member
     * @param others more member should be passed as ZEnity
     * @return The number of elements added to the sorted sets, not including elements already existing for which the score was updated.
     */
    Long zadd(String key, double score, Object member, ZEntity... others);

    /**
     * Returns the sorted set cardinality (number of elements) of the sorted set stored at key.
     *
     * @param key key
     * @return the cardinality (number of elements) of the sorted set, or 0 if key does not exist.
     */
    Long zcard(String key);

    /**
     * Returns the number of elements in the sorted set at key with a score between min and max.
     * The min and max arguments have the same semantic as described for ZRANGEBYSCORE.
     *
     * @param key key
     * @param min min score
     * @param max max score
     * @return the number of elements in the specified score range.
     */
    Long zcount(String key, String min, String max);

    /**
     * Increments the score of member in the sorted set stored at key by increment.
     *
     * @param key       key
     * @param increment score increment score
     * @param member    set member
     * @return the new score of member (a double precision floating point number), represented as string.
     */
    Double zincrBy(String key, double increment, Object member);

    /**
     * Computes the intersection of numkeys sorted sets given by the specified keys,
     * and stores the result in destination.
     * It is mandatory to provide the number of input keys (numkeys) before passing the input keys and the other (optional) arguments.
     *
     * @param destination destination key
     * @param keys        keys
     * @param weights     weights . if not null, size must equals keys size.
     * @param aggregate   aggregate .if null,default use ZSetAggregate.SUM
     * @return
     */
    Long zinterStore(String destination, String[] keys, int[] weights, ZSetAggregate aggregate);

    Long zunionStore(String destination, String[] keys, int[] weights, ZSetAggregate aggregate);

    /**
     * Returns the specified range of elements in the sorted set stored at key. The elements are considered to be ordered from the lowest to the highest score. Lexicographical order is used for elements with equal score.
     *
     * @param key   key
     * @param start start index
     * @param stop  stop index
     * @return list of elements in the specified range (optionally with their scores).
     */
    List<?> zrange(String key, int start, int stop);

    List<ZEntity<?>> zrangeWithScores(String key, int start, int stop);

    List<?> zrangeByScore(String key, String min, String max);

    List<?> zrangeByScore(String key, String min, String max, int offset, int count);

    List<ZEntity<?>> zrangeByScoreWithScores(String key, String min, String max);

    List<ZEntity<?>> zrangeByScoreWithScores(String key, String min, String max, int offset, int count);

    /**
     * Returns the rank of member in the sorted set stored at key, with the scores ordered from low to high. The rank (or index) is 0-based, which means that the member with the lowest score has rank 0.
     *
     * @param key    key
     * @param member member
     * @return If member exists in the sorted set, Integer reply: the rank of member.
     *         If member does not exist in the sorted set or key does not exist, Bulk reply: nil.
     */
    Long zrank(String key, Object member);

    /**
     * Removes the specified members from the sorted set stored at key. Non existing members are ignored.
     * An error is returned when key exists and does not hold a sorted set.
     *
     * @param key     key
     * @param members remove members
     * @return The number of members removed from the sorted set, not including non existing members.
     */
    Long zrem(String key, Object... members);

    /**
     * Removes all elements in the sorted set stored at key with rank between start and stop. Both start and stop are 0 -based indexes with 0 being the element with the lowest score.
     *
     * @param key   key
     * @param start start
     * @param stop  stop
     * @return the number of elements removed.
     */
    Long zremRangeByRank(String key, int start, int stop);

    /**
     * Removes all elements in the sorted set stored at key with a score between min and max (inclusive).
     *
     * @param key key
     * @param min min score.format by ZNumbers
     * @param max max score.format by ZNumbers
     * @return the number of elements removed.
     */
    Long zremRangeByScore(String key, String min, String max);

    List<?> zrevRange(String key, int start, int stop);

    List<ZEntity<?>> zrevRangeWithScores(String key, int start, int stop);

    List<?> zrevRangeByScore(String key, String max, String min);
    List<?> zrevRangeByScore(String key, String max, String min, int offset, int count);

    List<ZEntity<?>> zrevRangeByScoreWithScores(String key, String max, String min);
    List<ZEntity<?>> zrevRangeByScoreWithScores(String key, String max, String min, int offset, int count);

    Long zrevRank(String key, Object member);
    /**
     * Returns the score of member in the sorted set at key.
     * If member does not exist in the sorted set, or key does not exist, nil is returned.
     *
     * @param key    key
     * @param member member
     * @return the score of member (a double precision floating point number), represented as string.
     */
    Double zscore(String key, Object member);

    Reply eval(String script, String[] keys, byte[]... args);

    Reply evalSha(String sha1, String[] keys, byte[]... args);

    String scriptLoad(String script);
    Long scriptExists(String script);
    List<Integer> scriptExists(String[]  scripts);
    String scriptFlush();
    String scriptKill();

    //servers
    String  bgRewriteAOF();
    String bgSave();
    String clientKill(String ip, int port);
    String clientList();
    String configGet(String parameter);
    String configResetStat();
    String configSet(String config, String parameter);
    Long dbSize();
    String flushAll();
    String flushDB();
    String info();
    Long lastSave();
    String save();
    String shutdown(boolean save);
    String slaveOf(String host, int port);
}
