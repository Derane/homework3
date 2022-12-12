package secondTask.exception;

public class NotSuchPropertyKeyException extends RuntimeException {

	public NotSuchPropertyKeyException() {
		super("Entered key is no valid! ");
	}
}
