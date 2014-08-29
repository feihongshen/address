package cn.explink.lucene;

import java.util.HashSet;
import java.util.Set;

import cn.explink.domain.Address;
import cn.explink.domain.Alias;

public class DictChange {

	private String dictName;

	private Set<String> removeSet = new HashSet<String>();

	private Set<String> addSet = new HashSet<String>();

	public String getDictName() {
		return dictName;
	}

	public void setDictName(String dictName) {
		this.dictName = dictName;
	}

	public Set<String> getRemoveSet() {
		return removeSet;
	}

	public void setRemoveSet(Set<String> removeSet) {
		this.removeSet = removeSet;
	}

	public Set<String> getAddSet() {
		return addSet;
	}

	public void setAddSet(Set<String> addSet) {
		this.addSet = addSet;
	}

	/**
	 * 添加一个地址变化
	 * @param address
	 */
	public void addAddress(Address address) {
		if (address.getOldName() != null) {
			removeSet.add(address.getOldName());
		}
		addSet.add(address.getName());
	}

	/**
	 * 添加一个别名变化
	 * @param alias
	 */
	public void addAlias(Alias alias) {
		if (alias.getOldName() != null) {
			removeSet.add(alias.getOldName());
		}
		addSet.add(alias.getName());
	}

}
