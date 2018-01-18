ACC.farefinder = {
    _autoloadTracc: [
        "init"
    ],

    componentParentSelector: '#y_fareFinderForm',
    minRelativeReturnDate:0,

    init: function () {
        // Sets the passenger quantity hidden value to the actualy value that is displaying to user. This is a fix for browser back button.
        var $passengerQuantityArray = $(".y_fareFinderPassengerQuantity");
        $passengerQuantityArray.each(function () {
            $(this).attr('value', parseInt($(this).parent().find('.y_fareFinderPassengerQuantitySpan').text()));
        });
        
        ACC.farefinder.bindChangeTripTypeButton();
        ACC.farefinder.bindTravelWithChildrenButton();
        ACC.farefinder.bindFareFinderValidation();
        ACC.farefinder.bindFareFinderLocationEvents();
        ACC.farefinder.bindOriginLocationAutosuggest();
        ACC.farefinder.bindDestinationLocationAutosuggest();
    },

    bindChangeTripTypeButton: function () {
        var $returnField = $(ACC.farefinder.componentParentSelector + ' .y_fareFinderReturnField');
        // Event handler for trip type radio buttons
        // Radio button show/hide
        $('#y_oneWayRadbtn').on('click', function () {
            // apply changes to the current return field
        	ACC.farefinder.hideReturnField();
        });
        $('#y_roundTripRadbtn').on('click', function () {
        	ACC.farefinder.showReturnField();
            ACC.farefinder.addReturnFieldValidation($(".y_fareFinderDatePickerReturning"), $(".y_fareFinderDatePickerDeparting").val());
        });
    },

    bindTravelWithChildrenButton: function () {
        // Event handler for 'travelWithChildrenButton'
        $(".y_fareFinderChildrenTrigger").click(function () {
            $(this).hide();
            $(ACC.farefinder.componentParentSelector + " .y_fareFinderTravelingChildren").removeClass("hidden");
            $("#y_fareFinderTravelingWithChildren").val("true");
        });
    },

    bindFareFinderValidation: function () {
        var $returnDateField = $(".y_fareFinderDatePickerReturning");
        var $departureDateField = $(".y_fareFinderDatePickerDeparting");


        $(ACC.farefinder.componentParentSelector).validate({
            errorElement: "span",
            errorClass: "fe-error",
            ignore: ".fe-dont-validate",
            submitHandler: function (form) {
                ACC.formvalidation.serverFormValidation(form, "validateFareFinderFormAttributes", function (jsonData) {
                    if (!jsonData.hasErrorFlag) {
                        $('#y_processingModal').modal({
                            backdrop: 'static',
                            keyboard: false
                        });
                        form.submit();
                    }
                });
            },
            onfocusout: function (element) {
                $(element).valid();
            },
            rules: {
                departureLocationName: {
                    required: {
	           			 depends:function(){
	        				 $(this).val($.trim($(this).val()));
	        		            return true;
	        			 }
	        		},
                    locationIsValid : ".y_originLocationCode"
                },
                arrivalLocationName: {
                    required: {
	           			 depends:function(){
	        				 $(this).val($.trim($(this).val()));
	        		            return true;
	        			 }
	        		},
                    locationIsValid : ".y_destinationLocationCode"
                },
                cabinClass: "required"
            },
            messages: {
                departureLocationName: {
                    required: ACC.addons.travelacceleratorstorefront['error.farefinder.from.location'],
                    locationIsValid: ACC.addons.travelacceleratorstorefront['error.farefinder.from.locationExists']
                },
                arrivalLocationName: {
                    required: ACC.addons.travelacceleratorstorefront['error.farefinder.to.location'],
                    locationIsValid: ACC.addons.travelacceleratorstorefront['error.farefinder.to.locationExists']
                },
                cabinClass: ACC.addons.travelacceleratorstorefront['error.farefinder.cabin.class']
            }
        });
        // add validation to departure date
        if ($departureDateField && $departureDateField.length > 0) {
            ACC.farefinder.addDepartureFieldValidation($departureDateField);
        }

        // if it's a "round trip", we need to validate the return date field
        if ($('#y_roundTripRadbtn').is(":checked")) {
            ACC.farefinder.addReturnFieldValidation($returnDateField, $departureDateField.val());
        }

        // initialize date picker fields
        ACC.farefinder.addDatePickerForFareFinder($departureDateField, $returnDateField);
    },

    bindFareFinderLocationEvents: function () {
        // validation events bind for y_originLocation field
		$(".y_originLocation").keydown(function(e) {
			if((e.keyCode == 9 || ((e.keyCode || e.which) == 13)) && $(".y_originLocationCode").val()==''){
				if((e.keyCode || e.which) == 13){
					e.preventDefault();
				}

				//select the first suggestion
				var suggestions = $(this).parent().find(".autocomplete-suggestions");
				if( $(this).val().length >= 3 && suggestions && suggestions.length != 0 ) {
					var firstSuggestion = suggestions.find("a").first();
					firstSuggestion.click();
				}
				$(this).valid();

			}
		});

		$(".y_originLocation").autosuggestion({
            suggestionFieldChangedCallback: function () {
            	$(".y_originLocation").valid();
            }
        });

		$(".y_originLocation").blur(function (e) {
			$(".y_originLocation").valid();
		});

		$(".y_originLocation").focus(function (e) {
			$(this).select();
		});

		// validation events bind for y_destinationLocation field
		$(".y_destinationLocation").keydown(function(e) {
			if((e.keyCode == 9 || ((e.keyCode || e.which) == 13)) && $(".y_destinationLocationCode").val()==''){
				if((e.keyCode || e.which) == 13){
					e.preventDefault();
				}

				//select the first suggestion
				var suggestions = $(this).parent().find(".autocomplete-suggestions");
				if( $(this).val().length >= 3 && suggestions && suggestions.length != 0 ) {
					var firstSuggestion = suggestions.find("a").first();
					firstSuggestion.click();
				}
				$(this).valid();

			};
		});

		$(".y_destinationLocation").autosuggestion({
            suggestionFieldChangedCallback: function () {
            	$(".y_destinationLocation").valid();
            }
        });

		$(".y_destinationLocation").blur(function (e) {
			$(".y_destinationLocation").valid();
		});

		$(".y_destinationLocation").focus(function (e) {
			$(this).select();
		});
    },

    addDatePickerForFareFinder : function($departureDateField, $returnDateField){

         var todayDate = new Date();
         $departureDateField.datepicker({
                minDate: todayDate,
                maxDate: '+1y',
                beforeShow: function (input) {

                    setTimeout(function() {
                        if($returnDateField.length){
                            $departureDateField.rules('remove')
                        }

                        }, 0);
                },
                onClose: function (selectedDate) {
                    // add validation to departure date

                    setTimeout(function() {
                         ACC.farefinder.addDepartureFieldValidation($departureDateField)
                        }, 0);
                    // add validation to return date, only if it's visible and
                    // validate the current field
                    if ($returnDateField.is(":visible")) {
                        if ($(this).valid()) {
                        	 ACC.farefinder.setReturnFieldMinDate($returnDateField,selectedDate,true);
                            // change the validation of the 'return date' to
                            // have a minDate of the 'from date'

                            setTimeout(function() {
                                 ACC.farefinder.addReturnFieldValidation($returnDateField, selectedDate)
                                }, 0);
                        }
                        else {
                        	ACC.farefinder.setReturnFieldMinDate($returnDateField,todayDate,false);
                            // change the validation of the 'return date' to
                            // have a minDate of the 'from date'

                            setTimeout(function() {
                                 ACC.farefinder.addReturnFieldValidation($returnDateField, ACC.travelcommon.getTodayUKDate())
                                }, 0);

                        }
                    }
                    else {
                        $(this).valid();
                    }
                }
            });

        $returnDateField.datepicker({
            minDate: todayDate,
            maxDate: '+1y',
            beforeShow: function (input) {

                setTimeout(function() {
                    $returnDateField.rules('remove')
                    }, 0);
            },
            onClose: function (selectedDate) {
                if ($departureDateField.valid()) {
                	ACC.farefinder.setReturnFieldMinDate($returnDateField,$departureDateField.val(),true); 
                    // change the validation of the 'return date' to have a
                    // minDate of the 'departure date'

                    setTimeout(function() {
                         ACC.farefinder.addReturnFieldValidation($returnDateField, $departureDateField.val())
                        }, 0);

                }
                else {
                	ACC.farefinder.setReturnFieldMinDate($returnDateField,todayDate,false); 
                    // change the validation of the 'return date' to have a
                    // minDate of the 'from date'

                    setTimeout(function() {
                        ACC.farefinder.addReturnFieldValidation($returnDateField, ACC.travelcommon.getTodayUKDate())
                        }, 0);

                }
                $(this).valid();
            }
        });

    },

    addDepartureFieldValidation: function ($departureDateField) {
        var minDateUK = ACC.travelcommon.getTodayUKDate();

        $departureDateField.rules("add", {
            required: true,
            dateUK: true,
            dateGreaterEqualTo: minDateUK,
            dateLessEqualTo: ACC.travelcommon.getHundredYearUKDate(),
            messages: {
                required: ACC.addons.travelacceleratorstorefront['error.farefinder.departure.date']
            }
        });
    },
    
    setReturnFieldMinDate:function($returnDateField, selectedDate,isUKDate){
    	var selectedUSDate=new Date();
    	if(selectedDate){
    		 selectedUSDate=selectedDate;
    		 if(isUKDate){
    			 selectedUSDate=ACC.travelcommon.convertToUSDate(selectedDate); 
    		 }
    	}
    	 var minDateForReturnDate=ACC.travelcommon.addDaysToUsDate(selectedUSDate,ACC.farefinder.minRelativeReturnDate); 
    	 $returnDateField.datepicker("option", "minDate", minDateForReturnDate); 
    },

    addReturnFieldValidation: function ($returnDateField, selectedMinDate) {
        var minDateUK = ACC.travelcommon.getTodayUKDate();
        if (selectedMinDate) {
            minDateUK = selectedMinDate;
        }
		var departure_Date = $('.y_transportDepartDate').val();
		var edit_Date = departure_Date.split("/");
		var date = edit_Date[0];
		var month = edit_Date[1];

		if (date > 15) {
			date = date - 5;
			month = parseInt(month);
			month = (month + 1);
			edit_Date[0] = date.toString();
			edit_Date[1] = month.toString();
		} 
		else {
			date = parseInt(date);
			date = (date + 15);
			month = parseInt(month);
			edit_Date[0] = date.toString();
			edit_Date[1] = month.toString();
		}
		
		var return_Date = edit_Date[0].toString() + "/0" + edit_Date[1]
				+ "/" + edit_Date[2];
		
        document.getElementById('returnDateTime').value = return_Date;
        if(!$returnDateField.length){
            return;
        }
        var selectedUSDate=ACC.travelcommon.convertToUSDate(minDateUK);
    	var minDateForReturnDate=ACC.travelcommon.addDays(selectedUSDate,ACC.farefinder.minRelativeReturnDate);

        $returnDateField.rules("add", {
            required: true,
            dateUK: true,
            dateGreaterEqualTo: minDateForReturnDate,
            dateLessEqualTo: ACC.travelcommon.getHundredYearUKDate(),
            messages: {
                required: ACC.addons.travelacceleratorstorefront['error.farefinder.arrival.date'],
                dateGreaterEqualTo: ACC.addons.travelacceleratorstorefront['error.farefinder.arrival.dateGreaterEqualTo']
            }
        });
    },
    
    hideReturnField : function(){
   	 var $returnField = $('.y_fareFinderReturnField');
   	 $returnField.hide("fast");
        $returnField.find("input").rules("remove");
   },

   showReturnField : function(){
   	 var $returnField = $('.y_fareFinderReturnField');
   	 $returnField.show("fast");
   },

    // Suggestions/autocompletion for Origin location
    bindOriginLocationAutosuggest: function () {
        $(".y_originLocation").autosuggestion({
            inputToClear: ".y_destinationLocation",
            autosuggestServiceHandler: function (locationText) {
                var suggestionSelect = "#y_originLocationSuggestions";
                // make AJAX call to get the suggestions
                $.when(ACC.services.getSuggestedOriginLocationsAjax(locationText)).then(
                    // success
                    function ( data ) {
                        $(suggestionSelect).html(data.htmlContent);
                        if (data.htmlContent) {
                            $(suggestionSelect).removeClass("hidden");
                        }
                    }
                );
            },
            suggestionFieldChangedCallback: function () {
                // clear the destination fields
                $(".y_destinationLocation").val("");
                $(".y_destinationLocationCode").val("");
            },
            attributes : ["Code", "SuggestionType"]
        });
    },

    // Suggestions/autocompletion for Destination location
    bindDestinationLocationAutosuggest: function () {
        $(".y_destinationLocation").autosuggestion({
            autosuggestServiceHandler: function (destinationText) {
                var suggestionSelect = "#y_destinationLocationSuggestions";
                var originCode = $(".y_originLocationCode").val();

                // make AJAX call to get the suggestions
                $.when(ACC.services.getSuggestedDestinationLocationsAjax(originCode, destinationText)).then(
                    // success
                    function (data) {
                        $(suggestionSelect).html(data.htmlContent);
                        if (data.htmlContent) {
                            $(suggestionSelect).removeClass("hidden");
                        } else {
                            var suggestionInput = ".y_destinationLocation";
                            var suggestionCode = ".y_destinationLocationCode";
                            $(suggestionCode).val($(suggestionInput).val());
                        }
                    }
                );
            },
            attributes : ["Code", "SuggestionType"]
        });
    }
};
