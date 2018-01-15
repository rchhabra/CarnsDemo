/**
 * The module for personal details scripts.
 *
 * @namespace
 */
ACC.personaldetails = {
    _autoloadTracc :[
        "onload",
        "bindingPersonalDetailsForm",
        "bindGuestDetails" ,
        "bindEmailValidation"
    ],

    onload:function(){

        if($(".y_guestDetails").prop('checked')){
            $("#roomCollapse").addClass('in');
            $(window).ready(function() {
                ACC.personaldetails.bindValidationForCheckBox($(".y_guestDetails").prop('checked'));
            });

        }

        $(window).ready(function() {
            $('.y_personalDetailsSubmit').removeClass('disabled');
        });

    },

    bindingPersonalDetailsForm : function(){
        var $form = $("#y_personalForms");
        if($form.is('form')){

            $form.find('.y_guestFirstname').attr('maxLength','35');
            $form.find('.y_guestLastname').attr('maxLength','35');
            $form.find('.y_passengerFirstname').attr('maxLength','35');
            $form.find('.y_passengerLastname').attr('maxLength','35');

            $form.validate({
                errorElement: "span",
                errorClass: "fe-error",
                ignore: ".fe-dont-validate",
                submitHandler : function(form) {

                    if($(".y_guestDetails").prop('checked')){
                        var size = parseInt($("#y_numberOfRooms").val());
                        for(var i=1;i<=size;i++){
                            var hours = $('#y_rm_'+i+'-accomodation-arrival-hours').val();
                            var minutes = $('#y_rm_'+i+'-accomodation-arrival-minutes').val();
                            var seconds = '00';
                            var date = $("#y_checkInDate").val();
                            $("#y_arrivalTime_"+i).val(date+" "+hours+":"+minutes+":"+seconds);
                        }
                    }
                    $("#y_validateContactNumber").val(!$(".y_guestDetails").prop('checked'));
                    ACC.formvalidation.serverFormValidation(form, "validatePersonalDetailsForms", function (jsonData) {
                        if (!jsonData.hasErrorFlag) {
                            $('#y_processingModal').modal({
                                backdrop: 'static',
                                keyboard: false
                            });
                            form.submit();
                        }
                    });
                },
                onfocusout: function(element) {
                    $(element).valid();
                },
                onkeyup: false
            });

            $(".y_passengerFirstname" ).each(function(){
                $(this).rules( "add", {
                    required: true,
                    guestNameValidation: true,
                    messages: {
                        required: ACC.addons.travelacceleratorstorefront['error.formvalidation.firstName'],
                        guestNameValidation: ACC.addons.travelacceleratorstorefront['error.formvalidation.firstNameValid']
                    }
                });
            });

            $(".y_passengerLastname" ).each(function(){
                $(this).rules( "add", {
                    required: true,
                    guestNameValidation: true,
                    messages: {
                        required: ACC.addons.travelacceleratorstorefront['error.formvalidation.lastName'],
                        guestNameValidation: ACC.addons.travelacceleratorstorefront['error.formvalidation.lastNameValid']
                    }
                });
            });

            $(".y_passengerTitle" ).each(function(){
                $(this).rules( "add", {
                    required: true,
                    messages: {
                        required: ACC.addons.travelacceleratorstorefront['error.traveller.title.empty'],
                    }
                });
            });

            $("#y_personalForms #y_adult" ).each(function(){
                $(this).rules( "add", {
                    adultGuestValue: true,
                    messages: {
                        adultGuestValue: ACC.addons.travelacceleratorstorefront['error.accommodationfinder.guest.adult'],
                    }
                });
            });

            $("#y_passengerContactNumber").each(function(){
                $(this).rules( "add", {
                    maxlength:50,
                    required: true,
                    validatePhoneNumberPattern: true,
                    messages: {
                        required: ACC.addons.travelacceleratorstorefront['error.lead.guest.phone'],
                        validatePhoneNumberPattern: ACC.addons.travelacceleratorstorefront['error.lead.guest.phone.invalid']
                    }
                });
            });

        }
    },

    bindEmailValidation : function(){
        $(".y_guestEmail" ).on("keyup", function(){
            if($(".y_guestDetails").prop('checked')){
                $(this).rules( "add", {
                    maxlength:255,
                    validateEmailPattern: true,
                    messages: {
                        validateEmailPattern: ACC.addons.travelacceleratorstorefront['error.lead.guest.email.invalid']
                    }
                });
            }
        });

        $(".y_passengerEmail" ).on("keyup", function(){
            $(this).rules( "add", {
                maxlength:255,
                validateEmailPattern: true,
                messages: {
                    validateEmailPattern: ACC.addons.travelacceleratorstorefront['error.lead.guest.email.invalid']
                }
            });
        });

    },

    bindGuestDetails :function(){
        $(".y_guestDetails").on('click', function() {
            var useDiffCheckBoxChecked=this.checked;
            if(useDiffCheckBoxChecked && ($("#rm_1-first-name").val()=="")){
                var size = parseInt($("#y_numberOfRooms").val());
                for(var i=0;i<size;i++){
                    $("#rm_"+(i+1)+"-first-name").val($("#y_travellerdetails_"+i+"_first_name").val());
                    $("#rm_"+(i+1)+"-last-name").val($("#y_travellerdetails_"+i+"_last_name").val());
                    $("#rm_"+(i+1)+"-email").val($("#y_travellerdetails_"+i+"_email").val());
                    $("#rm_"+(i+1)+"-email").val($("#y_travellerdetails_"+i+"_email").val());
                }
                $("#y_contactNumber").val($("#y_passengerContactNumber").val());
            }
            ACC.personaldetails.bindValidationForCheckBox(this.checked);
        })},

    bindValidationForCheckBox :function(useDiffLeadDetails){
        $(".y_guestFirstname").each(function(){
            $(this).rules( "add", {
                required: useDiffLeadDetails,
                guestNameValidation: useDiffLeadDetails,
                messages: {
                    required: ACC.addons.travelacceleratorstorefront['error.formvalidation.firstName'],
                    guestNameValidation: ACC.addons.travelacceleratorstorefront['error.formvalidation.firstNameValid']
                }
            });
        });

        $(".y_guestLastname").each(function(){
            $(this).rules( "add", {
                required: useDiffLeadDetails,
                guestNameValidation: useDiffLeadDetails,
                messages: {
                    required: ACC.addons.travelacceleratorstorefront['error.formvalidation.lastName'],
                    guestNameValidation: ACC.addons.travelacceleratorstorefront['error.formvalidation.lastNameValid']
                }
            });
        });

        $("#y_contactNumber").each(function(){
            $(this).rules( "add", {
                maxlength:50,
                required: useDiffLeadDetails,
                validatePhoneNumberPattern: useDiffLeadDetails,
                messages: {
                    required: ACC.addons.travelacceleratorstorefront['error.lead.guest.phone'],
                    validatePhoneNumberPattern: ACC.addons.travelacceleratorstorefront['error.lead.guest.phone.invalid']
                }
            });
        });

        $(".y_guestEmail").each(function(){
            $(this).rules( "add", {
                maxlength:255,
                validateEmailPattern: useDiffLeadDetails,
                messages: {
                    validateEmailPattern: ACC.addons.travelacceleratorstorefront['error.lead.guest.email.invalid']
                }
            });

        });

        $("#y_passengerContactNumber").each(function(){
            if(useDiffLeadDetails){
                $(this).rules("remove", "maxlength validatePhoneNumberPattern required");
            }else{
                $(this).rules( "add", {
                    maxlength:50,
                    required: !useDiffLeadDetails,
                    validatePhoneNumberPattern: !useDiffLeadDetails,
                    messages: {
                        required: ACC.addons.travelacceleratorstorefront['error.lead.guest.phone'],
                        validatePhoneNumberPattern: ACC.addons.travelacceleratorstorefront['error.lead.guest.phone.invalid']
                    }
                });
            }
        });
    }
}
