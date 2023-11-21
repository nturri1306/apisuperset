package it.unidoc.cdr.api.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.UriClientParam;
import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.hl7.fhir.r4.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author n.turri
 */

public class RestFhirApi {

    private static final Logger log = LoggerFactory.getLogger(RestFhirApi.class);

    private String SOURCE_SERVER_BASE_URL = "https://localhost:9443/fhir-server/api/v4/";

    IGenericClient sourceClient;

    public RestFhirApi(String SOURCE_SERVER_BASE_URL, String username, String password) {

        this.SOURCE_SERVER_BASE_URL = SOURCE_SERVER_BASE_URL;

       /* var userDir = System.getProperty("user.dir");

        System.setProperty("javax.net.ssl.keyStoreType", "PKCS12");
        System.setProperty("javax.net.ssl.keyStore", userDir + "/misc/fhirKeyStore.p12");
        System.setProperty("javax.net.ssl.keyStorePassword", password);

        System.setProperty("javax.net.ssl.trustStoreType", "PKCS12");
        System.setProperty("javax.net.ssl.trustStore", userDir + "/misc/fhirTrustStore.p12");
        System.setProperty("javax.net.ssl.trustStorePassword", password);*/

        // Create FHIR context and client for the source server
        FhirContext sourceContext = FhirContext.forR4();
        //sourceClient = sourceContext.newRestfulGenericClient(SOURCE_SERVER_BASE_URL);

        sourceClient = ignoreSSL(sourceContext);


        IClientInterceptor authInterceptor = new BasicAuthInterceptor(username, password);
        sourceClient.registerInterceptor(authInterceptor);

        //ibm server has problem with pretty
        //sourceClient.setPrettyPrint(true);

        //ignore warning log in validation
        // System.setProperty("javax.xml.accessExternalDTD", "");

        log.info("url:" + SOURCE_SERVER_BASE_URL);


    }


    public List<Resource> getHistoryById(String id, Class<? extends Resource> resourceType) {

        String methodName = "public List<Resource>  getHistoryById(String id, Class<? extends Resource> resourceType) ";

        try {

            List<Resource> ResourceList = new ArrayList<>();

            log.info("Begin " + methodName);
            log.info("id: " + id);
            log.info("resource: " + resourceType.getSimpleName());

            IdType idType = new IdType(resourceType.getSimpleName(), id);

            var bundle = sourceClient.history().onInstance(idType).returnBundle(Bundle.class).execute();

            for (Bundle.BundleEntryComponent entry : bundle.getEntry()) {
                Resource Resource = entry.getResource();
                //System.out.println("history id: " + entry.getResource().getId());
                ResourceList.add(Resource);
            }

            return ResourceList;
        } catch (Exception e) {
            handleExceptionAndLog(e);
            return null;
        } finally {
            log.info("End " + methodName);
        }
    }

    public boolean deleteResourceById(String id, Class<? extends Resource> resourceType) {
        String methodName = "public boolean deleteResourceById(String id, Class<? extends Resource> resourceType)";

        try {

            log.info("Begin " + methodName);
            log.info("id: " + id);
            log.info("resourceType: " + resourceType.getSimpleName());

            IdType idType = new IdType(resourceType.getSimpleName(), id);
            sourceClient.delete().resourceById(idType).execute();
            return true;
        } catch (Exception e) {
            handleExceptionAndLog(e);
            return false;
        } finally {
            log.info("End " + methodName);
        }
    }

    public Resource getByUrl(String url, Resource resourceType) {

        String methodName = "Resource getByUrl(String url,Resource resourceType)";

        try {

            log.info("Begin " + methodName);
            log.info("resourceType: " + resourceType.getClass());
            log.info("url: " + url);

            Bundle bundle = sourceClient.search().forResource(resourceType.getClass()).where(new UriClientParam(FilterNames.URL).matches().value(url)).returnBundle(Bundle.class).execute();

            var resourceList = extractResourcesFromBundle(bundle, resourceType.getClass());

            log.info("elements: " + resourceList.size());

            if (resourceList.size() > 0) return resourceList.get(0);

            else return null;

        } catch (Exception e) {
            handleExceptionAndLog(e);
            return null;

        } finally {
            log.info("End " + methodName);
        }


    }


    private <T> List<T> extractResourcesFromBundle(Bundle bundle, Class<T> resourceType) {
        List<T> resources = new ArrayList<>();
        for (Bundle.BundleEntryComponent entry : bundle.getEntry()) {
            if (entry.getResource().getClass().equals(resourceType)) {
                resources.add(resourceType.cast(entry.getResource()));
            }
        }
        return resources;
    }

    public List<Object> extractResourcesFromBundle(Bundle bundle) {

        List<Object> resources = new ArrayList<>();


        System.out.println("size:" + bundle.getEntry().size());

        for (Bundle.BundleEntryComponent entry : bundle.getEntry()) {

            var resource = Resource.class.cast(entry.getResource());


            if (resource instanceof Observation) {

                resources.add(toObservation((Observation) resource));

            } else if (resource instanceof AllergyIntolerance) {

                // resources.add(toAllergy((AllergyIntolerance) resource));

            } else
                resources.add(resource);


        }
        return resources;
    }

    IGenericClient ignoreSSL(FhirContext fhirContext) {
        try {
            // Create a trust manager that ignores certificate verification
            TrustManager[] trustAllCertificates = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            // Create an SSL context that ignores certificate verification
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCertificates, new java.security.SecureRandom());

            // Create a custom HttpClient to bypass certificate verification
            CloseableHttpClient httpClient = HttpClients.custom().setSslcontext(sslContext).setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).build();

            // Set the custom HttpClient in the FHIR context
            fhirContext.getRestfulClientFactory().setHttpClient(httpClient);

            return fhirContext.newRestfulGenericClient(SOURCE_SERVER_BASE_URL);


        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String formatDate(String inputDate) {
        if (inputDate == null || inputDate.length() != 8) {
            throw new IllegalArgumentException("Input date must be in the format 'yyyyMMdd' and should not be null.");
        }

        String year = inputDate.substring(0, 4);
        String month = inputDate.substring(4, 6);
        String day = inputDate.substring(6, 8);

        String formattedDate = year + "-" + month + "-" + day;
        return formattedDate;
    }

    public Date parseDate(String dateStr, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        try {
            return dateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    public boolean isNotBlank(String str) {
        return str != null && !str.isEmpty();
    }

    private String handleExceptionAndLog(Exception e) {
        String msgError = "";
        e.printStackTrace();
        log.error(e.getMessage());

        if (e instanceof BaseServerResponseException) {
            msgError = ((BaseServerResponseException) e).getResponseBody();
            log.error(msgError);
            return msgError;
        } else return e.getMessage();
    }


    public String getByFullUrl(String fullUrl) {

        var bundle = sourceClient
                .search()
                .byUrl(fullUrl)
                .returnBundle(Bundle.class)
                .execute();


        List<Object> resourceList = extractResourcesFromBundle(bundle);

        if (resourceList.size() > 0)
            return Util.toArrayJson(resourceList);

        else return Util.toJson(bundle);

    }

    public List<Object> getResourceByFullUrl(String fullUrl) {

        var bundle = sourceClient
                .search()
                .byUrl(fullUrl)
                .returnBundle(Bundle.class)
                .execute();


        return extractResourcesFromBundle(bundle);

    }

    private CustomObservation toObservation(Observation source) {

        CustomObservation dest = new CustomObservation();

        try {

            if (source.getValue() != null && source.getValue() instanceof Quantity) {

                dest.setValue(source.getValueQuantity().getValue().toString());

                dest.setSystem(source.getValueQuantity().getSystem());

                dest.setCode(source.getValueQuantity().getCode());

                dest.setUnit(source.getValueQuantity().getUnit());

            } else if (source.getValue() != null && source.getValue() instanceof CodeableConcept) {

                CodeableConcept codeableConcept = (CodeableConcept) source.getValue();

                for (var coding : codeableConcept.getCoding()) {

                    dest.setValueCodeableConceptCode(coding.getCode());
                    dest.setValueCodeableConceptSystem(coding.getSystem());
                    dest.setValueCodeableConceptDisplay(coding.getDisplay());

                }

            } else {
                throw new IllegalArgumentException("value null.");
            }

        } catch (Exception ex) {

        }


        return dest;
    }


}
