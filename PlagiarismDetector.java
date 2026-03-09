import java.util.*;

public class PlagiarismDetector {

    private Map<String, Set<String>> index = new HashMap<>();
    private Map<String, List<String>> documents = new HashMap<>();
    private int n = 5;

    public void addDocument(String docId, String text) {
        List<String> words = tokenize(text);
        documents.put(docId, words);

        for (int i = 0; i <= words.size() - n; i++) {
            String gram = buildGram(words, i);
            index.computeIfAbsent(gram, k -> new HashSet<>()).add(docId);
        }
    }

    public void analyzeDocument(String docId) {
        List<String> words = documents.get(docId);

        Map<String, Integer> matchCount = new HashMap<>();

        for (int i = 0; i <= words.size() - n; i++) {
            String gram = buildGram(words, i);
            Set<String> docs = index.getOrDefault(gram, new HashSet<>());

            for (String other : docs) {
                if (!other.equals(docId)) {
                    matchCount.put(other, matchCount.getOrDefault(other, 0) + 1);
                }
            }
        }

        int totalGrams = words.size() - n + 1;
        System.out.println("Extracted " + totalGrams + " n-grams");

        for (Map.Entry<String, Integer> e : matchCount.entrySet()) {
            double similarity = (e.getValue() * 100.0) / totalGrams;
            System.out.println("Found " + e.getValue() + " matching n-grams with " + e.getKey());
            System.out.println("Similarity: " + String.format("%.2f", similarity) + "%");
        }
    }

    private List<String> tokenize(String text) {
        return Arrays.asList(text.toLowerCase().split("\\s+"));
    }

    private String buildGram(List<String> words, int start) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < start + n; i++) {
            sb.append(words.get(i)).append(" ");
        }
        return sb.toString().trim();
    }

    public static void main(String[] args) {

        PlagiarismDetector detector = new PlagiarismDetector();

        detector.addDocument("essay_089.txt",
                "data structures and algorithms are important concepts in computer science");

        detector.addDocument("essay_092.txt",
                "data structures and algorithms are important concepts in computer science and software engineering");

        detector.addDocument("essay_123.txt",
                "data structures and algorithms are important concepts in computer science for students");

        detector.analyzeDocument("essay_123.txt");
    }
}