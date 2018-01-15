ACC.reservation = {

    _autoloadTracc: [
        "bindSeeFullReservationButton",
        [ "bindDesktopStickySideBar", $(".y_reservationSideBar").length != 0 ],
        [ "bindMobileItineraryOpenClose", $(".y_reservationSideBar").length != 0 ],
        [ "bindStickyContinueBar", $(".y_continueBar").length != 0 ]
    ],

    refreshReservationTotalsComponent: function (componentId) {
        // call the web service to get the response HTML
        $.when(ACC.services.refreshReservationTotalsComponent(componentId)).then(
            function (response) {
                $('.y_reservationTotalsComponent').replaceWith(response);
                // refresh the sticky sidebar functionality when anything on the itinerary changes (i.e. the sidebar's height has changed).
                ACC.reservation.checkAffixSidebar();
            }
        );
    },

    refreshAccommodationSummaryComponent: function (componentId) {
        // call the web service to get the response HTML
        $.when(ACC.services.refreshAccommodationSummaryComponent(componentId)).then(
            function (response) {
                $('.y_accommodationSummaryComponent').replaceWith(response);
                // refresh the sticky sidebar functionality when anything on the itinerary changes (i.e. the sidebar's height has changed).
                ACC.reservation.checkAffixSidebar();
            }
        );
    },

    refreshTransportSummaryComponent: function (componentId) {
        // call the web service to get the response HTML
        $.when(ACC.services.refreshTransportSummaryComponent(componentId)).then(
            function (response) {
                $('.y_transportSummaryComponent').replaceWith(response);
                // refresh the sticky sidebar functionality when anything on the itinerary changes (i.e. the sidebar's height has changed).
                ACC.reservation.checkAffixSidebar();
            }
        );
    },

    getTransportReservationComponent : function(componentId) {
        $.when(ACC.services.getTransportReservationComponent(componentId)).then(
            function(response){
                $('.y_transportReservationComponent').replaceWith(response);
            }
        );
    },

    getAccommodationReservationComponent : function(componentId) {
        $.when(ACC.services.getAccommodationReservationComponent(componentId)).then(
            function(response){
                $('.y_accommodationReservationComponent').replaceWith(response);
            }
        );
    },

    getReservationOverlayTotalsComponent : function(componentId) {
        $.when(ACC.services.getReservationOverlayTotalsComponent(componentId)).then(
            function(response){
                $('.y_reservationOverlayTotalsComponent').replaceWith(response);
            }
        );
    },

    bindSeeFullReservationButton: function () {
        $(document).on("click", ".y_seeFullReservationBtn", function () {
            ACC.reservation.getTransportReservationComponent($("#y_transportReservationComponentId").val());
            ACC.reservation.getAccommodationReservationComponent($("#y_accommodationReservationComponentId").val());
            ACC.reservation.getReservationOverlayTotalsComponent($("#y_reservationOverlayTotalsComponentId").val());
            $("#y_fullReservationModal").modal();
        });
    },

    // Sticky sidebar functionality
    checkAffixSidebar : function() {
        var $sideBar = $(".y_reservationSideBar"),
            $sideBarContent = $(".y_reservationSideBarContent");

        // add stickiness
        function addAffixOnSidebar(){
            $sideBar.affix({
                offset: {
                    top: $sideBar.offset().top,
                    bottom: function () {
                        return ($('.footer-wrapper').outerHeight(true));
                    }
                }
            });
        }
        // remove stickiness
        function removeAffixOnSidebar(){
            // delay the removal of affix due to Bootstrap affix taking some time to kick in
            setTimeout(function(){
                $(window).off('.affix');
                $sideBar.removeClass("affix affix-top affix-bottom").removeData("bs.affix");
            }, 200);
        }

        // check to see if we need to add or remove stickiness
        if ($(window).width() < 767) {
            removeAffixOnSidebar();
            $(".y_reservationSideBar").removeAttr('style');
            if(!$(".y_reservationSideBar").hasClass('open')){
                $(".y_reservationSideBarContent").removeAttr('style');
            }
            return false;
        }

        // remove any styles related to mobile itinerary
        if($sideBar.hasClass("open")) {
            $sideBar.removeClass("open pa");
            $sideBarContent.hide();
            $(".modal-backdrop").remove();
        }

        var isWindowTallEnough = $sideBar.height() + 20 < $(".y_nonItineraryContentArea").height();

        // show the sidebar again, when in desktop view
        $sideBarContent.show();
        $sideBar.removeAttr('style');

        // if the sidebar is shorter than the content, make it sticky
        if (isWindowTallEnough) {
            addAffixOnSidebar();
            $sideBar.affix('checkPosition');
            $sideBar.affix('checkPosition'); // need to call it twice due to a bug in the bootstrap function
        } else {
            removeAffixOnSidebar();
        }
    },

    // Sticky continuebar functionality
    checkAffixContinueBar : function() {
        var $continueBar = $(".y_continueBar");

        function addAffixContinueBar(){
            $continueBar.affix({
                offset: {
                    bottom: $continueBar.offset().bottom,
                    bottom: function () {
                        return ($('.footer-wrapper').outerHeight(true)) +20;
                    }
                }
            });   
        }

        if(!$('html').hasClass('y_isMobile')) {
            if($('.y_nonItineraryContentArea').length){
                var footerMarginTop = $('footer').css('margin-top');
                $('main').css('padding-bottom', footerMarginTop);
            }
        }

        addAffixContinueBar();

    },

    bindDesktopStickySideBar : function() {
        // apply sticky sidebar on load
        ACC.reservation.checkAffixSidebar();

        // on window resize, we apply the sticky sidebar for desktop
        // NOTE: this is run only every half a second to avoid calling the method too many times during resize
        var resizeTimeout;
        $(window).resize(function(){
            clearTimeout(resizeTimeout);
            resizeTimeout = setTimeout(function(){
                ACC.reservation.checkAffixSidebar();
            }, 500);
        });

        // when panels are opened/closed (i.e. the content's height has changed), refresh the sticky sidebar
        $(".show-hide-button").on("click", function() {

            var target = $(this).data("target");
            $(target).on("hidden.bs.collapse shown.bs.collapse", function () {
                ACC.reservation.checkAffixSidebar();
            });
        });

    },

    bindStickyContinueBar : function() {
        // apply sticky continuebar on load
        ACC.reservation.checkAffixContinueBar();

        // on window resize, we apply the sticky continuebar for desktop
        // NOTE: this is run only every half a second to avoid calling the method too many times during resize
        var resizeTimeout;
        $(window).resize(function(){

            clearTimeout(resizeTimeout);
            resizeTimeout = setTimeout(function(){
                ACC.reservation.checkAffixContinueBar();
            }, 500);
        });

        $(".y_nonItineraryContentArea .show-hide-button, .panel a").on("click", function() {
            var target = $(this).data("target");

            $('.y_continueBar').removeAttr('style');
            
            $(target).on("hidden.bs.collapse", function () {
                $('.y_continueBar').removeClass("affix-bottom");
                $('.y_continueBar').addClass("affix");
            });

        });

        $('.panel').on('hidden.bs.collapse shown.bs.collapse', function () {
            $('.y_continueBar').removeClass("affix-bottom");
            $('.y_continueBar').addClass("affix");
        });

    },

    // expand/collapse itinerary on mobile
    bindMobileItineraryOpenClose : function() {
        $(".y_reservationSideBar").on('click', '.y_fareSlide', function() {
            if ($(':animated').length) {
                return false; // don't respond to clicks until
                // animation completed
            }

            var $sideBar = $('.y_reservationSideBar'),
                $sideBarContent = $('.y_reservationSideBarContent');

            if(!$sideBar.hasClass('open')) {
                $sideBar.addClass('open pa');

                var elementOffset = $sideBar.offset().top - 50;

                $("html, body").animate({ scrollTop: elementOffset+"px" });
                $sideBarContent.slideDown();

                $('body').append($('<div class="modal-backdrop fade in"></div>')).promise().done(function(){
                    $('.modal-backdrop').addClass('in');
                });

            } else {
                $sideBar.removeClass('open pa');

                var scrollTop     = $(window).scrollTop(),
                    winHeight = ( $(window).height() < $sideBar.height() )? $(window).height() : $sideBar.height(),
                    scrollNew = scrollTop - winHeight + 140;

                $("html, body").animate({ scrollTop: scrollNew+"px" });
                $sideBarContent.slideUp();

                $('.modal-backdrop').remove();
            }

        });
    },

    refreshFareSelectionReservationComponent : function() {
        $.when(ACC.services.refreshFareSelectionReservation()).then(
            // handle the response
            function(response) {
                // insert the HTML into the itinerary section
                $("#y_fareSelectionTransportSummary").html(response.transportSummaryHtmlContent);
                $("#y_fareSelectionFullReservation").html(response.fullReservationHtmlContent);
                var redirectUrl = $("#y_fareSelection_Redirecturl").val();
                $("#y_addBundleToCartFormRedirectUrl").val(redirectUrl);
                // refresh the sticky sidebar functionality when anything on the itinerary changes (i.e. the sidebar's height has changed).
                ACC.reservation.checkAffixSidebar();
            }
        );
    }

};
