package cn.explink.modle;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 导入进度条
 * @author
 * 
 */
public class ImportProcessJson  {
	public String id="";
	private int total = 0;//总数
	private int success = 0;//成功
	private int failure = 0;//失败
	private int processed = 0;//已处理数
	private SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmssSSS");
	private boolean isFinish = false;
	
	
	public boolean isFinish() {
		return isFinish;
	}
	public void setFinish(boolean isFinish) {
		this.isFinish = isFinish;
	}
	public ImportProcessJson (){
		id=sdf.format(new Date());
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public int getSuccess() {
		return success;
	}
	public void setSuccess(int success) {
		this.success = success;
	}
	public int getFailure() {
		return failure;
	}
	public void setFailure(int failure) {
		this.failure = failure;
	}
	public int getProcessed() {
		return processed;
	}
	public void setProcessed(int processed) {
		this.processed = processed;
	}
	public int percent;
	public void cal() {
		double d = 0.0;
		if(this.total!=0){
			  d = (this.processed/(this.total+0.0))*100;
		}else{
			  d = 0;
		}
		percent=(int)d;
	}
	
}
