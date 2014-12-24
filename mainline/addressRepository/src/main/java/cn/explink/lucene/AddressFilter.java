package cn.explink.lucene;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.explink.domain.Address;

/**
 * 地址过滤逻辑.
 * <p>
 * 1.加入了地址名称在全地址中的匹配权重.
 * <p>
 * 2.考虑匹配节点的权重加入地址层级关系(weigth= parentWeigth + level*matchWeight).
 *
 * @author zhaoshb
 * @since AR1.0
 *
 */
public class AddressFilter {

	public static List<Address> filter(String strAddr, List<Address> addrList) {
		if (addrList.isEmpty()) {
			return addrList;
		}
		AddressForest forest = new AddressForest(strAddr, addrList);
		MatchResult matchResult = forest.getMatchResult();

		return matchResult.getAddress();
	}

	private static class AddressForest {

		private List<AddressTree> treeList = null;

		private String fullAddr = null;

		public AddressForest(String fullAddr, List<Address> addrList) {
			this.fullAddr = fullAddr;
			// 将地址按照层级递增排序.
			this.sortAddrByLevel(addrList);
			for (Address addr : addrList) {
				this.addAddress(addr);
			}
		}

		public void addAddress(Address addr) {
			AddressTreeNode addrTreeNode = this.createAddrTreeNode(addr);
			if (this.getTreeList().isEmpty()) {
				this.getTreeList().add(this.createAddressTree(addrTreeNode));
			} else {
				boolean added = false;
				for (AddressTree tree : this.getTreeList()) {
					added |= tree.addAddrTreeNode(addrTreeNode);
				}
				if (!added) {
					this.getTreeList().add(this.createAddressTree(addrTreeNode));
				}
			}
		}

		public MatchResult getMatchResult() {
			MatchResult matchResult = new MatchResult();
			for (AddressTree tree : this.getTreeList()) {
				tree.match(matchResult);
			}
			return matchResult;
		}

		private void sortAddrByLevel(List<Address> addrList) {
			Collections.sort(addrList, new Comparator<Address>() {

				@Override
				public int compare(Address o1, Address o2) {

					return o1.getAddressLevel() - o2.getAddressLevel();
				}
			});
		}

		private List<AddressTree> getTreeList() {
			if (this.treeList == null) {
				this.treeList = new ArrayList<AddressTree>();
			}
			return this.treeList;
		}

		private AddressTree createAddressTree(AddressTreeNode addrTreeNode) {
			return new AddressTree(addrTreeNode);
		}

		private AddressTreeNode createAddrTreeNode(Address addr) {
			return new AddressTreeNode(addr, this.getFullAddr());
		}

		public String getFullAddr() {
			return this.fullAddr;
		}

	}

	private static class AddressTree {

		private AddressTreeNode rootNode = null;

		public AddressTree(AddressTreeNode rootNode) {
			this.rootNode = rootNode;
		}

		public boolean addAddrTreeNode(AddressTreeNode newNode) {
			return this.getRootNode().addAddressTreeNode(newNode);
		}

		public void match(MatchResult result) {
			this.findMaxWeightNode(this.getRootNode(), result);
		}

		public void findMaxWeightNode(AddressTreeNode node, MatchResult result) {
			if (node.hasChild()) {
				for (AddressTreeNode childNode : node.getChildNodeList()) {
					this.findMaxWeightNode(childNode, result);
				}
			} else {
				result.compare(node);
			}
		}

		private AddressTreeNode getRootNode() {
			return this.rootNode;
		}

	}

	private static class AddressTreeNode {
		private Address address = null;

		private String path = null;

		private List<AddressTreeNode> childNodeList = null;

		// 权重，初始化时仅为Address名称和全地址的匹配字符数.
		// 在加入父节点后权重修改父权重+当前权重.
		private int weight = 0;

		public AddressTreeNode(Address addr, String fullAddr) {
			this.address = addr;
			this.weight = this.getMatchScore(addr, fullAddr) * this.getAddress().getAddressLevel().intValue();
			this.initPath();
		}

		public boolean addAddressTreeNode(AddressTreeNode treeNode) {
			boolean contains = treeNode.getPath().contains(this.getPath());
			if (!contains) {
				return false;
			}
			if (this.getChildNodeList().isEmpty()) {
				treeNode.addWeight(this.getWeight());
				this.getChildNodeList().add(treeNode);
			} else {
				boolean childAdded = false;
				for (AddressTreeNode childNode : this.getChildNodeList()) {
					childAdded |= childNode.addAddressTreeNode(treeNode);
				}
				if (!childAdded) {
					treeNode.addWeight(this.getWeight());
					this.getChildNodeList().add(treeNode);
				}
			}
			return contains;
		}

		public String getPath() {
			return this.path;
		}

		public int getWeight() {
			return this.weight;
		}

		public void addWeight(int weight) {
			this.weight += weight;
		}

		public boolean hasChild() {
			return !this.getChildNodeList().isEmpty();
		}

		private void initPath() {
			String addrPath = this.getAddress().getPath();
			if ((addrPath == null) || addrPath.isEmpty()) {
				this.path = Long.toString(this.getAddress().getId());
			} else {
				this.path = this.getAddress().getPath() + "-" + this.getAddress().getId();
			}
		}

		private Address getAddress() {
			return this.address;
		}

		private List<AddressTreeNode> getChildNodeList() {
			if (this.childNodeList == null) {
				this.childNodeList = new ArrayList<AddressTreeNode>();
			}
			return this.childNodeList;
		}

		private int getMatchScore(Address address, String fullAddr) {
			String partAddr = address.getName();
			int length = partAddr.length();
			int index = -1;
			while (length > 0) {
				index = fullAddr.indexOf(partAddr.substring(0, length));
				if (index != -1) {
					break;
				}
				length--;
			}
			if (length == 0) {
				return -1;
			}
			// 关键词越靠后权重越高.
			return length + index;
		}
	}

	private static class MatchResult {
		private int weight = 0;

		private List<Address> address = null;

		public int getWeight() {
			return this.weight;
		}

		public List<Address> getAddress() {
			if (this.address == null) {
				this.address = new ArrayList<Address>();
			}
			return this.address;
		}

		public void compare(AddressTreeNode treeNode) {
			if (this.getWeight() < treeNode.getWeight()) {
				this.getAddress().clear();
				this.getAddress().add(treeNode.getAddress());
				this.setWeight(treeNode.getWeight());
			} else if (this.getWeight() == treeNode.getWeight()) {
				this.getAddress().add(treeNode.getAddress());
			} else {
			}
		}

		private void setWeight(int weight) {
			this.weight = weight;
		}
	}
}
