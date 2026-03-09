import java.util.*;

class DNSEntry {
    String domain;
    String ip;
    long expiryTime;

    DNSEntry(String domain, String ip, long ttlSeconds) {
        this.domain = domain;
        this.ip = ip;
        this.expiryTime = System.currentTimeMillis() + ttlSeconds * 1000;
    }

    boolean isExpired() {
        return System.currentTimeMillis() > expiryTime;
    }
}

public class DNSCache {

    private int capacity;
    private Map<String, DNSEntry> cache;
    private LinkedHashMap<String, DNSEntry> lru;
    private int hits = 0;
    private int misses = 0;

    public DNSCache(int capacity) {
        this.capacity = capacity;
        this.cache = new HashMap<>();
        this.lru = new LinkedHashMap<>(capacity, 0.75f, true);
    }

    public String resolve(String domain) {

        DNSEntry entry = cache.get(domain);

        if (entry != null && !entry.isExpired()) {
            hits++;
            lru.get(domain);
            return "Cache HIT → " + entry.ip;
        }

        if (entry != null && entry.isExpired()) {
            cache.remove(domain);
            lru.remove(domain);
        }

        misses++;
        String ip = queryUpstream(domain);
        put(domain, ip, 300);
        return "Cache MISS → " + ip;
    }

    private void put(String domain, String ip, long ttl) {

        if (cache.size() >= capacity) {
            String oldest = lru.keySet().iterator().next();
            cache.remove(oldest);
            lru.remove(oldest);
        }

        DNSEntry entry = new DNSEntry(domain, ip, ttl);
        cache.put(domain, entry);
        lru.put(domain, entry);
    }

    private String queryUpstream(String domain) {
        return "172.217.14." + new Random().nextInt(255);
    }

    public void getCacheStats() {
        int total = hits + misses;
        double hitRate = total == 0 ? 0 : (hits * 100.0 / total);
        System.out.println("Hit Rate: " + hitRate + "%");
    }

    public static void main(String[] args) throws InterruptedException {

        DNSCache dns = new DNSCache(3);

        System.out.println(dns.resolve("google.com"));
        System.out.println(dns.resolve("google.com"));

        Thread.sleep(2000);

        System.out.println(dns.resolve("facebook.com"));
        System.out.println(dns.resolve("amazon.com"));

        dns.getCacheStats();
    }
}