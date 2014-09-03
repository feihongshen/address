package cn.explink.modle;

public class SortInfo {
	private Integer columnId;
	public Integer getColumnId() {
		return columnId;
	}
	public void setColumnId(Integer columnId) {
		this.columnId = columnId;
	}
	public SortDirection getSortOrder() {
		return sortOrder;
	}
	public void setSortOrder(SortDirection sortOrder) {
		this.sortOrder = sortOrder;
	}
	private SortDirection sortOrder;
}
