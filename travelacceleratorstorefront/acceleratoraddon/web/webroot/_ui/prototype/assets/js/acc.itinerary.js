ACC.itinerary = {
		
	_autoloadTracc : [ 
		[ "bindDesktopStickySideBar", $(".y_itinerarySideBar").length != 0 ], 
		[ "bindMobileItineraryOpenClose", $(".y_itinerarySideBar").length != 0 ]
	],

	// Sticky sidebar functionality
	checkAffixSidebar : function() {
		var $sideBar = $(".y_itinerarySideBar"),
			$sideBarContent = $(".y_itinerarySideBarContent");

		// add stickiness
		function addAffixOnSidebar(){
			$sideBar.affix({
				offset: {
					top: $sideBar.offset().top,
					bottom: function () {
				      return ($('.footer-wrapper').outerHeight(true))
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
			$(".y_itinerarySideBar").removeAttr('style');
			if(!$(".y_itinerarySideBar").hasClass('open')){
				$(".y_itinerarySideBarContent").removeAttr('style');
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
	
	bindDesktopStickySideBar : function() {

		// apply sticky sidebar on load
		ACC.itinerary.checkAffixSidebar();

		// on window resize, we apply the sticky sidebar for desktop
		// NOTE: this is run only every half a second to avoid calling the method too many times during resize
		var resizeTimeout;
		$(window).resize(function(){
		    clearTimeout(resizeTimeout);
		    resizeTimeout = setTimeout(function(){    
		        ACC.itinerary.checkAffixSidebar();
		    }, 500);
		});

		// when panels are opened/closed (i.e. the content's height has changed), refresh the sticky sidebar
		$(".show-hide-button").on("click", function() {
			
			var target = $(this).data("target");
			$(target).on("hidden.bs.collapse shown.bs.collapse", function () {
			  	ACC.itinerary.checkAffixSidebar();
			});
		});

	},
	
	// expand/collapse itinerary on mobile
	bindMobileItineraryOpenClose : function() {
		$(document).on("click", ".y_fareSlide", function() {
			if ($(":animated").length) {
				return false; // don't respond to clicks until
								// animation completed
			}

			var $sideBar = $(".y_itinerarySideBar"),
			$sideBarContent = $(".y_itinerarySideBarContent");

			if(!$sideBar.hasClass("open")) {
				$sideBar.addClass("open pa");

				// scroll the itinerary up to the top, leaving just enough room for the header				
				var elementOffset = $sideBar.offset().top - $(".navbar-header").height();
				$("html, body").animate({ scrollTop: elementOffset+"px" });

				// show all of the itinerary content
				$sideBarContent.slideDown();

				// darken 
				$("body").append($("<div class='modal-backdrop fade in'></div>")).promise().done(function(){
					$(".modal-backdrop").addClass("in");
				});

			} else {
				$sideBar.removeClass("open pa");

				var scrollTop     = $(window).scrollTop(),
					winHeight = ( $(window).height() < $sideBar.height() )? $(window).height() : $sideBar.height(),
					scrollNew = scrollTop - winHeight + 140;

				$("html, body").animate({ scrollTop: scrollNew+"px" }); 
				$sideBarContent.slideUp();
				
				$(".modal-backdrop").remove();
			}

		});
	},

	// update the itinerary HTML code (for the Fare Selection page) 
	refreshFareSelectionItineraryComponent : function(componentId) {
		// call the web service to get the response HTML
		//....
		$(componentId).show();

		// refresh the sticky sidebar
		ACC.itinerary.checkAffixSidebar();
	}
}