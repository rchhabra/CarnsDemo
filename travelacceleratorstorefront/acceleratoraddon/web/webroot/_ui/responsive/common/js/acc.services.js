/**
 * The module for backend services.
 * @namespace
 */
ACC.services = {
		
    getAccommodationListForSelectedView: function (pagenumber, resultsViewType) {
        return $.ajax({
            url: ACC.config.contextPath + "/accommodation-search/display-view",
            type: "GET",
            contentType: "application/json; charset=utf-8",
            data: {
                pageNum: pagenumber,
                resultsViewType: resultsViewType
            }
        });
    },

    getPackageListForSelectedView: function (pagenumber, resultsViewType, priceRange) {
        return $.ajax({
            url: ACC.config.contextPath + "/package-listing/display-view",
            type: "GET",
            contentType: "application/json; charset=utf-8",
            data: {
                pageNum: pagenumber,
                resultsViewType: resultsViewType,
                priceRange: priceRange
            }
        });
    },

    getNearestTransportFacilityAjax: function (position, activity) {
        return $.ajax({
            url: "trip-finder/get-nearest-airport?activity=" + activity + "&latitude=" + position.coords.latitude + "&longitude=" + position.coords.longitude,
            type: "POST"
        });
    },

    // fetch destination locations using the originCode
    getDestinationLocationAjax: function (url) {
        return $.ajax({
            url: url,
            type: "POST"
        });
    },

    reloadDestinationLocation: function (serializedForm) {
        return $.ajax({
            type: "POST",
            url: "trip-finder/get-destination-locations.json",
            data: serializedForm
        });

    },

    addProductToCartAjax: function () {
        var form = $('.y_addToCartForm');
        return $.ajax({
            type: form.attr('method'),
            url: form.attr('action'),
            data: form.serialize()
        });

    },
    
    addProductsToCartForGroup: function(addToCartGroupForms) {
    	return $.ajax({
			contentType: 'application/json',
			type: 'POST',
            url: ACC.config.contextPath +"/cart/add/group",
            data: JSON.stringify(addToCartGroupForms)
        });
    },

    checkOfferGroupsRestriction: function (url) {
        return $.ajax({
            url: url,
            type: "GET"
        });
    },

    validateTransportOfferingStatusForm: function (serializedForm) {
        return $.ajax({
            url: ACC.config.contextPath + "/view/TransportOfferingStatusSearchComponentController/validate-transport-offering-status-form",
            type: "POST",
            data: serializedForm
        });
    },

    refreshTransportOfferingStatusResults: function (serializedForm) {
        return $.ajax({
            url: ACC.config.contextPath + "/transport-offering-status/refresh-transport-offering-status-results",
            type: "POST",
            data: serializedForm
        });
    },

    validateFareFinderFormAttributes: function (serializedForm) {
        return $.ajax({
            url: ACC.config.contextPath + "/view/FareFinderComponentController/validate-fare-finder-form",
            type: "POST",
            data: serializedForm
        });
    },

    sortingFareSelectionResults: function (fareFinderForm, refNumber, selectedSorting) {
        return $.ajax({
            url: ACC.config.contextPath + "/fare-selection/sorting-fare-selection-results",
            type: "POST",
            data: fareFinderForm + "&refNumber=" + refNumber + "&displayOrder=" + selectedSorting
        });
    },

    // Save Search.
    saveSearch: function (fareFinderForm) {
        return $.ajax({
            url: ACC.config.contextPath + "/fare-selection/save-search",
            type: "POST",
            data: fareFinderForm
        });
    },

    getSuggestedOriginLocationsAjax: function (originLocation) {
        return $.ajax({
            url: ACC.config.contextPath + "/suggestions/origin?text=" + originLocation,
            type: "GET"
        });
    },

    getSuggestedDestinationLocationsAjax: function (originLocation, destinationLocation) {
        return $.ajax({
            url: ACC.config.contextPath + "/suggestions/destination?text=" + destinationLocation + "&code=" + originLocation,
            type: "GET"
        });
    },

    getCurrentUserDetailsAjax: function () {
        return $.ajax({
            url: "traveller-details/get-current-user-details",
            dataType: "json",
            type: "GET"
        });
    },
    
    addRemoveSeats: function(addRemoveAccommodations) {
    	return $.ajax({
			contentType: 'application/json',
			type: 'POST',
            url: ACC.config.contextPath +"/cart/addremove/accommodations",
            data: JSON.stringify(addRemoveAccommodations)
        });
    },

    removeRoomAjax: function (href) {
        return $.ajax({
            url: href,
            type: "GET",
            contentType: "application/json; charset=utf-8"
        });
    },

    cancelBookingRequest: function (requestUrl, orderCode) {
        return $.ajax({
            url: ACC.config.contextPath + requestUrl + orderCode,
            type: "GET",
            contentType: "application/json; charset=utf-8"
        });
    },

    cancelTraveller: function (orderCode, travellerUid) {
        return $.ajax({
            url: ACC.config.contextPath + "/manage-booking/cancel-traveller-request/",
            type: "GET",
            contentType: "application/json; charset=utf-8",
            data: {
                orderCode: orderCode,
                travellerUid: travellerUid
            }
        });
    },

    getSeatMapAjax: function () {
    	var url=ACC.config.contextPath + "/ancillary/accommodation-map";
        return $.ajax({
            url: url,
            type: "GET"
        });
    },

    getAmendSeatMapAjax: function () {
        var url=ACC.config.contextPath + "/manage-booking/ancillary/accommodation-map";
        return $.ajax({
            url: url,
            type: "GET"
        });
    },

    upgradeBundle: function () {
        var url = $('#y_upgradeBundleForm').attr('action');
        var selectedBundleType = $('.y_bundleType:checked').parents('.active').find('.y_bundleType:checked').val();
        var refNumber = $('.y_bundleType:checked').parents('.active').find('#y_refNumber').val();
        return $.ajax({
            type: "POST",
            url: url,
            data: {
                selectedBundleType: selectedBundleType,
                refNumber: refNumber
            }
        });

    },

    renewSession: function () {
        var url = ACC.config.contextPath + "/session-cookie/renew-session/";
        return $.ajax({
            url: url,
            type: "GET"
        });
    },

    validateAccommodationFinderForm: function (serializedForm) {
        return $.ajax({
            url: ACC.config.contextPath + "/view/AccommodationFinderComponentController/validate-accommodation-finder-form",
            type: "POST",
            data: serializedForm
        });
    },

    getSuggestedAccommodationLocationsAjax: function (destinationLocation) {
        return $.ajax({
            url: ACC.config.contextPath + "/accommodation-suggestions?text=" + destinationLocation,
            type: "GET"
        });
    },

    readCustomerReviews: function (accommodationOfferingCode) {
        return $.ajax({
            url: ACC.config.contextPath + "/accommodation-search/customer-review/" + accommodationOfferingCode,
            type: "GET",
            contentType: "application/json; charset=utf-8"
        });
    },

    getPagedAccommodationDetailsCustomerReviews: function (accommodationOfferingCode, pageNumber) {
        return $.ajax({
            url: ACC.config.contextPath + "/accommodation-details/customer-review?accommodationOfferingCode=" + accommodationOfferingCode + "&pageNumber=" + pageNumber,
            type: "GET",
            contentType: "application/json; charset=utf-8"
        });
    },

    addAccommodationToCartAjax: function (form) {
        return $.ajax({
            type: form.attr('method'),
            url: form.attr('action'),
            data: form.serialize()
        });
    },

    addTransportBundleToCartAjax: function (form) {
        return $.ajax({
            type: form.attr('method'),
            url: form.attr('action'),
            data: form.serialize()
        });
    },

    addAccommodationToCartForPackageAjax: function (form) {
        return $.ajax({
            type: form.attr('method'),
            url: form.attr('action'),
            data: form.serialize()
        });
    },

    addTransportBundleToCartForPackageAjax: function (form) {
        return $.ajax({
            type: form.attr('method'),
            url: form.attr('action'),
            data: form.serialize()
        });
    },

    validateLeadGuestDetailsForms: function (serializedForm) {
        return $.ajax({
            url: ACC.config.contextPath + "/checkout/guest-details/validate-lead-guest-details-forms",
            type: "POST",
            data: serializedForm
        });
    },

    getAccommodationListing: function (pagenumber, resultsViewType) {
        return $.ajax({
            url: ACC.config.contextPath + "/accommodation-search/show-more?pageNumber=" + pagenumber + "&resultsViewType=" + resultsViewType,
            type: "GET",
            contentType: "application/json; charset=utf-8"
        });
    },

    getPackageListing: function (pagenumber, resultsViewType, priceRange) {
        return $.ajax({
            url: ACC.config.contextPath + "/package-listing/show-more",
            type: "GET",
            contentType: "application/json; charset=utf-8",
            data: {
                pageNumber: pagenumber,
                resultsViewType: resultsViewType,
                priceRange: priceRange
            }
        });
    },

    getPackageListingWithPriceRangeFilter: function (resultsViewType, pageNumber, priceRange) {
        return $.ajax({
            url: ACC.config.contextPath + "/package-listing/filter-price-range",
            type: "GET",
            contentType: "application/json; charset=utf-8",
            data: {
                resultsViewType: resultsViewType,
                pageNum: pageNumber,
                priceRange: priceRange
            }
        });
    },

    addExtraToCartAjax: function () {
        var form = $('#y_addExtraToCartForm');
        return $.ajax({
            type: form.attr('method'),
            url: form.attr('action'),
            data: form.serialize()
        });
    },

    validateAccommodationCartAjax: function () {
        return $.ajax({
            url: ACC.config.contextPath + "/accommodation-details/validate-cart",
            type: "GET"
        });
    },

    refreshTransportSummaryComponent: function (componentUid) {
        $.ajaxSetup({cache: false});
        return $.ajax({
            url: ACC.config.contextPath + "/view/TransportSummaryComponentController/refresh",
            type: "GET",
            data: {componentUid: componentUid}
        });
    },

    refreshReservationTotalsComponent: function (componentUid) {
        $.ajaxSetup({cache: false});
        return $.ajax({
            url: ACC.config.contextPath + "/view/ReservationTotalsComponentController/refresh",
            type: "GET",
            data: {componentUid: componentUid}
        });
    },

    refreshAccommodationSummaryComponent: function (componentUid) {
        $.ajaxSetup({cache: false});
        return $.ajax({
            url: ACC.config.contextPath + "/view/AccommodationSummaryComponentController/refresh",
            type: "GET",
            data: {componentUid: componentUid}
        });

    },

    getTransportReservationComponent: function (componentId) {
        $.ajaxSetup({cache: false});
        return $.ajax({
            url: ACC.config.contextPath + "/view/TransportReservationComponentController/load",
            type: "GET",
            data: {
                componentUid: componentId
            }
        });
    },

    getAccommodationReservationComponent: function (componentId) {
        $.ajaxSetup({cache: false});
        return $.ajax({
            url: ACC.config.contextPath + "/view/AccommodationReservationComponentController/load",
            type: "GET",
            data: {
                componentUid: componentId
            }
        });
    },

    getReservationOverlayTotalsComponent: function (componentId) {
        $.ajaxSetup({cache: false});
        return $.ajax({
            url: ACC.config.contextPath + "/view/ReservationOverlayTotalsComponentController/load",
            type: "GET",
            data: {componentUid: componentId}
        });
    },

    updateBookingDates: function () {
        var form = $("#y_updateBookingDatesForm");
        return $.ajax({
            url: form.attr('action'),
            type: form.attr('method'),
            data: form.serialize()
        });
    },

    validateTravelFinderForm: function (serializedForm) {
        return $.ajax({
            url: ACC.config.contextPath + "/view/TravelFinderComponentController/validate-travel-finder-form",
            type: "POST",
            data: serializedForm
        });
    },

    validatePackageFinderForm: function (serializedForm) {
        return $.ajax({
            url: ACC.config.contextPath + "/view/PackageFinderComponentController/validate-travel-finder-form",
            type: "POST",
            data: serializedForm
        });
    },

    refreshTravelFinderComponent: function (context, componentUid) {
        $.ajaxSetup({cache: false});
        return $.ajax({
            url: context,
            ype: "GET",
            data: {
                componentUid: componentUid
            }
        });
    },

    submitPaymentOptionForm: function (formID) {
        var form = $(formID);
        return $.ajax({
            type: form.attr('method'),
            url: form.attr('action'),
            data: form.serialize()
        });

    },

    validatePaymentOptions: function () {
        var url = ACC.config.contextPath + "/checkout/multi/payment-method/check-payment-options";
        return $.ajax({
            url: url,
            type: "GET"
        });
    },

    getDealValidDates: function (dealDepartureDate, dealStartingDatePattern) {
        return $.ajax({
            url: ACC.config.contextPath + "/view/DealComponentController/get-valid-dates",
            type: "GET",
            contentType: "application/json; charset=utf-8",
            data: {
                dealDepartureDate: dealDepartureDate,
                dealStartingDatePattern: dealStartingDatePattern
            }
        });
    },

    validateAndRefreshComponent: function (dealSelectedDepartureDate, dealComponentId, dealBundleTemplateId) {
        return $.ajax({
            url: ACC.config.contextPath + "/view/DealComponentController/validate-departure-date",
            type: "GET",
            contentType: "application/json; charset=utf-8",
            data: {
                dealSelectedDepartureDate: dealSelectedDepartureDate,
                dealComponentId: dealComponentId,
                dealBundleTemplateId: dealBundleTemplateId
            }
        });
    },

    validateSelectedDepartureDate: function (dealSelectedDepartureDate, dealBundleTemplateId) {
        return $.ajax({
            url: ACC.config.contextPath + "/deal-details/validate-departure-date",
            type: "GET",
            contentType: "application/json; charset=utf-8",
            data: {
                dealSelectedDepartureDate: dealSelectedDepartureDate,
                dealBundleTemplateId: dealBundleTemplateId
            }
        });
    },

    addBundleToCartAjax: function (form) {
        return $.ajax({
            type: form.attr('method'),
            url: form.attr('action'),
            data: form.serialize()
        });
    },

    validatePersonalDetailsForms: function (serializedForm) {
        return $.ajax({
            url: ACC.config.contextPath + "/checkout/personal-details/validate-personal-details-forms",
            type: "POST",
            data: serializedForm
        });
    },

    addRoomPreference: function (roomStayRefNum, roomPreferenceCode) {
        return $.ajax({
            url: ACC.config.contextPath + "/cart/accommodation/save-room-preference",
            type: "POST",
            data: {
                roomStayRefNum: roomStayRefNum,
                roomPreferenceCode: roomPreferenceCode
            }
        });
    },

    validateTravellerDetailsForms: function (serializedForm) {
        return $.ajax({
            url: ACC.config.contextPath + "/checkout/traveller-details/validate-traveller-details-forms",
            type: "POST",
            data: serializedForm
        });
    },

    getUpgradeBundleOptions: function () {
        return $.ajax({
            url: ACC.config.contextPath + "/ancillary/upgrade-bundle-options",
            type: "GET"
        });
    },

    getPackageUpgradeBundleOptions: function () {
        return $.ajax({
            url: ACC.config.contextPath + "/ancillary-extras/upgrade-bundle-options",
            type: "GET"
        });
    },

    getAddRoomOptions: function (orderCode) {
        return $.ajax({
            url: ACC.config.contextPath + "/view/TravelBookingDetailsComponentController/add-room-options",
            type: "POST",
            data: {orderCode: orderCode}
        });
    },

    validateAddRoomAccommodationFinderForm: function (serializedForm, orderCode) {
        return $.ajax({
            url: ACC.config.contextPath + "/view/TravelBookingDetailsComponentController/validate-accommodation-availability-form/" + orderCode,
            type: "POST",
            data: serializedForm
        });
    },

    getSuggestedNamesAjax: function (firstNameText, passengerType, requestMapping) {
        return $.ajax({
            url: ACC.config.contextPath + "/checkout/traveller-details/suggestions/" + requestMapping + "?text=" + firstNameText + "&passengerType=" + passengerType,
            type: "GET"
        });
    },

    manageBookingLogin: function (serializedForm) {
        return $.ajax({
            type: "POST",
            url: ACC.config.contextPath + "/manage-booking/login",
            data: serializedForm
        });
    },
    
    getSeatMapSvgCss:function(vehicleCode){
    	return $.ajax({
            url: "/travelseatmapservices/seatmap/getseatmap/" + vehicleCode,
            type: "GET",
            contentType: "application/json; charset=utf-8",
        });
    	
    }
};
