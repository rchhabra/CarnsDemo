ACC.tripfinder = {
	originLocationCode: '',
	_autoloadTracc : [ 
        "showTripFinderOriginTextBox",
		"tripFinderDestinationLocation",
		"bindChangeOriginLocation",
		"bindOriginLocationAutosuggest",
		["bindGeoLocation", $(".y_tripFinderTransportFacilitySearch").length != 0]
	],
	
	showTripFinderOriginTextBox: function() {
		$('.y_tripFinderTransportFacilitySearch').removeClass("trip-finder-origin-hide");
	},
	
	tripFinderDestinationLocation : function() {
		// Event handler for destination Location Link

		$('#y_fareFinderModal').on('show.bs.modal', function(e) {

		    //get data-id attribute of the clicked element
		    var destinationLocationCode = $(e.relatedTarget).data('destination-code');
			var destinationLocationName = $(e.relatedTarget).data('destination-name');

		   	var orginLocation = $('input[name="origin"]').val();
		   	var orginLocationName = $('input[name="originName"]').val();
		   	var originLocationSuggestionType = $('input[name="originType"]').val();
		   	$('#y_originLocation').val(orginLocationName);
			$('.y_originLocationCode').val(orginLocation);
			$('.y_originLocationSuggestionType').val(originLocationSuggestionType);

		   	$('#y_destinationLocation').val(destinationLocationName);
			$('.y_destinationLocationCode').val(destinationLocationCode);
		    
		});

		
	},
	
	showDestinationLocations: function() {
		var selectedActivity = $("#y_tripFinderActivity").val();
		var	originLocationCode  = $(".y_tripFinderOriginLocationCode").val();
		var url = "trip-finder/get-destination-locations.json?activity="+selectedActivity;
		var originCityCode  = ACC.travelcommon.getURLParameter("city");
		var originType = "";
		$.cookie.json = true;
		if(!originLocationCode){
			var originJsonObject = $.cookie("tripFinderActivitySearch_"+selectedActivity);
			originLocationCode = originJsonObject.code;
			originType = originJsonObject.type;
		}
		// build the URL for the AJAX call by appending parameters
		if(originLocationCode){
			url = url+"&origin="+originLocationCode;
		}
		if(originCityCode){
			url = url+"&city="+originCityCode;
		}
		if(originType){
			url = url+"&originType="+originType;
		}

		$.when( ACC.services.getDestinationLocationAjax(url) ).then(
	 		// success getting the code for the nearest origin location 
			function( data ) {
				$("#y_destinationLocationList").html(data.htmlContent);
				$("#y_tripFinderOriginLocation").val(data.originLocationName);
				$(".y_tripFinderOriginLocationCode").val(data.originLocationCode);
				$(".y_tripFinderOriginLocationSuggestionType").val(data.originLocationSuggestionType);
			}
		);
	},

	bindGeoLocation: function(){
		$('#y_tripFinderOriginLocation').css("background-image", "url('" + ACC.config.commonResourcePath + "/images/spinner.gif')").addClass('loadingOrigin');
		var originParamValue = ACC.travelcommon.getURLParameter("origin");
		var originTypeValue =  ACC.travelcommon.getURLParameter("originType");
		var activity=$("#y_tripFinderActivity").val();
		var cookieName="tripFinderActivitySearch_"+activity;
		$.cookie.json = true;
		if(!originParamValue){
			if($.cookie(cookieName)){
				ACC.tripfinder.showDestinationLocations();
			}else{
				if(navigator.geolocation){
				 navigator.geolocation.getCurrentPosition(
				 	// success from Google geo location
				 	function (position){
					 	$.when( ACC.services.getNearestTransportFacilityAjax(position, activity) ).then(
					 		// success getting the code for the nearest origin location 
							function( data ) {
								$(".y_tripFinderOriginLocationCode").val(data);
								var originJsonObject = { code: data, type: '' };
								$.cookie(cookieName,originJsonObject,{ expires : 10 });
								ACC.tripfinder.showDestinationLocations();
							}
						);
					},
					// error from Google geo location
					function (error){
						switch(error.code) {
				        case error.PERMISSION_DENIED:
				        	ACC.tripfinder.showDestinationLocations();
				            break;
				        case error.POSITION_UNAVAILABLE:
				            break;
				        case error.TIMEOUT:
				            break;
				        case error.UNKNOWN_ERROR:
				            break;
					  }
					}); 
				} 
			}
		}
		// if origin is in the URL, we can directly get the destination locations
		else{
			$(".y_tripFinderOriginLocationCode").val(originParamValue);
			$(".y_tripFinderOriginLocationSuggestionType").val(originTypeValue);
			var originJsonObject = { code: $(".y_tripFinderOriginLocationCode").val(), type: $(".y_tripFinderOriginLocationSuggestionType").val() };
			$.cookie(cookieName,originJsonObject,{ expires : 10 });
			ACC.tripfinder.showDestinationLocations();
		}
		$('#y_tripFinderOriginLocation').css("background-image", "").removeClass("loadingOrigin");
	},
	
	bindChangeOriginLocation: function(){
		$('#y_destinationLocationSearchForm').submit(function (ev) {
			ev.preventDefault();
			var modifiedLocationCode = $('.y_tripFinderOriginLocationCode').val();
			var modifiedLocationType = $( ".y_tripFinderOriginLocationSuggestionType" ).val();
			var cookieName="tripFinderActivitySearch_"+$("#y_tripFinderActivity").val();
			$.cookie.json = true;
			var originJsonObject = { code: modifiedLocationCode, type: modifiedLocationType };
			$.cookie(cookieName,originJsonObject,{ expires : 10 });
			$.when( ACC.services.reloadDestinationLocation($(this).serialize()) ).then(
		 		// success
				function( data ) {
					$("#y_destinationLocationList").html(data.htmlContent);
				}
			);
	    });
	},
	
	bindOriginLocationAutosuggest : function() {
		$("#y_tripFinderOriginLocation").autosuggestion({
			autosuggestServiceHandler: function(locationText){
				var suggestionSelect = "#y_tripFinderOriginLocationSuggestions";
				// make AJAX call to get the suggestions
				$.when( ACC.services.getSuggestedOriginLocationsAjax(locationText) ).then(
					// success
					function( data ) {
					  	$(suggestionSelect).html(data.htmlContent);
						if(data.htmlContent){
							$(suggestionSelect).removeClass("hidden");
						}
					}
				);
			},
			suggestionFieldChangedCallback: function(){
				$('#y_destinationLocationSearchForm').submit();
			},
            attributes : ["Code", "SuggestionType"]
		});
	}
};
