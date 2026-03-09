import java.util.*;

public class FlashSaleInventoryManager {

    private HashMap<String, Integer> stock = new HashMap<>();
    private HashMap<String, Queue<Integer>> waitingList = new HashMap<>();

    public FlashSaleInventoryManager() {
        stock.put("IPHONE15_256GB", 100);
        waitingList.put("IPHONE15_256GB", new LinkedList<>());
    }

    public synchronized void checkStock(String productId) {
        int available = stock.getOrDefault(productId, 0);
        System.out.println(productId + " → " + available + " units available");
    }

    public synchronized void purchaseItem(String productId, int userId) {

        int available = stock.getOrDefault(productId, 0);

        if (available > 0) {
            stock.put(productId, available - 1);
            System.out.println("User " + userId + " → Success, " + (available - 1) + " units remaining");
        } else {
            Queue<Integer> queue = waitingList.get(productId);
            queue.add(userId);
            System.out.println("User " + userId + " → Added to waiting list, position #" + queue.size());
        }
    }

    public static void main(String[] args) {

        FlashSaleInventoryManager manager = new FlashSaleInventoryManager();

        manager.checkStock("IPHONE15_256GB");

        manager.purchaseItem("IPHONE15_256GB", 12345);
        manager.purchaseItem("IPHONE15_256GB", 67890);

        for (int i = 0; i < 100; i++) {
            manager.purchaseItem("IPHONE15_256GB", i);
        }

        manager.purchaseItem("IPHONE15_256GB", 99999);
    }
}