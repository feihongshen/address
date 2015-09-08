package cn.explink.db.dialect;

import org.hibernate.dialect.MySQL5Dialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.StringType;

public class MySQLLocalDialect extends MySQL5Dialect {

	public MySQLLocalDialect() {
		super();
		this.registerFunction("convert_mine", new SQLFunctionTemplate(new StringType(), "convert(?1 using ?2)"));
	}
}