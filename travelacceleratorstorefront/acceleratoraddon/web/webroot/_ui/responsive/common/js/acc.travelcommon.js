/**
 * The module for common scripts.
 * @namespace
 */
 ACC.travelcommon = {
	_autoloadTracc : [ 
	    "init",
		"initDatePickerDefaults",
		"bindBootstrapTooltip",
		"bindBootstrapPopover",
		"bindBootstrapQuantityInput",
		"bindCheckMobile",
		"bindNavbarActiveItem",
		"warnBeforeSessionExpires",
		"bindSessionContinue",
		"bindAsmModalAdjustPosition",
		"bindShowHideModifySearch",
		"bindShowHideFlightOptions",
		"bindTooltipArrow",
        "bindoffCanvasMenu",
        "bindEqualHeights",
        "bindFooterAdjustment",
        "bindModalExpandScroll",
        "bindColorboxRemoveInit",
        "bindEmptyAncillaryInfoRemoval",
        "bindDropdownCMSLinkAttributes"
	],
	
	schedulerId : 0,
	

	init : function() {
		if ($("#y_maxAllowedCheckInCheckOutDateDifference").val() > 0) {
			ACC.config.maxAllowedDateDifference = $(
					"#y_maxAllowedCheckInCheckOutDateDifference").val();
		} else {
			ACC.config.maxAllowedDateDifference = "20";
		}
	},
	
	/**
     * show warning popup message before the session expires.
     */
	warnBeforeSessionExpires : function() {
		function setSessionPrompt() {
			var cookieValue = $.cookie("sessionCookie");
			if (cookieValue) {
				var timeout = cookieValue - new Date().getTime();
				if (timeout <= 300000 && !$("#y_sessionExpirationPreWarningModal").hasClass("show")) {				
                    $('#y_sessionExpirationPreWarningModal').modal({
                        backdrop: 'static',
                        keyboard: false
                    });
					ACC.travelcommon.schedulerId=setTimeout(ACC.travelcommon.redirectToHomePage,timeout);				
				}
			}
		}
		setInterval(setSessionPrompt, 60000);
	},

	/**
     * extend session expiry time when that 'call to action' is clicked.
     */
	bindSessionContinue : function()
	{
		$("#y_sessionContinue").on('click', function() {
            $.when(ACC.services.renewSession()).then(function(){
                $("#y_sessionExpirationPreWarningModal").modal("hide");
                clearTimeout(ACC.travelcommon.schedulerId);
			});
		});
	},
	
	/**
     * redirect to the homepage.
     */
	redirectToHomePage : function(){
		var oUserInfo = $(".y_loggedIn");
		if (oUserInfo && oUserInfo.length === 1) {
			window.location = ACC.config.contextPath + "/logout";
		} else {
			window.location = ACC.config.contextPath;
		}
	},

	/**
     * convert UK date to US date format.
     * @param {string} ukDate - The date in UK format.
     * @return {string} - US date format.
     */
	convertToUSDate : function(ukDate) {
		var adata = ukDate.split('/');
        var dd = parseInt(adata[0],10);
        var mm = parseInt(adata[1],10);
        var yyyy = parseInt(adata[2],10);
        return (new Date(yyyy,mm-1,dd));
	},

	/**
     * convert US date to UK date format.
     * @param {string} usDate - The date is US format.
     * @return {string} - UK date.
     */
	convertToUKDate : function(usDate) {
		return ( usDate.getDate() ) +"/"+ ( usDate.getMonth ()+1 ) +"/"+ ( usDate.getFullYear() );
	},

	/**
     * get current date.
     * @return {string} - current UK date.
     */
	getTodayUKDate : function(){
		var today = new Date();
		return this.convertToUKDate(today);
	},
	
	addDays : function(theDate, days) {
	    return this.convertToUKDate(new Date(theDate.getTime() + days*24*60*60*1000));
	},
	
	addDaysToUsDate : function(theDate, days) {
	    return new Date(theDate.getTime() + days*24*60*60*1000);
	},
	/**
     * get the date in a hundred years time.
     * @return the date in a hundred years time, in UK format.
     */
	getHundredYearUKDate : function(){
		var inHundredYears = new Date();
		inHundredYears.setDate(inHundredYears.getDate() + 100*365);
		return this.convertToUKDate(inHundredYears);
	},

	/**
     * set the date picker configuration.
     */
	initDatePickerDefaults : function() {
		// Datepicker
	    $.datepicker.setDefaults(
	        $.extend($.datepicker.regional[''])
	    );
	    $.datepicker.setDefaults({
	        numberOfMonths: [1, 1],
	        defaultDate: new Date(),
	        dateFormat: 'dd/mm/yy'
	    });
	},

	/**
     * initialize bootstrap tooltips.
     */
	bindBootstrapTooltip : function() {
		// Bootstrap tooltip init
		$('[data-toggle="tooltip"]').tooltip();
		$('.modal').on('shown.bs.modal', function () {
			$('[data-toggle="tooltip"]').popover();
		});
	},

	/**
     * initialize bootstrap popovers.
     */
	bindBootstrapPopover : function() {
		// Bootstrap popover init
		$('[data-toggle="popover"]').popover();
		$('.modal').on('shown.bs.modal', function () {
			$('[data-toggle="popover"]').popover();
		});
	},

	/**
     * initialize quantity input fields.
     */
	bindBootstrapQuantityInput : function() {
		//plugin bootstrap minus and plus
		//http://jsfiddle.net/laelitenetwork/puJ6G/
		$(document).on('click', '.y_inputNumberChangeBtn', function(e) {
		    e.preventDefault();

		    fieldName = $(this).attr('data-field');
		    type = $(this).attr('data-type');
		    var input = $("input[name='" + fieldName + "']");
		    var currentVal = parseInt(input.val());
		    if (!isNaN(currentVal)) {
		        if (type == 'minus') {

		            if (currentVal > input.attr('data-min')) {
		                input.val(currentVal - 1).change();
		            }
		            if (parseInt(input.val()) == input.attr('data-min')) {
		                $(this).attr('disabled', true);
		            }

		        } else if (type == 'plus') {
					if (!input.attr('data-max')) {
						input.val(currentVal + 1).change();
					} else {
						if (currentVal < input.attr('data-max')) {
							input.val(currentVal + 1).change();
						}
						if (parseInt(input.val()) == input.attr('data-max')) {
							$(this).attr('disabled', true);
						}
					}
		        }
		    } else {
		        input.val(0);
		    }
		});
		$(document).on('focusin', '.y_inputNumber', function() {
		    $(this).data('oldValue', $(this).val());
		});
		$(document).on('change', '.y_inputNumber', function() {
		    var minValue = parseInt($(this).attr('data-min')),
		    	maxValue = parseInt($(this).attr('data-max')),
		    	valueCurrent = parseInt($(this).val());

		    var name = $(this).attr('name');
		    // if it falls within the range, set the quantity input value
		    if(valueCurrent >= minValue && (isNaN(maxValue) || valueCurrent <= maxValue)){
		    	$(this).attr('value', valueCurrent);
		    	// enable or disable minus button
		    	if (valueCurrent > minValue) {
			        $(".y_inputNumberChangeBtn[data-type='minus'][data-field='" + name + "']").removeAttr('disabled');
			    } 
			    else{
			    	$(".y_inputNumberChangeBtn[data-type='minus'][data-field='" + name + "']").attr('disabled','disabled');
			    }
			    // enable or disable plus button
			    if (isNaN(maxValue) || valueCurrent < maxValue) {
			        $(".y_inputNumberChangeBtn[data-type='plus'][data-field='" + name + "']").removeAttr('disabled');
			    } 
				else{
					$(".y_inputNumberChangeBtn[data-type='plus'][data-field='" + name + "']").attr('disabled','disabled');
				}
		    }
		    // if it's less than min, we set it to the previous value
		    else if(valueCurrent < minValue){
				console.log('Sorry, the minimum value was reached');
		        $(this).val($(this).data('oldValue'));
		    }
		    // if it's more than max, we set it to the previous value
		    else if(!isNaN(maxValue) && valueCurrent > maxValue){
				console.log('Sorry, the maximum value was reached');
		        $(this).val($(this).data('oldValue'));
		    }
		});
		$(".y_inputNumber").each(function(){
		    minValue = parseInt($(this).attr('data-min'));
		    maxValue = parseInt($(this).attr('data-max'));
		    valueCurrent = parseInt($(this).val());
		    name = $(this).attr('name');
		    if (valueCurrent > minValue) {
		        $(".y_inputNumberChangeBtn[data-type='minus'][data-field='" + name + "']").removeAttr('disabled');
		    } else {
		        $(".y_inputNumberChangeBtn[data-type='minus'][data-field='" + name + "']").attr('disabled', 'disabled');
		    }
		    if (isNaN(maxValue) || valueCurrent < maxValue) {
		        $(".y_inputNumberChangeBtn[data-type='plus'][data-field='" + name + "']").removeAttr('disabled');
		    } else {
		        $(".y_inputNumberChangeBtn[data-type='plus'][data-field='" + name + "']").attr('disabled', 'disabled');
		    }
		});
		$(document).on('keydown', '.y_inputNumber', function(e) {
		    // Allow: backspace, delete, tab, escape, enter and .
		    if ($.inArray(e.keyCode, [46, 8, 9, 27, 13]) !== -1 ||
		        // Allow: Ctrl+A
		        (e.keyCode == 65 && e.ctrlKey === true) ||
		        // Allow: home, end, left, right
		        (e.keyCode >= 35 && e.keyCode <= 39)) {
		        // let it happen, don't do anything
		        return;
		    }
		    // Ensure that it is a number and stop the keypress
		    if ((e.shiftKey || (e.keyCode < 48 || e.keyCode > 57)) && (e.keyCode < 96 || e.keyCode > 105)) {
		        e.preventDefault();
		    }
		});
	},

	/**
     * Add a class to the <HTML> if the screen is small, and hide all elements with .hide-on-mobile.
     */
	checkMobile : function() {
	    if ($(window).width() < 640) {
	        $('html').addClass('y_isMobile');
	        $('.hide-on-mobile').hide();
	         $('.mobile-show').show();
	    } else {
	        $('html').removeClass('y_isMobile');
	        $('.hide-on-mobile').show();
	        $('.mobile-show').hide();
	    }
	},

	/**
     * Re-apply mobile classes when the browser is resized.
     */
	bindCheckMobile : function() {
		ACC.travelcommon.checkMobile();
		$(window).resize(function(){ACC.travelcommon.checkMobile();});
	},
	
	/**
     * show active state for items in the navbar.
     */
	bindNavbarActiveItem : function(){
		var navbarItems = $(".y_navbar li");
		if(navbarItems){
			var oUserInfo = $(".y_loggedIn");
            if(oUserInfo && oUserInfo.length === 1){
            	$(navbarItems[2]).addClass("active");
            }else{
            	$(navbarItems[1]).addClass("active");
            }
		}
	},

	/**
     * change parameters in the current URL.
     * @param {array} params - what parameters to change.
     * @param {array} values - what values to change it to.
     */
	changeUrlParams: function(params, values) {
		var pageUrl = window.location.href;
		$.each(params, function( index, value ) {
			pageUrl = ACC.travelcommon.changeUrlParameterValue(pageUrl, params[index], values[index]);
		});
		
		if(pageUrl!=window.location){
			window.history.pushState({path:pageUrl},'',pageUrl);
		}
	},

	/**
     * change a single parameter in the URL.
     * @param {string} url - the full URL.
     * @param {array} name - parameters to change.
     * @param {array} values - value to change it to.
     * @return {string} - new URL.
     */
	changeUrlParameterValue: function(url, name, value) {
	    var paramName = ACC.travelcommon.getURLParameter(name);
	    if (paramName != null)
	    {
	        return url.replace(ACC.travelcommon.getURLParameter(name), value);
	    } else {
            url += "&"+name+"="+value;
	        return url;
	    }
	},

	/**
     * get a single parameter value from the URL.
     * @param {string} sParam - the parameter to get.
     * @return {string} - the value of that parameter.
     */
	getURLParameter: function(sParam) {
	    var sPageURL = window.location.search.substring(1);
	    var sURLVariables = sPageURL.split('&');
	    for (var i = 0; i < sURLVariables.length; i++) {
	        var sParameterName = sURLVariables[i].split('=');
	        if (sParameterName[0] == sParam) {
	            return sParameterName[1];
	        }
	    }
	},

	 // Validation for departure and arrival locations
	 isValidLocation: function (location) {
		 return /^[A-Za-z0-9\u00C0-\u017F\s]+$/i.test(location);
	 },

	 bindAsmModalAdjustPosition: function() {
	 	var asmNavigation = $('#_asm').outerHeight();
	 	$('.modal').css('top',asmNavigation);
	 },

	bindShowHideModifySearch: function() {

	 	var buttonParent = $('.modify-search').prev('div');

		$('.modify-search').on('shown.bs.collapse', function() {
			buttonParent.find('.show-text').addClass('hidden');
			buttonParent.find('.hide-text').removeClass('hidden');
		})

		$('.modify-search').on('hidden.bs.collapse', function() {
			buttonParent.find('.show-text').removeClass('hidden');
			buttonParent.find('.hide-text').addClass('hidden');
		})
	},

	bindShowHideFlightOptions: function() {

	 	var buttonParent = $('#flightOptionsCollapse').prev('div');

		$('#flightOptionsCollapse').on('shown.bs.collapse', function() {
			buttonParent.find('.show-text').addClass('hidden');
			buttonParent.find('.hide-text').removeClass('hidden');
		})

		$('#flightOptionsCollapse').on('hidden.bs.collapse', function() {
			buttonParent.find('.show-text').removeClass('hidden');
			buttonParent.find('.hide-text').addClass('hidden');
		})
	},

	bindTooltipArrow: function() {
		// remove the top positioning value for .arrow - this bug happens on Chrome only, zoom level 75%
	 	$('[data-toggle="popover"]').on('shown.bs.popover', function () {
			$('div[id^="popover"] .arrow').css('top','');
		})
	},
	
	bindoffCanvasMenu : function() {

	   $('[data-toggle=offcanvas]').click(function() {
	         $('.row-offcanvas').toggleClass('active');
	         $('#filter .sidebar-nav .nav, #filter .sidebar-nav').toggleClass('fixed');
	          $('html, main').toggleClass('overflow');
	   });
	 },

	 bindFooterAdjustment : function() {

		var docHeightCloseDropDown;

		var $dropdown = $('.dropdown');

		$dropdown.on('show.bs.dropdown', function () {
			docHeightCloseDropDown = $(document).height();
		});

		$dropdown.on('shown.bs.dropdown', function () {
			var localDocHeightCloseDropDown = docHeightCloseDropDown,
			localDocHeightOpenDropDown = $(document).height(),
			docHeightDifference = localDocHeightOpenDropDown - localDocHeightCloseDropDown,
			footerHeight = $('.footer-wrapper').height(),
			footerAdjustment = docHeightDifference + footerHeight;

			if(localDocHeightOpenDropDown > localDocHeightCloseDropDown){
				$('.footer-wrapper').css('bottom', - footerAdjustment);
			}

		});

		$('.dropdown').on('hidden.bs.dropdown', function () {
			$('.footer-wrapper').removeAttr('style');
		});

	 },

    bindEqualHeights : function() {
        function heightsEqualizer(selector) {
            var elements = document.querySelectorAll(selector),
                maxHeight = 0,
                len = 0,
                i;
         
            if ((elements) && (elements.length > 0)) {
                len = elements.length;
         
                for (i = 0; i < len; i++) { // get max height
                    elements[i].style.height = ''; // reset height attr
                    if (elements[i].clientHeight > maxHeight) {
                        maxHeight = elements[i].clientHeight;
                    }
                }
         
                for (i = 0; i < len; i++) { // set max height to all elements
                    elements[i].style.height = maxHeight + 'px';
                }
            }
        }

        if (!$('html').hasClass('y_isMobile')){

        	heightsEqualizer('.results-list .summary');
            heightsEqualizer('.price');
            heightsEqualizer('.reviews');

            window.addEventListener('resize', function(){
            	heightsEqualizer('.results-list .summary');
                heightsEqualizer('.price');
                heightsEqualizer('.reviews');
            });
        }       

    },

    bindModalExpandScroll : function() {
    	if($('html').hasClass('y_isMobile')){
    		$('.modal .panel-accommodation .panel-header-link').on('click', function() {
				$('html, .modal').animate({ scrollTop: $(document).height() }, 'slow');
			});
    	}
    },

    /**
     * Function to fix the issue of grey page over modals and ASM Customer 360 not working, due to conflicts between
     * Bootstraps modals and JQuery colorbox plugin.
     *
     * On modal show, the colorbox is removed.
     * On modal hidden, the colorbox is initialized again for ASM customer 360, calling the OOTB function in assistedservicestorefront.js.
     * This function is responsible to initialize the colorbox linked to the button.
     */
	bindColorboxRemoveInit : function() {
        $(document).on('show.bs.modal', '.modal', function () {
            $.colorbox.remove();
        });
        $(document).on('hidden.bs.modal', '.modal', function(){
            openCustomer360Colorbox($('.js-customer360'));
        });
	},

	// function to remove empty <ul> from the DOM as no viable backend or CSS solution available
	bindEmptyAncillaryInfoRemoval : function() {
		$('.y_additionalInformationModal').on('show.bs.modal', function () {
			$('#ancillary-info ul', this).each(function (){
				if(!$(this).has('li').length) {
					$(this).remove();
				}
			});
		})
        
	},
	bindDropdownCMSLinkAttributes: function(){
		 $('.dropdown-toggle').attr("data-toggle","dropdown").attr("role","button").attr("aria-haspopup","true").attr("aria-expanded","false");
	 }

};
