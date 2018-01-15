ACC.travellerdetails = {

    _autoloadTracc: [
        "bindFrequentFlyerMembershipNumber",
        "bindPayingForBookingCheckBoxes",
        "bindingTravellerInformationForm",
        "bindingPaymentDetails",
        "bindEmailValidation",
        "hideAdditionalInformationModal",
        "checkIfAnyValuePresentInsideModelOnLoad",
        "checkIfAnyValuePresentInsideModelOnFocusOut",
        "bindNameAutosuggest",
        "bindSaveAllTravellers",
        "bindClearAdditionalInformation"
    ],

    currentForm: '.y_passengerInformationForm',

    bindFrequentFlyerMembershipNumber: function () {

        $('.y_travellerDetailsMembershipYesBtn').each(function (index) {
            $(this).on('click', function () {
                $(this).closest('.row').find('.y_membershipNumber').show("fast");
                $(this).closest('.row').find('.y_passengerFrequentFlyerMembershipNumber').prop('disabled', false);
            });
        });

        $('.y_travellerDetailsMembershipNoBtn').each(function (index) {
            $(this).on('click', function () {
                var membershipNumber = $(this).closest('.row').find('.y_passengerFrequentFlyerMembershipNumber');

                $(this).closest('.row').find('.y_membershipNumber').hide("fast");
                membershipNumber.prop('disabled', true);

                if (membershipNumber.hasClass('fe-error')) {
                    membershipNumber.removeClass('fe-error');
                    membershipNumber.siblings('span').remove();
                }
            });
        });

    },

    bindClearAdditionalInformation: function () {
        // clears form inputs if user closes modal with 'X' & has not used 'Confirm' button

        $('.y_additionalInformationModal').on('show.bs.modal', function(e) {

            // obtain values when modal is opened

            var emailValue = $('.y_passengerEmail', this).val(),
                travelReasonValue = $('.y_passengerReasonForTravel', this).val(),
                specialAssistanceValue = $('.y_specialassistance', this).prop('checked'),
                membershipNoValue = $('.y_travellerDetailsMembershipNoBtn', this).prop('checked');  

            $('.y_additionalInformationModal .close').unbind().on('click', function(e) {

                // when modal is closed using 'X' values are kept to what they were upon modal opening
                
                if(emailValue == ''){
                    $(this)
                    .parents('.modal-content')
                    .find('.y_passengerEmail')
                    .val('')
                    .removeClass('valid');
                }
                if(travelReasonValue == '') {
                    $(this)
                    .parents('.modal-content')
                    .find('.y_passengerReasonForTravel')
                    .val('')
                    .removeClass('valid');
                }
                if(specialAssistanceValue == true) {
                    $(this)
                    .parents('.modal-content')
                    .find('.y_specialassistance')
                    .prop('checked', true);
                } else if(specialAssistanceValue == false){
                    $(this)
                    .parents('.modal-content')
                    .find('.y_specialassistance')
                    .prop('checked', false)
                    .removeClass('valid');
                }
                if(membershipNoValue == true) {
                    $(this)
                    .parents('.modal-content')
                    .find('.y_travellerDetailsMembershipNoBtn')
                    .prop('checked', true);
                    $(this)
                    .parents('.modal-content')
                    .find('.y_membershipNumber')
                    .hide();
                } else if(membershipNoValue == false){
                    $(this)
                    .parents('.modal-content')
                    .find('.y_travellerDetailsMembershipNoBtn')
                    .prop('checked', false)
                    .removeClass('valid');
                }
            });
        });

    },

    bindPayingForBookingCheckBoxes: function () {
        $("input:radio[class=y_bookerIsTravelling]").click(function () {
            // first Adult form
            var $form = $("#passenger-info-0");

            if ($(this).val() == 'true') {

                $.when(ACC.services.getCurrentUserDetailsAjax()).then(
                    // success
					function( data ) {

                        ACC.travellerdetails.resetFormFields($form);
                        if (data.isAuthenticated) {
                            ACC.travellerdetails.populateFormFields($form, data.travellerInfo);
                        }
                    },
                    // error
					function() {
                        // no details found so reset the form so new details can be entered
                        ACC.travellerdetails.resetFormFields($form);
                    }
                );
            }
            else {
                //
            }
        });
    },

    populateFormFields: function ($form, travellerInfo, userId) {
        if (!travellerInfo.title || $form.find('.y_passengerTitle option[value=' + travellerInfo.title + ']').length === 0) {
            $form.find('.y_passengerTitle option:first').attr('selected', 'selected');
        } else {
            $form.find('.y_passengerTitle').val(travellerInfo.title);
        }

        $form.find('.y_passengerFirstname').val($.parseHTML(travellerInfo.firstName)[0].textContent);
        $form.find('.y_passengerLastname').val($.parseHTML(travellerInfo.surname)[0].textContent);
        $form.find('.y_passengerReasonForTravel').val(travellerInfo.reasonForTravel);
        // populate selectedSavedTravellerUId
        $form.find('.y_passengerSelectedSavedTravellerUId').val($.parseHTML(travellerInfo.uid)[0].textContent);
        if (travellerInfo.email) {
            $form.find('.y_passengerEmail').val($.parseHTML(travellerInfo.email)[0].textContent);
        } else {
        	$form.find('.y_passengerEmail').val('');
        }

        // populate Gender checkbox
        if (travellerInfo.gender) {
            $form.find('input:radio[class=y_passengerGender][value=' + travellerInfo.gender + ']').prop('checked', true);
        }
        else {
            $form.find('input:radio[class=y_passengerGender][value=male]').prop('checked', true);
        }
        
        if($form.find('.y_groupPassengerGender').length > 0){
        	$form.find('.y_groupPassengerGender').val(travellerInfo.gender);
        }

        // populate Frequent Flyer checkbox
        if (travellerInfo.membershipNumber) {
            $form.find('.y_travellerDetailsMembershipYesBtn').prop('checked', true);
            $form.find('.y_travellerDetailsMembershipNoBtn').prop('checked', false);
            $form.find('.y_travellerDetailsMembershipYesBtn').closest('.row').find('.y_membershipNumber').show("fast");
            $form.find('.y_travellerDetailsMembershipYesBtn').closest('.row').find('.y_passengerFrequentFlyerMembershipNumber').prop('disabled', false);
            $form.find('.y_passengerFrequentFlyerMembershipNumber').val(travellerInfo.membershipNumber);

        }
        else {
            $form.find('.y_travellerDetailsMembershipYesBtn').prop('checked', false);
            $form.find('.y_travellerDetailsMembershipNoBtn').prop('checked', true);
            $form.find('.y_travellerDetailsMembershipNoBtn').closest('.row').find('.y_membershipNumber').hide("fast");
            $form.find('.y_travellerDetailsMembershipNoBtn').closest('.row').find('.y_passengerFrequentFlyerMembershipNumber').prop('disabled', true);
        }

        // populate Special Assistance check box
        if (travellerInfo.specialRequestDetail && travellerInfo.specialRequestDetail.specialServiceRequests.length !== 0) {
            $form.find('.y_specialassistance').prop('checked', true);
        }
        else {
            $form.find('.y_specialassistance').prop('checked', false);
        }

        if (ACC.travellerdetails.isAnyValuePresentInsideModal($form)) {
            $form.find('.y_checkedIcon').removeClass('hidden');
        }
        else {
            $form.find('.y_checkedIcon').addClass('hidden');
        }

    },

    resetFormFields: function ($form) {
        $form.find('.y_passengerTitle').val("");
        $form.find('.y_passengerFirstname').val("");
        $form.find('.y_passengerLastname').val("");
        $form.find('.y_passengerReasonForTravel').val("");
        $form.find('.y_passengerFrequentFlyerMembershipNumber').val("");
        $form.find('.y_passengerFrequentFlyerMembershipNumber').prop('disabled', true);
        $form.find('input:radio[class=y_travellerDetailsMembershipNoBtn]').prop('checked', true);
        $form.find('.y_membershipNumber').hide("fast");
        $form.find('input:radio[class=y_passengerGender][value=male]').prop('checked', true);
        $form.find('input:checkbox[class=y_savedetails]').prop('checked', false);
        $form.find('input:checkbox[class=y_specialassistance]').prop('checked', false);
    },

    bindingTravellerInformationForm: function () {
        var $form = $("#y_travellerForms");
        if ($form.is('form')) {
            $form.find('.y_passengerFirstname').attr('maxLength', '35');
            $form.find('.y_passengerLastname').attr('maxLength', '35');

            $form.validate({
                errorElement: "span",
                errorClass: "fe-error",
                ignore: ".fe-dont-validate",
                submitHandler: function (form) {
                    ACC.formvalidation.serverFormValidation(form, "validateTravellerDetailsForms", function (jsonData) {
                        if (!jsonData.hasErrorFlag) {
                            $('#y_processingModal').modal({
                                backdrop: 'static',
                                keyboard: false
                            });
                            form.submit();
                        } else {
                            $('.y_additionalInformationModal').each(function (index) {
                                if ($('input[type="text"].fe-error', this).length > 0) {
                                    $('input[type="text"].fe-error', this).parents('.y_additionalInformationModal')
                                        .prev()
                                        .find('.y_errorIcon')
                                        .removeClass('hidden');
                                }
                            });
                        }
                    });
                },
                showErrors: function (event, validator) {
                    this.defaultShowErrors();
                    $('.y_additionalInformationModal').each(function (index) {
                        if ($('input[type="text"].fe-error', this).length > 0) {
                            $('input[type="text"].fe-error', this).parents('.y_additionalInformationModal')
                                .prev()
                                .find('.y_errorIcon')
                                .removeClass('hidden');
                        }
                    });
                },
                onfocusout: function (element) {
                    $(element).valid();
                }
            });

            $(".y_passengerFirstname").each(function () {
                $(this).rules("add", {
                    required: true,
                    guestNameValidation: true,
                    messages: {
                        required: ACC.addons.travelacceleratorstorefront['error.formvalidation.firstName'],
                        guestNameValidation: ACC.addons.travelacceleratorstorefront['error.formvalidation.firstNameValid']
                    }
                });
            });

            $(".y_passengerLastname").each(function () {
                $(this).rules("add", {
                    required: true,
                    guestNameValidation: true,
                    messages: {
                        required: ACC.addons.travelacceleratorstorefront['error.formvalidation.lastName'],
                        nameValidation: ACC.addons.travelacceleratorstorefront['error.formvalidation.lastNameValid']
                    }
                });
            });

            $(".y_passengerFrequentFlyerMembershipNumber").each(function () {
                $(this).rules("add", {
                    validateFrequentFlyerNumber: true,
                    messages: {
                        validateFrequentFlyerNumber: ACC.addons.travelacceleratorstorefront['error.formvalidation.frequentFlyerNumber']
                    }
                });
            });

        }
    },

    bindingPaymentDetails: function () {
        var $form = $("#silentOrderPostForm");
        $form.find('#pd-card-verification').attr('maxLength', '4');

        $form.validate({
            errorElement: "span",
            errorClass: "fe-error",
            ignore: ".fe-dont-validate",
            onfocusout: function (element) {
                $(element).valid();
            }
        });

        $(".y_cardNumber").each(function () {
            $(this).rules("add", {
                required: true,
                digits: true,
                messages: {
                    required: ACC.addons.travelacceleratorstorefront['error.formvalidation.cardNumber']
                }
            });
        });

        $(".y_nameOnCard").each(function () {
            $(this).rules("add", {
                required: true,
                nameValidation: true,
                messages: {
                    required: ACC.addons.travelacceleratorstorefront['error.formvalidation.cardHolderName']
                }
            });
        });

        $(".y_cardVerification").each(function () {
            $(this).rules("add", {
                required: true,
                digits: true,
                messages: {
                    required: ACC.addons.travelacceleratorstorefront['error.formvalidation.cardVerification']
                }
            });
        });
    },

    bindEmailValidation: function () {
        $(".y_passengerEmail").on("keyup", function () {
            $(this).rules("add", {
                maxlength: 255,
                validateEmailPattern: true,
                messages: {
                    validateEmailPattern: ACC.addons.travelacceleratorstorefront['error.lead.guest.email.invalid']
                }
            });
        });
    },

    hideAdditionalInformationModal: function () {
        $(".y_saveAdditionalChanges, .y_modalCloseButton").on('click', function () {
            $(this).closest('.y_additionalInformationModal').modal('hide');
        });
    },

    checkIfAnyValuePresentInsideModelOnLoad: function () {
        $(ACC.travellerdetails.currentForm).each(function () {
            if (ACC.travellerdetails.isAnyValuePresentInsideModal($(this))) {
                $(this).find('.y_checkedIcon').removeClass('hidden');
            }
        });
    },

    checkIfAnyValuePresentInsideModelOnFocusOut: function () {
        $(document).on('hidden.bs.modal', ".y_additionalInformationModal", function (e) {
            var $form = $(this).closest(ACC.travellerdetails.currentForm);
            if (ACC.travellerdetails.isAnyValuePresentInsideModal($form)) {
                $form.find('.y_checkedIcon').removeClass('hidden');
            }
            else {
                $form.find('.y_checkedIcon').addClass('hidden');
            }
            if ($('input[type="text"].fe-error', this).length > 0) {
                $form.find('.y_errorIcon').removeClass('hidden');
            } else {
                $form.find('.y_errorIcon').addClass('hidden');
            }
        });
    },

    isAnyValuePresentInsideModal: function ($form) {
        var email = $form.find('.y_passengerEmail').val();
        var reasonOfTravel = $form.find('.y_passengerReasonForTravel').val();
        var frequentFlier = $form.find('.y_travellerDetailsMembershipYesBtn').is(':checked');
        var specialAssistance = $form.find('.y_specialassistance').is(':checked')
        if (email != '' || reasonOfTravel != '' || frequentFlier == true || specialAssistance == true) {
            return true;
        }
    },

    // Suggestions/autocompletion for First Name
    bindNameAutosuggest: function () {
   	 $('.y_passengerFirstname,.y_passengerLastname').each(function () {
		 $(this).autosuggestion({
		 suggestionSelect: "#y_passengerFirstNameSuggestions, #y_passengerLastNameSuggestions", 
         autosuggestServiceHandler: function (nameText) {
         	var eventTargetId=event.target.id;
             if ($('#' + eventTargetId).hasClass('y_customer')) {
                 var currentDiv = $('#' + eventTargetId).closest('.y_passengerInformationForm');
                 var passengerType = $('#' + eventTargetId).closest('.y_passengerInformationForm').find('.y_passengerType').val();
                 var currentSuggestion = $('#' + eventTargetId).closest('.y_passengerInformationForm').find('#y_passengerFirstNameSuggestions');
                 var requestMapping = "first-name";
                 if ($('#' + eventTargetId).hasClass('y_passengerLastname')) {
                     requestMapping = "last-name";
                     currentSuggestion = $('#' + eventTargetId).closest('.y_passengerInformationForm').find('#y_passengerLastNameSuggestions');
                 }

                 // make AJAX call to get the suggestions
                 $.when(ACC.services.getSuggestedNamesAjax(nameText, passengerType, requestMapping)).then(
                     // success
             			function (data) {
                         currentSuggestion.html(data.htmlContent);
                         ACC.travellerdetails.disableOptions(currentDiv);
                         ACC.travellerdetails.bindChangeReturningCustomer(eventTargetId, currentSuggestion);
                         currentSuggestion.addClass('bound');
                         if (data.htmlContent) {
                             currentSuggestion.removeClass("hidden");
                         }
                     }
                 );
             }
         },
         isName: true
     });});
},

    disableOptions: function (currentDiv) {

        $(ACC.travellerdetails.currentForm).each(function () {
            if ($(this).find('.y_passengerType').val() != currentDiv.find('.y_passengerType').val()) {
                return;
            }
            if ($(this).find('.y_passengerSelectedSavedTravellerUId').val() == currentDiv.find('.y_passengerSelectedSavedTravellerUId').val()) {
                return;
            }
            var selectedUId = $(this).closest('.y_passengerInformationForm').find('.y_passengerSelectedSavedTravellerUId').val();
            if (selectedUId) {
                currentDiv.find('ul li a').each(function () {
                    if ($(this).data('uid') == selectedUId) {
                        $(this).addClass('unavailable');
                    }
                });
            }
        });

    },

    bindChangeReturningCustomer: function (currElem, currentSuggestion) {
        $('.passengerName').on("click", function (e) {
            var $form = $('#' + currElem).closest(ACC.travellerdetails.currentForm);
            var travellerInfo = ACC.travellerdetails.createTravellerInfoObject($(e.target), $form);
            ACC.travellerdetails.resetFormFields($form);
            ACC.travellerdetails.populateFormFields($form, travellerInfo);
            currentSuggestion.addClass("hidden");
            ACC.travellerdetails.removeErrors($form);
            e.stopImmediatePropagation();
            e.preventDefault();
        });

        // clicking outside of the selection will close it
        $(document).mouseup(function (e) {
            if (currentSuggestion.is(":visible")) {
                var $_target = $(e.target);
                if (!currentSuggestion.is($_target) // if the target of the click isn't the current Suggestion...
                    && !$('#' + currElem).is($_target) // if the target of the click isn't the input...
                    && currentSuggestion.has($_target).length === 0) // ... nor a descendant of the suggestion
                {
                    currentSuggestion.addClass("hidden");
                }
            }
        });

        /** Autocomplete keyboard functionality **/
        // pressing down on the input field goes into the suggestions
        $('#' + currElem).on('keydown', function (e) {
            // back space and delete
            if (e.keyCode == 8 || e.keyCode == 46) {
                var $form = $('#' + currElem).closest(ACC.travellerdetails.currentForm);
                $form.find('#y_selectedUid').val('');
                $form.find('.y_passengerSelectedSavedTravellerUId').val('');
            }

            // Up-Down key = scroll down
            if (e.keyCode == 40 || e.keyCode == 38) {
                e.preventDefault();
                var $suggestion;
                if (e.keyCode == 40) // for down key
                    $suggestion = currentSuggestion.find(".autocomplete-suggestion").first();
                else // for up key
                    $suggestion = currentSuggestion.find(".autocomplete-suggestion").last();

                var $form = $('#' + currElem).closest(ACC.travellerdetails.currentForm);
                var travellerInfo = ACC.travellerdetails.createTravellerInfoObject($suggestion, $form);
                if (!$suggestion.hasClass('unavailable')) {
                    ACC.travellerdetails.resetFormFields($form);
                    ACC.travellerdetails.populateFormFields($form, travellerInfo);
                    ACC.travellerdetails.removeErrors($form);
                }

                $suggestion.focus();
            }
            // Tab key = ignore
            else if (e.keyCode == 9) {
                currentSuggestion.addClass("hidden");
            }
        });

        // pressing up or down on the suggestions scrolls through them
        currentSuggestion.on('keydown', ".autocomplete-suggestion", function (e) {
            e.stopImmediatePropagation();
            // Down key = scroll down
            if (e.keyCode == 40) {
                e.preventDefault();
                var $nextSuggestion;
                // if the next suggestion is under the same parent
                if ($(this).closest('li').next().length) {
                    $nextSuggestion = $(this).closest('li').next().find('a');
                }
                // if the next suggestion is NOT under the same parent
                else {
                    $nextSuggestion = currentSuggestion.find(".autocomplete-suggestion").first();
                }
                var $form = $('#' + currElem).closest(ACC.travellerdetails.currentForm);
                var travellerInfo = ACC.travellerdetails.createTravellerInfoObject($nextSuggestion, $form);
                if (!$nextSuggestion.hasClass('unavailable')) {
                    ACC.travellerdetails.resetFormFields($form);
                    ACC.travellerdetails.populateFormFields($form, travellerInfo);
                    ACC.travellerdetails.removeErrors($form);
                }

                $nextSuggestion.focus();
            }
            // Up key = scroll up
            if (e.keyCode == 38) {
                e.preventDefault();

                var $prevSuggestion;
                // if the previous suggestion is under the same parent
                if ($(this).closest('li').prev().length) {
                    $prevSuggestion = $(this).closest('li').prev().find('a');
                }
                // if the previous suggestion is NOT under the same parent
                else {
                    $prevSuggestion = currentSuggestion.find(".autocomplete-suggestion").last();
                }

                var $form = $('#' + currElem).closest(ACC.travellerdetails.currentForm);
                var travellerInfo = ACC.travellerdetails.createTravellerInfoObject($prevSuggestion, $form);
                if (!$prevSuggestion.hasClass('unavailable')) {
                    ACC.travellerdetails.resetFormFields($form);
                    ACC.travellerdetails.populateFormFields($form, travellerInfo);
                    ACC.travellerdetails.removeErrors($form);
                }
                $prevSuggestion.focus();
            }
            // Tab key = ignore
            if (e.keyCode == 9) {
                currentSuggestion.addClass("hidden");
            }
            // Enter key = fill input field with the current suggestion
            if ((e.keyCode || e.which) == 13) {
                e.preventDefault();

                var $form = $('#' + currElem).closest(ACC.travellerdetails.currentForm);
                var travellerInfo = ACC.travellerdetails.createTravellerInfoObject($(e.target), $form);
                if (!$(this).hasClass('unavailable')) {
                    ACC.travellerdetails.resetFormFields($form);
                    ACC.travellerdetails.populateFormFields($form, travellerInfo);
                    ACC.travellerdetails.removeErrors($form);
                    currentSuggestion.addClass("hidden");
                }

            }
            // Esc key
            if (e.keyCode == 27) {
                currentSuggestion.addClass("hidden");
            }

        });

    },

    createTravellerInfoObject: function ($suggestion, $form) {

        var selectedUid = $suggestion.data('uid');
        var title = $suggestion.data('title');
        var firstName = $suggestion.data('firstname');
        var lastName = $suggestion.data('lastname');
        var gender = $suggestion.data('gender');
        var reasonForTravel = $suggestion.data('reasonfortravel');
        var email = $suggestion.data('email');
        var membershipNumber = $suggestion.data('membershipnumber');
        var specialRequestDetail = {
            specialServiceRequests: $suggestion.data('specialrequestdetail')
        };

        var travellerInfo = {
            title: title,
            gender: gender,
            reasonForTravel: reasonForTravel,
            firstName: firstName,
            surname: lastName,
            uid: selectedUid,
            email: email,
            membershipNumber: membershipNumber,
            specialRequestDetail: specialRequestDetail
        };

        $form.find('#y_selectedUid').val(selectedUid);

        return travellerInfo;

    },

    bindSaveAllTravellers: function () {
        $('.y_savedetails').prop('checked', false);
        $('.y_saveAllTravellers').prop('checked', false);
        $('.y_saveAllTravellers').on('click', function (event) {
            if ($(this).is(':checked')) {
                $(this).closest('.y_travellerDetailsParentDiv').find('.y_savedetails').prop('checked', true);
            }
            else {
                $(this).closest('.y_travellerDetailsParentDiv').find('.y_savedetails').prop('checked', false);
            }

        });
    },

    removeErrors: function ($form) {

        // get validator object
        var $validator = $form.validate();

        // get errors that were created using jQuery.validate.unobtrusive
        var $errors = $form.find(".fe-error");

        // removes erros messages
        $errors.each(function () {
            $(this).addClass("valid");
            $(this).removeClass(ACC.formvalidation.errorClass);
            $(this).attr("aria-describedby", '');
            $(this).attr("aria-invalid", "false");

            $(this).closest('span').text('');
            $(this).closest('span').removeClass('fe-error');

        });

    }

};
