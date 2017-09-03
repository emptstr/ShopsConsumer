package shpe.util;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by jordan on 6/22/17.
 */
public class IndexList implements Iterable<Integer>{

    private final List<Integer> indexList;

    public IndexList(List<Integer> indexList) {
        this.indexList = indexList;
    }

    public Iterator<Integer> iterator() {
        return null;
    }


    private class IndexListIterator implements Iterator<Integer>{

        private final List<Integer> indexList;
        private int listSize = 0;
        private int currentPosition = 0;

        private IndexListIterator(List<Integer> indexList) {
            this.indexList = indexList;
        }

        @Override
        public boolean hasNext() {
            if(hasChanged()){
                throw new ConcurrentModificationException("Index List was Modified");
            }
            return currentPosition < listSize;
        }

        @Override
        public Integer next() {
            if(hasChanged()){
                throw new ConcurrentModificationException("Index List was Modified");
            }
            currentPosition++;
            return indexList.get(currentPosition);
        }

        private boolean hasChanged(){
            return listSize == indexList.size();
        }
    }
}
