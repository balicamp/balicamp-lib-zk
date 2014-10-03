package id.co.sigma.zk.ui;

import id.co.sigma.zk.ui.util.CustomLabelLocator;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.util.WebAppInit;

public class SigmaWebAppInit implements WebAppInit {

	@Override
	public void init(WebApp wapp) throws Exception {
		Labels.register(new CustomLabelLocator("/metainfo/labels/common-label.properties"));
		Labels.register(new CustomLabelLocator("/metainfo/labels/common-messages.properties"));
		Labels.register(new CustomLabelLocator("/metainfo/labels/menu-label.properties"));
	}

}
