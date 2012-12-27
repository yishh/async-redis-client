package mobi.app.redis.netty.command;

import mobi.app.redis.netty.reply.Reply;
import mobi.app.redis.transcoders.Transcoder;

/**
 * User: thor
 * Date: 12-12-27
 * Time: 下午3:09
 */
public class ZScoreCommand<T> extends BaseCommand<T> {
    public ZScoreCommand(Transcoder transcoder, String command, String key, Object member) {
        this.command = command;
        setTranscoder(transcoder);
        byte[][] byteArgs = new byte[2][];
        byteArgs[0] = tu.encodeString(String.valueOf(key));
        //noinspection unchecked
        byteArgs[1] = getTranscoder().encode(member);

        init(byteArgs);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setResult(Reply reply) {
        Double result = (Double) reply.decode(Transcoder.DOUBLE_TRANSCODER);
        future.setResultAndAWake((T) result);
    }
}
