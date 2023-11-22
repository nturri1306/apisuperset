package it.unidoc.cdr.api;


import it.unidoc.cdr.api.fhir.Conf;
import it.unidoc.cdr.api.fhir.RestFhirApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;


@RestController
@RequestMapping("/api/fhir")
public class ApiController {

    /*
     * curl -i "http://username:password@127.0.0.1:8081/api/example/ValueSet?_count=50&_lastUpdated=ge2021-11-17&_lastUpdated=le2023-11-17&_sort=-_lastUpdated&_page=1&_firstId=37005&_lastId=34006&_format=json&_pretty=true"
     */

    private static final Logger log = LoggerFactory.getLogger(ApiController.class);

    @Autowired
    Conf conf;

    @GetMapping(value = "/{queryString}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String handleRequest(
            @PathVariable String queryString,
            @RequestParam Map<String, String> queryParams) throws IOException {


        String method = "String handleRequest(" +
                "@PathVariable String queryString," +
                "@RequestParam Map<String, String> queryParams)";

        log.info("--- BEGIN " + method);

        try {


            var restFhirApi = new RestFhirApi(conf.getFhirBaseUrl(), conf.getFhirUsername(), conf.getFhirPassword());


            final String[] urlParams = {""};

            queryParams.forEach((key, value) ->
                    {

                        urlParams[0] += "&" + key + "=" + value;

                        log.info("Parametro: " + key + ", Valore: " + value);
                    }

            );

            String url = conf.getFhirBaseUrl() + "/" + queryString;

            if (urlParams[0].length() > 1)
                url += "?" + urlParams[0].substring(1);

            log.info(url);

            String jsonContent = restFhirApi.getByFullUrl(url);

            return jsonContent;

           /* File tempDir = new File(System.getProperty("java.io.tmpdir"));
            File tempFile = new File(tempDir, UUID.randomUUID() + ".json");

            try (FileWriter fileWriter = new FileWriter(tempFile)) {
                fileWriter.write(jsonContent);
            }

            Resource resource = new FileSystemResource(tempFile);

            try (InputStream inputStream = resource.getInputStream()) {
                return StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            }*/


        } catch (Exception ex) {
            return RestFhirApi.handleExceptionAndLog(ex);

        } finally {
            log.info("--- END " + method);
        }

    }

}