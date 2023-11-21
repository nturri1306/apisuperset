package it.unidoc.cdr.api.fhir;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CustomCoding {

    private String system;
    private String code;
    private String display;
    public CustomCoding() {
    }



}