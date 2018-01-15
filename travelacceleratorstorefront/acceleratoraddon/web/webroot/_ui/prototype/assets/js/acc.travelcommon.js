ACC.travelcommon = {
	// common scripts
	_autoloadTracc : [ 
		"initDatePickerDefaults",
		"bindBootstrapTooltip",
		"bindBootstrapPopover",
		"bindBootstrapQuantityInput",
		"bindCheckMobile",
		"costCentreHide",
		"fixedContinueButton",
		"bindShowHideShowRoomOptions",
		"bindShowHideModifySearch",
		"bindShowHideFlightOptions"
	],
	
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

	bindBootstrapTooltip : function(argument) {
		// Bootstrap tooltip init
		$('[data-toggle="tooltip"]').tooltip()
	},

	bindBootstrapPopover : function(argument) {
		// Bootstrap popover init
	    $('[data-toggle="popover"]').popover();
	},

	bindBootstrapQuantityInput : function(argument) {
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

		            if (currentVal < input.attr('data-max')) {
		                input.val(currentVal + 1).change();
		            }
		            if (parseInt(input.val()) == input.attr('data-max')) {
		                $(this).attr('disabled', true);
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

		    name = $(this).attr('name');
		    // if it falls within the range, set the quantity input value
		    if(valueCurrent >= minValue && valueCurrent <= maxValue){
		    	$(this).attr('value', valueCurrent);
		    	// enable or disable minus button
		    	if (valueCurrent > minValue) {
			        $(".y_inputNumberChangeBtn[data-type='minus'][data-field='" + name + "']").removeAttr('disabled');
			    } 
			    else{
			    	$(".y_inputNumberChangeBtn[data-type='minus'][data-field='" + name + "']").attr('disabled','disabled');
			    }
			    // enable or disable plus button
			    if (valueCurrent < maxValue) {
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
		    else if(valueCurrent > maxValue){
				console.log('Sorry, the maximum value was reached');
		        $(this).val($(this).data('oldValue'));
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

	// Add a class to the <HTML> if the screen is small, and hide all elements with .hide-on-mobile
	checkMobile : function() {
	    if ($(window).width() < 768) {
	        $('html').addClass('y_isMobile');
	        $('.hide-on-mobile').hide();
	         $('.mobile-show').show();
	    } else {
	        $('html').removeClass('y_isMobile');
	        $('.hide-on-mobile').show();
	        $('.mobile-show').hide();
	    }
	},

	bindCheckMobile : function() {
		ACC.travelcommon.checkMobile();
		$(window).resize(function(){ACC.travelcommon.checkMobile()});
	},

	changeUrlParams: function(params, values) {
		//e.preventDefault();
		/*
		if uncomment the above line, html5 non-supported browsers won't change the url but will display the ajax content;
		if commented, html5 non-supported browsers will reload the page to the specified link.
		*/
		var pageUrl = window.location.href;
		$.each(params, function( index, value ) {
			pageUrl = ACC.travelcommon.changeUrlParameterValue(pageUrl, params[index], values[index]);
		});
		
		if(pageUrl!=window.location){
			window.history.pushState({path:pageUrl},'',pageUrl);
		}
	},

	changeUrlParameterValue: function(url, name, value) {
		return url.replace(ACC.travelcommon.getURLParameter(name), value);
	},

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
	
	costCentreHide: function () {
		$('#y_paymentType1').click(function() {
		  $('#y_pdCostCentre').parent().hide('fast');
		});
		$('#y_paymentType2').click(function() {
			if ($('#y_pdCostCentre').parent().is(':hidden')){
				$('#y_pdCostCentre').parent().show('fast');
			}
		});
	},

	fixedContinueButton: function () {

		// function that makes continue button appear & fix in position on scroll

		function continueButton() {

	    	var continueBtn = $('.continue'),
	        duration = 300,
	        footerHeight = $('footer').outerHeight(),
	        windowWidth = $(window).width();

	        continueBtn.fadeIn(duration);

	        if(!$('html').hasClass('y_isMobile')) {
				continueBtn.css('bottom',footerHeight+20);
			}

	        $(window).scroll(function() {

	        	var doc = $(document),
	        	docHeight = doc.height(),
	        	winHeight = $(window).height(),
            	distanceFromBottom = Math.floor(docHeight - doc.scrollTop() - winHeight);

            	if($('html').hasClass('y_isMobile')){

					if(distanceFromBottom <= footerHeight) {
						continueBtn.addClass('continue-fixed');
						continueBtn.removeClass('continue-scroll');
						
					} else if(distanceFromBottom > footerHeight) {
						continueBtn.removeClass('continue-fixed');
						continueBtn.addClass('continue-scroll');
						
					}
				} 
	        });
		}

		// event to display continue button on accommodation details pages

	    $('.y_roomSelect').on('change', function() {
	    	continueButton();
	    });

	    // event to display continue button on fare selection page

	    var outBound = $('#y_outbound'),
	    inBound = $('#y_inbound'),
	    outFareSelect = $('#y_outbound .y_fareResultSelect'),
	    inFareSelect = $('#y_inbound .y_fareResultSelect')

	    // check if journey is only outbound or outbound & inbound
	    if(outBound.length && inBound.length == 0) {
    		if(outFareSelect.is(':checked')){
    			continueButton();
    		}
			outFareSelect.on('click', function() {
		    	continueButton();
		    });
	    } else if (outBound.length && inBound.length) {
	    	inFareSelect.on('click', function() {
		    	if(outFareSelect.is(':checked') && inFareSelect.is(':checked')){
		    		continueButton();
		    	}
		    });
	    }

	    if($('.y_ancillarySection').length) {
	    	continueButton();
	    }

	},

	bindShowHideShowRoomOptions: function() {

        var buttonParent = $('.y_roomOptionsCollapse');
        var roomOptionsCollapse = $('#roomOptionsCollapse');

        roomOptionsCollapse.on('shown.bs.collapse', function() {
            buttonParent.find('.show-text').addClass('hidden');
            buttonParent.find('.hide-text').removeClass('hidden');
        });

        roomOptionsCollapse.on('hidden.bs.collapse', function() {
            buttonParent.find('.show-text').removeClass('hidden');
            buttonParent.find('.hide-text').addClass('hidden');
        });
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

}