package mobi.app.redis.netty.reply;

import mobi.app.redis.transcoders.Transcoder;

/**
 * User: thor
 * Date: 12-12-25
 * Time: 下午3:10
 */
public class BytesTranscoder implements Transcoder<byte[]> {
    @Override
    public byte[] encode(byte[] v) {
        return v;
    }

    @Override
    public byte[] decode(byte[] v) {
        return v;
    }
}
