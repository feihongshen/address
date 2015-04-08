package cn.explink.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.explink.dao.KeywordSuffixDao;
import cn.explink.domain.KeywordSuffix;
import cn.explink.modle.AjaxJson;

@Service
public class KeywordSuffixService extends CommonServiceImpl<KeywordSuffix, Long> {

	public KeywordSuffixService() {
		super(KeywordSuffix.class);
	}

	private static Logger logger = LoggerFactory.getLogger(KeywordSuffixService.class);

	@Autowired
	private KeywordSuffixDao keywordSuffixDao;

	public List<KeywordSuffix> getKeywordSuffixByCustomerId(Long customerId) {
		return this.keywordSuffixDao.getKeywordSuffixByCustomerId(customerId);

	}

	public AjaxJson addKeywordSuffix(String name, Long customerId) {
		AjaxJson aj = new AjaxJson();
		KeywordSuffix keywordSuffix = this.keywordSuffixDao.getKeywordSuffixByName(name, customerId);
		try {
			if (keywordSuffix == null) {
				keywordSuffix = new KeywordSuffix();
				keywordSuffix.setCustomerId(customerId);
				keywordSuffix.setName(name);
				this.keywordSuffixDao.save(keywordSuffix);
				aj.setSuccess(true);
			} else {
				aj.setSuccess(false);
				aj.setMsg("已存在关键词后缀：" + name);
			}
		} catch (Exception e) {
			aj.setSuccess(false);
			aj.setMsg(e.getMessage());
		}
		aj.setObj(keywordSuffix);
		return aj;
	}

	public void deleteKeywordSuffix(Long id) {
		KeywordSuffix keywordSuffix = this.keywordSuffixDao.get(id);
		this.keywordSuffixDao.delete(keywordSuffix);
	}
}
