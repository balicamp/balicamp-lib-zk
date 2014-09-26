package id.co.sigma.zk.ui.data;

import java.util.HashMap;
import java.util.Map;
import org.zkoss.json.JSONObject;

/**
 * State untuk class id.co.sigma.zk.ui.data.SelectableApplicationMenu
 * @author <a href="mailto:ida.suartama@sigma.co.id">Goesde Rai</a>
 */
public class SelectableApplicationMenuState extends JSONObject{
	private static final long serialVersionUID = 3061571139779080895L;
	
	static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(SelectableApplicationMenuState.class.getName());
	
	boolean opened;
	boolean disabled;
	boolean selected;
	
	public SelectableApplicationMenuState(boolean opened, boolean disabled, boolean selected) {
		super();
		setOpened(opened);
		setDisabled(disabled);
		setSelected(selected);
	}
	
	public boolean isOpened() {
		return opened;
	}
	public void setOpened(boolean opened) {
		this.opened = opened;
	}
	public boolean isDisabled() {
		return disabled;
	}
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public String toJSONString() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("opened", isOpened());
		map.put("disabled", isDisabled());
		map.put("selected", isSelected());
		return JSONObject.toJSONString(map);
	}
}
