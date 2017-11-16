import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SetSimJoin {

    private LinkedHashMap<String, ArrayList<Integer>> records;
    private ArrayList<Object[]> results = new ArrayList<>();

    private LinkedHashMap<Integer, ArrayList<Integer>> records_internal = new LinkedHashMap<>();
    private ArrayList<Object[]> results_internal = new ArrayList<>();
    private HashMap<Integer, String> mapper = new HashMap<>();

    public SetSimJoin() {
    }

    public SetSimJoin(LinkedHashMap<String, ArrayList<Integer>> records) {
        setRecords(records);
    }

    public void topkGlobal(Integer k) {
        if (records == null)
            throw new IllegalArgumentException("Records missing");
        if (k == null)
            throw new IllegalArgumentException("k missing");

        mapRecords();
        TopkGlobal topk = new TopkGlobal(records_internal, new JaccardTopK(k), results_internal);
        topk.run();
        mapResults();
    }

    public void topkLocal(Integer k) {
        if (records == null)
            throw new IllegalArgumentException("Records missing");
        if (k == null)
            throw new IllegalArgumentException("k missing");

        mapRecords();
        TopkLocal topk = new TopkLocal(records_internal, new JaccardTopK(k), results_internal);
        topk.run();
        mapResults();
    }

    public void setRecords(LinkedHashMap<String, ArrayList<Integer>> records) {
        this.records = records;
    }

    public ArrayList<Object[]> getResults() {
        return results;
    }

    public void readFromFile(String filePath) {
        records = new LinkedHashMap<>();
        try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
            stream.forEach(lnStr -> {
                String[] ln = lnStr.split(" ", 2);
                int[] tokens = Stream.of(ln[1].split(" ")).mapToInt(Integer::parseInt).toArray();
                ArrayList<Integer> list = new ArrayList<>(Arrays.stream(tokens).boxed().collect(Collectors.toList()));
                records.put(ln[0], list);
            });
        } catch (IOException e) {
            System.out.println("Could not read from " + filePath);
            e.printStackTrace();
        }
    }

    public void saveToFile(String filePath) {
        try {
            Files.write(Paths.get(filePath), () -> results.stream().sequential().<CharSequence>map(e ->
                    e[0] +"\t"+e[1] +"\t"+e[2]).iterator());
        } catch (IOException e) {
            System.out.println("Could not write to " + filePath);
            e.printStackTrace();
        }
    }

    public void printResults() {
        results.forEach(System.out::println);
    }

    private void mapRecords() {
        final int[] internalid = {0};
        records.forEach((key, value) -> {
            records_internal.put(internalid[0], value);
            mapper.put(internalid[0]++, key);
        });
    }

    private void mapResults() {
        results_internal.forEach(e -> results.add(new Object[]{mapper.get(e[1]), mapper.get(e[2]), e[0]}));
    }
}
