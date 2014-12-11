package id.co.sigma.zk.ui.custom.component;

import id.co.sigma.common.server.dao.IGeneralPurposeDao;
import id.co.sigma.common.server.util.ExtendedBeanUtils;
import id.co.sigma.zk.ui.annotations.ListOfValue;
import id.co.sigma.zk.ui.controller.base.BaseSimpleController;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.zk.au.AuResponse;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Bandpopup;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;

/**
 * Bandbox dgn fungsi autocomplete
 * @author <a href="mailto:ida.suartama@sigma.co.id">Goesde Rai</a>
 */
public class BcBandbox extends Bandbox implements IdSpace, AfterCompose{
	
	private static final long serialVersionUID = -3806592381198871322L;
	
	static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(BcBandbox.class.getName());
	
	private ListModelList<Object> listData;
	
	private BaseSimpleController controller;
	
	@Wire
	private Bandpopup bandPopup;
	
	@Wire 
	private Listbox listbox;
	
	@Wire
	private Textbox selectedItem;
	
	private IGeneralPurposeDao generalPurposeDao;
	
	private static final ExtendedBeanUtils BEAN_UTILS = ExtendedBeanUtils.getInstance();
	
	
	protected class LOVParam {
		String lovFQCN ;  
		String valueField ; 
		String labelField ; 
		String separator ; 
		boolean appendNoneSelectedMarker ; 
		String noneSelectedLabel ; 
		
		public LOVParam() {}

		public LOVParam(String lovFQCN, String valueField, String labelField,
				String separator, boolean appendNoneSelectedMarker,
				String noneSelectedLabel) {
			super();
			this.lovFQCN = lovFQCN;
			this.valueField = valueField;
			this.labelField = labelField;
			this.separator = separator;
			this.appendNoneSelectedMarker = appendNoneSelectedMarker;
			this.noneSelectedLabel = noneSelectedLabel;
		}
		
	}
	
	/**
	 * LOV Mapping <br/> 
	 * [class, valueField, labelField, separator]
	 */
	//private String[] lovMap = null;
	private LOVParam lovMap; 
	
	public ListModelList<Object> getListData() {
		return listData;
	}
	
	public void setListData(ListModelList<Object> listData) {
		this.listData = listData;
	}
	
	public BaseSimpleController getController() {
		return controller;
	}
	
	public void setController(BaseSimpleController controller) {
		this.controller = controller;
	}
	
	public BcBandbox(){
		Executions.createComponents("~./zul/pages/common/BcBandbox.zul", this, null);
		Selectors.wireComponents(this, this, false);
		Selectors.wireEventListeners(this, this);
	}
	
	public Object getObjectValue(){
		return listbox.getSelectedItem();
	}

	public final void getDataList(final Event event) {
		List<ListOfValueItem> list = new ArrayList<ListOfValueItem>();
		String cName = getId();
		String sFilter = "";
		if(lovMap != null) {
			cName = lovMap.lovFQCN;
			if(controller != null) {
				if(generalPurposeDao != null) {
					try {
						
						InputEvent iEvent = (InputEvent)event;
						
						sFilter = iEvent.getValue();
						
						List<Object> lov = generalPurposeDao.list("lov", 
								cName + 
								" where lov." + (String)lovMap.valueField + " like '%" + iEvent.getValue() + "%'" + 
								" or lov." + (String)lovMap.labelField + " like '%"  + iEvent.getValue() + "%'", 
								null);
						
						if(lov != null) {
							if ( lovMap.appendNoneSelectedMarker){
								list.add(new ListOfValueItem("" , lovMap.noneSelectedLabel , lovMap.separator)); 
							}
							for(Object i : lov) {
								list.add(new ListOfValueItem( 
										String.valueOf(BEAN_UTILS.getProperty(i, (String)lovMap.valueField)), 
										String.valueOf(BEAN_UTILS.getProperty(i, (String)lovMap.labelField)), 
										(String)lovMap.separator));
							}
						}
						
					} catch (Exception e) {
						
					}
				}
			} else {
				//just for testing				
				InputEvent iEvent = (InputEvent)event;
				sFilter = iEvent.getValue();
				for(int i = 0; i < 10; i++) {					
					list.add(new ListOfValueItem(iEvent.getValue() + i, iEvent.getValue(), (String)lovMap.separator));
				}
			}
			
		} else {
			InputEvent iEvent = (InputEvent)event;
			sFilter = iEvent.getValue();
			for(int i = 0; i < 10; i++) {					
				list.add(new ListOfValueItem(iEvent.getValue() + i, iEvent.getValue(), " - "));
			}
		}
		
		Clients.response(new AuResponse("loadBandboxData", this, 
				"{className: \"" + cName+ "\", id: \"" + getId() + "\", filter: \"" + sFilter + "\", " +
				" list: " + list.toString() +
				"}"));
	}
	
	@Override
	public void afterCompose() {

		Object ctrl = ComponentUtils.getWindowController(this);
		if(ctrl instanceof BaseSimpleController) {
			this.controller = (BaseSimpleController)ctrl;
		}
		
		if(ctrl != null) {
			init(ctrl);
		}
		
		// Key down event
		this.setCtrlKeys("#down");
		
		//add event listener
		this.addEventListener("onChanging", new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				getDataList(event);
			}
		});
		
//		this.setWidgetListener("onFocus", "this.select();");
		
		//client event listener
		this.setWidgetListener("onChanging", 
				"if(event.value.length < 3) {"
				+ "		event.stop();"
				+ "} else {"
				+ "		bc['"+ getId() + "'] = this;"
				+ "		onChangingBandbox('" + (lovMap != null ? lovMap.lovFQCN : getId()) 
				+ "', event.value, this, event);"
				+ "}"
		);
		
		//client event listener on key down
		this.setWidgetListener("onCtrlKey",
				"listFocus(event, this);"
		);
		
		this.listbox.setWidgetListener("onOK", 
				"putSelectedValue(this);"
		);
		
		this.listbox.setWidgetListener("onClick", 
				"putSelectedValue(this);"
		);
		
		this.listbox.setWidgetListener("onSelect", "putSelectedValue(this, true);");
		
	}

	@Override
	public String getValue() throws WrongValueException {
		if(selectedItem != null) {
			return selectedItem.getValue();
		}
		return super.getValue();
	}
	
	private void init(Object ctrl) {
		if(ctrl != null) {
			Field[] fields = ctrl.getClass().getDeclaredFields();
			for(Field f : fields) {
				if(f.isAnnotationPresent(ListOfValue.class)) {
					ListOfValue ann = f.getAnnotation(ListOfValue.class);
					String thisId = ann.componentId();
					if("".equals(thisId)) {
						thisId = f.getName();
					}
					if(thisId.equals(getId())) {
						try {
							Object o = ann.lovClass().newInstance();
							String[] className = o.getClass().getName().split("\\.");
							lovMap = new LOVParam( 
									className[className.length - 1],
									ann.valueField(),
									ann.labelField(),
									ann.separator() , 
									ann.appendNoneSelectedItem() , 
									ann.noneSelectedLabel()
								);
						} catch(Exception e) {
						}
					}
				} else if(f.isAnnotationPresent(Autowired.class)) {
					if(f.getType().equals(IGeneralPurposeDao.class)) {
						f.setAccessible(true);
						try {
							this.generalPurposeDao = (IGeneralPurposeDao)f.get(ctrl);
						} catch(Exception e) {
							
						}
					}
				}
			}
		}
	}
}
