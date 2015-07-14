package cn.explink.schedule.worker;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.explink.dao.AddressDao;
import cn.explink.dao.ScheduledTaskDao;
import cn.explink.dao.SystemConfigDao;
import cn.explink.domain.ScheduledTask;
import cn.explink.lucene.LuceneInitializer;
import cn.explink.schedule.Constants;
import cn.explink.schedule.ScheduledWorker;
import cn.explink.service.LuceneService;

@Component("updateIndexWorker")
public class UpdateIndexWorker extends ScheduledWorker {

	private static final Logger LOGGER = LoggerFactory.getLogger(UpdateIndexWorker.class);

	@Autowired
	private AddressDao addressDao;

	@Autowired
	private SystemConfigDao SystemConfigDao;

	@Autowired
	private ScheduledTaskDao scheduledTaskDao;

	@Autowired
	private LuceneService luceneService;

	@Override
	protected boolean doJob(ScheduledTask scheduledTask) throws Exception {
		if (!LuceneInitializer.inited) {
			UpdateIndexWorker.LOGGER.info("lucene is not inited. waiting lucene initialize");
			LuceneInitializer.class.wait();
		}
		List<ScheduledTask> scheduledTaskList = this.scheduledTaskDao.listAllTasksByType(Constants.TASK_TYPE_SUB_UPDATE_INDEX, true);
		if (scheduledTaskList.size() == 0) {
			return this.completeJob(scheduledTask);
		}

		List<Long> addressIdList = new ArrayList<Long>();
		List<Long> aliasIdList = new ArrayList<Long>();
		List<Long> taskIdList = new ArrayList<Long>(scheduledTaskList.size());
		for (ScheduledTask task : scheduledTaskList) {
			if (Constants.REFERENCE_TYPE_ADDRESS_ID.equals(task.getReferenceType())) {
				addressIdList.add(Long.parseLong(task.getReferenceId()));
			}
			if (Constants.REFERENCE_TYPE_ALIAS_ID.equals(task.getReferenceType())) {
				aliasIdList.add(Long.parseLong(task.getReferenceId()));
			}
			taskIdList.add(task.getId());
		}

		this.luceneService.updateIndex(addressIdList, aliasIdList);
		this.scheduledTaskDao.batchUpdateStatus(taskIdList, Constants.TASK_STATUS_COMPLETED);
		return this.completeJob(scheduledTask);
	}

	private boolean completeJob(ScheduledTask scheduledTask) {
		super.scheduleNextTask(scheduledTask, new Date());
		return true;
	}

}
