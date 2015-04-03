package cn.explink.gis;

/**
 * 地理 点对象
 * 
 * @author Administrator
 *
 */
public class GeoPoint  {

	private double lng;
	private double lat;

	
	public GeoPoint()
	{
		this.lng=0;
		this.lat=0;
	}
	
	/**
	 * 构造函数
	 * 
	 * @param lng
	 *            经度
	 * @param lat
	 *            纬度
	 *            
	*/
	public GeoPoint(double lng, double lat) {
		this.lng = lng;
		this.lat = lat;
	}

	/**
	 * 获取经度值
	 * 
	 * @return 经度值
	 */
	public double getLng() {
		return this.lng;
	}

	/**
	 * 设置经度值
	 * 
	 * @param lng
	 *            经度值
	 */
	public void setLng(double lng) {
		this.lng = lng;
	}

	/**
	 * 获取纬度值
	 * 
	 * @return 纬度值
	 */
	public double getLat() {
		return this.lat;
	}

	/**
	 * 设置纬度值
	 * 
	 * @param lat
	 *            纬度值
	 */
	public void setLat(double lat) {
		this.lat = lat;
	}

	
    @Override
    public boolean equals(Object obj) {
    	
    	if(obj==null)
    	{
    		return false;
    	}
    	
    	if(getClass()!=obj.getClass())
    	{
    		return false;
    	}
    	
    	double precision = 2e-10; // 浮点类型计算时候与0比较时候的容差
    	
    	GeoPoint pt=(GeoPoint)obj;
    	
    	// 相等
    	if((Math.abs(pt.getLat()-this.getLat())<precision) && (Math.abs(pt.getLng()-this.getLng())<precision))
    	{
    		return true;
    	}
    	
    	return false;

    }
	
	
}
