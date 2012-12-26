package mobi.app.redis.netty.command;

import mobi.app.redis.transcoders.Transcoder;

/**
 * User: thor
 * Date: 12-12-26
 * Time: 下午5:29
 */
public class LremOrSetCommand<T> extends BaseCommand<T> {
    public LremOrSetCommand(Transcoder transcoder, String command, String key, int count, Object value) {

        this.command = command;
        if (transcoder != null)
            setTranscoder(transcoder);
        else
            setTranscoder(SERIALIZING_TRANSCODER);
        byte[][] byteArgs = new byte[3][];
        byteArgs[0] = tu.encodeString(key);
        byteArgs[1] = tu.encodeString(String.valueOf(count));
        //noinspection unchecked
        byteArgs[2] = getTranscoder().encode(value);


        init(byteArgs);
    }
}
