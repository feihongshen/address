
package cn.explink.service;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.explink.dao.AddressDao;
import cn.explink.dao.CustomerDao;
import cn.explink.dao.DeliveryStationDao;
import cn.explink.db.merge.AdressDto;
import cn.explink.db.merge.DeliverersDto;
import cn.explink.db.merge.ValidateDto;
import cn.explink.domain.Address;
import cn.explink.domain.AddressPermission;
import cn.explink.domain.Alias;
import cn.explink.domain.ClientApplication;
import cn.explink.domain.Customer;
import cn.explink.domain.DelivererRule;
import cn.explink.domain.DeliveryStation;
import cn.explink.domain.KeywordSuffix;
import cn.explink.domain.OldId;
import cn.explink.domain.SystemConfig;
import cn.explink.domain.User;
import cn.explink.domain.Vendor;

@Service
public class MergeService {

    @Autowired
    CustomerDao customerDao;

    @Autowired
    DeliveryStationDao deliveryStationDao;

    @Autowired
    AddressDao addressDao;

    @Value("${jdbc.drivers}")
    private String drivers;

    @Value("${jdbc.url}")
    private String url;

    @Value("${jdbc.user}")
    private String user;

    @Value("${jdbc.password}")
    private String passwrod;

    @Value("${commit.size}")
    private int commit_size = 20000;

    @Value("${dbPath}")
    private String dbPath;

    private static Map<Long, Long> provinceMap = new HashMap<Long, Long>();

    private static Map<Long, String> urlMap = new HashMap<Long, String>();

    private static final String TABLE_DELIVERY_STATIONS = "delivery_stations";

    private static final String TABLE_ADDRESS = "address";

    private static final String TABLE_DELIVERY = "deliverers";

    private static final int OLD_TYPE_ID = 1;

    public static final SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final String id_split = "^^^^^^^^^";

    private static final Map<Long, Long> idMap = new HashMap<Long, Long>();

    private Connection localConn;

    private static final Logger LOGGER = LoggerFactory.getLogger(MergeService.class);

    public void doExc(Long customerId) throws SQLException {

        Customer customer = this.getCustomer(customerId);
        LOGGER.info("======start:" + customer.getName());
        // 1、清除customerId相关数据
        this.txNewdelData(customerId);
        // 2、根据customerId得到数据库连接符,省Id
        String url = this.getUrlMap().get(customerId);
        // 一个Connection代表一个数据库连接
        Connection conn = DriverManager.getConnection(url);
        // 3、合并customer相关数据
        this.doExcCustomer(conn, customer);
        // 4、合并delivery_stations相关数据
        this.txNewDeliveryStations(conn, customer);
        // 5、合并address相关数据
        this.doExcAddress(conn, customer);
        // 6、合并deliverers相关数据
        this.doExcDeliverers(conn, customer);
        // 7、合并完成校验数据
        // this.validate(conn, customer);
        conn.close();
        LOGGER.info("======end:" + customer.getName());
    }

    private String getConUrl() {
        return this.url + "&user=" + this.user + "&password=" + this.passwrod;

    }

    public Customer getCustomer(Long customerId) {
        Customer customer = this.customerDao.get(customerId);
        return customer;
    }

    private Long getOldCustomerId(Long customerId) {

        if (customerId == 8L) {
            return 6L;
        }
        return customerId;
    }

    public ValidateDto validate(Long customerId) throws SQLException {

        // 2、根据customerId得到数据库连接符,省Id
        Customer customer = this.customerDao.get(customerId);
        String url = this.getUrlMap().get(customerId);
        // 一个Connection代表一个数据库连接
        Connection conn = DriverManager.getConnection(url);
        return this.validate(conn, customer);
    }

    public Map<Long, String> getUrlMap() {
        if (MapUtils.isEmpty(urlMap) || MapUtils.isEmpty(provinceMap)) {
            // loadUrlMap(ValidateDto.class.getResource("db.txt").getPath());
            loadUrlMap(this.dbPath);
        }
        return urlMap;

    }

    private Map<Long, Long> getProvinceMap() {
        if (MapUtils.isEmpty(urlMap) || MapUtils.isEmpty(provinceMap)) {
            // loadUrlMap(ValidateDto.class.getResource("db.txt").getPath());
            loadUrlMap(this.dbPath);
        }
        return provinceMap;

    }

    public ValidateDto validate(Connection conn, Customer customer) throws SQLException {
        ValidateDto dto = new ValidateDto();
        Long provinceId = this.getProvinceMap().get(customer.getId());
        Statement stmt = conn.createStatement();
        ResultSet rs = null;
        String tmpSql = "select count(table.id) from table inner join address on table.address_id=address.id  where (address.parent_id="
                + provinceId + " or path like '%-" + provinceId + "-%') and customer_id=?";

        // 2、校验数量address_station_relation
        String sql = "select count(address_station_relation.id) address_station_relation FROM address_station_relation LEFT JOIN address  ON address_station_relation.address_id=address.id where address.parent_id="
                + provinceId + " or address.path like '%" + provinceId + "%'";
        Integer newTmp = Integer.parseInt(this.customerDao.getHSession().createSQLQuery(sql).uniqueResult() + "");
        dto.setCountNewAddressStationRelation(newTmp.intValue());

        rs = stmt.executeQuery(sql);
        while (rs.next()) {
            dto.setCountOldAddressStationRelation(rs.getInt(1));
        }
        // 1、校验address
        sql = "select count(*) from address where parent_id=" + provinceId + " or path like '%-" + provinceId + "-%'";
        newTmp = Integer.parseInt(this.customerDao.getHSession().createSQLQuery(sql).uniqueResult() + "");
        dto.setCountNewAddress(newTmp.intValue());
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
            dto.setCountOldAddress(rs.getInt(1));
        }
        // 3、校验address_permissions
        sql = StringUtils.replace(tmpSql, "table", "address_permissions");
        newTmp = Integer.parseInt(this.customerDao.getHSession()
                .createSQLQuery(StringUtils.replace(sql, "?", customer.getId() + "")).uniqueResult() + "");
        dto.setCountNewAddressPermissions(newTmp.intValue());
        rs = stmt.executeQuery(StringUtils.replace(sql, "?", this.getOldCustomerId(customer.getId()) + ""));
        while (rs.next()) {
            dto.setCountOldAddressPermissions(rs.getInt(1));
        }

        // 4、删除alias
        sql = StringUtils.replace(tmpSql, "table", "alias");
        newTmp = Integer.parseInt(this.customerDao.getHSession()
                .createSQLQuery(StringUtils.replace(sql, "?", customer.getId() + "")).uniqueResult() + "");
        dto.setCountNewAlias(newTmp.intValue());
        rs = stmt.executeQuery(StringUtils.replace(sql, "?", this.getOldCustomerId(customer.getId()) + ""));
        while (rs.next()) {
            dto.setCountOldAlias(rs.getInt(1));
        }
        // client_applications
        sql = "select count(*) from client_applications where customer_id=" + customer.getId();
        String oldSql = "select count(*) from client_applications where customer_id="
                + this.getOldCustomerId(customer.getId());
        newTmp = Integer.parseInt(this.customerDao.getHSession().createSQLQuery(sql).uniqueResult() + "");
        dto.setCountNewClientApplications(newTmp.intValue());
        rs = stmt.executeQuery(oldSql);
        while (rs.next()) {
            dto.setCountOldClientApplications(rs.getInt(1));
        }
        // deliverer_rules

        sql = "select count(deliverer_rules.id) from deliverer_rules inner join deliverers on deliverer_rules.DELIVERER_ID=deliverers.id inner join address on deliverer_rules.address_id=address.id  where (address.parent_id="
                + provinceId + " or address.path like '%-" + provinceId + "-%') and deliverers.CUSTOMER_ID="
                + customer.getId();
        oldSql = "select count(deliverer_rules.id) from deliverer_rules inner join deliverers on deliverer_rules.DELIVERER_ID=deliverers.id inner join address on deliverer_rules.address_id=address.id  where (address.parent_id="
                + provinceId + " or address.path like '%-" + provinceId + "-%') and deliverers.CUSTOMER_ID="
                + this.getOldCustomerId(customer.getId());
        newTmp = Integer.parseInt(this.customerDao.getHSession().createSQLQuery(sql).uniqueResult() + "");
        dto.setCountNewDelivererRules(newTmp.intValue());
        rs = stmt.executeQuery(oldSql);
        while (rs.next()) {
            dto.setCountOldDelivererRules(rs.getInt(1));
        }

        // deliverers
        sql = "select count(*) from deliverers where customer_id=" + customer.getId();
        oldSql = "select count(*) from deliverers where customer_id=" + this.getOldCustomerId(customer.getId());
        newTmp = Integer.parseInt(this.customerDao.getHSession().createSQLQuery(sql).uniqueResult() + "");
        dto.setCountNewDeliverer(newTmp.intValue());
        rs = stmt.executeQuery(oldSql);
        while (rs.next()) {
            dto.setCountOldDeliverer(rs.getInt(1));
        }
        // delivery_station_rules
        sql = "select count(delivery_station_rules.id) from delivery_station_rules inner join address on delivery_station_rules.address_id=address.id inner join delivery_stations    on delivery_station_rules.DELIVERY_STATION_ID=delivery_stations.id  where (address.parent_id="
                + provinceId + " or address.path like '%-" + provinceId + "-%') and delivery_stations.customer_id="
                + customer.getId();
        oldSql = "select count(delivery_station_rules.id) from delivery_station_rules inner join address on delivery_station_rules.address_id=address.id inner join delivery_stations    on delivery_station_rules.DELIVERY_STATION_ID=delivery_stations.id  where (address.parent_id="
                + provinceId + " or address.path like '%-" + provinceId + "-%') and delivery_stations.customer_id="
                + this.getOldCustomerId(customer.getId());
        newTmp = Integer.parseInt(this.customerDao.getHSession().createSQLQuery(sql).uniqueResult() + "");
        dto.setCountNewDeliveryStationRules(newTmp.intValue());
        rs = stmt.executeQuery(oldSql);
        while (rs.next()) {
            dto.setCountOldDeliveryStationRules(rs.getInt(1));
        }
        // delivery_stations
        sql = "select count(*) from delivery_stations where customer_id=" + customer.getId();
        oldSql = "select count(*) from delivery_stations where customer_id=" + this.getOldCustomerId(customer.getId());
        newTmp = Integer.parseInt(this.customerDao.getHSession().createSQLQuery(sql).uniqueResult() + "");
        dto.setCountNewDeliveryStations(newTmp.intValue());
        rs = stmt.executeQuery(oldSql);
        while (rs.next()) {
            dto.setCountOldDeliveryStations(rs.getInt(1));
        }
        // keyword_suffix
        sql = "select count(*) from keyword_suffix where customer_id=" + customer.getId();
        oldSql = "select count(*) from keyword_suffix where customer_id=" + this.getOldCustomerId(customer.getId());
        newTmp = Integer.parseInt(this.customerDao.getHSession().createSQLQuery(sql).uniqueResult() + "");
        dto.setCountNewKeywordSuffix(newTmp.intValue());
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
            dto.setCountOldKeywordSuffix(rs.getInt(1));
        }

        // system_config
        sql = "select count(*) from system_config where customer_id=" + customer.getId();
        oldSql = "select count(*) from system_config where customer_id=" + this.getOldCustomerId(customer.getId());
        newTmp = Integer.parseInt(this.customerDao.getHSession().createSQLQuery(sql).uniqueResult() + "");
        dto.setCountNewSystemConfig(newTmp.intValue());
        rs = stmt.executeQuery(oldSql);
        while (rs.next()) {
            dto.setCountOldSystemConfig(rs.getInt(1));
        }
        // users
        sql = "select count(*) from users where customer_id=" + customer.getId();
        oldSql = "select count(*) from users where customer_id=" + this.getOldCustomerId(customer.getId());
        newTmp = Integer.parseInt(this.customerDao.getHSession().createSQLQuery(sql).uniqueResult() + "");
        dto.setCountNewUsers(newTmp.intValue());
        rs = stmt.executeQuery(oldSql);
        while (rs.next()) {
            dto.setCountOldUsers(rs.getInt(1));
        }

        // vendors
        sql = "select count(*) from vendors where customer_id=" + customer.getId();
        oldSql = "select count(*) from vendors where customer_id=" + this.getOldCustomerId(customer.getId());
        newTmp = Integer.parseInt(this.customerDao.getHSession().createSQLQuery(sql).uniqueResult() + "");
        dto.setCountNewVendors(newTmp.intValue());
        rs = stmt.executeQuery(oldSql);
        while (rs.next()) {
            dto.setCountOldVendors(rs.getInt(1));
        }
        return dto;
    }

    public void doExcDeliverers(Connection conn, Customer customer) throws SQLException {
        Statement stmt = conn.createStatement();
        String sql = "select * from deliverers where CUSTOMER_ID=" + this.getOldCustomerId(customer.getId());
        ResultSet rs = stmt.executeQuery(sql);
        List<String> sqlLst = new ArrayList<String>();
        List<DeliverersDto> dtoLst = new ArrayList<DeliverersDto>();
        while (rs.next()) {
            DeliverersDto entry = new DeliverersDto();
            // ID int
            // NAME varchar
            // entry.setName(this.removeIllegalChar(rs.getString(2)));
            entry.setName(rs.getString(2));
            // EXTERNAL_ID int
            entry.setExternalId(rs.getLong(3));
            // CUSTOMER_ID int
            entry.setCustomer(customer);
            // STATUS int
            entry.setStatus(rs.getInt(5));
            // CREATION_TIME datetime
            entry.setCreationTime(rs.getTimestamp(6));
            entry.setCreatTime(rs.getTimestamp(6));
            entry.setDeliveryStationId(0L);
            // entry.setUserCode("''");
            // 武汉2个字段
            // DELIVERY_STATION_ID int
            if (customer.getId().intValue() == 39) {
                Long newDeliveryStationId = this.getNewId(rs.getLong(7), TABLE_DELIVERY_STATIONS, OLD_TYPE_ID,
                        customer.getId());
                entry.setDeliveryStationId(newDeliveryStationId);
                // user_code varchar
                entry.setUserCode(rs.getString(8));
            }
            String tmpDate = null;
            if (entry.getCreationTime() != null) {
                tmpDate = "'" + sf.format(entry.getCreationTime()) + "'";
            }
            // this.saveOrUpdate(entry);
            // this.saveOldId(entry.getId(), rs.getLong(1), TABLE_DELIVERY, OLD_TYPE_ID, customer.getId());

            sqlLst.add(rs.getLong(1) + id_split
                    + "INSERT INTO `deliverers` (`NAME`, `EXTERNAL_ID`, `CUSTOMER_ID`, `STATUS`, `CREATION_TIME`, `DELIVERY_STATION_ID`, `user_code`) VALUES ('"
                    + entry.getName() + "'," + entry.getExternalId() + ", " + customer.getId() + ", "
                    + customer.getStatus() + ", " + tmpDate + ", " + entry.getDeliveryStationId() + ", "
                    + entry.getUserCode() + ")");
            dtoLst.add(entry);

        }
        // List<Long> newIdLst = this.batchInsertSql(this.getConUrl(), sqlLst, true);
        List<Long> newIdLst = this.batchInsertDeliverers(this.getConUrl(), dtoLst);
        this.batchInsertOldId(newIdLst, sqlLst, TABLE_DELIVERY, OLD_TYPE_ID, customer.getId());
        this.txNewDelivererRules(conn, customer.getId());
        LOGGER.info("====doExcDeliverers:end");
        System.out.println("====doExcDeliverers:end");
    }

    private String removeIllegalChar(String string) {
        // TODO Auto-generated method stub
        return StringUtils.replace(string, "'", "");
    }

    private void txNewDelivererRules(Connection conn, Long customerId) throws SQLException {
        Long provinceId = this.getProvinceMap().get(customerId);
        Statement stmt = conn.createStatement();
        String sql = "select deliverer_rules.* from deliverer_rules inner join deliverers on deliverer_rules.DELIVERER_ID=deliverers.id inner join address on deliverer_rules.address_id=address.id  where (address.parent_id=? or address.path like'%-?-%') and deliverers.CUSTOMER_ID="
                + this.getOldCustomerId(customerId);
        sql = StringUtils.replace(sql, "?", provinceId + "");
        ResultSet rs = stmt.executeQuery(sql);
        List<String> sqlLst = new ArrayList<String>();
        while (rs.next()) {
            DelivererRule entry = new DelivererRule();
            // ID int
            // RULE varchar
            entry.setRule(StringUtils.isEmpty(rs.getString(2)) ? null : "'" + rs.getString(2) + "'");
            // RULE_EXPRESSION varchar
            entry.setRuleExpression(StringUtils.isEmpty(rs.getString(3)) ? null : "'" + rs.getString(3) + "'");
            // RULE_TYPE int
            entry.setRuleType(rs.getInt(4));
            // ADDRESS_ID int
            // Long newAddressId = this.getNewId(rs.getLong(5), TABLE_ADDRESS, OLD_TYPE_ID,
            // deliverer.getCustomer().getId());
            // entry.setAddress(this.addressDao.get(newAddressId));
            // DELIVERER_ID int
            // entry.setDeliverer(deliverer);
            // CREATION_TIME datetime
            entry.setCreationTime(rs.getTimestamp(7));
            // DELIVERY_STATION_ID int
            Long deliveryStationId = 0L;
            if (customerId == 39L) {
                deliveryStationId = rs.getLong(8);
            }

            // this.saveOrUpdate(entry);
            sqlLst.add(
                    "INSERT INTO `deliverer_rules` (`RULE`, `RULE_EXPRESSION`, `RULE_TYPE`, `ADDRESS_ID`, `DELIVERER_ID`, `CREATION_TIME`, `DELIVERY_STATION_ID`) VALUES ("
                            + entry.getRule() + ", " + entry.getRuleExpression() + ", " + entry.getRuleType() + ", '"
                            + rs.getLong(5) + "', " + rs.getLong(6) + ", '" + sf.format(entry.getCreationTime()) + "', "
                            + deliveryStationId + ")");

        }
        this.batchInsertUpdateSql(this.getConUrl(), sqlLst);
        // 更新addressId
        this.updateSql(this.getConUrl(),
                "update deliverer_rules p,old_id o set p.address_id=o.new_id where p.address_id=o.old_id and tab='"
                        + TABLE_ADDRESS + "' and type=1 and o.customer_id=" + customerId);
        // 更新deliveryId
        this.updateSql(this.getConUrl(),
                "update deliverer_rules p,old_id o set p.DELIVERER_ID=o.new_id where p.DELIVERER_ID=o.old_id and tab='"
                        + TABLE_DELIVERY + "' and type=1 and o.customer_id=" + customerId);

        if (customerId == 39L) {
            // 更新deliveryStationId
            this.updateSql(this.getConUrl(),
                    "update deliverer_rules p,old_id o set p.DELIVERY_STATION_ID=o.new_id where p.DELIVERY_STATION_ID=o.old_id and tab='"
                            + TABLE_DELIVERY_STATIONS + "' and type=1 and o.customer_id=" + customerId);

        }
        LOGGER.info("====txNewDelivererRules:end");
        System.out.println("====txNewDelivererRules:end");
    }

    public void txNewDeliveryStations(Connection conn, Customer customer) throws SQLException {
        Statement stmt = conn.createStatement();
        String sql = "select * from delivery_stations where CUSTOMER_ID=" + this.getOldCustomerId(customer.getId());
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            DeliveryStation entry = new DeliveryStation();
            // ID int
            // NAME varchar
            entry.setName(rs.getString(2));
            // COORDINATE varchar
            entry.setCoordinate(rs.getString(3));
            // MAPCENTER_LNG decimal
            entry.setMapcenterLng(rs.getBigDecimal(4));
            // MAPCENTER_LAT decimal
            entry.setMapcenterLat(rs.getBigDecimal(5));
            // UID varchar
            entry.setUid(rs.getString(6));
            // EXTERNAL_ID int
            entry.setExternalId(rs.getLong(7));
            // CUSTOMER_ID int
            entry.setCustomer(customer);
            // STATUS int
            entry.setStatus(rs.getInt(9));
            // CREATION_TIME datetime
            entry.setCreationTime(rs.getTimestamp(10));
            // STATION_CODE varchar
            entry.setStationCode(rs.getString(11));
            // this.customerDao.getHSession().saveOrUpdate(entry);
            this.saveOrUpdate(entry);
            this.saveOldId(entry.getId(), rs.getLong(1), TABLE_DELIVERY_STATIONS, OLD_TYPE_ID, customer.getId());
        }
        LOGGER.info("====txNewDeliveryStations:end");
        System.out.println("====txNewDeliveryStations:end");
    }

    public void doExcAddress(Connection conn, Customer customer) throws SQLException {

        // 新增地址
        this.txNewInsertAddress(conn, customer);
        // 更新path
        this.txNewUpdatePathAddress(conn, customer);
        // 合并权限
        this.mergeAddressPermissions(customer.getId(), conn);
        // 合并别名
        this.mergeAlias(conn, customer);
        // 合并站点规则
        this.mergeDeliveryStationRule(customer.getId(), conn);

    }

    private void mergeAlias(Connection conn, Customer customer) throws SQLException {
        Statement stmt = conn.createStatement();
        Long provinceId = this.getProvinceMap().get(customer.getId());
        String sql = "select alias.* from alias inner join address on alias.address_id= address.id where (address.parent_id="
                + provinceId + " or address.path like '%-" + provinceId + "-%') and CUSTOMER_ID="
                + this.getOldCustomerId(customer.getId());
        ResultSet rs = stmt.executeQuery(sql);
        // List<String> sqlLst = new ArrayList<String>();
        List<Alias> sqlLst = new ArrayList<Alias>();
        while (rs.next()) {
            Alias entry = new Alias();
            // ID int
            // ADDRESS_ID int
            entry.setAddressId(rs.getLong(2));
            // CUSTOMER_ID int
            entry.setCustomerId(customer.getId());
            // NAME varchar
            entry.setName(rs.getString(4));
            // OLD_NAME varchar
            entry.setOldName(rs.getString(5));

            // sqlLst.add(entry.getAddressId() + id_split
            // + "INSERT INTO `alias` (`ADDRESS_ID`, `CUSTOMER_ID`, `NAME`, `OLD_NAME`) VALUES ("
            // + entry.getAddressId() + ", '" + customer.getId() + "', '" + entry.getName() + "', '"
            // + entry.getOldName() + "')");
            sqlLst.add(entry);
        }

        // 新增
        // this.batchInsertSql(this.getConUrl(), sqlLst, false);
        this.batchInsertAlias(this.getConUrl(), sqlLst);
        // 更新id
        this.updateSql(this.getConUrl(), "update alias p,old_id o set p.address_id=o.new_id where o.customer_id="
                + customer.getId() + " and tab='address' and p.address_id=o.old_id  and type=1");
        LOGGER.info("====mergeAlias:end");
        System.out.println("====mergeAlias:end");
    }

    private void txNewUpdatePathAddress(Connection conn, Customer customer) throws SQLException {

        Long provinceId = this.getProvinceMap().get(customer.getId());
        // 查询该省下面的所有数据
        String sql = " from Address where parentId=" + provinceId + " or path like '%-" + provinceId
                + "-%' order by path asc";
        Query query = this.customerDao.getHSession().createQuery(sql);
        List<Address> list = query.list();
        int i = 0;
        List<String> sqlLst = new ArrayList<String>();
        for (Address address : list) {

            String[] idStr = StringUtils.split(address.getPath(), "\\-");
            if (ArrayUtils.isEmpty(idStr) || (idStr.length < 3)) {
                continue;
            }

            Long newParentId = idMap.get(address.getParentId());

            // 更新parentId
            // 更新path

            StringBuffer tmpPath = new StringBuffer(idStr[0] + "-" + idStr[1]);
            for (int indnex = 2; indnex < idStr.length; indnex++) {
                Long tmpId = idMap.get(Long.parseLong(idStr[indnex]));
                tmpPath.append("-" + String.valueOf(tmpId));
            }
            sqlLst.add("update address set parent_id=" + newParentId + ",path='" + tmpPath.toString() + "' where id="
                    + address.getId());

        }

        this.batchInsertUpdateSql(this.getConUrl(), sqlLst);
        LOGGER.info("===txNewUpdatePathAddress:end");
        System.out.println("===txNewUpdatePathAddress:end");

    }

    public void mergeDeliveryStationRule(Long customerId, Connection conn) throws SQLException {
        Long provinceId = this.getProvinceMap().get(customerId);
        Statement stmt = conn.createStatement();
        String sql = "select delivery_station_rules.* from delivery_station_rules inner join address on delivery_station_rules.address_id=address.id inner join DELIVERY_STATIONS d on delivery_station_rules.DELIVERY_STATION_ID=d.id  where (address.parent_id=? or path like '%-?-%') and d.customer_id="
                + this.getOldCustomerId(customerId);
        ResultSet rs = stmt.executeQuery(StringUtils.replace(sql, "?", provinceId + ""));

        List<String> sqlLst = new ArrayList<String>();
        while (rs.next()) {
            // ID int
            // RULE varchar
            // entry.setRule(rs.getString(2));
            // RULE_EXPRESSION varchar
            // entry.setRuleExpression(rs.getString(3));
            // RULE_TYPE int
            // entry.setRuleType(rs.getInt(4));
            // ADDRESS_ID int
            // entry.setAddress(this.addressDao.get(newId));
            // DELIVERY_STATION_ID int
            // Long newDeliveryStationId = this.getNewId(rs.getLong(6), TABLE_DELIVERY_STATIONS, OLD_TYPE_ID,
            // customerId);
            // entry.setDeliveryStation(this.deliveryStationDao.get(newDeliveryStationId));
            // CREATION_TIME datetime
            // entry.setCreationTime(rs.getDate(7));
            // this.customerDao.getHSession().saveOrUpdate(entry);
            // this.saveOrUpdate(entry);
            String ruleExpression = StringUtils.isEmpty(rs.getString(3)) ? null : "'" + rs.getString(3) + "'";
            String rule = StringUtils.isEmpty(rs.getString(2)) ? null : "'" + rs.getString(2) + "'";
            sqlLst.add(
                    "INSERT INTO `delivery_station_rules` (`RULE`, `RULE_EXPRESSION`, `RULE_TYPE`, `ADDRESS_ID`, `DELIVERY_STATION_ID`, `CREATION_TIME`) VALUES ("
                            + rule + ", " + ruleExpression + ", " + rs.getInt(4) + ", " + rs.getLong(5) + ", "
                            + rs.getLong(6) + ", '" + sf.format(rs.getTimestamp(7)) + "')");

        }
        this.batchInsertUpdateSql(this.getConUrl(), sqlLst);
        // 更新addresId
        this.updateSql(this.getConUrl(),
                "update delivery_station_rules p,old_id o set p.address_id=o.new_id where p.address_id=o.old_id and tab='address' and type=1 and o.customer_id="
                        + customerId);
        // 更新deliveryStationId
        this.updateSql(this.getConUrl(),
                "update delivery_station_rules p,old_id o set p.DELIVERY_STATION_ID=o.new_id where p.DELIVERY_STATION_ID=o.old_id and tab='"
                        + TABLE_DELIVERY_STATIONS + "' and type=1 and o.customer_id=" + customerId);

        LOGGER.info("====mergeDeliveryStationRules:end");
        System.out.println("====mergeDeliveryStationRules:end");
    }

    private void mergeAddressPermissions(Long customerId, Connection conn) throws SQLException {
        Long provinceId = this.getProvinceMap().get(customerId);

        Statement stmt = conn.createStatement();
        String sql = "select address_permissions.* from address_permissions inner join address on address_permissions.address_id=address.id  where CUSTOMER_ID="
                + this.getOldCustomerId(customerId) + " and (address.parent_id=" + provinceId + " or path like '%-"
                + provinceId + "-%')";
        ResultSet rs = stmt.executeQuery(sql);
        List<String> sqlLst = new ArrayList<String>();
        while (rs.next()) {
            AddressPermission entry = new AddressPermission();
            // ID int
            // ADDRESS_ID int
            entry.setAddressId(rs.getLong(2));
            // CUSTOMER_ID int
            entry.setCustomerId(customerId);
            // this.saveOrUpdate(entry);
            sqlLst.add(entry.getAddressId() + id_split
                    + "INSERT INTO `address_permissions` (`ADDRESS_ID`, `CUSTOMER_ID`) VALUES (" + entry.getAddressId()
                    + ", " + entry.getCustomerId() + ")");
        }
        // 新增省权限
        this.updateSql(this.getConUrl(), "INSERT INTO `address_permissions` (`ADDRESS_ID`, `CUSTOMER_ID`) VALUES ("
                + provinceId + ", " + customerId + ")");
        this.batchInsertSql(this.getConUrl(), sqlLst, false);
        // 更新id
        this.updateSql(this.getConUrl(),
                "update address_permissions p,old_id o set p.address_id=o.new_id where p.address_id=o.old_id and tab='address' and type=1 and o.customer_id="
                        + customerId);
        LOGGER.info("====txNewMergeAddressPermissions:end");
        System.out.println("====txNewMergeAddressPermissions:end");
    }

    private Long getOldId(Long newId, String table, int type, Long customerId) {
        Query query = this.customerDao.getHSession().createQuery(
                "from OldId where newId=:newId and table=:table and type=:type and customerId=:customerId");
        query.setParameter("newId", newId);
        query.setParameter("table", table);
        query.setParameter("type", type);
        query.setParameter("customerId", customerId);
        OldId oldId = (OldId) query.uniqueResult();
        return oldId == null ? null : oldId.getOldId();
    }

    private Long getNewId(Long oldId, String table, int type, Long customerId) {
        Query query = this.customerDao.getHSession().createQuery(
                "from OldId where oldId=:oldId and table=:table and type=:type and customerId=:customerId");
        query.setParameter("oldId", oldId);
        query.setParameter("table", table);
        query.setParameter("type", type);
        query.setParameter("customerId", customerId);
        OldId old = (OldId) query.uniqueResult();
        return old == null ? null : old.getNewId();
    }

    private void txNewInsertAddress(Connection conn, Customer customer) throws SQLException {

        // address
        Long provinceId = this.getProvinceMap().get(customer.getId());
        Statement stmt = conn.createStatement();
        String sql = "select * from address where parent_id=" + provinceId + " or path like '%-" + provinceId
                + "-%' order by path asc";
        ResultSet rs = stmt.executeQuery(sql);
        List<String> sqlLst = new ArrayList<String>();
        List<AdressDto> addressDtoLst = new ArrayList<AdressDto>();
        while (rs.next()) {

            AdressDto entry = new AdressDto();
            // ID int 12
            Long oid = rs.getLong(1);
            entry.setOldAddressId(oid);
            // NAME varchar 50
            entry.setName(rs.getString(2));
            // OLD_NAME varchar 50
            entry.setOldName(rs.getString(3));
            // STATUS int 2
            entry.setStatus(rs.getInt(4));
            // ADDRESS_TYPE_ID int 2
            entry.setAddressTypeId(rs.getInt(5));
            // ADDRESS_LEVEL int 2
            entry.setAddressLevel(rs.getInt(6));
            // PARENT_ID int 12

            entry.setParentId(rs.getLong(7));
            // PATH varchar 100

            entry.setPath(rs.getString(8));
            // INDEXED int 1
            entry.setIndexed(false);
            // CREATION_TIME datetime 0
            entry.setCreateTime(rs.getTimestamp(10));

            // entry.setCreationTime(.getDate(10));
            String tmpSql = rs.getLong(1) + id_split
                    + "INSERT INTO address(`NAME`, `OLD_NAME`, `STATUS`, `ADDRESS_TYPE_ID`, `ADDRESS_LEVEL`,"
                    + " `PARENT_ID`, `PATH`, `INDEXED`, `CREATION_TIME`) VALUES ('" + entry.getName() + "', "
                    + entry.getOldName() + ", " + entry.getStatus() + ", " + entry.getAddressTypeId() + ", "
                    + entry.getAddressLevel() + ", " + entry.getParentId() + ", '" + entry.getPath() + "', 0,'"
                    + sf.format(rs.getTimestamp(10)) + "')";

            sqlLst.add(StringUtils.replace(tmpSql, "\\'", "'"));
            addressDtoLst.add(entry);

            // this.saveOrUpdate(entry);
            // 保存旧id
            // this.saveOldId(entry.getId(), oid, TABLE_ADDRESS, OLD_TYPE_ID, customer.getId());
        }

        // List<Long> newLst = this.batchInsertSql(this.getConUrl(), sqlLst, true);
        List<Long> newLst = this.batchInsertAddress(this.getConUrl(), addressDtoLst);
        this.batchInsertOldId(newLst, sqlLst, TABLE_ADDRESS, OLD_TYPE_ID, customer.getId());
        // this.batchInsertOldId(newLst, sqlLst);
        LOGGER.info("===txNewInsertAddress:end");
        System.out.println("===txNewInsertAddress:end");
    }

    private void batchInsertOldId(List<Long> newLst, List<String> sqlLst, String table, int oldTypeId, Long customerId)
            throws SQLException {
        if (CollectionUtils.isEmpty(sqlLst)) {
            return;
        }
        List<String> tmpLst = new ArrayList<String>();
        idMap.clear();
        for (int i = 0; i < newLst.size(); i++) {
            String[] tmp = StringUtils.split(sqlLst.get(i), id_split);
            tmpLst.add(tmp[0] + id_split
                    + "INSERT INTO `old_id` (`type`, `tab`, `old_id`, `customer_id`, `new_id`) VALUES ('" + oldTypeId
                    + "', '" + table + "', '" + tmp[0] + "', '" + customerId + "', '" + newLst.get(i) + "')");
            idMap.put(Long.parseLong(tmp[0]), newLst.get(i));
        }
        this.batchInsertSql(this.getConUrl(), tmpLst, false);

    }

    private void batchInsertOldId(List<Long> newLst, List<OldId> sqlLst) throws SQLException {
        if (CollectionUtils.isEmpty(sqlLst)) {
            return;
        }

        Connection conn = this.getLocalConn();
        PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO `old_id` (`type`, `tab`, `old_id`, `customer_id`, `new_id`) VALUES (?,?,?,?,?)");
        conn.setAutoCommit(false);
        idMap.clear();
        for (int i = 0; i < newLst.size(); i++) {

            // ID
            // type
            stmt.setInt(1, sqlLst.get(i).getType());
            // tab
            stmt.setString(2, sqlLst.get(i).getTable());
            // old_id
            stmt.setLong(3, sqlLst.get(i).getOldId());
            // customer_id
            stmt.setLong(4, sqlLst.get(i).getCustomerId());
            // new_id
            stmt.setLong(5, newLst.get(i));
            stmt.addBatch();
            if ((i % this.commit_size) == 0) {
                stmt.executeBatch();
                conn.commit();
            }
            idMap.put(sqlLst.get(i).getOldId(), newLst.get(i));
        }
        // 剩余未提交再做一次提交
        stmt.executeBatch();
        conn.commit();

    }

    private void saveOldId(Long newid, Long oid, String table, int type, Long customerId) {
        OldId entry = new OldId();
        entry.setCustomerId(customerId);
        entry.setNewId(newid);
        entry.setOldId(oid);
        entry.setType(type);
        entry.setTable(table);
        // this.customerDao.getHSession().saveOrUpdate(entry);
        this.saveOrUpdate(entry);
    }

    public void doExcCustomer(Connection conn, Customer customer) throws SQLException {

        // 合并system_config
        this.txNewMergeSystemConfig(conn, customer);
        // 合并users
        this.txNewMergeUsers(conn, customer);
        // 合并vendors
        this.txNewMergeVendors(conn, customer);
        // 合并vendors_aging
        this.txNewMergeVendorsAging(conn, customer);
        // 合并keyword_suffix
        this.txNewMergeKeywordSuffix(conn, customer);
        // 合并client_applications
        this.txNewMergeClientApplications(conn, customer);

    }

    private void txNewMergeClientApplications(Connection conn, Customer customer) throws SQLException {
        Statement stmt = conn.createStatement();
        String sql = "select * from client_applications where CUSTOMER_ID=" + this.getOldCustomerId(customer.getId());
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            ClientApplication entry = new ClientApplication();
            // ID int
            // NAME varchar
            entry.setName(rs.getString(2));
            // PASSWORD varchar
            entry.setPassword(rs.getString(3));
            // CUSTOMER_ID int
            entry.setCustomerId(customer.getId());
            // STATUS int
            entry.setStatus(rs.getInt(5));

            // this.customerDao.getHSession().saveOrUpdate(entry);
            this.saveOrUpdate(entry);
            this.customerDao.getHSession()
                    .createSQLQuery(
                            "UPDATE client_applications set id=CUSTOMER_ID where CUSTOMER_ID=" + customer.getId())
                    .executeUpdate();
        }
    }

    private void txNewMergeKeywordSuffix(Connection conn, Customer customer) throws SQLException {
        Statement stmt = conn.createStatement();
        String sql = "select * from keyword_suffix where CUSTOMER_ID=" + this.getOldCustomerId(customer.getId());
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            KeywordSuffix entry = new KeywordSuffix();
            // ID int 12
            // CUSTOMER_ID int 12
            entry.setCustomerId(customer.getId());
            // NAME varchar 50
            entry.setName(rs.getString(3));
            // this.customerDao.getHSession().saveOrUpdate(entry);
            this.saveOrUpdate(entry);
        }
    }

    private void txNewMergeVendorsAging(Connection conn, Customer customer) {
        // TODO Auto-generated method stub

    }

    private void txNewMergeVendors(Connection conn, Customer customer) throws SQLException {
        Statement stmt = conn.createStatement();
        String sql = "select * from vendors where CUSTOMER_ID=" + this.getOldCustomerId(customer.getId());
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            Vendor entry = new Vendor();
            // ID int
            // NAME varchar
            entry.setName(rs.getString(2));
            // EXTERNAL_ID int
            entry.setExternalId(rs.getLong(3));
            // CUSTOMER_ID int
            entry.setCustomer(customer);
            // STATUS int
            entry.setStatus(rs.getInt(5));
            // CREATION_TIME datetime
            entry.setCreationTime(rs.getTimestamp(6));
            // this.customerDao.getHSession().saveOrUpdate(entry);
            this.saveOrUpdate(entry);
        }
    }

    private void txNewMergeUsers(Connection conn, Customer customer) throws SQLException {
        Statement stmt = conn.createStatement();
        String sql = "select * from users where CUSTOMER_ID=" + this.getOldCustomerId(customer.getId());
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            User entry = new User();
            // ID int
            // entry.setId(rs.getLong(1) == 6 ? 8 : rs.getLong(1));
            // NAME varchar
            entry.setName(rs.getString(2));
            // PASSWORD varchar
            entry.setPassword(rs.getString(3));
            // STATUS int
            entry.setStatus(rs.getInt(4));
            // CUSTOMER_ID int
            entry.setCustomer(customer);
            // this.customerDao.getHSession().saveOrUpdate(entry);
            this.saveOrUpdate(entry);
            this.customerDao.getHSession()
                    .createSQLQuery("update users set id=customer_id where customer_id=" + customer.getId())
                    .executeUpdate();
        }
    }

    private void txNewMergeSystemConfig(Connection conn, Customer customer) throws SQLException {
        Statement stmt = conn.createStatement();
        String sql = "select * from system_config where CUSTOMER_ID=" + this.getOldCustomerId(customer.getId());
        ResultSet rs = stmt.executeQuery(sql);

        while (rs.next()) {
            SystemConfig config = new SystemConfig();
            // ID int 12 0 0 -1 0 0 0 0 主键id -1 0
            // NAME varchar 60 0 0 0 0 0 0 0 参数名称 utf8 utf8_general_ci 0 0
            config.setName(rs.getString(2));
            // VALUE varchar 32 0 -1 0 0 0 0 0 参数值 utf8 utf8_general_ci 0 0
            config.setValue(rs.getString(3));
            // DESCRIPTION varchar 100 0 -1 0 0 0 0 0 参数的描述 utf8 utf8_general_ci 0 0
            config.setDescription(rs.getString(4));
            // EDITABLE int 1 0 -1 0 0 0 0 0 是否可以编辑 0 0
            config.setEditable(rs.getInt(5) == 1 ? true : false);
            // CUSTOMER_ID int 11 0 -1 0 0 0 0 0 0 0
            config.setCustomerId(customer.getId());
            // System.out.println(rs.getString(1) + "\t" + rs.getString(2));// 入如果返回的是int类型可以用getInt()
            this.saveOrUpdate(config);

        }
    }

    private void saveOrUpdate(Object object) {
        this.customerDao.getHSession().saveOrUpdate(object);
    }

    public void txNewdelData(Long customerId) {
        Long provinceId = this.getProvinceMap().get(customerId);

        idMap.clear();

        // 删除address_station_relation
        this.customerDao.getHSession()
                .createSQLQuery(
                        "DELETE address_station_relation FROM address_station_relation LEFT JOIN address  ON address_station_relation.address_id=address.id where address.parent_id="
                                + provinceId + " or address.path like '%-" + provinceId + "-%'")
                .executeUpdate();

        // 删除address_permissions
        this.customerDao.getHSession()
                .createSQLQuery("delete from address_permissions where address_id<>1 and customer_id=" + customerId)
                .executeUpdate();
        // 删除alias
        this.customerDao.getHSession().createSQLQuery("delete from alias where customer_id=" + customerId)
                .executeUpdate();
        // 删除client_applications
        this.customerDao.getHSession().createSQLQuery("delete from client_applications where customer_id=" + customerId)
                .executeUpdate();
        // 删除deliverer_rules
        this.customerDao.getHSession()
                .createSQLQuery(
                        "delete deliverer_rules from deliverer_rules inner join deliverers on deliverer_rules.DELIVERER_ID=deliverers.id  where deliverers.customer_id="
                                + customerId)
                .executeUpdate();
        // 删除deliverers
        this.customerDao.getHSession().createSQLQuery("delete from deliverers where customer_id=" + customerId)
                .executeUpdate();
        // 删除delivery_station_rules
        this.customerDao.getHSession()
                .createSQLQuery(
                        "delete delivery_station_rules from delivery_station_rules inner join delivery_stations on delivery_station_rules.DELIVERY_STATION_ID=delivery_stations.id  where delivery_stations.customer_id="
                                + customerId)
                .executeUpdate();
        // 删除delivery_stations
        this.customerDao.getHSession().createSQLQuery("delete from delivery_stations where customer_id=" + customerId)
                .executeUpdate();
        // 删除keyword_suffix
        this.customerDao.getHSession().createSQLQuery("delete from keyword_suffix where customer_id=" + customerId)
                .executeUpdate();
        // 删除old_id
        this.customerDao.getHSession().createSQLQuery("delete from old_id where customer_id=" + customerId)
                .executeUpdate();
        // 删除system_config
        this.customerDao.getHSession().createSQLQuery("delete from system_config where customer_id=" + customerId)
                .executeUpdate();
        // 删除users
        this.customerDao.getHSession().createSQLQuery("delete from users where customer_id=" + customerId)
                .executeUpdate();
        // 删除vendors
        this.customerDao.getHSession().createSQLQuery("delete from vendors where customer_id=" + customerId)
                .executeUpdate();
        // 删除address
        this.customerDao.getHSession()
                .createSQLQuery(
                        "delete from address where parent_id=" + provinceId + " or path like '%-" + provinceId + "-%'")
                .executeUpdate();

    }

    public void resetUrlMap() {
        urlMap.clear();
    }

    /**
     * 以行为单位读取文件，常用于读面向行的格式化文件
     */
    public static void loadUrlMap(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        provinceMap.clear();
        urlMap.clear();
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
                if (StringUtils.indexOf(tempString, "#") >= 0) {
                    continue;
                }
                String[] val = StringUtils.split(tempString, "\\|");
                provinceMap.put(Long.parseLong(val[0]), Long.parseLong(val[1]));
                urlMap.put(Long.parseLong(val[0]), val[2]);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
    }

    private static void loadJdbc() {

        Properties props = new Properties();
        String path = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator
                + "resources" + File.separator + "address.properties";
        try {
            InputStream in = new BufferedInputStream(new FileInputStream(path));
            props.load(in);
            String drivers = props.getProperty("jdbc.drivers");
            String url = props.getProperty("jdbc.url");
            String user = props.getProperty("jdbc.user");
            String password = props.getProperty("jdbc.password");
            System.out.println("键的值是：" + drivers);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void batchInsertUpdateSql(String url, List<String> sqlLst) throws SQLException {

        if (CollectionUtils.isEmpty(sqlLst)) {
            return;
        }
        Connection conn = this.getLocalConn();

        Statement stmt = conn.createStatement();
        conn.setAutoCommit(false);

        for (int i = 0; i < sqlLst.size(); i++) {
            stmt.addBatch(sqlLst.get(i));
            if ((i % this.commit_size) == 0) {
                stmt.executeBatch();
                conn.commit();

            }
        }
        // 剩余未提交再做一次提交
        stmt.executeBatch();
        conn.commit();

        // conn.close();

    }

    private List<Long> batchInsertDeliverers(String url, List<DeliverersDto> sqlLst) throws SQLException {

        if (CollectionUtils.isEmpty(sqlLst)) {
            return null;
        }
        Connection conn = this.getLocalConn();
        PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO `deliverers` (`NAME`, `EXTERNAL_ID`, `CUSTOMER_ID`, `STATUS`, `CREATION_TIME`, `DELIVERY_STATION_ID`, `user_code`) VALUES (?,?,?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
        conn.setAutoCommit(false);
        List<Long> ids = new ArrayList<Long>();

        for (int i = 0; i < sqlLst.size(); i++) {
            // ID int
            // NAME varchar
            // entry.setName(this.removeIllegalChar(rs.getString(2)));
            stmt.setString(1, sqlLst.get(i).getName());
            // EXTERNAL_ID int
            // entry.setExternalId(rs.getLong(3));
            stmt.setLong(2, sqlLst.get(i).getExternalId());
            // CUSTOMER_ID int
            // entry.setCustomer(customer);
            stmt.setLong(3, sqlLst.get(i).getCustomer().getId());

            // STATUS int
            // entry.setStatus(rs.getInt(5));
            stmt.setInt(4, sqlLst.get(i).getStatus());
            // CREATION_TIME datetime
            // entry.setCreationTime(rs.getTimestamp(6));
            stmt.setTimestamp(5, sqlLst.get(i).getCreatTime());
            // entry.setDeliveryStationId(0L);
            stmt.setLong(6, sqlLst.get(i).getDeliveryStationId() == null ? 0L : sqlLst.get(i).getDeliveryStationId());
            // entry.setUserCode("''");
            stmt.setString(7, StringUtils.isEmpty(sqlLst.get(i).getUserCode()) ? "" : sqlLst.get(i).getUserCode());
            // 武汉2个字段

            stmt.addBatch();
            if ((i % this.commit_size) == 0) {
                stmt.executeBatch();
                conn.commit();
                ResultSet rs = stmt.getGeneratedKeys();
                while (rs.next()) {
                    ids.add(rs.getLong(1));
                }

            }
        }
        // 剩余未提交再做一次提交
        stmt.executeBatch();
        conn.commit();
        ResultSet rs = stmt.getGeneratedKeys();
        while (rs.next()) {
            ids.add(rs.getLong(1));
        }
        // conn.close();

        return ids;

    }

    private void batchInsertAlias(String url, List<Alias> sqlLst) throws SQLException {

        if (CollectionUtils.isEmpty(sqlLst)) {
            return;
        }
        Connection conn = this.getLocalConn();
        PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO `alias` (`ADDRESS_ID`, `CUSTOMER_ID`, `NAME`, `OLD_NAME`) VALUES (?,?,?,?)");
        conn.setAutoCommit(false);

        for (int i = 0; i < sqlLst.size(); i++) {
            // ID
            // ADDRESS_ID
            stmt.setLong(1, sqlLst.get(i).getAddressId());
            // CUSTOMER_ID
            stmt.setLong(2, sqlLst.get(i).getCustomerId());
            // NAME
            stmt.setString(3, sqlLst.get(i).getName());
            // OLD_NAME
            stmt.setString(4, sqlLst.get(i).getOldName());

            stmt.addBatch();
            if ((i % this.commit_size) == 0) {
                stmt.executeBatch();
                conn.commit();

            }
        }
        // 剩余未提交再做一次提交
        stmt.executeBatch();
        conn.commit();

        // conn.close();

    }

    private List<Long> batchInsertAddress(String url, List<AdressDto> sqlLst) throws SQLException {

        if (CollectionUtils.isEmpty(sqlLst)) {
            return null;
        }
        Connection conn = this.getLocalConn();
        PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO `address` (`NAME`, `OLD_NAME`, `STATUS`, `ADDRESS_TYPE_ID`, `ADDRESS_LEVEL`, `PARENT_ID`, `PATH`, `INDEXED`, `CREATION_TIME`) VALUES (?,?,?,?,?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
        conn.setAutoCommit(false);
        List<Long> ids = new ArrayList<Long>();

        for (int i = 0; i < sqlLst.size(); i++) {
            // ID int
            // NAME varchar
            stmt.setString(1, sqlLst.get(i).getName());
            // OLD_NAME varchar

            stmt.setString(2, sqlLst.get(i).getOldName());
            // STATUS int
            stmt.setInt(3, sqlLst.get(i).getStatus());
            // ADDRESS_TYPE_ID int
            stmt.setInt(4, sqlLst.get(i).getAddressTypeId());
            // ADDRESS_LEVEL int
            stmt.setInt(5, sqlLst.get(i).getAddressLevel());
            // PARENT_ID int
            stmt.setLong(6, sqlLst.get(i).getParentId());
            // PATH varchar
            stmt.setString(7, sqlLst.get(i).getPath());
            // INDEXED int
            stmt.setInt(8, 0);
            // CREATION_TIME datetime
            stmt.setTimestamp(9, sqlLst.get(i).getCreateTime());

            stmt.addBatch();
            if ((i % this.commit_size) == 0) {
                stmt.executeBatch();
                conn.commit();
                ResultSet rs = stmt.getGeneratedKeys();
                while (rs.next()) {
                    ids.add(rs.getLong(1));
                }

            }
        }
        // 剩余未提交再做一次提交
        stmt.executeBatch();
        conn.commit();
        ResultSet rs = stmt.getGeneratedKeys();
        while (rs.next()) {
            ids.add(rs.getLong(1));
        }
        // conn.close();

        return ids;

    }

    private List<Long> batchInsertSql(String url, List<String> sqlLst, boolean isNeed) throws SQLException {

        if (CollectionUtils.isEmpty(sqlLst)) {
            return null;
        }
        Connection conn = this.getLocalConn();
        Statement stmt = conn.createStatement();
        conn.setAutoCommit(false);
        List<Long> ids = new ArrayList<Long>();

        for (int i = 0; i < sqlLst.size(); i++) {

            String[] tmp = StringUtils.split(sqlLst.get(i), id_split);
            stmt.addBatch(tmp[1]);
            if ((i % this.commit_size) == 0) {
                stmt.executeBatch();
                conn.commit();
                if (isNeed) {
                    ResultSet rs = stmt.getGeneratedKeys();
                    while (rs.next()) {
                        ids.add(rs.getLong(1));
                    }
                }

            }
        }
        // 剩余未提交再做一次提交
        stmt.executeBatch();
        conn.commit();
        if (isNeed) {
            ResultSet rs = stmt.getGeneratedKeys();
            while (rs.next()) {
                ids.add(rs.getLong(1));
            }
        }
        // conn.close();

        return ids;

    }

    private Connection getLocalConn() throws SQLException {
        if (this.localConn == null) {
            this.localConn = DriverManager.getConnection(this.getConUrl());
        }
        if (this.localConn.isClosed()) {
            this.localConn = DriverManager.getConnection(this.getConUrl());
        }

        return this.localConn;
    }

    private void updateSql(String url, String sql) throws SQLException {

        Connection conn = this.getLocalConn();
        conn.setAutoCommit(true);
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(sql);

    }

    public static void main(String[] args) throws SQLException {
        Connection conn = DriverManager.getConnection(
                "jdbc:mysql://10.199.247.161:3306/address_shenyang?user=test&password=1234&useUnicode=true&characterEncoding=UTF8&rewriteBatchedStatements=true");
        //
        // String sql = " INSERT INTO `delivery_station_rules` (`RULE`, `RULE_EXPRESSION`, `RULE_TYPE`, `ADDRESS_ID`,
        // `DELIVERY_STATION_ID`, `CREATION_TIME`) VALUES (null, '', '1', '2373864', '9', '2016-03-25 17:04:11')";
        // Statement stmt = conn.createStatement();
        // stmt.executeUpdate(sql);
        String aa = "/dd/aa";
        System.out.println(aa);
        // System.out.println(StringUtils.split("adfdaf^^adfdfd", "^^^^^^^^^^^^^"));
        //
        // try {
        // PreparedStatement stmt = conn.prepareStatement(
        // "INSERT INTO `vendors_aging` (`CUSTOMER_ID`, `ADDRESS_ID`, `VENDORS_ID`, `AGING`, `create_time`) VALUES (?,
        // ?, ?, ?, NULL)");
        // conn.setAutoCommit(false);
        // for (int i = 0; i < 100; i++) {
        // stmt.setLong(1, i);
        // stmt.setLong(2, i);
        // stmt.setLong(3, i);
        // stmt.setString(4, "\'");
        // stmt.addBatch();
        // if ((i % Constants.DEFAULT_DICT_SIZE) == 0) {
        // stmt.executeBatch();
        // conn.commit();
        // }
        // }
        // stmt.executeBatch();
        // conn.commit();
        // } catch (SQLException e) {
        //
        // e.printStackTrace();
        // } finally {
        // }

    }

}
