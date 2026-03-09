import java.util.*;

class PageEvent {
    String url;
    String userId;
    String source;

    PageEvent(String url, String userId, String source) {
        this.url = url;
        this.userId = userId;
        this.source = source;
    }
}

public class RealTimeAnalytics {

    private Map<String, Integer> pageViews = new HashMap<>();
    private Map<String, Set<String>> uniqueVisitors = new HashMap<>();
    private Map<String, Integer> trafficSources = new HashMap<>();

    public void processEvent(PageEvent event) {

        pageViews.put(event.url, pageViews.getOrDefault(event.url, 0) + 1);

        uniqueVisitors
                .computeIfAbsent(event.url, k -> new HashSet<>())
                .add(event.userId);

        trafficSources.put(event.source,
                trafficSources.getOrDefault(event.source, 0) + 1);
    }

    public void getDashboard() {

        List<Map.Entry<String, Integer>> pages =
                new ArrayList<>(pageViews.entrySet());

        pages.sort((a, b) -> b.getValue() - a.getValue());

        System.out.println("Top Pages:");

        int limit = Math.min(10, pages.size());

        for (int i = 0; i < limit; i++) {
            String url = pages.get(i).getKey();
            int views = pages.get(i).getValue();
            int unique = uniqueVisitors.get(url).size();

            System.out.println((i + 1) + ". " + url +
                    " - " + views + " views (" + unique + " unique)");
        }

        System.out.println("\nTraffic Sources:");

        for (Map.Entry<String, Integer> e : trafficSources.entrySet()) {
            System.out.println(e.getKey() + " → " + e.getValue());
        }
    }

    public static void main(String[] args) {

        RealTimeAnalytics analytics = new RealTimeAnalytics();

        analytics.processEvent(new PageEvent("/article/breaking-news", "user123", "google"));
        analytics.processEvent(new PageEvent("/article/breaking-news", "user456", "facebook"));
        analytics.processEvent(new PageEvent("/sports/championship", "user789", "google"));
        analytics.processEvent(new PageEvent("/article/breaking-news", "user123", "direct"));
        analytics.processEvent(new PageEvent("/sports/championship", "user222", "facebook"));
        analytics.processEvent(new PageEvent("/sports/championship", "user333", "google"));

        analytics.getDashboard();
    }
}