package cn.explink.schedule;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.explink.dao.ScheduledTaskDao;
import cn.explink.domain.ScheduledTask;
import cn.explink.util.DateTimeUtil;

public abstract class ScheduledWorker implements Worker {

	private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledWorker.class);

	@Autowired
	private ScheduledTaskDao scheduledTaskDao;

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void executeTask(Task task) {
		ScheduledTask scheduledTask = null;
		try {
			scheduledTask = this.lockTask(task.getTaskId());
			ScheduledWorker.LOGGER.info("lock task success. task id = {} ", scheduledTask.getId());
			if (Constants.TASK_STATUS_COMPLETED == scheduledTask.getStatus()) {
				ScheduledWorker.LOGGER.info("task is already completed. id = {}, referenceId = {}", new Object[] { scheduledTask.getId(), scheduledTask.getReferenceId() });
				return;
			}
			if (DateTimeUtil.isBefore(new Date(), scheduledTask.getFireTime())) {
				ScheduledWorker.LOGGER.info("task is not readdy to run. id = {}, fireTime = {}", new Object[] { scheduledTask.getId(), scheduledTask.getFireTime() });
				return;
			}
		} catch (Exception e) {
			ScheduledWorker.LOGGER.warn("lock task failed. taskId = {}", task.getTaskId());
		}

		try {
			if (scheduledTask != null) {
				if (this.doJob(scheduledTask)) {
					ScheduledWorker.LOGGER.info("task success. task id = " + scheduledTask.getId());
					scheduledTask.setStatus(Constants.TASK_STATUS_COMPLETED);
					scheduledTask.setCompletedTime(new Date());
				} else {
					ScheduledWorker.LOGGER.info("task failed. task id = " + scheduledTask.getId());
					this.handleFailedTask(scheduledTask);
				}
				this.getScheduledTaskDao().save(scheduledTask);
			}
		} catch (Exception e) {
			ScheduledWorker.LOGGER.error("task exception. taskId = {}", task.getTaskId(), e);
		}
	}

	/**
	 * 处理失败的任务，默认只增加尝试次数，子类可以覆盖此方法做进一步的安排
	 *
	 * @param scheduledTask
	 */
	protected void handleFailedTask(ScheduledTask scheduledTask) {
		scheduledTask.setTryCount(scheduledTask.getTryCount() + 1);
	}

	/**
	 * 任务对应的处理方法，由子类实现各任务的方法
	 *
	 * @param scheduledTask
	 * @return 任务是否成功
	 * @throws Exception
	 */
	protected abstract boolean doJob(ScheduledTask scheduledTask) throws Exception;

	private ScheduledTask lockTask(Long taskId) {
		return this.scheduledTaskDao.lock(taskId);
	}

	/**
	 * for some duplicate tasks, we need scheduled the next task after a task
	 * finished.
	 *
	 * @param currentTask
	 * @param nextJobTime
	 */
	protected void scheduleNextTask(ScheduledTask currentTask, Date nextJobTime) {
		ScheduledTask newTask = new ScheduledTask();
		BeanUtils.copyProperties(currentTask, newTask, ScheduledTask.class);
		newTask.setId(null);
		newTask.setFireTime(nextJobTime);
		newTask.setCreatedAt(new Date());
		this.getScheduledTaskDao().save(newTask);
	}

	protected ScheduledTaskDao getScheduledTaskDao() {
		return this.scheduledTaskDao;
	}

	protected void setScheduledTaskDao(ScheduledTaskDao scheduledTaskDao) {
		this.scheduledTaskDao = scheduledTaskDao;
	}

}
