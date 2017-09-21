import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static similarity_computing.set_similarity_join.Verify.verifiy_sim;

public class TopkLocal {

    private final ArrayList<Object[]> results;
    private final JaccardTopK similarity;
    private final LinkedHashMap<Integer, ArrayList<Integer>> records;

    public TopkLocal(LinkedHashMap<Integer, ArrayList<Integer>> records, JaccardTopK similarity, ArrayList<Object[]> results) {
        this.records = records;
        this.similarity = similarity;
        this.results = results;
    }

    public void run() {
        InvertedIndex ind = new InvertedIndex();
        final AtomicInteger[] candcount = {new AtomicInteger(0)};

        // <recordid1, recordid2>
        Set<ArrayList<Integer>> testonce = ConcurrentHashMap.newKeySet();

        ArrayList<SortedList<Object[]>> tmpresultslist = new ArrayList<>();

        records.forEach((recordid, tokens) -> tokens.forEach(token -> {
            ind.add(token, new int[] {recordid});
        }));

        records.entrySet().forEach(el ->  {
            Integer recordid = el.getKey();
            ArrayList<Integer> tokens = el.getValue();
            int reclen = tokens.size();

            // <simval, recordid1, recordid2>
            SortedList<Object[]> tmpresults = new SortedList<>((o1, o2) -> {
                if ((Double)o1[0] > (Double)o2[0])
                    return -1;
                else if (Math.abs((Double) o1[0]-(Double) o2[0])<1E-13)
                    return 0;
                else
                    return 1;
            });

            double thres = getThres(tmpresults, similarity.k);

            for (ListIterator<Integer> iter = tokens.listIterator(); iter.hasNext(); ) {
                int tokenpos = iter.nextIndex();
                int token = iter.next();
                ListHead indheader = ind.getHeader(token);
                List<int[]> indlist = indheader.getInvlist();

                // SimilarityUpperBound-Probe
                double thres_p = similarity.upperbound_probe(reclen, tokenpos);
                if (thres_p <= thres)
                    break;

                for (int[] indrecordpair : indlist) {
                    int indrecid = indrecordpair[0];
                    if (recordid == indrecid)
                        continue;
                    ArrayList<Integer> indexedrecord = records.get(indrecid);
                    int indreclen = indexedrecord.size();

                    ArrayList<Integer> testpair = new ArrayList<>(Arrays.asList(Math.min(recordid, indrecid), Math.max(recordid, indrecid)));
                    if (!testonce.contains(testpair)) {
                        candcount[0].incrementAndGet();

                        int minoverlap = similarity.minoverlap(reclen, indreclen, thres);

                        // <sim_overlap, nextposrec, nextposindrec>
                        int[] verified = verifiy_sim(tokens, indexedrecord, minoverlap, 0, 0, 0);

                        if (verified[0] > 0) {
                            double sim_val = similarity.computesim(reclen, indreclen, verified[0]);

                            tmpresults.add(new Object[]{sim_val, recordid, indrecid});
                            testonce.add(testpair);

   //                         if (recordid >= 2712) {
      //                          System.out.println(sim_val);
        //                    }

                            // SimilarityUpperBound-Probe
                            thres = getThres(tmpresults, similarity.k);
                            if (thres_p <= thres)
                                break;
                        }
                    }
                }
            }

            if (tmpresults.size() >= similarity.k)
                tmpresults.subList(similarity.k, tmpresults.size()).clear();

            tmpresultslist.add(tmpresults);
        });

        tmpresultslist.forEach(tmpresults -> results.addAll(tmpresults.subList(0, tmpresults.size() < similarity.k ? tmpresults.size() : similarity.k)));
        System.out.println("SSJ Candidates: " + candcount[0]);
        System.out.println("SSJ Result Count: " + results.size());
    }

    private double getThres(SortedList<Object[]> tmpresults, int k) {
        if (tmpresults.size() >= k)
            return (double)tmpresults.get(k-1)[0];
        return 0.0;
    }
}
