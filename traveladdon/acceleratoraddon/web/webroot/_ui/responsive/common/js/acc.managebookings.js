ACC.managebookings = {

    _autoloadTracc : [
        "bindManageBookingButton",
        "bindManageBookingModal",
        "bindManageBookingFormSubmit",
        "enableRemoveTravellerButton",
        "bindManageBookingsValidation",
        "bindRemoveTravellerClick",
        "bindRemoveTravellerModalClick",
        "bindRemoveTravellerModalDismiss"
    ],

    bindManageBookingButton: function () {
        $(document).ready(function() {
            $(".y_manageBookingSubmit").prop("disabled", false);
        });
    },

    bindManageBookingModal: function() {
        $("#y_manageBookingModal").on("click", ".y_manageBookingSubmit", function(ev) {
            var manageBookingsForm = $("#y_additionalSecurityForm");
            if(!manageBookingsForm.valid()){
                return false;
            }
            ev.preventDefault();
            $.when( ACC.services.manageBookingLogin(manageBookingsForm.serialize())).then(
                // success
                function( data ) {
                    if(data.loginStatus === 'OK') {
                        window.location.href= ACC.config.contextPath + "/manage-booking/booking-details/" + data.bookingReference;
                        return false;
                    }
                    if(data.loginStatus === 'ADDITIONAL_SECURITY' || data.loginStatus === 'ERROR') {
                        $("#y_manageBookingModal").find('.modal-content').html(data.modal);
                        $("#y_manageBookingModal").find('.y_additional-security-popover').popover();
                        ACC.managebookings.bindManageBookingModalValidation();
                    }
                }
            );
            return false;
        });
    },

    bindManageBookingFormSubmit: function() {
        $(".y_manageBookingSubmit").click(function (ev) {
            ev.preventDefault();
            var manageBookingsForm = $("#y_manageBookingsForm");
            if(!manageBookingsForm.valid()){
                return false;
            }
            $.when( ACC.services.manageBookingLogin(manageBookingsForm.serialize())).then(
                // success
                function( data ) {
                    if(data.loginStatus === 'OK') {
                        window.location.href= ACC.config.contextPath + "/manage-booking/booking-details/" + data.bookingReference;
                        return false;
                    }
                    if(data.loginStatus === 'ADDITIONAL_SECURITY' || data.loginStatus === 'ERROR') {
                        var manageBookingModal = $("#y_manageBookingModal");
                        manageBookingModal.find('.modal-content').html(data.modal);
                        manageBookingModal.modal();
                        ACC.managebookings.bindManageBookingModalValidation();
                    }
                }
            );
            return false;
        });
    },

    bindManageBookingModalValidation: function() {
        $("#y_additionalSecurityForm").validate({
            errorElement: "span",
            errorClass: "fe-error",
            onfocusout: function(element) { $(element).valid(); },
            rules: {
                passengerReference: {
                    required: true,
                    minlength: 8,
                    maxlength: 11
                }
            }
        });
    },

    bindManageBookingsValidation: function() {
        $("#y_manageBookingsForm").validate({
            errorElement: "span",
            errorClass: "fe-error",
            onfocusout: function(element) { $(element).valid(); },
            rules: {
                bookingReference: "required",
                lastName: "required"
            },
            messages: {
                bookingReference: ACC.addons.travelacceleratorstorefront['error.managemybooking.booking.reference'],
                lastName: ACC.addons.travelacceleratorstorefront['error.managemybooking.last.name']
            }
        });
    },

    bindRemoveTravellerClick: function() {
        $(".y_removeTraveller").on('click', function(event){
            $(".y_removeTraveller").css({ 'pointer-events': 'none'});
            event.preventDefault();
            var travellerUid = $(this).closest(".form-group").find("input[name=travellerUid]").val();
            var orderCode = $("input[name=bookingReference]").val();
            var cancelTravellerUrl = $(this).attr('href');
            if(travellerUid != '' && orderCode != ''){
                $.when( ACC.services.cancelTraveller(orderCode,travellerUid) ).then(
                    function(data) {
                        if (data.isCancelPossible) {
                            $(".y_cancelTravellerConfirm").html(data.cancelTravellerModalHtml);
                            $("#y_cancelTravellerUrl").attr('href', cancelTravellerUrl);
                            $('#y_cancelTravellerModal').modal();
                        } else {
                            $(".y_cancellationResult").removeClass("alert-success");
                            $(".y_cancellationResult").addClass("alert-danger");
                            $(".y_cancellationResult").show();
                            $(".y_cancellationResultContent").html(data.errorMessage);
                            $(".y_cancellationRefundResultContent").html("");
                        }
                    }
                );
            }
        });
    },

    enableRemoveTravellerButton : function() {
        $(".y_removeTraveller").css({ 'pointer-events': ''});
    },

    bindRemoveTravellerModalClick : function() {
        $("#y_cancelTravellerUrl").on('click', function(event){
            $(this).attr('disabled', 'disabled');
        });
    },

    bindRemoveTravellerModalDismiss : function() {
        $(document).on('hidden.bs.modal', "#y_cancelTravellerModal", function(){
            ACC.managebookings.enableRemoveTravellerButton();
        });
    }

};
