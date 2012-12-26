package mobi.app.redis.netty.command;

import mobi.app.redis.transcoders.Transcoder;

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
            byteArgs[i] = tu.encodeString(args[i]);
        }
        init(byteArgs);
    }

    public StringArgsCommand(Transcoder transcoder, String command, String key, Object... args) {

        this.command = command;
        if(transcoder != null)
            setTranscoder(transcoder);
        else
            setTranscoder(SERIALIZING_TRANSCODER);
        byte[][] byteArgs = new byte[args.length + 1][];
        byteArgs[0] = tu.encodeString(key);
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof byte[]) {
                byteArgs[i+1] = (byte[]) args[i];
            }else {
                //noinspection unchecked
                byteArgs[i+1] = getTranscoder().encode(args[i]);
            }
        }
        init(byteArgs);
    }
}
