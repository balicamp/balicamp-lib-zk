package id.co.sigma.zk.ui.controller.report;

import id.co.sigma.common.data.query.SimpleQueryFilter;
import id.co.sigma.common.data.query.SimpleQueryFilterOperator;
import id.co.sigma.common.report.domain.RptDocParam;
import id.co.sigma.common.report.domain.RptDocument;
import id.co.sigma.zk.ui.controller.base.BaseSimpleController;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.zkoss.web.Attributes;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
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
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbar;
import org.zkoss.zul.Window;
import org.zkoss.zul.impl.InputElement;

public class ReportFormController extends BaseSimpleController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5141315517810906819L;
	
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

	@Listen("onClick = #btnPrintPdf")
	public void printPDF() {
		
		try {
			
			Locale locale = (Locale)Executions.getCurrent().getSession().getAttribute(Attributes.PREFERRED_LOCALE);
			
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
		
		List<RptDocument> rptDocs = generalPurposeDao.list(RptDocument.class, new SimpleQueryFilter[]{
			new SimpleQueryFilter("name", SimpleQueryFilterOperator.equal, rptName)
		}, null);
		
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
			
			for(RptDocParam param : rptParams) {
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
				}
				
				row.appendChild(inp);
				
				rows.appendChild(row);
				
				reportParams.add(inp);
			}
			
			grid.appendChild(rows);
			
			return grid;
		}
	}
	
	private String createReportParams() {
		StringBuffer sbuf = new StringBuffer();
		for(Component inp : reportParams) {
			Object val = "";
			if(inp instanceof Combobox) {
				if(((Combobox)inp).getSelectedIndex() > -1) {
					val = ((Combobox)inp).getSelectedItem().getValue();
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
}
