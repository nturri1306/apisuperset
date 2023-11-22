package it.unidoc.cdr.api.fhir;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.hl7.fhir.instance.model.api.IBaseResource;

import java.util.*;

public class MergeUtil {

    public static List<Map<String, Object>> merge(List<Object> resourceList) {

        List<Map<String, Object>> arrayList = new ArrayList();

        for (var r : resourceList) {

            var jsonString = Util.toJsonResource((IBaseResource) r);

            Gson gson = new Gson();
            JsonObject json = gson.fromJson(jsonString, JsonObject.class);

            Map<String, Object> flattenedMap = flattenJson(json);

            arrayList.add(flattenedMap);

        }


        for (int i = 0; i < arrayList.size(); i++)
            for (int j = 0; j < arrayList.size(); j++) {
                {

                    if (i == j)
                        continue;

                    var map1 = arrayList.get(i);

                    var map2 = arrayList.get(j);


                    mergeMap(map1, map2);
                }
            }

        return arrayList;

    }


    private static void mergeMap(Map<String, Object> map1, Map<String, Object> map2) {
        for (String key : map1.keySet()) {
            if (!map2.containsKey(key)) {
                map2.put(key, "");
            }
        }
    }


    private static Map<String, Object> flattenJson(JsonObject json) {
        Map<String, Object> flattenedMap = new HashMap<>();
        flattenJsonHelper(flattenedMap, "", json);
        return flattenedMap;
    }

    private static void flattenJsonHelper(Map<String, Object> flattenedMap, String prefix, JsonElement jsonElement) {
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> entrySet = jsonObject.entrySet();

            for (Map.Entry<String, JsonElement> entry : entrySet) {
                String newKey = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
                flattenJsonHelper(flattenedMap, newKey, entry.getValue());
            }
        } else if (jsonElement.isJsonPrimitive()) {
            JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
            flattenedMap.put(prefix, jsonPrimitive.isString() ? jsonPrimitive.getAsString() : jsonPrimitive);
        }
    }
}
