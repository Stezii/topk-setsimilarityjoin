import java.util.ArrayList;
import java.util.Objects;

public class Verify {

    public static int[] verifiy_sim(ArrayList<Integer> record, ArrayList<Integer> indrecord, int minoverlap, int foundoverlap,
                              int recpos, int indrecpos) {
        int reclen = record.size();
        int indreclen = indrecord.size();
        int maxrec = reclen - recpos + foundoverlap;
        int maxindrec = indreclen - indrecpos + foundoverlap;

        int nextposrec = -1;
        int nextposindrec = -1;

        while (maxrec >= minoverlap && maxindrec >= minoverlap && foundoverlap < minoverlap) {
            if (Objects.equals(record.get(recpos), indrecord.get(indrecpos))) {
                if (nextposrec == -1) {
                    nextposrec = recpos;
                    nextposindrec = indrecpos;
                }
                recpos++;
                indrecpos++;
                foundoverlap++;
            } else if (record.get(recpos) < indrecord.get(indrecpos)) {
                recpos++;
                maxrec--;
            } else {
                indrecpos++;
                maxindrec--;
            }
        }

        if (foundoverlap < minoverlap)
            return new int[]{0, -1, -1};

        while (recpos < reclen && indrecpos < indreclen) {
            if (Objects.equals(record.get(recpos), indrecord.get(indrecpos))) {
                if (nextposrec == -1) {
                    nextposrec = recpos;
                    nextposindrec = indrecpos;
                }
                recpos++;
                indrecpos++;
                foundoverlap++;
            } else if (record.get(recpos) < indrecord.get(indrecpos)) {
                recpos++;
            } else {
                indrecpos++;
            }
        }

        return new int[]{foundoverlap, nextposrec, nextposindrec};
    }
}
