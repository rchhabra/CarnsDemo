ACC.advancePassengerInfoForm = {

	_autoloadTracc : [
        "bindingAdvancePassengerInfoForm"
	],

    bindingAdvancePassengerInfoForm : function() {
        $("input[name='firstname']").attr('maxLength','35');
        $("input[name='lastname']").attr('maxLength','35');

        $('#apiForm').validate({
            errorElement: "span",
            errorClass: "fe-error",
            ignore: ".fe-dont-validate",
            onfocusout: function(element) {
                $(element).valid();
            },
            rules: {
                firstname: {
                    required: true,
                    nameValidation: true
                },
                lastname: {
                    required: true,
                    nameValidation: true
                }
            },
            messages: {
                firstname: {
                    required: ACC.addons.travelacceleratorstorefront['error.formvalidation.firstName'],
                    nameValidation: ACC.addons.travelacceleratorstorefront['error.formvalidation.firstNameValid']
                },
                lastname: {
                    required: ACC.addons.travelacceleratorstorefront['error.formvalidation.lastName'],
                    nameValidation: ACC.addons.travelacceleratorstorefront['error.formvalidation.lastNameValid']
                }
            }
        });
    }

};