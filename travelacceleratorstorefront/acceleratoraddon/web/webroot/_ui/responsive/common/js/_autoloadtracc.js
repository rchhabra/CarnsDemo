/**
* Run the autoload function only for travel accelerator Javascript files by running any function names in the array _autoloadTracc
* e.g. acc.travelcommon.js has the object ACC.travelcommon._autoloadTracc: ['someFunctionName']
* then this autoloader will run: someFunctionName();
*/
function _autoloadTracc(){
	$.each(ACC,function(section,obj){
		if($.isArray(obj._autoloadTracc)){
			$.each(obj._autoloadTracc,function(key,value){
				if($.isArray(value)){
					if(value[1]){
						ACC[section][value[0]]();
					}else{
						if(value[2]){
							ACC[section][value[2]]();
						}
					}
				}else{
					ACC[section][value]();
				}
			});
		}
	});
}

$(function(){
	_autoloadTracc();
});