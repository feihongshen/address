package tmp;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import cn.explink.dao.AddressDao;
import cn.explink.domain.Address;
import cn.explink.domain.enums.AddressStatusEnum;
import cn.explink.test.support.BaseTestCase;

public class AddressReader extends BaseTestCase {

	private static final String LEVEL_1 = "    ";

	private static final String LEVEL_2 = "      ";

	private static final String LEVEL_3 = "        ";

	static String path = "address.txt";
	
	@Autowired
	private AddressDao addressDao;

	@Test
	public void testReadFile() throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
		String line = null;
		Address lastLevel1 = null;
		Address lastLevel2 = null;
		Set<Address> addressSet = new HashSet<Address>();
		Address root = new Address();
		root.setId(1L);
		root.setName("中国");
		root.setAddressLevel(0);
		root.setPath("");
		addressSet.add(root);
		long id = 2;
		while ((line = br.readLine()) != null) {
			System.out.println(line);
			String[] array = null;
			int level = 0;
			if (line.indexOf(LEVEL_3) > -1) {
				level = 3;
				array = line.split(LEVEL_3);
			} else if (line.indexOf(LEVEL_2) > -1) {
				level = 2;
				array = line.split(LEVEL_2);
			} else if (line.indexOf(LEVEL_1) > -1) {
				level = 1;
				array = line.split(LEVEL_1);
			} else {
				throw new RuntimeException("format error");
			}
			String addressName = filter(array[1]);
			if (addressName.trim().isEmpty()) {
				throw new RuntimeException("name error");
			}
			Address address = new Address();
			address.setId(id);
			address.setName(addressName);
			address.setAddressLevel(level);
			if (level == 1) {
				lastLevel1 = address;
				address.setParentId(root.getId());
				address.setPath(root.getId() + "");
			} else if (level == 2) {
//				if (address.getName().equals("市辖区") || address.getName().equals("县")) {
//					address = lastLevel1;
//					id--;
//				} else {
//					address.setParentId(lastLevel1.getId());
//					address.setPath(lastLevel1.getPath() + "-" + lastLevel1.getId());
//				}
				address.setParentId(lastLevel1.getId());
				address.setPath(lastLevel1.getPath() + "-" + lastLevel1.getId());
				lastLevel2 = address;
			} else if (level == 3) {
				address.setParentId(lastLevel2.getId());
				address.setPath(lastLevel2.getPath() + "-" + lastLevel2.getId());
			} else {
				throw new RuntimeException("level error");
			}
			id++;
			addressSet.add(address);
		}
		br.close();

		PrintWriter pw = new PrintWriter(new FileOutputStream("out.txt"));
		for (Address address : addressSet) {
			address.setStatus(AddressStatusEnum.valid.getValue());
			print(address, pw);
//			addressDao.create(address);
		}
		pw.close();
	}

	private void print(Address address, PrintWriter pw) {
		StringBuilder sql = new StringBuilder();
		sql.append("insert into ADDRESS (");
		sql.append("id, name, status, address_level, parent_id, path, creation_time");
		sql.append(")");
		sql.append(" values (");
		sql.append(address.getId()).append(", ");
		sql.append("'").append(address.getName()).append("', ");
		sql.append(address.getStatus()).append(", ");
		sql.append(address.getAddressLevel()).append(", ");
		sql.append(address.getParentId()).append(", ");
		sql.append("'").append(address.getPath()).append("', ");
		sql.append("now()");
		sql.append(");");
		System.out.println(sql.toString());
		pw.println(sql.toString());
	}

	public static String filter(String address) {
//		if (address.equals("市辖区") || address.equals("县")) {
//			return address;
//		}
//		if (address.endsWith("特别行政区")) {
//			address = address.substring(0, address.length() - 5);
//		} else if (address.endsWith("自治区") || address.endsWith("自治州") || address.endsWith("自治县")) {
//			address = address.substring(0, address.length() - 3);
//		} else if (address.endsWith("省") || address.endsWith("市") || address.endsWith("区") || address.endsWith("县")) {
//			address = address.substring(0, address.length() - 1);
//		}
		return address;
	}

//	public static class Address {
//
//		int id;
//
//		String name;
//
//		int level;
//
//		Integer parentId;
//
//		public Address(int id, String name, int level) {
//			super();
//			this.id = id;
//			this.name = name;
//			this.level = level;
//		}
//
//		public Address(int id, String name, int level, Integer parentId) {
//			super();
//			this.id = id;
//			this.name = name;
//			this.level = level;
//			this.parentId = parentId;
//		}
//
//		@Override
//		public String toString() {
//			StringBuilder builder = new StringBuilder();
//			builder.append("Address [id=").append(id).append(", ");
//			if (name != null)
//				builder.append("name=").append(name).append(", ");
//			builder.append("level=").append(level).append(", parentId=").append(parentId).append("]");
//			return builder.toString();
//		}
//
//	}
}
