ACC.managebookings = {

    _autoloadTracc : [
        "bindManageBookingsValidation"
    ],

    bindManageBookingsValidation: function() {
        $(".y_manageBookingsForm").validate({
            errorElement: "span",
            errorClass: "fe-error",
            submitHandler: function() { alert("Valid form submitted!") },
            onfocusout: function(element) { $(element).valid(); },
            rules: {
                bookingReference: "required",
                lastName: "required"
            },
            messages: {
                bookingReference: "Please enter your booking reference.",
                lastName: "Please enter your last name."
            }
        });
    }
}