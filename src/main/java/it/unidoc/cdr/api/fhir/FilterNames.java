package it.unidoc.cdr.api.fhir;


/*
*  "diagnostics": "HAPI-0524: Unknown search parameter \"id\" for resource type \"CodeSystem\".
*
* Valid search parameters for this search are: [_content, _id, _lastUpdated, _profile, _security,
*  _source, _tag, _text, code, content-mode, context, context-quantity, context-type,
* context-type-quantity, context-type-value, date, description, identifier, jurisdiction,
* language, name, publisher, status, supplements, system, title, url, version]"
  }
* */
public interface FilterNames {

    String ID = "_id";
    String LASTUPDATED = "_lastUpdated";
    String TEXT = "_text";
    String ACTIVE = "active";
    String STATUS = "status";
    String URL = "url";
    String NAME = "name";
    String PATIENT = "patient";

    String FAMILY = "family";

    String GIVEN = "given";

    String GENDER = "gender";
    String BIRTHDATE = "birthdate";
    String DESC = "description";
    String SUBMISSION_DATA = "date";
    String VERSION = "version";
    String STATUS_GROUP = "status_group";
    String NOTE = "note";
    String CODE = "code";
    String SUBJECT = "subject";

    String IDENTIFIER = "identifier";


    String PRESENTEDFORM = "presentedForm";

    String EFFECTIVEDATETIME = "effectiveDateTime";

    String PERIOD = "period";

    String REFERENCE = "reference";

    String RECORDER = "recorder";

    String REACTION = "reaction";
    String SERVICEPROVIDER = "service-provider";

    String CLINICALSTATUS = "clinical-status";

    String VERIFICATIONSTATUS = "verification-status";

    String TYPE = "type";
    String CATEGORY = "category";

    String VACCINECODE = "vaccine-code";

    String OCCURENCEDATETIME = "occurrenceDateTime";

    String DEVICE = "device";

    String BODYSITE = "bodysite";

    String CONTENT = "content";


}