package cn.explink.lucene;

import java.util.ArrayList;
import java.util.List;

import cn.explink.domain.Address;

public class AddressFilter {

	/**
	 * 从索引匹配的地址中过滤出符合条件的地址
	 * 1. 每个地址和剩余地址匹配，判别1：是否在路径上 2：是否包含过省市区关键字
	 * 2. 返回：不再路径上，包含过省市区关键字的地址列表 
	 * 
	 * @param sourceAddressList
	 * @return
	 */
	public static List<Address> filter(List<Address> sourceAddressList) {
		List<AddressCompare> lc = new ArrayList<AddressCompare> ();
		if(sourceAddressList!=null&&!sourceAddressList.isEmpty()){
			for(Address a:sourceAddressList){
				a.setPath("-"+a.getPath()+"-");
				lc.add(new AddressCompare(a) );
			}
			for(int i=0;i<lc.size();i++){
				for(int j=i;j<lc.size();j++){
					if(i!=j)
					compare(lc.get(i),lc.get(j));
				}
			}
		}else{
			return new ArrayList<Address> ();
		}
		List<Address> result = new ArrayList<Address>();
		for(AddressCompare ac:lc){
		    if(ac.hasSsq&&!ac.isPath){
		    	result.add(ac.address);
		    }
		}
		return result;
	}

	private static void compare(AddressCompare l1,
			AddressCompare l2) {
		if(l2.address.getPath().indexOf("-"+l1.address.getId()+"-") >0){
			if(l1.address.getAddressLevel()<=3){
				l2.hasSsq=true;
			}
			l1.isPath=true;
		}
		if(l1.address.getPath().indexOf("-"+l2.address.getId()+"-") >0){
			if(l2.address.getAddressLevel()<=3){
				l1.hasSsq=true;
			}
			l2.isPath=true;
		}
	}
	
}
