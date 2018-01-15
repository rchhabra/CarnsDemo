ACC.fareselection = {
		
	selectedOutboundArrivalDateTime: null,
	selectedInboundDepartureDateTime: null,

	_autoloadTracc : [ 
			"init",
			"validateOutboundTimeSelection",
			"validateInboundTimeSelection",
			"bindNextPrev",
			"bindExpandContractTravelSelection",
			"bindWindowResize",
			"bindShowMore",
			"bindTravelClassSwitcher",
			"bindMobileLowestFromPrice",
			"bindMobilePriceArrowToggle",
			"bindMobileTripDetailsToggle",
			"setFromPriceForOptions" 
	],

	init : function() {
		// only collapse the fare-finder component on the fare-selection page
		/*if($('html').hasClass('y_isMobile')){
			$(".fieldset-inner-wrapper").collapse("hide")
		} */
	},

	validateOutboundTimeSelection : function() {
		$('#y_outbound .flight-option input[type=radio]').on('click', function() {
			var outboundArrival = $(this).val();
			if (ACC.fareselection.selectedInboundDepartureDateTime != undefined) {
				if (outboundArrival > ACC.fareselection.selectedInboundDepartureDateTime) {
					$('#timeValidationModal').modal('show');
					$(this).removeAttr("checked");
					if ($('#y_outbound .flight-option label').hasClass('selected')
						&& $('#y_outbound .flight-option input[type="radio"]').not('checked')) {
						$('#y_outbound .flight-option label')
								.removeClass('selected');
					}
					return;
				}
			}
			ACC.fareselection.selectedOutboundArrivalDateTime = outboundArrival;
		});
	},

	validateInboundTimeSelection : function() {
		$('#y_inbound .flight-option input[type=radio]').on('click', function() {
			var inboundDeparture = $(this).val();
			if (ACC.fareselection.selectedOutboundArrivalDateTime != undefined) {
				if (inboundDeparture < ACC.fareselection.selectedOutboundArrivalDateTime) {
					$('#timeValidationModal').modal('show');
					$(this).removeAttr("checked");
					if ($('#y_inbound .flight-option label').hasClass('selected')
						&& $('#y_inbound .flight-option input[type="radio"]').not('checked')) {
						$('#y_inbound .flight-option label')
								.removeClass('selected');
					}
					return;
				}
			}
			ACC.fareselection.selectedInboundDepartureDateTime = inboundDeparture;
		});
	},

	// bindNextPrev to move through the date tabs

	bindNextPrev : function() {
		$('.y_fareResultNext').on('click', function(e) {
		        e.preventDefault;

		        $(this).parent()
		        .siblings()
		        .children('.active')
		        .removeClass('active in')
		        .next()
		        .addClass('active in');

		        $(this).siblings()
		        .children('.active')
		        .removeClass('active')
		        .attr("aria-expanded","false")
		        .next().addClass('active')
		        .attr("aria-expanded","true");
		});

		$('.y_fareResultPrev').on('click', function(e) {
		        e.preventDefault;

		        $(this).parent()
		        .siblings()
		        .children('.active')
		        .removeClass('active in')
		        .prev()
		        .addClass('active in');

		        $(this).siblings()
		        .children('.active')
		        .removeClass('active')
		        .attr("aria-expanded","false")
		        .prev().addClass('active')
		        .attr("aria-expanded","true");
		});
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

	bindExpandContractTravelSelection : function() {

    	// contract
        $('.y_fareResultSelect').on("click", function() {
        	if($('html').hasClass('y_isMobile')) {
        		ACC.fareselection.contractExpandTravelSelection(true, $(this));
			}
        });

		// expand
        $('.y_fareResultChangeButton').on("click", function(e) {
        	if($('html').hasClass('y_isMobile')) {
                e.preventDefault;

        		ACC.fareselection.contractExpandTravelSelection(false, $(this));
            }

        });

	},

	// bindWindowResize removes & re-runs bindExpandContractTravelSelection() based on orientation, further details below

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
		$('.y_fareResultShowMore button').on("click", function() {
            $(this).parents('.y_fareResultTabWrapper')
            .children('.y_fareResultTabContent')
            .find('li.hidden')
            .removeClass('hidden');
            
            $(this).attr('disabled', true);
        });
	},

	// bindTravelClassSelectedSwitcher removes & adds class for styling when selecting travel class

	bindTravelClassSwitcher : function() {
		$(".y_fareSelectionSection").on('click', '#y_outbound .y_fareResultSelect' ,function(){
		    $(this).parent().on('change', function() {
		        if($('#y_outbound label').hasClass('selected') && $('#y_outbound label').not(':checked')) {
		            $('#y_outbound label').removeClass('selected');
		        }
		        $(this).addClass('selected').siblings().removeClass('selected');
		    });
		    ACC.itinerary.refreshFareSelectionItineraryComponent("#y_fareSelectionMockOutboundItinerary");

		});

		$(".y_fareSelectionSection").on('click', '#y_inbound .y_fareResultSelect' ,function(){
		    $(this).parent().on('change', function() {
		        if($('#y_inbound label').hasClass('selected') && $('#y_inbound label').not(':checked')) {
		            $('#y_inbound label').removeClass('selected');
		        }
		        $(this).addClass('selected').siblings().removeClass('selected');
		    });
		    ACC.itinerary.refreshFareSelectionItineraryComponent("#y_fareSelectionMockInboundItinerary");
		});
	},

	bindMobileLowestFromPrice : function() {

		var currentLowestPrice = 100000;
		var fromPrice = 0;

		// find the lowest price in each group
		$('.y_fareResultClassSelectGroup').each(function(){ 
			$(this).find('.price-desc').each(function(){ 
				fromPrice = parseInt($(this).html().match(/\d+/)[0]); 
				if(fromPrice < currentLowestPrice){
					currentLowestPrice = fromPrice;
				}
			});
			
			// set the lowest price field in the HTML
			$(this).siblings('.y_fareResultTravelDetails')
			.find('.y_fareResultLowestPrice')
			.html(currentLowestPrice);

		});
	},

	

	bindMobilePriceArrowToggle : function() {
		// Show/hide class select on mobile (arrows pointing down)
		$('.y_fareResultPriceButton').on('click',function(){
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
		$('.y_fareResultInfoTrigger').on('click',function(){
			$(this).next().slideToggle();
		});
	},

	setFromPriceForOptions : function(){
		$('.y_fareResultTravelOption').each(
				function(index,element){
					var listOfPrices = $(element).find('.y_fareResultClassSelectGroup .price-desc');
					for(var i=0; i<listOfPrices.length; i++){
						var price = $(listOfPrices[i]).html();
						if(price != 'X'){
							break;
						}
					}
					$(element).find('.y_fareResultLowestPrice').html(price.replace(/(\D*)(\d*\.)(\d*)/,'$1$2<span class="fare-decimal">$3</span>'));
				});
	},
}