package cn.explink.gis;

/**
 * 兴趣点 逆地理编码返回结果附近的兴趣点
 *
 * @author Administrator
 *
 */
public class POI implements Comparable<POI> {

	/**
	 * 地址
	 */
	private String addr;

	/**
	 * ?
	 */
	private String cp;
	/**
	 * 方位
	 */
	private String direction;
	/**
	 * 距离
	 */
	private String distance;
	/**
	 * 名称
	 */
	private String name;
	/**
	 * 类型
	 */
	private String poiType;

	/**
	 * 位置
	 */
	private Point point;
	/**
	 * 电话
	 */
	private String tel;
	/**
	 * uid
	 */
	private String uid;
	/**
	 * zip
	 */
	private String zip;

	public Point getPoint() {
		return this.point;
	}

	public void setPoint(Point point) {
		this.point = point;
	}

	public String getTel() {
		return this.tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getUid() {
		return this.uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getZip() {
		return this.zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getAddr() {
		return this.addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPoiType() {
		return this.poiType;
	}

	public void setPoiType(String poiType) {
		this.poiType = poiType;
	}

	public String getDirection() {
		return this.direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getDistance() {
		return this.distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public String getCp() {
		return this.cp;
	}

	public void setCp(String cp) {
		this.cp = cp;
	}

	@Override
	public int compareTo(POI o) {
		int distance = Integer.parseInt(this.distance);
		int distance1 = Integer.parseInt(o.getDistance());
		if (distance > distance1) {
			return 1;
		} else if (distance < distance1) {
			return -1;
		} else {
			return 0;
		}
	}
}
