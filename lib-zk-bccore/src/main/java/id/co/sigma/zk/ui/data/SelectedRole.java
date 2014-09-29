package id.co.sigma.zk.ui.data;

import id.co.sigma.common.security.domain.Role;

import javax.persistence.Transient;

/**
 * 
 * @author <a href="mailto:gusti.darmawan@sigma.co.id">Eka Darmawan</a>
 */
public class SelectedRole extends Role{
	static final org.slf4j.Logger logger = org.slf4j.LoggerFactory
			.getLogger(SelectedRole.class.getName());
	
	public SelectedRole() {
		super();
	}
	
	public SelectedRole(Role data) {
		setId(data.getId());
		setRoleCode(data.getRoleCode());
		setIsPredefined(data.getIsPredefined());
		setRoleDesc(data.getRoleDesc());
	}
	
	
	@Transient
	private boolean selected = false;

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}
