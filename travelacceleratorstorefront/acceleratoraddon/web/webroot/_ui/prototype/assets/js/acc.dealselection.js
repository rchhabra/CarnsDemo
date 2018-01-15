ACC.dealselection = {

	_autoloadTracc : [ 
		"bindDealDepartureDate"
	],

	componentParentSelector: '.y_dealSelectionSection',

	bindDealDepartureDate : function() {

        $('[id^="dealDatePickerDeparting"]').datepicker({ 
            onClose: function( selectedDate ) {
                $('[id^="dealDatePickerDeparting"]').datepicker( "option", "minDate", selectedDate );
                $(this).valid();
          }
        });
		
	}

}
