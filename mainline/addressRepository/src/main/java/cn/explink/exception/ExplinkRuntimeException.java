package cn.explink.exception;

public class ExplinkRuntimeException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5574759551365998922L;

	public ExplinkRuntimeException() {
	}

	public ExplinkRuntimeException(String message) {
		super(message);
	}

	public ExplinkRuntimeException(Throwable cause) {
		super(cause);
	}

	public ExplinkRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

}
