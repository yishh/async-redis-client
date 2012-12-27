package mobi.app.redis;

/**
 * User: thor
 * Date: 12-12-27
 * Time: 下午2:15
 */
public class ZNumbers {
    public static final String MINIMUM = "-inf";
    public static final String MAXIMUM = "+inf";
    public static String includeNumber(double d){
         return String.format("%s", d);
    }

    public static String excludeNumber(double d){
        return String.format("(%s", d);
    }

    public static String includeNumber(int d){
        return String.format("%s", d);
    }

    public static String excludeNumber(int d){
        return String.format("(%s", d);
    }

    public static String includeNumber(long d){
        return String.format("%s", d);
    }

    public static String excludeNumber(long d){
        return String.format("(%s", d);
    }
}
