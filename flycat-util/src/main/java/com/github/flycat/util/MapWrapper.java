/**
 * Copyright 2019 zgqq <zgqjava@gmail.com>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.flycat.util;


import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Map;

public class MapWrapper {
    private final Map map;

    public MapWrapper(Map map) {
        this.map = map;
    }

    public String getString(String key) {
        if (map != null) {
            Object answer = map.get(key);
            if (answer != null) {
                if (answer instanceof Object[]) {
                    return ((Object[]) answer)[0].toString();
                }
                return answer.toString();
            }
        }
        return null;
    }

    public String getString(String key, String defaultValue) {
        String value = getString(key);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }


    public static Number getNumber(Map map, Object key) {
        if (map != null) {
            Object answer = map.get(key);
            if (answer != null) {
                if (answer instanceof Object[]) {
                    answer = ((Object[]) answer)[0];
                }

                if (answer instanceof Number) {
                    return (Number)answer;
                }
                if (answer instanceof String) {
                    try {
                        String text = (String)answer;
                        return NumberFormat.getInstance().parse(text);
                    } catch (ParseException var4) {
                    }
                }
            }
        }

        return null;
    }

    public Integer getInteger(String key) {
        Number answer = getNumber(map, key);
        if (answer == null) {
            return null;
        } else {
            return answer instanceof Integer ? (Integer)answer : new Integer(answer.intValue());
        }
    }

    public Integer getInteger(String key, Integer defaultValue) {
        Integer value = getInteger(key, defaultValue);
        if (value == null) {
            value = defaultValue;
        }
        return value;
    }

    public Map getMap() {
        return map;
    }
}
