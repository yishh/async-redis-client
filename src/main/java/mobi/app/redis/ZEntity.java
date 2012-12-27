package mobi.app.redis;

/**
 * User: thor
 * Date: 12-12-27
 * Time: 下午1:50
 */
public class ZEntity<T> {
    public final T member;
    public final double score;

    public ZEntity(T member, double score) {
        this.member = member;
        this.score = score;
    }
}
