package cn.explink.gis;

/**
 * 矩形
 * @author Administrator
 *
 */
public class GeoRectangle {

	
	private GeoPoint _leftTop;
	private GeoPoint _rightBottom;
	
	/**
	 * 构造函数
	 * @param lefttop 左上点
	 * @param rightbottom 右下点
	 */
	public GeoRectangle(GeoPoint lefttop,GeoPoint rightbottom)
	{
		this._leftTop=lefttop;
		this._rightBottom=rightbottom;
	}
	
	/**
	 * 构造函数
	 * @param ltlng 左上经度
	 * @param ltlat 左上纬度
	 * @param rblng 右下经度
	 * @param rblat 右下纬度
	 */
	public GeoRectangle(double ltlng,double ltlat,double rblng,double rblat)
	{
		this._leftTop=new GeoPoint(ltlng,ltlat);
		this._rightBottom=new GeoPoint(rblng,rblat);
	}
	
	/**
	 * 左上点
	 * @return
	 */
	public GeoPoint getLeftTop()
	{
		return this._leftTop;
	}
	
	/**
	 * 右下点
	 * @return
	 */
	public GeoPoint getRightBottom()
	{
		return this._rightBottom;
	}
	
	
	
}
