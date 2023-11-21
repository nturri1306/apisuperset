package it.unidoc.cdr.api.fhir;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Setter
@Getter
public class Conf {

    @Value("${cdr.ui.rest-fhir-base-url}")
    private String fhirBaseUrl;



    @Value("${cdr.ui.rest-fhir-username}")
    private String fhirUsername;

    @Value("${cdr.ui.rest-fhir-password}")
    private String fhirPassword;
}