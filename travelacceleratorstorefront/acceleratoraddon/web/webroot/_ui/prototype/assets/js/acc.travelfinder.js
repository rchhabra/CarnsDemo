ACC.travelfinder = {

	_autoloadTracc : [ 
        "bindChangeTripTypeButton",
		"bindTravelFinderValidation",
        "bindRoomQuantity"
	],

	componentParentSelector: '.y_travelFinderForm',

    bindChangeTripTypeButton : function() {
        var $returnField = $(ACC.travelfinder.componentParentSelector+' .y_travelFinderReturnField');
        // Event handler for trip type radio buttons
        // Radio button show/hide
        $('.y_travelFinderOneWayBtn').on('click', function() {
            // apply changes to the current return field
            $returnField.hide("fast");
            $returnField.find("input").rules("remove", "required");
        });
        $('.y_travelFinderRoundTripBtn').on('click', function() {
            $returnField.show("fast");
            $returnField.find("input").rules("add", "required");
        });
    },

	bindTravelFinderValidation : function() {

		// Custom validator to handle validation for HTML input elements that have the 'required' attribute
		
		$(ACC.travelfinder.componentParentSelector).validate({
            errorElement: "span",
            errorClass: "fe-error",
            ignore: ".fe-dont-validate",
            submitHandler: function() { window.location.href = 'ph2-landing-travel.html' },
            onfocusout: function(element) { $(element).valid(); },
            rules: {
                travelAccommodationFromLocation: "required",
                travelAccommodationToLocation: "required",
                travelAccommodationDatePickerDeparting: "required",
                travelAccommodationDatePickerReturning: "required",
                travelAccommodationDatePickerCheckIn: "required",
                travelAccommodationDatePickerCheckOut: "required"
            },
            messages: {
                travelAccommodationFromLocation: "Please enter a 'from' location.",
                travelAccommodationToLocation: "Please enter a 'to' destination.",
                travelAccommodationDatePickerDeparting: "Please select a check in date.",
                travelAccommodationDatePickerReturning: "Please select a check out date.",
                travelAccommodationDatePickerCheckIn: "Please select a check in date.",
                travelAccommodationDatePickerCheckOut: "Please select a check out date."
            }
        });

        $('#travelAccommodationDatePickerDeparting').datepicker({ 
            onClose: function( selectedDate ) {
                $( "#travelAccommodationDatePickerReturning" ).datepicker( "option", "minDate", selectedDate );
                $(this).valid();
          }
        });
        $('#travelAccommodationDatePickerReturning').datepicker({ 
            onClose: function( selectedDate ) {
                $(this).valid();
          }
        });


        $('#travelAccommodationDatePickerCheckIn').datepicker({ 
            onClose: function( selectedDate ) {
                $('#travelAccommodationDatePickerCheckIn').datepicker( "option", "minDate", selectedDate );
                $(this).valid();
          }
        });
        $('#travelAccommodationDatePickerCheckOut').datepicker({ 
            onClose: function( selectedDate ) {
                $(this).valid();
          }
        });
		
	},

    bindRoomQuantity : function() {
        var roomQuantitySelect = '.y_travelAccommodationRoomQuantity',
        roomId = '#travelRoom',
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
