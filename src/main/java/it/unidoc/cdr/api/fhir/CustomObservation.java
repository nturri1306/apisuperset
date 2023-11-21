package it.unidoc.cdr.api.fhir;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Quantity;

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


    public static CustomObservation toObservation(Observation source) {

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


