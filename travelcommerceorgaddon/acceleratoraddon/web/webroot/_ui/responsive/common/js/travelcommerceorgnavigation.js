ACC.myCompanyNavigation = {

    _autoload: [
        "myCompanyNavigation"
    ],
    
    myCompanyNavigation: function() {
        var mainNavigation = $(".y_navbar");

        var myCompanyComponent = $('.y_myCompanyComponent');

        if(myCompanyComponent && myCompanyComponent.length === 1){
            var myCompanyButton = '<li class=\"dropdown nav-link-login\">' + myCompanyComponent[0].innerHTML + '</li>';
            if($('.y_myAccountButton') && $('.y_myAccountButton').length > 0){
                $(myCompanyButton).insertAfter($(mainNavigation.children()[1]));
            }else{
                $(myCompanyButton).insertBefore($(mainNavigation.children()[0]));
            }

        }
    }
};
