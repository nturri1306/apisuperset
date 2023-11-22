package it.unidoc.cdr.api.fhir;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;
import com.google.gson.Gson;
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

import java.util.ArrayList;
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

    public List<Object> extractResourcesFromBundle(Bundle bundle) {

        List<Object> resources = new ArrayList<>();

        System.out.println("size:" + bundle.getEntry().size());

        for (Bundle.BundleEntryComponent entry : bundle.getEntry()) {

            var resource = Resource.class.cast(entry.getResource());

            /*if (resource instanceof Observation) {

                resources.add(CustomObservation.toObservation((Observation) resource));

            } else if (resource instanceof AllergyIntolerance) {

                // resources.add(toAllergy((AllergyIntolerance) resource));

            } else*/
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


    public static String handleExceptionAndLog(Exception e) {
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

        var ar =  MergeUtil.merge(resourceList);

        String json = new Gson().toJson(ar);

        return  json;

       /* if (resourceList.size() > 0)
            return Util.toArrayJson(resourceList);

        else return Util.toJsonResource(bundle);*/

    }

    public List<Object> getResourceByFullUrl(String fullUrl) {

        var bundle = sourceClient
                .search()
                .byUrl(fullUrl)
                .returnBundle(Bundle.class)
                .execute();


        return extractResourcesFromBundle(bundle);

    }


}
