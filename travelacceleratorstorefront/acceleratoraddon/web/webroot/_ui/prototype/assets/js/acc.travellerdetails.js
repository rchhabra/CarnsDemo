ACC.travellerdetails = {

	_autoloadTracc : [ 
		"bindMembershipButton",
		"bindAdditonalInformation"
	],

	bindMembershipButton : function() {

		$('.y_travellerDetailsMembershipYesBtn').each( function(index) {
			$(this).on('click', function() {
				$(this).closest('.row').find('.y_membershipNumber').show("fast");
			})
		});

		$('.y_travellerDetailsMembershipNoBtn').each( function(index) {
			$(this).on('click', function() {
				$(this).closest('.row').find('.y_membershipNumber').hide("fast");
			})
		});

	},

	bindAdditonalInformation : function() {

        $('.y_additionalInformationModal').on('hide.bs.modal', function () {
		    $(this).closest('.modal')
		    		.siblings('.additional-information')
		    		.find('.glyphicon')
		    		.removeClass('hidden');
		});

	}
}
