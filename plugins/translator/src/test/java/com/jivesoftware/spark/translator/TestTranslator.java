/**
 * Copyright (C) 2004-2011 Jive Software. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jivesoftware.spark.translator;

import net.suuft.libretranslate.Language;
import org.jivesoftware.spark.translator.TranslatorUtil;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.*;

/**
 * A simple class to test translation functionality.
 */
public class TestTranslator {

    @Test
    public void test() {
        // Dummy test to avoid Maven confusion.
    }

    public static void main(String[] args) {
        Map<Integer, Language> translationMap = initalizeTranslationMap();

        boolean again = true;
        while (again) {
            System.out.println("Please enter some text");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String text = "";
            try {
                text = reader.readLine();
            } catch (IOException e) {
                System.out.println("Could not read text:" + e);
            }

            if (text == null) {
                System.out.println("Sorry, I didn't get that.");
                continue;
            }

            System.out.println("Great, now enter the translation id. Below are the following choices:");

            for (Map.Entry<Integer, Language> o : translationMap.entrySet()) {
                System.out.println(o.getKey() + " - " + o.getValue());
            }

            String translationID = "";
            try {
                translationID = reader.readLine();
            } catch (IOException e) {
                System.out.println("Could not read translationID:" + e);
            }

            Integer id = Integer.valueOf(translationID);
            Language type = translationMap.get(id);

            if (type == null) {
                System.out.println("Not a valid translation type.");
                continue;
            }

            String result = TranslatorUtil.translate(text, type);

            System.out.println("Your original text:\n" + text);
            System.out.println("Has been translated from: " + type);
            System.out.println("The result is:\n" + result);

            System.out.println("Do you want to continue testing?");
            String cont;
            try {
                cont = reader.readLine();
                again = "yes".equals(cont.toLowerCase().trim()) || "y".equals(cont.toLowerCase().trim());
            } catch (IOException e) {
                System.out.println("Could not read text:" + e);
            }
        }
    }

    private static Map<Integer, Language> initalizeTranslationMap() {
        Language[] types = Language.values();
        Map<Integer,Language> map = new TreeMap<>(Integer::compareTo);

        for (int i = 1; i < types.length; i++) {
            map.put(i, types[i]);
        }

        return map;
    }


    @Test public void testSort(){
        for (int i = 0; i < TranslatorUtil.getLanguage().length-1; i++) {
            System.out.println(TranslatorUtil.getLanguage()[i]);
        }
    }
}
