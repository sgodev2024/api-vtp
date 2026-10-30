package vn.aicrm.exception;

public class ViettelPostApiException extends RuntimeException{
    public ViettelPostApiException(String message){
        super(message);
    }

    public ViettelPostApiException(String message, Exception e){
        super(message, e);
    }
}
