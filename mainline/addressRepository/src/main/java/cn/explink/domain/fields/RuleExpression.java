package cn.explink.domain.fields;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * 规则表达式<br/>
 * 规则表达式由数字表达式和单位构成，用"|"分割，如：1,3,4-10,11-20单,21-30双|号<br/>
 * 地址连续匹配上数字表达式和单位即认为匹配此规则表达式。<br/>
 * 例如：甘露园南里2区，匹配"1-3,5|区"，甘露园南里4区则不匹配<br/>
 * 例如：大望路101号，匹配"1-200单|号"，单不匹配"1-200双|号"<br/>
 * 例如：新乡医学院2号院，可匹配"1-3|号院"，不匹配"1-3|院"<br/>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RuleExpression {

	private static final String FUZZY_EXPRESSION = ".*";

	private static final String NUMBER_EXPRESSION = "[0-9]+";

	public String getFuzzyExpression;

	/**
	 * 单位
	 */
	private String unit;

	/**
	 * 数字表达式list
	 */
	private List<NumberExpression> numberExpressionList;

	/**
	 * 模糊表达式
	 * 
	 * @return
	 */
	public String getFuzzyPatten() {
		return FUZZY_EXPRESSION + NUMBER_EXPRESSION + unit + FUZZY_EXPRESSION;
	}

	/**
	 * 精确表达式
	 * 
	 * @return
	 */
	public String getStrictPatten() {
		return NUMBER_EXPRESSION + unit + FUZZY_EXPRESSION;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public List<NumberExpression> getNumberExpressionList() {
		return numberExpressionList;
	}

	public void setNumberExpressionList(List<NumberExpression> numberExpressionList) {
		this.numberExpressionList = numberExpressionList;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RuleExpression [");
		if (unit != null)
			builder.append("unit=").append(unit).append(", ");
		if (numberExpressionList != null)
			builder.append("numberExpressionList=").append(numberExpressionList);
		builder.append("]");
		return builder.toString();
	}

}
