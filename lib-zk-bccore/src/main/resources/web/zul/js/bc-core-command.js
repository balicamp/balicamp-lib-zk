/**
 * 
 */

var bc = [];

var _db$init = new PouchDB("bc_core");

zAu.cmd0.loadBandboxData=function(data) {
	var bData = jq.evalJSON(data);
	console.log(bData);
	var dbName = bData.className;
	var bId = bData.id;
	var filter = bData.filter;
	var list = bData.list;
	var isInit = bData.isInit;
	var db = new PouchDB(dbName);
	var listbox = bc[bId].firstChild.firstChild;
	for(_i=0;_i<list.length;_i++) {
		db.put(list[_i], list[_i].value, function(err,resp){});
	}
	
	db.changes().on('complete',function(resp){
		console.log(resp);
		setTimeout("afterInsert('"+ dbName +"', '"+ bId +"','" + filter + "'," + isInit + ")", 10);
	});
	
};

zAu.cmd0.loadComboboxData=function(data) {
	var bData = jq.evalJSON(data);
	console.log(bData);
	var dbName = bData.className;
	var bId = bData.id;
	var filter = bData.filter;
	var list = bData.list;
	var isLov = bData.isLov;
	var isInit = bData.isInit;
	var db = new PouchDB(dbName);	
	for(_i=0;_i<list.length;_i++) {
		db.put(list[_i], list[_i].value, function(err,resp){});
	}	
	db.changes().on('complete',function(resp){
		console.log(resp);
		setTimeout("afterInsertComboitem('"+ dbName +"', '"+ bId +"','" + filter + "'," + isLov + "," + isInit + ")", 10);
	});	
};

function afterInsertComboitem(dbName, dbid, filter, isLov, isInit) {
	var db = new PouchDB(dbName);
	var cmbbox =  bc[dbid];
	db.query(
		function(doc, emit) {
			var lwVal = doc.value.toLowerCase();
			var lwLbl = doc.label.toLowerCase();
			if((lwVal.indexOf(filter.toLowerCase()) >= 0) || (lwLbl.indexOf(filter.toLowerCase()) >= 0)) {
				emit(doc);
			}
		},
		function(err,resp) {
			cmbbox.clear();
			if((isLov != true) && (isInit != true)){
				cmbbox.close();
			}
			if(resp.total_rows > 0) {
				var val = cmbbox.getValue();
				var sel = null;
				for(i=0;i<resp.total_rows;i++) {
					cmbbox.appendChild(new zul.inp.Comboitem(resp.rows[i].key));
					if(val == resp.rows[i].key.value) {
						sel = resp.rows[i].key;
					}
				}
				if(sel != null) {
					cmbbox.setValue(sel.label);
				}
			}
			if((isLov != true) && (isInit != true)){ 
				cmbbox.open();
			}
		}
	);
}

function afterInsert(dbName, dbid, filter, isInit) {
	console.log(dbName + ":" + dbid + ":" + filter);
	var db = new PouchDB(dbName);
	var listbox = bc[dbid].firstChild.firstChild;
	db.query(
		function(doc, emit) {
			var lwVal = doc.value.toLowerCase();
			var lwLbl = doc.label.toLowerCase();
			if((lwVal.indexOf(filter.toLowerCase()) >= 0) || (lwLbl.indexOf(filter.toLowerCase()) >= 0)) {
				emit(doc);
			}
		},
		function(err,resp) {
			console.log(resp);
			if(resp.total_rows > 0) {
				listbox.clear();
				var width = 1;
				var sel = null;
				for(i=0;i<resp.total_rows;i++) {
					listbox.appendChild(new zul.sel.Listitem(resp.rows[i].key));
					var w = resp.rows[i].key.label.length * 14;
					if(w > width) {
						width = w;
					}
					if(bc[dbid].getValue() == resp.rows[i].key.value) {
						sel = resp.rows[i].key;
					}
				}
				listbox.setWidth(width + 'px');
				if(isInit != true) {
					bc[dbid].open();
				} else if(sel != null) {
					bc[dbid].setValue(sel.label);
				}
			} else {
				bc[dbid].close();					
			}
		}
	);
}

function onChangingCombobox(dbName, cmbbox, event) {
	if(event.value.length < 3) {
		event.stop();
	} else {
		event.stop({au:true});
		var db = new PouchDB(dbName);
		var filter = event.value;
		bc[cmbbox.getId()] = cmbbox;
		db.query(
			function(doc, emit) {
				var lwVal = doc.value.toLowerCase();
				var lwLbl = doc.label.toLowerCase();
				if((lwVal.indexOf(filter.toLowerCase()) >= 0) || (lwLbl.indexOf(filter.toLowerCase()) >= 0)) {
					emit(doc);
				}
			},
			function(err,resp) {
				cmbbox.clear();
				if(resp.total_rows > 0) {
					cmbbox.close();
					for(i=0;i<resp.total_rows;i++) {
						cmbbox.appendChild(new zul.inp.Comboitem(resp.rows[i].key));
					}
					if(resp.total_rows == 1) {
						cmbbox.smartUpdate('value',resp.rows[0].key.value);
					}
					cmbbox.open();
				} else{
					zAu.send(new zk.Event(cmbbox,"onChanging",{value: event.value},{toServer:true}));
				}
			}
		);
	}
}

function loadLOVCombo(combo, dbName) {
	bc[combo.getId()] = combo;
	var db = new PouchDB(dbName);
	db.allDocs(
		{include_docs: true},
		function(err, docs){
			console.log(docs);
			if(docs.total_rows > 0) {
				var val = combo.getValue();
				var sel = null;
				for(i=0;i<docs.total_rows;i++) {
					combo.appendChild(new zul.inp.Comboitem(docs.rows[i].doc));
					if(val == docs.rows[i].doc.value) {
						sel = docs.rows[i].doc;
					}
				}
				if(sel != null) {
					combo.setValue(sel.label);
				}
			} else {
				zAu.send(new zk.Event(combo,"onFill",{headerId: dbName},{toServer:true}));
			}
		}
	);
}

function loadCombo(combo, dbName, isCmbbox) {
	bc[combo.getId()] = combo;
	var db = new PouchDB(dbName);
	var filter = combo.getValue();
	if(filter == null || filter == "") return;
	db.query(
		function(doc, emit) {
			var lwVal = doc.value.toLowerCase();
			var lwLbl = doc.label.toLowerCase();
			if((lwVal.indexOf(filter.toLowerCase()) >= 0) || (lwLbl.indexOf(filter.toLowerCase()) >= 0)) {
				emit(doc);
			}
		},
		function(err,resp) {
			if(resp.total_rows > 0) {
				var sel = null;
				for(i=0;i<resp.total_rows;i++) {
					var d = resp.rows[i].key;
					if(isCmbbox != true) { /*bandbox*/
						/* bandbox > bandpopup > listbox */
						listComp = combo.firstChild.firstChild;
						listComp.appendChild(new zul.sel.Listitem(d));
					} else {/*combobox*/
						combo.appendChild(new zul.inp.Comboitem(d));
					}
					if(d.value == filter) {
						sel = d;
					}
				}
				if(sel != null) {
					combo.setValue(sel.label);
				}
			} else {
				zAu.send(new zk.Event(combo,"onChanging",{value: filter},{toServer:true}));
			}
		}
	);
}

function onChangingBandbox(dbName, filter, bandbox, event) {
	var db = new PouchDB(dbName);
	var found = false;
	var listbox = bandbox.firstChild.firstChild;
	event.stop({au:true});
	db.query(
		function(doc, emit) {
			var lwVal = doc.value.toLowerCase();
			var lwLbl = doc.label.toLowerCase();
			if((lwVal.indexOf(filter.toLowerCase()) >= 0) || (lwLbl.indexOf(filter.toLowerCase()) >= 0)) {
				emit(doc);
			}
		},
		function(err,resp) {
			console.log(resp);
			if(bandbox.oldVal != event.value) {
				console.log(event.value);
				bandbox.oldVal = event.value;
				if(resp.total_rows > 0) {
					listbox.clear();
					var width = 1;
					for(i=0;i<resp.total_rows;i++) {
						listbox.appendChild(new zul.sel.Listitem(resp.rows[i].key));
						var w = resp.rows[i].key.label.length * 14;
						if(w > width) {
							width = w;
						}
					}
					listbox.setWidth(width + 'px');
					bandbox.open();
				} else {
					bandbox.close();					
					zAu.send(new zk.Event(bandbox,"onChanging",{value: event.value},{toServer:true}));
					console.log("fire: " + event.value);
				}
			} else {
				bandbox.open();
			}
		}
	);
	return found;
}

function listFocus(event, bandbox) {
	event.stop({propagation:true, dom:true});
	if(bandbox.isOpen()) {		
		var listbox = bandbox.firstChild.firstChild.firstChild;
		listbox.focus(50);
	} else {
		var listbox = bandbox.firstChild.firstChild;
		if(listbox.nChildren > 0) {
			bandbox.open();
			listbox.firstChild.focus(50);
		}
	}
}

function putSelectedValue(listbox, isOnSelect) {
	var bDom = listbox.parent.parent;
	var holdCom = listbox.nextSibling;
	if(holdCom != null) {
		holdCom.setValue(listbox.getSelectedItem().getValue());
		holdCom.smartUpdate('value', listbox.getSelectedItem().getValue());
	} else {
		bDom.smartUpdate('value', listbox.getSelectedItem().getValue());
	}
	bDom.setValue(listbox.getSelectedItem().getLabel());
	if((isOnSelect == undefined) || (isOnSelect == false)) {
		bDom.close();
	}
}
