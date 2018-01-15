ACC.payment = {
		
		_autoload : [ 
			"bindPaymentCardTypeSelect"
		],

		bindPaymentCardTypeSelect: function () {
			ACC.payment.filterCardInformationDisplayed();
			$("#pd-cardtype").change(function ()
			{
				ACC.payment.filterCardInformationDisplayed();
			});
		},

		filterCardInformationDisplayed: function () {
			var cardType = $('#pd-cardtype').val(),
				$startDate = $("#pd-valid-day").closest(".form-group").parent(),
				$issueNum = $("#pd-valid-year").closest(".form-group").parent();
			if (cardType == '024')
			{
				$startDate.show();
				$issueNum.show();
			}
			else
			{
				$startDate.hide();
				$issueNum.hide();
			}
		}
}

	
