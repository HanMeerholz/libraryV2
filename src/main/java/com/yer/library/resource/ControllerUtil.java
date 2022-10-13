package com.yer.library.resource;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ControllerUtil {
    public static Map<String, Object> getDataMap(String name, Object data) {
        // Java 8 equivalent of ``Map.of(test1, test2)''
        return Collections.unmodifiableMap(
                new HashMap<String, Object>() {
                    {
                        put(name, data);
                    }
                }
        );
    }
}
