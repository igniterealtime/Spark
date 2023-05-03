package org.jivesoftware.spark.ui.themes.lafs;

import com.formdev.flatlaf.themes.FlatMacLightLaf;

public class SparkMacLightLaf  extends FlatMacLightLaf {
    public static boolean setup() {
        return setup(new SparkMacLightLaf());
    }

    @Override
    public String getName() {
        return "SparkMacLightLaf";
    }

    @Override
    public String getDescription() {
        return "SparkMacLightLaf Look and Feel";
    }
}
