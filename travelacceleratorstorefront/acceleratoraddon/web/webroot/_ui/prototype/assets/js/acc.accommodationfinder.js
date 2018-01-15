ACC.accommodationfinder = {

	_autoloadTracc : [ 
		"bindAccommodationFinderValidation",
        "bindRoomQuantity"
	],

	componentParentSelector: '.y_accommodationFinderForm',

	bindAccommodationFinderValidation : function() {

		// Custom validator to handle validation for HTML input elements that have the 'required' attribute
		
		$(ACC.accommodationfinder.componentParentSelector).validate({
            errorElement: "span",
            errorClass: "fe-error",
            ignore: ".fe-dont-validate",
            submitHandler: function() { window.location.href = 'ph2-accommodation-search-default.html' },
            onfocusout: function(element) { $(element).valid(); },
            rules: {
                destination: "required",
                accommodationDatePickerCheckIn: "required",
                accommodationDatePickerCheckOut: "required"
            },
            messages: {
                destination: "Please enter a destination.",
                accommodationDatePickerCheckIn: "Please select a check in date.",
                accommodationDatePickerCheckOut: "Please select a check out date."
            }
        });


        $('#accommodationDatePickerCheckIn').datepicker({ 
            onClose: function( selectedDate ) {
                $('#accommodationDatePickerCheckIn').datepicker( "option", "minDate", selectedDate );
                $(this).valid();
          }
        });
        $('#accommodationDatePickerCheckOut').datepicker({ 
            onClose: function( selectedDate ) {
                $(this).valid();
          }
        });
		
	},

    bindRoomQuantity : function() {
        var roomQuantitySelect = '.y_accommodationRoomQuantity',
        roomId = '#room',
        hide = 'hidden';
        $(roomQuantitySelect).change(function() {
            var roomQuantity = $(this).val(),
            i;
            for (i = 1; i <= roomQuantity; i++) {
                $(roomId+'\\['+i+'\\]').removeClass(hide);
            };
            $(roomId+'\\['+(i-1)+'\\]').nextUntil('.age-info').addClass(hide);
        });
    }

}
