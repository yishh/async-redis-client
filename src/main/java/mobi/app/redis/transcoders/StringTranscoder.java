package mobi.app.redis.transcoders;

/**
 * User: thor
 * Date: 12-12-21
 * Time: 下午4:50
 */
public class StringTranscoder implements Transcoder<String>{
    @Override
    public byte[] encode(String v) {
        return v.getBytes();
    }

    @Override
    public String decode(byte[] v) {
        return new String(v);
    }
}
