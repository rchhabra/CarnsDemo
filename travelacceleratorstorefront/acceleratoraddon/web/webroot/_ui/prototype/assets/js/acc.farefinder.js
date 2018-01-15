ACC.farefinder = {

	_autoloadTracc : [ 
		"bindChangeTripTypeButton",
		"bindTravelWithChildrenButton",
		"bindFareFinderValidation",
		"bindOriginLocationAutosuggest"
	],

	componentParentSelector: '.y_fareFinderForm',

	bindChangeTripTypeButton : function() {
		var $returnField = $(ACC.farefinder.componentParentSelector+' .y_fareFinderReturnField');
		// Event handler for trip type radio buttons
		// Radio button show/hide
		$('.y_fareFinderOneWayBtn').on('click', function() {
			// apply changes to the current return field
		    $returnField.hide("fast");
		    $returnField.find("input").rules("remove", "required");
		});
		$('.y_fareFinderRoundTripBtn').on('click', function() {
		    $returnField.show("fast");
		    $returnField.find("input").rules("add", "required");
		});
	},
	bindTravelWithChildrenButton : function() {
		// Event handler for 'travelWithChildrenButton'

		$(".y_fareFinderChildrenTrigger").click(function() {
			$(this).hide();
			$(ACC.farefinder.componentParentSelector+" .y_fareFinderTravelingChildren").removeClass("hidden");
			$("#y_fareFinderTravelingWithChildren").val("true");
		});
	},

	bindFareFinderValidation : function() {
		// Custom validator to handle validation for HTML input elements that have the 'required' attribute
		
		$(ACC.farefinder.componentParentSelector).validate({
            errorElement: "span",
            errorClass: "fe-error",
            ignore: ".fe-dont-validate",
            submitHandler: function() { window.location.href = 'fare-selection.html' },
            onfocusout: function(element) { $(element).valid(); },
            //errorLabelContainer: "#messageBox ul",
            //wrapper: "li",
            rules: {
                fromLocation: "required",
                toLocation: "required",
                datePickerDeparting: "required",
                datePickerReturning: "required",
                flightClass: "required"
            },
            messages: {
                fromLocation: "Please enter a 'from' location.",
                toLocation: "Please enter a 'to' destination.",
                datePickerDeparting: "Please select a departure date.",
                datePickerReturning: "Please select a return date.",
                flightClass: "Please select class."
            }
        });


        $('#datePickerDeparting').datepicker({ 
            onClose: function( selectedDate ) {
                $( "#datePickerReturning" ).datepicker( "option", "minDate", selectedDate );
                $(this).valid();
          }
        });
        $('#datePickerReturning').datepicker({ 
            onClose: function( selectedDate ) {
                $(this).valid();
          }
        });
        $('#departureDate').datepicker({
             onClose: function( selectedDate ) {
                $(this).valid();
          }
        });
		
	},

	// Suggestions/autocompletion for Origin location
	bindOriginLocationAutosuggest : function() {
		$(".y_fareFinderOriginLocation").autosuggestion();
	}
}
