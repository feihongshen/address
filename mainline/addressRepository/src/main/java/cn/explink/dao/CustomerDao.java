
package cn.explink.dao;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import cn.explink.dao.support.BasicHibernateDaoSupport;
import cn.explink.domain.Customer;

@Repository
public class CustomerDao extends BasicHibernateDaoSupport<Customer, Long> {

    public CustomerDao() {
        super(Customer.class);
    }

    public Session getHSession() {
        return super.getSession();
    }

    public void updateSql(String sql) {
        super.getSession().createSQLQuery(sql).executeUpdate();
    }

}
