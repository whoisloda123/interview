package com.liucan.interview.designpattern.actionpattern.iterator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liucan
 * @version 19-3-31
 */
public class Aggregate {
    private List<Object> list = new ArrayList<>();

    public void add(Object obj) {
        list.add(obj);
    }

    public void remove(Object obj) {
        list.remove(obj);
    }

    public Iterator iterator() {
        return new AppreagetIterator(list);
    }

    private class AppreagetIterator implements Iterator {
        private List<Object> list;
        private int index = -1;

        public AppreagetIterator(List<Object> list) {
            this.list = list;
        }

        @Override
        public Object next() {
            if (!hasNext()) {
                return null;
            }
            return list.get(++index);
        }

        @Override
        public boolean hasNext() {
            return index < list.size() - 1;
        }
    }
}
