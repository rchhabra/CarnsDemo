ACC.accommodationselection = {

    _autoloadTracc: [
    	"loadSelectedView",
        "bindStopPropagation",
        "bindFacetCheckboxes",
        "bindSortSelect",
        "bindPropertyFilterValidation",
        "bindClearFacetSelection",
        "bindClearAllFacetSelection",
        "bindAccommodationShowMore",
        "readCustomerReviews",
        "bindDisplayPropertiesOnMap",
        "bindDisplayPropertiesOnListView",
        "bindDisplayPropertiesOnGridView"
    ],
    
    isAccommodationSearchResultsUpdated:true,
    isMapViewPresent:false,
    markerProperties:null,
    displayedInfoWindow:null,
    displayedMap:null,
    defaultMapBounds:null,
    
    loadSelectedView:function(){
    	var resultsViewType=new String($('.y_ListingPageParams').data('resultviewtype'));
    	if(resultsViewType.toUpperCase()==="MAPVIEW"){
    		ACC.accommodationselection.loadSelectedViewHtml($( ".y_accommodationListingShowMore" ).data('pagenumber'), resultsViewType);
    	}
    },
    
    loadSelectedViewHtml : function(pageNumber, resultsViewType){
    	$.when(ACC.services.getAccommodationListForSelectedView(pageNumber, resultsViewType)).then(function(data) {
			ACC.accommodationselection.markerProperties = data;
			if(resultsViewType.toUpperCase()==="MAPVIEW"){
	    		ACC.accommodationselection.markPropertiesOnMap();
	    	}
	    	else if(resultsViewType.toUpperCase()==="LISTVIEW"){
	    	    $("#y_hotelResults").removeClass("grid");
	    		$("#y_hotelResults").html(data.listGridhtmlContent);
	    	}
	    	else if(resultsViewType.toUpperCase()==="GRIDVIEW"){
	    	    $("#y_hotelResults").addClass("grid");
	    		$("#y_hotelResults").html(data.listGridhtmlContent);
                ACC.travelcommon.bindEqualHeights();
	    	}
		});
    },

	bindDisplayPropertiesOnListView : function() {
		$("#y_displayListView").click(function(e) {
			$("#y_displayListView").addClass("hidden");
			$("#y_displayGridView").removeClass("hidden");
			$("#y_showResults").removeClass("hidden");
			$("#y_displaySortSelection").removeClass("hidden");
			if(ACC.accommodationselection.isMapViewPresent){
			ACC.accommodationselection.isAccommodationSearchResultsUpdated=false;
			}
			ACC.accommodationselection.hideMap();
			ACC.accommodationselection.setResultViewType("ListView");
		});
	},
	
	bindDisplayPropertiesOnGridView : function() {
		$("#y_displayGridView").on("click", function(e) {
			$("#y_displayListView").removeClass("hidden");
			$("#y_displayGridView").addClass("hidden");
			$("#y_showResults").removeClass("hidden");
			$("#y_displaySortSelection").removeClass("hidden");
			if(ACC.accommodationselection.isMapViewPresent){
				ACC.accommodationselection.isAccommodationSearchResultsUpdated=false;
				}
			ACC.accommodationselection.hideMap();
			ACC.accommodationselection.setResultViewType("GridView");
		});
	},
	
	bindDisplayPropertiesOnMap : function() {
		$("#y_displayMapView").click(function(e) {
			ACC.accommodationselection.setResultViewType("MapView");
			ACC.accommodationselection.markPropertiesOnMap();
		});
	},
	
	markPropertiesOnMap: function()
	{
		$("#y_displayListView").removeClass("hidden");
		$("#y_displayGridView").removeClass("hidden");
		$("#y_showResults").addClass("hidden");
		$("#y_displaySortSelection").addClass("hidden");
		ACC.accommodationselection.displayMap();
		if(ACC.accommodationselection.isAccommodationSearchResultsUpdated){
			if (ACC.accommodationselection.markerProperties != null) {
				ACC.config.googleApiKey = $('.y_ListingPageParams').data('googleapi');
				ACC.accommodationselection.addGoogleMapsApi("ACC.accommodationselection.loadGoogleMap");
				ACC.accommodationselection.isMapViewPresent=true;
			}
		}
		else{
			ACC.accommodationselection.setDefaultViewOfMap();
		}
	},
	
	hideMap:function()
	{
		$("#y_googleMapResult").addClass("hidden");
		$("#y_displayMapView").removeClass("hidden");
	},
	
	displayMap:function()
	{
		$("#y_googleMapResult").removeClass("hidden");
		$("#y_displayMapView").addClass("hidden");
	},
	
	setResultViewType:function(resultViewType){
		var pageNum = $( ".y_accommodationListingShowMore" ).data('pagenumber');
		if(pageNum === undefined){
			pageNum = 1;
		}
		$.when(ACC.accommodationselection.loadSelectedViewHtml(pageNum, resultViewType)).then(function() {
            $("#y_resultsViewTypeForFacetForm").val(resultViewType);
            $("#y_resultsViewTypeForPropertyFilter").val(resultViewType);
            $("#y_resultsViewTypeForSortForm").val(resultViewType);
            ACC.travelcommon.changeUrlParams(["resultsViewType"], [resultViewType]);
		});
	},
	
	setDefaultViewOfMap: function(){
		ACC.accommodationselection.displayedMap.fitBounds(ACC.accommodationselection.defaultMapBounds);
		if(ACC.accommodationselection.displayedInfoWindow){
			ACC.accommodationselection.displayedInfoWindow.close();
		}
	},
	
	addGoogleMapsApi : function(callback) {
		if (callback != undefined && $(".js-googleMapsApi").length == 0) {
            $('head').append('<script async defer class="js-googleMapsApi" type="text/javascript" src="//maps.googleapis.com/maps/api/js?key=' + ACC.config.googleApiKey + '&callback=' + callback + '"></script>');
        } else if (callback != undefined) {
            eval(callback + "()");
        }
	},

	loadGoogleMap : function() {
		var map;
		var bounds = new google.maps.LatLngBounds();
		var mapOptions = {
	            mapTypeId: google.maps.MapTypeId.ROADMAP
		};

		// Display a map on the page
		map = new google.maps.Map(document.getElementById("map"), mapOptions);
		map.setTilt(45);

		// Multiple Markers
		var markers = [];

		// Info Window Content
		var infoWindowContent = [];
		var data=ACC.accommodationselection.markerProperties;
		
		for (i = 0; i < data.length; i++) {
			var marker = [ data[i].name, data[i].latitude, data[i].longitude ];
			markers.push(marker);
			infoWindowContent.push(data[i].htmlContent);
		}

		// Display multiple markers on a map
		var infoWindow = new google.maps.InfoWindow({ maxWidth: 400 }), marker, i;

		// Loop through our array of markers & place each one on the map
		for (i = 0; i < markers.length; i++) {
			var position = new google.maps.LatLng(markers[i][1], markers[i][2]);
			bounds.extend(position);
			marker = new google.maps.Marker({
				position : position,
				map : map,
				title : markers[i][0]
			});

			// Allow each marker to have an info window
			google.maps.event.addListener(marker, 'click',
					(function(marker, i) {
						return function() {
							infoWindow.setContent(infoWindowContent[i]);
							infoWindow.open(map, marker);
							ACC.accommodationselection.displayedInfoWindow=infoWindow;
						}
					})(marker, i));

			// Automatically center the map fitting all markers on the screen
			map.fitBounds(bounds);
			ACC.accommodationselection.defaultMapBounds=bounds;

			// Style map components
			google.maps.event
					.addListener(
							infoWindow,
							'domready',
							function() {
								// Reference to the DIV which receives the
								// contents of the infowindow using jQuery
								var iwOuter = $('.gm-style-iw'), iwBackground = iwOuter
										.prev(), iwPromotionBackground = iwOuter
										.find('.promoted'), arrowBackground = iwPromotionBackground
										.parent().closest('div.gm-style-iw')
										.prev('div').children(':nth-child(3)')
										.children('div'), promoArrowPointer = arrowBackground
										.children();

								// Remove the background shadow DIV
								iwBackground.children(':nth-child(2)').css({
									'display' : 'none'
								});

								$('.gm-style-iw > div > div').css({
									'overflow' : 'hidden'
								});

								// Remove the white background DIV
								iwBackground.children(':nth-child(4)').css({
									'display' : 'none'
								});

								if (iwPromotionBackground) {
									// Styles the arrow for a promoted offer
									promoArrowPointer
											.css({
												'backgroundColor' : '#fdebce',
												'box-shadow' : 'rgb(249, 168, 37) 0 0 5px',
												'top' : '0',
												'z-index' : '9'
											});
									arrowBackground.css('top', '-3px');
								} else {
									iwBackground
											.css({
												'backgroundColor' : '#f5f5f5',
												'box-shadow' : 'rgba(178, 178, 178, 0.6) 0 1px 6px'
											});
								}

								// Stack the arrow up to overlay
								iwBackground.css({
									'z-index' : '1'
								});

								// Selecting the close button elements.
								var iwCloseBtn = iwOuter.next(),
								iwCloseBtnTransparent = iwOuter.next().next();

								// Apply the desired effect to the close button
								iwCloseBtn.css({
									opacity : '1', // by default the close
													// button has an opacity of
													// 0.7
									right : '40px',
									top : '25px' // button repositioning
								});

								iwCloseBtnTransparent.css({
									right: '30px', top: '15px' // button transparent repositioning so mobile infoWindow can close.
								});

								iwOuter.css({
									'top' : '20px'
								});

								// Replace the close icon imagery
								iwOuter
										.next('div')
										.find('img')
										.replaceWith(
												'<span class="glyphicon glyphicon-remove"></span>');

							});

		}

		// Override our map zoom level once our fitBounds function runs (Make
		// sure it only runs once)
		var boundsListener = google.maps.event.addListener((map),
				'bounds_changed', function(event) {
					this.setZoom(12);
					google.maps.event.removeListener(boundsListener);
				})
		
		ACC.accommodationselection.displayedMap=map;
				
	},

    bindStopPropagation: function () {
        $('#filter .dropdown-menu').click(function (e) {
            e.stopPropagation();
        });
    },

    bindFacetCheckboxes: function () {
        $(document).on("change", ".y_accommodationFacetCheckbox", function () {
            $("#y_accommodationSearchFacetForm").find(":input[name=q]").val($(this).val());
            $("#y_accommodationSearchFacetForm").submit();
        });
    },

    bindSortSelect: function () {
        $(document).on("change", "#y_accommodationSearchSortSelect", function () {
            $("#y_accommodationSearchSortForm").submit();
        })
    },

    bindPropertyFilterValidation: function () {
        $(".y_filterNameForm").validate({
            errorElement: "span",
            errorClass: "fe-error",
            onfocusout: function (element) {
                $(element).valid();
            },
            onkeyup: false,
            onclick: false,
            //focusCleanup: true,
            rules: {
                propertyName: "required"
            },
            messages: {
                propertyName: ACC.addons.travelacceleratorstorefront['error.accommodationselection.filter.property']
            }
        });
    },

    bindClearFacetSelection: function () {
        $('.y_clearFacetFilter').on('click', function () {
            var accommodationSearchFacetForm = $('#y_accommodationSearchFacetForm');
            if ($(this).data('facetcode') == 'propertyName') {
                accommodationSearchFacetForm.find('input[name="propertyName"]').remove();
            } else {
                accommodationSearchFacetForm.find(":input[name=q]").val($(this).data('query'));
            }
            accommodationSearchFacetForm.submit();
        });
    },

    bindClearAllFacetSelection: function () {
        $('.y_clearAllFacetFilter').on(
            'click',
            function () {
                var accommodationSearchFacetForm = $('#y_accommodationSearchFacetForm');
                accommodationSearchFacetForm.find(
                    'input[name="propertyName"]').val("");
                accommodationSearchFacetForm.find(
                    'input[name="q"]').val("");
                accommodationSearchFacetForm.submit();
            });
    },

    bindAccommodationShowMore: function () {
        $(".y_accommodationListingShowMore").on('click', function () {
        	var pageNumber = $(this).data('pagenumber');
        	pageNumber = pageNumber + 1;
        	ACC.accommodationselection.getShowMoreResults(pageNumber);
        });


    },

    getShowMoreResults : function(pageNumber){
          $.when(ACC.services.getAccommodationListing(pageNumber, $("#y_resultsViewTypeForFacetForm").val())).then(
              function (data) {
                  $("#y_hotelResults").append(data.htmlContent);
                  if (!data.hasMoreResults) {
                      $(".y_accommodationListingShowMore").hide();
                  }
                  $(".y_shownResultId").html(data.totalshownResults);
                  $(".y_accommodationListingShowMore").data('pagenumber', pageNumber);
                  ACC.travelcommon.bindEqualHeights();
              });
          return false;
    },
    
    readCustomerReviews : function() {
    	$(document).on("click", ".y_customerReview", function() {
            var accommodationOfferingCode=$(this).data('accommodationofferingcode');
            $.when(ACC.services.readCustomerReviews(accommodationOfferingCode)).then(
            function(data) {
                    $(".y_customerReviewsModal").html(data.customerReviewsModalHtml);
                    $("#y_reviewsModal").modal();
            });
            return false;
        });
    }
    
};
