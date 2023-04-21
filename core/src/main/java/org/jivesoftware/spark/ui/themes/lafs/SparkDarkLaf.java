package org.jivesoftware.spark.ui.themes.lafs;

import com.formdev.flatlaf.FlatDarkLaf;

public class SparkDarkLaf extends FlatDarkLaf {

    public static boolean setup() {
        return setup(new SparkDarkLaf());
    }

    @Override
    public String getName() {
        return "SparkDarkLaf";
    }

    @Override
    public String getDescription() {
        return "SparkDarkLaf Look and Feel";
    }
}
