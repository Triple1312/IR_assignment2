
import org.apache.lucene.analysis.en.EnglishAnalyzer;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.store.FSDirectory;

import java.nio.file.Paths;


public class IndexRetrievalSystem extends RetrievalSystem {

    IndexRetrievalSystem() {
        _analyzer = new EnglishAnalyzer();
    }

    @Override
    public void fit_corpus(String path) throws Exception {
        _index = FSDirectory.open(Paths.get(path));
        super.fit_corpus(path);
    }

    @Override
    public void loadIndex(String path) throws Exception {
        _index = FSDirectory.open(Paths.get(path));
    }


}
