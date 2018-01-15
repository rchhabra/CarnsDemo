/**
 * Represents the model for Travel Accelerator.
 * @namespace
 */
 ACC.appmodel = {
	/**
     * Refer to this by {@link ACC.appmodel.trips}.
     * @namespace
     */
	trips : {
		/**
	     * Refer to this by {@link ACC.appmodel.trips.outbound}.
	     * @namespace - outbound trip
	     */
		outbound: {
			/** @type {Date} */
			dateTime: null,

			/** @type {int} */
			pricedItinerary: -1,

			/** @type {string} - The travel class e.g. "ECONOMY", "BUSINESS" */
			bundleType: null
		},
		/**
	     * Refer to this by {@link ACC.appmodel.trips.inbound}.
	     * @namespace - inbound (return) trip.
	     */
		inbound: {
			/** @type {Date} */
			dateTime: null,

			/** @type {int} */
			pricedItinerary: -1,

			/** @type {string} - The travel class e.g. "ECONOMY", "BUSINESS". */
			bundleType: null
		}
	}


};