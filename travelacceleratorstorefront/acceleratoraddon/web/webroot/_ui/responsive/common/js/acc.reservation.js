ACC.reservation = {

    _autoloadTracc: [
        "bindSeeFullReservationButton",
        [ "bindStickySideBar", $(".y_reservationSideBar").length != 0 ],
        "bindPageMargin",
        "mobileReservationSlideButton",
        "bindAffixContinueBar",
        "bindPanelClose",
        "bindPanelOpen",
        "bindTestWindowResize",
        "bindAsmReservation"
    ],

    bindPageMargin : function() {
        if($('html').hasClass('y_isMobile')) {
            if($('.y_nonItineraryContentArea').length){
                var sideBarHeight = $('.y_reservationSideBar').height();
                $('main').css('padding-bottom', sideBarHeight);
            }
        }
    },

    mobileReservationSlideButton: function() {
        if($('.y_reservationSideBarContent').find('.summary').length == 0) {
            $('.y_fareSlide').addClass('inactive');
        } else {
            $('.y_fareSlide').removeClass('inactive');
        }
    },

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
        $(document).on("click", ".y_seeFullReservationBtn", function (e) {
            e.preventDefault();
            if($("#y_transportReservationComponentId").val()) {
                ACC.reservation.getTransportReservationComponent($("#y_transportReservationComponentId").val());
            }
            if($("#y_accommodationReservationComponentId").val()) {
                ACC.reservation.getAccommodationReservationComponent($("#y_accommodationReservationComponentId").val());
            }
            if($("#y_reservationOverlayTotalsComponentId").val()) {
                ACC.reservation.getReservationOverlayTotalsComponent($("#y_reservationOverlayTotalsComponentId").val());
                ACC.reservation.populateReservationCode();
            }
            $("#y_fullReservationModal").modal();
        });
    },

    populateReservationCode : function () {
        $( "#y_fullReservationModal" ).on('shown.bs.modal', function(){
            $("#y_targetReservationCode").text(" "+$("#y_reservationCode").val());
        });
    },

    // Sticky sidebar functionality
    checkAffixSidebar : function() {
        var $sideBar = $(".y_reservationSideBar"),
            $sideBarContent = $(".y_reservationSideBarContent");

        // add stickiness
        function addAffixOnSidebar(){
            if($('html').hasClass('y_isMobile')) {
                if($('.y_transportSummaryComponent #summary').hasClass('in')){
                    $('#accommodationsummary').addClass('in');
                }
            }

            if(!$('html').hasClass('y_isMobile')) {
                $sideBar.affix({
                    offset: {
                        top: $sideBar.offset().top,
                        bottom: function () {
                            return ($('.footer-wrapper').outerHeight(true)) + $('.continue-bar').outerHeight(true);
                        }
                    }
                });
            } else if($('html').hasClass('y_isMobile')) {
                $sideBar.affix({
                    offset: {
                        bottom: $sideBar.offset().bottom,
                        bottom: function () {
                            return ($('.footer-wrapper').outerHeight(true));
                        }
                    }
                });
            }

        }

        addAffixOnSidebar();

        // remove stickiness
        function removeAffixOnSidebar(){
            // delay the removal of affix due to Bootstrap affix taking some time to kick in
            $(window).off('.affix');
            $sideBar.removeClass("affix affix-top affix-bottom")
                .removeData("bs.affix");
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
    bindAffixContinueBar : function() {
        $('.y_continueBar').affix({
            offset: {
                bottom: function(){
                    return ($('.footer-wrapper').height()) +20;
                }
            }
        });
    },

    bindStickySideBar : function() {
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
        $(".y_reservationSideBar .show-hide-button").on("click", function() {
            var target = $(this).data("target"),
                $footer = $('.footer-wrapper'),
                $footerHeight = $footer.outerHeight(),
                $sideBar = $(".y_reservationSideBar");

            $(target).on("hide.bs.collapse show.bs.collapse", function () {
                ACC.reservation.checkAffixSidebar();
            });

            $(".y_reservationSideBar").on("hide.bs.collapse show.bs.collapse", function () {
                ACC.reservation.checkAffixSidebar();
            });

        });

        if($('html').hasClass('y_isMobile')) {

            $(".y_reservationSideBar").on("shown.bs.collapse", function() {
                if($(window).scrollTop() + $(window).height() == $(document).height()) {
                    var pagePadding = parseInt($('main').css('padding-bottom'));
                    $(this).removeClass('affix').addClass("affix-bottom").animate({
                        bottom: -pagePadding
                    }, 100);
                }
            });

            $(".y_reservationSideBar").on("hide.bs.collapse", function() {
                var pagePadding = parseInt($('main').css('padding-bottom'));
                $(this).removeClass('affix').addClass("affix-bottom").css('bottom',-pagePadding.toString());
                ACC.reservation.checkAffixSidebar();
            });

            $(".y_reservationSideBar").on("hidden.bs.collapse", function() {
                ACC.reservation.checkAffixSidebar();
            });

            $('.y_reservationSideBar').on('show.bs.collapse', function () {
                if($(this).hasClass('affix-bottom')){
                    var footerHeight = $('.footer-wrapper').outerHeight();
                    $(this).css('bottom',footerHeight);
                }
            });

            $('.y_reservationSideBar').on('affix.bs.affix', function () {
                $(this).css('bottom','');
            });
        }

        $('.y_nonItineraryContentArea .collapse').on('shown.bs.collapse hidden.bs.collapse', function () {
            //alert('1');

            // if the sidebar is shorter than the content, make it sticky
            var $sideBar = $(".y_reservationSideBar");
            var isWindowTallEnough = $sideBar.height() + 20 < $(".y_nonItineraryContentArea").height();

            // if the sidebar is shorter than the content, make it sticky
            if (isWindowTallEnough) {
                $('.y_reservationSideBar').removeClass("affix affix-top affix-bottom").removeData("bs.affix");
                ACC.reservation.checkAffixSidebar();
                $('.y_reservationSideBar').affix('checkPosition');
            }
        });

    },

    bindPanelClose : function() {
        var $continueBar = $('.y_continueBar'),
            $sideBar = $('.y_reservationSideBar'),
            $footer = $('.footer-wrapper');

        $('.y_nonItineraryContentArea .collapse').on('hide.bs.collapse', function () {
            $sideBar.removeClass("affix affix-top affix-bottom").removeData("bs.affix");
            $footer.css('position','static').css('opacity','0');
            $continueBar.removeClass("affix affix-top affix-bottom").removeData("bs.affix").removeAttr('style');
            $continueBar.addClass("affix");
        });

        $('.y_nonItineraryContentArea .collapse').on('hidden.bs.collapse', function () {
            $continueBar.removeAttr('style');
            $footer.removeAttr('style');
            $footer.css('position','absolute');

            ACC.reservation.checkAffixSidebar();

            ACC.reservation.bindAffixContinueBar();
            $continueBar.affix('checkPosition');

        });
    },

    bindPanelOpen : function() {

        var $continueBar = $('.y_continueBar'),
            $sideBar = $('.y_reservationSideBar');

        $('.y_nonItineraryContentArea .collapse').on('show.bs.collapse', function () {

            $continueBar.removeClass("affix affix-top affix-bottom").removeData("bs.affix");
            $sideBar.removeClass("affix affix-top affix-bottom").removeData("bs.affix");

        });

        $('.y_nonItineraryContentArea .collapse').on('shown.bs.collapse', function () {

            ACC.reservation.bindAffixContinueBar();
            $continueBar.affix('checkPosition');

            ACC.reservation.checkAffixSidebar();
            $sideBar.affix('checkPosition');

        });
    },

    bindTestWindowResize : function() {

        var $continueBar = $('.y_continueBar');

        // on window resize, we apply the sticky continuebar for desktop
        // NOTE: this is run only every half a second to avoid calling the method too many times during resize

        $(window).resize(function(){
            $continueBar.removeClass("affix affix-top affix-bottom").removeData("bs.affix");
            ACC.reservation.bindAffixContinueBar();
            $continueBar.affix('checkPosition');
        });

    },

    bindAsmReservation : function() {
        if($("#y_fullReservationModal").length > 0) {
            $(document).on("click", ".y_asmSeeFullReservation", function (e) {
                e.preventDefault();
                if($("#y_transportReservationComponentId").val()) {
                    ACC.reservation.getTransportReservationComponent($("#y_transportReservationComponentId").val());
                }
                if($("#y_accommodationReservationComponentId").val()) {
                    ACC.reservation.getAccommodationReservationComponent($("#y_accommodationReservationComponentId").val());
                }
                if($("#y_reservationOverlayTotalsComponentId").val()) {
                    ACC.reservation.getReservationOverlayTotalsComponent($("#y_reservationOverlayTotalsComponentId").val());
                }
                $("#y_fullReservationModal").modal();
            });
        }
    }

};
