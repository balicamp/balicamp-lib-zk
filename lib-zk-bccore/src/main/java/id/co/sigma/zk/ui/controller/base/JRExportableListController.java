package id.co.sigma.zk.ui.controller.base;

import id.co.sigma.common.data.query.SimpleQueryFilter;
import id.co.sigma.common.data.query.SimpleQueryFilterOperator;
import id.co.sigma.common.report.domain.RptDocument;
import id.co.sigma.common.server.dao.IGeneralPurposeDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Window;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JRExportableListController.
 *
 * @author <a href="mailto:wayan.saryada@sigma.co.id">Wayan Saryada</a>
 */
@org.springframework.stereotype.Component
public abstract class JRExportableListController<T extends Serializable> extends BaseSimpleListController<T> {
    private static final long serialVersionUID = 5333305493601980594L;

    private Logger logger = LoggerFactory.getLogger(getClass());

    public static final String EXPORT_TYPE_PDF = "pdf";

    public static final String EXPORT_TYPE_XLS = "xls";

    private Radio pdfRadio;
    private Radio xlsRadio;

    @Value("${jasper.report.server}")
    private String reportServerUrl;

    @Value("${jasper.report.user}")
    private String reportServerUsername;

    @Value("${jasper.report.pass}")
    private String reportServerPassword;

    @Autowired
    protected IGeneralPurposeDao generalPurposeDao;

    @Wire
    private Div exportButtonsPlaceHolder;

    @Wire
    protected Listbox searchListbox;

    /**
     * Returns placeholder component where the export buttons (PDF & XLS) will be placed.
     *
     * @return placeholder component.
     */
    public Component getExportButtonsPlaceHolder() {
        return exportButtonsPlaceHolder;
    }

    /**
     * Checks to see if export to PDF is supported.
     *
     * @return true if supported, false when not supported;
     */
    public boolean isPdfSupported() {
        return true;
    }

    /**
     * Checks to see it export to XLS is supported.
     *
     * @return true if supported, false when not supported;
     */
    public boolean isXlsSupported() {
        return true;
    }

    /**
     * Returns report unit.
     *
     * @return report unit.
     */
    public abstract String getReportName();

    /**
     * Gets export parameters.
     *
     * @return export parameters Map.
     */
    public abstract Map<String, String> getExportParameters();

    /**
     * Gets search Listbox.
     *
     * @return search Listbox.
     */
    public Listbox getSearchListbox() {
        return searchListbox;
    }

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        if (getExportButtonsPlaceHolder() != null) {
            createView(getExportButtonsPlaceHolder());
        }
    }

    private void createView(Component component) {
        Label label = new Label(Labels.getLabel("export.label"));
        label.setStyle("margin-right: 10px;");

        component.getChildren().add(label);
        component.getChildren().add(createRadioButtons());
        component.getChildren().add(createExportButton());
    }

    private Radiogroup createRadioButtons() {
        Radiogroup radiogroup = new Radiogroup();
        if (isPdfSupported()) {
            pdfRadio = new Radio(Labels.getLabel("export.file.pdf"));
            pdfRadio.setStyle("margin-right: 5px;");
            radiogroup.getChildren().add(pdfRadio);
        }
        if (isXlsSupported()) {
            xlsRadio = new Radio(Labels.getLabel("export.file.xls"));
            xlsRadio.setStyle("margin-right: 5px;");
            radiogroup.getChildren().add(xlsRadio);
        }
        radiogroup.setSelectedIndex(0);
        radiogroup.setStyle("margin-right: 10px;");
        return radiogroup;
    }

    private Button createExportButton() {
        Button exportButton = new Button(Labels.getLabel("action.button.export"));
        exportButton.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                if (getSearchListbox() != null && getSearchListbox().getItemCount() > 0) {
                    doExport();
                } else {
                    Messagebox.show("export.empty", Labels.getLabel("title.msgbox.information"), Messagebox.OK,
                            Messagebox.INFORMATION);
                }
            }
        });
        return exportButton;
    }

    public void doExport() {
        if (pdfRadio.isSelected()) {
            exportList(JRExportableListController.EXPORT_TYPE_PDF);
        } else if (xlsRadio.isSelected()) {
            exportList(JRExportableListController.EXPORT_TYPE_XLS);
        }
    }

    private void exportList(String documentType) {
        Map<String, String> exportParameters = getExportParameters();

        try {
            String reportUrl = createReportUrl(documentType) + createReportParams(exportParameters);
            Map<String, String> args = new HashMap<>();
            args.put("reportUrl", reportUrl);

            if (JRExportableListController.EXPORT_TYPE_PDF.equals(documentType)) {
                Window reportWindow = (Window) Executions.createComponents("~./zul/pages/report/ReportView.zul", null, args);
                reportWindow.doModal();
            } else if (JRExportableListController.EXPORT_TYPE_XLS.equals(documentType)) {
                URL url = new URL(reportUrl);
                BufferedInputStream bis = new BufferedInputStream(url.openStream());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                byte[] data = new byte[1024];
                int length;
                while ((length = bis.read(data)) != -1) {
                    baos.write(data, 0, length);
                }

                String[] reportUnit = getReportName().split(File.separator);
                AMedia media = new AMedia(reportUnit[reportUnit.length - 1] + "_" + System.currentTimeMillis() +
                        "." + documentType, documentType, "application/vnd.ms-excel", baos.toByteArray());
                Filedownload.save(media);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private String createReportUrl(String documentType) throws Exception {
        List<RptDocument> reportDocuments = null;

        try {
            reportDocuments = generalPurposeDao.list(RptDocument.class, new SimpleQueryFilter[]{
                    new SimpleQueryFilter("name", SimpleQueryFilterOperator.equal, getReportName())
            }, null);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        if (reportDocuments == null || reportDocuments.isEmpty()) {
            throw new Exception("Undefined report document '" + getReportName() + "'.");
        }

        RptDocument reportDocument = reportDocuments.get(0);
        String reportFolder = reportDocument.getTemplate();
        String reportUnit = reportFolder + File.separator + reportDocument.getName();

        return reportServerUrl + "&ParentFolderUri=" + URLEncoder.encode(reportFolder, "UTF-8") +
                "&reportUnit=" + URLEncoder.encode(reportUnit, "UTF-8") + "&j_username=" + reportServerUsername +
                "&j_password=" + reportServerPassword + "&output=" + documentType + "&decoration=no&userLocale=" +
                getLocale().getLanguage() + "_" + getLocale().getCountry();
    }

    /**
     * Converts export parameter into url parameters.
     *
     * @param exportParameters a key-value pair of parameters.
     * @return string of url parameters.
     */
    private String createReportParams(Map<String, String> exportParameters) {
        StringBuilder builder = new StringBuilder();

        for (String parameter : exportParameters.keySet()) {
            try {
                builder.append("&").append(parameter).append("=")
                        .append(URLEncoder.encode(exportParameters.get(parameter), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                logger.error(e.getMessage(), e);
            }
        }

        return builder.toString();
    }
}
