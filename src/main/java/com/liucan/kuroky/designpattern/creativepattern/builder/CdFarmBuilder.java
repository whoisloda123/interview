package com.liucan.kuroky.designpattern.creativepattern.builder;

import com.liucan.kuroky.designpattern.creativepattern.abstractfactory.Anima;
import com.liucan.kuroky.designpattern.creativepattern.abstractfactory.FruitagePlant;
import com.liucan.kuroky.designpattern.creativepattern.abstractfactory.HorseAnima;
import com.liucan.kuroky.designpattern.creativepattern.abstractfactory.Plant;

/**
 * @author liucan
 * @version 19-3-24
 */
public class CdFarmBuilder extends FarmBuilder {

    private Anima anima;
    private Plant plant;

    public static FarmBuilder builder() {
        return new CdFarmBuilder();
    }

    @Override
    public FarmBuilder buildAnima() {
        anima = new HorseAnima();
        return this;
    }

    @Override
    public FarmBuilder buildPlant() {
        plant = new FruitagePlant();
        return this;
    }

    @Override
    public Farm build() {
        farm = new Farm(anima, plant);
        return farm;
    }
}
