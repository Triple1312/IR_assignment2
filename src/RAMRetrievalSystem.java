
import org.apache.lucene.analysis.en.EnglishAnalyzer;

import org.apache.lucene.store.ByteBuffersDirectory;


public class RAMRetrievalSystem extends RetrievalSystem{

    RAMRetrievalSystem() {
        _index = new ByteBuffersDirectory();
        _analyzer = new EnglishAnalyzer(); // englishAnalyser does already tokenization, stopword removal and stemming
    }

}
