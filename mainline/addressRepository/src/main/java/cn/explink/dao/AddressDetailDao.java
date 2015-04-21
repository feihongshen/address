package cn.explink.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import cn.explink.dao.support.BasicHibernateDaoSupport;
import cn.explink.domain.AddressDetail;
import cn.explink.util.StringUtil;

@Repository
public class AddressDetailDao extends BasicHibernateDaoSupport<AddressDetail, Long> {

	public AddressDetailDao() {
		super(AddressDetail.class);
	}

	public void synAddressDetail(List<AddressDetail> addressDetailList) {
		// this.createTempTable();

		// this.insertAddressDetail(addressDetailList);
		String clearData = " delete from AddressDetail";
		Query clearDataQuery = this.getSession().createQuery(clearData);
		clearDataQuery.executeUpdate();

		this.insert(addressDetailList);
	}

	public void deleteByIdList(List<Long> addressDetailIdList) {
		StringBuffer deleteSql = new StringBuffer("delete from AddressDetail where id in :addressDetailIdList");
		Query deleteQuery = this.getSession().createQuery(deleteSql.toString());
		deleteQuery.setParameterList("addressDetailIdList", addressDetailIdList);
		deleteQuery.executeUpdate();
	}

	public List<AddressDetail> fuzzyQueryByPage(String keyword, String stationName, Long customerId, int page, int pageSize) {
		StringBuffer sql = new StringBuffer(
				"select id,province,city,district,address_id1,address_name1,address_id2,address_name2,address_id3,address_name3,delivery_station_name from address_detail where customer_id="
						+ customerId);
		sql.append(this.getWhereSql(keyword, stationName));

		Query query = this.getSession().createSQLQuery(sql.toString());
		query.setFirstResult((page - 1) * pageSize);
		query.setMaxResults(pageSize);

		@SuppressWarnings("unchecked")
		List<Object> addressDetailObjList = query.list();

		return this.convertToAddressDetail(addressDetailObjList);
	}

	private StringBuffer getWhereSql(String keyword, String stationName) {
		StringBuffer sql = new StringBuffer();

		String keywordLikeSql = " like '%" + keyword + "%' ";

		if (StringUtil.isNotEmpty(keyword)) {
			sql.append(" and (province ");
			sql.append(keywordLikeSql + " or city ");
			sql.append(keywordLikeSql + " or district ");
			sql.append(keywordLikeSql + " or address_name1 ");
			sql.append(keywordLikeSql + " or address_name2 ");
			sql.append(keywordLikeSql + " or address_name3 ");
			sql.append(keywordLikeSql + ")");
		}
		if (StringUtil.isNotEmpty(stationName)) {
			sql.append(" and delivery_station_name like '%" + stationName + "%'");
		}

		return sql;
	}

	public int getAddressDetailCount(String keyword, String stationName, Long customerId) {
		StringBuffer sql = new StringBuffer("select count(id) from address_detail where customer_id=" + customerId);
		sql.append(this.getWhereSql(keyword, stationName));
		Query query = this.getSession().createSQLQuery(sql.toString());
		return ((Number) query.uniqueResult()).intValue();
	}

	private List<AddressDetail> convertToAddressDetail(List<Object> addressDetailObjList) {
		List<AddressDetail> addressDetailList = new ArrayList<AddressDetail>();
		for (Object addressDetailObj : addressDetailObjList) {
			Object[] parts = (Object[]) addressDetailObj;
			AddressDetail addressDetail = new AddressDetail();
			addressDetail.setId(((Integer) parts[0]).longValue());
			addressDetail.setProvince((String) parts[1]);
			addressDetail.setCity((String) parts[2]);
			addressDetail.setDistrict((String) parts[3]);
			addressDetail.setAddressId1(((Integer) parts[4]).longValue());
			addressDetail.setAddressName1((String) parts[5]);
			addressDetail.setAddressId2(null == parts[6] ? 0L : ((Integer) parts[6]).longValue());
			addressDetail.setAddressName2(null == parts[7] ? "" : (String) parts[7]);
			addressDetail.setAddressId3(null == parts[8] ? 0L : ((Integer) parts[8]).longValue());
			addressDetail.setAddressName3(null == parts[9] ? "" : (String) parts[9]);
			addressDetail.setDeliveryStationName((String) parts[10]);

			addressDetailList.add(addressDetail);
		}
		return addressDetailList;
	}
}
