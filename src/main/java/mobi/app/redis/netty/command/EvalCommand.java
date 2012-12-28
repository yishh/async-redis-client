package mobi.app.redis.netty.command;

import mobi.app.redis.netty.reply.Reply;
import mobi.app.redis.transcoders.Transcoder;

/**
 * User: thor
 * Date: 12-12-28
 * Time: 上午10:46
 */
public class EvalCommand extends BaseCommand<Reply> {
    public EvalCommand(Transcoder transcoder, String command, byte[] script, String[] keys, byte[]... args) {
        this.command = command;
        if (transcoder != null)
            setTranscoder(transcoder);
        else
            setTranscoder(SERIALIZING_TRANSCODER);
        byte[][] byteArgs = new byte[args.length + keys.length + 2][];
        byteArgs[0] = script;
        byteArgs[1] = tu.encodeString(String.valueOf(keys.length));
        for (int i = 0; i < keys.length; i++) {
            byteArgs[i + 2] = tu.encodeString(keys[i]);
        }
        for (int i = 0; i < args.length; i++) {
            byteArgs[i + 2 + keys.length] = args[i];
        }
        init(byteArgs);
    }

    @Override
    public void setResult(Reply reply) {
        future.setResultAndAWake( reply);
    }

}
