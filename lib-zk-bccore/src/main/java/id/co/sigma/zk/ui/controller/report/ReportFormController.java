package id.co.sigma.zk.ui.controller.report;

import id.co.sigma.common.data.lov.CommonLOV;
import id.co.sigma.common.data.lov.CommonLOVHeader;
import id.co.sigma.common.data.query.SimpleQueryFilter;
import id.co.sigma.common.data.query.SimpleQueryFilterOperator;
import id.co.sigma.common.data.query.SimpleSortArgument;
import id.co.sigma.common.report.domain.RptDocParam;
import id.co.sigma.common.report.domain.RptDocument;
import id.co.sigma.common.server.util.ExtendedBeanUtils;
import id.co.sigma.zk.ui.controller.base.BaseSimpleController;
import id.co.sigma.zk.ui.custom.component.ListOfValueComboitemRenderer;
import id.co.sigma.zk.ui.custom.component.ListOfValueItem;
import id.co.sigma.zk.ui.custom.component.ListOfValueModel;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.zkoss.zk.au.AuResponse;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Panelchildren;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Script;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbar;
import org.zkoss.zul.Window;
import org.zkoss.zul.impl.InputElement;

public class ReportFormController extends BaseSimpleController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5141315517810906819L;
	
	private static final Logger logger = LoggerFactory.getLogger(ReportFormController.class);
	
	@Value("${jasper.report.server}")
	private String rptServerURL;
	
	@Value("${jasper.report.user}")
	private String rptUserName;
	
	@Value("${jasper.report.pass}")
	private String rptPassword;
	
	@Wire
	private Iframe reportFrame;
	
	@Wire
	private Toolbar buttonToolbar;
	
	@Wire
	private Panelchildren panelEditor;
	
	@Wire
	Label caption;
	
	private String reportFolder;
	
	private String reportUnit;
	
	private List<Component> reportParams = new ArrayList<Component>();
	private Map<String, Combobox> lovCombos = new HashMap<String, Combobox>();

	@Listen("onClick = #btnPrintPdf")
	public void printPDF() {
		
		try {
			
			Locale locale = getLocale();
			
			String reportUnit = URLEncoder.encode(this.reportUnit,"UTF-8");
			String reportFolder= URLEncoder.encode(this.reportFolder,"UTF-8");
			String reportUrl = rptServerURL + "&ParentFolderUri=" + reportFolder + "&reportUnit=" + reportUnit 
					+ "&j_username=" + rptUserName + "&j_password=" + rptPassword 
					+ "&output=pdf&decoration=no&userLocale=" + locale.getLanguage() + "_" + locale.getCountry();
			
			reportUrl = reportUrl + createReportParams();
			
			Map<String, String> args = new HashMap<String, String>();
			args.put("reportUrl", reportUrl);
			
			Window rptWindow = (Window) Executions.createComponents("~./zul/pages/report/ReportView.zul", null, args);
			rptWindow.doModal();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see id.co.sigma.zk.ui.controller.base.BaseSimpleController#doAfterCompose(org.zkoss.zk.ui.Component)
	 */
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		
		super.doAfterCompose(comp);
		
		String[] rpt = Executions.getCurrent().getParameterMap().get("reportUnit");
		
		if(rpt == null || rpt.length == 0) {
			throw new Exception("Report not defined");
		} 
		
		String rptName = rpt[0];
		
		List<RptDocument> rptDocs = generalPurposeDao.list(RptDocument.class, 
				new SimpleQueryFilter[]{
					new SimpleQueryFilter("name", SimpleQueryFilterOperator.equal, rptName)
				}, 
				null);
		
		if(rptDocs == null || rptDocs.isEmpty()) {
			throw new Exception("Report not defined");
		}
		
		RptDocument rptDoc = rptDocs.get(0);
		List<RptDocParam> rptParams = rptDoc.getRptDocParams();
		
		reportFolder = rptDoc.getTemplate();
		reportUnit = reportFolder + "/" + rptDoc.getName();
		
		if(caption != null) {
			caption.setValue(rptDoc.getDescription());
		}
		
		Grid g = createInputForm(rptParams);
		
		if(g != null) {
			panelEditor.appendChild(g);
		}

		if(buttonToolbar != null) {
			buttonToolbar.getChildren().clear();
			Button printPdf = new Button("Cetak PDF");
			printPdf.setId("btnPrintPdf");
			buttonToolbar.appendChild(printPdf);
		}
	}

	private Grid createInputForm(List<RptDocParam> rptParams) {
		if(rptParams == null || rptParams.isEmpty()) {
			return null;
		} else {
			Grid grid = new Grid();
			Columns hdr = new Columns();
			Column chdr = new Column();
			chdr.setAlign("left");
			chdr.setWidth("150px");
			hdr.appendChild(chdr);
			
			chdr = new Column();
			hdr.appendChild(chdr);
			
			grid.appendChild(hdr);
			
			Rows rows = new Rows();
			
			StringBuffer script = new StringBuffer();
			String lastCmp = null;
			
			for(final RptDocParam param : rptParams) {
				Row row = new Row();
				Label label = new Label(param.getParamLabel());
				label.setTooltip(param.getDescription());
				row.appendChild(label);
				
				Component inp = null;
				
				if("Textbox".equals(param.getParamType())) {
					inp = new Textbox();
					inp.setId(param.getParamCode());
				} else if("Intbox".equals(param.getParamType())) {
					inp = new Intbox();
					inp.setId(param.getParamCode());
					((Intbox)inp).setStyle("text-align: right;");
				} else if("Decimalbox".equals(param.getParamType())) {
					inp = new Decimalbox();
					inp.setId(param.getParamCode());
					((Decimalbox)inp).setStyle("text-align: right;");
				} else if("Datebox".equals(param.getParamType())) {
					inp = new Datebox();
					inp.setId(param.getParamCode());
				} else if("LookupCombobox".equals(param.getParamType())) {					
					inp = new Combobox();
					inp.setId(param.getParamCode());
					
					lastCmp = param.getParamCode();
					String dbName = param.getLovClass();
					
					script.append("loadLOVCombo(").append("cWindow.$f('").append(inp.getId()).append("'),'").append(dbName).append("');\n");
					
					inp.addEventListener("onFill", new EventListener<Event>() {

						@Override
						public void onEvent(Event event) throws Exception {
							loadLOVComboboxData(event, param);							
						}
						
					});
					
					inp.setWidgetListener("onSelect",
							"try {"
							+ "		this.smartUpdate('value', event.data.reference.value);"
							+ "} catch (e){}"
						);
					
					inp.setAttribute("org.zkoss.zk.ui.updateByClient", true);
					
					
				} else if("LoVCombobox".equals(param.getParamType())) {
					
					inp = new Combobox();
					inp.setId(param.getParamCode());
					
					lovCombos.put(param.getParamCode(), (Combobox)inp);
					
					final Component comp = inp;
					
					if(param.getLovParentId() == null || "".equals(param.getLovParentId().trim())) {
						List<ListOfValueItem> list = loadListOfValueItems(param);
						String defaultVal = "";
						try {
							defaultVal = ((Combobox)inp).getValue();
						} catch (Exception e) {}
						((Combobox)inp).setItemRenderer(new ListOfValueComboitemRenderer(defaultVal));
						((Combobox)inp).setModel(new ListOfValueModel(list));
						
					} else {

						List<String> vals = new ArrayList<String>();
						final String[] dependencies = param.getLovParentId().split("\\,");
						for(String dpd: dependencies)  {
							
							String cmbId = dpd.split("\\(")[0];
							
							String val = lovCombos.get(cmbId).getValue();
							if(val != null && val.trim().length() > 0) {
								vals.add(val);
							} else {
								vals.add("-1");
							}
						}
						
						if(!vals.isEmpty()) {
							String[] svals = new String[vals.size()];
							for(int i=0;i<vals.size();i++) {
								svals[i] = vals.get(i);
							}
							List<ListOfValueItem> list = loadListOfValueItems(param, svals);
							String defaultVal = "";
							try {
								defaultVal = ((Combobox)inp).getValue();
							} catch (Exception e) {}
							((Combobox)inp).setItemRenderer(new ListOfValueComboitemRenderer(defaultVal));
							((Combobox)inp).setModel(new ListOfValueModel(list));
						}
						
						for(String dpd: dependencies)  {
							
							String cmbId = dpd.split("\\(")[0];							
							Component cdep = lovCombos.get(cmbId);
							
							cdep.addEventListener("onSelect", new EventListener<Event>() {
								@Override
								public void onEvent(Event event)
										throws Exception {
									
									List<Object> vals = new ArrayList<Object>();
									vals.add(((Combobox)event.getTarget()).getSelectedItem().getValue());
									for(String dpd: dependencies)  {

										String cmbId = dpd.split("\\(")[0];							
										Combobox dpc = (Combobox)getSelf().getFellowIfAny(cmbId);
										
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
										
									}
									
									List<ListOfValueItem> list = loadListOfValueItems(param, vals.toArray(new String[vals.size()]));
									String defaultVal = "";
									try {
										defaultVal = ((Combobox)comp).getValue();
									} catch (Exception e) {}
									((Combobox)comp).setItemRenderer(new ListOfValueComboitemRenderer(defaultVal));
									((Combobox)comp).setModel(new ListOfValueModel(list));
									
								}
							});
							
						}
					}
					
				} else if("Combobox".equals(param.getParamType())) {
					inp = new Combobox();
					inp.setId(param.getParamCode());
				} else if("MonthCombobox".equals(param.getParamType())) {
					inp = new Combobox();
					inp.setId(param.getParamCode());
					
					List<ListOfValueItem> list = monthListOfValueItems();
					String defaultVal = "1";
					try {
						defaultVal = ((Combobox)inp).getValue();
					} catch (Exception e) {
						defaultVal = "1";
					}
					((Combobox)inp).setItemRenderer(new ListOfValueComboitemRenderer(defaultVal));
					((Combobox)inp).setModel(new ListOfValueModel(list));
				}
				
				row.appendChild(inp);
				
				rows.appendChild(row);
				
				reportParams.add(inp);
			}
			
			grid.appendChild(rows);
			
			if(lastCmp != null) {
				script.insert(0, "zAu.send(new zk.Event(cWindow,'onLoadCombodata',{comboId: '" + lastCmp + "'},{toServer:true}));\n");
				script.insert(0, "cWindow.lcmb = cWindow.$f('" + lastCmp + "');\n");
			}
			
			createClientScript(script.toString());
			
			return grid;
		}
	}
	
	private void createClientScript(String script) {
		Component thisComp = getSelf();
		if(thisComp instanceof Window) {
			Component lstChild = thisComp.getLastChild();
			if(lstChild instanceof Script) {
				((Script)lstChild).setContent("cWindow = this.parent; \n"
						+ "zk.afterMount(\n"
						+ "function(){\n"
						+ script
						+ "}\n"
						+ ");");
			}
		}
		
	}
	
	private String createReportParams() {
		StringBuffer sbuf = new StringBuffer();
		for(Component inp : reportParams) {
			Object val = "";			
			if(inp instanceof Combobox) {

				Object clientUpdate = inp.getAttribute("org.zkoss.zk.ui.updateByClient");
				boolean isUpdByClient = false;
				if(clientUpdate instanceof Boolean) {
					isUpdByClient = (Boolean)clientUpdate;
				}
				
				if(((Combobox)inp).getSelectedIndex() > -1) {
					if(isUpdByClient) {
						val = ((Combobox)inp).getValue();
					} else {
						val = ((Combobox)inp).getSelectedItem().getValue();
					}
				} else {
					if(isUpdByClient) {
						val = ((Combobox)inp).getValue();
					}
				}
				
			} else {
				val = ((InputElement)inp).getRawValue();
			}
			try {
				if(val != null) {
					String sVal = String.valueOf(val);				
					if(!("".equals(sVal.trim()))){
						sbuf.append("&").append(inp.getId()).append("=").append(URLEncoder.encode(sVal, "UTF-8"));
					}
				}
			} catch (UnsupportedEncodingException e) {
				//todo
			}
		}
		return sbuf.toString();
	}
	
	@SuppressWarnings("unchecked")
	private final void loadLOVComboboxData(final Event event, final RptDocParam param) {
		List<ListOfValueItem> list = new ArrayList<ListOfValueItem>();
		String sFilter = "";
		Object data = event.getData();
		if(data instanceof Map) {
			Map<String,?> map = (Map<String,?>) data;
			String headerid = (String)map.get("headerId");
			if(!(headerid == null || "".equals(headerid))) {
				
				List<String> headers = new ArrayList<String>();
				headers.add(headerid);
				
				Map<String, CommonLOVHeader> vals =  lovProviderService.getLOVAsMap(getLanguge(), headers);
				
				if(vals == null || vals.isEmpty()) return;
				
				List<CommonLOV> lov = vals.get(headerid).getDetails();
				
				for(CommonLOV val : lov) {					
					list.add(new ListOfValueItem(val.getDataValue(), val.getLabel(), param.getSeparator()));
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

	private final List<ListOfValueItem> loadListOfValueItems(final RptDocParam param, String... dependencyFilter) {
		
		List<ListOfValueItem> list = new ArrayList<ListOfValueItem>();
		List<SimpleQueryFilter> filters = null;
//		if(annLOV.onDemand()) {
//			filters = new ArrayList<SimpleQueryFilter>();
//			filters.add(new SimpleQueryFilter("".equals(annLOV.codeField()) ? annLOV.valueField() : annLOV.codeField(), 
//					SimpleQueryFilterOperator.likeBothSide, sFilter));
//			filters.add(new SimpleQueryFilter(annLOV.labelField(), SimpleQueryFilterOperator.likeBothSide, sFilter));		
//		}
		
//		if(annLOV.filterFlags().length > 0) {
//			if(filters == null) filters = new ArrayList<SimpleQueryFilter>();
//			for(LoVFlag flag : annLOV.filterFlags()) {
//				Constructor<?>[] cs = flag.type().getConstructors();
//				String dtType = String.class.getName();
//				if(cs != null && cs.length > 0) {
//					dtType = cs[0].getName();
//				}
//				SimpleQueryFilter filterFlag = new SimpleQueryFilter();
//				filterFlag.setField(flag.field());
//				filterFlag.setFilter(flag.value());
//				filterFlag.setFilterTypeClass(dtType);
//				filterFlag.setOperator(flag.operator());
//				filters.add(filterFlag);
//			}
//		}
		
		if(param.getLovParentId() != null && !("".equals(param.getLovParentId().trim()))) {
			int i = 0;
			if(filters == null) filters = new ArrayList<SimpleQueryFilter>();
			String[] dependencies = param.getLovParentId().split("\\,");			
			for(String dp : dependencies) {
				
				dp = dp.replace(")", "");
				
				String[] prn = dp.split("\\(");
				String[] fieldDes = prn[1].split("\\:");
				String field = fieldDes[0];
				String dtype = fieldDes[1];
				
				if(dependencyFilter != null && dependencyFilter.length > 0) {
					SimpleQueryFilter filterFlag = new SimpleQueryFilter();
					filterFlag.setField(field);
					filterFlag.setFilter(dependencyFilter[i++]);
					filterFlag.setFilterTypeClass(dtype);
					filterFlag.setOperator(SimpleQueryFilterOperator.equal);
					filters.add(filterFlag);
				}
			}
		}
		
		SimpleSortArgument[] sortArgs = null;
//		if(annLOV.sorts().length > 0) {
//			sortArgs = new SimpleSortArgument[annLOV.sorts().length];
//			int i = 0;
//			for(LoVSort s : annLOV.sorts()) {
//				sortArgs[i++] = new SimpleSortArgument(s.field(), s.ascending());
//			}
//		}
		
		try {
			
			String lovClass = param.getLovClass().replace(")", "");
			String[] lclazz = lovClass.split("\\(");
			String dbName = lclazz[0];
			String[] fields = lclazz[1].split("\\,");
			
			
			
			List<?> lov = generalPurposeDao.list(dbName + " lov", "lov", 
					(filters != null ? (SimpleQueryFilter[])filters.toArray(new SimpleQueryFilter[filters.size()]) : null), 
					sortArgs);
			if(lov != null) {
				for(Object o : lov) {
					if(fields.length == 3) {
						String value = String.valueOf(ExtendedBeanUtils.getInstance().getProperty(o, fields[0]));
						String code = String.valueOf(ExtendedBeanUtils.getInstance().getProperty(o, fields[1]));
						String label = String.valueOf(ExtendedBeanUtils.getInstance().getProperty(o, fields[2]));
						list.add(new ListOfValueItem(code, value, label, param.getSeparator()));
					} else {
						String value = String.valueOf(ExtendedBeanUtils.getInstance().getProperty(o, fields[0]));
						String label = String.valueOf(ExtendedBeanUtils.getInstance().getProperty(o, fields[1]));
						list.add(new ListOfValueItem(value, label, param.getSeparator()));
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return list;
		
	}
	
	private final List<ListOfValueItem> monthListOfValueItems() {
		List<ListOfValueItem> list = new ArrayList<ListOfValueItem>();
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, Calendar.JANUARY);
		SimpleDateFormat sdf = new SimpleDateFormat("MMMMM", getLocale());
		for(int m = 0; m < 12; m++) {
			ListOfValueItem month = new ListOfValueItem((cal.get(Calendar.MONTH)+1), sdf.format(cal.getTime()), "");
			list.add(month);
			cal.add(Calendar.MONTH, 1);
		}
		return list;
	}
	
}
