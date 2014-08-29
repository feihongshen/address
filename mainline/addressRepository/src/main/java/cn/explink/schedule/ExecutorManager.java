package cn.explink.schedule;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * thread pool manager
 */
public class ExecutorManager {

	private static ExecutorManager instance;

	private Map<String, ExecutorService> executors = new HashMap<String, ExecutorService>();

	private ExecutorManager() {
	}

	public static ExecutorManager getInstance() {
		if (instance == null) {
			synchronized (ExecutorManager.class) {
				if (instance == null) {
					instance = new ExecutorManager();
					instance.init();
				}
			}
		}
		return instance;
	}

	/**
	 * 初始化各种任务的线程池
	 */
	private void init() {
		// 公共线程池
		 ExecutorService executor = Executors.newFixedThreadPool(Constants.COMMON_TASK_THREAD_POOL_SIZE);
		executors.put(Constants.COMMON_TASK_THREAD_POOL_NAME, executor);
	}

	public ExecutorService getExecutorService(String name) {
		return executors.get(name);
	}
}
