ACC.services = {
    
    spinner: $("<img src='" + ACC.config.commonResourcePath + "/images/spinner.gif' />"),
  

    // make calls to web services

    getAllFromLocations: function() {
    	// call the web service to get all 'from locations' and return it as an array
    	return $.ajax({
    			beforeSend: function ()
				{
    				$('body').block({ message: ACC.services.spinner });
				},
    			dataType: "json",
                url: "assets/js/departure-locations.json",
				complete: function ()
				{
					$('body').unblock();
				}
            });
    },

    getAllToLocations: function(fromLocation) {
    	// call the web service to get all 'to locations' and return it as an array
    	return $.ajax({
    			beforeSend: function ()
				{
    				$('body').block({ message: ACC.services.spinner });
				},
    			dataType: "json",
                url: "assets/js/departure-locations.json",	// temporarily using the same data as 'from locations'
				complete: function ()
				{
					$('body').unblock();
				}
            });
    },

    getSeatChartMap: function() {
    	// call web service for the seat chart
        return $.ajax({
                beforeSend: function ()
                {
                    $('body').block({ message: ACC.services.spinner });
                },
                dataType: "json",
                url: "assets/js/seat-chart.json",  // temporarily using the same data as 'from locations'
                complete: function ()
                {
                    $('body').unblock();
                }
            });
    }


}