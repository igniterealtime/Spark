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
package org.jivesoftware.spark.translator;

import net.suuft.libretranslate.Language;
import net.suuft.libretranslate.Translator;
import org.apache.commons.lang3.ArrayUtils;

import javax.swing.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A utility class that uses google's translation service to translate text to various languages.
 */
public class TranslatorUtil {
    private TranslatorUtil() {

    }

    public static String translate(String text, Language language) {
        if (language == Language.NONE) {
            return text;
        }

        return Translator.translate(language,text);
    }

    public static Object[] getLanguage(){
        Object[] none = Arrays.stream(Language.values()).filter( x -> x.equals(Language.NONE)).toArray();
        Object[] languageList = Arrays.stream(Language.values()).filter( x -> !x.equals(Language.NONE)).sorted(Comparator.comparing(Language::name)).toArray();
       return ArrayUtils.addAll(none,languageList);
    }

}

