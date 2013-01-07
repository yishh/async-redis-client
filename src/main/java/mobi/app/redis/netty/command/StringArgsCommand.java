package mobi.app.redis.netty.command;

import mobi.app.redis.transcoders.Transcoder;
import mobi.app.redis.transcoders.TranscoderUtils;

/**
 * User: thor
 * Date: 12-12-20
 * Time: 上午11:17
 */
public class StringArgsCommand<T> extends BaseCommand<T> {


    public StringArgsCommand(Transcoder transcoder, String command) {
        this(transcoder, command, new String[]{});
    }


    public StringArgsCommand(Transcoder transcoder, String command, String arg) {
        this(transcoder, command, new String[]{arg});
    }

    public StringArgsCommand(Transcoder transcoder, String command, String... args) {
        this.command = command;
        setTranscoder(transcoder);
        byte[][] byteArgs = new byte[args.length][];
        for (int i = 0; i < args.length; i++) {
            byteArgs[i] = TranscoderUtils.encodeString(args[i]);
        }
        init(byteArgs);
    }

    public StringArgsCommand(Transcoder transcoder, String command, String key, Object... args) {
        this(transcoder, command, new String[]{key}, args);
    }

    public StringArgsCommand(Transcoder transcoder, String command, String[] keys, Object... args) {

        this.command = command;
        if (transcoder != null)
            setTranscoder(transcoder);
        else
            setTranscoder(SERIALIZING_TRANSCODER);
        byte[][] byteArgs = new byte[args.length + keys.length][];
        for (int i = 0; i < keys.length; i++) {
            byteArgs[i] = TranscoderUtils.encodeString(keys[i]);
        }

        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof byte[]) {
                byteArgs[i + keys.length] = (byte[]) args[i];
            } else {
                //noinspection unchecked
                byteArgs[i + keys.length] = getTranscoder().encode(args[i]);
            }
        }
        init(byteArgs);
    }
}
