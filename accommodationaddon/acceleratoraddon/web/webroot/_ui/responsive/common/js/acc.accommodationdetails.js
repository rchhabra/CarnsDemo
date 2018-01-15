ACC.accommodationdetails = {
    _autoloadTracc: [
        "init",
        "bindPropertyMapTab",
        "bindAmenitiesList",
        "bindAddToCart",
        "bindAccommodationDetailsReviewShowMore",
        "bindContinueButton",
        "enableContinueButton",
        "showNoAvailabilityModal",
        "bindFeaturesMoreLess"
    ],

    map: null,

    marker: null,
    

    init: function () {
        if ($(".y_propertyPositionMap").length > 0) {
            ACC.config.googleApiKey = $('.y_propertyPositionMap').data('googleapi');
            ACC.accommodationdetails.addGoogleMapsApi("ACC.accommodationdetails.loadGoogleMap");
        }
    },

    bindPropertyMapTab: function () {
        $('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
            var target = $(e.target).attr("class");
            if (target == "y_propertyMapTab") {
                google.maps.event.trigger(ACC.accommodationdetails.map, "resize");
                ACC.accommodationdetails.map.setCenter(ACC.accommodationdetails.marker.getPosition());
            }
        });
    },

    addGoogleMapsApi: function (callback) {
        if (callback != undefined && $(".js-googleMapsApi").length == 0) {
            $('head').append('<script async defer class="js-googleMapsApi" type="text/javascript" src="//maps.googleapis.com/maps/api/js?key=' + ACC.config.googleApiKey + '&callback=' + callback + '"></script>');
        } else if (callback != undefined) {
            eval(callback + "()");
        }
    },

    loadGoogleMap: function () {
        var mapDiv = $('.y_propertyPositionMap');
        var accommodationOfferingName = mapDiv.data('accommodationofferingname');

        var mapOptions = {
            zoom: 13,
            zoomControl: true,
            panControl: true,
            streetViewControl: false,
            mapTypeId: google.maps.MapTypeId.ROADMAP,
            center: {lat: mapDiv.data('latitude'), lng: mapDiv.data('longitude')},
            styles: [
                {
                    featureType: 'poi.business',
                    stylers: [
                        {visibility: 'off'}
                    ]
                },
                {
                    featureType: 'poi.attraction',
                    stylers: [
                        {hue: '#ff0000'},
                        {saturation: 50},
                        {weight: 20}
                    ]
                }
            ]
        };

        ACC.accommodationdetails.map = new google.maps.Map(mapDiv.get(0), mapOptions);

        ACC.accommodationdetails.marker = new google.maps.Marker({
            position: ACC.accommodationdetails.map.getCenter(),
            map: ACC.accommodationdetails.map,
            title: accommodationOfferingName,
            icon: "https://maps.google.com/mapfiles/kml/pal2/icon28.png"
        });
    },

    bindAmenitiesList: function () {

        var amenities = '.y_amenities-list';

        $('a', amenities).on("click", function (e) {
            e.preventDefault();
            $(this).next('ul').slideToggle('fast', function () {
                if ($('.amenities-items li > ul:hidden').length == 0) {
                    $('.collapse-all').removeClass('hidden');
                    $('.show-all').addClass('hidden');
                } else if ($('.amenities-items li > ul:visible').length == 0) {
                    $('.show-all').removeClass('hidden');
                    $('.collapse-all').addClass('hidden');
                }
            });
        });

        $('.show-all', amenities).on("click", function (e) {
            e.preventDefault();
            $('.amenities-items li > ul').slideDown('fast');
            $(this).addClass('hidden');
            $('.collapse-all').removeClass('hidden');
        });

        $('.collapse-all', amenities).on("click", function (e) {
            e.preventDefault();
            $('.amenities-items li > ul').slideUp('fast');
            $(this).addClass('hidden');
            $('.show-all').removeClass('hidden');
        });

    },

    bindAddToCart: function () {
    	
    	$(window).on("unload", function() {
    	  $('.y_numberOfRooms').each(function () { 
    		 if($(this).is('select')){ 
    			 $(this).find('option').remove();
    		}
    	  });
    	});
    	
        var previousSelectedValue;
        $(".y_numberOfRooms").on("focus click", function(){
            previousSelectedValue=$(this).val();
        }).on("change", function () {
            if ($(this).val() != previousSelectedValue) {
            	if(!ACC.accommodationdetails.sameRoomTypeDiffRatePlanStockAvailable($(this),previousSelectedValue)) {
            		return;
            	}
            	
                var checkInDate = $(this).closest(".y_roomStayContainer").find("input[class=y_checkInDate]").val();
                var checkOutDate = $(this).closest(".y_roomStayContainer").find("input[class=y_checkOutDate]").val();
                var accommodationCode = $(this).closest(".y_roomStayContainer").find("input[class=y_accommodationCode]").val();
                var ratePlanCode = $(this).closest(".y_accommodationSelect").find("input[class=y_ratePlanCode]").val();
                var listOfRoomRateCodes = [];
                var listOfRoomRateDates = [];
                $(this).closest(".y_accommodationSelect").find("input[class=y_roomRate]").each(function () {
                    listOfRoomRateCodes.push($(this).attr('code'));
                    listOfRoomRateDates.push($(this).val());
                });
                $("#accommodationAddToCartForm #y_numberOfRooms").attr('value', $(this).val());
                $("#accommodationAddToCartForm #y_checkInDate").attr('value', checkInDate);
                $("#accommodationAddToCartForm #y_checkOutDate").attr('value', checkOutDate);
                $("#accommodationAddToCartForm #y_accommodationCode").attr('value', accommodationCode);
                $("#accommodationAddToCartForm #y_roomRateCodes").attr('value', listOfRoomRateCodes);
                $("#accommodationAddToCartForm #y_roomRateDates").attr('value', listOfRoomRateDates);
                $("#accommodationAddToCartForm #y_ratePlanCode").attr('value', ratePlanCode);

                var addToCartResult;
                var currentSelect = $(this);
                $.when(ACC.services.addAccommodationToCartAjax($("#accommodationAddToCartForm"))).then(
                    function (response) {
                        var jsonData = JSON.parse(response);

                        if (jsonData.valid) {
                            ACC.reservation.refreshReservationTotalsComponent($("#y_reservationTotalsComponentId").val());
                            ACC.reservation.refreshAccommodationSummaryComponent($("#y_accommodationSummaryComponentId").val());
                            previousSelectedValue=$(currentSelect).val();
                        } else {
                            var output = [];
                            jsonData.errors.forEach(function (error) {
                                output.push("<p>" + error + "</p>");
                            });
                            $("#y_addAccommodationToCartErrorModal .y_addAccommodationToCartErrorBody").html(output.join(""));
                            $("#y_addAccommodationToCartErrorModal").modal();
                            $(currentSelect).val(previousSelectedValue);
                        }
                        addToCartResult = jsonData.valid;
                    });
                return addToCartResult;
            }
        });
    },
    
    sameRoomTypeDiffRatePlanStockAvailable: function(currentNoOfRoomsSelection,previousSelectedValue) {
    	var currentNoOfRoomsSelectionId=currentNoOfRoomsSelection.attr('id');
		var maxStockQuantity=Number(currentNoOfRoomsSelection.data('maxstockquantity'));
    	var currentNoOfRoomsSelectionIdArr=currentNoOfRoomsSelectionId.split('_');
		var roomQtySelectedAvailableInStock=true;
		
    	if(currentNoOfRoomsSelectionIdArr.length==3) {
    		var numberOfAccomodationPrefix=currentNoOfRoomsSelectionIdArr[0];
    		var roomStayIndex=currentNoOfRoomsSelectionIdArr[1];
    		var similarRoomStayId=numberOfAccomodationPrefix+'_'+roomStayIndex+'_';
    		var totalRoomSelectedForSameRoomType=Number(currentNoOfRoomsSelection.val());
    		
    		$("select[id^='"+similarRoomStayId+"']").each(function (index, element) {
    			if(currentNoOfRoomsSelectionId!=$(element).attr('id')) {
    				totalRoomSelectedForSameRoomType+=Number($(element).val());
    				if(totalRoomSelectedForSameRoomType>maxStockQuantity){
    					var plandescription=$(element).data('plandescription');
    					var errorModalBodyMsg=$(".y_ratePlanRoomSelectStockErrorModal .y_ratePlanRoomSelectStockErrorModalBody").data('errormodalbodymsg');
    					errorModalBodyMsg=errorModalBodyMsg.replace('{0}',maxStockQuantity);
    					errorModalBodyMsg=errorModalBodyMsg.replace('{1}',plandescription);
    					$(".y_ratePlanRoomSelectStockErrorModal .y_ratePlanRoomSelectStockErrorModalBody").html(errorModalBodyMsg);
    					$(".y_ratePlanRoomSelectStockErrorModal").modal();
    					roomQtySelectedAvailableInStock=false;
    					currentNoOfRoomsSelection.val(previousSelectedValue);
    					return false;
    				}
    			}
    		});
    	}
    	return roomQtySelectedAvailableInStock;
    },
    
    bindAccommodationDetailsReviewShowMore: function() {
        $(".y_accommodationCustomerReviewShowMore").on('click', function() {    
            var accommodationOfferingCode=$(this).data('accommodationofferingcode');    
            var pageNumber=$(this).data('pagenumber');
            var reviewsHeight = $('.reviews-container .row').outerHeight();

            $.when(ACC.services.getPagedAccommodationDetailsCustomerReviews(accommodationOfferingCode,++pageNumber)).then(
            function(data) {
                    $(".y_customerReviewListItems").append(data.customerReviewsPagedHtml);
                    $(".reviews-container").animate({ scrollTop: reviewsHeight }, 600);
                    if(!data.hasMoreReviews) {
                        $(".y_accommodationCustomerReviewShowMore").hide();
                    }
            });
            $(this).data('pagenumber',pageNumber);
            return false;
        });
    },

    bindContinueButton: function () {
        $(".y_accommodationDetailsContinue").on('click', function (e) {
            e.preventDefault();
            var form = $(this).closest(".y_continueForm");
            $.when(ACC.services.validateAccommodationCartAjax()).then(
                function (response) {
                    if (response.valid) {
                        $(form).submit();
                    } else {
                        $("#y_validateCartErrorModal").modal();
                    }
                });
        });
    },

    enableContinueButton: function () {
        $(".y_accommodationDetailsContinue").removeAttr("disabled");
    },

    showNoAvailabilityModal : function() {
        if($("#y_noAccommodationAvailability"))
        {
            var noAccommodationAvailability = $("#y_noAccommodationAvailability").val();
            if(noAccommodationAvailability == "show"){
                $("#y_noAvailabilityModal").modal();
            }
        }
    },
    
    bindFeaturesMoreLess : function() {

        var features = '.y_features',
        sliceValue = 3,
        more = '.more',
        less = '.less';

        $(features).each(function(){
            var items = $(this).find('li'),
            hiddenItems = items.slice(sliceValue).hide();

            hiddenItems;

            if(items.length > sliceValue){
                $(this).siblings(more).removeClass('hidden');
                $(more).each(function(){
                    $(this).on('click', function(e) {
                        e.preventDefault();
                        $(this).siblings(features).find('li').slideDown('fast');
                        $(this).addClass('hidden');
                        $(this).siblings(less).removeClass('hidden');
                    });
                });
            }

            $(less).each(function(){
                $(this).on('click', function(e) {
                    e.preventDefault();
                    $(this).siblings(features)
                        .find('li:nth-child(' + sliceValue + ')')
                        .nextAll()
                        .slideUp('fast');
                    $(this).addClass('hidden');
                    $(this).siblings(more).removeClass('hidden');
                });
            });
        });

    }
};
