package cn.explink.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;

@NamedQueries({
		@NamedQuery(name = "ScheduledTask.listAllTasksByType", query = "select id from ScheduledTask where fireTime < now() and status = 0 and taskType = :taskType"),
		@NamedQuery(name = "ScheduledTask.findTaskByTypeAndReferenceId", query = "from ScheduledTask where taskType = :taskType and referenceId = :referenceId"),
		@NamedQuery(name = "ScheduledTask.countTimeOutTasks", query = "select count(*) as counts from ScheduledTask where fireTime < :fireTime and status = 0 and taskType = :taskType") })
@Entity
@Table(name = "SCHEDULED_TASKS")
public class ScheduledTask {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "TASK_TYPE", length = 50, nullable = false)
	private String taskType;

	@Column(name = "STATUS", nullable = false)
	private Integer status;

	@Column(name = "REFERENCE_TYPE", length = 50)
	private String referenceType;

	@Column(name = "REFERENCE_ID", length = 32)
	private String referenceId;

	@Column(name = "FIRE_TIME", nullable = false)
	private Date fireTime;

	@Column(name = "COMPLETED_TIME")
	private Date completedTime;

	@Column(name = "CREATED_AT")
	private Date createdAt;

	@Column(name = "TRY_COUNT")
	private Integer tryCount;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTaskType() {
		return taskType;
	}

	public void setTaskType(String taskName) {
		this.taskType = taskName;
	}

	public String getReferenceType() {
		return referenceType;
	}

	public void setReferenceType(String referenceType) {
		this.referenceType = referenceType;
	}

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	public Date getFireTime() {
		return fireTime;
	}

	public void setFireTime(Date fireTime) {
		this.fireTime = fireTime;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date creationDate) {
		this.createdAt = creationDate;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getCompletedTime() {
		return completedTime;
	}

	public void setCompletedTime(Date completedDate) {
		this.completedTime = completedDate;
	}

	public Integer getTryCount() {
		return tryCount;
	}

	public void setTryCount(Integer tryCount) {
		this.tryCount = tryCount;
	}

}