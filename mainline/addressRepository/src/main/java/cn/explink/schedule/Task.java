package cn.explink.schedule;

public class Task implements Runnable {

	private Worker worker;
	
	private Long taskId;

	public Worker getWorker() {
		return worker;
	}

	public void setWorker(Worker worker) {
		this.worker = worker;
	}
	
	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	@Override
	public void run() {
		worker.executeTask(this);
	}
}
