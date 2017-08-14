package similarity_computing.set_similarity_join;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

// Taken from: https://stackoverflow.com/a/12638571/8133064

/**
 * This class is a List implementation which sorts the elements using the
 * comparator specified when constructing a new instance.
 *
 * @param <T>
 */
public class SortedList<T> extends ArrayList<T> {
    /**
     * Needed for serialization.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Comparator used to sort the list.
     */
    private Comparator<? super T> comparator = null;
    /**
     * Construct a new instance with the list elements sorted in their
     * {@link java.lang.Comparable} natural ordering.
     */
    public SortedList() {
    }
    /**
     * Construct a new instance using the given comparator.
     *
     * @param comparator
     */
    public SortedList(Comparator<? super T> comparator) {
        this.comparator = comparator;
    }
    /**
     * Construct a new instance containing the elements of the specified
     * collection with the list elements sorted in their
     * {@link java.lang.Comparable} natural ordering.
     *
     * @param collection
     */
    public SortedList(Collection<? extends T> collection) {
        addAll(collection);
    }
    /**
     * Construct a new instance containing the elements of the specified
     * collection with the list elements sorted using the given comparator.
     *
     * @param collection
     * @param comparator
     */
    public SortedList(Collection<? extends T> collection, Comparator<? super T> comparator) {
        this(comparator);
        addAll(collection);
    }
    /**
     * Add a new entry to the list. The insertion point is calculated using the
     * comparator.
     *
     * @param paramT
     * @return <code>true</code> if this collection changed as a result of the call.
     */
    @Override
    public boolean add(T paramT) {
        int initialSize = this.size();
        // Retrieves the position of an existing, equal element or the
        // insertion position for new elements (negative).
        int insertionPoint = Collections.binarySearch(this, paramT, comparator);
        super.add((insertionPoint > -1) ? insertionPoint : (-insertionPoint) - 1, paramT);
        return (this.size() != initialSize);
    }
    /**
     * Adds all elements in the specified collection to the list. Each element
     * will be inserted at the correct position to keep the list sorted.
     *
     * @param paramCollection
     * @return <code>true</code> if this collection changed as a result of the call.
     */
    @Override
    public boolean addAll(Collection<? extends T> paramCollection) {
        boolean result = false;
        if (paramCollection.size() > 4) {
            result = super.addAll(paramCollection);
            Collections.sort(this, comparator);
        }
        else {
            for (T paramT:paramCollection) {
                result |= add(paramT);
            }
        }
        return result;
    }
    /**
     * Check, if this list contains the given Element. This is faster than the
     * {@link #contains(Object)} method, since it is based on binary search.
     *
     * @param paramT
     * @return <code>true</code>, if the element is contained in this list;
     * <code>false</code>, otherwise.
     */
    public boolean containsElement(T paramT) {
        return (Collections.binarySearch(this, paramT, comparator) > -1);
    }
    /**
     * @return The comparator used for sorting this list.
     */
    public Comparator<? super T> getComparator() {
        return comparator;
    }
    /**
     * Assign a new comparator and sort the list using this new comparator.
     *
     * @param comparator
     */
    public void setComparator(Comparator<? super T> comparator) {
        this.comparator = comparator;
        Collections.sort(this, comparator);
    }
}