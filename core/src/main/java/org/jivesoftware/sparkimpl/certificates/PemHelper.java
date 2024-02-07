package org.jivesoftware.sparkimpl.certificates;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.openssl.jcajce.JcaPEMWriter;

public class PemHelper {

    /**
     * Saves object to PEM file
     * 
     * @param object meant to be saved
     * @param file to which object should be saved
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void saveToPemFile(Object object, File file) throws FileNotFoundException, IOException{
        try (JcaPEMWriter pem = new JcaPEMWriter(new OutputStreamWriter(new FileOutputStream(file)))) {
            pem.writeObject(object);
        }
    }
    
    /**
     * This class can be used to write few object into one PEM file.
     * @author Paweł Ścibiorski
     *
     */
    public static class PemBuilder {
        private final List<Object> buildList = new ArrayList<>();

        public void add(Object object) {
            buildList.add(object);
        }

        public void saveToPemFile(File file) throws IOException {
            try (JcaPEMWriter pem = new JcaPEMWriter(new OutputStreamWriter(new FileOutputStream(file)))) {
                for (Object object : buildList) {
                    pem.writeObject(object);
                }
            }
        }
    }
    
}
