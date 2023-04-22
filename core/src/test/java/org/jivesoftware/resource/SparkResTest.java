package org.jivesoftware.resource;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class SparkResTest extends TestCase {

    @Test
    public void testSparkRes() {
        Properties prb = new SparkRes().getPrb();
        HashMap<String,String> map = new HashMap<>();
        Enumeration<String> enumeration = (Enumeration<String>) prb.propertyNames();
        
        while (enumeration.hasMoreElements()) {
            String token = enumeration.nextElement();
            String value = prb.getProperty(token).toLowerCase();
            if (value.endsWith(".gif") || value.endsWith(".png") || value.endsWith(".jpg") || value.endsWith("jpeg")) {
                map.put(token,value);
            }
        }

        for(Map.Entry<String,String> entry : map.entrySet()){
            if(!Files.exists(Paths.get(entry.getValue()))){
                Assert.fail(String.format("Variable with name=%s and value=%s does not exist",entry.getKey(),entry.getValue()));
            }
        }
    }

}
