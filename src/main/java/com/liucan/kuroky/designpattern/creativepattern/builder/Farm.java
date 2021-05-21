package com.liucan.kuroky.designpattern.creativepattern.builder;

import com.liucan.kuroky.designpattern.creativepattern.abstractfactory.Anima;
import com.liucan.kuroky.designpattern.creativepattern.abstractfactory.Plant;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 产品类
 *
 * @author liucan
 * @version 19-3-24
 */
@Data
@AllArgsConstructor
public class Farm {
    private final Anima anima;
    private final Plant plant;

    public static FarmBuilder cdFarmBuilder() {
        return CdFarmBuilder.builder();
    }

    public static FarmBuilder cqFarmBuilder() {
        return CqFarmBuilder.builder();
    }
}
