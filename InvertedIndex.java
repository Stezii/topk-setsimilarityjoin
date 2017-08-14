import java.util.HashMap;

public class InvertedIndex {

    private HashMap<Integer, ListHead> invlist = new HashMap<>();

    public void add(int key, int[] value) {
        ListHead ilist = invlist.getOrDefault(key, new ListHead());
        ilist.add(value);
        if (ilist.getInvlist().size() == 1)
            invlist.put(key, ilist);
    }

    public ListHead getHeader(int key) {
        return invlist.getOrDefault(key, new ListHead());
    }
}
