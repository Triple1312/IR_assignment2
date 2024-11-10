

import org.apache.lucene.document.Document;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class Main {


    public static void analyseResults(String filepath) throws FileNotFoundException {
        MiniDataFrame df = new MiniDataFrame(filepath);
        MiniDataFrame relevants = new MiniDataFrame("dev_query_results.csv");
        List<List<String>> translatedPredictions = new ArrayList<>();
        for (List<String> row : df.data) {
            translatedPredictions.add(row.subList(1, row.size()));
        }

        List<List<String>> translateedRelevants = new ArrayList<>();
        List<String> tmp = new ArrayList<>();
        String index = relevants.data.get(0).get(0);
        for (List<String> row : relevants.data) {
            if (row.get(0).equals(index)) {
                tmp.add(row.get(1));
            }
            else {
                translateedRelevants.add(tmp);
                tmp = new ArrayList<>();
                tmp.add(row.get(1));
                index = row.get(0);
            }
        }
        translateedRelevants.add(tmp);

        System.out.println("Map@1: " + SystemEvaluator.mean_average_precision(translateedRelevants, df.data, 1));
        System.out.println("Map@3: " + SystemEvaluator.mean_average_precision(translateedRelevants, df.data, 3));
        System.out.println("Map@5: " + SystemEvaluator.mean_average_precision(translateedRelevants, df.data, 5));
        System.out.println("Map@10: " + SystemEvaluator.mean_average_precision(translateedRelevants, df.data, 10));

        System.out.println("Mar@1: " + SystemEvaluator.mean_average_recall(translateedRelevants, df.data, 1));
        System.out.println("Mar@3: " + SystemEvaluator.mean_average_recall(translateedRelevants, df.data, 3));
        System.out.println("Mar@5: " + SystemEvaluator.mean_average_recall(translateedRelevants, df.data, 5));
        System.out.println("Mar@10: " + SystemEvaluator.mean_average_recall(translateedRelevants, df.data, 10));

    }


    public static void main(String[] args) throws Exception {
//        analyseResults("tmp_results.csv");
        RetrievalSystem retrievalSystem = new IndexRetrievalSystem();
        retrievalSystem.loadIndex("large_dataset");
        try {
            retrievalSystem.fit_corpus("large_dataset");
            System.out.println("Finished indexing");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        MiniDataFrame df = new MiniDataFrame("dev_queries.tsv", '\t');
        List<String[]> results = new ArrayList<>();

        StringBuilder sb = new StringBuilder();
        sb.append("query_id,doc1,doc2,doc3,doc4,doc5,doc6,doc7,doc8,doc9,doc10\n");
        int quindex = 0;
        for (List<String> row : df.data) {
            try {
                String q = row.get(1).replace("*"," ").replace("?"," ").replace(":"," ").replace("\""," ").replace("\\"," ").replace("/"," ").replace("|"," ").replace("<"," ").replace(">"," ").replace("\n", " ").replace("\r", " ").replace("\t", " ").replace("(", " ").replace(")", " ").replace("&", " ").replace("$", " ");
                List<Map.Entry<Document,Float>> query_results = retrievalSystem.query_bm(q, 10, 3F, 0.5F);
                List<String> filenames = new ArrayList<>();
                for (int i = 0; i < query_results.size(); i++) {
                    filenames.add(query_results.get(i).getKey().get("id").split("_")[1].split("\\.")[0]);
                }


                sb.append(row.get(0))
                        .append(",").append(filenames.get(0))
                        .append(",").append(filenames.get(1))
                        .append(",").append(filenames.get(2))
                        .append(",").append(filenames.get(3))
                        .append(",").append(filenames.get(4))
                        .append(",").append(filenames.get(5))
                        .append(",").append(filenames.get(6))
                        .append(",").append(filenames.get(7))
                        .append(",").append(filenames.get(8))
                        .append(",").append(filenames.get(9))
                        .append("\n");



            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            quindex++;
            if (quindex % 100 == 0) {
                System.out.println("Finished query " + quindex);
            }
        }
        FileWriter tmp_reults = new FileWriter("tmp_results.csv");
        tmp_reults.write(sb.toString());
        tmp_reults.close();
        analyseResults("tmp_results.csv");

    }

    void createResults() throws IOException {
        MiniDataFrame df = new MiniDataFrame("tmp_results.csv");
        StringBuilder sb = new StringBuilder();
        for (List<String> row : df.data) {
            sb.append(row.get(0)).append(",").append(row.get(1)).append("\n");
            sb.append(row.get(0)).append(",").append(row.get(2)).append("\n");
            sb.append(row.get(0)).append(",").append(row.get(3)).append("\n");
            sb.append(row.get(0)).append(",").append(row.get(4)).append("\n");
            sb.append(row.get(0)).append(",").append(row.get(5)).append("\n");
            sb.append(row.get(0)).append(",").append(row.get(6)).append("\n");
            sb.append(row.get(0)).append(",").append(row.get(7)).append("\n");
            sb.append(row.get(0)).append(",").append(row.get(8)).append("\n");
            sb.append(row.get(0)).append(",").append(row.get(9)).append("\n");
            sb.append(row.get(0)).append(",").append(row.get(10)).append("\n");
        }
        FileWriter writer = new FileWriter("results.csv");
        writer.write(sb.toString());
    }

}