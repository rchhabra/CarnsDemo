/**
 * The module for deal selection scripts.
 * @namespace
 */
ACC.dealselection = {
    _autoloadTracc: [
        "onload",
        "bindSearchMorePackages",
        "bindDealDepartureDatePicker",
        "bindAddDealToCartFormSubmit",
        "enableSubmitButtons"
    ],

    dealValidDates: [],

    onload: function () {
        jQuery(window).on("load", function () {
            $('.y_packageSearch').show();
        });
    },

    bindSearchMorePackages: function () {
        $(document).on('click', '.y_search_more_packages', function () {
            var context = ACC.tabbedfindercomponent.actions["package"];
            $.when(ACC.tabbedfindercomponent.refreshTravelFinderComponent(context, "PackageFinderComponent")).then(function () {
                var dealTravelFinderDiv = $(this).closest('.y_packageSearch').find('.y_travelFinderAttributes');
                ACC.dealselection.populateSearchMorePackageComponent(dealTravelFinderDiv);
            });

        });
    },

    populateSearchMorePackageComponent: function (dealTravelFinderDiv) {
        $('#y_roundTripRadbtn').prop("checked", true);
        $('.y_originLocationCode').val($(dealTravelFinderDiv).find('.y_departureLocation').val());
        var departureLocationCityName = $(dealTravelFinderDiv).find('.y_departureLocationCityName').val();
        if (departureLocationCityName) {
            $('.y_originLocation').val(departureLocationCityName + " - " + $(dealTravelFinderDiv).find('.y_departureLocationName').val() + " (" + $(dealTravelFinderDiv).find('.y_departureLocation').val() + ") ");
        }
        $('.y_originLocationSuggestionType').val($(dealTravelFinderDiv).find('.y_departureLocationSuggestionType').val());

        var arrivalLocationCityName = $(dealTravelFinderDiv).find('.y_arrivalLocationCityName').val();
        if (arrivalLocationCityName) {
            $('.y_destinationLocation').val(arrivalLocationCityName + " - " + $(dealTravelFinderDiv).find('.y_arrivalLocationName').val() + " (" + $(dealTravelFinderDiv).find('.y_arrivalLocation').val() + ") ");
        }
        $('.y_destinationLocationCode').val($(dealTravelFinderDiv).find('.y_arrivalLocation').val());
        $('.y_destinationLocationSuggestionType').val($(dealTravelFinderDiv).find('.y_arrivalLocationSuggestionType').val());
        if ($(dealTravelFinderDiv).find('.y_roomStayCandidates_passengerType_adult_quantity').val()) {
            $('.y_adultSelect').val($(dealTravelFinderDiv).find('.y_roomStayCandidates_passengerType_adult_quantity').val());
        }
        if ($(dealTravelFinderDiv).find('.y_roomStayCandidates_passengerType_child_quantity').val()) {
            $('.y_childSelect').val($(dealTravelFinderDiv).find('.y_roomStayCandidates_passengerType_child_quantity').val());
        }
        if ($(dealTravelFinderDiv).find('.y_roomStayCandidates_passengerType_infant_quantity').val()) {
            $('.y_infantSelect').val($(dealTravelFinderDiv).find('.y_roomStayCandidates_passengerType_infant_quantity').val());
        }
        $('.y_accommodationFinderLocationSuggestionType').val($(dealTravelFinderDiv).find('.y_suggestionType').val());
        if ($(dealTravelFinderDiv).find('.y_departingDateTime').val()) {
            $('.y_transportDepartDate').val($(dealTravelFinderDiv).find('.y_departingDateTime').val());
            $('.y_transportReturnDate').val($(dealTravelFinderDiv).find('.y_returnDateTime').val());
            $('.y_travelFinderDatePickerCheckIn').val($(dealTravelFinderDiv).find('.y_departingDateTime').val());
            $('.y_travelFinderDatePickerCheckOut').val($(dealTravelFinderDiv).find('.y_returnDateTime').val());
        }
    },

    bindDealDepartureDatePicker: function () {

        var dealDepartureDatePickerIds = $('.datePickerDeparting').map(function () {
            return $(this).attr('id');
        });
        for (var i = 0; i < dealDepartureDatePickerIds.length; i++) {
            $('#' + dealDepartureDatePickerIds[i]).on("keyup", function () {
                ACC.dealselection.dealValidDates = [];
            });
            $('#' + dealDepartureDatePickerIds[i]).datepicker({
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
                    ACC.dealselection.validateAndRefreshDealComponent(selectedDate,
                        this.getAttribute('data-dealcomponentid'),
                        this.getAttribute('data-dealbundletemplateid'));
                    ACC.dealselection.dealValidDates = [];
                    ACC.travelcommon.bindEqualHeights();
                }
            });
        }

    },

    validateAndRefreshDealComponent: function (dealSelectedDepartureDate, dealComponentId, dealbundletemplateid) {
        $.when(ACC.services.validateAndRefreshComponent(dealSelectedDepartureDate, dealComponentId, dealbundletemplateid)).then(function (response) {
            if (response.dealChangeDateValidationError) {
                $('#y_dealDepartureError_' + dealComponentId).html(response.dealChangeDateValidationError);
                $('#y_dealDepartureError_' + dealComponentId).parent().show();
                $('#dealReturning_' + dealComponentId).val('');
                $(".y_dealComponent_" + dealComponentId).find(':submit').attr('disabled', true);
            }
            else {
                var summaryHeight = $('.y_dealComponent_' + dealComponentId).find('.summary').css('height');
                var reviewsHeight = $('.y_dealComponent_' + dealComponentId).find('.reviews').css('height');
                $('.y_dealComponent_' + dealComponentId).replaceWith(response);
                $('.y_dealComponent_' + dealComponentId).find('.summary').css('height', summaryHeight);
                $('.y_dealComponent_' + dealComponentId).find('.reviews').css('height', reviewsHeight);
                ACC.dealselection.bindDealDepartureDatePicker();
                $('.y_packageSearch').show();
                if ($('.y_dealComponent_' + dealComponentId).find('.isPackageResponseDataAvailable').val() == "true") {
                    $($('.y_dealComponent_' + dealComponentId).find('.y_addDealToCartSubmit')).removeAttr("disabled");
                }
            }
        });
    },

    getDealValidDates: function (datePicker, month, year, dealStartDatePattern) {
        var selectedMonth = month;
        if (month < 10) {
            selectedMonth = "0" + month;
        }

        var fromDate = "01/" + selectedMonth + "/" + year;
        $.when(ACC.services.getDealValidDates(fromDate, dealStartDatePattern)).then(
            function (data) {
                ACC.dealselection.dealValidDates = [];
                for (var i = 0; i < data.length; i++) {
                    ACC.dealselection.dealValidDates.push(data[i].dealValidDate);
                }
                $(datePicker).datepicker("refresh");
            })
    },

    bindAddDealToCartFormSubmit: function () {
        $(document).on('submit', '.y_addDealToCartForm', function () {
            $(".y_addDealToCartSubmit").attr('disabled', 'disabled');
        });
    },

    enableSubmitButtons: function () {
        $('.y_addDealToCartSubmit').each(function () {
            if ($(this).closest('div').find('.isPackageResponseDataAvailable').val() == "true") {
                $(this).removeAttr("disabled");
            }
        });
    }

};
