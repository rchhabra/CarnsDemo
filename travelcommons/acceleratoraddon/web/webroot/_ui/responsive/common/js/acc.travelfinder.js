ACC.travelfinder = {

	_autoloadTracc : [ 
	      "init"
	],
	
	componentParentSelector : '#y_travelFinderForm',
	
	init : function(){
		  ACC.travelfinder.bindRoomQuantity();
		  ACC.travelfinder.bindCheckInAndCheckOutFieldsWithHotelPartStay();
		  ACC.travelfinder.bindPartHotelStayWithTripType();
		  ACC.travelfinder.bindTravelFinderFormValidation();
		  ACC.travelfinder.bindValidationsOnCheckInAndCheckOut();
		  ACC.travelfinder.calculateNumberOfNights();
		  ACC.travelfinder.populateCheckInDateInOneWay();
		  ACC.travelfinder.populateNumberOfNightsOnLoad();
	},

	bindTravelFinderFormValidation : function() {
		var $departureDateField = $(".y_travelFinderDatePickerDeparting" );
		var $returnDateField = $(".y_travelFinderDatePickerReturning");
		var $checkInDateField = $(".y_travelFinderDatePickerCheckIn");
		var $checkOutDateField = $(".y_travelFinderDatePickerCheckOut");

		$(ACC.travelfinder.componentParentSelector).validate({
			errorElement : "span",
			errorClass : "fe-error",
			onfocusout: function(element) { 
				$(element).valid(); 
			},
			submitHandler : function(form) {
				
				if ($('.radio-button-row').find(':checked').val() == 'RETURN') {
					var departureDate = $(".y_travelFinderDatePickerDeparting").val();
					if(!$(".y_travelFinderDatePickerCheckIn").val()
							|| (!$("#y_hotel-part-stay").prop("checked") && ACC.travelcommon.convertToUSDate($(".y_travelFinderDatePickerCheckIn").val() ).getTime() != ACC.travelcommon.convertToUSDate(departureDate).getTime())){
						$(".y_travelFinderDatePickerCheckIn").val(departureDate);
					}
					var arrivalDate = $(".y_travelFinderDatePickerReturning").val();
					if(!$(".y_travelFinderDatePickerCheckOut").val() 
							|| (!$("#y_hotel-part-stay").prop("checked") && ACC.travelcommon.convertToUSDate($(".y_travelFinderDatePickerCheckOut").val() ).getTime() != ACC.travelcommon.convertToUSDate(arrivalDate).getTime())){
						$(".y_travelFinderDatePickerCheckOut").val(arrivalDate);
					}
					if( ACC.travelcommon.convertToUSDate($checkOutDateField.val()).getTime() == ACC.travelcommon.convertToUSDate($checkInDateField.val()).getTime()){
						var date2 = ACC.travelcommon.convertToUSDate($checkInDateField.val());
						date2.setDate(date2.getDate()+1);
						$checkOutDateField.datepicker('setDate', date2);
					}
	    		 }

				var componentController;
                if($(ACC.travelfinder.componentParentSelector).find(".y_bookingJourney").val() == 'TRANSPORT_ACCOMMODATION'){
					componentController = "validateTravelFinderForm";
				}
				else {
                    componentController = "validatePackageFinderForm";
				}
				ACC.formvalidation.serverFormValidation(form, componentController, function (jsonData) {
                    if (!jsonData.hasErrorFlag) {
                        $('#y_processingModal').modal({
                            backdrop: 'static',
                            keyboard: false
                        });
                        form.submit();
                    }
                });
			},
			onkeyup: false,
			onclick: false,
		});

		 
		 if($("#y_travelFinderForm .y_originLocation" ).is('input')){
			 $("#y_travelFinderForm .y_originLocation" ).rules( "add", {
	              required: {
           			 depends:function(){
        				 $(this).val($.trim($(this).val()));
        		            return true;
        			 }
	        	  },
	              messages: {
	            	  required: ACC.addons.travelacceleratorstorefront['error.farefinder.from.location']
	              }
			 });

			 $("#y_travelFinderForm .y_originLocation" ).rules( "add", {
	              locationIsValid : ".y_originLocation",
	              messages: {
	                  locationIsValid: ACC.addons.travelacceleratorstorefront['error.farefinder.from.locationExists']
	              }
			 });
		 }
		 
		 if($("#y_travelFinderForm .y_destinationLocation" ).is('input')){
			 $("#y_travelFinderForm .y_destinationLocation" ).rules( "add", {
	             required:  {
           			 depends:function(){
        				 $(this).val($.trim($(this).val()));
        		            return true;
        			 }
        		 },
	             messages: {
	           	  required: ACC.addons.travelacceleratorstorefront['error.farefinder.to.location']
	             }
			 });

			 $("#y_travelFinderForm .y_destinationLocation" ).rules( "add", {
	              locationIsValid : ".y_destinationLocationCode",
	              messages: {
	                  locationIsValid: ACC.addons.travelacceleratorstorefront['error.farefinder.to.locationExists']
	              }
			 });
		 }
		 
		 if($("#y_travelFinderForm #flightClass" ).is('select')){
			 $("#y_travelFinderForm #flightClass" ).rules( "add", {
	             required: true,
	             messages: {
	           	  required: ACC.addons.travelacceleratorstorefront['error.farefinder.cabin.class']
	             }
			 });
		 }
		 
		 // add validation to departure date
        if ($departureDateField && $departureDateField.length > 0) {
            ACC.farefinder.addDepartureFieldValidation($departureDateField);
        }

        // if it's a "round trip", we need to validate the return date field
        if ($('#y_roundTripRadbtn').is(":checked")) {
            ACC.farefinder.addReturnFieldValidation($returnDateField, $departureDateField.val());
        }

        ACC.travelfinder.initializeDatePicker();
	        
    },
	
	bindRoomQuantity : function() {
		ACC.accommodationfinder.bindRoomQuantity();
    },
    
    bindCheckInAndCheckOutFieldsWithHotelPartStay : function() {
    	 $('#y_hotel-part-stay').change(function () {
    		 $('#y_hotel-part-stay-value').val(this.checked);
    		 if (this.checked) {
    	        $(".checkInAndCheckOutDiv").show();
    	        $(".y_travelFinderDatePickerCheckIn").val("");
    			$(".y_travelFinderDatePickerCheckOut").val("");
    			var numberOfNightsValue = $('.nights-placeholder-text');
    			$("#y_numberOfNights").text('0');
    			numberOfNightsValue.last().addClass('hidden');
				numberOfNightsValue.first().removeClass('hidden');
    		 } 
    	    else
    	        $(".checkInAndCheckOutDiv").hide();
    	 });
    	 
    },
    
    bindPartHotelStayWithTripType : function(){
    	$('.radio-button-row').change(function(){
    		if($('.radio-button-row').find(':checked').val() == 'SINGLE'){
    			 ACC.farefinder.hideReturnField();
    			 $('#y_hotel-part-stay').prop('checked',true);
    			 $('#y_hotel-part-stay-value').val(true);
    			 $('#y_hotel-part-stay').attr('disabled',true);
    			 $(".checkInAndCheckOutDiv").show();
    			 
    			 $(".y_travelFinderDatePickerCheckIn" ).rules( "add", {
    	             required: true,
    	             messages: {
    	           	  required: ACC.addons.travelacceleratorstorefront['error.accommodationfinder.checkin.date']
    	             }
    	       });
    			 
    			 $(".y_travelFinderDatePickerCheckOut").rules( "add", {
    	             required: true,
    	             messages: {
    	           	  required: ACC.addons.travelacceleratorstorefront['error.accommodationfinder.checkout.date']
    	             }
    	       });
    			 
    			 var departingDate = $(".y_travelFinderDatePickerDeparting").val();
    			 if(departingDate){
    				 $(".y_travelFinderDatePickerCheckIn").val(departingDate);
    			 }
    			 $(".y_travelFinderDatePickerCheckOut").val('');
    			 $("#y_numberOfNights").text('0');
    			 var numberOfNightsValue = $('.nights-placeholder-text');
    			 numberOfNightsValue.last().addClass('hidden');
				 numberOfNightsValue.first().removeClass('hidden');
    			 var todayDate = new Date();
    			 todayDate.setFullYear(todayDate.getFullYear() + 1);
    			 var currDate = ACC.travelcommon.convertToUSDate(departingDate);
    			 $(".y_travelFinderDatePickerCheckOut").datepicker("option", "minDate", ACC.travelcommon.addDays(currDate, 1));
    			 $(".y_travelFinderDatePickerCheckIn").datepicker("option", "maxDate", todayDate);
    			 if( $(".y_travelFinderDatePickerCheckIn").val()){
    				 $(".y_travelFinderDatePickerCheckOut").datepicker("option", 
    						 "maxDate", ACC.travelcommon.addDays(ACC.travelcommon.convertToUSDate($(".y_travelFinderDatePickerCheckIn").val()), 20));
    			 }else{
    				 $(".y_travelFinderDatePickerCheckOut").datepicker("option", "maxDate", todayDate);
    			 }
    		}
    		else{
    			ACC.farefinder.showReturnField();
		        ACC.farefinder.addReturnFieldValidation($(".y_travelFinderDatePickerReturning"), $(".y_travelFinderDatePickerDeparting")
		        .val());
		        $('#y_hotel-part-stay').prop('checked',false);
		        $('#y_hotel-part-stay-value').val(false);
    			$('#y_hotel-part-stay').attr('disabled',false);
    			$(".checkInAndCheckOutDiv").hide();
    			$(".y_travelFinderDatePickerCheckIn").rules("remove","required");
    			$(".y_travelFinderDatePickerCheckOut").rules("remove","required");
    		}
    	});
    },
    
    bindValidationsOnCheckInAndCheckOut : function(){
    	$("#y_travelDatePickerCheckIn, #y_travelDatePickerCheckOut").on("focusout", function() {
    		
    		if($(this).hasClass('y_travelFinderDatePickerCheckIn')){
    			ACC.accommodationfinder.reInitializeCheckInDate($(this));
    		}
    		$('#y_hotel-part-stay-value').val($('#y_hotel-part-stay').prop("checked"));
    		 if ($('#y_hotel-part-stay').prop("checked") && $('.radio-button-row').find(':checked').val() == 'RETURN') {
    			 $(".y_travelFinderDatePickerCheckIn").rules("remove","required");
    			 $(".y_travelFinderDatePickerCheckOut").rules("remove","required");
    		 }else{
    			 	if($(this).is('input') && $(this).valid() ){
    			 		if($(this).hasClass('y_travelFinderDatePickerCheckOut')){
    			 			ACC.accommodationfinder.addCheckOutDateFieldValidation($(".y_travelFinderDatePickerCheckOut"), $(".y_travelFinderDatePickerCheckIn").val());
    			 		}else{
    			 			ACC.accommodationfinder.addCheckInDateFieldValidation($(this));
    			 		}
    			 		$(this).valid();
    			 	}
    		 }
    		 
    	});
    },
    
    populateCheckInDateInOneWay : function(){
    	$(".y_travelFinderDatePickerDeparting").on("change keyup", function() {
    		 if ($('.radio-button-row').find(':checked').val() == 'SINGLE') {
    			 var departingDate = $(".y_travelFinderDatePickerDeparting").val();
    			 if(departingDate){
    				 $(".y_travelFinderDatePickerCheckIn").val(departingDate);
    				 var $checkOutDateField = $(".y_travelFinderDatePickerCheckOut");
    				 $checkOutDateField.val('');
    				 var currDate = ACC.travelcommon.convertToUSDate(departingDate);
    				 $checkOutDateField.datepicker("option", "minDate", ACC.travelcommon.addDays(currDate, 1));
	            	 $checkOutDateField.datepicker("option", "maxDate", ACC.travelcommon.addDays(currDate, 20));
    				 ACC.travelfinder.updateNumberOfNights($(".y_travelFinderDatePickerCheckIn"), $(".y_travelFinderDatePickerCheckOut"));
    			 }
    		 }
    	});
    },
    
    populateNumberOfNightsOnLoad : function(){
    	if(!$(".y_travelFinderDatePickerCheckOut").val() && !$(".y_travelFinderDatePickerCheckIn").val()){
    		return;
    	}
    	ACC.accommodationfinder.populateNumberOfNightsOnListingPage($(".y_travelFinderDatePickerCheckIn"), $(".y_travelFinderDatePickerCheckOut"));
    },
    
    initializeDatePicker: function(){
    	
		var $departureDateField = $(".y_travelFinderDatePickerDeparting" );
		var $returnDateField = $(".y_travelFinderDatePickerReturning");
		var $checkInDateField = $(".y_travelFinderDatePickerCheckIn");
		var $checkOutDateField = $(".y_travelFinderDatePickerCheckOut");
		
		ACC.farefinder.addDatePickerForFareFinder($departureDateField, $returnDateField);
		ACC.accommodationfinder.addDatePickerForAccommodationFinder($checkInDateField, $checkOutDateField);
    },
    
    calculateNumberOfNights : function(){
		
		$(".y_travelFinderDatePickerCheckIn, .y_travelFinderDatePickerCheckOut").on("change keyup", function(event) {
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
			ACC.travelfinder.updateNumberOfNights($(".y_travelFinderDatePickerCheckIn"), $(".y_travelFinderDatePickerCheckOut"));
		});
		
	},
	
	updateNumberOfNights : function($checkInDateField, $checkOutDateField){
		var checkOutDate = $checkOutDateField.datepicker('getDate');
		var checkInDate = $checkInDateField.datepicker('getDate');
		
		ACC.accommodationfinder.reInitializeCheckInDate($checkInDateField);
		
		if(jQuery.type(checkInDate)==='date'){
			ACC.accommodationfinder.populateCheckOutField($checkInDateField, $checkOutDateField);
			ACC.accommodationfinder.calculateNumberOfNights($checkInDateField, $checkOutDateField);
		}
	},
	
	 addCheckInCheckOutDatePickersForTravelFinder : function(){
    	 
		 var $checkInDateField = $(".y_travelFinderDatePickerCheckIn");
		 var $checkOutDateField = $(".y_travelFinderDatePickerCheckOut");
		 
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
	            	 if ($checkOutDateField.is(":visible")) {
		                 if($(this).valid())
		                 {
		                	 if(selectedDate){
		                		 var newDate = ACC.travelcommon.convertToUSDate(selectedDate);
			                	 $checkOutDateField.datepicker("option", "minDate", ACC.travelcommon.addDays(newDate, 1));
					             $checkOutDateField.datepicker("option", "maxDate", ACC.travelcommon.addDays(newDate, 20));
		                	 }
		                 }
		                 else
		                 {
				            	$checkOutDateField.datepicker({
				 		           minDate: ACC.travelcommon.getTodayUKDate(),
				 		           maxDate: '+1y',
				 		           beforeShow: function (input) {
				 		           setTimeout(function() {
				 		            	$checkOutDateField.rules('remove')
				 		            	}, 0);
				 		           },
				 		           
				            	 });
		                 }
	            	 }
	            }
	     });
		 
		 $checkOutDateField.datepicker({
	            minDate: ACC.travelcommon.getTodayUKDate(),
	            beforeShow: function (input) {
	            	if(!$checkInDateField.val()){
	            		$checkOutDateField.datepicker("option", "minDate", ACC.travelcommon.addDays(todayDate, 1));
	            		$checkOutDateField.datepicker("option", "maxDate", ACC.travelcommon.addDays(todayDate, 20));
	            	}
	            	setTimeout(function() {
	            		$checkOutDateField.rules('remove')
	            		}, 0);
	            },
	            onClose: function (selectedDate) {
	            	 $(this).valid();
	            }
	     });
	    }
	    
};
