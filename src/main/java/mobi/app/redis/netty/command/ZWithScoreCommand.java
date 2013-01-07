package mobi.app.redis.netty.command;

import mobi.app.redis.ZEntity;
import mobi.app.redis.netty.reply.Reply;
import mobi.app.redis.transcoders.Transcoder;
import mobi.app.redis.transcoders.TranscoderUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * User: thor
 * Date: 12-12-27
 * Time: 下午2:57
 */
public class ZWithScoreCommand<T> extends BaseCommand<T> {
    public ZWithScoreCommand(Transcoder transcoder, String command, String key, String... args) {
        this.command = command;
        setTranscoder(transcoder);
        byte[][] byteArgs = new byte[args.length + 1][];
        byteArgs[0] = TranscoderUtils.encodeString(String.valueOf(key));
        int i = 1;
        for (String arg : args) {
            byteArgs[i] = TranscoderUtils.encodeString(arg);
            i += 1;
        }
        init(byteArgs);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setResult(Reply reply) {
        List<byte[]> results = (List<byte[]>) reply.decode(Transcoder.BYTES_TRANSCODER);
        List<ZEntity<?>> entities = new ArrayList<ZEntity<?>>();
        if (!results.isEmpty()) {
            for (int i = 0; i < results.size(); ) {
                Object o = transcoder.decode(results.get(i));
                entities.add(new ZEntity(o, Double.valueOf(new String(results.get(i + 1)))));
                i += 2;
            }
        }
        future.setResultAndAWake((T) entities);
    }
}
