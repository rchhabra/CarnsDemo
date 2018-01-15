ACC.packagedetails = {

    addTransportBundleToCartResult: true,
    addAccommodationToCartResult: true,

    _autoloadTracc: [
        "showNoAvailabilityModal",
        "bindDealDepartureDatePicker",
        "bindShowHideShowRoomOptions",
        "bindAccommodationChange",
        ["bindOutboundSelectionButton", $(".y_packageFareSelection").length !== 0],
        ["bindInboundSelectionButton", $(".y_packageFareSelection").length !== 0],
        "addPackageToCartOnLoad",
        "amendPackageToCartOnLoad"
    ],

    showNoAvailabilityModal: function () {
        var noPackageAvailability = $("#y_noPackageAvailability");
        if (noPackageAvailability) {
            var availabilityValue = noPackageAvailability.val();
            if (availabilityValue == "show") {
                $("#y_noPackageAvailabilityModal").modal();
            }
        }
    },

    bindDealDepartureDatePicker: function () {

        $('#dealDatePickerDeparting').on("keyup", function () {
            ACC.dealselection.dealValidDates = [];
        });
        $('#dealDatePickerDeparting').datepicker({
            minDate: new Date(),

            beforeShow: function (datePicker) {
                var parts = datePicker.value.split('/');
                ACC.dealselection.dealValidDates = [];
                ACC.dealselection.getDealValidDates(this, parts[1], parts[2], this.getAttribute('data-dealstartdatepattern'));
            },

            beforeShowDay: function (date) {
                var formattedDate = jQuery.datepicker.formatDate('dd/mm/yy', date);
                return [$.inArray(formattedDate, ACC.dealselection.dealValidDates) >= 0, ""];
            },

            onChangeMonthYear: function (year, month) {
                ACC.dealselection.dealValidDates = [];
                ACC.dealselection.getDealValidDates(this, month, year, this.getAttribute('data-dealstartdatepattern'));
            },

            onClose: function (selectedDate) {
                ACC.packagedetails.validateSelectedDepartureDate(selectedDate,
                    this.getAttribute('data-dealbundletemplateid'));
                ACC.dealselection.dealValidDates = [];
            }
        });

    },

    validateSelectedDepartureDate: function (dealSelectedDepartureDate, dealbundletemplateid) {
        $.when(ACC.services.validateSelectedDepartureDate(dealSelectedDepartureDate, dealbundletemplateid)).then(function (response) {
            if (response.dealChangeDateValidationError) {
                $('#y_dealDepartureError').html(response.dealChangeDateValidationError);
                $('#y_dealDepartureError').parent().show();
                $('#dealDatePickerReturning').val('');
                $('.y_dealUpdateDate').attr('disabled', 'disabled');
            }
            else {
                $('#y_dealUpdateDates').html(response.dealChangeDateHtmlContent);
                ACC.packagedetails.bindDealDepartureDatePicker();
            }
        });
    },

    bindShowHideShowRoomOptions: function () {

        var buttonParent = $('.y_roomOptionsCollapse');
        var roomOptionsCollapse = $('#roomOptionsCollapse');

        roomOptionsCollapse.on('shown.bs.collapse', function () {
            buttonParent.find('.show-text').addClass('hidden');
            buttonParent.find('.hide-text').removeClass('hidden');
        });

        roomOptionsCollapse.on('hidden.bs.collapse', function () {
            buttonParent.find('.show-text').removeClass('hidden');
            buttonParent.find('.hide-text').addClass('hidden');
        });
    },

    bindAccommodationChange: function () {
        var previouslySelectedOptionId;
        $(".y_changeAccommodationButton").on("focus", function () {
            previouslySelectedOptionId = $(document).find('.y_changeAccommodationButton:checked').prop("id");
        }).on("change", function () {

            $('#y_processingModal').modal({
                backdrop: 'static',
                keyboard: false
            });

            var ratePlanAttributesDiv = $(this).closest(".y_ratePlanAttributes");
            var roomStayContainerDiv = $(this).closest(".y_roomStayContainer");

            var checkInDate = roomStayContainerDiv.find("input[class=y_checkInDate]").val();
            var checkOutDate = roomStayContainerDiv.find("input[class=y_checkOutDate]").val();
            var roomStayRefNumber = roomStayContainerDiv.find("input[class=y_roomStayRefNumber]").val();
            var accommodationCode = roomStayContainerDiv.find("input[class=y_accommodationCode]").val();
            var ratePlanCode = ratePlanAttributesDiv.find("input[class=y_ratePlanCode]").val();
            var listOfRoomRateCodes = [];
            var listOfRoomRateDates = [];
            ratePlanAttributesDiv.find("input[class=y_roomRate]").each(function () {
                listOfRoomRateCodes.push($(this).attr('code'));
                listOfRoomRateDates.push($(this).val());
            });

            var packageChangeAccommodationForm = $("#packageChangeAccommodationForm");
            packageChangeAccommodationForm.find("#y_checkInDate").attr('value', checkInDate);
            packageChangeAccommodationForm.find("#y_checkOutDate").attr('value', checkOutDate);
            packageChangeAccommodationForm.find("#y_accommodationCode").attr('value', accommodationCode);
            packageChangeAccommodationForm.find("#y_roomRateCodes").attr('value', listOfRoomRateCodes);
            packageChangeAccommodationForm.find("#y_roomRateDates").attr('value', listOfRoomRateDates);
            packageChangeAccommodationForm.find("#y_ratePlanCode").attr('value', ratePlanCode);
            packageChangeAccommodationForm.find("#y_roomStayRefNumber").attr('value', roomStayRefNumber);

            var currentlySelectedOption = $(this);

            $.when(ACC.services.addAccommodationToCartAjax(packageChangeAccommodationForm)).then(
                function (response) {
                    var jsonData = JSON.parse(response);

                    if (jsonData.valid) {
                        ACC.packagedetails.refreshPage("#roomOptionsCollapse");
                    } else {
                        var output = [];
                        jsonData.errors.forEach(function (error) {
                            output.push("<p>" + error + "</p>");
                        });
                        $("#y_addPackageToCartErrorModal").find(".y_addPackageToCartErrorBody").html(output.join(""));
                        $('#y_processingModal').modal("hide");
                        $("#y_addPackageToCartErrorModal").modal();

                        currentlySelectedOption.removeAttr('checked');
                        $("#" + previouslySelectedOptionId)[0].checked = true;
                    }
                }
            );
        });
    },

    amendPackageToCartOnLoad: function () {
        if ($("#y_amendPackageDetailsPage").is('input') && $("#y_amendPackageDetailsPage").val() == 'true') {

            if ($("#y_packageAccommodationAddToCartForm").length !== 0) {
                // reveal spinner inside reservation component
                $('.y_spinner').removeClass('hidden');
                // trigger package build message
                $('#package-build-modal').modal();
                // disable buttons on the page
                ACC.packagedetails.disableButtons();

                setTimeout(function () {
                    // remove package build message
                    ACC.packagedetails.hidePackageBuildModal();
                }, 2500);
            }

            $(window).on("load", function () {
                if ($("#y_packageAccommodationAddToCartForm").length === 0 || $("#y_packageAccommodationAddToCartForm").length === 0) {
                    ACC.packagedetails.enableButtons();
                    return false;
                }

                $.when(ACC.packagedetails.addAccommodationToCart()).then(function () {
                    if (!ACC.packagedetails.addAccommodationToCartResult) {
                        $('.y_spinner').addClass('hidden');
                        // hide spinner in reservation
                        $("#y_addPackageToCartErrorModal").modal();
                    }

                    ACC.reservation.refreshReservationTotalsComponent($("#y_reservationTotalsComponentId").val());
                    ACC.reservation.refreshAccommodationSummaryComponent($("#y_accommodationSummaryComponentId").val());

                    $('.y_spinner').addClass('hidden');
                    ACC.packagedetails.hidePackageBuildModal();
                    ACC.packagedetails.enableButtons();
                });
            });
        }
    },

    addPackageToCartOnLoad: function () {
        if ($("#y_amendPackageDetailsPage").is('input') && $("#y_amendPackageDetailsPage").val() == 'false') {

            if ($(".y_packageAddBundleToCartForm").length !== 0 && $("#y_packageAccommodationAddToCartForm").length !== 0) {
                // reveal spinner inside reservation component
                $('.y_spinner').removeClass('hidden');
                // trigger package build message
                $('#package-build-modal').modal();
                // disable buttons on the page
                ACC.packagedetails.disableButtons();

                setTimeout(function () {
                    // remove package build message
                    ACC.packagedetails.hidePackageBuildModal();
                }, 2500);
            }

            $(window).on("load", function () {
                if ($(".y_packageAddBundleToCartForm").length === 0 || $("#y_packageAccommodationAddToCartForm").length === 0) {
                    ACC.packagedetails.enableButtons();
                    return false;
                }

                $.when(ACC.packagedetails.addTransportBundleToCart()).then(function () {
                    if (!ACC.packagedetails.addTransportBundleToCartResult) {
                        $('.y_spinner').addClass('hidden');
                        // hide spinner in reservation
                        $("#y_addPackageToCartErrorModal").modal();
                    } else {
                        $.when(ACC.packagedetails.addAccommodationToCart()).then(function () {
                            if (!ACC.packagedetails.addAccommodationToCartResult) {
                                $('.y_spinner').addClass('hidden');
                                // hide spinner in reservation
                                $("#y_addPackageToCartErrorModal").modal();
                            } else {
                                ACC.reservation.refreshReservationTotalsComponent($("#y_reservationTotalsComponentId").val());
                                ACC.reservation.refreshAccommodationSummaryComponent($("#y_accommodationSummaryComponentId").val());
                                ACC.reservation.refreshTransportSummaryComponent($("#y_transportSummaryComponentId").val());

                                $('.y_spinner').addClass('hidden');
                                ACC.packagedetails.hidePackageBuildModal();
                                ACC.packagedetails.enableButtons();
                            }
                        });
                    }
                });
            });

        }
    },

    addTransportBundleToCart: function () {
        var dfd = $.Deferred();

        var packageAddBundleToCartFormList = $(".y_packageAddBundleToCartForm");
        ACC.packagedetails.addTransportBundleToCartResult = true;
        $.when(ACC.packagedetails.addTransportBundleEntry(packageAddBundleToCartFormList, 0)).then(function () {
            dfd.resolve();
        });
        return dfd.promise();
    },

    addTransportBundleEntry: function (formList, index) {
        if (index == (formList.length - 1)) {
            return $.when(ACC.services.addTransportBundleToCartForPackageAjax($(formList[index]))).then(function (response) {
                if (!response.valid) {
                    var output = [];
                    response.errors.forEach(function (error) {
                        output.push("<p>" + error + "</p>");
                    });
                    $("#y_addPackageToCartErrorModal").find(".y_addPackageToCartErrorBody").html(output.join(""));
                }
                ACC.packagedetails.addTransportBundleToCartResult = ACC.packagedetails.addTransportBundleToCartResult && response.valid;
            });
        } else {
            return $.when(ACC.services.addTransportBundleToCartForPackageAjax($(formList[index]))).then(function (response) {
                ACC.packagedetails.addTransportBundleToCartResult = ACC.packagedetails.addTransportBundleToCartResult && response.valid;
                if (!response.valid) {
                    var output = [];
                    response.errors.forEach(function (error) {
                        output.push("<p>" + error + "</p>");
                    });
                    $("#y_addPackageToCartErrorModal").find(".y_addPackageToCartErrorBody").html(output.join(""));
                    return;
                }
                return ACC.packagedetails.addTransportBundleEntry(formList, index + 1);
            });
        }
    },

    addAccommodationToCart: function () {
        var dfd = $.Deferred();

        var selectedRoomStays = $(".y_selectedRoomStayContainer");
        ACC.packagedetails.addAccommodationToCartResult = true;
        $.when(ACC.packagedetails.addAccommodationToCartEntry(selectedRoomStays, 0)).then(function () {
            dfd.resolve();
        });
        return dfd.promise();
    },

    addAccommodationToCartEntry: function (selectedRoomStays, index) {
        var packageAccommodationAddToCartForm = ACC.packagedetails.buildPackageAccommodationAddToCartForm(selectedRoomStays[index]);
        if (index == (selectedRoomStays.length - 1)) {
            return $.when(ACC.services.addAccommodationToCartForPackageAjax(packageAccommodationAddToCartForm)).then(function (response) {
                var jsonData = JSON.parse(response);
                if (!jsonData.valid) {
                    var output = [];
                    jsonData.errors.forEach(function (error) {
                        output.push("<p>" + error + "</p>");
                    });
                    $("#y_addPackageToCartErrorModal").find(".y_addPackageToCartErrorBody").html(output.join(""));
                }
                ACC.packagedetails.addAccommodationToCartResult = ACC.packagedetails.addAccommodationToCartResult && jsonData.valid;
            });
        } else {
            return $.when(ACC.services.addAccommodationToCartForPackageAjax(packageAccommodationAddToCartForm)).then(function (response) {
                var jsonData = JSON.parse(response);
                ACC.packagedetails.addAccommodationToCartResult = ACC.packagedetails.addAccommodationToCartResult && jsonData.valid;
                if (!jsonData.valid) {
                    var output = [];
                    jsonData.errors.forEach(function (error) {
                        output.push("<p>" + error + "</p>");
                    });
                    $("#y_addPackageToCartErrorModal").find(".y_addPackageToCartErrorBody").html(output.join(""));
                    return;
                }
                return ACC.packagedetails.addAccommodationToCartEntry(selectedRoomStays, index + 1);
            });
        }
    },

    buildPackageAccommodationAddToCartForm: function (element) {
        var checkInDate = $(element).find("input[class=y_checkInDate]").val();
        var checkOutDate = $(element).find("input[class=y_checkOutDate]").val();
        var accommodationCode = $(element).find("input[class=y_accommodationCode]").val();
        var ratePlanCode = $(element).find("input[class=y_ratePlanCode]").val();
        var roomStayRefNumber = $(element).find("input[class=y_roomStayRefNumber]").val();
        var listOfRoomRateCodes = [];
        var listOfRoomRateDates = [];
        $(element).find("input[class=y_roomRate]").each(function () {
            listOfRoomRateCodes.push($(this).attr('code'));
            listOfRoomRateDates.push($(this).val());
        });

        var packageAccommodationAddToCartForm = $("#y_packageAccommodationAddToCartForm");
        packageAccommodationAddToCartForm.find("#y_checkInDate").attr('value', checkInDate);
        packageAccommodationAddToCartForm.find("#y_checkOutDate").attr('value', checkOutDate);
        packageAccommodationAddToCartForm.find("#y_accommodationCode").attr('value', accommodationCode);
        packageAccommodationAddToCartForm.find("#y_roomRateCodes").attr('value', listOfRoomRateCodes);
        packageAccommodationAddToCartForm.find("#y_roomRateDates").attr('value', listOfRoomRateDates);
        packageAccommodationAddToCartForm.find("#y_ratePlanCode").attr('value', ratePlanCode);
        packageAccommodationAddToCartForm.find("#y_roomStayRefNumber").attr('value', roomStayRefNumber);

        return packageAccommodationAddToCartForm;
    },

    disableButtons: function () {
        $(".y_packageDetailsContinueButton").addClass('disabled');
        $(".y_roomOptionsCollapse").addClass('disabled');
        $(".y_flightOptionsCollapse").addClass('disabled');
    },

    enableButtons: function () {
        $(".y_packageDetailsContinueButton").removeClass('disabled');
        $(".y_roomOptionsCollapse").removeClass('disabled');
        $(".y_flightOptionsCollapse").removeClass('disabled');
    },

    hidePackageBuildModal: function () {
        if ($('#package-build-modal').is(":visible")) {
            $('#package-build-modal').modal('hide');
        }
    },

    bindOutboundSelectionButton: function () {
        $(document).on('click', '#y_outbound .y_fareResultSelect', function () {
            ACC.appmodel.previouslySelectedItineraryId = $("#y_outbound label.selected input").attr("id");
        }).on('change', '#y_outbound .y_fareResultSelect', function () {
            $('#y_processingModal').modal({
                backdrop: 'static',
                keyboard: false
            });
            ACC.appmodel.currentlySelectedItineraryId = $(this).attr("id");

            var outboundDepartureDate = new Date(parseInt($(this).val()));
            var selectedInboundDepartureDate = new Date(parseInt($("#y_selectedArrivalTime_1").val()));
            // Check if dateTimes are compatible, if not show modal and don't call addToCart
            if (selectedInboundDepartureDate) {
                // if the outbound date is after the inbound date, show validation modal
                if (outboundDepartureDate > selectedInboundDepartureDate) {
                    $('#y_processingModal').modal("hide");
                    if ($('html').hasClass('y_isMobile')) {
                        ACC.fareselection.contractExpandTravelSelection(false, $(this));
                    }
                    $("#y_addPackageToCartErrorModal .y_addPackageToCartErrorBody").html(ACC.addons.travelacceleratorstorefront['fareselection.validation.time.error']);
                    $('#y_addPackageToCartErrorModal').modal('show');

                    $(this).prop('checked', false);
                    $(this).removeClass('selected');
                    $("#" + ACC.appmodel.previouslySelectedItineraryId).prop('checked', true);
                    $("#" + ACC.appmodel.previouslySelectedItineraryId).parent().addClass('selected');
                    return;
                }
            }

            // remove selected class from other items
            if ($('#y_outbound label').hasClass('selected') && $('#y_outbound label').not(':checked')) {
                $('#y_outbound label').removeClass('selected');
            }
            // add selected class to this item
            $(this).parent().addClass('selected').siblings().removeClass('selected');

            //Call to server to addBundleToCart
            var addBundleToCartForm = $(this).parent().find(".y_addBundleToCartForm");
            ACC.packagedetails.addBundleToCartSubmit(addBundleToCartForm, this);
        });
    },

    bindInboundSelectionButton: function () {
        $(document).on('click', '#y_inbound .y_fareResultSelect', function () {
            ACC.appmodel.previouslySelectedItineraryId = $("#y_inbound label.selected input").attr("id");
        }).on('change', '#y_inbound .y_fareResultSelect', function () {
            $('#y_processingModal').modal({
                backdrop: 'static',
                keyboard: false
            });

            ACC.appmodel.currentlySelectedItineraryId = $(this).attr("id");

            var inboundDepartureDate = new Date(parseInt($(this).val()));
            var selectedOutboundDepartureDate = new Date(parseInt($("#y_selectedDepartureTime_0").val()));
            // Check if dateTimes are compatible, if not show modal and don't call addToCart
            if (selectedOutboundDepartureDate) {
                // if the inbound date is less than the outbound date, show validation modal
                if (inboundDepartureDate < selectedOutboundDepartureDate) {
                    $('#y_processingModal').modal("hide");
                    if ($('html').hasClass('y_isMobile')) {
                        ACC.fareselection.contractExpandTravelSelection(false, $(this));
                    }
                    $("#y_addPackageToCartErrorModal .y_addPackageToCartErrorBody").html(ACC.addons.travelacceleratorstorefront['fareselection.validation.time.error']);
                    $('#y_addPackageToCartErrorModal').modal('show');

                    $(this).prop('checked', false);
                    $(this).removeClass('selected');
                    $("#" + ACC.appmodel.previouslySelectedItineraryId).prop('checked', true);
                    $("#" + ACC.appmodel.previouslySelectedItineraryId).parent().addClass('selected');

                    return;
                }
            }

            // remove selected class from other items
            if ($('#y_inbound label').hasClass('selected') && $('#y_inbound label').not(':checked')) {
                $('#y_inbound label').removeClass('selected');
            }
            // add selected class to this item
            $(this).parent().addClass('selected').siblings().removeClass('selected');

            //Call to server to addBundleToCart
            var addBundleToCartForm = $(this).parent().find(".y_addBundleToCartForm");
            ACC.packagedetails.addBundleToCartSubmit(addBundleToCartForm, this);
        });
    },

    addBundleToCartSubmit: function (addBundleToCartForm, btnClicked) {
        if ($('html').hasClass('y_isMobile')) {
            ACC.fareselection.contractExpandTravelSelection(true, $(btnClicked));
        }
        var addToCartResult;
        $.when(ACC.services.addBundleToCartAjax(addBundleToCartForm)).then(
            function (response) {

                addToCartResult = response.valid;

                if (!response.valid) {
                    if ($('html').hasClass('y_isMobile')) {
                        ACC.fareselection.contractExpandTravelSelection(false, $(btnClicked));
                    }
                    var output = [];
                    response.errors.forEach(function (error) {
                        output.push("<p>" + error + "</p>");
                    });
                    $("#y_addPackageToCartErrorModal .y_addPackageToCartErrorBody").html(output.join(""));
                    $("#y_addPackageToCartErrorModal").modal();

                    if (response.minOriginDestinationRefNumber == undefined) {
                        $("#" + ACC.appmodel.currentlySelectedItineraryId).prop('checked', false);
                        $("#" + ACC.appmodel.currentlySelectedItineraryId).parent().removeClass('selected');
                        if (ACC.appmodel.previouslySelectedItineraryId) {
                            $("#" + ACC.appmodel.previouslySelectedItineraryId).prop('checked', true);
                            $("#" + ACC.appmodel.previouslySelectedItineraryId).parent().addClass('selected');
                        }
                    }
                    $('#y_processingModal').modal("hide");
                }
                else {
                    ACC.packagedetails.refreshPage("#flightOptionsCollapse");
                }
            });
        return addToCartResult;
    },

    refreshPage: function (elementID) {
        sessionStorage.setItem("elementToUncollapse", elementID);
        sessionStorage.setItem("scrollTop", $(this).scrollTop());
        location.reload(true);
    },

    changeButtonLabel: function (elementID) {
        if (elementID === "#flightOptionsCollapse") {
            $('#flightOptionsCollapse').prev('div').find('.show-text').addClass('hidden');
            $('#flightOptionsCollapse').prev('div').find('.hide-text').removeClass('hidden');
        }
        else if (elementID === "#roomOptionsCollapse") {
            $('.y_roomOptionsCollapse').find('.show-text').addClass('hidden');
            $('.y_roomOptionsCollapse').find('.hide-text').removeClass('hidden');
        }
    }

};

$(document).ready(function () {
    if (sessionStorage.getItem("scrollTop") !== null) {
        var elementID = sessionStorage.getItem("elementToUncollapse");
        ACC.packagedetails.changeButtonLabel(elementID);
        $(elementID).addClass("in");
        $(window).scrollTop(sessionStorage.getItem("scrollTop"));
        sessionStorage.clear();
    }
});
