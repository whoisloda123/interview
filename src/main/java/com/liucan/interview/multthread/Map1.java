package com.liucan.interview.multthread;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * @author liucan
 * @version 19-3-3
 */
public class Map1 {

    private void test() {
        //采用分段锁
        ConcurrentHashMap<Integer, String> concurrentHashMap = new ConcurrentHashMap<>();
        concurrentHashMap.put(1, "2");

        //用法和NavigableMap差不多，导航map，可以返回大于、等于key的Entry
        ConcurrentNavigableMap<Integer, String> concurrentNavigableMap = new ConcurrentSkipListMap<>();
        concurrentHashMap.put(1, "2");
        concurrentHashMap.put(2, "3");
    }
}
