package mobi.app.redis.netty.command;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * User: thor
 * Date: 12-12-20
 * Time: 下午2:34
 */
public class ReplyFutureTask<V> extends FutureTask<V> {
    public ReplyFutureTask(Callable<V> callable) {
        super(callable);
    }

    public ReplyFutureTask() {
        super(new Callable<V>() {
            @Override
            public V call() throws Exception {
                return null;
            }
        });
    }

    /**
     * 设置结果，并唤醒等待该结果的线程
     *
     * @param v result
     */
    public void setResultAndAWake(V v) {
        set(v);
    }
}
