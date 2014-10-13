package id.co.sigma.zk.ui.custom.component;

import id.co.sigma.common.server.util.ExtendedBeanUtils;
import id.co.sigma.zk.ui.data.ZKClientSideListDataEditorContainer;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Box;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;

public class DualListbox extends Div implements AfterCompose, IdSpace {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<Object> srcModel;
	
	@Wire
	private Hlayout dualLayout;
	
	@Wire
	private Box btnBox;
	
	@Wire
	private Listbox candidate;
	
	@Wire
	private Listbox choosendata;
	
	@Wire
	private Button moveRightAll;
	@Wire
	private Button moveRight;
	@Wire
	private Button moveLeft;
	@Wire
	private Button moveLeftAll;
	
	private String showField;
	
	private ZKClientSideListDataEditorContainer<Object> targetContainer;
	
	private ListModelList<Object> candidateModel;
	private ListModelList<Object> chosenModel;
	
	public DualListbox() {
		Executions.createComponents("~./zul/pages/common/DualListbox.zul", this, null);
		Selectors.wireComponents(this, this, false);
		Selectors.wireVariables(this, this, null);
		Selectors.wireEventListeners(this, this);
		choosendata.setModel(chosenModel = new ListModelList<Object>());
		chosenModel.setMultiple(true);
	}

	@Override
	public void afterCompose() {
		String height = getHeight();
		if(height != null && height.trim().length() > 0) {
			dualLayout.setHeight(height);
		} 			
		
		btnBox.setHeight(dualLayout.getHeight());
		candidate.setHeight(dualLayout.getHeight());
		choosendata.setHeight(dualLayout.getHeight());

		if(this.srcModel != null) {
			
			List<Object> existingData = targetContainer.getAllStillExistData();
			
			if((existingData != null) && !existingData.isEmpty()) {
				this.srcModel.removeAll(existingData);				
				chosenModel.addAll(existingData);
				
				populateData(choosendata, existingData);
				
			}
			
			candidate.setModel(candidateModel = new ListModelList<Object>(this.srcModel));
			candidateModel.setMultiple(true);
			
			populateData(candidate, candidateModel.getInnerList());
		}
	}

	/**
	 * @return the srcModel
	 */
	public List<Object> getSrcModel() {
		return srcModel;
	}

	/**
	 * @param srcModel the srcModel to set
	 */
	public void setSrcModel(List<Object> srcModel) {
		this.srcModel = srcModel;
	}

	/**
	 * @return the showField
	 */
	public String getShowField() {
		return showField;
	}

	/**
	 * @param showField the showField to set
	 */
	public void setShowField(String showField) {
		this.showField = showField;
	}

	@Listen("onClick = #moveRightAll")
	public void moveRightAll() {
		targetContainer.appendNewItems(candidateModel.getInnerList());
		chosenModel.addAll(candidateModel);
		candidateModel.clear();
		
		populateData(choosendata, targetContainer.getAllStillExistData());
	}
	
	@Listen("onClick = #moveLeftAll")
	public void moveLeftAll() {
		targetContainer.eraseData(chosenModel.getInnerList());
		candidateModel.addAll(chosenModel);
		chosenModel.clear();
		
		populateData(candidate, candidateModel.getInnerList());
	}
	
	@Listen("onClick = #moveRight")
	public void moveRight() {
		Events.postEvent(new ChooseEvent(this, chooseSome()));
	}

	@Listen("onClick = #moveLeft")
	public void moveLeft() {
		Events.postEvent(new ChooseEvent(this, removeSome()));
	}
	
	/**
	 * @return the targetContainer
	 */
	public ZKClientSideListDataEditorContainer<Object> getTargetContainer() {
		return targetContainer;
	}

	/**
	 * @param targetContainer the targetContainer to set
	 */
	public void setTargetContainer(
			ZKClientSideListDataEditorContainer<Object> targetContainer) {
		this.targetContainer = targetContainer;
	}
	
	private Set<Object> chooseSome() {
		Set<Object> set = candidateModel.getSelection();
		
		chosenModel.addAll(set);
		targetContainer.appendNewItems(new ArrayList<Object>(set));

		candidateModel.removeAll(set);
		
		populateData(choosendata, targetContainer.getAllStillExistData());

		return set;
	}
	
	private Set<Object> removeSome() {
		Set<Object> set = chosenModel.getSelection();
		
		candidateModel.addAll(set);

		targetContainer.eraseData(new ArrayList<Object>(set));
		chosenModel.removeAll(set);
		
		populateData(candidate, candidateModel.getInnerList());

		return set;
	}

	private void setLabel(Listitem item) {
		try {

			Object data = item.getValue();
			PropertyDescriptor descriptor = ExtendedBeanUtils.getPropertyDescriptor(data.getClass(), showField);
			
			Object field = descriptor.getReadMethod().invoke(data, new Object[]{});
			
			item.setLabel(String.valueOf(field));
			
		} catch (Exception e) {
			
		}
	}
	
	private void populateData(Listbox listbox, List<Object> list) {
		int i = 0;
		for(Listitem item : listbox.getItems()) {
			item.setValue(list.get(i++));
			setLabel(item);
		}
	}
	
	public class ChooseEvent extends Event {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public ChooseEvent(Component target, Set<Object> data) {
			super("onChoose", target, data);
		}
		
	}
}
