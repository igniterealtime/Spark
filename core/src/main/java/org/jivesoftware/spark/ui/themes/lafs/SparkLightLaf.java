/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jivesoftware.spark.ui.themes.lafs;

import com.formdev.flatlaf.FlatLightLaf;


/**
 *
 * @author KeepToo
 */
public class SparkLightLaf extends FlatLightLaf {

    public static boolean install() {
        return install(new SparkLightLaf());
    }

    @Override
    public String getName() {
        return "SparkLightLaf";
    }

    @Override
    public String getDescription() {
        return "SparkLightLaf Look and Feel";
    }
}
