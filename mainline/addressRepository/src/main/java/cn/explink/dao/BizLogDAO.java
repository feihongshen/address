package cn.explink.dao;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import cn.explink.dao.support.BasicHibernateDaoSupport;
import cn.explink.domain.BizLog;

@Repository
public class BizLogDAO extends BasicHibernateDaoSupport<BizLog, Long> {

	public BizLogDAO() {
		super(BizLog.class);
	}

	public Map<String, Object> findInfo(int operationType, String beginTime, String endTime, String operationIP, int page, int pageNumber) {
		Map<String, Object> map = new HashMap<String, Object>();
		StringBuffer sql = new StringBuffer();
		StringBuffer countsql = new StringBuffer();
		StringBuffer where = new StringBuffer();
		sql.append("from BizLog where 1=1 ");
		countsql.append("select count(id) from BizLog where 1=1 ");
		if ((operationType != 0)) {//int类型的operationType，默认值会是0
			where.append(" and OPERATION_TYPE=" + operationType);
		}
		if ((beginTime != null) && !beginTime.isEmpty()) {
			where.append(" and OPERATION_TIME>='" + beginTime + " 00:00:00'");
		}
		if ((endTime != null) && !endTime.isEmpty()) {
			where.append(" and OPERATION_TIME<='" + endTime + " 00:00:00'");
		}
		if ((operationIP != null) && !operationIP.isEmpty()) {
			where.append(" and OPERATOR_IP<=" + operationIP);
		}
		where.append(" order by OPERATION_TIME desc ");
		sql.append(where);
		countsql.append(where);
		Query query = this.getSession().createQuery(sql.toString());
		Query countQuery = this.getSession().createQuery(countsql.toString());
		int count = Integer.parseInt(countQuery.uniqueResult().toString());
		query.setFirstResult((page - 1) * pageNumber);
		query.setMaxResults(pageNumber);

		map.put("list", query.list());
		map.put("count", count);
		return map;
	}

}
