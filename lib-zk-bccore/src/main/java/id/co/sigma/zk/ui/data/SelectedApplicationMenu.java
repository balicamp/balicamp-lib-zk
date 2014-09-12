package id.co.sigma.zk.ui.data;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import id.co.sigma.common.security.domain.ApplicationMenu;

/**
 * Selected Application Menu
 * @author <a href="mailto:ida.suartama@sigma.co.id">Goesde Rai</a>
 */

@Entity
@Table(name="sec_menu")
public class SelectedApplicationMenu extends ApplicationMenu{
	private static final long serialVersionUID = 2132390938444583319L;
	
	static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(SelectedApplicationMenu.class.getName());
	
	@Transient
	private boolean selected;

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

}
