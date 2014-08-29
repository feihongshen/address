package cn.explink.lucene;

import org.springframework.beans.factory.annotation.Autowired;

import cn.explink.service.LuceneService;

public class LuceneInitializer {

	public static boolean inited;
	
	@Autowired
	private LuceneService luceneService;
	
	public void initLucene() {
		if (!inited) {
			synchronized (LuceneInitializer.class) {
				if (!inited) {
					luceneService.initLucene();
					inited = true;
				}
				LuceneInitializer.class.notifyAll();
			}
		}
	}
	
}
