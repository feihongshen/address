
package cn.explink.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import cn.explink.dao.support.BasicHibernateDaoSupport;
import cn.explink.domain.Deliverer;
import cn.explink.modle.ComboBox;

@Repository
public class DelivererDao extends BasicHibernateDaoSupport<Deliverer, Long> {

    public DelivererDao() {
        super(Deliverer.class);
    }

    public Deliverer getDelivererById(Long id) {
        String hql = "from Deliverer where id = :id";
        Query query = this.getSession().createQuery(hql);
        query.setLong("id", id);
        return (Deliverer) query.uniqueResult();
    }

    public Deliverer getDeliverer(Long customerId, Long externalId) {
        String hql = "from Deliverer where externalId = :externalId and customer.id = :customerId";
        Query query = this.getSession().createQuery(hql);
        query.setLong("externalId", externalId);
        query.setLong("customerId", customerId);
        return (Deliverer) query.uniqueResult();
    }

    public Deliverer getByNameAndCustomerId(String delivererName, Long customerId) {
        String hql = "from Deliverer where name = :name and customer.id = :customerId";
        Query query = this.getSession().createQuery(hql);
        query.setString("name", delivererName);
        query.setLong("customerId", customerId);
        return (Deliverer) query.uniqueResult();
    }

    public List<Deliverer> listAll(Long customerId) {
        if (customerId == null) {
            return new ArrayList<Deliverer>();
        }
        String hql = "from Deliverer where customer.id=:customerId and status=1";
        Query query = this.getSession().createQuery(hql);
        query.setLong("customerId", customerId);
        return query.list();
    }

    /**
     * 根据站点id获取小件员list
     * <p>
     * 方法详细描述
     * </p>
     * @param stationId
     * @param customerId
     * @return
     * @since 1.0
     */
    public List<ComboBox> getDelivererComBoxByStation(Long stationId, Long customerId) {
        if (customerId == null) {
            return new ArrayList<ComboBox>();
        }

        String hql = "select new cn.explink.modle.ComboBox(d.id,d.name) from Deliverer d where  d.deliveryStationId = :stationId  and d.customer.id=:customerId and d.status=1";
        Query query = this.getSession().createQuery(hql);
        query.setLong("customerId", customerId);
        query.setLong("stationId", stationId);
        return query.list();
    }

}
