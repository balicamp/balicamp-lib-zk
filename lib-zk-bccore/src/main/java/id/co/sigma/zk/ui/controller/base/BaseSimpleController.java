
package id.co.sigma.zk.ui.controller.base;

import id.co.sigma.common.data.CustomDataFormatter;
import id.co.sigma.common.data.lov.CommonLOV;
import id.co.sigma.common.data.lov.CommonLOVHeader;
import id.co.sigma.common.data.query.SimpleQueryFilter;
import id.co.sigma.common.data.query.SimpleQueryFilterOperator;
import id.co.sigma.common.data.query.SimpleSortArgument;
import id.co.sigma.common.security.domain.Branch;
import id.co.sigma.common.security.domain.User;
import id.co.sigma.common.server.dao.IGeneralPurposeDao;
import id.co.sigma.common.server.dao.util.ServerSideDateTimeParser;
import id.co.sigma.common.server.lov.ILOVProviderService;
import id.co.sigma.common.server.service.system.ICommonSystemService;
import id.co.sigma.common.server.util.ExtendedBeanUtils;
import id.co.sigma.common.util.json.SharedServerClientLogicManager;
import id.co.sigma.zk.service.IZKCommonService;
import id.co.sigma.zk.spring.security.SecurityUtil;
import id.co.sigma.zk.spring.security.model.UserData;
import id.co.sigma.zk.ui.annotations.ListOfValue;
import id.co.sigma.zk.ui.annotations.LoVDependency;
import id.co.sigma.zk.ui.annotations.LoVFlag;
import id.co.sigma.zk.ui.annotations.LoVSort;
import id.co.sigma.zk.ui.annotations.LookupEnabledControl;
import id.co.sigma.zk.ui.custom.component.ListOfValueComboitemRenderer;
import id.co.sigma.zk.ui.custom.component.ListOfValueItem;
import id.co.sigma.zk.ui.custom.component.ListOfValueModel;
import id.co.sigma.zk.ui.data.FormDataBinderUtil;
import id.co.sigma.zk.ui.lov.CommonLOVWithRenderer;
import id.co.sigma.zk.ui.lov.DefaultLOVRenderer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import org.zkoss.web.Attributes;
import org.zkoss.zk.au.AuResponse;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Script;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Window;

/**
 * base class untuk ZK MVC controller 
 * @author <a href='mailto:gede.sutarsa@gmail.com'>Gede Sutarsa</a>
 */
public abstract class BaseSimpleController extends SelectorComposer<Component>{
	
	private static final long serialVersionUID = 7154919543754238692L;
	
	private boolean springWired = false ; 
	
	private static final Logger logger = LoggerFactory.getLogger(BaseSimpleController.class); 
	
	private Map<Class<?>, CustomDataFormatter<CommonLOV>> indexedCustomRender = new HashMap<Class<?>, CustomDataFormatter<CommonLOV>>() ;
	
	private DefaultLOVRenderer defaultLOVRenderer = new DefaultLOVRenderer();
	
	@Autowired
	protected ILOVProviderService lovProviderService;
	
	@Autowired
	protected ICommonSystemService commonSystemService;
	
	@Autowired
	@Qualifier(value="zkCommonServiceBean")
	protected IZKCommonService zkCommonService ; 
	
	@Autowired
	@Qualifier("commonUiDateFormat")
	protected String commonDateFormat;
	
	@Autowired
	@Qualifier("commonUiConstraintDateFormat")
	protected String commonConstraintDateFormat;
	
	@Autowired
	@Qualifier("commonUiNumberWithDecimalFormat")
	protected String commonNumberWithDecimalFormat;
	
	@Autowired
	@Qualifier("commonUiNumberWithoutDecimalFormat")
	protected String commonNumberWithoutDecimalFormat;
	
	/**
	 * id space. ini untuk akses ke element dari componen
	 */
	private IdSpace idspace ;
	
	@Wire
	private Timer lovLoaderTimer;
	
	
	/**
	 * general purpose dao. untuk akses ke database yang tidak perlu spesifik
	 */
	@Autowired
	protected IGeneralPurposeDao generalPurposeDao ; 

	public BaseSimpleController() {
		super() ; 
		SharedServerClientLogicManager.getInstance().setDateTimeParser(ServerSideDateTimeParser.getInstance());
	}
	
	@Override
	public void doBeforeComposeChildren(Component comp) throws Exception {
		
		super.doBeforeComposeChildren(comp);
		wireSpring();
		
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		registerComboScript();
//		fillLOVControls();
		idspace = comp.getSpaceOwner(); 
	}
	@Override
	public ComponentInfo doBeforeCompose(Page page, Component parent,
			ComponentInfo compInfo) {
		
		ComponentInfo retval =  super.doBeforeCompose(page, parent, compInfo);
		wireSpring();
		return retval ; 
	}
	
	private void wireSpring () {
		if ( springWired)
			return ; 
		try {
			SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
			springWired = true ; 
		} catch (Exception e) {
			springWired = false ;
			logger.error("gagal wire spring. error : " + e.getMessage() , e);
		}
	}
	
	
	/**
	 * language dari current user
	 */
	public String getLanguge() {
		if(getLocale() != null) {
			return getLocale().getLanguage().toLowerCase();
		} else {
			return "in";
		}
	}
	
	/**
	 * get country from locale
	 * @return
	 */
	public String getCountryLocale() {
		if(getLocale() != null) {
			String cntry = getLocale().getCountry().toLowerCase();			
			return ("".equals(cntry) ? "id" : cntry);
		} else {
			return "id";
		}
	}
	
	public Locale getLocale() {
		Locale locale = (Locale)Executions.getCurrent().getSession().getAttribute(Attributes.PREFERRED_LOCALE);
		if(locale == null) {
			locale = new Locale("ID", "in"); // default country in = Indonesia, language ID = Bahasa
		}
		return locale;
	}
	
	/**
	 * mengisi semua control dengan annotation {@link LookupEnabledControl}
	 */
	@SuppressWarnings("unchecked")
	protected void fillLOVControls () {
		Field[] flds =  this.getClass().getDeclaredFields();
		 
		Map<String, List<Field>> indexedParamField = new HashMap<String, List<Field>>();
		
		Map<Field, Class<?> > customRenderer = new HashMap<Field, Class<?>>() ; 
		for ( Field scn : flds){
			if ( !scn.isAnnotationPresent(LookupEnabledControl.class))
				continue ;
			LookupEnabledControl ann = scn.getAnnotation(LookupEnabledControl.class); 
			String paramId =  ann.parameterId();
			if (! DefaultLOVRenderer.class.getName().equals(  ann.lovDataRenderer().getName())){
				customRenderer.put(scn, ann.lovDataRenderer()); 
			}
			if (! indexedParamField.containsKey(paramId))
				indexedParamField.put(paramId, new ArrayList<Field>()); 
			indexedParamField.get(paramId).add(scn); 
		}
		if ( indexedParamField.isEmpty())
			return ; 
		Map<String, CommonLOVHeader> vals =  lovProviderService.getLOVAsMap(getCountryLocale(), indexedParamField.keySet());
		if ( vals== null)
			return  ; 
		for ( String paramId : vals.keySet()) {
			CommonLOVHeader lov = vals.get(paramId);
			List<Field> fs =  indexedParamField.get(lov.getLovId());
			
			if (! indexedParamField.containsKey(lov.getLovId()) || fs == null ||fs.isEmpty())
				continue ; 
			for ( Field fld : fs ) {
				if (! customRenderer.containsKey(fld)){
					try {
						fillLooukup(fld, lov, this.defaultLOVRenderer);
					} catch (Exception e) {
						logger.error("gagal render combo :" + lov.getLovId() + ",error : " + e.getMessage() , e);
					}
				}
				else {
					Class<?> r = customRenderer.get(fld); 
					if (! indexedCustomRender.containsKey(r)){
						CustomDataFormatter<CommonLOV> rnd = (CustomDataFormatter<CommonLOV>)BeanUtils.instantiate(r); 
						indexedCustomRender.put(r, rnd); 
					}	
					try {
						fillLooukup(fld, lov, indexedCustomRender.get(r));
					} catch (Exception e) {
						logger.error("gagal render combo :" + lov.getLovId() + ",error : " + e.getMessage() , e);
					}
					
				}
			}
			
			
		}
	}
	
	
	
	
	
	/**
	 * render lookup
	 */
	protected void fillLooukup (Field field, CommonLOVHeader header  , CustomDataFormatter<CommonLOV> dataRenderer ) throws Exception{
		field.setAccessible(true);
		Object swp =  field.get(this);
		if ( swp instanceof Combobox) {
			Combobox cmb = (Combobox)swp ; 
			ListModelList<CommonLOVWithRenderer> models = new ListModelList<CommonLOVWithRenderer>();
			String label = "";
			if ( header.getDetails()!= null ) {
				ArrayList<CommonLOVWithRenderer> contents = new ArrayList<CommonLOVWithRenderer>() ;
				String val = getSelectedLOV(cmb);
				for ( CommonLOV scn : header.getDetails() ) {
					CommonLOVWithRenderer w = new CommonLOVWithRenderer(scn, dataRenderer) ; 
					contents.add(w) ; 
					if(val != null) {
						if(scn.getDataValue().equals(val)) {
							label = scn.getLabel();
						}
					}
				}
				models.addAll(contents); 
			}
			cmb.setModel(models);
			cmb.invalidate();
			if(!("".equals(label))) {
				cmb.setValue(label);
			}
		}
	}
	
	/**
	 * get selected combobox value
	 * @return
	 */
	protected String getSelectedLOV(Combobox cmb) {
		return null;
	}
	
	
	/**
	 * bind data dengan XML tag dan annotation
	 */
	protected void bindValueFromControl (Object targetToBindData) throws Exception {
		// bind dengan xml param
		FormDataBinderUtil.getInstance().bindDataFromControl(targetToBindData,idspace);
		FormDataBinderUtil.getInstance().bindDataFromControl(targetToBindData  , this); 
		
	}
	
	/**
	 * ini untuk keperluan penambahan listener ke component tertentu
	 * override method ini jika diperlukan
	 */
	public void setListenerForSpecificComponent(Component[] comps){
	    throw new RuntimeException("Method Not Suppport");
	}

	public String getCommonDateFormat() {
		return commonDateFormat;
	}
	
	public String getCommonConstraintDateFormat() {
		return commonConstraintDateFormat;
	}
	
	public String getCommonNumberWithDecimalFormat() {
	    return commonNumberWithDecimalFormat;
	}
	
	public String getCommonNumberWithoutDecimalFormat() {
	    return commonNumberWithoutDecimalFormat;
	}
	
	/**
	 * get authenticate user
	 * @return
	 */
	public User getAuthenticateUser() {
		UserData authUser = SecurityUtil.getUser(); 
		return (authUser == null ? null : authUser.getApplicationUser());
	}
	
	/**
	 * get default branch from authenticate user
	 * @return
	 */
	public Branch getDefaultBranch() {
		User authUser = getAuthenticateUser();
		return (authUser == null ? null : authUser.getBranch());
	}
	
	/**
	 * register combo data untuk client cache
	 */
	protected void registerComboScript() {
		Component thisComp = getSelf();
		if(thisComp instanceof Window) {
			Script script = new Script("cWindow = this.parent; \n"
					+ "zk.afterMount(\n"
					+ "function(){\n"
					+ createComboScript()
					+ "}\n"
					+ ");");
			script.setDefer(true);
			thisComp.appendChild(script);
		}
	}
	
	/**
	 * create script untuk combobox dan bandbox
	 * @return
	 */
	protected String createComboScript() {
		Field[] fields = getClass().getDeclaredFields();
		
		StringBuffer script = new StringBuffer();
		
		String lastCombo = "";
		
		for(Field f : fields) {
			if(f.isAnnotationPresent(ListOfValue.class)) {
				final ListOfValue ann = f.getAnnotation(ListOfValue.class);
				
				String cId = ann.componentId();
				
				if("".equals(cId)) {
					cId = f.getName();
				}
				
				final Component comp = getSelf().getFellowIfAny(cId);
				
				try {
					
					Object o = ann.lovClass().newInstance();
					String[] className = o.getClass().getName().split("\\.");
					final String dbName = className[className.length - 1];
					
					if(comp instanceof Combobox) {

						comp.setWidgetListener("onSelect",
							"try {"
							+ "		if(event.data.reference.value != undefined) {"
							+ "			this.smartUpdate('value', event.data.reference.value);"
							+ "		}"
							+ "} catch (e){}"
						);
						
						if(ann.dependency() == null || ann.dependency().length == 0) {
							
							List<ListOfValueItem> list = loadListOfValueItems(dbName, "", ann);
							String defaultVal = "";
							try {
								defaultVal = ((Combobox)comp).getValue();
							} catch (Exception e) {}
							((Combobox)comp).setItemRenderer(new ListOfValueComboitemRenderer(defaultVal));
							((Combobox)comp).setModel(new ListOfValueModel(list));
							
						} else {
							
							List<String> vals = new ArrayList<String>();
							for(LoVDependency dpd: ann.dependency())  {
								if(dpd.isFilter()) {
									String val = null;
									try {
										val = ((Combobox) getSelf()
												.getFellowIfAny(dpd.comboId()))
												.getValue();
									} catch (Exception e) {
										((Combobox) getSelf()
												.getFellowIfAny(dpd.comboId())).clearErrorMessage();
									}
									if(val != null && val.trim().length() > 0) {
										vals.add(val);
									}
								} else {
									vals.add("-1");
								}
							}
							
							if(!vals.isEmpty()) {
								String[] svals = new String[vals.size()];
								for(int i=0;i<vals.size();i++) {
									svals[i] = vals.get(i);
								}
								List<ListOfValueItem> list = loadListOfValueItems(dbName, "", ann, svals);
								String defaultVal = "";
								try {
									defaultVal = ((Combobox)comp).getValue();
								} catch (Exception e) {}
								((Combobox)comp).setItemRenderer(new ListOfValueComboitemRenderer(defaultVal));
								((Combobox)comp).setModel(new ListOfValueModel(list));
							}
							
							for(LoVDependency dpd: ann.dependency())  {
								
								Component cdep = getSelf().getFellowIfAny(dpd.comboId());
								
								cdep.addEventListener("onSelect", new EventListener<Event>() {
									@Override
									public void onEvent(Event event)
											throws Exception {
										
										List<Object> vals = new ArrayList<Object>();
										vals.add(((Combobox)event.getTarget()).getSelectedItem().getValue());
										for(LoVDependency dpd: ann.dependency())  {
											if(!dpd.comboId().equals(event.getTarget().getId())) {
												if(dpd.isFilter()) {
													Combobox dpc = (Combobox)getSelf().getFellowIfAny(dpd.comboId());
													Object val = null;
													if(dpc.getSelectedIndex() > -1) {
														val = dpc.getSelectedItem().getValue();
													} else {
														val = "-1";
													}
													if(val != null) {
														if((val instanceof String) && (val.toString().trim().length() > 0)) {
															vals.add(val);
														} else {
															vals.add(val);
														}
													}
												} else {
													vals.add("-1");
												}
											}
										}
										
										List<ListOfValueItem> list = loadListOfValueItems(dbName, "", ann, vals.toArray(new String[vals.size()]));
										String defaultVal = "";
										try {
//											defaultVal = ((Combobox)comp).getValue();
											((Combobox)comp).setRawValue(null);
										} catch (Exception e) {}
										((Combobox)comp).setItemRenderer(new ListOfValueComboitemRenderer(defaultVal));
										((Combobox)comp).setModel(new ListOfValueModel(list));
									}
								});
							}
						}

//						lastCombo = cId;
						
//						script.append("loadCombo(").append("cWindow.$f('").append(comp.getId()).append("'),'")
//						.append(dbName).append("',true,").append(ann.onDemand()).append(");\n");
					
						if(ann.onDemand()) {
							comp.setWidgetListener("onChanging", 
								"onChangingCombobox('" + dbName +"',this,event);"
							);
						} else {
							comp.setWidgetListener("onChanging", "event.stop({au:true});");
						}
					
						
//						comp.addEventListener("onFill", new EventListener<Event>() {
//							@Override
//							public void onEvent(Event event) throws Exception {
//								loadComboboxData(dbName, event, ann);							
//							}
//						});
//						
//						comp.addEventListener("onChanging", new EventListener<Event>() {
//							@Override
//							public void onEvent(Event event) throws Exception {
//								loadComboboxData(dbName, event, ann);								
//							}
//							
//						});
						
						comp.setAttribute("org.zkoss.zk.ui.updateByClient", true);
							
					} else if(comp instanceof Bandbox) {
						
						lastCombo = cId;
						
						((Bandbox)comp).setCtrlKeys("#down");

						//client event listener on key down
						comp.setWidgetListener("onCtrlKey",
								"listFocus(event, this);"
						);
						
						script.append("loadCombo(").append("cWindow.$f('").append(comp.getId()).append("'),'")
							.append(dbName).append("',false,").append(ann.onDemand()).append(");\n");
						
						// bandpopup > listbox
						Component listbox = comp.getFirstChild().getFirstChild();
						
						listbox.setWidgetListener("onOK", 
								"putSelectedValue(this);"
						);
						
						listbox.setWidgetListener("onClick", 
								"putSelectedValue(this);"
						);
						
						listbox.setWidgetListener("onSelect", "putSelectedValue(this, true);");
						
						if(ann.onDemand()) {
							comp.setWidgetListener("onChanging", 
									"onChangingBandbox('" + dbName +"', event.value, this, event);"
								);
						} else {
							comp.setWidgetListener("onChanging", "event.stop({au:true});");
						}

						comp.addEventListener("onFill", new EventListener<Event>() {
							@Override
							public void onEvent(Event event) throws Exception {
								loadBandboxData(dbName, event, ann);							
							}
						});
							
						comp.addEventListener("onChanging", new EventListener<Event>() {
							@Override
							public void onEvent(Event event) throws Exception {
								loadBandboxData(dbName, event, ann);								
							}
							
						});

						comp.setAttribute("org.zkoss.zk.ui.updateByClient", true);
					}
				} catch(Exception e) {
					
				}
			} else if(f.isAnnotationPresent(LookupEnabledControl.class)) {
				final LookupEnabledControl ann = f.getAnnotation(LookupEnabledControl.class);
				Component comp = getSelf().getFellowIfAny(f.getName());
				if(comp != null) {
					
					lastCombo = f.getName();
					
					String dbName = ann.parameterId();
					
					script.append("loadLOVCombo(").append("cWindow.$f('").append(comp.getId()).append("'),'").append(dbName).append("');\n");
					
					comp.addEventListener("onFill", new EventListener<Event>() {

						@Override
						public void onEvent(Event event) throws Exception {
							loadLOVComboboxData(event, ann);							
						}
					});
					
					comp.setWidgetListener("onSelect",
							"try {"
							+ "		this.smartUpdate('value', event.data.reference.value);"
							+ "} catch (e){}"
						);
					
					comp.setAttribute("org.zkoss.zk.ui.updateByClient", true);
				}
			}
		}
		if(!"".equals(lastCombo)) {
			script.insert(0, "zAu.send(new zk.Event(cWindow,'onLoadCombodata',{comboId: '" + lastCombo + "'},{toServer:true}));\n");
			script.insert(0, "cWindow.lcmb = cWindow.$f('" + lastCombo + "');\n");
		}
		return script.toString();
	}

	private final void loadBandboxData(final String dbName, final Event event, final ListOfValue annLOV) {
		loadComboData("loadBandboxData", dbName, event, annLOV);
	}
	
	@SuppressWarnings("unused")
	private final void loadComboboxData(final String dbName, final Event event, final ListOfValue annLOV) {
		loadComboData("loadComboboxData", dbName, event, annLOV);
	}
	
	@SuppressWarnings("unchecked")
	private final void loadComboData(final String func, final String dbName, final Event event, final ListOfValue annLOV) {
		String sFilter = "";
		boolean isInit = false;
		if(event instanceof InputEvent) {
			InputEvent iEvent = (InputEvent)event;
			sFilter = iEvent.getValue();
		} else {
			Object data = event.getData();
			Map<String,?> map = (Map<String,?>) data;
			sFilter = (String)map.get("value");		
			isInit = Boolean.valueOf(map.get("isInit").toString());
		}

		List<ListOfValueItem> list = loadListOfValueItems(dbName, sFilter, annLOV);
	
		String id = event.getTarget().getId(); 
		
		Clients.response(new AuResponse(func, event.getTarget(), 
				"{className: \"" + dbName + "\", id: \"" + id + "\", filter: \"" + sFilter + "\", isInit: " + isInit + ", " +
				" list: " + list.toString() +
				"}"));
		
	}
	
	private final List<ListOfValueItem> loadListOfValueItems(final String dbName, final String sFilter, final ListOfValue annLOV, String... dependencyFilter) {
		
		List<ListOfValueItem> list = new ArrayList<ListOfValueItem>();
		List<SimpleQueryFilter> filters = null;
		if(annLOV.onDemand()) {
			filters = new ArrayList<SimpleQueryFilter>();
			filters.add(new SimpleQueryFilter("".equals(annLOV.codeField()) ? annLOV.valueField() : annLOV.codeField(), 
					SimpleQueryFilterOperator.likeBothSide, sFilter));
			filters.add(new SimpleQueryFilter(annLOV.labelField(), SimpleQueryFilterOperator.likeBothSide, sFilter));		
		}
		
		if(annLOV.filterFlags().length > 0) {
			if(filters == null) filters = new ArrayList<SimpleQueryFilter>();
			for(LoVFlag flag : annLOV.filterFlags()) {
				Constructor<?>[] cs = flag.type().getConstructors();
				String dtType = String.class.getName();
				if(cs != null && cs.length > 0) {
					dtType = cs[0].getName();
				}
				SimpleQueryFilter filterFlag = new SimpleQueryFilter();
				filterFlag.setField(flag.field());
				filterFlag.setFilter(flag.value());
				filterFlag.setFilterTypeClass(dtType);
				filterFlag.setOperator(flag.operator());
				filters.add(filterFlag);
			}
		}
		
		if(annLOV.dependency() != null && annLOV.dependency().length > 0) {
			int i = 0;
			if(filters == null) filters = new ArrayList<SimpleQueryFilter>();
			for(LoVDependency dp : annLOV.dependency()) {
				Constructor<?>[] c = dp.dataType().getConstructors();
				String dType = String.class.getName();
				if(c != null && c.length > 0) {
					dType = c[0].getName();
				}
				if(dp.isFilter()) {
					SimpleQueryFilter filterFlag = new SimpleQueryFilter();
					filterFlag.setField(dp.field());
					filterFlag.setFilter(dependencyFilter[i++]);
					filterFlag.setFilterTypeClass(dType);
					filterFlag.setOperator(SimpleQueryFilterOperator.equal);
					filters.add(filterFlag);
				}
			}
		}
		
		SimpleSortArgument[] sortArgs = null;
		if(annLOV.sorts().length > 0) {
			sortArgs = new SimpleSortArgument[annLOV.sorts().length];
			int i = 0;
			for(LoVSort s : annLOV.sorts()) {
				sortArgs[i++] = new SimpleSortArgument(s.field(), s.ascending());
			}
		}
		
		try {
			List<?> lov = generalPurposeDao.list(dbName + " lov", "lov", 
					(filters != null ? (SimpleQueryFilter[])filters.toArray(new SimpleQueryFilter[filters.size()]) : null), 
					sortArgs);
			if(lov != null) {
				for(Object o : lov) {
					if(!("".equals(annLOV.codeField()))) {
						String code = String.valueOf(ExtendedBeanUtils.getInstance().getProperty(o, annLOV.codeField()));
						String value = String.valueOf(ExtendedBeanUtils.getInstance().getProperty(o, annLOV.valueField()));
						String label = String.valueOf(ExtendedBeanUtils.getInstance().getProperty(o, annLOV.labelField()));
						list.add(new ListOfValueItem(code, value, label, annLOV.separator()));
					} else {
						String value = String.valueOf(ExtendedBeanUtils.getInstance().getProperty(o, annLOV.valueField()));
						String label = String.valueOf(ExtendedBeanUtils.getInstance().getProperty(o, annLOV.labelField()));
						list.add(new ListOfValueItem(value, label, annLOV.separator()));
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return list;
		
	}
	
	@SuppressWarnings("unchecked")
	private final void loadLOVComboboxData(final Event event, final LookupEnabledControl lec) {
		List<ListOfValueItem> list = new ArrayList<ListOfValueItem>();
		String sFilter = "";
		Object data = event.getData();
		if(data instanceof Map) {
			Map<String,?> map = (Map<String,?>) data;
			String headerid = (String)map.get("headerId");
			logger.info("Load lookup data...: " + headerid);
			if(!(headerid == null || "".equals(headerid))) {
				
				List<String> headers = new ArrayList<String>();
				headers.add(headerid);
				
				Map<String, CommonLOVHeader> vals =  lovProviderService.getLOVAsMap(getCountryLocale(), headers);
				
				if(!(vals == null || vals.isEmpty())) {
				
					List<CommonLOV> lov = vals.get(headerid).getDetails();
					
					for(CommonLOV val : lov) {					
						list.add(new ListOfValueItem(val.getDataValue(), val.getLabel(), lec.separator()));
					}
				
				}
				
				String id = event.getTarget().getId();
				
				Clients.response(new AuResponse("loadComboboxData", event.getTarget(), 
						"{className: \"" + headerid + "\", id: \"" + id + "\", " + 
						"filter: \"" + sFilter + "\", " +
						"isLov: true, " +
						"list: " + list.toString() +
						"}"));
			}
		}
	}
	
}
