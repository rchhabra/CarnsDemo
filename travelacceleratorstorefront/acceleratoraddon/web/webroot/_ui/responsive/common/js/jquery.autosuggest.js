// Autosuggestion plugin for the travel accelerator
(function ( $ ) {
	$.fn.autosuggestion = function(setup){

		// default options.
        var settings = $.extend({
            suggestionInput: this,				// the input field
			suggestionSelect: "#"+this.attr("id")+"Suggestions",	// the suggestions box (that contains all the suggestions)
			suggestionLinkClass: ".autocomplete-suggestion",	// a single suggestion
			autosuggestServiceHandler: function(locationText){},
			suggestionFieldChangedCallback: function(){},
			attributes : [],		// attributes to be set
			isName:false
        },setup);
        
	    function setSearchFieldValue($_target) {
			settings.suggestionInput.val($_target.text());
    		
    		$.each(settings.attributes, function(index, attribute) {
    			var elementClassName = "." + settings.suggestionInput.attr("id") + attribute;
    			$(elementClassName).val($_target.data(attribute.toLowerCase()));
			});
    		
    		$(settings.suggestionSelect).addClass("hidden");
    		settings.suggestionInput.focus();

    		// callback function after field is selected
    		settings.suggestionFieldChangedCallback();
		}
	    
	    function setSearchFieldValueArrow($_target) {
			settings.suggestionInput.val($_target.text());
    		
    		$.each(settings.attributes, function(index, attribute) {
    			var elementClassName = "." + settings.suggestionInput.attr("id") + attribute;
    			$(elementClassName).val($_target.data(attribute.toLowerCase()));
			});
		}

        function clearSuggestionAttributes() {
    		$.each(settings.attributes, function(index, attribute) {
    			var elementClassName = "." + settings.suggestionInput.attr("id") + attribute;
    			$(elementClassName).val(null);
			});
		}

		// Bind all the event listeners
		function bindAutosuggestListeners(){
			$(document).on("click", settings.suggestionSelect + " " + settings.suggestionLinkClass, function (e){
				e.preventDefault();
				setSearchFieldValue($(this));
			});

			// clicking outside of the selection will close it
			$(document).mouseup(function (e)
			{
				if($(settings.suggestionSelect).is(":visible")){
					var $_target = $(e.target);
				    if (!$(settings.suggestionSelect).is($_target) // if the target of the click isn't the $(settings.suggestionSelect)...
				    	&& !settings.suggestionInput.is($_target) // if the target of the click isn't the settings.suggestionInput...
				        && $(settings.suggestionSelect).has($_target).length === 0) // ... nor a descendant of the $(settings.suggestionSelect)
				    {
				        $(settings.suggestionSelect).addClass("hidden");
				    }
				}
			});

			/** Autocomplete keyboard functionality **/
			// pressing down on the input field goes into the suggestions
			settings.suggestionInput.on('keydown', function (e)
			{
				// Up-Down key = scroll down
				if (e.keyCode === 40 || e.keyCode === 38) {
					e.preventDefault();
					var $suggestion;
					if(e.keyCode === 40) // for down key
						$suggestion=$(settings.suggestionSelect).find(settings.suggestionLinkClass).first();
					else // for up key
						$suggestion=$(settings.suggestionSelect).find(settings.suggestionLinkClass).last();
					setSearchFieldValueArrow($suggestion);
					$suggestion.focus();
			    }
				// Tab key = ignore
			    else if(e.keyCode === 9){
			    	$(settings.suggestionSelect).addClass("hidden");
			    }
			});
			// pressing up or down on the suggestions scrolls through them
			$(settings.suggestionSelect).on('keydown', settings.suggestionLinkClass,  function (e)
			{
				// Down key = scroll down
				if (e.keyCode === 40) {
					e.preventDefault();
					var $nextSuggestion;
					// if the next suggestion is under the same parent
					if($(this).closest('li').next().length){
						$nextSuggestion = $(this).closest('li').next().find('a');
					}
					// if the next suggestion is NOT under the same parent
					else{
						$nextSuggestion = $(settings.suggestionSelect).find(settings.suggestionLinkClass).first();
					}
					setSearchFieldValueArrow($nextSuggestion);
			        $nextSuggestion.focus();
			    }
			    // Up key = scroll up
			    if (e.keyCode === 38) {
					e.preventDefault();
					setSearchFieldValueArrow($(e.target));
					var $prevSuggestion;
					// if the previous suggestion is under the same parent
					if($(this).closest('li').prev().length){
						$prevSuggestion = $(this).closest('li').prev().find('a');
					}
					// if the previous suggestion is NOT under the same parent
					else{
						$prevSuggestion = $(settings.suggestionSelect).find(settings.suggestionLinkClass).last();
					}
					setSearchFieldValueArrow($prevSuggestion);
			        $prevSuggestion.focus(); 
			    }
			    // Tab key = ignore
			    if(e.keyCode === 9){
			    	$(settings.suggestionSelect).addClass("hidden");
			    }
			    // Enter key = fill input field with the current suggestion
			    if ((e.keyCode || e.which) === 13) {
					e.preventDefault();
			        setSearchFieldValue($(e.target));
			    }
			    // Esc key
			    if (e.keyCode === 27){
			    	$(settings.suggestionSelect).addClass("hidden");
			    }

			});
		}

		$(document).on("keyup", "#" + settings.suggestionInput.attr("id"), function(e) {
			// ignore the Enter key
			if ((e.keyCode || e.which) === 13) {
				return false;
			}

			// call suggestion webservice when 3 or more characters are typed
			var text = $.trim($(this).val());
			clearSuggestionAttributes();
			if((settings.isName && text.length >= 1) || (text.length >= 3 && ACC.travelcommon.isValidLocation(text))){
				settings.autosuggestServiceHandler(text);
				if(!$(settings.suggestionSelect).hasClass('bound')){
					bindAutosuggestListeners();   // bind all the event listeners only after we get a response
				}
			}
			else{
				$(settings.suggestionSelect).addClass("hidden");
			}
		});
	    
	};
}( jQuery ));
