ACC.packagelisting = {

    _autoloadTracc: [
        "loadSelectedView",
        "bindStopPropagation",
        "bindFacetCheckboxes",
        "bindSortSelect",
        "bindPropertyFilterValidation",
        "bindClearFacetSelection",
        "bindClearAllFacetSelection",
        "bindPackageListingShowMore",
        "readCustomerReviews",
        "bindDisplayPackagesOnMap",
        "bindDisplayPackagesOnListView",
        "bindDisplayPackagesOnGridView",
        "bindPriceRangeSelector"
    ],

    isMapViewPresent:false,
    markerProperties:null,
    displayedInfoWindow:null,
    displayedMap:null,
    defaultMapBounds:null,

    loadSelectedView:function(){
        var resultsViewType=new String($('.y_packageListingPageParams').data('resultviewtype'));
        if(resultsViewType.toUpperCase()==="MAPVIEW"){
            var priceRange=$('#y_packageListingFacetForm').find('input[name="priceRange"]').val();
            ACC.packagelisting.loadSelectedViewHtml($( ".y_packageListingShowMore" ).data('pagenumber'), resultsViewType,priceRange);
        }
    },

    loadSelectedViewHtml : function(pageNumber, resultsViewType,priceRange){
        $.when(ACC.services.getPackageListForSelectedView(pageNumber, resultsViewType,priceRange)).then(function(data) {
            if(resultsViewType.toUpperCase()==="MAPVIEW"){
                ACC.packagelisting.markerProperties = data;
                ACC.packagelisting.markPropertiesOnMap();
            }
            else {
                $("#y_packageResults").html(data.htmlContent);
                if (data.hasMoreResults) {
                    $(".y_packageListingShowMore").show();
                    $(".y_shownResultId").html(data.totalshownResults);
                    $(".y_shownResultId").show();
                } else {
                    $(".y_packageListingShowMore").hide();
                    $(".y_shownResultId").hide();
                }
                if(resultsViewType.toUpperCase()==="LISTVIEW"){
                    $("#y_packageResults").removeClass("deal-grid");
                }
                else if(resultsViewType.toUpperCase()==="GRIDVIEW"){
                    $("#y_packageResults").addClass("deal-grid");
                }
            }
        });
    },

    bindDisplayPackagesOnListView : function() {
        $("#y_packageListingListView").click(function(e) {
            $("#y_packageListingListView").addClass("hidden");
            $("#y_packageListingGridView").removeClass("hidden");
            $("#y_packageListingShowResults").removeClass("hidden");
            $("#y_packageListingSortSelection").removeClass("hidden");
            ACC.packagelisting.hideMap();
            ACC.packagelisting.setResultViewType("ListView");
        });
    },

    bindDisplayPackagesOnGridView : function() {
        $("#y_packageListingGridView").click(function(e) {
            $("#y_packageListingListView").removeClass("hidden");
            $("#y_packageListingGridView").addClass("hidden");
            $("#y_packageListingShowResults").removeClass("hidden");
            $("#y_packageListingSortSelection").removeClass("hidden");
            ACC.packagelisting.hideMap();
            ACC.packagelisting.setResultViewType("GridView");
            ACC.travelcommon.bindEqualHeights();
        });
    },

    bindDisplayPackagesOnMap : function() {
        $("#y_packageListingMapView").click(function(e) {
            ACC.packagelisting.setResultViewType("MapView");
            ACC.packagelisting.markPropertiesOnMap();
        });
    },

    markPropertiesOnMap: function()
    {
        $("#y_packageListingListView").removeClass("hidden");
        $("#y_packageListingGridView").removeClass("hidden");
        $("#y_packageListingShowResults").addClass("hidden");
        $("#y_packageListingSortSelection").addClass("hidden");
        ACC.packagelisting.displayMap();
        if (ACC.packagelisting.markerProperties != null && ACC.packagelisting.markerProperties.length>0) {
            ACC.config.googleApiKey = $('.y_packageListingPageParams').data('googleapi');
            ACC.packagelisting.addGoogleMapsApi("ACC.packagelisting.loadGoogleMap");
            ACC.packagelisting.isMapViewPresent=true;
        }
    },

    hideMap:function()
    {
        $("#y_googleMapResult").addClass("hidden");
        $("#y_packageListingMapView").removeClass("hidden");
    },

    displayMap:function()
    {
        $("#y_googleMapResult").removeClass("hidden");
        $("#y_packageListingMapView").addClass("hidden");
    },

    setResultViewType:function(resultViewType){
        var pageNum = $( ".y_packageListingShowMore" ).data('pagenumber');
        if(pageNum == undefined){
            pageNum = 1;
        }
        var priceRange=$('#y_packageListingFacetForm').find('input[name="priceRange"]').val();
        $.when(ACC.packagelisting.loadSelectedViewHtml(pageNum, resultViewType,priceRange)).then(function() {
            $("#y_resultsViewTypeForFacetForm").val(resultViewType);
            $("#y_resultsViewTypeForPropertyFilter").val(resultViewType);
            $("#y_resultsViewTypeForSortForm").val(resultViewType);
            $('.y_packageListingPageParams').data('resultviewtype',resultViewType);
            ACC.travelcommon.changeUrlParams(["resultsViewType"], [resultViewType]);
        });
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
        var data=ACC.packagelisting.markerProperties;

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
                        ACC.packagelisting.displayedInfoWindow=infoWindow;
                    }
                })(marker, i));

            // Automatically center the map fitting all markers on the screen
            map.fitBounds(bounds);
            ACC.packagelisting.defaultMapBounds=bounds;

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
                            right : '45px',
                            top : '30px' // button repositioning
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

        ACC.packagelisting.displayedMap=map;

    },

    bindStopPropagation: function () {
        $('#filter .dropdown-menu').click(function (e) {
            e.stopPropagation();
        });
    },

    bindFacetCheckboxes: function () {
        $(document).on("change", ".y_packageListingFacetCheckbox", function () {
            $('#y_packageListingFacetForm').find('input[name="priceRange"]').val($('.y_priceRangeValue').val());
            $("#y_packageListingFacetForm").find(":input[name=q]").val($(this).val());
            $("#y_packageListingFacetForm").submit();
        });
    },

    bindSortSelect: function () {
        $(document).on("change", "#y_packageSearchSortSelect", function () {
            $("#y_packageSearchSortForm").submit();
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
            // focusCleanup: true,
            rules: {
                propertyName: "required"
            },
            messages: {
                propertyName: ACC.addons.travelacceleratorstorefront['error.accommodationselection.filter.property']
            }
        });
    },

    bindClearFacetSelection: function () {
        $('.y_packageClearFacetFilter').on('click', function () {
            var packageListingFacetForm = $('#y_packageListingFacetForm');
            if ($(this).data('facetcode') == 'propertyName') {
                packageListingFacetForm.find('input[name="propertyName"]').remove();
            } else {
                packageListingFacetForm.find(":input[name=q]").val($(this).data('query'));
            }
            packageListingFacetForm.submit();
        });
    },

    bindClearAllFacetSelection: function () {
        $('.y_packageListing_clearAllFacetFilter').on('click', function () {
            var packageListingFacetForm = $('#y_packageListingFacetForm');
            packageListingFacetForm.find('input[name="propertyName"]').val("");
            packageListingFacetForm.find('input[name="q"]').val("");
            packageListingFacetForm.submit();
        });
    },

    bindPackageListingShowMore: function () {
        $(".y_packageListingShowMore").on('click', function () {
            var pageNumber = $(this).data('pagenumber');
            pageNumber = pageNumber + 1;
            ACC.packagelisting.getShowMoreResults(pageNumber);
            ACC.travelcommon.bindEqualHeights();
        });
    },

    getShowMoreResults : function(pageNumber){
        $.when(ACC.services.getPackageListing(pageNumber, $("#y_resultsViewTypeForFacetForm").val(), $('#y_packageListingFacetForm').find('input[name="priceRange"]').val())).then(
            function (data) {
                $("#y_packageResults").append(data.htmlContent);
                if (!data.hasMoreResults) {
                    $(".y_packageListingShowMore").hide();
                }
                $(".y_shownResultId").html(data.totalshownResults);
                $(".y_packageListingShowMore").data('pagenumber', pageNumber);
            });
        return false;
    },

    readCustomerReviews : function() {
        $(document).on("click", ".y_packageCustomerReview", function() {
            var accommodationOfferingCode=$(this).data('accommodationofferingcode');
            $.when(ACC.services.readCustomerReviews(accommodationOfferingCode)).then(
                function(data) {
                    $(".y_customerReviewsModal").html(data.customerReviewsModalHtml);
                    $("#y_reviewsModal").modal();
                });
            return false;
        });
    },

    bindPriceRangeSelector : function() {
        var currentCurrencySymbol=$("a.dd-selected").text();
        var priceRangeSelected=$('#y_packageListingFacetForm').find('input[name="priceRange"]').val();
        var minPriceRange=lowerPriceRange=Number($('.y_priceRangeValue').data('minpricerange'));
        var maxPriceRange=upperPriceRange=Number($('.y_priceRangeValue').data('maxpricerange'));
        if(priceRangeSelected) {
            var priceRangelimitArr=priceRangeSelected.split(' - ');
            if(priceRangelimitArr.length>1) {
                lowerPriceRange = Number(priceRangelimitArr[0].substring(1));
                lowerPriceRange=lowerPriceRange<minPriceRange?minPriceRange:lowerPriceRange;
                lowerPriceRange=lowerPriceRange>maxPriceRange?maxPriceRange:lowerPriceRange;

                upperPriceRange = Number(priceRangelimitArr[1].substring(1));
                upperPriceRange=upperPriceRange>maxPriceRange?maxPriceRange:upperPriceRange;
                upperPriceRange=upperPriceRange<minPriceRange?minPriceRange:upperPriceRange;
            }
        }

        $('#price-slide').slider({
            range:true,
            min: minPriceRange,
            max: maxPriceRange,
            values: [lowerPriceRange, upperPriceRange],
            slide: function(event, ui) {
                $('.y_priceRangeValue').val(currentCurrencySymbol + ui.values[0] + ' - '+currentCurrencySymbol + ui.values[1]);
            },
            change: function(event, ui) {
                var resultsViewType=new String($('.y_packageListingPageParams').data('resultviewtype'));
                var pageNumber = $(".y_packageListingShowMore").data('pagenumber') ? Number($(".y_packageListingShowMore").data('pagenumber')) : 1;
                ACC.packagelisting.showPackagesWithInPriceRangeFilter(resultsViewType,pageNumber,$('.y_priceRangeValue').val());
            }
        });
        $('.y_priceRangeValue').val(currentCurrencySymbol + lowerPriceRange +' - '+currentCurrencySymbol + upperPriceRange);
    },

    showPackagesWithInPriceRangeFilter : function(resultsViewType,pageNumber,priceRange){
        var filteredFacetCode="&priceRange=";
        $.when(ACC.services.getPackageListingWithPriceRangeFilter(resultsViewType,pageNumber,priceRange)).then(
            function (data) {
                $('#y_packageListingFacetForm').find('input[name="priceRange"]').val(priceRange);
                $('.y_filterNameForm').find('input[name="priceRange"]').val($('.y_priceRangeValue').val());
                var packageListingUrl=window.location.search;
                if (packageListingUrl.search(filteredFacetCode)>-1) {
                    packageListingUrl = packageListingUrl.substring(0,packageListingUrl.indexOf(filteredFacetCode));
                }
                packageListingUrl+=filteredFacetCode+priceRange;
                history.pushState(null,null,packageListingUrl);
                var totalNumberOfResults=0;
                if(resultsViewType.toUpperCase()==="MAPVIEW") {
                    if(data[0]) {
                        totalNumberOfResults=data[0].totalNumberOfResults;
                        $(".y_totalNumberOfResults").html(data[0].totalNumberOfResultsText);
                        ACC.packagelisting.markerProperties=data;
                        ACC.packagelisting.markPropertiesOnMap();
                        ACC.packagelisting.displayMap();
                    } else {
                        $(".y_totalNumberOfResults").html(data.totalNumberOfResultsText);
                        ACC.packagelisting.hideMap();
                    }
                } else {
                    $("#y_packageResults").html(data.htmlContent);
                    totalNumberOfResults=data.totalNumberOfResults;
                    $(".y_totalNumberOfResults").html(data.totalNumberOfResultsText);

                    if (data.hasMoreResults) {
                        $(".y_packageListingShowMore").show();
                        $(".y_shownResultId").html(data.totalshownResults);
                        $(".y_shownResultId").show();
                    } else {
                        $(".y_packageListingShowMore").hide();
                        $(".y_shownResultId").hide();
                    }

                    if(totalNumberOfResults>0) {
                        $("#y_packageListingSortSelection").removeClass("hidden");
                    } else {
                        $("#y_packageListingSortSelection").addClass("hidden");
                    }
                }

                if(totalNumberOfResults>0) {
                    $(".y_resultsViewOptions").removeClass("hidden");
                } else {
                    $(".y_resultsViewOptions").addClass("hidden");
                }
            });
        return false;
    }
};
