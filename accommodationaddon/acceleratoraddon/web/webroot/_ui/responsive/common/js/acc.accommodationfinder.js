ACC.accommodationfinder = {

	_autoloadTracc : [
	      "init"
	],

	componentParentSelector : '#y_accommodationFinderForm',

	 init: function () {
		 if($(ACC.accommodationfinder.componentParentSelector).is('form')){
			 $(ACC.accommodationfinder.componentParentSelector)[0].reset();
		 }
		 
	     ACC.accommodationfinder.populateCheckOutFieldInit();
	     ACC.accommodationfinder.calculateNumberOfNightsInit();
	     ACC.accommodationfinder.populateNumberOfNightsOnLoad();
	     ACC.accommodationfinder.bindAccommodationOfferingFinderValidation();
	     ACC.accommodationfinder.bindRoomQuantity();
	     ACC.accommodationfinder.bindDestinationAutosuggest();
	     ACC.accommodationfinder.bindAccommodationFinderLocationEvents();
	},

	bindAccommodationOfferingFinderValidation : function() {

		var $checkInDateField = $(".y_accommodationFinderDatePickerCheckIn");
		var $checkOutDateField = $(".y_accommodationFinderDatePickerCheckOut");

		$(ACC.accommodationfinder.componentParentSelector).validate({
			errorElement : "span",
			errorClass : "fe-error",
			submitHandler : function(form) {
				ACC.formvalidation.serverFormValidation(form, "validateAccommodationFinderForm", function (jsonData) {
					if (!jsonData.hasErrorFlag) {
						$('#y_processingModal').modal({
							backdrop: 'static',
							keyboard: false
						});
						form.submit();
					}
				});
			},
			onfocusout: function(element) {
				$(element).valid();
			},
			onkeyup: false,
			onclick: false,
            rules: {
            	destinationLocationName: {
            		required: {
            			 depends:function(){
            				 $(this).val($.trim($(this).val()));
            		            return true;
            			 }
            		},
            		minlength: 3,
                	destinationLocationIsValid : "#y_accommodationFinderLocation"
            	},
                checkInDateTime: "required",
                checkOutDateTime: "required"
            },
            messages: {
            	destinationLocationName: {
            		required: ACC.addons.travelacceleratorstorefront['error.accommodationfinder.destination.location'],
            		minlength: ACC.addons.travelacceleratorstorefront['error.accommodationfinder.destination.location.autosuggestion'],
            		destinationLocationIsValid: ACC.addons.travelacceleratorstorefront['error.accommodationfinder.destination.location.autosuggestion']
            	},
                checkInDateTime: ACC.addons.travelacceleratorstorefront['error.accommodationfinder.checkin.date'],
                checkOutDateTime: ACC.addons.travelacceleratorstorefront['error.accommodationfinder.checkout.date']
            }

		});

		// initialize date picker fields
		 ACC.accommodationfinder.addDatePickerForAccommodationFinder($checkInDateField, $checkOutDateField);

	},
	
	populateCheckOutFieldInit : function(){
		
		var $checkInDateField = $(".y_accommodationFinderDatePickerCheckIn");
		var $checkOutDateField = $(".y_accommodationFinderDatePickerCheckOut");
		
		$checkInDateField.on("change keyup", function() {
			var code;
		    if(!e){
		    	var e = window.event;
		    }
		    if(e.keyCode){
		    	code = e.keyCode; 
		    }
		    else if(e.which){
		    	code = e.which;	
		    }

		    if(code == 8 || code == 46 || code == 37 || code == 39 ||code == undefined){
		    	if(!$(this).val()){
		    		var numberOfNightsValue = $('.nights-placeholder-text');
		    		 $("#y_numberOfNights").text('0');
		    		 numberOfNightsValue.last().addClass('hidden');
					 numberOfNightsValue.first().removeClass('hidden');
		    	}
		    	return false;		    	
		    }
			ACC.accommodationfinder.populateCheckOutField($checkInDateField, $checkOutDateField);
			ACC.accommodationfinder.calculateNumberOfNights($checkInDateField, $checkOutDateField);
		});
		
	},

	calculateNumberOfNightsInit : function(){
		
		var $checkInDateField = $(".y_accommodationFinderDatePickerCheckIn");
		var $checkOutDateField = $(".y_accommodationFinderDatePickerCheckOut");
		
		$checkOutDateField.on("change keyup", function() {
			ACC.accommodationfinder.calculateNumberOfNights($checkInDateField, $checkOutDateField);
		});

	},

	populateNumberOfNightsOnLoad : function(){
    	if(!$(".y_accommodationFinderDatePickerCheckOut").val() && !$(".y_accommodationFinderDatePickerCheckIn").val()){
    		return;
    	}
    	ACC.accommodationfinder.populateNumberOfNightsOnListingPage($(".y_accommodationFinderDatePickerCheckIn"), $(".y_accommodationFinderDatePickerCheckOut"));
    },
    
    populateNumberOfNightsOnListingPage : function($checkInDateField, $checkOutDateField){
    	var checkOutDate = ACC.travelcommon.convertToUSDate($checkOutDateField.val());
		var checkInDate = ACC.travelcommon.convertToUSDate($checkInDateField.val());
		var numberOfNightsValue = $('.nights-placeholder-text');
		if(jQuery.type(checkOutDate)==='date' && jQuery.type(checkInDate)==='date'){
            var numberOfNights = Math.round((checkOutDate - checkInDate)/(1000*60*60*24));
            	
				if(numberOfNights<=0){
					var date2 = $checkInDateField.datepicker('getDate', '+1d');
					date2.setDate(date2.getDate()+1);
					$checkOutDateField.datepicker('setDate', date2);
					numberOfNights = 1;
				}
				if(isNaN(numberOfNights)){
            		numberOfNights=0;
            	}
				if(numberOfNights > 1){
					numberOfNightsValue.last().addClass('hidden');
					numberOfNightsValue.first().removeClass('hidden');
				} else if(numberOfNights <= 1) {
					numberOfNightsValue.last().removeClass('hidden');
					numberOfNightsValue.first().addClass('hidden');
				}
				$("#y_numberOfNights").text(numberOfNights);
			
		}
    },

    // Suggestions/autocompletion for Origin location
	bindDestinationAutosuggest: function () {
		if(!$(".y_accommodationFinderLocation" ).is('input')){
			return;
		}
		$(".y_accommodationFinderLocation").autosuggestion({

            autosuggestServiceHandler: function (locationText) {
                var suggestionSelect = "#y_accommodationFinderLocationSuggestions";
                // make AJAX call to get the suggestions
                $.when(ACC.services.getSuggestedAccommodationLocationsAjax(locationText)).then(
                    // success
                    function (data) {
                        $(suggestionSelect).html(data.htmlContent);
                        if (data.htmlContent) {
                            $(suggestionSelect).removeClass("hidden");
                        }
                    }
                );
            },
            suggestionFieldChangedCallback: function () {
            	$(".y_accommodationFinderLocation").valid();
            },
            attributes : ["Code", "SuggestionType", "Latitude", "Longitude", "Radius"]
        });
	},

	bindAccommodationFinderLocationEvents : function(){
		if(!$(".y_accommodationFinderLocation").is('input')){
			return;
		}
		
		$(".y_accommodationFinderLocation").keydown(function(e) {
			if((e.keyCode == 9 || ((e.keyCode || e.which) == 13)) && $(".y_accommodationFinderLocationCode").val()==''){
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
		
		$(".y_accommodationFinderLocation").blur(function (e) {
			$(".y_accommodationFinderLocation").valid();
		});
		
		$(".y_accommodationFinderLocation").focus(function (e) {
			$(this).select();
		});
	},

    bindRoomQuantity : function() {
	        roomId = '#room',
	        hide = 'hidden';
	        $('.y_accommodationRoomQuantity').change(function() {
	            var roomQuantity = $(this).val(),
	            i;
	            for (i = 1; i <= roomQuantity; i++) {
	                $(roomId+'\\['+i+'\\]').removeClass(hide);
	            };
                $(roomId+'\\['+(i-1)+'\\]').nextUntil('.y_roomStayCandidatesError', '.guest-types').addClass(hide);
	        });
	    },

	    addDatePickerForAccommodationFinder : function($checkInDateField, $checkOutDateField){

	    	var todayDate = new Date();
	        $checkInDateField.datepicker({
			    minDate: ACC.travelcommon.getTodayUKDate(),
			    maxDate: '+1y',
	            beforeShow: function (input) {

	            	setTimeout(function() {
	            		$checkInDateField.rules('remove')
	            		}, 0);

	            },
	            onClose: function (selectedDate) {
	                // add validation to check in date

	            		if($checkInDateField.val()){
	            			ACC.accommodationfinder.addCheckInDateFieldValidation($checkInDateField);
	            		}

	                // add validation to check out date, only if it's visible
					// and validate the current field
	                if ($checkOutDateField.is(":visible")) {
	                    if ($(this).valid()) {
	                    	var newDate = ACC.travelcommon.convertToUSDate(selectedDate);
	                    	$checkOutDateField.datepicker("option", "minDate", ACC.travelcommon.addDays(newDate, 1));
	                    	$checkOutDateField.datepicker("option", "maxDate", ACC.travelcommon.addDays(newDate, ACC.config.maxAllowedDateDifference));
	                        // change the validation of the 'check out date' to
							// have a minDate of the 'check in date'

	                    	setTimeout(function() {
	                    		ACC.accommodationfinder.addCheckOutDateFieldValidation($checkOutDateField, selectedDate)
	    	            		}, 0);

	                    }
	                    else {
	                    	$checkOutDateField.datepicker("option", "minDate", todayDate);
	                        // change the validation of the 'check out date' to
							// have a minDate as today's date

	                    	setTimeout(function() {
	                    		ACC.accommodationfinder.addCheckOutDateFieldValidation($checkOutDateField, ACC.travelcommon.getTodayUKDate())
	    	            		}, 0);

	                    }
	                }
	                else {
	                    $(this).valid();
	                }
	            }
	        });

		 $checkOutDateField.datepicker({
	            minDate: ACC.travelcommon.getTodayUKDate(),
	            maxDate: '+1y',
	            beforeShow: function (input) {
	            	var newDate;
	            	 if($checkInDateField.val() && jQuery.type($checkInDateField.datepicker('getDate'))==='date'){
	            		 	newDate = ACC.travelcommon.convertToUSDate($checkInDateField.val());
		            		$checkOutDateField.datepicker("option", "maxDate", ACC.travelcommon.addDays($checkInDateField.datepicker('getDate','+1d'), ACC.config.maxAllowedDateDifference));
		             }else{
		            	 newDate = todayDate;
		            	 $checkOutDateField.datepicker( "option", "maxDate", "+1y" );
		             }
	            	 $checkOutDateField.datepicker("option", "minDate", ACC.travelcommon.addDays(newDate, 1));
	            	setTimeout(function() {
	            		$checkOutDateField.rules('remove')
	            		}, 0);

	            },
	            onClose: function (selectedDate) {
	                if ($checkInDateField.valid()) {
	                	$checkOutDateField.datepicker("option", "minDate", $checkInDateField.val());
	                    // change the validation of the 'check out date' to have
						// a minDate of the 'check in date'

	                	setTimeout(function() {
	                		if($checkInDateField.val() && $checkOutDateField.val()){
	                			ACC.accommodationfinder.addCheckOutDateFieldValidation($checkOutDateField, $checkInDateField.val())
	                		}
		            		}, 0);

	                }
	                else {
	                	$checkOutDateField.datepicker("option", "minDate", todayDate);
	                    // change the validation of the 'check out date' to have
						// a minDate as today's date

	                	setTimeout(function() {
	                		ACC.accommodationfinder.addCheckOutDateFieldValidation($checkOutDateField, ACC.travelcommon.getTodayUKDate())
		            		}, 0);

	                }
	                $(this).valid();
	            }
	        });
	    },

	    addCheckInDateFieldValidation : function($checkInDateField) {
			var minDate = ACC.travelcommon.getTodayUKDate();

			$checkInDateField.rules("add",
							{
								dateUK : true,
								dateGreaterEqualTo : minDate,
								dateLessEqualTo : ACC.travelcommon
										.getHundredYearUKDate(),
							});
		},

		addCheckOutDateFieldValidation : function($checkOutDateField, selMinDate) {
			  var minDate = ACC.travelcommon.getTodayUKDate();

		        if (selMinDate) {
		            minDate = selMinDate;
		        }
		    minDate = ACC.travelcommon.addDays(ACC.travelcommon.convertToUSDate(minDate),1);
			$checkOutDateField.rules("add",
							{
								dateUK : true,
								dateGreaterEqualTo : minDate,
								dateLessEqualTo : ACC.travelcommon
										.getHundredYearUKDate(),
							});
		},

	    calculateNumberOfNights : function($checkInDateField, $checkOutDateField){
				var checkOutDate = $checkOutDateField.datepicker('getDate'),
					checkInDate = $checkInDateField.datepicker('getDate'),
					numberOfNightsValue = $('.nights-placeholder-text');

				if(jQuery.type(checkOutDate)==='date' && jQuery.type(checkInDate)==='date'){
					var numberOfNights = Math.round((checkOutDate - checkInDate)/(1000*60*60*24));
					var date2 = ACC.travelcommon.convertToUSDate($checkInDateField.val());
					if(date2==null || isNaN(date2.getTime()) || date2.getTime()<0){
						$("#y_numberOfNights").text("0");
						numberOfNightsValue.last().addClass('hidden');
						numberOfNightsValue.first().removeClass('hidden');
						return;
					}

					if(numberOfNights > 1){
						numberOfNightsValue.last().addClass('hidden');
						numberOfNightsValue.first().removeClass('hidden');
					} else if(numberOfNights <= 1) {
						numberOfNightsValue.last().removeClass('hidden');
						numberOfNightsValue.first().addClass('hidden');
					}

					if(numberOfNights<=0){
						date2.setDate(date2.getDate()+1);
						$checkOutDateField.datepicker('setDate', date2);
						numberOfNights = 1;
					}
					else if(numberOfNights>ACC.config.maxAllowedDateDifference){
						date2.setDate(date2.getDate()+ACC.config.maxAllowedDateDifference);
						$checkOutDateField.datepicker('setDate', date2);
						numberOfNights = ACC.config.maxAllowedDateDifference;
					}

					$("#y_numberOfNights").text(numberOfNights);
				}else{
					$("#y_numberOfNights").text("0");
					numberOfNightsValue.last().addClass('hidden');
					numberOfNightsValue.first().removeClass('hidden');
				}

		},

		populateCheckOutField : function($checkInDateField, $checkOutDateField){

			ACC.accommodationfinder.reInitializeCheckInDate($checkInDateField);
			
				if($checkOutDateField.datepicker('getDate') == null){
					var date2 = ACC.travelcommon.convertToUSDate($checkInDateField.val());
					if(date2==null || isNaN(date2.getTime()) || date2.getTime()<0){
						return;
					}
					date2.setDate(date2.getDate()+1);
					$checkOutDateField.datepicker('setDate', date2);
					$('label[id*="accommodationDatePickerCheckOut-error"]').text('');
					$("#y_numberOfNights").text("1");
				}else{
					var checkOutDate = $checkOutDateField.datepicker('getDate');
					var checkInDate = $checkInDateField.datepicker('getDate');
					if(jQuery.type(checkOutDate)==='date' && jQuery.type(checkInDate)==='date'){
						var numberOfNights = Math.round((checkOutDate - checkInDate)/(1000*60*60*24));
						if(numberOfNights<=0){
							var date2 = ACC.travelcommon.convertToUSDate($checkInDateField.val());
							if(date2==null || isNaN(date2.getTime()) || date2.getTime()<0){
								return;
							}
							date2.setDate(date2.getDate()+1);
							$checkOutDateField.val(ACC.travelcommon.convertToUKDate(date2));
							numberOfNights = 1;
						}
						$("#y_numberOfNights").text(numberOfNights);
					}
				}

		},

		reInitializeCheckInDate : function($checkInDateField){
			var currDate = ACC.travelcommon.getTodayUKDate();
			if(ACC.travelcommon.convertToUSDate($checkInDateField.val()).getTime() > 0){
				if($checkInDateField.val() && ACC.travelcommon.convertToUSDate($checkInDateField.val()).getTime() < ACC.travelcommon.convertToUSDate(currDate).getTime()){
					$checkInDateField.datepicker('setDate', ACC.travelcommon.convertToUSDate(currDate));
	    		}
			}
		}

};
