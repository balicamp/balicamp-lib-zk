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
	var db = new PouchDB(dbName);
	var listbox = bc[bId].firstChild.firstChild;
	for(_i=0;_i<list.length;_i++) {
		db.put(list[_i], list[_i].value, function(err,resp){});
	}
	
	db.changes().on('complete',function(resp){
		console.log(resp);
	});
	
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
				for(i=0;i<resp.total_rows;i++) {
					listbox.appendChild(new zul.sel.Listitem(resp.rows[i].key));
					var w = resp.rows[i].key.label.length * 14;
					if(w > width) {
						width = w;
					}
				}
				listbox.setWidth(width + 'px');
				bc[bId].open();
			} else {
				bc[bId].close();					
			}
		}
	);
};

zAu.cmd0.loadComboboxData=function(data) {
	
};

function queryData(dbName, filter, bandbox, event) {
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
			}
		}
	);
	return found;
}

function listFocus(event, bandbox) {
	if(bandbox.isOpen()) {
		event.stop({propagation:true, dom:true});
		var listbox = bandbox.firstChild.firstChild;
		listbox.focus();
		var selIdx = listbox.getSelectedIndex();
		if(selIdx >= 0) {
			listbox.setSelectedIndex(selIdx);
		} else {
			listbox.setSelectedIndex(0);
		}
	}
}

function putSelectedValue(listbox) {
	var bDom = listbox.parent.parent;
	var holdCom = listbox.nextSibling;
	holdCom.setValue(listbox.getSelectedItem().getValue());
	holdCom.smartUpdate('value',listbox.getSelectedItem().getValue());
	bDom.setValue(listbox.getSelectedItem().getLabel());
	bDom.close();
}
