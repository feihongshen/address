package cn.explink.exception;

/**
 * 绑定失败异常
 *
 * @author songkaojun 2015年4月24日
 */
public class BindFailedException extends ExplinkRuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 835764657903877338L;

	public BindFailedException(String message) {
		super(message);
	}

}
