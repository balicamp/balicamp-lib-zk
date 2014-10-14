package id.co.sigma.zk.ui.custom.component;

import id.co.sigma.common.server.util.ExtendedBeanUtils;
import id.co.sigma.zk.ui.annotations.DualListboxBinder;
import id.co.sigma.zk.ui.annotations.EqualsField;
import id.co.sigma.zk.ui.data.ZKClientSideListDataEditorContainer;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
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
import org.zkoss.zul.Caption;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

public class DualListbox extends Div implements AfterCompose, IdSpace {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<?> srcModel;
	
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
	
	@Wire
	private Caption sourceCaption;
	
	@Wire
	private Caption targetCaption;
	
	private String showField;
	
	private String sourceClass;
	
	private String targetClass;
	
	private ZKClientSideListDataEditorContainer<Object> targetContainer;
	
	private ListModelList<Object> candidateModel;
	private ListModelList<Object> chosenModel;
	
	private EqualsField[] equalsFields;
	
	public DualListbox() {
		Executions.createComponents("~./zul/pages/common/DualListbox.zul", this, null);
		Selectors.wireComponents(this, this, false);
		Selectors.wireVariables(this, this, null);
		Selectors.wireEventListeners(this, this);
		choosendata.setModel(chosenModel = new ListModelList<Object>());
		chosenModel.setMultiple(true);
	}
	
	public void loadData() {
		if(candidateModel != null) candidateModel.clear();
		if(chosenModel != null) chosenModel.clear();
		if(this.srcModel != null) {
			
			if(!this.srcModel.isEmpty()){
				this.srcModel.get(0).hashCode();
			}
			if((targetContainer != null) && (targetContainer.getAllStillExistData() != null) && !targetContainer.getAllStillExistData().isEmpty()) {
				targetContainer.getAllStillExistData().get(0).hashCode();
			}
			
			List<Object> existingData = copyData(targetContainer.getAllStillExistData(), sourceClass);
			
			if((existingData != null) && !existingData.isEmpty()) {
				
				if(equalsFields != null && equalsFields.length > 0) {
					for(Object o : existingData) {
						for(Object s : this.srcModel) {
							boolean eq = true;
							for(EqualsField ef : equalsFields) {
								try {
									Object tk = ExtendedBeanUtils.getPropertyDescriptor(
											o.getClass(), 
											"".equals(ef.targetField()) ? ef.sourceField() : ef.targetField()
											).getReadMethod().invoke(o, new Object[]{});
									Object sk = ExtendedBeanUtils.getPropertyDescriptor(
											s.getClass(), 
											ef.sourceField()
											).getReadMethod().invoke(s, new Object[]{});
									eq = eq && (tk != null && tk.equals(sk));
								} catch (Exception e) {
									eq = false;
									break;
								}
							}
							if(eq){
								this.srcModel.remove(s);
								break;
							}
						}
					}
				} else {				
					this.srcModel.removeAll(existingData);				
				}

				chosenModel.addAll(targetContainer.getAllStillExistData());
				
				populateData(choosendata, targetContainer.getAllStillExistData());
				
			}
			
			candidate.setModel(candidateModel = new ListModelList<Object>(this.srcModel));
			candidateModel.setMultiple(true);
			
			populateData(candidate, candidateModel.getInnerList());
		}
	}

	@Override
	public void afterCompose() {
		
		Window window = getParentWindow();
		
		if(window != null) {
			Object composer = window.getAttribute(window.getId() + "$composer");
			if(composer != null) {
				Field[] fields = composer.getClass().getDeclaredFields();
				for(Field f: fields) {
					if(f.isAnnotationPresent(DualListboxBinder.class)) {
						String id = getId();
						if(f.isAnnotationPresent(Wire.class)) {
							Wire w = f.getAnnotation(Wire.class);
							if(!("".equals(w.value()))) {
								id = w.value();
							}
						}
						if(f.getName().equals(id)) {
							DualListboxBinder ann = f.getAnnotation(DualListboxBinder.class);
							this.targetClass = ann.targetClass().getName();
							this.sourceClass = ann.sourceClass().getName();
							this.equalsFields = ann.equalsFields();
						}
					}
				}
			}
		}
		
		String height = getHeight();
		if(height != null && height.trim().length() > 0) {
			dualLayout.setHeight(height);
		} 			
		
		btnBox.setHeight(dualLayout.getHeight());
		candidate.setHeight(dualLayout.getHeight());
		choosendata.setHeight(dualLayout.getHeight());
		
		loadData();
		
		toggleButtons();
	}

	/**
	 * @return the srcModel
	 */
	public List<?> getSrcModel() {
		return srcModel;
	}

	/**
	 * @param srcModel the srcModel to set
	 */
	public void setSrcModel(List<?> srcModel) {
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
		List<Object> data = copyData(candidateModel.getInnerList(), targetClass);
		targetContainer.appendNewItems(data);
		chosenModel.addAll(data);
		candidateModel.clear();
		
		populateData(choosendata, targetContainer.getAllStillExistData());
	}
	
	@Listen("onClick = #moveLeftAll")
	public void moveLeftAll() {
		List<Object> data = copyData(chosenModel.getInnerList(), sourceClass);
		targetContainer.eraseData(chosenModel.getInnerList());
		candidateModel.addAll(data);
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
	
	/**
	 * @return the sourceClass
	 */
	public String getSourceClass() {
		return sourceClass;
	}

	/**
	 * @param sourceClass the sourceClass to set
	 */
	public void setSourceClass(String sourceClass) {
		this.sourceClass = sourceClass;
	}

	/**
	 * @return the targetClass
	 */
	public String getTargetClass() {
		return targetClass;
	}

	/**
	 * @param targetClass the targetClass to set
	 */
	public void setTargetClass(String targetClass) {
		this.targetClass = targetClass;
	}
	
	public void setSourceTitle(String title) {
		this.sourceCaption.setLabel(title);
	}

	public void setTargetTitle(String title) {
		this.targetCaption.setLabel(title);
	}
	
	@SuppressWarnings("rawtypes")
	public List<Object> copyData(List<Object> sources, String className) {
		
		try {
			Class c = Class.forName(className);
		
			List<Object> datas = new ArrayList<Object>();
			for(Object source : sources) {
				try {
					Object oDest = c.newInstance();
					ExtendedBeanUtils.copyProperties(source, oDest);
					datas.add(oDest);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			return datas;
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public Set<Object> chooseSome() {
		Set<Object> set = candidateModel.getSelection();
		
		List<Object> orig = new ArrayList<Object>(set);
		List<Object> data = copyData(orig, targetClass);
		
		chosenModel.addAll(data);
		targetContainer.appendNewItems(data);

		candidateModel.removeAll(set);
		
		populateData(choosendata, targetContainer.getAllStillExistData());

		return set;
	}
	
	public Set<Object> removeSome() {
		Set<Object> set = chosenModel.getSelection();

		List<Object> orig = new ArrayList<Object>(set);
		List<Object> data = copyData(orig, sourceClass);
		
		candidateModel.addAll(data);

		targetContainer.eraseData(orig);
		chosenModel.removeAll(set);
		
		populateData(candidate, candidateModel.getInnerList());

		return set;
	}

	public void setLabel(Listitem item) {
		try {

			Object data = item.getValue();
			PropertyDescriptor descriptor = ExtendedBeanUtils.getPropertyDescriptor(data.getClass(), showField);
			
			Object field = descriptor.getReadMethod().invoke(data, new Object[]{});
			
			item.setLabel(String.valueOf(field));
			
		} catch (Exception e) {
			
		}
	}
	
	public void populateData(Listbox listbox, List<Object> list) {
		int i = 0;
		for(Listitem item : listbox.getItems()) {
			item.setValue(list.get(i++));
			setLabel(item);
		}
		toggleButtons();
	}
	
	private void toggleButtons() {
		if(candidateModel == null || candidateModel.getInnerList() == null) {
			moveRightAll.setDisabled(true);
			moveRight.setDisabled(true);
		} else {
			moveRightAll.setDisabled(candidateModel.getInnerList().isEmpty());
			moveRight.setDisabled(candidateModel.getInnerList().isEmpty());
		}
		if(targetContainer == null || targetContainer.getAllStillExistData() == null) {
			moveLeftAll.setDisabled(true);
			moveLeft.setDisabled(true);
		} else {
			moveLeftAll.setDisabled(targetContainer.getAllStillExistData().isEmpty());
			moveLeft.setDisabled(targetContainer.getAllStillExistData().isEmpty());
		}
	}
	
	private Window getParentWindow() {
		Component parent = getParent();
		if(parent instanceof Window) {
			return (Window)parent;
		}
		for(;!(parent instanceof Window);) {
			parent = parent.getParent();
			if(parent instanceof Window) {
				return (Window)parent;
			}
		}
		return null;
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
