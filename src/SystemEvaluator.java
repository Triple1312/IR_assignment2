import java.util.List;

public class SystemEvaluator {

    static double mean_average_precision(List<List<String>> relevant, List<List<String>> predictions, int k) {
        double precision = 0;
        for (int i = 0; i < relevant.size(); i++) {
            precision += precision(relevant.get(i), predictions.get(i), k);
        }
        return precision / relevant.size();
    }

    static double precision(List<String> relevant, List<String> predicted, int k) {
        double precision = 0;
        for (int i = 0; i < k; i++) {
            if (relevant.contains(predicted.get(i))) {
                precision++;
            }
        }
        return precision / k;
    }

    static double recall(List<String> relevant, List<String> predictions, int k) {
        double tp = 0;
        for (int i = 0; i < k; i++) {
            if (relevant.contains(predictions.get(i))) tp++;
        }
        return tp / relevant.size();
    }

    static double mean_average_recall(List<List<String>> relevant, List<List<String>> predicted, int k) {
        double recall = 0;
        for (int i = 0; i < relevant.size(); i++) {
            recall += recall(relevant.get(i), predicted.get(i), k);
        }
        return recall / relevant.size();
    }

}
