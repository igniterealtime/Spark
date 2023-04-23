package org.jivesoftware.resource;

import junit.framework.TestCase;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SparkResTest extends TestCase {

    final String path = System.getProperty("user.dir") + "/src/main/resources/images";

    @Test
    public void testCheckifVariableIsExist() {
        ClassLoader cl = getClass().getClassLoader();
        StringBuilder sb = new StringBuilder();
        int numberOfLines = 0;

        for(Map.Entry<String,String> entry : getMapWithProperty().entrySet()){
            try {
                if(!Files.exists(Paths.get(Objects.requireNonNull(cl.getResource(entry.getValue())).toURI()))){
                    throw new RuntimeException();
                }
            } catch (Exception e){
                sb.append(String.format("Variable with name=%s and value=%s does not exist\n",entry.getKey(),entry.getValue()));
                numberOfLines++;
            }
        }
        if(StringUtils.isNotBlank(sb.toString())){
            Assert.fail(String.format("Number of lines is %d\n", numberOfLines )+ sb);
        }
    }

    @Test
    public void testCheckIfTokenNotRepeated() {
        StringBuilder sb = new StringBuilder("\n");
        Map<String,Long> values =  getMapWithProperty().values().stream().collect(Collectors.groupingBy(Function.identity(),Collectors.counting()));
        int numberOfLines = 0;

        for(Map.Entry<String,Long> entry : values.entrySet()){
            if(entry.getValue() > 1){
                sb.append(String.format("Property with value=%s used %d times\n",entry.getKey(),entry.getValue()));
                numberOfLines++;
            }
        }

        if(StringUtils.isNotBlank(sb.toString())){
            Assert.fail(String.format("Number of lines is %d\n", numberOfLines )+ sb);
        }
    }

    @Test
    public void testCheckIfImageIsNotUsed(){
        StringBuilder sb = new StringBuilder("\n");
        final int[] numberOfLines = {0};

        getListPathProperty().stream().filter(x -> !x.contains("emoticons")).forEach(x -> {
            if(!getMapWithProperty().containsValue(x)){
                sb.append(String.format("File with path=%s not used in property\n",x));
                numberOfLines[0]++;
            }
        });

        if(StringUtils.isNotBlank(sb.toString())){
            Assert.fail(String.format("Number of lines is %d\n", numberOfLines[0])+ sb);
        }
    }

    @Test
    public void testCheckIfOldPictureUsedInProperty(){
        StringBuilder sb = new StringBuilder("\n");
        int numberOfLines = 0;
        List<String> listOldFiles = new ArrayList<>();
        Arrays.asList((new File(path).listFiles())).stream().forEach(x -> listOldFiles.add(x.toString().split("resources+\\\\")[1].replace("\\","/")));

        for(String val : getMapWithProperty().values()){
            if (listOldFiles.contains(val)){
                sb.append(String.format("Old file with path=%s uses in property\n",val));
                numberOfLines++;
            }
        }

        if(StringUtils.isNotBlank(sb.toString())){
            Assert.fail(String.format("Number of lines is %d\n", numberOfLines )+ sb);
        }
    }

    private List<String> getListPathProperty(){
        List<String> pathList = new ArrayList<>();

        try (Stream<Path> paths = Files.walk(Paths.get(path))) {
            paths
                .filter(Files::isRegularFile)
                .forEach(x -> pathList.add(x.toString().split("resources+\\\\")[1].replace("\\","/")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return pathList;
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
