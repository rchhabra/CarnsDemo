ACC.checkin = {
	_autoloadTracc : [ "init", "bindCheckinFormValidation" ],

	init : function() {
		var todayDate = new Date();
		$('[class$=documentExpiryDate]').datepicker(
		{	
			changeYear: true,
			changeMonth: true,
			minDate : todayDate,
			yearRange : todayDate.getFullYear() + ":"
					+ (todayDate.getFullYear() + 15),
			onClose : function(selectedDate) {
				$(this).valid();
			}
		});

		$('[class$=dateOfBirth]').datepicker({
			changeYear: true,
			changeMonth: true,
			maxDate : todayDate,
			yearRange : 1900 + ":" + todayDate.getFullYear(),
			onClose : function(selectedDate) {
				$(this).valid();
			}
		});
	},

	bindCheckinFormValidation : function() {
		$("#checkInForm").validate({
			errorElement : "span",
			errorClass : "fe-error",
			onfocusout : function(element) {
				$(element).valid();
			}
		});

		$("[class$=documentType]")
				.each(
						function() {
							$(this)
									.rules(
											"add",
											{
												required : true,
												messages : {
													required : ACC.addons.travelacceleratorstorefront['error.checkinform.documentNumber.required']
												}
											});
						});
		$("[class$=documentNumber]")
				.each(
						function() {
							$(this)
									.rules(
											"add",
											{
												required : true,
												maxlength: 30,
												messages : {
													required : ACC.addons.travelacceleratorstorefront['error.checkinform.documentNumber.required'],
													maxlength: ACC.addons.travelacceleratorstorefront['error.checkinform.documentNumber.maxlength']
												}
											});
						});
		$(".y_checkinApDocExpiry")
				.each(
						function() {
							$(this)
									.rules(
											"add",
											{
												required : true,
												dateUK : true,
												messages : {
													required : ACC.addons.travelacceleratorstorefront['error.checkinform.documentExpiryDate.required'],
													dateUK : ACC.addons.travelacceleratorstorefront['error.checkinform.documentExpiryDate.dateuk']
												}
											});
						});

		$(".y_checkinApDob")
				.each(
						function() {
							$(this)
									.rules(
											"add",
											{
												required : true,
												dateUK : true,
												messages : {
													required : ACC.addons.travelacceleratorstorefront['error.checkinform.dateOfBirth.required'],
													dateUK : ACC.addons.travelacceleratorstorefront['error.checkinform.dateOfBirth.dateuk']
												}
											});

						});
	}
}
