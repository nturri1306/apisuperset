package it.unidoc.cdr.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.unidoc.cdr.api.fhir.RestFhirApi;
import it.unidoc.cdr.api.fhir.Util;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class DynamicClassCreation {

    static String fhirUrl = "http://172.16.14.191:9080/fhir-server/api/v4/";
    static String fhirUser = "fhiruser";
    static String fhirPwd = "change-password";

    public static void main(String[] args) {
        try {

            // Percorso del file JSON
            String filePath = Paths.get("src\\main\\resources\\static", "observation.json").toAbsolutePath().toString();

            // Leggere il contenuto del file JSON come stringa
            String json = readFile(filePath);

            // Creare un ObjectMapper
            ObjectMapper objectMapper = new ObjectMapper();

            // Deserializzare il JSON in una mappa chiave-valore
            Map<String, Object> jsonMap = objectMapper.readValue(json, Map.class);

            System.out.println(Util.toJson(jsonMap));


            Hashtable<String, Object> fieldsHierarchy = extractFieldsHierarchy(jsonMap, "");


            System.out.println(Util.toJson(fieldsHierarchy));



           /* Observation observation = new Observation();

            observation.setId("1234");

            Reference reference = new Reference();

            reference.setDisplay("prova");
            reference.setReference("12345");

            Coding coding = new Coding();

            coding.setSystem("http://loinc.org");
            coding.setCode("789-8");
            coding.setDisplay("Erythrocytes");

            List<Coding> codingList = new ArrayList<>();

            codingList.add(coding);

            observation.getCode().setCoding(codingList);


            observation.setSubject(reference);
 System.out.println(observationValues);

            Map<String, Object> observationValues = extractValues(observation, fieldsHierarchy);*/

            var restFhirApi = new RestFhirApi(fhirUrl, fhirUser, fhirPwd);

            var resourceList = restFhirApi.getResourceByFullUrl("http://172.16.14.191:9080/fhir-server/api/v4/Observation?subject=1d475236-6cba-4e52-9b94-af37bb315cd0&_format=json&_pretty=true");

            var count = 0;
            for (var r : resourceList) {

                count = count + 1;


                // Hashtable per contenere i risultati
                Hashtable<String, Object> result = new Hashtable<>();

                // Estrai i campi ricorsivamente
                estraiCampiRicorsivo("", r, result);

                System.out.println(result.size());

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static String readFile(String filePath) throws Exception {
        Path path = Paths.get(filePath);
        return new String(Files.readAllBytes(path));
    }


    private static Hashtable<String, Object> extractFieldsHierarchy(Map<String, Object> jsonMap, String prefix) {
        Hashtable<String, Object> fieldsHierarchy = new Hashtable<>();

        for (Map.Entry<String, Object> entry : jsonMap.entrySet()) {
            String fieldName = prefix + entry.getKey();
            Object fieldValue = entry.getValue();

            if (fieldValue instanceof List<?>) {
                List<?> list = (List<?>) fieldValue;

                for (int i = 0; i < list.size(); i++) {

                    Object item = list.get(i);

                    Hashtable<String, Object> nestedHierarchy = extractFieldsHierarchy((Map<String, Object>) item, fieldName + ".");
                    fieldsHierarchy.putAll(nestedHierarchy);
                }
            }

            if (fieldValue instanceof Map) {
                // Se il valore è una mappa, ricorsivamente popola i suoi campi
                Hashtable<String, Object> nestedHierarchy = extractFieldsHierarchy((Map<String, Object>) fieldValue, fieldName + ".");
                fieldsHierarchy.putAll(nestedHierarchy);
            } else {
                // Altrimenti, imposta il valore nella gerarchia
                fieldsHierarchy.put(fieldName.toLowerCase(), fieldValue);
            }
        }

        return fieldsHierarchy;
    }

    private static void estraiCampiRicorsivo(String prefix, Object obj, Hashtable<String, Object> result) {
        // Ottieni i campi dichiarati nella classe
        Field[] campi = obj.getClass().getDeclaredFields();

        // Itera attraverso i campi
        for (Field campo : campi) {
            try {
                campo.setAccessible(true);
                Object valoreCampo = campo.get(obj);

                if (valoreCampo != null && org.hl7.fhir.r4.model.Type.class.isAssignableFrom(campo.getType())) {

                    org.hl7.fhir.r4.model.Type tipoFhir = (org.hl7.fhir.r4.model.Type) valoreCampo;

                    result.put(prefix + campo.getName(), valoreCampo);
                } else if (valoreCampo != null && isOggettoComplesso(campo.getType())) {
                    // Se sì, richiama ricorsivamente la funzione
                    estraiCampiRicorsivo(prefix + campo.getName() + ".", valoreCampo, result);

                } else if (valoreCampo != null) {

                    result.put(prefix + campo.getName(), valoreCampo);
                }


            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean isOggettoComplesso(Class<?> type) {
        try {
            return type.getPackage().getName().startsWith("org.hl7.fhir");
        } catch (Exception ex) {
            return false;
        }


    }

    private static boolean isTipoFHIR(Class<?> type) {
        while (type != null) {
            if (type.getName().equals("org.hl7.fhir.instance.model.api.IBaseDatatype")) {
                return true;
            }
            type = type.getSuperclass();
        }
        return false;
    }
}
