
package cn.explink.lucene;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.explink.domain.Address;
import cn.explink.domain.Alias;

/**
 * 地址过滤逻辑.
 * <p>
 * 1.加入了地址名称在全地址中的匹配权重.
 * <p>
 * 2.考虑匹配节点的权重加入地址层级关系(weigth= parentWeigth + level*matchWeight).
 * @author zhaoshb
 * @since AR1.0
 */
public class AddressFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddressFilter.class);

    // 四个直辖市的ID集合
    private static final Set<Long> MUNICIPALITY_ID_SET = new HashSet<Long>();
    static {
        AddressFilter.MUNICIPALITY_ID_SET.add(2L);
        AddressFilter.MUNICIPALITY_ID_SET.add(21L);
        AddressFilter.MUNICIPALITY_ID_SET.add(862L);
        AddressFilter.MUNICIPALITY_ID_SET.add(2466L);
    }

    public static List<Address> filter(String strAddr, List<Address> addrList) {
        if (addrList.isEmpty()) {
            return addrList;
        }
        AddressForest forest = new AddressForest(strAddr, addrList);
        MatchResult matchResult = forest.getMatchResult();

        AddressFilter.LOGGER.info("得分最高的地址是{}，得分为{}", matchResult.getAddress(), matchResult.getWeight());

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
            this.weight = this.getMatchScore(addr, fullAddr);
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
                this.path = this.getAddress().getPath() + "-" + this.getAddress().getId() + "-";
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
            // added by songkaojun 2015-01-28 添加别名权重
            List<Alias> aliasList = address.getAliasList();
            // 取关键词和该关键词的别名中得分最高的一个最为最终的得分
            if ((null != aliasList) && (aliasList.size() > 0)) {
                int score = this.getScore(address.getName(), address.getAddressLevel(), fullAddr,
                        address.getParentId());
                for (Alias alias : aliasList) {
                    String name = alias.getName();
                    int tempScore = this.getScore(name, address.getAddressLevel(), fullAddr, address.getParentId());
                    if (tempScore > score) {
                        score = tempScore;
                    }
                }
                return score;
            }
            return this.getScore(address.getName(), address.getAddressLevel(), fullAddr, address.getParentId());
        }

        private int getScore(String addressName, Integer addressLevel, String fullAddr, Long parentId) {
            int score = 0;
            int factor = 3;
            // 区写错（最常见），或者一路跨两区，地址库中只在一个区下挂这条路
            // 这种情况系数是其他层级的三分之一
            if (Integer.valueOf(3).equals(addressLevel)) {
                factor = 1;
            } // 市级别极少会写错，并且错分不同市极不能容忍
            else if (Integer.valueOf(2).equals(addressLevel)) {
                if (!AddressFilter.MUNICIPALITY_ID_SET.contains(parentId)) {
                    factor = 10;
                }
            }
            String lowerAddr = addressName.toLowerCase();
            String lowerFullAddr = fullAddr.toLowerCase();
            int length = addressName.length();
            if (lowerFullAddr.contains(lowerAddr)) {
                score = length * factor;
            } else {
                score = -length * factor;
            }
            return score;
        }
    }

    private static class MatchResult {

        private int weight = -1000;

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
