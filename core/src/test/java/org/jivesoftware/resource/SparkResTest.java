package org.jivesoftware.resource;

import junit.framework.TestCase;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SparkResTest extends TestCase {

    @Test
    public void testSparkRes() {
        ClassLoader cl = getClass().getClassLoader();
        StringBuilder sb = new StringBuilder("\n");

        for(Map.Entry<String,String> entry : getMapWithProperty().entrySet()){
            try {
                if(!Files.exists(Paths.get(Objects.requireNonNull(cl.getResource(entry.getValue())).toURI()))){
                    throw new RuntimeException();
                }
            } catch (Exception e){
                sb.append(String.format("Variable with name=%s and value=%s does not exist\n",entry.getKey(),entry.getValue()));
            }
        }
        if(StringUtils.isNotBlank(sb.toString())){
            Assert.fail(sb.toString());
        }
    }

    @Test
    public void testCheckIfTokenNotRepeated() {
        StringBuilder sb = new StringBuilder("\n");
        Map<String,Long> values =  getMapWithProperty().values().stream().collect(Collectors.groupingBy(Function.identity(),Collectors.counting()));

        for(Map.Entry<String,Long> entry : values.entrySet()){
            if(entry.getValue() > 1){
                sb.append(String.format("Property with value=%s used %d times\n",entry.getKey(),entry.getValue()));
            }
        }

        if(StringUtils.isNotBlank(sb.toString())){
            Assert.fail(sb.toString());
        }
    }

    private HashMap<String,String> getMapWithProperty(){
        Properties prb = new SparkRes().getPrb();
        HashMap<String,String> map = new HashMap<>();
        Enumeration<String> enumeration = (Enumeration<String>) prb.propertyNames();

        while (enumeration.hasMoreElements()) {
            String token = enumeration.nextElement();
            String value = prb.getProperty(token).toLowerCase();
            if (value.endsWith(".gif") || value.endsWith(".png") || value.endsWith(".jpg") || value.endsWith(".jpeg")) {
                map.put(token,value);
            }
        }
        return map;
    }

}
