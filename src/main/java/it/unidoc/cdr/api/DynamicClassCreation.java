package it.unidoc.cdr.api;

import com.google.gson.Gson;
import it.unidoc.cdr.api.fhir.MergeUtil;
import it.unidoc.cdr.api.fhir.RestFhirApi;


public class DynamicClassCreation {

    static String fhirUrl = "http://172.16.14.191:9080/fhir-server/api/v4/";
    static String fhirUser = "fhiruser";
    static String fhirPwd = "change-password";

    public static void main(String[] args) {


        var restFhirApi = new RestFhirApi(fhirUrl, fhirUser, fhirPwd);

        var resourceList = restFhirApi.getResourceByFullUrl("http://172.16.14.191:9080/fhir-server/api/v4/Medication");


       var ar =  MergeUtil.merge(resourceList);

        String json = new Gson().toJson(ar);

        System.out.println(json);



    }
}



