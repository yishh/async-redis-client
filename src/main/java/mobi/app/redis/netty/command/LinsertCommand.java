package mobi.app.redis.netty.command;

import mobi.app.redis.transcoders.Transcoder;

/**
 * User: thor
 * Date: 12-12-26
 * Time: 下午5:10
 */
public class LinsertCommand<T> extends BaseCommand<T> {
    public LinsertCommand(Transcoder transcoder, String command, String key, String position, Object pivot, Object value) {

        this.command = command;
        if(transcoder != null)
            setTranscoder(transcoder);
        else
            setTranscoder(SERIALIZING_TRANSCODER);
        byte[][] byteArgs = new byte[4][];
        byteArgs[0] = tu.encodeString(key);
        byteArgs[1] = tu.encodeString(position);
        //noinspection unchecked
        byteArgs[2] =  getTranscoder().encode(pivot);
        //noinspection unchecked
        byteArgs[3] = getTranscoder().encode(value);

        init(byteArgs);
    }
}
