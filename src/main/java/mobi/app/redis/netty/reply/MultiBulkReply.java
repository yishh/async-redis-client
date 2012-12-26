package mobi.app.redis.netty.reply;

import mobi.app.redis.transcoders.Transcoder;

import java.util.ArrayList;
import java.util.List;

/**
 * User: thor
 * Date: 12-12-21
 * Time: 上午10:04
 */
public class MultiBulkReply<T> implements Reply<List<T>> {
    final List<byte[]> result;

    final int fullSize;

    public MultiBulkReply(int size) {
        fullSize = size;
        if (size >= 0)
            result = new ArrayList<byte[]>(size);
        else
            result = null;
    }

    public boolean isReady() {
        return fullSize == -1 || result.size() == fullSize;
    }

    public void add(byte[] object) {
        result.add(object);
    }

    @Override
    public Object get() {
        return result;
    }

    @Override
    public List<T> decode(Transcoder transcoder) {
        List<T> decodeResult = new ArrayList<T>(fullSize);
        for (byte[] buffer : result) {
            if (buffer == null) {
                decodeResult.add(null);
            } else {
                //noinspection unchecked
                decodeResult.add((T) transcoder.decode(buffer));
            }
        }
        return decodeResult;
    }
}
