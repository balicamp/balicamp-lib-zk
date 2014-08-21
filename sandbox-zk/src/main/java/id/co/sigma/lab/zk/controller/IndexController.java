package id.co.sigma.lab.zk.controller;


import java.util.List;


import id.co.sigma.common.data.query.SimpleQueryFilter;
import id.co.sigma.common.data.query.SimpleSortArgument;
import id.co.sigma.common.server.dao.IGeneralPurposeDao;
import id.co.sigma.sandbox.shared.domain.Person;
import id.co.sigma.zk.ui.SimpleQueryDrivenListModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;

/**
 * 
 * @author <a href='mailto:gede.sutarsa@gmail.com'>Gede Sutarsa</a>
 */
public class IndexController extends GenericForwardComposer<Component>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3985804946802605085L;
	
	@Autowired
	IGeneralPurposeDao generalPurposeDao ;
	
	@Wire
	Listbox personListbox ; 
	
	private SimpleQueryDrivenListModel<Person> personData ;
	
	
	Textbox txt    ; 
	private List<Person> persons ; 
	@Override
	public void doBeforeComposeChildren(Component comp) throws Exception {
		
		super.doBeforeComposeChildren(comp);
	}
	@Override
	public ComponentInfo doBeforeCompose(Page page, Component parent,
			ComponentInfo compInfo) {
		
		if ( generalPurposeDao== null){
			SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
		}
		persons = generalPurposeDao.list(Person.class, null);
		return super.doBeforeCompose(page, parent, compInfo);
	}
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		/* Window win = (Window) comp;
         AnnotateDataBinder binder = new AnnotateDataBinder(win);
         System.out.println("WINDOW_ID : " + win.getId());
         System.out.println("DESKTOP_ID : " + win.getDesktop().getId());
		*/
				
		personData = new SimpleQueryDrivenListModel<Person>() {
			
			@Override
			protected SimpleSortArgument[] getSorts() {
				return null;
			}
			
			@Override
			public Class<? extends Person> getHandledClass() {
				return Person.class;
			}
			
			@Override
			protected SimpleQueryFilter[] getFilters() {
				return null;
			}
		};
		personData.initiate(personListbox.getPageSize());/**/
		personListbox.setModel(personData);
		Executions.createComponents("~./zul/pages/HelloComponent.zul", null	, null);
		 
		
		
	}
	public List<Person> getPersons() {
		return persons;
	}
	
	
	

}
