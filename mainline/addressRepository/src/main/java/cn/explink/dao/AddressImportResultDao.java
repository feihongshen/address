package cn.explink.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import cn.explink.dao.support.BasicHibernateDaoSupport;
import cn.explink.domain.AddressImportResult;


@Repository
public class AddressImportResultDao extends BasicHibernateDaoSupport<AddressImportResult, Long> {

	public AddressImportResultDao() {
		super(AddressImportResult.class);
	}

	public List<AddressImportResult> getImportAddressResults(Date startDate, Date endDate) {
		String hql = "from AddressImportResult where importDate >= :startDate and importDate <= :endDate";
		Query query = getSession().createQuery(hql);
		query.setDate("startDate", startDate);
		query.setDate("endDate", endDate);
		return query.list();
	}
	
}
