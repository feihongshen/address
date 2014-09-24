package cn.explink.service;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.internal.CriteriaImpl;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.transaction.annotation.Transactional;

import cn.explink.dao.support.BasicHibernateDaoSupport;
import cn.explink.modle.ComboBox;
import cn.explink.modle.DataGrid;
import cn.explink.modle.DataGridReturn;
import cn.explink.qbc.CriteriaQuery;
import cn.explink.qbc.PagerUtil;

@Transactional
public class CommonServiceImpl<T, ID extends Serializable> extends
BasicHibernateDaoSupport  {
	protected String entitySql;
	protected String countSql;
	protected String tableName;
	public CommonServiceImpl(Class<? extends T> entityClass) {
		super(entityClass);
		initTableName();
		entitySql = "select * from " + tableName + " WHERE 1 = 1 ";
		countSql = "select count(1) from " + tableName + " WHERE 1 = 1 ";
	}
	private void initTableName() {
		Annotation annotation = persistentClass.getAnnotation(Table.class);
		if (annotation != null) {
			Table table = (Table) annotation;
			tableName = table.name();
			if (tableName == null) {
//				use class name as table name if it is not declared.
				tableName = persistentClass.getSimpleName();
			}
		}
	}
	public List<ComboBox> findComboBox(SqlRowSet rs) {
		List<ComboBox> list=new ArrayList<ComboBox>();
		ComboBox cb0=new ComboBox();
		cb0.setId("");
		cb0.setText("全部");
		list.add(cb0);
		while (rs.next()) { 
			ComboBox cb=new ComboBox();
			cb.setId(String.valueOf(rs.getInt("id")));
			cb.setText(rs.getString("name"));
			list.add(cb);
        }  
		return list;
	}
	

	public DataGridReturn getDataGridReturn(final CriteriaQuery cq,
			final boolean isOffset) {
		Criteria criteria = cq.getDetachedCriteria().getExecutableCriteria(
				getSession());
		CriteriaImpl impl = (CriteriaImpl) criteria;
		// 先把Projection和OrderBy条件取出来,清空两者来执行Count操作
		Projection projection = impl.getProjection();
		final int allCounts = ((Long) criteria.setProjection(
				Projections.rowCount()).uniqueResult()).intValue();
		criteria.setProjection(projection);
		if (projection == null) {
			criteria.setResultTransformer(CriteriaSpecification.ROOT_ENTITY);
		}
		if (StringUtils.isNotBlank(cq.getDataGrid().getSort())) {
			cq.addOrder(cq.getDataGrid().getSort(), cq.getDataGrid().getOrder());
		}

		// 判断是否有排序字段
		if (!cq.getOrdermap().isEmpty()) {
			cq.setOrder(cq.getOrdermap());
		}
		int pageSize = cq.getPageSize();// 每页显示数
		int curPageNO = PagerUtil.getcurPageNo(allCounts, cq.getCurPage(),
				pageSize);// 当前页
		int offset = PagerUtil.getOffset(allCounts, curPageNO, pageSize);
		if (isOffset) {// 是否分页
			criteria.setFirstResult(offset);
			criteria.setMaxResults(cq.getPageSize());
		} else {
			pageSize = allCounts;
		}
		// DetachedCriteriaUtil.selectColumn(cq.getDetachedCriteria(),
		// cq.getField().split(","), cq.getClass1(), false);
		List<T> list = criteria.list();
		setRlation(list);
		cq.getDataGrid().setResults(list);
		cq.getDataGrid().setTotal(allCounts);
		return new DataGridReturn(allCounts, list);

	}
	
	protected void setRlation(List<T> list) {
		// TODO Auto-generated method stub
		
	}
	public  List<T> getAll() {
		// TODO Auto-generated method stub
		return loadAll(persistentClass);
	}
	
	public DataGridReturn getDataGridReturnBySql(DataGrid dataGrid,String sql) {
		Integer allCounts=getCount(tableName).intValue();
		List<T> list = findListbySql(sql);
		dataGrid.setResults(list);
		dataGrid.setTotal(allCounts);
		return new DataGridReturn(allCounts, list);
	}
	public void delete(ID id) {
		T entity=(T) getSession().load(persistentClass, id);
		getSession().delete(entity);
	}
}
