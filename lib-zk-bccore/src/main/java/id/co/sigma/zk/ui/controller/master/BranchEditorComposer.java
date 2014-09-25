package id.co.sigma.zk.ui.controller.master;

import id.co.sigma.common.security.domain.Branch;
import id.co.sigma.zk.ui.controller.ZKEditorState;
import id.co.sigma.zk.ui.controller.base.BaseSimpleDirectToDBEditor;

import org.zkoss.zhtml.Del;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Textbox;

/**
 * 
 * @author <a href="mailto:gusti.darmawan@sigma.co.id">Eka Darmawan</a>
 */
public class BranchEditorComposer extends BaseSimpleDirectToDBEditor<Branch> {
	static final org.slf4j.Logger logger = org.slf4j.LoggerFactory
			.getLogger(BranchEditorComposer.class.getName());
	
	@Wire
	Textbox branchCode;
	
	private boolean isAddNewState;
	
	public boolean isAddNewState() {
		return isAddNewState;
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		branchCode.setReadonly(getEditorState().equals(ZKEditorState.EDIT));
	}
	

	/*@Wire
	private Textbox txtKode;
	
	@Wire
	private Textbox txtNamaCabang;
	
	@Wire
	private Textbox txtAlamat;
	
	@Wire
	private Textbox txtKeterangan;
	
	@Wire
	private Button btnSave;
	
	@Wire
	private Button btnCancel;*/

	/*@Listen(value="onClick = #btnSave")
	public void onClickSave(){
		if(ZKEditorState.ADD_NEW.equals(getEditorState())){
			try {
				insertData();
				EditorManager.getInstance().closeCurrentEditorPanel();
				Messagebox.show("Sukses tambah data baru");
			} catch (Exception e) {
				logger.error( "" + e.getMessage() , e);
				Messagebox.show("Gagal input data page. error : " + e.getMessage(), "Gagal Tambah Data", Messagebox.OK, Messagebox.ERROR);
			
			}
		}else{
			try {
				updateData();
				EditorManager.getInstance().closeCurrentEditorPanel();
				Messagebox.show("Sukses update data");
			} catch (Exception e) {
				logger.error( "" + e.getMessage() , e);
				Messagebox.show("Gagal input data page. error : " + e.getMessage(), "Gagal Tambah Data", Messagebox.OK, Messagebox.ERROR);
			
			}
		}
	}*/
		
	
}