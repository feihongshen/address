package cn.explink.spliter.vo;

/**
 * @author songkaojun
 * @since AR1.0
 */
public class RawAddressQuickVO {

	private Long id;

	private String name;

	public RawAddressQuickVO() {
		super();
	}

	public RawAddressQuickVO(Long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
