/**
 * Module for common form validation methods.
 * @namespace
 */
ACC.formvalidation = {

    _autoloadTracc : [
        "initDateValidationMethods"
    ],

    errorClass : "fe-error",
    errorTag : "span",

    /**
     * create new methods for jquery validation
     */
    initDateValidationMethods : function() {
        // date greater than or equal to
        $.validator.addMethod("dateGreaterEqualTo",
            function(value, element, params) {
                if (!/Invalid|NaN/.test(ACC.travelcommon.convertToUSDate(value))) {
                    return ACC.travelcommon.convertToUSDate(value) >= ACC.travelcommon.convertToUSDate(params);
                }

                return isNaN(value) && isNaN(params) ||
                    (Number(value) >= Number(params));
            },ACC.addons.travelacceleratorstorefront['error.formvalidation.dateGreaterEqualTo']);

        // date less than or equal to
        $.validator.addMethod("dateLessEqualTo",
            function(value, element, params) {
                if (!/Invalid|NaN/.test(ACC.travelcommon.convertToUSDate(value))) {
                    return ACC.travelcommon.convertToUSDate(value) <= ACC.travelcommon.convertToUSDate(params);
                }

                return isNaN(value) && isNaN(params) ||
                    (Number(value) <= Number(params));
            },ACC.addons.travelacceleratorstorefront['error.formvalidation.dateLessEqualTo']);

        // date in UK format
        $.validator.addMethod("dateUK",
            function(value, element) {
                var check = false;
                var re = /^\d{1,2}\/\d{1,2}\/\d{4}$/;
                if( re.test(value)){
                    var adata = value.split('/');
                    var dd = parseInt(adata[0],10);
                    var mm = parseInt(adata[1],10);
                    var yyyy = parseInt(adata[2],10);
                    var xdata = new Date(yyyy,mm-1,dd);
                    if ( ( xdata.getFullYear() == yyyy ) && ( xdata.getMonth () == mm - 1 ) && ( xdata.getDate() == dd ) )
                        check = true;
                    else
                        check = false;
                } else
                    check = false;
                return this.optional(element) || check;
            },ACC.addons.travelacceleratorstorefront['error.formvalidation.dateUK'] );

        //Name validation,only allows A-Z, a-Z, space, hyphen or apostrophe characters
        $.validator.addMethod("nameValidation",
            function(value, element) {
                var re = new RegExp(/^([a-z A-Z-\']{1,35})$/);
                return this.optional(element) || re.test($.trim(value));
            },ACC.addons.travelacceleratorstorefront['error.formvalidation.nameValid'] );

        $.validator.addMethod("guestNameValidation",
            function(value, element) {
                var re = new RegExp(/^[a-zA-Z]([a-z A-Z-\']{0,34})$/);
                return this.optional(element) || re.test(value);
            },ACC.addons.travelacceleratorstorefront['error.formvalidation.nameValid'] );

        $.validator.addMethod("adultGuestValue",
            function(value, element) {
                if(Number(value)!=0){
                    return true;
                }
            },ACC.addons.travelacceleratorstorefront['error.accommodationfinder.guest.adult'] );

        $.validator.addMethod("validateFrequentFlyerNumber",
    	        function(value, element) {
    	        	var frequentFlyerCheck = $(element).closest('.y_frequentFlyerInfo')
    	        										.find('.y_travellerDetailsMembershipYesBtn')
    	        										.is(':checked');
    	        	if(frequentFlyerCheck) {
    	        		return value!='';
    	        	}
    	        },ACC.addons.travelacceleratorstorefront['error.formvalidation.frequentFlyerNumber'] );

        $.validator.addMethod("validateCountryCode",
            function(value, element) {
                if(Number(value)!=0 || value!=null){
                    return true;
                }
            },ACC.addons.travelacceleratorstorefront['error.guestdetails.country.empty'] );

        $.validator.addMethod("validatePhoneNumberPattern",
            function(value, element) {
                var atLeastOneNumRegex = /[0-9]+/;
                var reAtLeastOneNumber = new RegExp(atLeastOneNumRegex);
                var splCharRegex = /^\+?[0-9\(\).\s-]{1,50}$/;
                var reSplChars = new RegExp(splCharRegex);
                if(reAtLeastOneNumber.test($.trim(value)) && reSplChars.test($.trim(value))){
                    return true;
                }
            },ACC.addons.travelacceleratorstorefront['error.lead.guest.phone.invalid'] );

        $.validator.addMethod("validateEmailPattern",
            function(value, element) {
                var emailRegex = /\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,4}\b/;
                var reEmail = new RegExp(emailRegex);
                if(value==''||reEmail.test($.trim(value))){
                    return true;
                }
            },ACC.addons.travelacceleratorstorefront['error.lead.guest.email.invalid'] );

        //AccommodationFinderForm
        $.validator.addMethod("destinationLocationIsValid",
            function(value, element, params) {
                var inputId = element.getAttribute('id');
                var locationDestinationName = $(element).val();
                var locationDestination = $("." + inputId + "Code").val();
                if(locationDestinationName && locationDestination) {
                    return true;
                }
                var latitude = $("." + inputId + "Latitude").val();
                var longitude = $("." + inputId + "Longitude").val();
                var radius = $("." + inputId + "Radius").val();
                if(locationDestinationName && latitude && longitude && radius) {
                    return true;
                }
                return false;

            },ACC.addons.travelacceleratorstorefront['error.accommodationfinder.destination.location.autosuggestion'] );

        $.validator.addMethod("locationIsValid",
            function(value, element, params) {
                if(value && !$(params).val()) {
                    return false;
                }
                return true;

            },ACC.addons.travelacceleratorstorefront['error.farefinder.from.locationExists'] );
    },

    /**
     * validate the form using server side validation
     * and show inline error messages.
     * @param {string} formElement - selector to get the form element. e.g. "#firstName" or ".departureDate".
     * @param {string} serviceName - The name of the service that performs the backend validation. e.g. "ACC.services.refreshFareSelectionItinerary".
     * @param {function} validateFunction - The function that needs to be called when the server validation has returned.
     * @returns {json} jsonData - The response from the service.
     */
    serverFormValidation : function(formElement, serviceName, validateFunction) {
        var $form = $(formElement);
        var jsonData;

        $.when( window["ACC"]["services"][serviceName]($form.serialize()) ).then(
            // success
            function( data, textStatus, jqXHR ) {
                jsonData = JSON.parse(data);
                
                // clear validation messages
				$form.find("."+ACC.formvalidation.errorClass).html(''); // for group errors
				$form.find(ACC.formvalidation.errorTag+"."+ACC.formvalidation.errorClass).remove();
				$form.find("."+ACC.formvalidation.errorClass).removeClass(ACC.formvalidation.errorClass);
				$("[id$=QuantityListError]").html('');	// for group errors

				// process JSON response and display validation messages
				if(jsonData.hasErrorFlag){
					var $formField, $nextElement;
					// add error messages for each field
					$.each(jsonData.errors, function(key,value){
						// If it's an error for a group of fields (denoted by 'QuantityList' in the name), 
						// display the error in the dedicated group error section
						if((key.indexOf("QuantityList") != -1) && (key.indexOf("quantity") == -1)){
							$(".y_"+key+"Error").html(value);
							$(".y_"+key+"Error").addClass(ACC.formvalidation.errorClass);
						}
						else if(key.indexOf("Candidates") != -1){
							if($form.find("[name='"+key+"']").length > 0){
								$form.find("[name='"+key+"']").parents('.y_passengerGroup').find('.y_roomStayCandidatesError').html(value);
								$form.find("[name='"+key+"']").parents('.y_passengerGroup').find('.y_roomStayCandidatesError').addClass(ACC.formvalidation.errorClass);
							}
							else{
								 $(".y_roomStayCandidatesError").html(value); 
						         $(".y_roomStayCandidatesError").addClass(ACC.formvalidation.errorClass); 
							}
						}
						// display normal field errors inline
						else{
							$formField = $form.find("[name='"+key+"']");
							$formField.removeClass("valid");
							$formField.addClass(ACC.formvalidation.errorClass);
							$formField.attr("aria-describedby",$formField.attr('id')+"-error");
							$formField.attr("aria-invalid","true");
	
							var $newElem = document.createElement(ACC.formvalidation.errorTag);
							$newElem.setAttribute("id", $formField.attr('id')+"-error");
							$newElem.setAttribute("class", ACC.formvalidation.errorClass);
							$newElem.innerHTML = value;
							$formField.after($newElem);
						}
					});
				}
				validateFunction(jsonData);
			}
		);
	}
};
