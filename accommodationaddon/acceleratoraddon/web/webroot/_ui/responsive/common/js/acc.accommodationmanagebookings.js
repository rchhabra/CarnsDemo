ACC.accommodationmanagebookings = {
    _autoloadTracc: [
        "bindAddRequestButton",
        "bindRemoveEmptyRequestSection",
        "bindReviewValidation",
        "bindRequestValidation",
        "bindUpdateBookingDates",
        "initalizeDatePickers",
    ],
    
    bindAddRequestButton: function () {
    	$('.y_addRequestForm').each(function(){
    		$(this).submit(function(){
        		if($.trim($('.y_requestMessage').val()) != ''){
        	    $("button[type='submit']", this)
        	    	.attr('disabled', 'disabled')
        	    	.text(ACC.addons.travelacceleratorstorefront['submit.button.please.wait']);
        	    return true;
        		}
        	  });
    	});
    	
    	
    },
    
    bindRemoveEmptyRequestSection: function () {
		$('.y_roomBooking .request-amendments').each(function() {
			if ($(this).children().length < 1) {
				$(this).closest('section').remove();
			}
		});
    },
    
    bindReviewValidation: function() {
    	$(".y_addReviewForm").each(function(){
    		$(this).validate({
                errorElement: "span",
                errorClass: "fe-error",
                onfocusout: function(element) { $(element).valid(); },
                rules: {
                    headline: "required",
                    comment: "required",
                    rating: "required"	
                },
                messages: {
                    headline: ACC.addons.travelacceleratorstorefront['error.review.headline'],
                    comment: ACC.addons.travelacceleratorstorefront['error.review.comment'],
                	rating: ACC.addons.travelacceleratorstorefront['error.review.rating']
                },
                errorPlacement: function(error, element) {
                    if (element.attr("name") == "rating") {
                      error.appendTo(element.parents("ul"));
                    } else {
                        error.insertAfter(element);
                    }
                  }
            });
    	});
        
    },
    
    bindRequestValidation: function() {
    	$(".y_addRequestForm").each(function(){
    		$(this).validate({
                errorElement: "span",
                errorClass: "fe-error",
                onfocusout: function(element) { $(element).valid(); },
                rules: {
                    requestMessage: "required"
                },
                messages: {
                	requestMessage: ACC.addons.travelacceleratorstorefront['error.request.message'],
                }
            });
    	});
    },      

    initalizeDatePickers : function() {

		var $checkInDateField = $(".y_updateBookingDatePickerCheckIn");
		var $checkOutDateField = $(".y_updateBookingDatePickerCheckOut");
		
		$('#y_updateBookingDatesForm').validate({
			errorElement : "span",
			errorClass : "fe-error"
		});
		
		ACC.accommodationfinder.addDatePickerForAccommodationFinder($checkInDateField,$checkOutDateField);
		
		$(".y_updateBookingDatePickerCheckIn, .y_updateBookingDatePickerCheckOut").on("change keyup", function() {
			var checkOutDate = $checkOutDateField.datepicker('getDate');
			var checkInDate = $checkInDateField.datepicker('getDate');
			if(jQuery.type(checkOutDate)==='date' && jQuery.type(checkInDate)==='date'){
				ACC.accommodationfinder.populateCheckOutField($checkInDateField, $checkOutDateField);
			}
		});
		
	},
	
    bindUpdateBookingDates : function(){
    	$(".y_updateBookingDates").on('click', function (e) {
    		e.preventDefault();
            $.when(ACC.services.updateBookingDates()).then(
            	function(data) {
            		$("#y_updatedBookingPagedHtml").html(data.updatedBookingPagedHtml);
            		$("#y_updatedBookingPagedHtml").modal();
                });
        });
    }

};
