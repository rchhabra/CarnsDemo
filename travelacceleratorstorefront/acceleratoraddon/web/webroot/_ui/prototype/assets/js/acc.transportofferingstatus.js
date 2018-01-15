ACC.transportofferingstatus = {
		
	_autoloadTracc : [
		"init",
	    "bindTransportOfferingStatus",
	    "bindTransportOfferingStatusValidation"
	],
	
	init: function(){
		// initialise date picker plugin
		$('#departureDate').datepicker();
	},

	bindTransportOfferingStatus: function(){
		$(document).on("submit", "#transportOfferingStatusForm-ResultPage", function(event){
			event.preventDefault();
			var number = $("input[name='transportOfferingNumber']").val();
			var departureDate = $("input[name='departureDate']").val();
			ACC.transportofferingstatus.refreshResultTable();
			if(number && departureDate){
				ACC.travelcommon.changeUrlParams(["number", "departureDate"], [number, departureDate]);
			}
		});
	},

	refreshResultTable: function() {
		var form = $("#transportOfferingStatusForm-ResultPage").serialize();
		$.ajax({
			url: $("#transportOfferingStatusForm-ResultPage").attr("action"),
			type : "GET",
			contentType: "application/json; charset=utf-8",
			async : false,
			data: form,
			success : function(data) {
				var jsonData = JSON.parse(data);
				$("#flight-status-search").html(jsonData.htmlContent);
			}
		});
	},

	bindTransportOfferingStatusValidation: function() {
		$(".y_formFlightStatus").validate({
            errorElement: "span",
            errorClass: "fe-error",
            submitHandler: function() { alert("Valid form submitted!") },
            onfocusout: function(element) { $(element).valid(); },
            rules: {
                flightNumber: "required",
                departureDate: "required"
            },
            messages: {
                flightNumber: "Please enter your flight number.",
                departureDate: "Please enter your departure date."
            }
        });
	}
		
}