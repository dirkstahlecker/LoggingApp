package model;

/**
 * Custom exception to be used inside all methods, where necessary
 * @author Dirk
 *
 */
public class LoggingException extends Exception {

	private static final long serialVersionUID = 1L; //required
	
    public LoggingException(){
        super();
    }

    public LoggingException(String message){
        super(message);
    }
}
