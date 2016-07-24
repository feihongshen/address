
package cn.explink.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import cn.explink.dao.support.BasicHibernateDaoSupport;
import cn.explink.dao.support.DataInfo;
import cn.explink.domain.Address;
import cn.explink.domain.Deliverer;
import cn.explink.domain.DeliveryStation;
import cn.explink.domain.Vendor;
import cn.explink.domain.enums.DeliveryStationStausEnmu;
import cn.explink.modle.ComboBox;
import cn.explink.quick.AddressStationPair;
import cn.explink.tree.ZTreeNode;

@Repository
public class DeliveryStationDao extends BasicHibernateDaoSupport<DeliveryStation, Long> {

    public DeliveryStationDao() {
        super(DeliveryStation.class);
    }

    public DeliveryStation getDeliveryStation(Long customerId, Long externalId) {
        String hql = "from DeliveryStation where customer.id = :customerId and externalId = :externalId";
        Query query = this.getSession().createQuery(hql);
        query.setLong("customerId", customerId);
        query.setLong("externalId", externalId);
        return (DeliveryStation) query.uniqueResult();
    }

    public List<Address> getAddress(Long id) {
        String hql = "from Address a where a.id in( select address.id from  DeliveryStationRule where deliveryStation.id=:id )";
        Query query = this.getSession().createQuery(hql);
        query.setLong("id", id);
        return query.list();
    }
    public List<Deliverer> getDeliverer(Long id) {
        String hql = "from Deliverer a where a.deliveryStationId=:id )";
        Query query = this.getSession().createQuery(hql);
        query.setLong("id", id);
        return query.list();
    }
    @SuppressWarnings("unchecked")
    public List<Address> getAddressByIds(Set<Long> addIds) {
        if (addIds.isEmpty()) {
            return new ArrayList<Address>();
        }
        String hql = "from Address a where a.id in :addIds ";
        Query query = this.getSession().createQuery(hql);
        query.setParameterList("addIds", addIds);
        return query.list();
    }

    public List<DeliveryStation> listAll(Long customerId) {
        if (customerId == null) {
            return new ArrayList<DeliveryStation>();
        }
        String hql = "from DeliveryStation where customer.id=:customerId and status=1 order by convert_mine(name , 'gbk')";
        Query query = this.getSession().createQuery(hql);
        query.setLong("customerId", customerId);
        return query.list();
    }

    public List<ComboBox> getComBoxDeliveryStation(Long customerId) {
        String hql = "select new cn.explink.modle.ComboBox(ds.id,ds.name) from DeliveryStation ds where customer.id = :customerId and status="
                + DeliveryStationStausEnmu.valid.getValue() + " order by ds.name ";
        Query query = this.getSession().createQuery(hql);
        query.setLong("customerId", customerId);
        return query.list();
    }

    public DeliveryStation getByNameAndCustomerId(String deliveryStationName, Long customerId) {
        String hql = "from DeliveryStation where customer.id = :customerId and name=:name ";
        Query query = this.getSession().createQuery(hql);
        query.setLong("customerId", customerId);
        query.setString("name", deliveryStationName);
        return (DeliveryStation) query.uniqueResult();
    }

    public List<DeliveryStation> fuzzySearchByNameAndCustomerId(String deliveryStationName, Long customerId) {
        String hql = "from DeliveryStation where customer.id = :customerId and name like:name ";
        Query query = this.getSession().createQuery(hql);
        query.setLong("customerId", customerId);
        query.setString("name", deliveryStationName);
        return query.list();
    }

    public List<Vendor> listAllVendor(Long customerId) {
        String hql = "from Vendor where customer.id = :customerId and status=1";
        Query query = this.getSession().createQuery(hql);
        query.setLong("customerId", customerId);
        return query.list();
    }

    /**
     * 关键词维护工具(广州通路)
     * @param delStatIdSet
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<DeliveryStation> getDeliverStation(Set<Long> delStatIdSet) {
        String hql = "from DeliveryStation where  id in (:ids)";
        Query query = this.getSession().createQuery(hql);
        query.setParameterList("ids", delStatIdSet);

        return query.list();
    }

    public DeliveryStation getDeliveryStationByUid(String uid) {
        String hql = "from DeliveryStation where  uid = :uid";
        Query query = this.getSession().createQuery(hql);
        query.setParameter("uid", uid);

        return (DeliveryStation) query.uniqueResult();
    }

    public DeliveryStation getDeliveryStationById(Long id) {
        String hql = "from DeliveryStation where  id = :id";
        Query query = this.getSession().createQuery(hql);
        query.setParameter("id", id);

        return (DeliveryStation) query.uniqueResult();
    }

    public List<DeliveryStation> getAllDeliverStation() {
        String hql = "from DeliveryStation";
        Query query = this.getSession().createQuery(hql);

        return query.list();
    }

    public List<DeliveryStation> getDeliverStation(List<AddressStationPair> pairList) {
        Set<Long> delStatIdSet = this.getDeliverStationIdSet(pairList);

        if (delStatIdSet.size() == 0) {
            return new ArrayList<DeliveryStation>();
        } else {
            return this.getDeliverStation(delStatIdSet);
        }
    }

    private Set<Long> getDeliverStationIdSet(List<AddressStationPair> pairList) {
        Set<Long> delStatIdSet = new HashSet<Long>();
        for (AddressStationPair pair : pairList) {
            delStatIdSet.add(pair.getStationId());
        }
        return delStatIdSet;
    }

    public List<ZTreeNode> listAllToTreeNode(Long customerId, Integer page, Integer pageSize) {
        if (customerId == null) {
            return new ArrayList<ZTreeNode>();
        }
        StringBuilder hql = new StringBuilder(
                "select new cn.explink.tree.ZTreeNode( a.name,a.id,customer.id ,1,'',false) from DeliveryStation a  where  status=1 and customer.id = :customerId");

        StringBuilder hqlCount = new StringBuilder(
                "select count(a.id) from DeliveryStation a where  status=1 and customer.id =:customerId ");

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("customerId", customerId);
        DataInfo di = this.findByHql(hql.toString(), hqlCount.toString(), page, pageSize, param);
        List<ZTreeNode> l = (List<ZTreeNode>) di.getResult();
        for (int i = 0; i < l.size(); i++) {
            if (i == 0) {
                l.get(i).setMaxPage(di.getPageCount());
                l.get(i).setPage(di.getPage());
            } else {
                l.get(i).setPageSize(pageSize);
                l.get(i).setPage(1);
            }
        }
        return l;

    }
}
