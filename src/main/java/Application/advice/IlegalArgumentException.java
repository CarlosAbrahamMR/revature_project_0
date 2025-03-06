package Application.advice;

public class IlegalArgumentException extends RuntimeException{
    public IlegalArgumentException(String message)  {
        super(message);
    }
}
