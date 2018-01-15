ACC.appmodel = {
	// model for all our trips
	trips: [
		// first trip
		{
			fromLocation: "",
			toLocation: "",
		    allFromLocations: [],
		    allToLocations: [],
		    fares: {}
		},
		// second trip
		{
			fromLocation: "",
			toLocation: "",
		    allFromLocations: [],
		    allToLocations: [],
		    fares: {}
		}
	],

	getOutboundTrip: function() {
		return ACC.appmodel.trips[0];
	},

	getInboundTrip: function() {
		return ACC.appmodel.trips[1];
	}
}