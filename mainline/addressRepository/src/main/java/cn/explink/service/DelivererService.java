
package cn.explink.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.explink.dao.CustomerDao;
import cn.explink.dao.DelivererDao;
import cn.explink.dao.DeliveryStationDao;
import cn.explink.domain.Customer;
import cn.explink.domain.Deliverer;
import cn.explink.domain.DeliveryStation;
import cn.explink.domain.enums.DelivererStausEnmu;
import cn.explink.modle.ComboBox;
import cn.explink.ws.vo.DelivererVo;

@Service
public class DelivererService {

    @Autowired
    private DelivererDao delivererDao;

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private DeliveryStationDao deliveryStationDao;

    public Deliverer createDeliverer(DelivererVo delivererVo) {
        Customer customer = this.customerDao.get(delivererVo.getCustomerId());
        if (customer == null) {
            throw new RuntimeException("customer is not exist");
        }

        Deliverer deliverer = this.delivererDao.getDeliverer(delivererVo.getCustomerId(), delivererVo.getExternalId());
        if (deliverer == null) {
            deliverer = new Deliverer();
        }

        // 根据站点id+customerId找到对应的阡陌站点id
        DeliveryStation station = this.deliveryStationDao.getDeliveryStation(delivererVo.getCustomerId(),
                delivererVo.getExternalStationId());
        if (station == null) {
            throw new RuntimeException("station_id is not exist");
        }

        BeanUtils.copyProperties(delivererVo, deliverer);
        deliverer.setCreationTime(new Date());
        deliverer.setDeliveryStationId(station.getId());// 阡陌地址库站点id
        deliverer.setStatus(DelivererStausEnmu.valid.getValue());
        deliverer.setCustomer(customer);
        this.delivererDao.save(deliverer);
        return deliverer;
    }

    public Deliverer updateDeliverer(DelivererVo delivererVo) {
        Deliverer deliverer = this.delivererDao.getDeliverer(delivererVo.getCustomerId(), delivererVo.getExternalId());
        deliverer.setName(delivererVo.getName());
        deliverer.setStatus(DelivererStausEnmu.valid.getValue());
        deliverer.setUserCode(delivererVo.getUserCode());
        this.delivererDao.save(deliverer);
        return deliverer;
    }

    public Deliverer deleteDeliverer(DelivererVo delivererVo) {
        Deliverer deliverer = this.delivererDao.getDeliverer(delivererVo.getCustomerId(), delivererVo.getExternalId());
        deliverer.setStatus(DelivererStausEnmu.invalid.getValue());
        this.delivererDao.save(deliverer);
        return deliverer;
    }

    public Deliverer getDeliverer(Long id) {
        return this.delivererDao.get(id);
    }

    public Deliverer getByNameAndCustomerId(String delivererName, Long customerId) {
        return this.delivererDao.getByNameAndCustomerId(delivererName, customerId);
    }

    public List<Deliverer> listAll(Long customerId) {
        return this.delivererDao.listAll(customerId);
    }

    /**
     * 根据站点id获取list-小件员
     * <p>
     * 方法详细描述
     * </p>
     * @param stationId
     * @return
     * @since 1.0
     */
    public List<ComboBox> getDelivererComBoxByStation(String stationId, Long customerId) {
        return this.delivererDao.getDelivererComBoxByStation(Long.valueOf(stationId), customerId);
    }

}
