
package cn.explink.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.explink.dao.CustomerDao;
import cn.explink.dao.VendorDao;
import cn.explink.domain.Customer;
import cn.explink.domain.Vendor;
import cn.explink.domain.enums.VendorStausEnmu;
import cn.explink.exception.ExplinkRuntimeException;
import cn.explink.modle.ComboBox;
import cn.explink.ws.vo.VendorVo;

@Service
@Transactional
public class VendorService {

    @Autowired
    private VendorDao vendorDao;

    @Autowired
    private CustomerDao customerDao;

    public Vendor createVendor(VendorVo vendorVo) {
        Customer customer = this.customerDao.get(vendorVo.getCustomerId());
        if (customer == null) {
            throw new ExplinkRuntimeException("customer not exist for id " + vendorVo.getCustomerId());
        }

        Vendor vendor = this.vendorDao.getVendor(vendorVo.getExternalId(), vendorVo.getCustomerId());
        if (vendor == null) {
            vendor = new Vendor();
        }
        BeanUtils.copyProperties(vendorVo, vendor);
        vendor.setStatus(VendorStausEnmu.valid.getValue());
        vendor.setCreationTime(new Date());
        vendor.setCustomer(customer);
        this.vendorDao.save(vendor);
        return vendor;
    }

    public Vendor updateVendor(VendorVo vendorVo) {
        Vendor vendor = this.vendorDao.getVendor(vendorVo.getExternalId(), vendorVo.getCustomerId());
        vendor.setName(vendorVo.getName());
        vendor.setStatus(VendorStausEnmu.valid.getValue());
        this.vendorDao.save(vendor);
        return vendor;
    }

    public Vendor deleteVendor(VendorVo vendorVo) {
        Vendor vendor = this.vendorDao.getVendor(vendorVo.getExternalId(), vendorVo.getCustomerId());
        vendor.setStatus(VendorStausEnmu.invalid.getValue());
        this.vendorDao.save(vendor);
        return vendor;
    }

    public List<ComboBox> getAllvendor(Long customerId) {
        return this.vendorDao.getComBoxDeliveryStation(customerId);
    }

    public void deleteAgingByIds(List<Long> addressIdList, Long customerId) {
        this.vendorDao.deleteAgingByIds(addressIdList, customerId);
    }

}
