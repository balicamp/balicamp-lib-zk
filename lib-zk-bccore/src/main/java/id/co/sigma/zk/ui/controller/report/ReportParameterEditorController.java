package id.co.sigma.zk.ui.controller.report;

import id.co.sigma.common.report.domain.RptDocParam;
import id.co.sigma.common.security.domain.lov.LookupHeader;
import id.co.sigma.common.server.dao.util.ServerSideWrappedJSONParser;
import id.co.sigma.common.util.json.ParsedJSONArrayContainer;
import id.co.sigma.common.util.json.ParsedJSONContainer;
import id.co.sigma.zk.ui.controller.ZKEditorState;
import id.co.sigma.zk.ui.controller.base.BaseSimpleNoDirectToDBEditor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.jpa.EntityManagerFactoryInfo;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ComboitemRenderer;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.event.ListDataListener;
import org.zkoss.zul.ext.Selectable;

public class ReportParameterEditorController extends
		BaseSimpleNoDirectToDBEditor<RptDocParam> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1367577843402068150L;
	
	private static final List<String> javaTypes = Arrays.asList(new String[]{
			Short.class.getName(),
			Integer.class.getName(),
			Long.class.getName(),
			Float.class.getName(),
			Double.class.getName(),
			BigInteger.class.getName(),
			BigDecimal.class.getName(),
			short.class.getName(),
			int.class.getName(),
			long.class.getName(),
			float.class.getName(),
			double.class.getName(),
			Boolean.class.getName(),
			boolean.class.getName(),
			Date.class.getName(),
			String.class.getName()
	});
	
	@Wire	
	private Textbox paramCode;
	
	@Wire
	private Grid formGrid;
	
	@Wire
	private Grid lovClassParams;
	
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
	
	@Wire
	private Combobox lovClass;
	
	@Wire
	private Combobox lovClassValue;

	@Wire
	private Combobox lovClassCode;

	@Wire
	private Combobox lovClassLabel;

	@Autowired
	@Qualifier(value="entityManagerFactory")
	private EntityManagerFactoryInfo entityManagerFactory; 
	
	private static Map<String, EntityType<?>> jpaEntities;
	
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
		lovClassParams.setVisible(showLov);
		
		if(jpaEntities == null) {
			jpaEntities = new HashMap<String, EntityType<?>>();
			Set<EntityType<?>> eTypes = entityManagerFactory.getNativeEntityManagerFactory().getMetamodel().getEntities();
			if(eTypes != null) {
				Iterator<EntityType<?>> iter = eTypes.iterator();
				for(;iter.hasNext();) {
					EntityType<?> entity = iter.next();
					jpaEntities.put(entity.getName(), entity);
				}
			}
		}
		
		Label l = (Label)rowLovClass.getFirstChild();
		l.setValue(showLookup ? "Kode Lookup" : "LoV Class");
		
		if(showLookup) {
			ListModelList<Object> model = new ListModelList<Object>(generalPurposeDao.list(LookupHeader.class, null));			
			lovClass.setItemRenderer(new LoVClassComboitemRenderer(getEditedData().getLovClass()));
			lovClass.setModel(model);
		} else if(showLov) {
			String lClass = getEditedData().getLovClass().replace(")", "");
			String[] zclass = lClass.split("\\(");
			ListModelList<Object> model = new ListModelList<Object>(jpaEntities.values());
			lovClass.setItemRenderer(new LoVClassComboitemRenderer((zclass==null || zclass.length == 0) ? "" : zclass[0]));
			lovClass.setModel(model);
			
			if(zclass != null && zclass.length == 2) {
			
				List<Attribute<?, ?>> attrFields = basicAttributes(new ArrayList<Attribute<?, ?>>(jpaEntities.get(zclass[0]).getSingularAttributes()));
				
				ListModelList<Object> modelVal = new ListModelList<Object>(attrFields);
				ListModelList<Object> modelCd = new ListModelList<Object>(attrFields);
				ListModelList<Object> modelLbl = new ListModelList<Object>(attrFields);
				
				String[] fields = zclass[1].split("\\,");
				
				if(fields.length == 3) {
					lovClassValue.setItemRenderer(new LoVClassComboitemRenderer(fields[0]));
					lovClassValue.setModel(modelVal);				
					lovClassCode.setItemRenderer(new LoVClassComboitemRenderer(fields[1]));
					lovClassCode.setModel(modelCd);				
					lovClassLabel.setItemRenderer(new LoVClassComboitemRenderer(fields[2]));
					lovClassLabel.setModel(modelLbl);
				} else if(fields.length == 2) {
					lovClassValue.setItemRenderer(new LoVClassComboitemRenderer(fields[0]));
					lovClassValue.setModel(modelVal);				
					lovClassCode.setItemRenderer(new LoVClassComboitemRenderer(""));
					lovClassCode.setModel(modelCd);				
					lovClassLabel.setItemRenderer(new LoVClassComboitemRenderer(fields[1]));
					lovClassLabel.setModel(modelLbl);
				} else {
					lovClassValue.setItemRenderer(new LoVClassComboitemRenderer(""));
					lovClassValue.setModel(modelVal);				
					lovClassCode.setItemRenderer(new LoVClassComboitemRenderer(""));
					lovClassCode.setModel(modelCd);				
					lovClassLabel.setItemRenderer(new LoVClassComboitemRenderer(""));
					lovClassLabel.setModel(modelLbl);
				}
				
				List<Object> cmbItems = new ArrayList<Object>(attrFields);
				cmbItems.add("-");
				if(getEditedData().getLovParentId() != null && !"".equals(getEditedData().getLovParentId().trim())) {
					try {
						final ParsedJSONArrayContainer dependencies = ServerSideWrappedJSONParser.getInstance().parseJSONArray(getEditedData().getLovParentId().trim());
						if(dependencies.length() > 0) {
							ParsedJSONContainer con = dependencies.get(0);
							String field = con.getAsString("fieldName");
							String cmbId = con.getAsString("comboId");
							Listitem fDep = (Listitem)lstLoVDepedencies.getLastChild();
							if(fDep != null) {
								Combobox cmb = (Combobox)fDep.getChildren().get(1).getFirstChild();
								cmb.setItemRenderer(new LoVClassComboitemRenderer(field, true));
								cmb.setModel(new ListModelList<Object>(cmbItems));
								
								Textbox tVal = (Textbox)fDep.getChildren().get(0).getFirstChild();
								tVal.setValue(cmbId);
								
							}
						}
					} catch (Exception e) {
						Listitem fDep = (Listitem)lstLoVDepedencies.getLastChild();
						Combobox cmb = (Combobox)fDep.getChildren().get(1).getFirstChild();
						cmb.setItemRenderer(new LoVClassComboitemRenderer("", true));
						cmb.setModel(new ListModelList<Object>(cmbItems));
					}
				} else {
					Listitem fDep = (Listitem)lstLoVDepedencies.getLastChild();
					Combobox cmb = (Combobox)fDep.getChildren().get(1).getFirstChild();
					cmb.setItemRenderer(new LoVClassComboitemRenderer("", true));
					cmb.setModel(new ListModelList<Object>(cmbItems));
				}
				
				if(getEditedData().getLovFilters() != null && !"".equals(getEditedData().getLovFilters().trim())) {
					try {
						final ParsedJSONArrayContainer filters = ServerSideWrappedJSONParser.getInstance().parseJSONArray(getEditedData().getLovFilters().trim());
						if(filters.length() > 0) {
							ParsedJSONContainer con = filters.get(0);
							String field = con.getAsString("field");
							String opr = con.getAsString("opr");
							String val = con.getAsString("val");
							Listitem fFltr = (Listitem)lstLoVFilter.getLastChild();
							Combobox fCmb = (Combobox)fFltr.getChildren().get(0).getFirstChild();
							
							fCmb.setItemRenderer(new LoVClassComboitemRenderer(field, true));
							fCmb.setModel(new ListModelList<Object>(attrFields));
							
							

							Combobox oCmb = (Combobox)fFltr.getChildren().get(1).getFirstChild();
							List<Comboitem> oitems = oCmb.getItems();
							for(Comboitem oitem : oitems) {
								if(oitem.getValue().equals(opr)) {
									oCmb.setValue(oitem.getLabel());
									break;
								}
							}

							Textbox tVal = (Textbox)fFltr.getChildren().get(2).getFirstChild();
							tVal.setValue(val);
							
						}
					} catch (Exception e) {
						e.printStackTrace();
						Listitem fFltr = (Listitem)lstLoVFilter.getLastChild();
						Combobox fCmb = (Combobox)fFltr.getChildren().get(0).getFirstChild();
						fCmb.setItemRenderer(new LoVClassComboitemRenderer("", true));
						fCmb.setModel(new ListModelList<Object>(attrFields));
					}
				} else {
					Listitem fFltr = (Listitem)lstLoVFilter.getLastChild();
					Combobox fCmb = (Combobox)fFltr.getChildren().get(0).getFirstChild();
					fCmb.setItemRenderer(new LoVClassComboitemRenderer("", true));
					fCmb.setModel(new ListModelList<Object>(attrFields));
				}
				
			}
			
		} else if(showCmbItem) {
			if(getEditedData().getStaticData() != null && !"".equals(getEditedData().getStaticData().trim())) {
				try {
					final ParsedJSONArrayContainer items = ServerSideWrappedJSONParser.getInstance().parseJSONArray(getEditedData().getStaticData().trim());
					for(int i = 0; i < items.length(); i++) {
						ParsedJSONContainer data = items.get(i);
						if(i > 0) {
							Listitem last = (Listitem)lstComboItem.getLastChild();					
							lstComboItem.appendChild(createFilterListitem(last));
							last.getLastChild().getFirstChild().setVisible(false);
							last.getLastChild().getLastChild().setVisible(true);
						}
						Listitem litem = lstComboItem.getItems().get(i);
						Textbox val = (Textbox)litem.getChildren().get(0).getFirstChild();
						Textbox label = (Textbox)litem.getChildren().get(1).getFirstChild();
						label.setValue(data.getAsString("itemLabel"));
						val.setValue(data.getAsString("itemValue"));
					}
				} catch (Exception e) {}
			}
		}
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
			lovClassParams.setVisible(showLov);
			
			Label l = (Label)rowLovClass.getFirstChild();
			l.setValue(showLookup ? "Kode Lookup" : "LoV Class");

			if(showLookup) {
				ListModelList<Object> model = new ListModelList<Object>(generalPurposeDao.list(LookupHeader.class, null));			
				lovClass.setItemRenderer(new LoVClassComboitemRenderer(""));
				lovClass.setModel(model);
			} else if(showLov) {
				ListModelList<Object> model = new ListModelList<Object>(jpaEntities.values());
				lovClass.setItemRenderer(new LoVClassComboitemRenderer(""));
				lovClass.setModel(model);

				ListModelList<Object> modelVal = new ListModelList<Object>();
				ListModelList<Object> modelCd = new ListModelList<Object>();
				ListModelList<Object> modelLbl = new ListModelList<Object>();

				lovClassValue.setItemRenderer(new LoVClassComboitemRenderer(""));
				lovClassValue.setModel(modelVal);
				
				lovClassCode.setItemRenderer(new LoVClassComboitemRenderer(""));
				lovClassCode.setModel(modelCd);
				
				lovClassLabel.setItemRenderer(new LoVClassComboitemRenderer(""));
				lovClassLabel.setModel(modelLbl);
				
				Listitem fFltr = createFilterListitem((Listitem)lstLoVFilter.getLastChild());
				Combobox fCmb = (Combobox)fFltr.getChildren().get(0).getFirstChild();
				if(fCmb.getItemCount() > 0) {
					fCmb.getItems().clear();
				}
				lstLoVFilter.getItems().clear();
				lstLoVFilter.appendChild(fFltr);

				Listitem fDep = createFilterListitem((Listitem)lstLoVDepedencies.getLastChild());
				Combobox dCmb = (Combobox)fDep.getChildren().get(1).getFirstChild();
				if(dCmb.getItemCount() > 0) {
					dCmb.getItems().clear();
				}
				lstLoVDepedencies.getItems().clear();
				lstLoVDepedencies.appendChild(fDep);
				
				
			}
			
			getSelf().invalidate();
		}
	}
	
	@Listen("onSelect = #lovClass")
	public void onSelectLoVClass() {
		if(showLov) {
			String entityName = lovClass.getValue();
			
			List<Attribute<?, ?>> fields = basicAttributes(new ArrayList<Attribute<?, ?>>(jpaEntities.get(entityName).getSingularAttributes()));
			
			ListModelList<Object> modelVal = new ListModelList<Object>(fields);
			ListModelList<Object> modelCd = new ListModelList<Object>(fields);
			ListModelList<Object> modelLbl = new ListModelList<Object>(fields);

			lovClassValue.setItemRenderer(new LoVClassComboitemRenderer(""));
			lovClassValue.setModel(modelVal);
			
			lovClassCode.setItemRenderer(new LoVClassComboitemRenderer(""));
			lovClassCode.setModel(modelCd);
			
			lovClassLabel.setItemRenderer(new LoVClassComboitemRenderer(""));
			lovClassLabel.setModel(modelLbl);

			Listitem fFltr = createFilterListitem((Listitem)lstLoVFilter.getLastChild());
			Combobox fCmb = (Combobox)fFltr.getChildren().get(0).getFirstChild();
			if(fCmb.getItemCount() > 0) {
				fCmb.getItems().clear();
			}
			fCmb.setItemRenderer(new LoVClassComboitemRenderer("", true));
			fCmb.setModel(new ListModelList<Object>(fields));
			lstLoVFilter.getItems().clear();
			lstLoVFilter.appendChild(fFltr);

			List<Object> deps = new ArrayList<Object>(fields);
			deps.add("-");
			Listitem fDep = createFilterListitem((Listitem)lstLoVDepedencies.getLastChild());
			Combobox dCmb = (Combobox)fDep.getChildren().get(1).getFirstChild();
			if(dCmb.getItemCount() > 0) {
				dCmb.getItems().clear();
			}
			dCmb.setItemRenderer(new LoVClassComboitemRenderer("", true));
			dCmb.setModel(new ListModelList<Object>(deps));
			lstLoVDepedencies.getItems().clear();
			lstLoVDepedencies.appendChild(fDep);
			
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
	
	@Listen("onCreate = #lateRenderTimer")
	public void onCreateTimer() {
		Clients.showBusy(getSelf(),"Loading...");
	}
	
	@Listen("onTimer = #lateRenderTimer")
	public void onLateRender() {
		if(getEditedData().getLovClass() == null || "".equals(getEditedData().getLovClass().trim())) {
			Clients.clearBusy(getSelf());
			return;
		}
		String lClass = getEditedData().getLovClass().replace(")", "");
		String[] zclass = lClass.split("\\(");
		if(zclass != null && zclass.length == 2) {
			
			List<Attribute<?, ?>> attrFields = basicAttributes(new ArrayList<Attribute<?, ?>>(jpaEntities.get(zclass[0]).getSingularAttributes()));
			
			try {
				
				List<Object> cmbItems = new ArrayList<Object>(attrFields);
				cmbItems.add("-");
				
				final ParsedJSONArrayContainer dependencies = ServerSideWrappedJSONParser.getInstance().parseJSONArray(getEditedData().getLovParentId().trim());
				for(int i = 1; i < dependencies.length(); i++) {
					ParsedJSONContainer con = dependencies.get(i);
					String field = con.getAsString("fieldName");
					String cmbId = con.getAsString("comboId");
					
					Listitem fDep = createFilterListitem((Listitem)lstLoVDepedencies.getLastChild());
					lstLoVDepedencies.appendChild(fDep);
					
					if(fDep != null) {
						Combobox cmb = (Combobox)fDep.getChildren().get(1).getFirstChild();
						cmb.setItemRenderer(new LoVClassComboitemRenderer(field, true));
						cmb.setModel(new ListModelList<Object>(cmbItems));
						
						Textbox tVal = (Textbox)fDep.getChildren().get(0).getFirstChild();
						tVal.setValue(cmbId);
						
					}
					Listitem litm = lstLoVDepedencies.getItems().get(i-1);
					litm.getLastChild().getFirstChild().setVisible(false);
					litm.getLastChild().getLastChild().setVisible(true);
				}
			} catch (Exception e) {
			}
			
			try {
				
				final ParsedJSONArrayContainer filters = ServerSideWrappedJSONParser
						.getInstance().parseJSONArray(
								getEditedData().getLovFilters().trim());
				
				for (int i = 1; i < filters.length(); i++) {
					ParsedJSONContainer con = filters.get(i);
					String field = con.getAsString("field");
					String opr = con.getAsString("opr");
					String val = con.getAsString("val");
					Listitem fFltr = createFilterListitem((Listitem) lstLoVFilter.getLastChild());
					lstLoVFilter.appendChild(fFltr);
						
					Combobox fCmb = (Combobox) fFltr.getChildren().get(0)
								.getFirstChild();

					fCmb.setItemRenderer(new LoVClassComboitemRenderer(
							field, true));
					fCmb.setModel(new ListModelList<Object>(attrFields));

					Combobox oCmb = (Combobox) fFltr.getChildren().get(1)
							.getFirstChild();
					List<Comboitem> oitems = oCmb.getItems();
					for (Comboitem oitem : oitems) {
						if (oitem.getValue().equals(opr)) {
							oCmb.setValue(oitem.getLabel());
							break;
						}
					}

					Textbox tVal = (Textbox) fFltr.getChildren().get(2)
							.getFirstChild();
					tVal.setValue(val);
					
					Listitem litm = lstLoVFilter.getItems().get(i - 1);
					litm.getLastChild().getFirstChild().setVisible(false);
					litm.getLastChild().getLastChild().setVisible(true);

				}
			} catch (Exception e) {
			}
			
			getSelf().invalidate();
		}
		
		Clients.clearBusy(getSelf());
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
		if(showLov) {
			getEditedData().setLovFilters(parseLoVFilters());
			getEditedData().setLovParentId(parseLoVDependencies());
			Comboitem cval = lovClassValue.getSelectedItem();
			String fields = cval.getLabel();
			Comboitem ccod = lovClassCode.getSelectedItem();
			if(ccod != null) {
				fields = fields + "," + ccod.getLabel();
			}
			Comboitem clbl = lovClassLabel.getSelectedItem();
			fields = fields + "," + clbl.getLabel();
			String sclass = getEditedData().getLovClass() + "(" + fields + ")";
			getEditedData().setLovClass(sclass);
		} else if(showCmbItem) {
			getEditedData().setStaticData(parseComboboxItems());
		}
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
					if(o instanceof Attribute<?, ?>) {
						 Attribute<?, ?> attr = ( Attribute<?, ?>)o;
						 if(filter.length() > 0) filter.append(",");
						 filter.append("\"").append(headers.get(i).getId()).append("\":\"").append(attr.getName()).append("\",");
						 filter.append("\"").append("fType").append("\":\"").append(attr.getJavaType().getName()).append("\"");
					} else {
						String sval = String.valueOf(o);
						if(!"".equals(sval.trim())) {
							if(filter.length() > 0) filter.append(",");
							filter.append("\"").append(headers.get(i).getId()).append("\":\"").append(sval).append("\"");
						}
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
			if (f instanceof Combobox) {
				((Combobox)f).setValue("");
				ListModelList<Object> model = (ListModelList<Object>)((Combobox)f).getModel();
				if(model != null && model.getInnerList() != null) {
					((Combobox)f).setModel(new ListModelList<Object>(model.getInnerList()));
				}
				
			} else if(f instanceof Textbox) {
				((Textbox)f).setValue("");
			}
		}
		return item;
	}
	
	private List<Attribute<?, ?>> basicAttributes(List<Attribute<?, ?>> attrs) {
		
		List<Attribute<?, ?>> bAttrs = new ArrayList<Attribute<?, ?>>();
		
		for(Attribute<?, ?> attr : attrs) {
			if(attr.getPersistentAttributeType() == Attribute.PersistentAttributeType.BASIC) {
				bAttrs.add(attr);
			}
		}
		
		return bAttrs;
	}
	
	private class LoVClassComboitemRenderer implements ComboitemRenderer<Object> {
		
		private String defaultValue;
		
		private boolean initiated = false;
		
		private boolean attrValue = false;
		
		public LoVClassComboitemRenderer(String defaultValue) {
			super();
			this.defaultValue = defaultValue;
			this.initiated = false;
		}

		public LoVClassComboitemRenderer(String defaultValue, boolean attrValue) {
			this(defaultValue);
			this.attrValue = attrValue;
		}
		
		@Override
		public void render(Comboitem item, Object data, int index)
				throws Exception {
			String val = null;
			if(data instanceof EntityType<?>) {
				item.setLabel(((EntityType<?>)data).getName());
				item.setValue(((EntityType<?>)data).getName());
				if(item.getValue().equals(defaultValue) && (!initiated)) {
					try {
						initiated = true;
						((Combobox)item.getParent()).setRawValue(item.getLabel());
					} catch (Exception e) {}
				}
			} else if(data instanceof LookupHeader) {
				item.setLabel(((LookupHeader)data).getLovRemark());
				item.setValue(((LookupHeader)data).getLovId());
				if(item.getValue().equals(defaultValue) && (!initiated)) {
					try {
						initiated = true;
						((Combobox)item.getParent()).setRawValue(item.getLabel());
					} catch (Exception e) {}
				}
			} else if(data instanceof Attribute<?, ?>) {
				Attribute<?, ?> attr = (Attribute<?, ?>)data;
				item.setLabel(attr.getName());
				if(attrValue) {
					item.setValue(data);
				} else {
					item.setValue(attr.getName());
				}
				if(item.getLabel().equals(defaultValue) && (!initiated)) {
					try {
						initiated = true;
						((Combobox)item.getParent()).setRawValue(item.getLabel());
					} catch (Exception e) {}
				}
			} else if(data instanceof String) {
				item.setLabel((String)data);
				item.setValue((String)data);
				if(item.getLabel().equals(defaultValue) && (!initiated)) {
					try {
						initiated = true;
						((Combobox)item.getParent()).setRawValue(item.getLabel());
					} catch (Exception e) {}
				}
			}
			if(!initiated) {
				try {
					((Combobox)item.getParent()).setRawValue(val);
				} catch (Exception e) {}
			}
		}
		
	}
	
	@SuppressWarnings("unused")
	private class JPAAttributeListModel implements ListModel<Attribute<?, ?>>, Selectable<Attribute<?, ?>> {
		
		private List<Attribute<?, ?>> data;
		private int totalAttr = 0;
		private Set<Attribute<?, ?>> selectedItems = new HashSet<Attribute<?, ?>>();
		
		public JPAAttributeListModel(Collection<Attribute<?, ?>> data) {
			super();
			this.data = new ArrayList<Attribute<?, ?>>(data);
		}

		@Override
		public Attribute<?, ?> getElementAt(int index) {
			if(this.data != null && !this.data.isEmpty()) {
				Attribute<?, ?> attr = null;
				boolean able = false;
				while(!able) {
					attr = this.data.get(index);
					able = javaTypes.contains(attr.getJavaType().getName());
					if(!able) data.remove(attr);
					if(this.data.size() <= index) {
						return null;
					}
				}
				return attr;
			}
			return null;
		}

		@Override
		public int getSize() {
			return this.data.size();
		}

		@Override
		public void addListDataListener(ListDataListener l) {
		}

		@Override
		public void removeListDataListener(ListDataListener l) {
		}

		@Override
		public Set<Attribute<?, ?>> getSelection() {
			return selectedItems;
		}

		@Override
		public void setSelection(Collection<? extends Attribute<?, ?>> selection) {
			selectedItems.clear();
			selectedItems.addAll(selection);
		}

		@Override
		public boolean isSelected(Object obj) {
			return selectedItems.contains(obj);
		}

		@Override
		public boolean isSelectionEmpty() {
			return selectedItems.isEmpty();
		}

		@Override
		public boolean addToSelection(Attribute<?, ?> obj) {
			return selectedItems.add(obj);
		}

		@Override
		public boolean removeFromSelection(Object obj) {
			return selectedItems.remove(obj);
		}

		@Override
		public void clearSelection() {
			selectedItems.clear();
		}

		@Override
		public void setMultiple(boolean multiple) {
			
		}

		@Override
		public boolean isMultiple() {
			return false;
		}
		
	}
	
}
