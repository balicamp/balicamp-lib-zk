package id.co.sigma.zk.ui.util;

import java.io.InputStream;
import java.util.Locale;

import org.zkoss.util.resource.LabelLocator2;

public class CustomLabelLocator implements LabelLocator2 {

	private String resourceName;
	
	public CustomLabelLocator(String resourceName) {
		super();
		this.resourceName = resourceName;
	}

	@Override
	public InputStream locate(Locale locale) {
		InputStream is = getClass().getClassLoader().getResourceAsStream(resourceName);
		return is;
	}

	@Override
	public String getCharset() {
		return "UTF-8";
	}

}
