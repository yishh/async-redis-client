package mobi.app.redis;

import java.util.concurrent.TimeUnit;

/**
 * User: thor
 * Date: 13-1-4
 * Time: 下午2:09
 */
public class RedisConnection {
    public final String address;
    public final int db;
    public final String password ;
    public final int timeout;
    public final TimeUnit timeUnit;
    public final int weight;

    public RedisConnection(String address, int db, String password, int timeout, TimeUnit timeUnit) {
        this.address = address;
        this.db = db;
        this.password = password;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
        this.weight = 1;
    }

    public RedisConnection(String address, int db, String password, int timeout, TimeUnit timeUnit, int weight) {
        this.address = address;
        this.db = db;
        this.password = password;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
        this.weight = weight;
    }
}
