package mobi.app.redis.netty;

/**
 * User: thor
 * Date: 12-12-20
 * Time: 下午4:31
 */
public class RedisException extends RuntimeException {
    private static final long serialVersionUID = 308360259601044866L;
    public RedisException(Exception e) {
        super(e);
    }
    public RedisException(String s) {
        super(s);
    }
}
