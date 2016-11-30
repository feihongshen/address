/**
 * 
 */
package cn.explink.init;

import java.util.ArrayList;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * spring容器启动完成后,初始化变量
 * @author xinming01.lu
 *
 */
public class InstantiationTracingBeanPostProcessor implements ApplicationListener<ContextRefreshedEvent> {
	private static final Logger LOGGER = LoggerFactory.getLogger(InstantiationTracingBeanPostProcessor.class);
	
	@Value("${baidu_map_key}")
	private String baidu_map_key="pz8gGVf2lcvlbQmg3e5QgOVi,liVsUEXGw2g8SbGYdK4IYnKG";
	
	//private String baidu_map_key = "pz8gGVf2lcvlbQmg3e5QgOVi,liVsUEXGw2g8SbGYdK4IYnKG,6C4PXqYKl3KvbOIi4zkyQTXUMj28pSLb,z2RoCbclxOBdAoxhEWi8aNd0V4Dxn6jA,xFcTZ0R0EdUc3wuCGKfZMhrdEy8PFKSW,CPsu08dyPpMsmw6eDBNpmDtek1D9WIFo,QSLxe2LHDHt1C2DTGzi14GvmMjG0TweE";

	/* (non-Javadoc)
	 * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
	 */
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (event.getApplicationContext().getParent() == null) {// root application context 没有parent，具为最上层容器.(spring允许嵌套多层容器,为防止多次处理,只能在最上层容器处理)
			LOGGER.warn("百度地图key初始化前："+Arrays.toString(cn.explink.Constants.baidu_map_key.toArray()));
			this.setBaiduMapKey();
			LOGGER.warn("百度地图key初始化后："+Arrays.toString(cn.explink.Constants.baidu_map_key.toArray()));
		}
		
	}
	
	/**
	 * 设置百度地图key
	 * @author xinming01.lu
	 * @edit_log 2016-11-30 xinming01.lu 新增
	 */
	private void setBaiduMapKey(){
		if (cn.explink.Constants.baidu_map_key == null) {
			cn.explink.Constants.baidu_map_key = new ArrayList<String>(7);
		}
		if (baidu_map_key == null) {
			LOGGER.error("初始化百度地图key失败,百度地图key为null");
			return;
		}
		String[] keys = baidu_map_key.split(",");
		String key = null;
		for (int i = 0; i < keys.length; i++) {
			key = keys[i];
			
			if(key!=null && key.trim().length()>0){
				cn.explink.Constants.baidu_map_key.add(key.trim());
			}
		}
		if (cn.explink.Constants.baidu_map_key == null || cn.explink.Constants.baidu_map_key.isEmpty()) {
			LOGGER.error("初始化百度地图key失败,百度地图key为null");
			return;
		}
	}
}
