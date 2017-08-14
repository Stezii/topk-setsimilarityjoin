import java.util.ArrayList;
import java.util.List;

public class ListHead {

    private List<int[]> invlist = new ArrayList<>();

    public List<int[]> getInvlist() {
        return invlist;
    }

    public void add(int[] value) {
        invlist.add(value);
    }

    public void cutoff_from(int position) {
        invlist = invlist.subList(position, invlist.size());
    }

}
