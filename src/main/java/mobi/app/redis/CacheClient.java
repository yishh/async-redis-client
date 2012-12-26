package mobi.app.redis;

/**
 * User: thor
 * Date: 12-12-18
 * Time: 下午6:42
 */
public interface CacheClient {

    //server commands
    String auth(String password);
    String echo(String message);
    String ping();
    String select(int db);
    String quit();

    //keys commands
    long delete(String key);
    long delete(String... key);

    String dump(String key);

    boolean exists(String key);
    long expire(String key, int seconds);
    long expireAt(String key, long timestamp);

    String[] keys(String pattern);
    String migrate(String host, int port, String key, int db, int timeOut);

    /**
     * Move a key to another database
     * @param key  cached key
     * @param db   dest db index
     * @return  success = true, fail = false
     */
    boolean move(String key, int db);

    /**
     *  Remove the existing timeout on key,
     *  turning the key from volatile (a key with an expire set)
     *  to persistent (a key that will never expire as no timeout is associated).
     * @param key cached key
     * @return   success = true, fail = false
     */
    boolean presist(String key);

    //strings commands
    String set(String key, Object o);
    Object get(String key);



}
