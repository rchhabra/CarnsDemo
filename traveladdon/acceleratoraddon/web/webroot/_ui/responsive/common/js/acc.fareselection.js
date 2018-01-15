ACC.fareselection = {

    defaultNumberOfFares : 5,
    serializedForm : "",

    _autoloadTracc : [
        [ "bindOutboundSelectionButton", $(".y_fareSelectionPage").length !== 0],
        [ "bindInboundSelectionButton", $(".y_fareSelectionPage").length !== 0],
        "bindFareSelectionContinue",
        "init",
        "bindExpandTravelSelection",
        "bindWindowResize",
        "bindShowMore",
        "bindMobilePriceArrowToggle",
        "bindMobileTripDetailsToggle",
        "setFromPriceForOptions",
        [ "bindSortingSelection", $(".y_fareSelectionSection").length !== 0 ],
        "hideProcessingModal",
        "bindSaveSearch"
    ],

    init : function() {
        if($("#y_processingModal").hasClass('show')){
            //remove colorbox if the processingModal is visible to resolve conflicts
            $.colorbox.remove();
        }
        ACC.fareselection.disableUnavailableOutboundTabs();
        ACC.fareselection.showFares("#y_outbound");
        ACC.fareselection.preselectOption("#y_outbound");
        ACC.fareselection.serializedForm = $("#y_fareFinderForm").serialize();
        if($('#y_inbound') && $('#y_inbound').length > 0) {
            ACC.fareselection.disableUnavailableInboundTabs();
            ACC.fareselection.showFares("#y_inbound");
            ACC.fareselection.preselectOption("#y_inbound");
        }
        if($('html').hasClass('y_isMobile')) {
            if($("#y_outbound label.selected input").length>0){
                ACC.fareselection.contractExpandTravelSelection(true, $("#y_outbound label.selected input"));
            }
            if($("#y_inbound label.selected input").length>0){
                ACC.fareselection.contractExpandTravelSelection(true, $("#y_inbound label.selected input"));
            }
        }
    },

    bindOutboundSelectionButton : function() {
        $(document).on('click', '#y_outbound .y_fareResultSelect', function() {
            ACC.appmodel.previouslySelectedItineraryId = $("#y_outbound label.selected input").attr("id");
        }).on('change', '#y_outbound .y_fareResultSelect', function() {
            ACC.fareselection.disableButtonsOnPage();
            ACC.appmodel.currentlySelectedItineraryId = $(this).attr("id");
            ACC.appmodel.trips.outbound.dateTime = new Date(parseInt($(this).val()));

            // remove selected class from other items
            if($('#y_outbound label').hasClass('selected') && $('#y_outbound label').not(':checked')) {
                $('#y_outbound label').removeClass('selected');
            }
            // add selected class to this item
            $(this).parent().addClass('selected').siblings().removeClass('selected');

            //Call to server to addBundleToCart
            var addBundleToCartForm = $(this).parent().find(".y_addBundleToCartForm");
            ACC.fareselection.addBundleToCartSubmit(addBundleToCartForm,this);
        });
    },

    bindInboundSelectionButton : function() {
        $(document).on('click', '#y_inbound .y_fareResultSelect', function() {
            ACC.appmodel.previouslySelectedItineraryId = $("#y_inbound label.selected input").attr("id");
        }).on('change', '#y_inbound .y_fareResultSelect', function() {
            ACC.fareselection.disableButtonsOnPage();
            ACC.appmodel.currentlySelectedItineraryId = $(this).attr("id");
            var inboundDepartureDate = new Date(parseInt($(this).val()));
            // Check if dateTimes are compatible, if not show modal and don't call addToCart
            if (ACC.appmodel.trips.outbound.dateTime) {
                // if the inbound date is less than the outbound date, show validation modal
                if (inboundDepartureDate < ACC.appmodel.trips.outbound.dateTime) {
                    ACC.fareselection.enableButtonsOnPage();
                    if($('html').hasClass('y_isMobile')) {
                        ACC.fareselection.contractExpandTravelSelection(false, $(this));
                    }
                    $("#y_addBundleToCartValidationModal .y_addBundleToCartValidationBody").html(ACC.addons.travelacceleratorstorefront['fareselection.validation.time.error']);
                    $('#y_addBundleToCartValidationModal').modal('show');

                    $(this).prop('checked', false);
                    $(this).removeClass('selected');
                    $("#" + ACC.appmodel.previouslySelectedItineraryId).prop('checked', true);
                    $("#" + ACC.appmodel.previouslySelectedItineraryId).parent().addClass('selected');

                    return;
                }
            }

            // remove selected class from other items
            if($('#y_inbound label').hasClass('selected') && $('#y_inbound label').not(':checked')) {
                $('#y_inbound label').removeClass('selected');
            }
            // add selected class to this item
            $(this).parent().addClass('selected').siblings().removeClass('selected');

            //Call to server to addBundleToCart
            var addBundleToCartForm = $(this).parent().find(".y_addBundleToCartForm");
            ACC.fareselection.addBundleToCartSubmit(addBundleToCartForm,this);
        });
    },

    disableButtonsOnPage: function(){
        $('.y_fareResultSelect').attr('disabled',true);
        $(".y_fareSelectionContinueButton").addClass('disabled');
        $(".y_seeFullReservationBtn").attr('disabled', true);
        $(".y_reservationSideBar .title-collapse a").bind('click', false);
        $(".y_reservationTotalsComponent .y_fareSlide").bind('click', false).addClass('inactive');
        $('.y_spinner').removeClass('hidden');
        $('.y_reservationSideBarContent').addClass('content-fade');
    },

    enableButtonsOnPage: function(){
        $('.y_fareResultSelect').filter(function( index ) {
            return $( this ).siblings().filter('.price-desc').html() != '—';
        }).attr('disabled',false);
        $(".y_fareSelectionContinueButton").removeClass('disabled');
        $(".y_seeFullReservationBtn").attr('disabled', false);
        $(".y_reservationSideBar .title-collapse a").unbind('click', false);
        $(".y_reservationTotalsComponent .y_fareSlide").unbind('click', false).removeClass('inactive');
        $('.y_spinner').addClass('hidden');
        $('.y_reservationSideBarContent').removeClass('content-fade');
    },

    // expands or contracts the travel choice made by user on mobile
    // parameter is boolean (true = contract, false = expand)
    contractExpandTravelSelection : function(contract, $elem) {
        // by default it's contract
        var addClass = "addClass",
            removeClass = "removeClass",
            slideMethod = "slideUp",
            disable = true,
            hide = "hide";
        // if it's a expand we change the actions to be the opposite
        if(!contract){
            addClass = "removeClass",
                removeClass = "addClass",
                slideMethod = "slideDown",
                disable = false,
                hide = "show";
        }

        var travelClassSelectedClass = '.y_fareResultSelected',
            travelClassOptionsClass = '.y_fareResultClassSelectGroup',
            travelOptionWrapperClass = '.y_fareResultTravelOption',
            travelDetails = '.y_fareResultTravelDetails',
            navTabsClass = '.y_fareResultNavTabs',
            tabWrapper = '.y_fareResultTabWrapper',
            travelSortSection = '.y_fareResultSort',
            buttonPrice = '.y_fareResultPriceButton',
            lowestPrice = '.y_fareResultLowestPrice',
            buttonContent = buttonPrice + ' span',
            buttonChangeElement = $('.y_fareResultChangeButton'),
            showMoreElement = $('.y_fareResultShowMore'),
            stopDivide = $('.y_fareResultStopDivide'),
            slideSpeed = 300;

        var $travelOptionWrapper = $elem.parents(travelOptionWrapperClass),
            $travelDetails = $travelOptionWrapper.find(travelDetails),
            $travelOptions = $travelOptionWrapper.find(travelClassOptionsClass),
            $tabs = $elem.parents(tabWrapper);

        $travelOptionWrapper[addClass]('y_fareResultSelected selected');

        // animate current section to a grey background and dark text 
        $travelDetails[addClass]('invert-theme');

        // collapse current travel details
        if(contract){
            $travelOptions.siblings(travelDetails).find('.panel-collapse')[slideMethod](slideSpeed, function(){
                $travelOptions.siblings(travelDetails).find('.panel-collapse')["removeClass"]('in');
            });
        }

        // make the stop divide selected?
        $travelOptions.siblings().find(stopDivide)[addClass]('selected');

        // collapse the 'sort-by' section
        $elem.parents('.active').find(travelSortSection)[slideMethod](slideSpeed);

        // collapse all 'class options' (other than the current one)
        $elem.parents('ul').find(travelOptionWrapperClass).not(travelClassSelectedClass)[slideMethod](slideSpeed);

        // collapse current 'class options'
        $travelOptions[slideMethod](slideSpeed);

        // collapse current 'tabs'
        $tabs.children(navTabsClass)[slideMethod](slideSpeed);

        // collapse current 'show more' button
        $tabs.find(showMoreElement)[slideMethod](slideSpeed);

        // hide everything inside the price details except for the actual price (i.e. the 'From' text and 'arrow icon')
        $travelOptionWrapper.find($(buttonContent).not('.fare-decimal').not(lowestPrice))[hide]();

        // hide 'price' button
        $travelOptionWrapper.find($(buttonPrice)).attr('disabled', disable).css('top','-10px');

        // show 'change' button
        $travelOptionWrapper.find(buttonChangeElement).attr('disabled', !disable)[removeClass]('hidden');

        if(contract){
            // scroll to the top of the current section
            $('html, body').animate({
                scrollTop: $('.fare-table-inner-wrap')
                    .offset()
                    .top -70 }, slideSpeed);
        }
    },

    // this is used to reset the travel selection on Mobile (used when we change to Desktop view)
    resetMobileTravelSelection : function() {
        // reset arrows back to the contracted form
        $('.y_fareResultPriceButton .arrow').html('&#9656;').removeClass('expanded');

    },

    bindExpandTravelSelection : function() {

        // expand
        $('.y_fareResultChangeButton').on("click", function(e) {
            if($('html').hasClass('y_isMobile')) {
                e.preventDefault;
                ACC.fareselection.contractExpandTravelSelection(false, $(this));
            }

        });

    },

    // bindWindowResize removes & re-runs bindExpandTravelSelection() based on orientation, further details below
    bindWindowResize : function() {
        // below listener to detect window resize

        $(window).resize(function() {
            if(!$('html').hasClass('y_isMobile')){
                ACC.fareselection.resetMobileTravelSelection();
                // When on Desktop view, open up all sections that were closed mobile view
                ACC.fareselection.contractExpandTravelSelection(false, $(".y_fareResultChangeButton").not( ".hidden"));
            }
        });
    },

    // bindShowMore shows hidden travel results & disableds button when no more to display
    bindShowMore : function() {
        $(".y_fareSelectionSection").on('click', '.y_fareResultShowMore button' ,function(){
            $(this).parents('.y_fareResultTabContent')
                .find('li.y_pricedItinerary:hidden:lt(' + ACC.fareselection.defaultNumberOfFares + ')')
                .show();

            if($(this).parents('.y_fareResultTabContent').find('li.y_pricedItinerary:hidden').length === 0){
                $(this).hide();
            }
        });
    },

    bindFareSelectionContinue : function() {
        $(document).on("click", ".y_fareSelectionContinueButton", function(e) {
            var tripType = $("#y_tripType").val();
            if (tripType == "SINGLE") {
                if ($('#y_outbound :has(:radio:checked)').length == 0 ) {
                    e.preventDefault();
                    $('#y_fareSelectionValidationModal').modal('show');
                    return false;
                }
            }
            if (tripType == "RETURN") {
                if ($('#y_outbound :has(:radio:checked)').length == 0 || $('#y_inbound :has(:radio:checked)').length == 0 ) {
                    e.preventDefault();
                    $('#y_fareSelectionValidationModal').modal('show');
                    return false;
                }
            }
        });
    },

    bindMobilePriceArrowToggle : function() {
        // Show/hide class select on mobile (arrows pointing down)
        $(".y_fareSelectionSection").on('click', '.y_fareResultPriceButton' ,function(){
            $('.arrow', this).toggleClass('expanded');
            if($('.arrow', this).hasClass('expanded')) {
                $('.arrow', this).html('&#9662;');
            } else {
                $('.arrow', this).html('&#9656;');
            }
            // get the ClassSelectGroup related to the button pressed and toggle to hide or show the contents
            $(this).parents('.y_fareResultTravelOption').children('.y_fareResultClassSelectGroup').slideToggle();

        });
    },

    bindMobileTripDetailsToggle : function() {
        $(".y_fareSelectionSection").on('click', '.y_fareResultInfoTrigger' ,function(){
            $(this).next().slideToggle();
        });
    },

    disableUnavailableOutboundTabs : function() {

        var listOfLinks = $('#y_outbound .y_tab a');
        var selectedInboundTab = $('#y_inbound .y_tab.active a');
        var previous = $('#y_outbound .y_previous');
        var next = $('#y_outbound .y_next');

        ACC.fareselection.disableUnavailableTabs('outbound', listOfLinks,
            selectedInboundTab, next, previous);
    },

    disableUnavailableInboundTabs : function() {

        var listOfLinks = $('#y_inbound .y_tab a');
        var selectedOutboundTab = $('#y_outbound .y_tab.active a');
        var previous = $('#y_inbound .y_previous');
        var next = $('#y_inbound .y_next');

        ACC.fareselection.disableUnavailableTabs('inbound', listOfLinks,
            selectedOutboundTab, next, previous);

    },

    disableUnavailableTabs : function(leg, listOfLinks, selectedOtherTab, next,	previous) {
        var today = new Date();
        today.setHours(0, 0, 0, 0);

        if (listOfLinks) {
            for (var i = 0; i < listOfLinks.length; i++) {
                // check if not smaller than today
                var dateValue = parseInt($(listOfLinks[i]).attr("dateValue"));
                var currentTabDate = new Date(dateValue);
                currentTabDate.setHours(0, 0, 0, 0);
                if (today > currentTabDate) {
                    ACC.fareselection.disableLink($(listOfLinks[i]), previous,
                        next, i);
                } else if (selectedOtherTab) {
                    var otherDateValue = parseInt(selectedOtherTab.attr("dateValue"));
                    var selectedOtherDate = new Date(otherDateValue);
                    selectedOtherDate.setHours(0, 0, 0, 0);
                    switch (leg) {
                        case 'outbound':
                            if (selectedOtherDate < currentTabDate) {
                                ACC.fareselection.disableLink($(listOfLinks[i]),
                                    previous, next, i);
                            }
                            break;
                        case 'inbound':
                            if (selectedOtherDate > currentTabDate) {
                                ACC.fareselection.disableLink($(listOfLinks[i]),
                                    previous, next, i);
                            }
                            break;
                    }
                }
            }
        }
    },

    disableLink : function(link, previous, next, index) {
        link.parent().addClass("disabled");
        link.attr("href", "javascript:void(0)");
        if (index === 1) {
            previous.attr("href", "javascript:void(0)");
        } else if (index === 3) {
            next.attr("href", "javascript:void(0)");
        }
    },

    bindSortingSelection : function() {
        $(window).on("unload", function() {
            $('.y_sorting-options').each(function () {
                if($(this).is('select')){
                    $(this).find('option').remove();
                }
            });
        });
        // maintain sort order when page is reloaded or browser back button is used
        var fareFinderForm = ACC.fareselection.serializeFareFinderForm();
        $.when(ACC.fareselection.sortSelection(fareFinderForm, 0, $('.y_sorting-options[id="outbound-sort-by"]').find(":selected").val())).then(function(){
            if($('#y_inbound') && $('#y_inbound').length > 0) {
                ACC.fareselection.sortSelection(fareFinderForm, 1, $('.y_sorting-options[id="inbound-sort-by"]').find(":selected").val());
            }
        });

        $('.y_sorting-options').on('change', function() {
            var refNumber = $(this).attr("refNumber");
            var selectedSorting = $(this).find(":selected").val();
            var fareFinderForm = ACC.fareselection.serializeFareFinderForm();

            ACC.travelcommon.changeUrlParams(["displayOrder"], [selectedSorting]);
            ACC.fareselection.sortSelection(fareFinderForm, refNumber, selectedSorting);
        });
    },

    sortSelection : function(fareFinderForm, refNumber, selectedSorting) {
       return $.when( ACC.services.sortingFareSelectionResults(fareFinderForm, refNumber, selectedSorting)).then(
            function( data, textStatus, jqXHR ) {
                var jsonData = JSON.parse(data);
                var classStr = "#flight-option-" + jsonData.refNumber;
                $(classStr).html($(classStr, jsonData.htmlContent).html());
                ACC.travelcommon.bindCheckMobile();
                ACC.fareselection.bindExpandTravelSelection();
                ACC.fareselection.setFromPriceForOptions();
                ACC.fareselection.showFares("#y_outbound");
                ACC.fareselection.preselectOption("#y_outbound");
                if($('#y_inbound') && $('#y_inbound').length > 0) {
                    ACC.fareselection.showFares("#y_inbound");
                    ACC.fareselection.preselectOption("#y_inbound");
                }
            }
        );
    },

    setFromPriceForOptions : function(){
        $('.y_fareResultTravelOption').each(
            function(index,element){
                var listOfPrices = $(element).find('.y_fareResultClassSelectGroup .price-desc'),
                    price;
                for(var i=0; i<listOfPrices.length; i++){
                    price = $(listOfPrices[i]).html();
                    if(price != 'X'){
                        break;
                    }
                }
                $(element).find('.y_fareResultLowestPrice').html(price.replace(/(\D*)(\d*\.)(\d*)/,'$1$2<span class="fare-decimal">$3</span>'));
            });
    },

    showFares : function(fareType, fromIndex) {
        $(fareType).find('.y_fareResultTabContent li.y_pricedItinerary:lt(' + ACC.fareselection.defaultNumberOfFares + ')').show();
        if($(fareType).find('.y_fareResultTabContent li.y_pricedItinerary').length > ACC.fareselection.defaultNumberOfFares){
            $(fareType).find('.y_fareResultTabContent .y_fareResultShowMore button').show();
        }
    },

    preselectOption : function(journeyDiv) {
        var selectedPricedItinerary = $(journeyDiv).find("input:checked").closest(".y_pricedItinerary");
        if($(selectedPricedItinerary).is(":hidden"))
        {
            $(selectedPricedItinerary).show();
        }
        $(journeyDiv).find("label[class='selected']").find("input[type='radio']").prop("checked", true)
    },

    hideProcessingModal: function(){
        if($("#y_processingModal").hasClass('show')){
            // if the processingModal is visible hide
            $("#y_processingModal").removeClass("show");

            // if the enable360View is true, then open the customer360 View
            if($( "#enable360View" ).val()) {
                openCustomer360Colorbox($);
            }
            else
            {
                // otherwise, just re-initialize colorbox
                openCustomer360Colorbox($('.js-customer360'));
            }
        }
    },

    getPassengerTypeCodeList: function(){
        var passengerTypeCodeList = new Array();
        $(".y_passengerTypeCode").each(function()
        {
            if ($.inArray($(this).val(), passengerTypeCodeList) == -1)
            {
                passengerTypeCodeList.push($(this).val());
            }
        });

        return passengerTypeCodeList;
    },

    serializeFareFinderForm : function() {
        var fareFinderSerialized = "";

        if ($('#y_roundTripRadbtn').is(":checked"))
        {
            fareFinderSerialized = "tripType=RETURN";
        } else if ($('#y_oneWayRadbtn').is(':checked')){
            fareFinderSerialized = "tripType=SINGLE";
        }
        fareFinderSerialized += "&departureLocationName="+$('.y_originLocation').val();
        fareFinderSerialized += "&departureLocation="+$('.y_originLocationCode').val();
        fareFinderSerialized += "&arrivalLocationName="+$('.y_destinationLocation').val();
        fareFinderSerialized += "&arrivalLocation="+$('.y_destinationLocationCode').val();
        fareFinderSerialized += "&departingDateTime="+$('.y_transportDepartDate').val();
        fareFinderSerialized += "&returnDateTime="+$('.y_transportReturnDate').val();
        fareFinderSerialized += "&departureLocationSuggestionType="+$('.y_originLocationSuggestionType').val();
        fareFinderSerialized += "&arrivalLocationSuggestionType="+$('.y_destinationLocationSuggestionType').val();
        fareFinderSerialized += "&cabinClass="+$("#flightClass").val();

        $.each(ACC.fareselection.getPassengerTypeCodeList(), function(i, item){
            var totPassengerType = 0;
            $(".y_"+item+"Select").each(function(index)
            {
                totPassengerType += parseInt($(this).val());
            });
            fareFinderSerialized += "&"+item+"=" + totPassengerType;
        });

        return fareFinderSerialized;
    },

    addBundleToCartSubmit : function(addBundleToCartForm,btnClicked){
        if($('html').hasClass('y_isMobile')) {
            ACC.fareselection.contractExpandTravelSelection(true, $(btnClicked));
        }
        var addToCartResult;
        $.when(ACC.services.addBundleToCartAjax(addBundleToCartForm)).then(
            function(response) {
                if(!response.valid) {
                    if($('html').hasClass('y_isMobile')) {
                        ACC.fareselection.contractExpandTravelSelection(false, $(btnClicked));
                    }
                    var output = [];
                    response.errors.forEach(function(error) {
                        output.push("<p>" + error + "</p>");
                    });
                    $("#y_addBundleToCartValidationModal .y_addBundleToCartValidationBody").html(output.join(""));
                    $("#y_addBundleToCartValidationModal").modal();

                    if(response.minOriginDestinationRefNumber == undefined) {
                        $("#" + ACC.appmodel.currentlySelectedItineraryId).prop('checked', false);
                        $("#" + ACC.appmodel.currentlySelectedItineraryId).parent().removeClass('selected');
                        if(ACC.appmodel.previouslySelectedItineraryId) {
                            $("#" + ACC.appmodel.previouslySelectedItineraryId).prop('checked', true);
                            $("#" + ACC.appmodel.previouslySelectedItineraryId).parent().addClass('selected');
                        }
                    }

                }

                // Unselect itineraryPricingInfo for the specified odRefNumbers in response
                if(response.minOriginDestinationRefNumber){
                    for (var refNumber = response.minOriginDestinationRefNumber; refNumber <= 1; refNumber++){
                        if($('html').hasClass('y_isMobile')) {
                            if($("#y_inbound label.selected input")){
                                ACC.fareselection.contractExpandTravelSelection(false, $("#y_inbound label.selected input"));

                            }
                            var enabledReseultPriceButtons=$('#y_inbound .y_fareResultPriceButton').map(function() {
                                if( $('.arrow', $(this)).hasClass('expanded')){
                                    return this;
                                }
                            });
                            for (var i = 0; i < enabledReseultPriceButtons.length; i++){
                                $('.arrow', enabledReseultPriceButtons[i]).toggleClass('expanded');
                                $('.arrow', enabledReseultPriceButtons[i]).html('&#9656;');
                                $(enabledReseultPriceButtons[i]).parents('.y_fareResultTravelOption').children('.y_fareResultClassSelectGroup').slideToggle();
                            }
                        }
                        $('input[name=refNumber-' + refNumber + ']').prop('checked', false);
                        $('input[name=refNumber-' + refNumber + ']').parent().removeClass('selected');
                    }
                }

                addToCartResult = response.valid;
                // refresh reservationComponent
                ACC.reservation.refreshReservationTotalsComponent($("#y_reservationTotalsComponentId").val());
                ACC.reservation.refreshTransportSummaryComponent($("#y_transportSummaryComponentId").val());
                ACC.fareselection.enableButtonsOnPage();
            });
        return addToCartResult;
    },

    bindSaveSearch : function() {
        $("#y_saveSearch").on('click', function() {
            var finderFormSerialized = ACC.fareselection.serializeFareFinderForm();
            if(finderFormSerialized != "") {
                $.when(ACC.services.saveSearch(finderFormSerialized)).then( function(data) {
                    var jsonData = JSON.parse(data);
                    $('#y_messageSaveSearch').parent().show();
                    $('#y_messageSaveSearch').html(jsonData.message);
                });
            }
        });
    }


};
