package id.co.sigma.zk.ui.controller.base;

import id.co.sigma.common.data.query.SimpleQueryFilter;
import id.co.sigma.common.data.query.SimpleQueryFilterOperator;
import id.co.sigma.common.report.domain.RptDocument;

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

import org.springframework.beans.factory.annotation.Value;
import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Window;

/**
 * Base class editor yang khusus ada feature printnya
 * @author <a href="mailto:raka.sanjaya@sigma.co.id">Raka Sanjaya</a>
 */

@org.springframework.stereotype.Component
public abstract class JRPrintableEditorController<POJO extends Serializable> extends
		BaseSimpleDirectToDBEditor<POJO> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6846905208597141441L;
	static final org.slf4j.Logger logger = org.slf4j.LoggerFactory
			.getLogger(JRPrintableEditorController.class.getName());
	
	public static final String EXPORT_TYPE_PDF = "pdf";

    public static final String EXPORT_TYPE_XLS = "xls";
    
    @Value("${jasper.report.server}")
    private String reportServerUrl;

    @Value("${jasper.report.user}")
    private String reportServerUsername;

    @Value("${jasper.report.pass}")
    private String reportServerPassword;
    
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
    
    
    protected void printDocument(String documentType) {
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
