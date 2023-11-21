package it.unidoc.cdr.api.fhir;

/**
 * @author n.turri
 */
public class Result {
    private boolean success;
    private Object returnValue;

    public Result(boolean success, Object returnValue) {
        this.success = success;
        this.returnValue = returnValue;
    }

    public boolean isSuccess() {
        return success;
    }


    public Object getReturnValue() {
        return returnValue;
    }
}

