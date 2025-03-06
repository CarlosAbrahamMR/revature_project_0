package Application.advice;

public class DatabaseException extends RuntimeException{
    public DatabaseException(String message,Throwable cause)  {
        super(message,cause);
    }
}
