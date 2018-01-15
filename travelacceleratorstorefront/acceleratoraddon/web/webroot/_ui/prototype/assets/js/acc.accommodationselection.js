ACC.accommodationselection = {

	_autoloadTracc : [ 
		"bindAccommodationModifyValidation",
        "bindoffCanvasMenu",
        "bindInputSlider",
        "bindStopPropagation",
        "bindClearAll",
        "bindRemoveRoom",
        "bindEqualHeights"
	],

	componentParentSelector: '.y_accommodationModifyForm',

	bindAccommodationModifyValidation : function() {

		// Custom validator to handle validation for HTML input elements that have the 'required' attribute
		
		$(ACC.accommodationselection.componentParentSelector).validate({
            errorElement: "span",
            errorClass: "fe-error",
            ignore: ".fe-dont-validate",
            submitHandler: function() { window.location.href = 'ph2-accommodation-search-default.html' },
            onfocusout: function(element) { $(element).valid(); },
            rules: {
                destination: "required",
                accommodationDatePickerCheckIn: "required",
                accommodationDatePickerCheckOut: "required"
            },
            messages: {
                destination: "Please enter a destination.",
                accommodationDatePickerCheckIn: "Please select a check in date.",
                accommodationDatePickerCheckOut: "Please select a check out date."
            }
        });


        $('#accommodationDatePickerCheckIn').datepicker({ 
            onClose: function( selectedDate ) {
                $( "#accommodationDatePickerCheckIn" ).datepicker( "option", "minDate", selectedDate );
                $(this).valid();
          }
        });
        $('#accommodationDatePickerCheckOut').datepicker({ 
            onClose: function( selectedDate ) {
                $(this).valid();
          }
        });
		
	},

    bindoffCanvasMenu : function() {

        $('[data-toggle=offcanvas]').click(function() {
            $('.row-offcanvas').toggleClass('active');
            $('#filter .sidebar-nav .nav, #filter .sidebar-nav').toggleClass('fixed');
            $('html, main').toggleClass('overflow');
        });
        
    },

    bindInputSlider : function() {

        $('.y_inputSlider').on('propertychange input', function () {
            $('output[for='+$(this).attr('id')+']').val($(this).val());
        });
        
    },

    bindStopPropagation : function() {

        $('#filter .dropdown-menu').click(function(e) {
            e.stopPropagation();
        });

    },


    bindClearAll : function() {

        $('.y_clearAll').click(function() {
            $(this).parents('.dropdown-menu').find('input:checkbox').removeAttr('checked');
        });

    },

    bindRemoveRoom : function() {

        $('.y_removeRoom').click(function() {
            $(this).closest($('[id^="accommodation-summary-section"]')).fadeOut('fast', function() {
                $(this).remove();
            });
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

            heightsEqualizer('.summary');
            heightsEqualizer('.price');

            window.addEventListener('resize', function(){
                heightsEqualizer('.summary');
                heightsEqualizer('.price');
            });
        }       

    }

}
