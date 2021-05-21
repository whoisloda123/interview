package com.liucan.kuroky.designpattern.actionpattern.strategy;

import java.util.HashMap;
import java.util.Map;

/**
 * 策略类工厂
 *
 * @author liucan
 * @version 19-3-28
 */
public class CrabCookingStrategyFactory {

    private Map<String, CrabCookingStrategy> crabCookingStrategyMap = new HashMap<>();

    public CrabCookingStrategyFactory() {
        crabCookingStrategyMap.put("braisedCrabs", new BraisedCrabsStragegy());
        crabCookingStrategyMap.put("steamedCrabs", new SteamedCrabsStragegy());
    }

    public CrabCookingStrategy getCrabCookingStrategy(String name) {
        return crabCookingStrategyMap.get(name);
    }
}
