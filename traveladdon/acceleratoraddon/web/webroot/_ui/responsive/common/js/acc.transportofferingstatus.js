ACC.transportofferingstatus = {
		
	_autoloadTracc : [
	    "bindTransportOfferingStatusForm",
	    "bindTransportOfferingSearchForm",
	    "bindTransportOfferingSearchFormVisibility",
		"enableCheckOtherFlights",
		"enableFlightSearchButton"
	],
	
	componentParentSelector: '#y_nexttransportOfferingStatus',
	
	//	trip status form on Transport Offering Status page
	bindTransportOfferingStatusForm: function(){
		var yesterdayDate = new Date();
		yesterdayDate.setDate(yesterdayDate.getDate() - 1);
		
		$("#y_transportOfferingStatusForm").submit(function(e) {
		    e.preventDefault();
		}).validate({
            errorElement: "span",
            errorClass: "fe-error",
            onfocusout: function(element) { $(element).valid(); },
            submitHandler: function() {
            	$.when(ACC.transportofferingstatus.refreshResultTable()).then(function(){
                    var number = $("input[name='transportOfferingNumber']").val();
                    var departureDate = $("input[name='departureDate']").val();
                    if(number && departureDate){
                        ACC.travelcommon.changeUrlParams(["transportOfferingNumber", "departureDate"], [number, departureDate]);
                    }
				});
            },            
            rules: {
            	transportOfferingNumber: "required",
                departureDate:{
                	required: true,
                	dateUK: true,
                	dateGreaterEqualTo: ACC.travelcommon.convertToUKDate(yesterdayDate)
                }
            },
            messages: {
            	transportOfferingNumber: ACC.addons.travelacceleratorstorefront['error.transportofferingstatus.transport.offering.number'],
            }
        });
        $(".y_transportOfferingStatusDepartureDate").datepicker({
        	minDate: '-1d',
        	onClose : function(selectedDate) {
        		$(this).valid();
        	}
        });
	},

	refreshResultTable: function() {
		var $form = $("#y_transportOfferingStatusForm");
    	ACC.formvalidation.serverFormValidation($form, "refreshTransportOfferingStatusResults", function(jsonData){
            if(!jsonData.hasErrorFlag){
                $("#y_transportOfferingStatusErrors").html("");
                $("#y_transportOfferingStatusResultTable").html(jsonData.htmlContent);
            }
		});
	},
	
	//	trip status form on any page (other than the Transport Offering Status form)
	bindTransportOfferingSearchForm : function() {
		var yesterdayDate = new Date();
		yesterdayDate.setDate(yesterdayDate.getDate() - 1);
		
        $("#y_transportOfferingStatusSearchForm").validate({
            errorElement: "span",
            errorClass: "fe-error",
            onfocusout: function(element) { $(element).valid(); },
            submitHandler: function(form) {
            	ACC.formvalidation.serverFormValidation(form, "validateTransportOfferingStatusForm", function(jsonData){
                    if(!jsonData.hasErrorFlag){
                        form.submit();
                    }
				});
            },
            rules: {
            	transportOfferingNumber: "required",
                departureDate:{
                	required: true,
                	dateUK: true,
                	dateGreaterEqualTo: ACC.travelcommon.convertToUKDate(yesterdayDate)
                }
            },
            messages: {
            	transportOfferingNumber: ACC.addons.travelacceleratorstorefront['error.transportofferingstatus.transport.offering.number'],
                departureDate: ACC.addons.travelacceleratorstorefront['error.transportofferingstatus.transport.departure.date']
            }
        });

        $('.y_transportOfferingStatusDepartureDate').focusout(function () {
            $(this).rules('add', {
                messages: {
                required: ACC.addons.travelacceleratorstorefront['error.transportofferingstatus.transport.departure.date']
            }
        });      
    });

	},

	bindTransportOfferingSearchFormVisibility: function(){
		$(".y_transportOfferingStatusSearchTrigger").click(function(e) {
			e.preventDefault();
			$(this).hide();
			
			 $.ajax({
			        url : ACC.config.contextPath + "/view/TransportOfferingStatusSearchComponentController/get-transport-offering-status-search-form",
			        type: 'GET',
					data : {
						componentUid : $("#y_transportOfferingStatusSearchComponentId").val()
					},			        
			        success: function(result){
			        	var parentDiv=$(ACC.transportofferingstatus.componentParentSelector).parent();
			        	$(ACC.transportofferingstatus.componentParentSelector).remove();
						parentDiv.append(result);
						ACC.transportofferingstatus.bindTransportOfferingStatusForm();
						ACC.transportofferingstatus.bindTransportOfferingSearchForm();
						$("#flight-status").addClass("in");
						$(".y_flightStatusPanelHeaderLink").removeClass("collapsed");
			        }
			    });
		});
	},

    enableCheckOtherFlights: function () {
        $(".y_checkOtherFlights").removeAttr("disabled");
    },
	
    enableFlightSearchButton : function(){
        $(document).ready(function() {
    		if(!$.isEmptyObject($(".y_flightStatusSearchBtn")))
    		{
    			$(".y_flightStatusSearchBtn").prop("disabled", false);
    		}
    	});
    }

};
