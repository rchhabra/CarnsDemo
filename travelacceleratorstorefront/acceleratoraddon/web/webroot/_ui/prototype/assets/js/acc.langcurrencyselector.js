ACC.langcurrency = {

	_autoloadTracc : [
		"bindLangCurrencySelector"
	],

	bindLangCurrencySelector: function (){
		// Currency dropdown in the header
		$('.y_currencySelector').ddslick({
		    width: 50,
		    showSelectedHTML: false
		});
		$("#currency-selector").on({
		    keydown: function(e) {
		        // Enter key = fill input field with the current suggestion
                if ((e.keyCode || e.which) == 13) {
                    // $('#y_currencyForm').submit();
                }
		    },
		    click: function() {
		        // $('#y_currencyForm').submit();
		    }
		}, "a.dd-option");
	}

};
