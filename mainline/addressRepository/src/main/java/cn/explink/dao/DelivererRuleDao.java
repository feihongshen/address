
package cn.explink.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import cn.explink.dao.support.BasicHibernateDaoSupport;
import cn.explink.domain.DelivererRule;
import cn.explink.domain.enums.DelivererRuleTypeEnum;
import cn.explink.tree.ZTreeNode;
import cn.explink.web.vo.DelivererStationRuleVo;

@Repository
public class DelivererRuleDao extends BasicHibernateDaoSupport<DelivererRule, Long> {

    public DelivererRuleDao() {
        super(DelivererRule.class);
    }

    public List<DelivererRule> getDelivererRuleList(Long customerId, Long addressId) {
        Query query = this
                .getSession()
                .createQuery(
                        "select dr from DelivererRule dr where dr.deliverer.status=1 and dr.address.id =:aId and dr.deliverer.customer.id=:customerId ");
        query.setLong("customerId", customerId);
        query.setLong("aId", addressId);
        return query.list();

        // StringBuilder hql = new StringBuilder("from DelivererRule where address.id = :addressId");
        // // StringBuilder hql = new
        // //
        // StringBuilder("select new cn.explink.domain.DelivererRule(r.id, r.rule, new cn.explink.domain.Deliverer(d.id, d.name, d.externalId))");
        // // StringBuilder hql = new StringBuilder("select new cn.explink.domain.DelivererRule(r.id, r.rule)");
        // // hql.append(" from DelivererRule r, Deliverer d");
        // // hql.append(" where r.deliverer.id = d.id");
        // // hql.append(" and d.customer.id = :customerId");
        // // hql.append(" and r.address.id = :addressId");
        // Query query = this.getSession().createQuery(hql.toString());
        // // query.setLong("customerId", customerId);
        // query.setLong("addressId", addressId);
        // return query.list();
    }

    public List<DelivererRule> getDelivererRuleList(Long customerId, Long addressId, Long delivererId, Long stationId) {
        Query query = this
                .getSession()
                .createQuery(
                        "select dr from DelivererRule dr where dr.deliverer.status=1 and dr.address.id =:aId and dr.deliverer.customer.id=:customerId  and dr.deliverer.id=:dId and dr.deliverystation.id=:sId");
        query.setLong("customerId", customerId);
        query.setLong("aId", addressId);
        query.setLong("sId", stationId);
        query.setLong("dId", delivererId);
        return query.list();

    }

    /**
     * 根据小件员id+站点id+关键词id获取对应的匹配规则
     * <p>
     * 方法详细描述
     * </p>
     * @param customerId
     * @param stationId
     * @param addressId
     * @return
     * @since 1.0
     */
    public DelivererRule getDelivererRule(Long sourceDelivererId, Long stationId, Long addressId) {
        Query query = this
                .getSession()
                .createQuery(
                        "select dr from DelivererRule dr where dr.deliverer.status=1 and dr.address.id =:aId and dr.deliverer.id=:sourceDelivererId "
                                + " and dr.deliveryStation.id=:stationId");
        query.setLong("sourceDelivererId", sourceDelivererId);
        query.setLong("aId", addressId);
        query.setLong("stationId", stationId);
        return (DelivererRule) query.uniqueResult();
    }
    
    
    /**
     * 根据 站点id 获取对应的匹配规则
     * <p>
     * 方法详细描述
     * </p>
     * @param customerId
     * @param stationId
     * @return
     * @since 1.0
     */
    public List<DelivererRule> getDelivererRule(Long customerId, Long stationId) {
        Query query = this
                .getSession()
                .createQuery(
                        "select dr from DelivererRule dr where dr.deliverer.status=1 and dr.deliverer.customer.id=:customerId "
                                + " and dr.deliveryStation.id=:stationId");
        query.setLong("customerId", customerId);
        query.setLong("stationId", stationId);
        return   query.list();
    }
    

    public List getByAddressAndDeliverer(Long addressId, Long did, Long customerId) {
        String sql = "SELECT r.id FROM DELIVERER_RULES r left join DELIVERERS"
                + " s on r.DELIVERER_ID=s.ID WHERE r.RULE_TYPE=:ruleType AND r.RULE='' "
                + " AND s.CUSTOMER_ID=:customerId " + " AND r.ADDRESS_ID=:addressId";
        Query query = this.getSession().createSQLQuery(sql);
        query.setLong("addressId", addressId);
        query.setLong("customerId", customerId);
        query.setInteger("ruleType", DelivererRuleTypeEnum.fallback.getValue());
        return query.list();
    }

    /**
     * 根据客户编码+站点编码+addressId获取对应的小件员匹配信息
     * <p>
     * 方法详细描述
     * </p>
     * @param customerId
     * @param stationId
     * @param id
     * @return
     * @since 1.0
     */
    public List<DelivererRule> getDelivererRuleList(Long customerId, Long stationId, Long addressId) {
        Query query = this
                .getSession()
                .createQuery(
                        "select dr from DelivererRule dr where dr.deliverer.status=1 and dr.address.id =:aId and dr.deliverer.customer.id=:customerId "
                                + "and dr.deliveryStation.id=:sId ");
        query.setLong("customerId", customerId);
        query.setLong("aId", addressId);
        query.setLong("sId", stationId);
        return query.list();
    }

    public List<ZTreeNode> getAddressByDeliverer(Long customerId, String stationId, String delivererId) {
        Query query = this
                .getSession()
                .createQuery(
                        "select new cn.explink.tree.ZTreeNode( dsr.address.name,dsr.address.id,dsr.address.parentId,dsr.address.addressLevel,dsr.address.path ) from DelivererRule dsr where  dsr.deliveryStation.id=:stationId and dsr.deliveryStation.customer.id=:customerId and dsr.deliverer.id=:delivererId "
                                + "group by dsr.deliveryStation,dsr.deliverer,dsr.address");
        query.setLong("customerId", customerId);
        query.setLong("stationId", Long.parseLong(stationId));
        query.setLong("delivererId", Long.parseLong(delivererId));
        return query.list();
    }

    /**
     * 方法概要
     * <p>
     * 方法详细描述
     * </p>
     * @param customerId
     * @param stationId
     * @param delivererId
     * @return
     * @since 1.0
     */
    public List<DelivererStationRuleVo> getDelivererRule2(Long customerId, Long stationId, Long delivererId) {
        Query query = this
                .getSession()
                .createSQLQuery(
                        "select dr.id ruleId,dr.rule rule,dr.delivery_Station_Id stationId,dr.address_Id addressId,"
                                + "dr.DELIVERER_ID delivererId" + " from Deliverer_Rules dr where  dr.DELIVERER_ID ="
                                + delivererId + "  " + "and dr.delivery_Station_Id=" + stationId)

                .setResultTransformer(Transformers.aliasToBean(DelivererStationRuleVo.class));

        return query.list();
    }

    /**
     * 根据客户编码+站点id+小件员id获取小件员派送规则
     * <p>
     * 方法详细描述
     * </p>
     * @param customerId
     * @param deliveryStationId
     * @param id
     * @return
     * @since 1.0
     */
    public List<DelivererRule> getDelivererRuleListByDeliverer(Long customerId, Long deliveryStationId, Long delivererId) {
        Query query = this
                .getSession()
                .createQuery(
                        "select dr from DelivererRule dr where dr.deliverer.status=1 and dr.deliverer.id =:dId and dr.deliverer.customer.id=:customerId "
                                + "and dr.deliveryStation.id=:sId ");
        query.setLong("customerId", customerId);
        query.setLong("dId", delivererId);
        query.setLong("sId", deliveryStationId);
        return query.list();
    }

 

	public Map<String, Object> findInfo(Long stationId, Long delivererId,
			int page, int pageNumber, Long customerId) {
		Map<String, Object> map = new HashMap<String, Object>();
		StringBuffer sql = new StringBuffer();
		StringBuffer countsql = new StringBuffer();
	 
		sql.append("select dr.id ruleId,dr.rule rule,dr.delivery_Station_Id stationId,dr.address_Id addressId, adr.name addressName ,"
                                + "dr.DELIVERER_ID delivererId" + " from Deliverer_Rules dr inner join  address adr on dr.address_id=adr.id  where  dr.DELIVERER_ID ="
                                + delivererId + "  " + "and dr.delivery_Station_Id=" + stationId  
                                +" order by dr.id desc" );
		countsql.append("select count(id)   from Deliverer_Rules dr where  dr.DELIVERER_ID ="
                                + delivererId + "  " + "and dr.delivery_Station_Id=" + stationId );
 
		 
		 
		Query query = this.getSession().createSQLQuery(sql.toString()).setResultTransformer(Transformers.aliasToBean(DelivererStationRuleVo.class));
		Query countQuery = this.getSession().createSQLQuery(countsql.toString());
		int count = Integer.parseInt(countQuery.uniqueResult().toString());
		query.setFirstResult((pageNumber - 1) * page);
		query.setMaxResults(page);

		map.put("list", query.list());
		map.put("count", count);
		return map;
	}
}
