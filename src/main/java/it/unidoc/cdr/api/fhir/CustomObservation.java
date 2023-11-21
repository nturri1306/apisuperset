package it.unidoc.cdr.api.fhir;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class CustomObservation {

    @JsonProperty("valueQuantity.code")
    private String code;

    @JsonProperty("valueQuantity.value")
    private String value;

    @JsonProperty("valueQuantity.unit")
    private String unit;

    @JsonProperty("valueQuantity.system")
    private String system;

    @JsonProperty("valueCodeableConcept.system")
    private String valueCodeableConceptSystem;

    @JsonProperty("valueCodeableConcept.code")
    private String valueCodeableConceptCode;

    @JsonProperty("valueCodeableConcept.display")
    private String valueCodeableConceptDisplay;


}


