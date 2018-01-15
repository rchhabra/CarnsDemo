// Autosuggestion plugin for the travel accelerator
(function ( $ ) {
	$.fn.autosuggestion = function(){

		// default options.
        var settings = $.extend({
            suggestionInput: this.selector,				// the input field
			suggestionCode: this.selector+"Code",
			suggestionSelect: "#"+(this.selector).substring(1)+"Suggestions",	// the suggestions box
			suggestionLinkClass: ".autocomplete-suggestion"
        });

	    function setSearchFieldValue($_target) {
			$(settings.suggestionInput).val($_target.text());
    		$(settings.suggestionCode).val($_target.data("value"));
    		$(settings.suggestionSelect).addClass("hidden");
    		$(settings.suggestionInput)[0].focus();
		}

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
			        && $(settings.suggestionSelect).has($_target).length === 0) // ... nor a descendant of the $(settings.suggestionSelect)
			    {
			        $(settings.suggestionSelect).addClass("hidden");
			    }
			}
		});

		/** Autocomplete keyboard functionality **/
		// pressing down on the input field goes into the suggestions
		$(settings.suggestionInput).on('keydown', function (e)
		{
			// Down key = scroll down
			if (e.keyCode == 40) {
				e.preventDefault();
				$(settings.suggestionSelect).find(settings.suggestionLinkClass).first().focus();
		    }
			// Tab key = ignore
		    if(e.keyCode == 9){
		    	$(settings.suggestionSelect).addClass("hidden");
		    }
		});
		// pressing up or down on the suggestions scrolls through them
		$(settings.suggestionSelect).on('keydown', settings.suggestionLinkClass,  function (e)
		{
			// Down key = scroll down
			if (e.keyCode == 40) {
				e.preventDefault();
		        $(this).closest('li').next().find('a').focus();
		    }
		    // Up key = scroll up
		    if (e.keyCode == 38) { 
				e.preventDefault();
		        $(this).closest('li').prev().find('a').focus(); 
		    }
		    // Tab key = ignore
		    if(e.keyCode == 9){
		    	e.preventDefault();
		    }
		    // Enter key = fill input field with the current suggestion
		    if ((e.keyCode || e.which) == 13) {
				e.preventDefault();
		        setSearchFieldValue($(e.target));
		    }

		});

		$(document).on("keyup", settings.suggestionInput, function(e) {
			if ((e.keyCode || e.which) == 13) {
				return false;
			}
			var originText = $(this).val();

			if(originText.length >= 3) {
				// make AJAX call
				$(settings.suggestionSelect).removeClass("hidden");
			}
			else{
				$(settings.suggestionSelect).addClass("hidden");
			}
		});
	    
	};
}( jQuery ));