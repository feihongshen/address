
package cn.explink.dao.support;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.hibernate.Criteria;
import org.hibernate.LockOptions;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Example;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The default DAO implements use Hibernate.</p>
 */

// @Repository
public abstract class BasicHibernateDaoSupport<Entity, ID extends Serializable> {

    private static final Logger logger = LoggerFactory.getLogger(BasicHibernateDaoSupport.class);

    private int BATCH_SIZE = 50;

    @Resource
    protected SessionFactory sessionFactory;

    protected Class<Entity> persistentClass;

    public BasicHibernateDaoSupport(Class<Entity> clz) {
        this.persistentClass = clz;
    }

    @SuppressWarnings("unchecked")
    public Entity get(ID id) {
        Session session = this.getSession();
        return (Entity) session.get(this.persistentClass, id);
    }

    @SuppressWarnings("unchecked")
    public Entity load(ID id) {
        Session session = this.getSession();
        return (Entity) session.load(this.persistentClass, id);
    }

    public List<Entity> findByExample(Entity exampleInstance, String[] excludeProperty) {
        Session session = this.getSession();
        Criteria criteria = session.createCriteria(this.persistentClass);

        Example example = Example.create(exampleInstance);
        for (String element : excludeProperty) {
            example.excludeProperty(element);
        }
        criteria.add(example);
        return criteria.list();
    }

    public Entity save(Entity entity) {
        Session session = this.getSession();
        session.saveOrUpdate(entity);
        return entity;
    }

    public void delete(Entity entity) {
        this.getSession().delete(entity);
    }

    public void beginTransaction() {
        this.getSession().beginTransaction();
    }

    public void commitTransaction() {
        this.getSession().getTransaction().commit();
    }

    public void insert(Collection<Entity> items) {
        if ((items != null) && (items.size() > 0)) {
            Session session = this.getSession();
            int count = 0;
            for (Entity t : items) {
                session.save(t);
                count++;
                if ((count % this.BATCH_SIZE) == 0) {
                    session.flush();
                    session.clear();
                }
            }
        }
    }

    public void insert(Entity[] items) {
        if ((items != null) && (items.length > 0)) {
            Session session = this.getSession();
            int count = 0;
            for (Entity t : items) {
                session.save(t);
                count++;
                if ((count % this.BATCH_SIZE) == 0) {
                    session.flush();
                    session.clear();
                }
            }
        }
    }

    /**
     * lock an entity
     * @param id
     * @return
     */
    @SuppressWarnings("unchecked")
    protected Entity lock(ID id) {
        Session session = this.getSession();
        Entity entity = null;
        try {
            // 设置超时时间为较小的值，如果此资源已经被其他事物加锁，则迅速失败当前查询
            // http://dev.mysql.com/doc/innodb-plugin/1.0/en/innodb-other-changes-innodb_lock_wait_timeout.html
            // http://dev.mysql.com/doc/refman/5.1/en/innodb-parameters.html#sysvar_innodb_lock_wait_timeout
            try {
                Query query = session.createSQLQuery("set innodb_lock_wait_timeout = 1");
                query.executeUpdate();
            } catch (Exception e) {
                BasicHibernateDaoSupport.logger.warn("can't update innodb_lock_wait_timeout. ", e.getMessage());
            }

            entity = (Entity) session.load(this.persistentClass, id, LockOptions.UPGRADE);
        } finally {
            try {
                Query query = session.createSQLQuery("show global variables like 'innodb_lock_wait_timeout'");
                Object[] globalValue = (Object[]) query.list().get(0);
                // 恢复超时时间为数据库的全局设置
                query = session.createSQLQuery("set innodb_lock_wait_timeout = " + globalValue[1]);
                query.executeUpdate();
            } catch (Exception e) {
                // ignore
            }
        }

        return entity;
    }

    protected Session getSession() {
        return this.sessionFactory.getCurrentSession();
    }

    public BigInteger getCount(String tableName) {
        Session session = this.getSession();
        Query query = session.createSQLQuery("select count(1) from " + tableName);
        return (BigInteger) query.uniqueResult();
    }

    public List<Entity> findListbySql(final String sql) {
        Query querys = this.getSession().createSQLQuery(sql);
        return querys.list();
    }

    public <Entity> List<Entity> loadAll(final Class<Entity> entityClass) {
        Criteria criteria = this.createCriteria(entityClass);
        return criteria.list();
    }

    private <T> Criteria createCriteria(Class<T> entityClass) {
        Criteria criteria = this.getSession().createCriteria(entityClass);
        return criteria;
    }

    public List<Entity> findListByHql(String hql, Map<String, Object> param) {

        Query querys = this.getSession().createQuery(hql);
        if (param != null) {
            for (String k : param.keySet()) {
                querys.setParameter(k, param.get(k));
            }
        }
        return querys.list();

    }

    public DataInfo findByHql(String hql, String hqlCount, Integer page, Integer pageSize, Map<String, Object> param) {
        DataInfo d = new DataInfo();
        if ((page == null) || (page <= 0)) {
            page = 1;
        }
        if ((pageSize == null) || (pageSize <= 0)) {
            pageSize = 100;
        }
        Query querys = this.getSession().createQuery(hql);
        Query queryCount = this.getSession().createQuery(hqlCount);
        if (param != null) {
            for (String k : param.keySet()) {
                querys.setParameter(k, param.get(k));
                queryCount.setParameter(k, param.get(k));
            }
        }
        List l = querys.setFirstResult((page - 1) * pageSize).setMaxResults(pageSize).list();
        Integer count = ((Long) queryCount.uniqueResult()).intValue();
        d.setResult(l);
        d.setTotalCount(count);
        d.setPage(page);
        d.setPageSize(pageSize);
        d.setPageCount(this.maxPageSize(count, pageSize));
        return d;
    }

    public int maxPageSize(int count, int pageSize) {
        if (pageSize > 0) {
            if ((count % pageSize) != 0) {
                return (count / pageSize) + 1;
            } else {
                return (count / pageSize);
            }
        }
        return 0;
    }
}
