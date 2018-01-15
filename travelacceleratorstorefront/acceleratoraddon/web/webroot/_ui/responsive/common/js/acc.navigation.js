/**
 * The module for navigation elements.
 * @namespace
 */
 ACC.navigation = {
		
	_autoloadTracc : [ 
		"createMobileAccountNav"
	],
	
    /**
     * generate code for the My Account navigation bar on mobile
     */
	createMobileAccountNav: function(){

            // If there is no navigation bar, create a new list
			if($(".y_navbar") === undefined || $(".y_navbar").length === 0){
				var navigationList = '<ul class=\"y_navbar nav navbar-nav\"></ul>';
				$("#navbar-collapse").html(navigationList);
			}
		
			var mainNavigation = $(".y_navbar");

            // create Welcome User + expand/collapse
            var oUserInfo = $(".y_loggedIn");
            if(oUserInfo && oUserInfo.length === 1){
                var sUserBtn = '<li class=\"nav-link-login greeting\"><div class=\"userGroup\">';
                sUserBtn += '<div class=\"userName\">' + oUserInfo[0].innerHTML + '</div>';
                sUserBtn += '</div></li>';
                if($(mainNavigation.children()).length === 0){
                	$(mainNavigation).html(sUserBtn);
                }else{
                	$(sUserBtn).insertBefore($(mainNavigation.children()[0]));
                }
            }
            
            // create My Account link
            var myAccountLink = $('.y_myAccountLink');
            if(myAccountLink && myAccountLink.length === 1){
            	var myAccButton = '<li class=\"nav-link-login y_myAccountButton\">' + myAccountLink[0].innerHTML + '</li>';
            	$(myAccButton).insertAfter($(mainNavigation.children()[0]));
            }

            // create Sign In/Sign Out Button
            if($(".y_signButton a") && $(".y_signButton a").length > 0){
                var sSignBtn = '<li class=\"nav-link-login\" ><a class=\"userSign\" href=\"' + $(".y_signButton a")[0].href + '\">' + $(".y_signButton a")[0].innerHTML + '</a></li>';
                if(oUserInfo && oUserInfo.length === 1){
                    $(sSignBtn).insertAfter($(mainNavigation.children()[mainNavigation.children().length-1]));
                } else {
                	if($(mainNavigation.children()).length === 0){
                		$(mainNavigation).html(sSignBtn);
                	}else{
                		$(sSignBtn).insertBefore($(mainNavigation.children()[0]));
                	}
                }
            }
        }
};
