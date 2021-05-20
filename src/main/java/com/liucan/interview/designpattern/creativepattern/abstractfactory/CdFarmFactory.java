package com.liucan.interview.designpattern.creativepattern.abstractfactory;

/**
 * @author liucan
 * @version 19-3-21
 */
public class CdFarmFactory implements FarmFactory {
    @Override
    public Anima newAnima() {
        return new HorseAnima();
    }

    @Override
    public Plant newPlant() {
        return new FruitagePlant();
    }
}
