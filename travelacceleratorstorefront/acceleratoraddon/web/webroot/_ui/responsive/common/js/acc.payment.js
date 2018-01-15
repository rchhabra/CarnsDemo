/**
 * The module for payment pages.
 * @namespace
 */
ACC.payment = {

    _autoloadTracc: [
        "bindPaymentCardTypeSelect",
        "bindPayNowButton",
        "bindPaymentOptionRadioButton",
        "bindNextStepButton",
        "loadReservationComponents",
        "enablePayNowButton",
        "activateSavedPaymentButton"
    ],

    /**
     * bind the card type field to the filterCardInformationDisplayed function.
     */
    bindPaymentCardTypeSelect: function () {
        ACC.payment.filterCardInformationDisplayed();
        $("#pd-cardtype").change(function () {
            ACC.payment.filterCardInformationDisplayed();
        });
    },

    /**
     * hide/show payment fields based on the card type selected
     */
    filterCardInformationDisplayed: function () {
        var cardType = $('#pd-cardtype').val(),
            $startDate = $("#pd-valid-day").closest(".form-group").parent(),
            $issueNum = $("#pd-valid-year").closest(".form-group").parent();
        if (cardType == '024') {
            $startDate.show();
            $issueNum.show();
        }
        else {
            $startDate.hide();
            $issueNum.hide();
        }
    },

    /**
     * disable the pay now button after a click event to avoid a double payment/submission.
     */
    bindPayNowButton: function () {
        $(".y_payNow").on("click", function () {
            $(this).attr("disabled", true);
            $('#y_processingModal').modal({
                backdrop: 'static',
                keyboard: false
            });
            $(this).parents('form').submit();
        });
    },

    bindPaymentOptionRadioButton: function () {
        $("#payment-type").on("change", ".y_paymentOptionRadio", function () {
            $('.y_paymentOptionRadio').not(this).attr('checked', false);
            $(".y_paymentOptionError").hide();
            var formID = "#" + this.value;
            $.when(ACC.services.submitPaymentOptionForm(formID)).then(
                function (data) {
                    var jsonData = JSON.parse(data);
                    if (!jsonData.valid) {
                        $(".y_paymentOptionErrorContent").html(ACC.addons.travelacceleratorstorefront[jsonData.errors[0]]);
                        $(".y_paymentOptionError").show();
                        $(".y_paymentOptionRadio").prop('checked', false);
                    }
                }
            )
        });
    },

    bindNextStepButton: function () {
        $("#y_selectPaymentMethod").on("click", function (e) {
            $.when(ACC.services.validatePaymentOptions()).then(
                function (data) {
                    var jsonData = JSON.parse(data);
                    if (!jsonData.valid) {
                        e.preventDefault();
                        $(".y_paymentOptionErrorContent").html(ACC.addons.travelacceleratorstorefront[jsonData.errors[0]]);
                        $(".y_paymentOptionError").show();
                    }
                }
            );
        })

    },

    loadReservationComponents : function() {
        if($(".y_checkoutSummary")){
            if($("#y_transportReservationComponentId").val()){
                ACC.reservation.getTransportReservationComponent($("#y_transportReservationComponentId").val());
            }
            if($("#y_accommodationReservationComponentId").val()){
                ACC.reservation.getAccommodationReservationComponent($("#y_accommodationReservationComponentId").val());
            }
        }
    },

    enablePayNowButton: function() {
        $(".y_payNow").removeAttr("disabled");
    },
    
    activateSavedPaymentButton: function(){

		$(document).on("click",".js-saved-payments",function(e){
			e.preventDefault();
			
			var title = $("#savedpaymentstitle").html();
			
			$.colorbox({
				href: "#savedpaymentsbody",
				inline:true,
				maxWidth:"100%",
				opacity:0.7,
				width:"320px",
				title: title,
				close:'<span class="glyphicon glyphicon-remove"></span>',
				onComplete: function(){
				}
			});
		})
	}

};

	
