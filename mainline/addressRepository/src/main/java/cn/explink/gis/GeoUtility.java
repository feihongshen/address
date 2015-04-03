package cn.explink.gis;
/**
 * 地理基础算法实现
 * 
 * @author Administrator
 *
 */
public class GeoUtility {

	/**
	 * 点是否在多边形内
	 * @param point  点
	 * @param polygon 多边形顶点数组
	 * @return
	 */
	public static boolean isInPolygon(GeoPoint point, GeoPoint[] polygon) {

		if(point==null|| polygon==null)
		{
			return false;
		}
		
		// 1. 点是否在多边形的外包围框中。
		// 外包围框可以保存下来
		GeoRectangle extend =getBounds(polygon);
		
		if(extend==null)
		{
			return false;
		}

		// 点不在外接矩形内
		if(!isInRectangle(point, extend))
		{
			return false;
		}
		
		// 2. 点是否在多边形中
		GeoPoint[] pts = polygon;// 多边形点

		// 基本思想是利用射线法，计算射线与多边形各边的交点，如果是偶数，则点在多边形外，否则
		// 在多边形内。还会考虑一些特殊情况，如点在多边形顶点上，点在多边形边上等特殊情况。

		int N = pts.length;
		boolean boundOrVertex = true; // 如果点位于多边形的顶点或边上，也算做点在多边形内，直接返回true
		int intersectCount = 0;// 交点个数
		double precision = 2e-10; // 浮点类型计算时候与0比较时候的容差
		GeoPoint p1, p2;// neighbour bound vertices
		GeoPoint p = point; // 测试点

		p1 = pts[0];// 左点
		// 测试全部边
		for (int i = 1; i <= N; ++i) {
			if (p.equals(p1)) {
				return boundOrVertex;// 给定测试点是多边形的顶点
			}

			p2 = pts[i % N];// 右点
			if (p.getLat() < Math.min(p1.getLat(), p2.getLat())
					|| p.getLat() > Math.max(p1.getLat(), p2.getLat())) {
				// 射线和这个线段无交点
				p1 = p2;
				continue;// 这次线段的右点作为下个线段的左点
			}

			if (p.getLat() > Math.min(p1.getLat(), p2.getLat())
					&& p.getLat() < Math.max(p1.getLat(), p2.getLat())) {
				// ray is crossing over by the algorithm (common part of)
				if (p.getLng() <= Math.max(p1.getLng(), p2.getLng())) {
					// x is before of ray
					if (p1.getLat() == p2.getLat()
							&& p.getLng() >= Math.min(p1.getLng(), p2.getLng())) {
						// overlies on a horizontal ray
						return boundOrVertex;
					}

					if (p1.getLng() == p2.getLng()) {
						// ray is vertical
						if (p1.getLng() == p.getLng()) {
							// overlies on a vertical ray
							return boundOrVertex;
						} else {
							// before ray
							++intersectCount;
						}
					} else {
						// cross point on the left side
						double xinters = (p.getLat() - p1.getLat())
								* (p2.getLng() - p1.getLng())
								/ (p2.getLat() - p1.getLat()) + p1.getLng();
						// cross point of getLng()
						if (Math.abs(p.getLng() - xinters) < precision) {
							// overlies on a ray
							return boundOrVertex;
						}

						if (p.getLng() < xinters) {
							// before ray
							++intersectCount;
						}
					}
				}
			} else {
				// special case when ray is crossing through the vertex
				if (p.getLat() == p2.getLat() && p.getLng() <= p2.getLng()) {
					// p crossing over p2
					// next vertex
					GeoPoint p3 = pts[(i + 1) % N];
					if (p.getLat() >= Math.min(p1.getLat(), p3.getLat())
							&& p.getLat() <= Math.max(p1.getLat(), p3.getLat())) {
						// p.lat lies between p1.lat & p3.lat
						++intersectCount;
					} else {
						intersectCount += 2;
					}
				}
			}
			p1 = p2;// next ray left point
		}

		if (intersectCount % 2 == 0) {
			// 偶数在多边形外
			return false;
		} else {
			// 奇数在多边形内
			return true;
		}
	}

	/**
	 * 判断点是否在矩形内
	 * 
	 * @param point
	 *            点
	 * @param rectangle
	 *            矩形
	 * @return
	 */
	public static boolean isInRectangle(GeoPoint point, GeoRectangle rectangle) {
		GeoPoint lt = rectangle.getLeftTop();
		GeoPoint rb = rectangle.getRightBottom();
		return (point.getLng() >= lt.getLng() && point.getLng() <= rb.getLng()
				&& point.getLat() >= rb.getLat() && point.getLat() <= lt
				.getLat());
	}
	
	/**
	 * 获取一组顶点的外接矩形
	 * 不知是否存在更优的实现。
	 * @param polygon
	 * @return
	 */
	public static GeoRectangle getBounds(GeoPoint[] polygon) {

		//数据检测
		if (polygon==null||polygon.length==0) {
			return null;
		}
		
		// 获取一组顶点的外接矩形
		
		GeoPoint first=polygon[0];  // 记录第一个点
		double maxLng=first.getLng();    //最大经度
		double maxLat=first.getLat();    //最大纬度
		double minLng=first.getLng();	//最小经度
		double minLat=first.getLat();	//最小纬度
		// 遍历每个点
		for (int i = 1,length=polygon.length; i < length; i++) {
			// 最大经度
			if (polygon[i].getLng()>maxLng) {
				maxLng=polygon[i].getLng();
			}
			// 最小经度
			if (polygon[i].getLng()<minLng) {
				minLng=polygon[i].getLng();
			}
			// 最大纬度
			if (polygon[i].getLat()>maxLat) {
				maxLat=polygon[i].getLat();
			}
			// 最小纬度
			if (polygon[i].getLat()<minLat) {
				minLat=polygon[i].getLat();
			}
		}
		// 返回矩形
		return new GeoRectangle(minLng, maxLat,maxLng,minLat);

	}

}
