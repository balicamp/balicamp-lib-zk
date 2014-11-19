/**
 * 
 */

var bc = [];

var _db$init = new PouchDB("bc_core");

zAu.cmd0.loadBandboxData=function(data) {
	var bData = jq.evalJSON(data);
	var dbName = bData.className;
	var bId = bData.id;
	var filter = bData.filter;
	var list = bData.list;
	var isInit = bData.isInit;
	var db = new PouchDB(dbName);
	var listbox = bc[bId].firstChild.firstChild;
	
	try {
		db.bulkDocs(list, function(err,resp){});
	} catch(e){console.log(e);}
	
	db.changes().on('complete',function(resp){
		setTimeout("afterInsert('"+ dbName +"', '"+ bId +"','" + filter + "'," + isInit + "," + list.length +")", 10);
	});
	
};

zAu.cmd0.loadComboboxData=function(data) {
	var bData = jq.evalJSON(data);
	var dbName = bData.className;
	var bId = bData.id;
	var filter = bData.filter;
	var list = bData.list;
	var isLov = bData.isLov;
	var isInit = bData.isInit;
	var db = new PouchDB(dbName);
	
	try {
		db.bulkDocs(list, function(err,resp){});
	} catch(e){console.log(e);}
	
	db.changes().on('complete',function(resp){
		filter = (filter == null || filter == undefined) ? "" : filter;
		setTimeout("afterInsertComboitem('"+ dbName +"', '"+ bId +"','" + filter + "'," + isLov + "," + isInit + "," + list.length + ", 0)", 10);
	});	
};

function afterInsertComboitem(dbName, dbid, filter, isLov, isInit, _len, nrepeat) {
	var db = new PouchDB(dbName);
	var cmbbox =  bc[dbid];
	db.allDocs(
			{include_docs: true},
			function(err, docs) {
				if((docs.total_rows < _len) && (nrepeat < 10)) {
					populateLookupCombo(dbName, dbid, filter, isLov, isInit, _len, nrepeat++); //try again 10 time
				} else if(docs.total_rows > 0) {
					cmbbox.clear();
					if((isLov != true) && (isInit != true)){
						cmbbox.close();
					}
					var val = cmbbox.getValue();
					var sel = null;
					for(i=0;i<docs.total_rows;i++) {
						cmbbox.appendChild(new zul.inp.Comboitem(docs.rows[i].doc));
						if($isFound(docs.rows[i].doc, val)) {
							sel = docs.rows[i].doc;
						}
					}
					if(sel != null) {
						cmbbox.setValue(sel.label);
					}
					if((isLov != true) && (isInit != true)){ 
						cmbbox.open();
					}
				}
				stopTimer(cmbbox);
			}
	);
}

function populateLookupCombo(dbName, dbid, filter, isLov, isInit, _len, nrepeat) {
	setTimeout("afterInsertComboitem('"+ dbName +"', '"+ dbid +"','" + filter + "'," + isLov + "," + isInit + "," + _len + "," + nrepeat + ")", 10);	
}

function populateListOfValueCombo(dbName, dbid, filter, isInit, _len) {
	setTimeout("afterInsert('"+ dbName +"', '"+ dbid +"','" + filter + "'," + isInit + "," + _len +")", 10);
}

function $isFound(doc, filter) {
	if(filter == null || filter == undefined || filter == "") return false;	
	var val = doc.value.toLowerCase();
	var lbl = doc.label.toLowerCase();
	return ((val.indexOf(filter.toLowerCase()) >= 0) || (lbl.indexOf(filter.toLowerCase()) >= 0));
}

function afterInsert(dbName, dbid, filter, isInit, _len) {
	var db = new PouchDB(dbName);
	var listbox = bc[dbid].firstChild.firstChild;
	db.allDocs(
			{include_docs: true},
			function(err, docs) {
				if(docs.total_rows < _len) { //try again
					populateListOfValueCombo(dbName, dbid, filter, isInit, _len);
				} else if(docs.total_rows > 0) {
					listbox.clear();
					if((isLov != true) && (isInit != true)){
						bc[dbid].close();
					}
					var val = bc[dbid].getValue();
					var sel = null;
					for(i=0;i<docs.total_rows;i++) {
						listbox.appendChild(new zul.sel.Listitem(docs.rows[i].doc));
						if($isFound(docs.rows[i].doc, val)) {
							sel = docs.rows[i].doc;
						}
					}
					if(sel != null) {
						bc[dbid].setValue(sel.label);
					}
					if((isLov != true) && (isInit != true)){ 
						bc[dbid].open();
					}
				}
				stopTimer(bc[dbid]);
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
	
	syncLOVDb(combo.getId(), dbName);

}

function stopTimer(combo) {
	if(cWindow.lcmb == null || cWindow.lcmb == undefined) return;
	if(combo == cWindow.lcmb) {
		zAu.send(new zk.Event(cWindow,"onStopTimer",{comboId: combo.getId()},{toServer:true}));
	}
}

function loadCombo(combo, dbName, isCmbbox, isOnDeman) {
	bc[combo.getId()] = combo;
	var db = new PouchDB(dbName);
	var filter = combo.getValue();
	if((filter == null || filter == "") && (isOnDeman == true)) return;
	filter = (filter == null) ? "" : filter;
	db.query(
		function(doc, emit) {
			var lwVal = doc.value.toLowerCase();
			var lwLbl = doc.label.toLowerCase();
			if(doc.value == filter) {
				combo.setValue(doc.label);
			}
			if((filter == null) || (filter == "") || (isOnDeman == false)) {
				if(isCmbbox != true) {
					listComp = combo.firstChild.firstChild;
					listComp.appendChild(new zul.sel.Listitem(doc));
				} else {
					combo.appendChild(new zul.inp.Comboitem(doc));
				}
				emit(doc);
			} else if ((lwVal.indexOf(filter.toLowerCase()) >= 0) || (lwLbl.indexOf(filter.toLowerCase()) >= 0)){
				if(isCmbbox != true) {
					listComp = combo.firstChild.firstChild;
					listComp.appendChild(new zul.sel.Listitem(doc));
				} else {
					combo.appendChild(new zul.inp.Comboitem(doc));
				}
				emit(doc);
			}
		},
		function(err,resp) {
			if(resp.total_rows > 0) {
				stopTimer(combo);
			} else {
				zAu.send(new zk.Event(combo,"onFill",{value: filter, isInit: true},{toServer:true}));
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
			if(bandbox.oldVal != event.value) {
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

function populateLOVfromCache(combo, dbName) {
	var db = new PouchDB(dbName);	
	db.allDocs(
		{include_docs: true},
		function(err, docs){
			/*console.log(docs);*/
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
			}
			stopTimer(combo);
		}
	);
}

function syncLOVDb(cmbId, dbName) {
	var combo = bc[cmbId];
	var d = _db$init.get(dbName).then(
		function(doc){
			var dt = new Date();
			var cr = dt.getTime() - doc.created;
			if(cr >= (3600 * 24 * 1000)) {
				console.log("Sync DB...");
				_db$init.put(
					{created:dt.getTime(), 
						name:dbName, 
						_id:dbName,
						_rev: doc._rev},
						function(err,resp){
							if(!err) {
								zAu.send(new zk.Event(combo,"onFill",{headerId: dbName},{toServer:true}));
							}
						});
			} else {
				populateLOVfromCache(combo, dbName);
			}
		}
	).catch(function(err){
		var dt = new Date();
		console.log("Sync New DB...");
		_db$init.put({
				created: dt.getTime(), 
				name: dbName,
				_id: dbName
				}
		).then(
			function(resp){
				zAu.send(new zk.Event(combo,"onFill",{headerId: dbName},{toServer:true}));
			}
		).catch(
			function(err){}
		);
	});	
}
