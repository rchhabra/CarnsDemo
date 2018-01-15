ACC.guestdetails = {

	_autoloadTracc : [ 
		"bindingGuestDetailsForm",
		"bindPhoneNumberValidation",
        "bindExtraServiceCheckboxChange",
        "bindExtraServiceSelectQuantityChange",
        "bindGuestDetailsRemoveRoom",
        "setArrivalTimeDuringAmendServices",
        "bindEmailValidation"        
	],

	bindingGuestDetailsForm : function(){
		var $form = $("#y_guestDetailsForm");
		if($form.is('form')){
		$form.find('.y_guestFirstname').attr('maxLength','35');
        $form.find('.y_guestLastname').attr('maxLength','35');

        $form.validate({
            errorElement: "span",
            errorClass: "fe-error",
            ignore: ".fe-dont-validate",
            submitHandler : function(form) {
            	
            	var size = parseInt($("#y_numberOfRooms").val());
            	for(var i=1;i<=size;i++){
            		var hours = $('#y_rm_'+i+'-accomodation-arrival-hours').val();
            		var minutes = $('#y_rm_'+i+'-accomodation-arrival-minutes').val();
            		var seconds = '00';
            		var date = $("#y_checkInDate").val();
            		$("#y_arrivalTime_"+i).val(date+" "+hours+":"+minutes+":"+seconds);
            	}

                ACC.formvalidation.serverFormValidation(form, "validateLeadGuestDetailsForms", function (jsonData) {
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
            onkeyup: false
        });
        
        $(".y_guestFirstname" ).each(function(){
            $(this).rules( "add", {
              required: true,
              guestNameValidation: true,
              messages: {
                required: ACC.addons.travelacceleratorstorefront['error.formvalidation.firstName'],
                guestNameValidation: ACC.addons.travelacceleratorstorefront['error.formvalidation.firstNameValid']
              }
            });
        });

        $(".y_guestLastname" ).each(function(){
            $(this).rules( "add", {
              required: true,
              guestNameValidation: true,
              messages: {
                required: ACC.addons.travelacceleratorstorefront['error.formvalidation.lastName'],
                guestNameValidation: ACC.addons.travelacceleratorstorefront['error.formvalidation.lastNameValid']
              }
            });
        });
        
        $("#y_country-code" ).each(function(){
            $(this).rules( "add", {
            	validateCountryCode: true,
              messages: {
            	  validateCountryCode: ACC.addons.travelacceleratorstorefront['error.guestdetails.country.empty']
              }
            });
        });
        
        $("#y_guestDetailsForm #y_contactNumber" ).each(function(){
            $(this).rules( "add", {
              required: true,
              messages: {
            	  required: ACC.addons.travelacceleratorstorefront['error.lead.guest.phone']
              }
            });
        });
        
        $("#y_guestDetailsForm #y_adult" ).each(function(){
            $(this).rules( "add", {
              adultGuestValue: true,
              messages: {
            	  adultGuestValue: ACC.addons.travelacceleratorstorefront['error.accommodationfinder.guest.adult'],
              }
            });
        });
		}
        
	},
	
	bindPhoneNumberValidation : function(){
		
		$("#y_contactNumber" ).on("keyup", function(){
			$(this).rules( "add", {
	      	  	maxlength:50,
	            validatePhoneNumberPattern: true,
	            messages: {
	          	  validatePhoneNumberPattern: ACC.addons.travelacceleratorstorefront['error.lead.guest.phone.invalid']
	            }
	      });
		});
		
	},
	
    bindExtraServiceCheckboxChange : function() {
    	$('.y_booking-extras').on('change', '.y_extraServiceCheckbox', function(e) {
    		var elementId = this.getAttribute("id");
    		var productCode = $("#" + elementId + "-productCode").val();
    		var roomStayReferenceNumber = $("#" + elementId + "-roomStayReferenceNumber").val();
    		
    		var quantity = parseInt($(this).val());
    		
    		if (productCode && roomStayReferenceNumber) {
    			if (this.checked) // if changed state is "CHECKED"
				{
                    ACC.guestdetails.addServiceToCart(productCode, roomStayReferenceNumber, quantity, function(success){
                        if(!success) {
                            this.checked = false;
                        }
                    });

				} else {
                    ACC.guestdetails.addServiceToCart(productCode, roomStayReferenceNumber, 0, function(success){
                        if(!success) {
                            this.checked = true;
                        }
                    });
				}
    		}         
        });
    },
    
    bindExtraServiceSelectQuantityChange : function() {
    	var previousQuantity = "";
    	$('.y_booking-extras').on('click', '.y_extraServiceSelect', function() {
    		previousQuantity = parseInt($(this).val());
    	}).on('change', '.y_extraServiceSelect', function(e) {
    		var elementId = this.getAttribute("id");
    		var productCode = $("#" + elementId + "-productCode").val();
    		var roomStayReferenceNumber = $("#" + elementId + "-roomStayReferenceNumber").val();
    		
    		var quantity = parseInt($(this).val());
    		var diffQuantity = quantity - previousQuantity;
    		
    		if (productCode && roomStayReferenceNumber) {
    			ACC.guestdetails.addServiceToCart(productCode, roomStayReferenceNumber, quantity, function(success){
                    if(!success) {
                        // revert the select dropdown to the previous quantity
                        $(this).val(previousQuantity);
                    }
				});
    		}
    		
    	});
    },
    
    addServiceToCart : function(productCode, roomStayReferenceNumber, quantity, callbackFunction) {
    	
    	$("#y_addExtraToCartForm #y_productCode").val(productCode);
    	$("#y_addExtraToCartForm #y_roomStayReferenceNumber").val(roomStayReferenceNumber);
    	$("#y_addExtraToCartForm #y_quantity").val(quantity);
    	
    	var addToCartResult;
    	$.when(ACC.services.addExtraToCartAjax()).then(
    			function(response) {
    				
    				var jsonData = JSON.parse(response);
    	            if (jsonData.valid) {
    	                ACC.reservation.refreshReservationTotalsComponent($("#y_reservationTotalsComponentId").val());
    	            	ACC.reservation.refreshAccommodationSummaryComponent($("#y_accommodationSummaryComponentId").val());
    	            } else {
    	            	var output = [];
    					jsonData.errors.forEach(function(error) {
    						output.push("<p>" + error + "</p>");
    					});
    					$("#y_addExtraToCartErrorModal .y_addProductToCartErrorBody").html(output.join(""));
    					$("#y_addExtraToCartErrorModal").modal();
    	            }
    				addToCartResult = jsonData.valid;
                    callbackFunction(addToCartResult);
    			});
    },
    
    bindGuestDetailsRemoveRoom : function() {
    	$(".y_guestDetailsRemoveRoom").on('click', function() {  
            var href=$(this).data('href');
            var roomStayDetailsToRemove=$(this).closest("div.y_roomStayDetails");
            $.when(ACC.services.removeRoomAjax(href)).then(
	            function(data) {
                	if(data.SUCCESS) {
                    	roomStayDetailsToRemove.remove();
                        $("#y_removeRoomResultMessageModal .y_removeRoomResultMessageModalBody").html(data.SUCCESS);
    	                ACC.reservation.refreshReservationTotalsComponent($("#y_reservationTotalsComponentId").val());
    	                ACC.guestdetails.refreshRoomStayRefNumber();
                        $("#y_removeRoomResultMessageModal").modal();
                	} else if(data.REDIRECT) {
                    	window.location.href= ACC.config.contextPath+data.REDIRECT;
                	}else {
                        $("#y_removeRoomResultMessageModal .y_removeRoomResultMessageModalBody").html(data.FAILED);
                        $("#y_removeRoomResultMessageModal").modal();
                	}
	            });
            return false; 
        }); 
    },
    
    refreshRoomStayRefNumber: function() {
    	$(".y_guestDetailsRemoveRoom").each(function(index, obj) {
            var href=$(obj).data('href');
            var roomStayRefNumber=Number(href.substring(href.length-1, href.length));
            href=href.substring(0, href.length-1)+(index+1);
            $(obj).data('href',href);
            $(".y_roomStayRefNumber_"+roomStayRefNumber).val(index+1);
            $(".y_leadDetailsFormId_"+roomStayRefNumber).val(index+1);
            
            if($("[name^=leadForms\\["+roomStayRefNumber+"\\]]").length) {
	            $("[name^=leadForms\\["+roomStayRefNumber+"\\]]").each(function(tempIndex, obj) {
	            	$(obj).attr("id",obj.id.replace(roomStayRefNumber,index+1));
	            	$(obj).attr("name",obj.name.replace(roomStayRefNumber,index+1));
	            });
            } else { 
            	$("[name^=leadForms\\["+(index+1)+"\\]]").each(function(tempIndex, obj) {
	            	$(obj).attr("id",obj.id.replace(roomStayRefNumber,index+1));
	            	$(obj).attr("name",obj.name.replace(index+1,index));        	
	            });
            }
            
            if($('#y_rm_'+roomStayRefNumber+'-accomodation-arrival-hours').attr("id")) {
	            $('#y_rm_'+roomStayRefNumber+'-accomodation-arrival-hours').attr("id",$('#y_rm_'+roomStayRefNumber+'-accomodation-arrival-hours').attr("id").replace(roomStayRefNumber,index+1));
	            $('#y_rm_'+roomStayRefNumber+'-accomodation-arrival-minutes').attr("id",$('#y_rm_'+roomStayRefNumber+'-accomodation-arrival-minutes').attr("id").replace(roomStayRefNumber,index+1));
	            $('#y_arrivalTime_'+roomStayRefNumber).attr("id",$('#y_arrivalTime_'+roomStayRefNumber).attr("id").replace(roomStayRefNumber,index+1));
            } else {
	            $('#y_rm_'+(roomStayRefNumber+1)+'-accomodation-arrival-hours').attr("id",$('#y_rm_'+(roomStayRefNumber+1)+'-accomodation-arrival-hours').attr("id").replace((roomStayRefNumber+1),roomStayRefNumber));
	            $('#y_rm_'+(roomStayRefNumber+1)+'-accomodation-arrival-minutes').attr("id",$('#y_rm_'+(roomStayRefNumber+1)+'-accomodation-arrival-minutes').attr("id").replace((roomStayRefNumber+1),roomStayRefNumber));
	            $('#y_arrivalTime_'+(roomStayRefNumber+1)).attr("id",$('#y_arrivalTime_'+(roomStayRefNumber+1)).attr("id").replace((roomStayRefNumber+1),roomStayRefNumber));
            }
    	});
    	$("#y_numberOfRooms").val(Number($("#y_numberOfRooms").val())-1);
    },
    
    setArrivalTimeDuringAmendServices : function(){
    	var numberOfRooms = Number($('#y_numberOfRooms').val());
    		for(var i=1; i<=numberOfRooms; i++){
    			if($('#y_arrivalTime_'+i).val()){
    				var arrivalTime = $('#y_arrivalTime_'+i).val();
    				if(arrivalTime.indexOf(' ') >= 0){
    					arrivalTime = arrivalTime.split(" ");
    					arrivalTime = arrivalTime[1];
    				}
    				arrivalTime = arrivalTime.split(":");
    				$('#y_rm_'+i+'-accomodation-arrival-hours').val(arrivalTime[0]);
    				$('#y_rm_'+i+'-accomodation-arrival-minutes').val(arrivalTime[1]);
    			}
    		}
    },
    

	bindEmailValidation : function(){
		$(".y_guestEmail" ).on("keyup", function(){
            $(this).rules( "add", {
            	maxlength:255,
            	validateEmailPattern: true,
	              messages: {
	                validateEmailPattern: ACC.addons.travelacceleratorstorefront['error.lead.guest.email.invalid']
	              }
	            });
        });
	}
}
