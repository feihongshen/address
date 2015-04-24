package cn.explink.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import cn.explink.domain.DeliveryStationRule;
import cn.explink.domain.fields.NumberExpression;
import cn.explink.domain.fields.RuleExpression;
import cn.explink.exception.ExplinkRuntimeException;
import cn.explink.util.StringUtil;

public class RuleService extends CommonServiceImpl<DeliveryStationRule, Long> {

	public RuleService() {
		super(DeliveryStationRule.class);
	}

	protected boolean isConflict(RuleExpression ruleExpression, RuleExpression existingRuleExpression) {
		if (!existingRuleExpression.getUnit().equals(ruleExpression.getUnit())) {
			return false;
		}
		for (NumberExpression numberExpression : ruleExpression.getNumberExpressionList()) {
			for (NumberExpression exisintNumberExpression : existingRuleExpression.getNumberExpressionList()) {
				// 过滤条件相同
				if (numberExpression.getFilter() == exisintNumberExpression.getFilter()) {
					// 范围包含已存在规则的最小值
					if ((numberExpression.getMinNumber() <= exisintNumberExpression.getMinNumber()) && (numberExpression.getMaxNumber() >= exisintNumberExpression.getMinNumber())) {
						return true;
					}
					// 范围包含已存在规则的最大值
					if ((numberExpression.getMinNumber() <= exisintNumberExpression.getMaxNumber()) && (numberExpression.getMaxNumber() >= exisintNumberExpression.getMaxNumber())) {
						return true;
					}
					// 范围被已存在规则完全包含，新规则是旧规则的子集
					if ((numberExpression.getMinNumber() >= exisintNumberExpression.getMinNumber()) && (numberExpression.getMaxNumber() <= exisintNumberExpression.getMaxNumber())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	protected RuleExpression parseRule(String rule) {
		if (StringUtils.isBlank(rule)) {
			return null;
		}
		String[] array1 = rule.split("\\|");
		if (array1.length != 2) {
			throw new ExplinkRuntimeException("invalid rule: " + rule);
		}

		RuleExpression ruleExpression = new RuleExpression();
		ruleExpression.setUnit(array1[1].trim());
		List<NumberExpression> numberExpressionList = new ArrayList<NumberExpression>();
		ruleExpression.setNumberExpressionList(numberExpressionList);
		String[] array2 = array1[0].trim().split(",");
		for (String number : array2) {
			number = number.trim();
			NumberExpression numberExpression = new NumberExpression();
			numberExpressionList.add(numberExpression);

			if (number.endsWith("单")) {
				numberExpression.setFilter(NumberExpression.FILTER_ODD);
				number = number.substring(0, number.length() - 1);
			} else if (number.endsWith("双")) {
				numberExpression.setFilter(NumberExpression.FILTER_EVEN);
				number = number.substring(0, number.length() - 1);
			} else {
				numberExpression.setFilter(NumberExpression.NO_FILTER);
			}

			String[] array3 = number.split("-");
			if (array3.length > 2) {
				throw new ExplinkRuntimeException("invalid rule: " + rule);
			}
			numberExpression.setMinNumber(Integer.parseInt(array3[0]));
			if (array3.length == 2) {
				numberExpression.setMaxNumber(Integer.parseInt(array3[1]));
			} else {
				numberExpression.setMaxNumber(Integer.parseInt(array3[0]));
			}
		}

		return ruleExpression;
	}

	/**
	 * 判断给定的地址串是否匹配指定的规则
	 *
	 * @param addressLine
	 * @param ruleExpression
	 * @return
	 */
	protected boolean isMapping(String addressLine, RuleExpression ruleExpression) {
		// String fuzzyPatten = ruleExpression.getFuzzyPatten();
		String strictPatten = ruleExpression.getStrictPatten();
		// 关键数字
		int number = -1;
		// if(StringUtil.match(addressLine, strictPatten))
		// modified by songkaojun 2015-02-13 只用匹配到关键字“紧跟”着的数字才是该关键字的规则
		if (StringUtil.match(addressLine, strictPatten)) {
			addressLine = StringUtil.substring(addressLine, strictPatten);
			number = StringUtil.startNumeric(addressLine);
		} else {
			return false;
		}
		for (NumberExpression numberExpression : ruleExpression.getNumberExpressionList()) {
			// 关键数字在最小值和最大值之间
			if ((number >= numberExpression.getMinNumber()) && (number <= numberExpression.getMaxNumber())) {
				// 并且单双过滤符合
				if ((NumberExpression.NO_FILTER == numberExpression.getFilter()) || ((NumberExpression.FILTER_ODD == numberExpression.getFilter()) && ((number % 2) == 1))
						|| ((NumberExpression.FILTER_EVEN == numberExpression.getFilter()) && ((number % 2) == 0))) {
					return true;
				}
			}
		}
		return false;
	}
}
