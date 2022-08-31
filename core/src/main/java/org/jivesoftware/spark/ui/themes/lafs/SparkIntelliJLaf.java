package org.jivesoftware.spark.ui.themes.lafs;

import com.formdev.flatlaf.FlatIntelliJLaf;

public class SparkIntelliJLaf extends FlatIntelliJLaf {

    public static boolean setup() {
        return setup(new SparkIntelliJLaf());
    }
    @Override
    public String getName() {
        return "SparkIntelliJLaf";
    }

    @Override
    public String getDescription() {
        return "SparkIntelliJLaf Look and Feel";
    }
}
