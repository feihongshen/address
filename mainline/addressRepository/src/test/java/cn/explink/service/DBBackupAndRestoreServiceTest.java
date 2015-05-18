package cn.explink.service;

import org.junit.Test;

import cn.explink.test.support.BaseTestCase;

public class DBBackupAndRestoreServiceTest extends BaseTestCase {

	@Test
	public void testBackup() {
		DBBackupAndRestoreService.backup("ar_gxpx_0130");

	}

	@Test
	public void testRestore() {
		DBBackupAndRestoreService.restore("ar_gxpx_0130");

	}
}
