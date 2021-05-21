package com.liucan.kuroky.designpattern.creativepattern.abstractfactory;

/**
 * @author liucan
 * @version 19-3-21
 */
public class CqFarmFactory implements FarmFactory {
    @Override
    public Anima newAnima() {
        return new PigAnima();
    }

    @Override
    public Plant newPlant() {
        return new VegetablesPlant();
    }
}
