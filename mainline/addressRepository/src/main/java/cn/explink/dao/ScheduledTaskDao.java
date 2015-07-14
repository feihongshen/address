package cn.explink.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import cn.explink.dao.support.BasicHibernateDaoSupport;
import cn.explink.domain.ScheduledTask;

@Repository
public class ScheduledTaskDao extends BasicHibernateDaoSupport<ScheduledTask, Long> {

	public ScheduledTaskDao() {
		super(ScheduledTask.class);
	}

	@Override
	public ScheduledTask lock(Long id) {
		return super.lock(id);
	}

	public List<Long> listAllTasksByType(String taskType) {
		String sql = "select id from ScheduledTask where fireTime < now() and status = 0 and taskType = :taskType";
		Query query = this.getSession().createQuery(sql);
		query.setString("taskType", taskType);
		return query.list();
	}

	public List<ScheduledTask> listAllTasksByType(String taskType, boolean orderByReferenceId) {
		StringBuilder sql = new StringBuilder("from ScheduledTask where fireTime < now() and status = 0 and taskType = :taskType");
		if (orderByReferenceId) {
			sql.append(" order by referenceId");
		}
		Query query = this.getSession().createQuery(sql.toString());
		query.setString("taskType", taskType);
		return query.list();
	}

	public int batchUpdateStatus(List<Long> idList, int status) {
		String sql = "update ScheduledTask set status = :status ,completedTime=:date where id in (:idList)";
		Query query = this.getSession().createQuery(sql.toString());
		query.setParameterList("idList", idList);
		query.setInteger("status", status);
		query.setDate("date", new Date());
		return query.executeUpdate();
	}

	// public int countTimeOutTasks(String[] taskTypes, Date time) {
	// StringBuilder sql = new
	// StringBuilder("select count(1) from SCHEDULED_TASKS where STATUS = 0 and task_type in (");
	// for (int i = 0; i < taskTypes.length; i++) {
	// if (i != 0) {
	// sql.append(", ");
	// }
	// sql.append("'").append(taskTypes[i]).append("'");
	// }
	// sql.append(") and fire_time < ?");
	// return getJdbcTemplate().queryForInt(sql.toString(), time);
	// }

	// public void cleanUpOldTasks(Date date, int taskStatus) {
	// String sql =
	// "delete from SCHEDULED_TASKS where STATUS = ? and FIRE_TIME <= ?";
	// getJdbcTemplate().update(sql, taskStatus, date);
	// }

}
