package mobi.app.redis.netty.command;

import mobi.app.redis.transcoders.Transcoder;

/**
 * User: thor
 * Date: 12-12-26
 * Time: 下午1:28
 */
public class HashCommand<T> extends BaseCommand<T>{
    public HashCommand(Transcoder transcoder, String command, String key, String field, Object... args) {
        this.command = command;
        if(transcoder != null)
            setTranscoder(transcoder);
        else
            setTranscoder(SERIALIZING_TRANSCODER);
        byte[][] byteArgs = new byte[args.length + 2][];
        byteArgs[0] = tu.encodeString(key);
        byteArgs[1] = tu.encodeString(field);
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof byte[]) {
                byteArgs[i+2] = (byte[]) args[i];
            }else {
                //noinspection unchecked
                byteArgs[i+2] = getTranscoder().encode(args[i]);
            }
        }
        init(byteArgs);
    }
}
