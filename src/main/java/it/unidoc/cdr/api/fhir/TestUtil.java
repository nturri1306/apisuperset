package it.unidoc.cdr.api.fhir;

import org.json.JSONObject;

public class TestUtil {

    private static void mergeObservations(JSONObject observation1, JSONObject observation2) {
        for (String key : observation2.keySet()) {

            if (!observation1.has(key)) {

                observation1.put(key, "");
            }
        }


    }
}
