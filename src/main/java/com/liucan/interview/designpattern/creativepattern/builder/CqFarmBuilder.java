package com.liucan.interview.designpattern.creativepattern.builder;

import com.liucan.interview.designpattern.creativepattern.abstractfactory.Anima;
import com.liucan.interview.designpattern.creativepattern.abstractfactory.PigAnima;
import com.liucan.interview.designpattern.creativepattern.abstractfactory.Plant;
import com.liucan.interview.designpattern.creativepattern.abstractfactory.VegetablesPlant;

/**
 * @author liucan
 * @version 19-3-24
 */
public class CqFarmBuilder extends FarmBuilder {

    private Anima anima;
    private Plant plant;

    public static FarmBuilder builder() {
        return new CqFarmBuilder();
    }

    @Override
    public FarmBuilder buildAnima() {
        anima = new PigAnima();
        return this;
    }

    @Override
    public FarmBuilder buildPlant() {
        plant = new VegetablesPlant();
        return this;
    }

    @Override
    public Farm build() {
        farm = new Farm(anima, plant);
        return farm;
    }
}
