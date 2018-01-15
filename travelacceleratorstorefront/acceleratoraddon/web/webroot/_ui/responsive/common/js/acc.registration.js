/**
 * The module for registration pages.
 * @namespace
 */
 ACC.registrationForm = {

	_autoloadTracc : [
        "bindingRegistrationValidaiton"
	],

    /**
     * validation for registration form
     */
	bindingRegistrationValidaiton : function() {
    	$('#registerForm').validate({
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
    }

};