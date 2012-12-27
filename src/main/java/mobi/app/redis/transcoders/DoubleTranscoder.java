package mobi.app.redis.transcoders;

/**
 * User: thor
 * Date: 12-12-24
 * Time: 下午2:29
 */
public class DoubleTranscoder implements Transcoder<Double> {
    @Override
    public byte[] encode(Double v) {
        return String.valueOf(v).getBytes();
    }

    @Override
    public Double decode(byte[] v) {
        if(v == null) return null;
        return Double.parseDouble(new String(v));
    }
}
