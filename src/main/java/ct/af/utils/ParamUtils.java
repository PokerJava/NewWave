package ct.af.utils;

import com.google.gson.internal.LinkedTreeMap;
import ec02.af.utils.AFLog;
import java.util.*;

public class ParamUtils {
    public static void showMap(HashMap<String, Object> map) {
        AFLog.d("============================ START BLOCK ===================================");

        for (Map.Entry entry : map.entrySet()) {
            AFLog.d("class : "+entry.getValue().getClass()+" | key : "+entry.getKey());
            if (entry.getValue() instanceof String) {
                AFLog.d("key : "+ entry.getKey() + " | value : "+entry.getValue());
            } else if (entry.getValue() instanceof  ArrayList) {
                for (Object object : (ArrayList)entry.getValue()) {
                    AFLog.d("class : "+object.getClass());
                    if (object instanceof HashMap) {
                        showMap((HashMap<String, Object>) object);
                    } else if (object instanceof String) {
                        AFLog.d("class : "+entry.getValue().getClass()+" | key : "+object);
                    }

                }
            } else if (entry.getValue() instanceof HashMap) {
                showMap((HashMap<String, Object>)entry.getValue());
            }
        }
//        AFLog.d("============================ END BLOCK ==================================="+"\n");
    }

    public static void parseInternalLayer(HashMap<String, Object> param) {
        for (Map.Entry resourceItem : param.entrySet()) {
            checkAndParseInternalLayer(param,resourceItem.getKey().toString());
        }
    }

    private static void checkAndParseInternalLayer(HashMap<String, Object> data, String key) {
        Object obj = data.get(key);

        if (obj instanceof ArrayList) {
            List<Object> internalList = new ArrayList<>();
            for (Object dataObj : (ArrayList)obj) {
                if (dataObj instanceof LinkedTreeMap) {
                    // get all key in LinkedTreeMap
                    Set<String> keySet = ((LinkedTreeMap) dataObj).keySet();
                    HashMap<String, Object> internalMap = new HashMap<>();

                    // set HashMap
                    for (String keyStr : keySet) {
                        internalMap.put(keyStr, ((LinkedTreeMap) dataObj).get(keyStr));
                    }

                    // check internal layer
                    for (HashMap.Entry entry : internalMap.entrySet()) {
                        checkAndParseInternalLayer(internalMap, entry.getKey().toString());
                    }
                    internalList.add(internalMap);
                }
                else if (dataObj instanceof String) {
                    internalList.add(dataObj.toString().trim());
                }
            }
            data.put(key, internalList);
        } else if (obj instanceof LinkedTreeMap) {
            // get all key in LinkedTreeMap
            Set<String> keySet = ((LinkedTreeMap) obj).keySet();
            HashMap<String, Object> internalMap = new HashMap<>();

            // set HashMap
            for (String keyStr : keySet) {
                internalMap.put(keyStr, ((LinkedTreeMap) obj).get(keyStr));
            }

            // check internal layer
            for (HashMap.Entry entry : internalMap.entrySet()) {
                checkAndParseInternalLayer(internalMap, entry.getKey().toString());
            }
            data.put(key, internalMap);
        } else if (obj instanceof String ){
            data.put(key, obj.toString().trim());
        }
    }
}