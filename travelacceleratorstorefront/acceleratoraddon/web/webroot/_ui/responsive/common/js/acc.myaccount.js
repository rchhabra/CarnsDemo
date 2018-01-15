/**
 * The module for my account pages.
 * @namespace
 */
 ACC.myaccount = {
	_autoloadTracc: [
		"bindCollapseOutboundInbound",
		"initializeDobExpiryDatePickers",
		"bindingUpdatePersonalDetailsSave",
		"bindConfirmDeleteBooking",
        "bindMyAccountMyBookingsShowMore",
        "bindRemoveBooking"
	],

	/**
	 * Hide/show the inbound & outbound trip accordion
	 */
	bindCollapseOutboundInbound: function() {
		$(".y_journey_collapse_btn").on('click', function() {
			var buttonTarget = $(this).attr('data-target').split("-");
			var sectionNo = buttonTarget[buttonTarget.length - 1];
			var passSummary = $("#passenger-summary-section-" + sectionNo);
			var ancillariesShared = $("#ancillary-shared-section-" + sectionNo);
			if ($(this).hasClass('collapsed')) {
				$(passSummary).collapse('show');
				$(ancillariesShared).collapse('show');
			} else {
				$(passSummary).collapse('hide');
				$(ancillariesShared).collapse('hide');
			}
		});
	},

	/**
	 * add date picker to Date of Birth fields
	 */
	initializeDobExpiryDatePickers: function() {
		var dateToday = new Date();

		$('#y_dob_datepicker').datepicker({
			changeYear: true,
			changeMonth: true,
			yearRange: (dateToday.getFullYear() - 100) + ":" + dateToday.getFullYear()
		});

		$('#y_expirydate_datepicker').datepicker({
			changeYear: true,
			changeMonth: true,
			yearRange: dateToday.getFullYear() + ":" + (dateToday.getFullYear() + 10)
		});
	},

	/**
	 * validation for update personal details form
	 */
	bindingUpdatePersonalDetailsSave: function() {
		$("input[name='firstName']").attr('maxLength','35');
		$("input[name='lastName']").attr('maxLength','35');

		$('#updateProfileForm').validate({
            errorElement: "span",
            errorClass: "fe-error",
            ignore: ".fe-dont-validate",
            onfocusout: function(element) {
                $(element).valid();
            },
            rules: {
                firstName: {
                    required: true,
                    nameValidation: true
                },
                lastName: {
                    required: true,
                    nameValidation: true
                }
            },
            messages: {
                firstName: {
                    required: ACC.addons.travelacceleratorstorefront['error.formvalidation.firstName'],
                    nameValidation: ACC.addons.travelacceleratorstorefront['error.formvalidation.firstNameValid']
                },
                lastName: {
                    required: ACC.addons.travelacceleratorstorefront['error.formvalidation.lastName'],
                    nameValidation: ACC.addons.travelacceleratorstorefront['error.formvalidation.lastNameValid']
                }
            }
        });
	},
	
	bindConfirmDeleteBooking: function(){
		$('#confirm-delete').on('show.bs.modal', function(e) {
			$(this).find('.btn-ok').attr('href', $(e.relatedTarget).data('href'));
		});
	},
	
	bindMyAccountMyBookingsShowMore: function(){
        $(".y_myAccountMyBookingsShowMore").on('click', function() { 
            var pageSize=$(this).data('pagesize');
            $(".y_myBookings li:hidden").slice(0, pageSize).show();
            if ($(".y_myBookings li:hidden").length == 0) {
            	 $(".y_myAccountMyBookingsShowMore").hide();
            }
            return false;
        });
	},

	/**
     * disable the 'Remove from this list' until page fully rendered to prevent modal being opened before colorbox fix is loaded.
     */
    bindRemoveBooking: function () {
    	$(".y_removeBooking").removeClass("disabled");
    }	
};