package it.unidoc.cdr.api.fhir;

import ca.uhn.fhir.rest.api.Constants;
import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IHttpRequest;
import ca.uhn.fhir.rest.client.api.IHttpResponse;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.io.IOException;

/**
 * HTTP interceptor to be used for adding HTTP basic auth username/password tokens
 * to requests
 * <p>
 * See the <a href="https://hapifhir.io/hapi-fhir/docs/interceptors/built_in_client_interceptors.html">HAPI Documentation</a>
 * for information on how to use this class.
 * </p>
 */
public class BasicAuthInterceptor implements IClientInterceptor {

    private String myUsername;
    private String myPassword;
    private String myHeaderValue;

    /**
     * @param theUsername The username
     * @param thePassword The password
     */
    public BasicAuthInterceptor(String theUsername, String thePassword) {
        this(StringUtils.defaultString(theUsername) + ":" + StringUtils.defaultString(thePassword));
    }

    /**
     * @param theCredentialString A credential string in the format <code>username:password</code>
     */
    public BasicAuthInterceptor(String theCredentialString) {
        Validate.notBlank(theCredentialString, "theCredentialString must not be null or blank");
        Validate.isTrue(
                theCredentialString.contains(":"), "theCredentialString must be in the format 'username:password'");
        String encoded = Base64.encodeBase64String(theCredentialString.getBytes(Constants.CHARSET_US_ASCII));
        myHeaderValue = "Basic " + encoded;
    }

    @Override
    public void interceptRequest(IHttpRequest theRequest) {
        theRequest.addHeader(Constants.HEADER_AUTHORIZATION, myHeaderValue);
    }

    @Override
    public void interceptResponse(IHttpResponse theResponse) throws IOException {
        // nothing
    }
}