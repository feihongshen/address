package cn.explink.dao;

import org.springframework.stereotype.Repository;

import cn.explink.dao.support.BasicHibernateDaoSupport;
import cn.explink.domain.AddressImportDetail;


@Repository
public class AddressImportDetailDao extends BasicHibernateDaoSupport<AddressImportDetail, Long> {

	public AddressImportDetailDao() {
		super(AddressImportDetail.class);
	}
	
}
