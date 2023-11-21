package it.unidoc.cdr.api;


import it.unidoc.cdr.api.fhir.RestFhirApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import org.springframework.http.MediaType;

import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("/api/example")
public class ApiController {

    /*
     * curl -i "http://username:password@127.0.0.1:8081/api/example/ValueSet?_count=50&_lastUpdated=ge2021-11-17&_lastUpdated=le2023-11-17&_sort=-_lastUpdated&_page=1&_firstId=37005&_lastId=34006&_format=json&_pretty=true"
     */

    private static final Logger log = LoggerFactory.getLogger(ApiController.class);
    String fhirUrl = "http://172.16.14.191:9080/fhir-server/api/v4/";
    String fhirUser = "fhiruser";
    String fhirPwd = "change-password";

    //@GetMapping("/{queryString}")

    @GetMapping(value = "/{queryString}", produces = MediaType.APPLICATION_JSON_VALUE)
    //@ResponseBody
    public String handleExampleRequest(
            @PathVariable String queryString,
            @RequestParam Map<String, String> queryParams) throws IOException {


        var restFhirApi = new RestFhirApi(fhirUrl, fhirUser, fhirPwd);


        final String[] urlParams = {""};

        queryParams.forEach((key, value) ->
                {

                    urlParams[0] += "&" + key + "=" + value;

                    log.info("Parametro: " + key + ", Valore: " + value);
                }

        );

        String url = fhirUrl + queryString;

        if (urlParams[0].length() > 1)
            url += "?" + urlParams[0].substring(1);

        log.info(url);

        String jsonContent = restFhirApi.getByFullUrl(url);


        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        File tempFile = new File(tempDir, UUID.randomUUID()+".json");

        try (FileWriter fileWriter = new FileWriter(tempFile)) {
            fileWriter.write(jsonContent);
        }

        Resource resource = new FileSystemResource(tempFile);

        try (InputStream inputStream = resource.getInputStream()) {
            return StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        }

    }


  /*  @GetMapping(value = "/json", produces = MediaType.APPLICATION_JSON_VALUE)

    public String getPatientJson() throws IOException {

        Resource resource = new ClassPathResource("static/patient.json");

        try (InputStream inputStream = resource.getInputStream()) {

            return StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        }
    }*/



    @GetMapping(value = "/json", produces = MediaType.APPLICATION_JSON_VALUE)

    public String getPatientJson() throws IOException {

        Resource resource = new ClassPathResource("static/bundle.json");

        try (InputStream inputStream = resource.getInputStream()) {

            return StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        }
    }

}