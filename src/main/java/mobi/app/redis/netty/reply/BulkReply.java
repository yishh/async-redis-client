package mobi.app.redis.netty.reply;

import mobi.app.redis.transcoders.Transcoder;

/**
 * User: thor
 * Date: 12-12-20
 * Time: 下午4:59
 */
public class BulkReply implements Reply<Object> {


    final byte[] result;

    public BulkReply(byte[] result) {
        this.result = result;
    }


    @Override
    public byte[] get() {
        return result;
    }

    @Override
    public Object decode(Transcoder transcoder) {
        if (result == null || result.length == 0) return null;
        return transcoder.decode(result);
    }


}
