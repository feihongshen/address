
package cn.explink.web.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.explink.dao.CustomerDao;
import cn.explink.db.merge.ValidateDto;
import cn.explink.domain.Customer;
import cn.explink.domain.User;
import cn.explink.modle.AjaxJson;
import cn.explink.service.MergeService;

@RequestMapping("/common")
@Controller
public class CommonController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(CommonController.class);

    @Autowired
    MergeService mergeService;

    @Autowired
    CustomerDao customerDao;

    @RequestMapping("/index")
    public String hello(Model model) {
        User user = this.getLogginedUser();
        logger.info("user = " + user.toString());
        return "/common/index";
    }

    @RequestMapping("/merge")
    public String merge() {
        return "/common/merge";
    }

    @RequestMapping("/mergeUpdate")
    @ResponseBody
    public AjaxJson mergeUpdate(Long customerId) {

        AjaxJson aj = new AjaxJson();
        try {
            // 1、清除customerId相关数据
            this.mergeService.txNewdelData(customerId);
            // 2、根据customerId得到数据库连接符,省Id
            Customer customer = this.mergeService.getCustomer(customerId);
            String url = this.mergeService.getUrlMap().get(customerId);
            // 一个Connection代表一个数据库连接
            Connection conn = DriverManager.getConnection(url);
            // 3、合并customer相关数据
            this.mergeService.doExcCustomer(conn, customer);
            // 5、合并delivery_stations相关数据
            this.mergeService.txNewDeliveryStations(conn, customer);
            // 4、合并address相关数据
            // try {
            this.mergeService.doExcAddress(conn, customer);
            // } catch (Exception e) {
            // System.out.println(e);
            // }

            // 6、合并deliverers相关数据
            this.mergeService.doExcDeliverers(conn, customer);
            // 7、合并完成校验数据
            // this.validate(conn, customer);
            conn.close();

        } catch (SQLException e) {

            aj.setInfo(e.getMessage());
            this.mergeService.txNewdelData(customerId);
        } catch (Exception e) {
            aj.setInfo(e.getMessage());
            this.mergeService.txNewdelData(customerId);
        }

        finally {
            aj.setSuccess(true);
            aj.setInfo("处理成功");
            return aj;
        }
    }

    @RequestMapping("/validate")
    @ResponseBody
    public AjaxJson validate(Long customerId) {
        ValidateDto dto = null;
        try {
            dto = this.mergeService.validate(customerId);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        AjaxJson aj = new AjaxJson();
        aj.setSuccess(true);
        aj.setObj(dto == null ? new ValidateDto() : dto);
        return aj;
    }

    @RequestMapping("/resetdb")
    @ResponseBody
    public AjaxJson resetdb() {
        this.mergeService.resetUrlMap();
        AjaxJson aj = new AjaxJson();
        aj.setSuccess(true);
        return aj;
    }

}
