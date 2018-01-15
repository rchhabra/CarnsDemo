/**
 * Module for the currency selector.
 * @namespace
 */
 ACC.langcurrency = {

	_autoloadTracc : [
		"bindLangCurrencySelector"
	],

	/**
	 * Bind the keyboard and mouse actions to the currency selector
	 */
	bindLangCurrencySelector: function (){
		// Currency dropdown in the header
		$('.y_currencySelector').ddslick({
		    width: 50,
		    showSelectedHTML: false
		});
		$("#currency-selector").on({
		    keydown: function(e) {
		        // Enter key = submit the selected currency
                if ((e.keyCode || e.which) == 13) {
                    $('#y_currencyForm').submit();
                }
		    },
		    click: function() {
		        $('#y_currencyForm').submit();
		    }
		}, "a.dd-option");
	}

};