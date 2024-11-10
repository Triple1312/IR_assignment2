import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.StoredFields;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.Directory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class RetrievalSystem {

    Directory _index;
    Analyzer _analyzer;
    List<String> filenames = new ArrayList<>();


    public void fit_corpus(String path) throws Exception {
        IndexWriterConfig config = new IndexWriterConfig(_analyzer);

        IndexWriter writer = new IndexWriter(_index, config);
        int indexcount = 0;

        File folder = new File(path);
        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if (file.isFile()  && file.getName().endsWith(".txt")) {
                indexcount++;
                BufferedReader reader = new BufferedReader(new FileReader(file));
                StringBuilder contentBuilder = new StringBuilder(); // just to save space
                String line;
                while ((line = reader.readLine()) != null) {
                    contentBuilder.append(line).append("\n");
                }
                reader.close();
                Document doc = new Document();
                doc.add(new TextField("content", contentBuilder.toString(), Field.Store.YES));
                doc.add(new TextField("id", file.getName(), Field.Store.YES));
                filenames.add(file.getName());
                writer.addDocument(doc);
                if (indexcount % 1000 == 0) {
                    System.out.println("Indexed " + indexcount + " files");
                }
            }
        }
        writer.close();
    }

    public void loadIndex(String path) throws Exception {

    }

    public List<Map.Entry<Document, Float>> query(String queryInput, int k) throws Exception {
        List<Map.Entry<Document, Float>> ret = new ArrayList<>();

        try (DirectoryReader reader = DirectoryReader.open(_index)){
            IndexSearcher searcher = new IndexSearcher(reader);
            QueryParser parser = new QueryParser("content", _analyzer);
            Query query = parser.parse(queryInput);
            TopDocs topDocs = searcher.search(query, k);
            StoredFields fields = searcher.storedFields();
            for (int i = 0; i < topDocs.scoreDocs.length; i++) {
                Document doc = fields.document(topDocs.scoreDocs[i].doc);
                ret.add(Map.entry(doc, topDocs.scoreDocs[i].score));
            }
            return ret;
        } catch (IOException e) {
            throw new FileNotFoundException("Index not found");
        }
    }

    public List<Map.Entry<Document, Float>> query_vsm(String queryInput, int k) throws Exception {
        List<Map.Entry<Document, Float>> ret = new ArrayList<>();

        try (DirectoryReader reader = DirectoryReader.open(_index)){
            IndexSearcher searcher = new IndexSearcher(reader);
            searcher.setSimilarity(new ClassicSimilarity());
            QueryParser parser = new QueryParser("content", _analyzer);
            Query query = parser.parse(queryInput);
            TopDocs topDocs = searcher.search(query, k);
            StoredFields fields = searcher.storedFields();
            for (int i = 0; i < topDocs.scoreDocs.length; i++) {
                Document doc = fields.document(topDocs.scoreDocs[i].doc);
                ret.add(Map.entry(doc, topDocs.scoreDocs[i].score));
            }
            return ret;
        } catch (IOException e) {
            throw new FileNotFoundException("Index not found");
        }
    }

    public List<Map.Entry<Document, Float>> query_bm(String queryInput, int k, float k1, float b) throws Exception {
        List<Map.Entry<Document, Float>> ret = new ArrayList<>();

        try (DirectoryReader reader = DirectoryReader.open(_index)){
            IndexSearcher searcher = new IndexSearcher(reader);
            searcher.setSimilarity(new BM25Similarity(k1, b));
            QueryParser parser = new QueryParser("content", _analyzer);
            Query query = parser.parse(queryInput);
            TopDocs topDocs = searcher.search(query, k);
            StoredFields fields = searcher.storedFields();
            for (int i = 0; i < topDocs.scoreDocs.length; i++) {
                Document doc = fields.document(topDocs.scoreDocs[i].doc);
                ret.add(Map.entry(doc, topDocs.scoreDocs[i].score));
            }
            return ret;
        } catch (IOException e) {
            throw new FileNotFoundException("Index not found");
        }
    }

    public List<Map.Entry<Document, Float>> query(String queryInput) throws Exception {
        return query(queryInput, 10);
    }

    public void addDocument(Document doc) {
        IndexWriterConfig config = new IndexWriterConfig(_analyzer);
        try (IndexWriter writer = new IndexWriter(_index, config)) {
            writer.addDocument(doc);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addDocument(String name, String content) {
        Document doc = new Document();
        doc.add(new TextField("id", name, Field.Store.YES));
        doc.add(new TextField("content", content, Field.Store.YES));
        addDocument(doc);
    }


}
