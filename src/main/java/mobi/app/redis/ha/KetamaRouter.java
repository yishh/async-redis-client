package mobi.app.redis.ha;


import mobi.app.redis.HashAlgorithm;
import mobi.app.redis.RedisClient;

import java.util.*;

/**
 * User: thor
 * Date: 13-1-7
 * Time: 上午10:31
 * <p/>
 * A router use ketama hash algorithm
 */
public class KetamaRouter implements Router {
    private static final int NUM_REPS = 160;
    private final Map<RedisClient, Integer> redisClientMap;
    private final TreeMap<Long, List<RedisClient>> ketamaClients;
    private static final int MAX_TRIES = 50;
    private final Random random = new Random();

    /**
     * Construct a new  ketamaRouter
     *
     * @param redisClientMap key is  redis client, value is weight, must  > 0.
     */
    public KetamaRouter(Map<RedisClient, Integer> redisClientMap) {
        this.redisClientMap = redisClientMap;
        ketamaClients = new TreeMap<Long, List<RedisClient>>();
        // build ke
        for (RedisClient client : redisClientMap.keySet()) {
            String hashKey = client.hashKey();
            int numReps = NUM_REPS * redisClientMap.get(client);
            for (int i = 0; i < numReps / 4; i++) {
                byte[] digest = HashAlgorithm.computeMd5(hashKey + "-" + i);
                for (int h = 0; h < 4; h++) {
                    long k = (long) (digest[3 + h * 4] & 0xFF) << 24
                            | (long) (digest[2 + h * 4] & 0xFF) << 16
                            | (long) (digest[1 + h * 4] & 0xFF) << 8
                            | digest[h * 4] & 0xFF;
                    this.getClientList(ketamaClients, k).add(client);
                }
            }
        }
    }


    public final RedisClient getClientByHash(final long hash) {
        TreeMap<Long, List<RedisClient>> clientMap = this.ketamaClients;
        if (clientMap.size() == 0) {
            return null;
        }
        Long resultHash = hash;
        if (!clientMap.containsKey(resultHash)) {
            resultHash = clientMap.ceilingKey(resultHash);
            if (resultHash == null && clientMap.size() > 0) {
                resultHash = clientMap.firstKey();
            }
        }
        List<RedisClient> clientList = clientMap.get(resultHash);
        if (clientList == null || clientList.size() == 0) {
            return null;
        }
        int size = clientList.size();
        return clientList.get(this.random.nextInt(size));
    }

    public final long nextHash(long hashVal, String key, int tries) {
        long tmpKey = HashAlgorithm.KETAMA_HASH.hash(tries + key);
        hashVal += (int) (tmpKey ^ tmpKey >>> 32);
        hashVal &= 0xffffffffL; /* truncate to 32-bits */
        return hashVal;
    }


    private List<RedisClient> getClientList(
            TreeMap<Long, List<RedisClient>> clientMap, long k) {
        List<RedisClient> clientList = clientMap.get(k);
        if (clientList == null) {
            clientList = new ArrayList<RedisClient>();
            clientMap.put(k, clientList);
        }
        return clientList;
    }

    @Override
    public RedisClient route(String key) {
        if (this.ketamaClients == null || this.ketamaClients.size() == 0) {
            return null;
        }
        long hash = HashAlgorithm.KETAMA_HASH.hash(key);
        RedisClient client = this.getClientByHash(hash);
        int tries = 0;
        while ((client == null || client.isAvailable())
                && tries++ < MAX_TRIES) {
            hash = this.nextHash(hash, key, tries);
            client = this.getClientByHash(hash);
        }
        return client;
    }

    @Override
    public Collection<RedisClient> getClients() {
        return redisClientMap.keySet();
    }
}
