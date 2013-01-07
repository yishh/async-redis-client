package mobi.app.redis.netty.command;

import mobi.app.redis.netty.reply.Reply;
import mobi.app.redis.transcoders.Transcoder;
import mobi.app.redis.transcoders.TranscoderUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: thor
 * Date: 12-12-26
 * Time: 下午2:12
 */
public class HgetallCommand<T> extends BaseCommand<T> {
    public HgetallCommand(Transcoder transcoder, String command, String key) {
        this.command = command;
        setTranscoder(transcoder);
        byte[][] byteArgs = new byte[1][];
        byteArgs[0] = TranscoderUtils.encodeString(key);
        init(byteArgs);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setResult(Reply reply) {
        List<byte[]> results = (List<byte[]>) reply.decode(Transcoder.BYTES_TRANSCODER);
        Map cached = new HashMap();
        if (!results.isEmpty()) {
            for (int i = 0; i < results.size(); ) {
                String key = TranscoderUtils.decodeString(results.get(i));
                cached.put(key, transcoder.decode(results.get(i + 1)));
                i += 2;
            }
        }
        future.setResultAndAWake((T) cached);
    }

}
