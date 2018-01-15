ACC.tabbedfindercomponent = {

	_autoloadTracc : [ 
	      "bindTabActions",
	      "loadFinder"
	],
	
	actions : {
			"flight" : ACC.config.contextPath + "/view/FareFinderComponentController/load", 
			"hotel" : ACC.config.contextPath + "/view/AccommodationFinderComponentController/load",
			"flight-hotel" : ACC.config.contextPath + "/view/TravelFinderComponentController/load",
			"package" : ACC.config.contextPath + "/view/PackageFinderComponentController/load"
	},
	
	loadFinder: function(){
		if ( $('#y_finderContainer').is('div'))
	    {
			context=ACC.tabbedfindercomponent.actions["flight"];
			ACC.tabbedfindercomponent.refreshTravelFinderComponent(context,"FareFinderComponent");
	    }
  },

  bindTabActions: function(){
	  $(".componentSelector").click(function(){
	      var transition;
	      if($(this).parents().hasClass("active")){
			  $(this).parents().removeClass("active");
			  $(".panel-modify").slideToggle();
              return false;
	      }
	      if($(".panel-modify").is(":visible")){
	          transition = false;
	      }else{
	          transition = true;
	      }
		  var context=ACC.tabbedfindercomponent.actions[$(this).attr('aria-controls')];
          var selectedComponent = $(this).attr("component-uid");
		  $.when(ACC.tabbedfindercomponent.refreshTravelFinderComponent(context,selectedComponent)).then(function() {
              if(transition){
                  $(".panel-modify").slideToggle();
              }else{
                  $(".panel-modify").toggle()
              }
		  });
	  })
  },
  
  refreshTravelFinderComponent: function(context, selectedComponent){
	  if(selectedComponent) {
		  $.when(ACC.services.refreshTravelFinderComponent(context, selectedComponent)).then(
				  function(response){
					  $('#y_finderContainer').html(response);
					  ACC.tabbedfindercomponent.loadJs();
					  if(selectedComponent=='PackageFinderComponent' ||selectedComponent=='TravelFinderComponent'){
						  ACC.farefinder.minRelativeReturnDate=1;
					  }else{
						  ACC.farefinder.minRelativeReturnDate=0;
					  }
				  }
		  );
	  }
  },
  
  loadJs: function(){
	  ACC.farefinder.init();
	  ACC.accommodationfinder.init();
	  ACC.travelfinder.init();
	  ACC.travelfinder.initializeDatePicker();
	  ACC.travelcommon.bindBootstrapPopover();
  }
  	
};
