package cn.explink.gis;

public interface IRouteMatrix {

	
	/**
	 * 获取两点之间的导航路径距离（通过导航生成的路径的距离）
	 * @author Administrator
	 * 
	 */
	double getRouteDistance(GeoPoint startPt,GeoPoint endPt);
	
}
