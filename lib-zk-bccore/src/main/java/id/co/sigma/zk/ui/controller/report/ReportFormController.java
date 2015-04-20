package id.co.sigma.zk.ui.controller.report;

import id.co.sigma.common.data.lov.CommonLOV;
import id.co.sigma.common.data.lov.CommonLOVHeader;
import id.co.sigma.common.data.query.SimpleQueryFilter;
import id.co.sigma.common.data.query.SimpleQueryFilterOperator;
import id.co.sigma.common.data.query.SimpleSortArgument;
import id.co.sigma.common.report.domain.RptDocParam;
import id.co.sigma.common.report.domain.RptDocument;
import id.co.sigma.common.server.dao.util.ServerSideWrappedJSONParser;
import id.co.sigma.common.server.util.ExtendedBeanUtils;
import id.co.sigma.common.util.json.ParsedJSONArrayContainer;
import id.co.sigma.common.util.json.ParsedJSONContainer;
import id.co.sigma.zk.ui.controller.base.BaseSimpleController;
import id.co.sigma.zk.ui.custom.component.ListOfValueComboitemRenderer;
import id.co.sigma.zk.ui.custom.component.ListOfValueItem;
import id.co.sigma.zk.ui.custom.component.ListOfValueModel;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.ConfigurableEnvironment;
import org.zkoss.util.media.AMedia;
import org.zkoss.zk.au.AuResponse;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ComboitemRenderer;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Panelchildren;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Script;
import org.zkoss.zul.SimpleConstraint;
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
	
	@Autowired
	private ConfigurableEnvironment env;
	
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
	
	private List<ReportParam> reportParams = new ArrayList<ReportParam>();
	private Map<String, ComboComponent> lovCombos = new HashMap<String, ComboComponent>();

	@Listen("onClick = #btnPrintPdf")
	public void printPDF() {
		
		try {
			
			@SuppressWarnings("unused")
			String user = env.getProperty("jasper.report.user");
			
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
			
		} catch(WrongValuesException | WrongValueException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Listen("onClick = #btnDownloadXLS")
	public void downloadXLS() {
		
		try {
			
			Locale locale = getLocale();
			
			String reportUnit = URLEncoder.encode(this.reportUnit,"UTF-8");
			String reportFolder= URLEncoder.encode(this.reportFolder,"UTF-8");
			String reportUrl = rptServerURL + "&ParentFolderUri=" + reportFolder + "&reportUnit=" + reportUnit 
					+ "&j_username=" + rptUserName + "&j_password=" + rptPassword 
					+ "&output=xls&decoration=no&userLocale=" + locale.getLanguage() + "_" + locale.getCountry();
			
			reportUrl = reportUrl + createReportParams();
			
			URL url = new URL(reportUrl);
			
			BufferedInputStream bis = new BufferedInputStream(url.openStream());
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
			byte[] ba1 = new byte[1024];
            
		    int baLength;
            
            while ((baLength = bis.read(ba1)) != -1) {
                baos.write(ba1, 0, baLength);
            } 			    
		    
            String[] rptUnits = this.reportUnit.split("/");
            
            AMedia media  = new AMedia(rptUnits[rptUnits.length-1]+ "_" + Calendar.getInstance().getTimeInMillis() + ".xls", "xls", "application/xls", baos.toByteArray());
            
            Filedownload.save(media);
			
		} catch(WrongValuesException | WrongValueException e) {
			throw e;
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

			Button printXLS = new Button("Download XLS");
			printXLS.setId("btnDownloadXLS");
			buttonToolbar.appendChild(printXLS);
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
			
			int tabIndex = 1;
			
			for(final RptDocParam param : rptParams) {
				Row row = new Row();
				
				SimpleConstraint cons = null;
				
				if(param.getRequired() == 1) {
					cons = new SimpleConstraint("no empty : " + param.getInvalidErrMessage());
					Label label = new Label(param.getParamLabel());
					label.setTooltip(param.getDescription());
					Label reqlabel = new Label("*");
					reqlabel.setClass("sign-mandatory");
					Hbox hbox = new Hbox();
					hbox.appendChild(label);
					hbox.appendChild(reqlabel);
					row.appendChild(hbox);
					row.setStyle("background: #FFFFFF;");
				} else {				
					Label label = new Label(param.getParamLabel());
					label.setTooltip(param.getDescription());
					row.appendChild(label);
					row.setStyle("background: #FFFFFF;");
				}
				
				Component inp = null;
				
				if("Textbox".equals(param.getParamType())) {
					inp = new Textbox();
					inp.setId(param.getParamCode());
					((Textbox)inp).setConstraint(cons);
				} else if("Intbox".equals(param.getParamType())) {
					inp = new Intbox();
					inp.setId(param.getParamCode());
					((Intbox)inp).setStyle("text-align: right;");
					((Intbox)inp).setConstraint(cons);
				} else if("Decimalbox".equals(param.getParamType())) {
					inp = new Decimalbox();
					inp.setId(param.getParamCode());
					((Decimalbox)inp).setStyle("text-align: right;");
					((Decimalbox)inp).setConstraint(cons);
				} else if("Datebox".equals(param.getParamType())) {
					inp = new Datebox();
					inp.setId(param.getParamCode());
					((Datebox)inp).setConstraint(cons);
					((Datebox)inp).setFormat("dd-MMM-yyyy");
				} else if("LookupCombobox".equals(param.getParamType())) {					
					inp = new Combobox();
					inp.setId(param.getParamCode());
					((Combobox)inp).setConstraint(cons);
					
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
					((Combobox)inp).setConstraint(cons);
					if(param.getLovClass().startsWith("BankAccount")){
						((Combobox)inp).setWidth("450px");
					} else {
						((Combobox)inp).setWidth("300px");
					}
					
					ComboComponent cc = new ComboComponent();
					cc.combobox = (Combobox)inp;
					
					lovCombos.put(param.getParamCode(), cc);
					
					String defVal = param.getDefaultValue();
					
					if(defVal != null && defVal.trim().length() > 0) {
						if("#userBranch".equals(defVal.trim())) {
							defVal = getDefaultBranch().getId().toString();
						}
					} else defVal = null;

					cc.defaultValue = defVal;
					
					final Component comp = inp;
					
					if(param.getLovParentId() == null || "".equals(param.getLovParentId().trim())) {
						List<ListOfValueItem> list = loadListOfValueItems(param);
						String defaultVal = defVal;
						try {
							defaultVal = ((Combobox)inp).getValue();
							if(defaultVal == null || defaultVal.trim().length() == 0) {
								defaultVal = defVal;
							}
						} catch (Exception e) {}
						((Combobox)inp).setItemRenderer(new ListOfValueComboitemRenderer(defaultVal));
						((Combobox)inp).setModel(new ListOfValueModel(list));
						
					} else {

						List<String> vals = new ArrayList<String>();
						
						try {
							final ParsedJSONArrayContainer dependencies = ServerSideWrappedJSONParser.getInstance().parseJSONArray(param.getLovParentId());
							for(int i = 0; i < dependencies.length(); i++) {
								ParsedJSONContainer con = dependencies.get(i);
								String cmbId = con.getAsString("comboId");
								String val = null;
								ComboComponent c = lovCombos.get(cmbId); 
								try {
									if(c.combobox.getSelectedIndex() > -1) {
										val = c.combobox.getSelectedItem().getValue();
									} else {
										val = "-1";
									}
								} catch (Exception e) {
									c.combobox.clearErrorMessage();
									val = c.defaultValue == null ? "-1" : c.defaultValue;
								}
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
								String defaultVal = defVal;
								try {
									defaultVal = ((Combobox)inp).getValue();
									if(defaultVal == null || defaultVal.trim().length() == 0) {
										defaultVal = defVal;
									}
								} catch (Exception e) {}
								((Combobox)inp).setItemRenderer(new ListOfValueComboitemRenderer(defaultVal));
								((Combobox)inp).setModel(new ListOfValueModel(list));
							}
							
							for(int i = 0; i < dependencies.length(); i++) {
								ParsedJSONContainer con = dependencies.get(i);
								String cmbId = con.getAsString("comboId");
								Component cdep = lovCombos.get(cmbId).combobox;
								
								cdep.addEventListener("onSelect", new EventListener<Event>() {

									@Override
									public void onEvent(Event event)
											throws Exception {

										List<Object> vals = new ArrayList<Object>();
										vals.add(((Combobox)event.getTarget()).getSelectedItem().getValue());
										
										for(int i = 0; i < dependencies.length(); i++) {
											ParsedJSONContainer con = dependencies.get(i);
											String cmbId = con.getAsString("comboId");
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
										try {
											((Combobox)comp).clearErrorMessage();
											((Combobox)comp).setRawValue(null); //reset value selection
										} catch (Exception e) {}
										((Combobox)comp).setItemRenderer(new ListOfValueComboitemRenderer(""));
										((Combobox)comp).setModel(new ListOfValueModel(list));
										
									}
									
								});
							}
						} catch (Exception e1) {
						}
					}
					
				} else if("Combobox".equals(param.getParamType())) {
					inp = new Combobox();
					inp.setId(param.getParamCode());
					((Combobox)inp).setConstraint(cons);
					
					try {
						ParsedJSONArrayContainer items = ServerSideWrappedJSONParser
								.getInstance().parseJSONArray(param.getStaticData());
						List<ParsedJSONContainer> litems = new ArrayList<ParsedJSONContainer>();
						for(int i = 0; i < items.length(); i++) {
							ParsedJSONContainer con = items.get(i);
							litems.add(con);							
						}
						ListModelList<ParsedJSONContainer> model = new ListModelList<ParsedJSONContainer>(litems);
						((Combobox)inp).setItemRenderer(new ParamComboItemRenderer());
						((Combobox)inp).setModel(model);

					} catch (Exception e) {
						logger.error("gagal parsing JSON String: " + e.getMessage());
					}
					
				} else if("MonthCombobox".equals(param.getParamType())) {
					inp = new Combobox();
					inp.setId(param.getParamCode());
					((Combobox)inp).setConstraint(cons);
					
					List<ListOfValueItem> list = monthListOfValueItems();
					String defaultVal = "1";
					try {
						defaultVal = String.valueOf(Calendar.getInstance().get(Calendar.MONTH)+1);
					} catch (Exception e) {
						defaultVal = "1";
					}
					((Combobox)inp).setItemRenderer(new ListOfValueComboitemRenderer(defaultVal));
					((Combobox)inp).setModel(new ListOfValueModel(list));
				} else if("MinMaxCombobox".equals(param.getParamType())) {
					inp = new Combobox();
					inp.setId(param.getParamCode());
					((Combobox)inp).setConstraint(cons);
					
					String parMinVal =  param.getMinValue();
					String parMaxVal =  param.getMaxValue();
					
					String[] minVal = param.getMinValue().split("[\\-\\+]+");
					String[] maxVal = param.getMaxValue().split("[\\-\\+]+");
					
					int intMinVal = 0;
					int intMaxVal = 0;
					
					String defVal = param.getDefaultValue();
					
					if(defVal != null && defVal.trim().length() > 0) {
						if("#currentYear".equals(defVal.trim())) {
							defVal = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
						}
					} else defVal = null;
					
					if(minVal[0].startsWith("#currentYear")) {
						intMinVal = Calendar.getInstance().get(Calendar.YEAR);
						if(minVal.length == 2) {
							int intVal = Integer.parseInt(minVal[1].trim());
							if(parMinVal.contains("-")) {
								intMinVal = intMinVal - intVal;
							} else if(parMinVal.contains("+")) {
								intMinVal = intMinVal + intVal;
							}
						}
					} else {
						intMaxVal = Integer.parseInt(minVal[0].trim());
					}
					
					if(maxVal[0].startsWith("#currentYear")) {
						intMaxVal = Calendar.getInstance().get(Calendar.YEAR);
						if(maxVal.length == 2) {
							int intVal = Integer.parseInt(maxVal[1].trim());
							if(parMaxVal.contains("-")) {
								intMaxVal = intMaxVal - intVal;
							} else if(parMinVal.contains("+")) {
								intMaxVal = intMaxVal + intVal;
							}
						}
					} else {
						intMaxVal = Integer.parseInt(maxVal[0].trim());
					}
					
					List<ListOfValueItem> items = new ArrayList<ListOfValueItem>();
					
					if(intMinVal <= intMaxVal) {
						for(int i = intMinVal; i <= intMaxVal; i++) {
							ListOfValueItem item = new ListOfValueItem(i, i+"", "");
							items.add(item);
						}
					}
					String defaultVal = defVal;
					try {
						defaultVal = ((Combobox)inp).getValue();
						if(defaultVal == null || defaultVal.trim().length() == 0) {
							defaultVal = defVal;
						}
					} catch (Exception e) {}
//					((Combobox)inp).setItemRenderer(new ListOfValueComboitemRenderer(null));
					((Combobox)inp).setItemRenderer(new ListOfValueComboitemRenderer(defaultVal));
					((Combobox)inp).setModel(new ListOfValueModel(items));
				} else {
					int mPos = param.getParamType().lastIndexOf(".");
					String paramTypeClass = param.getParamType().substring(0, mPos);
					try {
						inp = (Component)Class.forName(paramTypeClass).newInstance();
						inp.setId(param.getParamCode());
						if(inp instanceof AfterCompose) {
							((AfterCompose)inp).afterCompose();
						}
					} catch (Exception e) {
					}
				}
				
				if(inp instanceof InputElement) {
					((InputElement)inp).setTabindex(tabIndex++);
				}
				
				row.appendChild(inp);
				
				rows.appendChild(row);				
				
				reportParams.add(new ReportParam(inp, param));
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
		for(ReportParam par : reportParams) {
			
			Object val = "";
			Component inp = par.rptInputParam;
			
			if(inp instanceof Combobox) {

				//trigger check for required field
				((Combobox)inp).getValue();
				
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
				boolean customComp = (par.rptDocParam.getParamType().indexOf(".") >= 0);
				if(!customComp) {
					if(!((InputElement)inp).isValid()) {
						SimpleConstraint cons = (SimpleConstraint)((InputElement)inp).getConstraint(); 
						throw new WrongValueException(inp, cons.getErrorMessage(inp));
					}
					val = ((InputElement)inp).getRawValue();
				} else {
					String[] classPathName = par.rptDocParam.getParamType().split("\\.");
					String valMethodName = classPathName[classPathName.length - 1];
					try {
						
						Method m = inp.getClass().getMethod(valMethodName);
						val = m.invoke(inp);
					} catch (Exception e) {
//						e.printStackTrace();
					}
				}
			}
			try {
				if(val != null) {
					String sVal = String.valueOf(val);
					if(val instanceof Date) {
						String dformat = par.rptDocParam.getDataFormat();
						if(dformat != null) {
							try {
								SimpleDateFormat sdf = new SimpleDateFormat(dformat);
								sVal = sdf.format((Date)val);
							} catch (Exception e) {
							}
						}
					}									
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
				
				Map<String, CommonLOVHeader> vals =  lovProviderService.getLOVAsMap(getCountryLocale(), headers);
				
				if(!(vals == null || vals.isEmpty())) {				
					List<CommonLOV> lov = vals.get(headerid).getDetails();
					
					for(CommonLOV val : lov) {					
						list.add(new ListOfValueItem(val.getDataValue(), val.getLabel(), param.getSeparator()));
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

	private final List<ListOfValueItem> loadListOfValueItems(final RptDocParam param, String... dependencyFilter) {
		
		List<ListOfValueItem> list = new ArrayList<ListOfValueItem>();
		List<SimpleQueryFilter> filters = null;
//		if(annLOV.onDemand()) {
//			filters = new ArrayList<SimpleQueryFilter>();
//			filters.add(new SimpleQueryFilter("".equals(annLOV.codeField()) ? annLOV.valueField() : annLOV.codeField(), 
//					SimpleQueryFilterOperator.likeBothSide, sFilter));
//			filters.add(new SimpleQueryFilter(annLOV.labelField(), SimpleQueryFilterOperator.likeBothSide, sFilter));		
//		}
		
		if(param.getLovFilters() != null && !("".equals(param.getLovFilters().trim()))) {
			
			try {
				
				ParsedJSONArrayContainer lovfilters = ServerSideWrappedJSONParser
						.getInstance().parseJSONArray(param.getLovFilters());
				
				if (filters == null)
					filters = new ArrayList<SimpleQueryFilter>();
				
				for (int i = 0; i < lovfilters.length(); i++) {
					ParsedJSONContainer con = lovfilters.get(i);
					String field = con.getAsString("field");
					String fType = con.getAsString("fType");
					String opr = con.getAsString("opr");
					String val = con.getAsString("val");
					SimpleQueryFilter filterFlag = new SimpleQueryFilter();
					filterFlag.setField(field);
					filterFlag.setFilter(val);
					filterFlag.setFilterTypeClass(fType);
					filterFlag.setOperator(SimpleQueryFilterOperator.valueOf(opr));
					filters.add(filterFlag);
				}
				
			} catch (Exception e) {
				logger.error("error parse JSON String" + e.getMessage());
			}
		}
		
		if(param.getLovParentId() != null && !("".equals(param.getLovParentId().trim()))) {			
			try {
				ParsedJSONArrayContainer dependencies = ServerSideWrappedJSONParser
						.getInstance().parseJSONArray(param.getLovParentId());
				if (filters == null)
					filters = new ArrayList<SimpleQueryFilter>();
				for (int i = 0; i < dependencies.length(); i++) {
					ParsedJSONContainer con = dependencies.get(i);
					String field = con.getAsString("fieldName");
					String dtype = con.getAsString("fType");
					if(!("-".equals(field))) {
						if (dependencyFilter != null && dependencyFilter.length > 0) {
							SimpleQueryFilter filterFlag = new SimpleQueryFilter();
							filterFlag.setField(field);
							filterFlag.setFilter(dependencyFilter[i]);
							filterFlag.setFilterTypeClass(dtype);
							filterFlag.setOperator(SimpleQueryFilterOperator.equal);
							filters.add(filterFlag);
						}
					}
				}
			} catch (Exception e) {
				logger.error("error parse JSON String" + e.getMessage());
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
	
	final class ParamComboItemRenderer implements ComboitemRenderer<ParsedJSONContainer> {

		@Override
		public void render(Comboitem item, ParsedJSONContainer data, int index)
				throws Exception {
			item.setLabel(data.getAsString("itemLabel"));
			item.setValue(data.getAsString("itemValue"));
		}
		
	}
	
	final class ComboComponent {
		Combobox combobox;
		String defaultValue;
	}
	
	final class ReportParam {
		Component rptInputParam;
		RptDocParam rptDocParam;
		
		public ReportParam(Component rptInputParam, RptDocParam rptDocParam) {
			super();
			this.rptInputParam = rptInputParam;
			this.rptDocParam = rptDocParam;
		}
		
	}
}
