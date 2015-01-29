/**
 *
 */
package cn.explink.quick;

/**
 * @author songkaojun
 * @since AR1.0
 */
public class AddressQuickVO {

	private Long id;

	private String name;

	public AddressQuickVO() {
		super();
	}

	public AddressQuickVO(Long id, String name) {
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
