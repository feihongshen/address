package cn.explink.domain.fields;

/**
 * 数字表达式<br/>
 * 可单独指定一个号码，如：5<br/>
 * 或指定一个范围，如：1-10<br/>
 * 或范围加过滤条件，如：1-10单, 20-30双<br/>
 */
public class NumberExpression {
	
	/**
	 * 没有过滤
	 */
	public static final int NO_FILTER = 0;
	
	/**
	 * 奇数过滤
	 */
	public static final int FILTER_ODD = 1;
	
	/**
	 * 偶数过滤
	 */
	public static final int FILTER_EVEN = 2;

	private int minNumber;

	private int maxNumber;

	/**
	 * 过滤条件 0:无过滤条件，1:只要单号，2:只要双号
	 */
	private int filter;

	public int getMinNumber() {
		return minNumber;
	}

	public void setMinNumber(int minNumber) {
		this.minNumber = minNumber;
	}

	public int getMaxNumber() {
		return maxNumber;
	}

	public void setMaxNumber(int maxNumber) {
		this.maxNumber = maxNumber;
	}

	public int getFilter() {
		return filter;
	}

	public void setFilter(int filter) {
		this.filter = filter;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("NumberExpression [minNumber=").append(minNumber).append(", maxNumber=").append(maxNumber).append(", filter=").append(filter)
				.append("]");
		return builder.toString();
	}

}
