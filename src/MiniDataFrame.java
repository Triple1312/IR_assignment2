import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


// it's a dataframe but mini without all the features
public class MiniDataFrame {
    List<String> headers = new ArrayList<>(); // list of column names
    List<List<String>> data = new ArrayList<>(); // list of rows

    MiniDataFrame(String filepath) throws FileNotFoundException {
        this(filepath, ',');
    }

    MiniDataFrame(String filepath, Character sep) throws FileNotFoundException {
        File file = new File(filepath);
        Scanner reader = new Scanner(file);
        headers = List.of(reader.nextLine().split(sep.toString()));
        while(reader.hasNextLine()) {
            data.add(List.of(reader.nextLine().split(sep.toString())));
        }
    }
}
