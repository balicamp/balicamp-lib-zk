package id.co.sigma.zk.ui.controller.report;

import id.co.sigma.common.report.domain.RptDocParam;
import id.co.sigma.common.server.dao.util.ServerSideWrappedJSONParser;
import id.co.sigma.common.util.json.ParsedJSONArrayContainer;
import id.co.sigma.zk.ui.controller.ZKEditorState;
import id.co.sigma.zk.ui.controller.base.BaseSimpleNoDirectToDBEditor;

import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;

public class ReportParameterEditorController extends
		BaseSimpleNoDirectToDBEditor<RptDocParam> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1367577843402068150L;
	
	@Wire	
	private Textbox paramCode;
	
	@Wire
	private Grid formGrid;
	
	@Wire
	private Row rowLovClass;

	@Wire
	private Row rowSeparator;

	@Wire
	private Row rowDependencies;

	@Wire
	private Row rowFilters;

	@Wire
	private Row rowCbmItem;
	
	@Wire
	private Listbox lstLoVFilter;
	
	@Wire
	private Listbox lstComboItem;
	
	@Wire
	private Listbox lstLoVDepedencies;
	
	@Wire
	private Textbox lovFilters;
	
	@Wire
	private Combobox paramType;

	private boolean showLov = false;
	private boolean showLookup = false;
	private boolean showCmbItem = false;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		if(ZKEditorState.ADD_NEW.equals(getEditorState())) {
			paramCode.setReadonly(false);
		} else {
			String type = getEditedData().getParamType();
			showCmbItem = ("Combobox".equals(type));
			showLov = ("LoVCombobox".equals(type));
			showLookup = ("LookupCombobox".equals(type));
		}
		rowLovClass.setVisible(showLov || showLookup);
		rowSeparator.setVisible(showLov || showLookup);
		rowDependencies.setVisible(showLov);
		rowFilters.setVisible(showLov);
		rowCbmItem.setVisible(showCmbItem);
	}
	
	@Listen("onSelect = #paramType")
	public void onSelectParamType(Event ev) {
		Combobox trgt = (Combobox)ev.getTarget();
		if(trgt.getSelectedIndex() >= 0) {
			showCmbItem = ("Combobox".equals(trgt.getSelectedItem().getValue()));
			showLov = ("LoVCombobox".equals(trgt.getSelectedItem().getValue()));
			showLookup = ("LookupCombobox".equals(trgt.getSelectedItem().getValue()));
			
			rowLovClass.setVisible(showLov || showLookup);
			rowSeparator.setVisible(showLov || showLookup);
			rowDependencies.setVisible(showLov);
			rowFilters.setVisible(showLov);
			rowCbmItem.setVisible(showCmbItem);
			
			Label l = (Label)rowLovClass.getFirstChild();
			l.setValue(showLookup ? "Kode Lookup" : "LoV Class");
			
			getSelf().invalidate();
		}
	}
	
	@Listen("onAddFilter = #editorParams")
	public void onAddFilter(ForwardEvent event) {
		Component list = event.getOrigin().getTarget().getParent().getParent().getParent();
		Listcell parent = (Listcell)event.getOrigin().getTarget().getParent();
		list.appendChild(createFilterListitem((Listitem)list.getLastChild()));
		event.getOrigin().getTarget().setVisible(false);
		parent.getLastChild().setVisible(true);
		
		getSelf().invalidate();
	}
	
	@Listen("onDelFilter = #editorParams")
	public void onDelFilter(ForwardEvent event) {
		Component listItem = event.getOrigin().getTarget().getParent().getParent();
		Component list = event.getOrigin().getTarget().getParent().getParent().getParent();
		list.removeChild(listItem);
		
		getSelf().invalidate();
	}
	
	@Listen("onDelComboitem = #editorParams")
	public void onDelComboitem(ForwardEvent event) {
		onDelFilter(event);
	}
	
	@Listen("onAddComboitem = #editorParams")
	public void onAddComboitem(ForwardEvent event) {
		onAddFilter(event);
	}

	@Listen("onAddDependcies= #editorParams")
	public void onAddDependcies(ForwardEvent event) {
		onAddFilter(event);
	}
	
	@Listen("onDelDependcies= #editorParams")
	public void onDelDependcies(ForwardEvent event) {
		onDelFilter(event);
	}
	
	/**
	 * @return the showLov
	 */
	public boolean isShowLov() {
		return showLov;
	}

	/**
	 * @return the showLookup
	 */
	public boolean isShowLookup() {
		return showLookup;
	}

	/**
	 * @return the showCmbItem
	 */
	public boolean isShowCmbItem() {
		return showCmbItem;
	}
	
	
	@Override
	protected void parseEditedData(Component comp) {		
		super.parseEditedData(comp);
		getEditedData().setLovFilters(parseLoVFilters());
		getEditedData().setStaticData(parseComboboxItems());
		getEditedData().setLovParentId(parseLoVDependencies());
		System.out.println(getEditedData().getLovParentId());
	}

	private String parseLoVFilters() {
		return parseToJSONString(lstLoVFilter);
	}
	
	private String parseLoVDependencies() {
		return parseToJSONString(lstLoVDepedencies);
	}
	
	private String parseComboboxItems() {
		return parseToJSONString(lstComboItem);
	}
	
	private String parseToJSONString(Listbox lbox) {
		List<Listitem> items = lbox.getItems();
		Listhead head = lbox.getListhead();
		List<Listheader> headers = head.getChildren();
		
		StringBuffer filters = new StringBuffer();
		for(Listitem item : items) {
			List<Component> cmps = item.getChildren();
			StringBuffer filter = new StringBuffer();
			for(int i = 0; i < headers.size() - 1; i++) {
				Component inp = cmps.get(i).getFirstChild();
				Object o = null;
				if(inp instanceof Combobox) {
					o = ((Combobox)inp).getSelectedItem();
					if(o != null) {
						o = ((Comboitem)o).getValue();
					}
				} else if(inp instanceof Textbox) {
					o = ((Textbox)inp).getValue();
				} else if(inp instanceof Listbox) {
					o = ((Listbox)inp).getSelectedItem().getValue();
				}
				if(o != null) {
					String sval = String.valueOf(o);
					if(!"".equals(sval.trim())) {
						if(filter.length() > 0) filter.append(",");
						filter.append("\"").append(headers.get(i).getId()).append("\":\"").append(sval).append("\"");
					}
				}
			}
			if(filter.length() > 0) {
				filter.insert(0, "{").append("}");
				if(filters.length() > 0) {
					filters.append(",");
				}
				filters.append(filter);
			}
		}
		if(filters.length() > 0) {
			filters.insert(0, "[").append("]");
		}
		return filters.toString();
	}
	
	private Listitem createFilterListitem(Listitem last) {
		Listitem item = (Listitem)last.clone();
		for(Component cell : item.getChildren()) {
			Component f = cell.getFirstChild();
			if(f instanceof Textbox) {
				((Textbox)f).setValue("");
			} else if (f instanceof Combobox) {
				((Combobox)f).setValue("");
			}
		}
		return item;
	}
	
}
