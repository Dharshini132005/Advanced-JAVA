import java.util.*;

public class UsernameChecker {

    private HashMap<String, Integer> users = new HashMap<>();
    private HashMap<String, Integer> attempts = new HashMap<>();
    private int userIdCounter = 1;

    public boolean checkAvailability(String username) {
        attempts.put(username, attempts.getOrDefault(username, 0) + 1);
        return !users.containsKey(username);
    }

    public void registerUser(String username) {
        if (checkAvailability(username)) {
            users.put(username, userIdCounter++);
        }
    }

    public List<String> suggestAlternatives(String username) {
        List<String> suggestions = new ArrayList<>();

        for (int i = 1; i <= 3; i++) {
            String suggestion = username + i;
            if (!users.containsKey(suggestion)) {
                suggestions.add(suggestion);
            }
        }

        String alt = username.replace("_", ".");
        if (!users.containsKey(alt)) {
            suggestions.add(alt);
        }

        return suggestions;
    }

    public String getMostAttempted() {
        String result = "";
        int max = 0;

        for (Map.Entry<String, Integer> entry : attempts.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                result = entry.getKey();
            }
        }

        return result + " (" + max + " attempts)";
    }

    public static void main(String[] args) {

        UsernameChecker system = new UsernameChecker();

        system.registerUser("john_doe");
        system.registerUser("admin");

        System.out.println(system.checkAvailability("john_doe"));
        System.out.println(system.checkAvailability("jane_smith"));

        System.out.println(system.suggestAlternatives("john_doe"));

        system.checkAvailability("admin");
        system.checkAvailability("admin");
        system.checkAvailability("admin");

        System.out.println(system.getMostAttempted());
    }
}