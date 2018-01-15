ACC.seat = {

    _autoloadTracc: [
        "bindPassengerIconChange",
        "seatDeselection",
        "deckChangeButton",
        "bindSelectSeat",
        "bindCarousel"
    ],

    bindSelectSeat : function() {
        $( document ).ready(function() {
            $('#y_selectSeat').attr("disabled", false);

            $('#y_selectSeat').on('click', function () {
                ACC.seat.bindSvgLoad();
            });
        });

    },

    bindCarousel : function() {
        $('#carousel-generic').on('slid.bs.carousel', function () {
            ACC.seat.bindSvgLoad();
            ACC.seat.checkSeatAvailability();
        });
    },

    bindSvgLoad: function () {
        ACC.seat.bindLoad();
        ACC.seat.bindDesktopHoverInfo();
    },

    checkSeatAvailability : function() {
        var activeSeatmap = $(document).find('.y_transportOffering.item.active').find(".y_seatMapSVGWrap:visible");
        var seats = activeSeatmap.find(".seat");
        var seatIconsNum = activeSeatmap.parents('.seatmapContent').find('.y_seatIconSeatNumber:visible');

        for (var i = 0; i < seatIconsNum.length; i++) {
            var invSeatIcon = $(seatIconsNum[i]);
            var isActive = invSeatIcon.closest('div').parent().find(".y_seatIcon").attr("class").includes('active');
            var invSeatIconNum = invSeatIcon.text();
            var getInvSeatNumID = invSeatIcon.closest('div').parent().attr('id');
            if (invSeatIconNum === '--') {
                continue;
            }

            for (var j = 0; j < seats.length; j++) {
                if (invSeatIconNum === $(seats[j]).data("seat-id")) {
                    ACC.seat.addClassToElement($(seats[j]), "selected");
                    ACC.seat.addClassToElement($(seats[j]), getInvSeatNumID);
                    if (isActive) {
                        ACC.seat.addClassToElement($(seats[j]), "seatActive");
                    }
                }
            }
        }
    },


    bindSvgLoad: function (y_seatMapSVGWrap) {
        ACC.seat.bindLoad(y_seatMapSVGWrap);
        ACC.seat.bindDesktopHoverInfo(y_seatMapSVGWrap);
    },

    deckChangeButton: function () {
        $(".y_changeDeckBtns").click(function () {
            $('.y_deckBtn').toggle();
            $('.y_seatMapSVGWrap').toggle();
            var activeSeatmap = $(document).find('.y_transportOffering.item.active').find(".y_seatMapSVGWrap:visible");
            var seatIconsNum = activeSeatmap.parents('.seatmapContent').find('.y_seatIconSeatNumber:visible');

            $(".seat").removeClass("selected");
            $(".seat").removeClass("seatActive");

            //remove all classes from seats on visible seat map before seats are checked
            for (var j = 0; j < seatIconsNum.length; j++) {
                var getInvSeatNumID = $(seatIconsNum[j]).closest('div').parent().attr('id');

                $(".seat").removeClass(getInvSeatNumID);
            }
            //check which seats need classes added
            ACC.seat.checkSeatAvailability();

        });
    },

    desktopSelectSeat: function (selectedSeat) {
        var currentSeats = $(selectedSeat).parents('div.y_seatMapSVGWrap').find('.seat');
        var seatID = $(selectedSeat).data("seat-id");
        var activeItem = $(document).find(".y_svgdiv").find("div.item.active");
        var findSeatText = $(activeItem).find(".active").siblings().find('.y_seatIconSeatNumber');
        var findPassengerID = $(activeItem).find(".active").parent().attr('id');
        var passengerSeatIcon = $(activeItem).find("div.y_seatIcon.active");
        var selectedPassengerType = $(document).find(".y_svgdiv").find("div.item.active")
            .find(".active").parent().data('passengernum');
        var tabid= $(document).find(".y_svgdiv").find("div.item.active").data('tabid');
        var previousSelectedSeat='';
        if ($(selectedSeat).attr("class").includes('selected')) {
            if (!$(selectedSeat).attr("class").includes(findPassengerID)) {
                return;
            }

            if(ACC.ancillary.removeSeatFromCart(seatID, selectedPassengerType,tabid)){
                ACC.seat.removeClassFromElement($(selectedSeat), "selected");
                ACC.seat.removeClassFromElement($(selectedSeat), "seatActive");
                ACC.seat.removeClassFromElement($(selectedSeat), findPassengerID);
                ACC.seat.removeClassFromElement(passengerSeatIcon, "selected");
                findSeatText.text("--");
            } else {
                return this.style();
            }

        }
        else {
            for (var j = 0; j < currentSeats.length; j++) {
                var seat = $(currentSeats[j]);

                if (seat.attr("class").includes(findPassengerID)) {
                    ACC.seat.removeClassFromElement(seat, 'selected');
                    ACC.seat.removeClassFromElement(seat, "seatActive");
                    ACC.seat.removeClassFromElement(seat, findPassengerID);
                    previousSelectedSeat=seat.attr("id");
                }
            }

            findSeatText.text(seatID);
            ACC.seat.addClassToElement($(selectedSeat), "selected");
            ACC.seat.addClassToElement($(selectedSeat), "seatActive");
            ACC.seat.addClassToElement($(selectedSeat), findPassengerID);
            ACC.seat.addClassToElement(passengerSeatIcon, "selected");

        }
    },

    bindLoad: function () {
        var activeSeatmaps = $(document).find('.y_transportOffering.item.active').find(".y_seatMapSVGWrap");

        for (var j = 0; j < activeSeatmaps.length; j++) {
            var activeSeatMap = activeSeatmaps[j];
            var seats = $(activeSeatMap).find(".seat");

            for (var i = 0; i < seats.length; i++) {
                $(seats[i]).unbind();
                $(seats[i]).on("click", function () {
                    if ($('html').hasClass('y_isMobile')) {
                        ACC.seat.mobileSelectSeat(this);
                    } else {
                        ACC.seat.desktopSelectSeat(this);
                    }
                });
            }
        }
    },

    mobileSelectSeat: function (selectedSeat) {
        var seatID = $(selectedSeat).data("seat-id");
        var findSeatTextModal = document.getElementById("y_seatNumberSelectedMobile");
        var activeItem = $(document).find(".y_svgdiv").find("div.item.active");
        var findPassengerID = $(activeItem).find(".active").parent().attr('id');
        var selectBtn = document.getElementsByClassName("y_selectSeatBtn");

        if (!$(selectedSeat).attr("class").includes(findPassengerID) && $(selectedSeat).hasClass('selected')) {
            return;
        }
        else {
            $("#y_seatInfoModal").modal("show");
            findSeatTextModal.innerHTML = "Seat: " + (seatID);

//      Toggle between select and deselect text on mobile modal button
            if ($(selectedSeat).attr("class").includes('selected')) {
                $("#y_selectSeatBtnText").hide();
                $("#y_deselectSeatBtnText").show();
                ACC.seat.removeClassFromElement($(selectBtn), "selectBtn");
            }
            else {
                $("#y_selectSeatBtnText").show();
                $("#y_deselectSeatBtnText").hide();
                ACC.seat.addClassToElement($(selectBtn), "selectBtn");
            }


            $(".y_selectSeatBtn").unbind().click(function () {
                var currentSeats = $(selectedSeat).parents('.seat-map').find('.seat');

                var findSeatText = $(activeItem).find(".active").find('.y_seatIconSeatNumber');

                var passengerSeatIcon = $(activeItem).find("div.y_seatIcon.active");

                if ($(selectedSeat).attr("class").includes('selected')) {
                    if (!$(selectedSeat).attr("class").includes(findPassengerID)) {
                        return;
                    }

                    ACC.seat.removeClassFromElement($(selectedSeat), "selected");
                    ACC.seat.removeClassFromElement($(selectedSeat), "seatActive");
                    ACC.seat.removeClassFromElement($(selectedSeat), findPassengerID);
                    ACC.seat.removeClassFromElement(passengerSeatIcon, "selected");
                    findSeatText.text("--");

                }
                else {
                    for (var j = 0; j < currentSeats.length; j++) {
                        var seat = $(currentSeats[j]);

                        if (seat.attr("class").includes(findPassengerID)) {
                            ACC.seat.removeClassFromElement(seat, 'selected');
                            ACC.seat.removeClassFromElement(seat, "seatActive");
                            ACC.seat.removeClassFromElement(seat, findPassengerID);
                        }
                    }

                    findSeatText.text(seatID);
                    ACC.seat.addClassToElement($(selectedSeat), "selected");
                    ACC.seat.addClassToElement($(selectedSeat), "seatActive");
                    ACC.seat.addClassToElement($(selectedSeat), findPassengerID);
                    ACC.seat.addClassToElement(passengerSeatIcon, "selected");
                }

            });

        }
    },

    bindDesktopHoverInfo: function () {
        var activeSeatmaps = $(document).find('.y_transportOffering.item.active').find(".y_seatMapSVGWrap");

        for (var j = 0; j < activeSeatmaps.length; j++) {
            var activeSeatMap = activeSeatmaps[j];
            var seats = $(activeSeatMap).find(".seat");

            var seatInfoBoxContainer = $(ACC.seat.findParent(activeSeatMap, "seatmapContent")).find('.seatmapSVG');
            var seatInfoBox = $(ACC.seat.findParent(activeSeatMap, "seatmapContent")).find(".seatInfo-Box");

            for (var i = 0; i < seats.length; i++) {
                $(seats[i]).mouseenter(function(e){
                    seatInfoBox.show();
                });

                $(seats[i]).mouseleave(function(e){
                    seatInfoBox.hide();
                });

                $(seats[i]).mousemove(function (e) {
                    var seatNumber = $(this).data("seat-id");
                    var priceInfo = seatInfoBox.find("#y_seatNumberofSelected");

                    var rect = seatInfoBoxContainer[0].getBoundingClientRect();
                    var x = e.clientX + $(seatInfoBoxContainer).scrollLeft() - rect.left + 5;
                    var y = e.clientY + $(seatInfoBoxContainer).scrollTop() - rect.top + 10;

                    seatInfoBox.css({'top': y,'left': x});

                    priceInfo.text(seatNumber);

                    var findPassengerID = $(document).find(".y_svgdiv").find("div.item.active")
                        .find(".active").parent().attr('id');

                    if (!$(this).attr("class").includes(findPassengerID) && $(this).attr("class").includes('selected')) {
                        $(this).attr('cursor', 'not-allowed');
                    }
                    else {
                        $(this).attr('cursor', 'pointer');
                        ACC.seat.addClassToElement($(this), "hover");
                    }
                });
                seats[i].addEventListener("mouseleave", function () {
                    ACC.seat.removeClassFromElement($(this), "hover");
                });
            }
        }
    },

    addClassToElement: function (element, newClassToAdd) {
        // get the existing classes from element
        var currentClass = element.attr("class");

        if (!currentClass.includes(newClassToAdd)) {
            // concat classes with newClass
            var newClass = currentClass + " " + newClassToAdd;

            // change class to element
            element.attr("class", newClass);
        }
    },

    removeClassFromElement: function (element, classToRemove) {
        // get the existing classes from element
        var currentClass = element.attr("class");

        if (currentClass.includes(classToRemove)) {
            // subtract class
            var oldClassToRemove = currentClass.replace(classToRemove, '');
            // change class to element
            element.attr("class", oldClassToRemove);
        }
    },

    toggleClassInElement: function (element, classToToggle) {
        // get the existing classes from element
        var currentClass = element.attr("class");
        var newClassString;
        if (currentClass.includes(classToToggle)) {
            // subtract class
            newClassString = currentClass.replace(classToToggle, '');
        }
        else {
            // concat class
            newClassString = currentClass + " " + classToToggle;
        }
        // change class to element
        element.attr("class", newClassString);

    },


    bindPassengerIconChange: function () {

        $(".y_seatIcon").click(function () {
            $(this).closest(".y_travellerIconPanel").find(".y_seatIcon").removeClass("active");
            $(this).addClass("active");
            var activeSeatMap = $(document).find('.y_transportOffering.item.active').find(".y_seatMapSVGWrap:visible");
            var seats = $(activeSeatMap).find(".seat");
            var activeItem = $(document).find(".y_svgdiv").find("div.item.active");
            var findPassengerID = $(activeItem).find(".active").parent().attr('id');
            var seatIcon = $(activeItem).find("div.y_seatIcon");

            for (var j = 0; j < seats.length; j++) {
                var invSeat = $(seats[j]);

                if (invSeat.attr("class").includes(findPassengerID) && seatIcon.hasClass('active')) {
                    ACC.seat.addClassToElement(invSeat, "seatActive");
                }
                else if (!invSeat.attr("class").includes(findPassengerID) && seatIcon.hasClass('active')) {
                    ACC.seat.removeClassFromElement(invSeat, "seatActive");
                }
            }
        });

    },

    seatDeselection: function () {
        $(".y_removeSeatSelection").click(function () {
            var seatRow = $(this).closest(".inv-seat");
            $(seatRow).find(".y_seatIcon").removeClass("selected");
            $(seatRow).find(".y_seatIconSeatNumber").text("--");
            var individualSeatId = $(seatRow).find(".y_seatIcon").parent().attr('id');
            var seatMapContent = $(document).find('.y_transportOffering.item.active').find(".y_seatMapSVGWrap:visible");
            var seatToDeselect = seatMapContent.find('.'+individualSeatId);

            if (seatToDeselect.length > 0) {
                ACC.seat.removeClassFromElement($(seatToDeselect), "selected");
                ACC.seat.removeClassFromElement($(seatToDeselect), "seatActive");
                ACC.seat.removeClassFromElement($(seatToDeselect), individualSeatId);
            }
            else {
                return;
            };
        });
    },

    findParent: function findAncestor(el, cls) {
        while ((el = el.parentElement) && !el.classList.contains(cls)) ;
        return el;
    }
};
