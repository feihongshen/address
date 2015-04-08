package cn.explink.service;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.explink.dao.DeliveryStationDao;
import cn.explink.domain.DeliveryStation;
import cn.explink.gis.GeoCoder;
import cn.explink.gis.GeoPoint;
import cn.explink.gis.GeoUtility;
import cn.explink.util.StringUtil;

/**
 *
 * @author songkaojun
 * @since AR 1.0
 */
@Service
@Transactional
public class GisService {

	@Autowired
	private DeliveryStationDao deliveryStationDao;

	public List<DeliveryStation> search(String addressLine, Long customerId) {
		try {
			GeoPoint position = GeoCoder.getInstance().getGeoCoder().GetLocationDetails(addressLine);
			if (position == null) // 地理编码+POI检索失败
			{
				return null;
			}

			List<DeliveryStation> allStations = this.deliveryStationDao.listAll(customerId);

			if ((allStations == null) || (allStations.size() == 0)) // 站点返回为空
			{
				return null;
			}

			List<DeliveryStation> resultStations = new ArrayList<DeliveryStation>();

			// 遍历全部站点，进行空间匹配
			for (DeliveryStation deliveryStation : allStations) {

				String coorString = deliveryStation.getCoordinate();
				if (StringUtil.isEmpty(coorString)) {
					continue;
				}
				// TODO 格式可能变化
				coorString = coorString.substring(coorString.indexOf("path") + 6, coorString.indexOf("}]") + 2);

				if (coorString == null) {
					// 站点配送范围获取失败
					continue;
				}

				@SuppressWarnings("unchecked")
				List<GeoPoint> pts = (List<GeoPoint>) JSONArray.toCollection(JSONArray.fromObject(coorString), GeoPoint.class);

				if (pts == null) // 站点的配送区域坐标查询失败
				{
					continue;
				}

				// 空间检索
				GeoPoint[] polygon = new GeoPoint[pts.size()];
				if (GeoUtility.isInPolygon(position, pts.toArray(polygon))) {
					resultStations.add(deliveryStation);
				}
			}
			return resultStations;

		} catch (Exception e) {
			System.out.print(e);
		}

		return null;
	}

	public DeliveryStationDao getDeliveryStationDao() {
		return this.deliveryStationDao;
	}

	public void setDeliveryStationDao(DeliveryStationDao deliveryStationDao) {
		this.deliveryStationDao = deliveryStationDao;
	}
}
