package it.unidoc.cdr.api.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ValidationResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author n.turri
 */
public class Util {

    private static final Logger log = LoggerFactory.getLogger(Util.class);

    public static String toJson(IBaseResource object) {

        String methodName = "public static String toJson(IBaseResource object)";
        try {
            log.info("Begin " + methodName);

            FhirContext ctx = FhirContext.forR4();

            IParser parser = ctx.newJsonParser();

            // parser.setPrettyPrint(true);

            return parser.encodeResourceToString(object);
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("error: " + ex.getMessage());
            return "";
        } finally {
            log.info("End " + methodName);
        }

    }

    public static String toArrayJson(List<Object> resources) {
        String methodName = "public static String toArrayJson(List<Object> resources)";
        try {
            log.info("Begin " + methodName);

            FhirContext ctx = FhirContext.forR4();

            IParser parser = ctx.newJsonParser();

            parser.setPrettyPrint(true);

            List<String> resourceJsonStrings = new ArrayList<>();
            for (var resource : resources) {

                if (resource instanceof Resource) {
                    resourceJsonStrings.add(parser.encodeResourceToString((Resource) resource));

                } else {
                    resourceJsonStrings.add(Util.toJson(resource));
                }

            }

            String jsonArray = "[" + String.join(",", resourceJsonStrings) + "]";

            return jsonArray;
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("error: " + ex.getMessage());
            return "";
        } finally {
            log.info("End " + methodName);
        }
    }


    public static String validator(String json) {

        String methodName = "public static String validator(String json) {";

        try {
            log.info("Begin " + methodName);

            FhirContext ctx = FhirContext.forR4();
            FhirValidator validator = ctx.newValidator();


            ValidationResult result = validator.validateWithResult(json);
            System.out.println("Success: " + result.isSuccessful());

            OperationOutcome outcome = (OperationOutcome) result.toOperationOutcome();
            IParser parser = ctx.newXmlParser().setPrettyPrint(true);
            System.out.println(parser.encodeResourceToString(outcome));

            return parser.encodeResourceToString(outcome);

        } catch (Exception ex) {
            return "error: " + ex.getMessage();
        } finally {
            log.info("End " + methodName);
        }
    }

    public static String toJson(Object object) {

        String jsonRequest = null;

        try {
            ObjectMapper objectMapper = new ObjectMapper();

            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

            jsonRequest = objectMapper.writeValueAsString(object);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return jsonRequest;


    }

    public static <T> T deserializeJson(String json, Class<T> valueType) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.readValue(json, valueType);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
