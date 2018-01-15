ACC.accommodationdetails = {

	_autoloadTracc : [ 
		"bindAmenitiesList",
        "bindFeaturesMoreLess"
	],

    bindAmenitiesList : function() {

        var amenities = '.y_amenities-list';

        $('a', amenities).on( "click", function(e) {
            e.preventDefault();
            $(this).next('ul').slideToggle('fast', function(){
                if($('.amenities-items li > ul:hidden').length == 0) {
                    $('.collapse-all').removeClass('hidden');
                    $('.show-all').addClass('hidden');
                }
            });
            
        });

        $('.show-all', amenities).on('click', function(e) {
            e.preventDefault();
            $('.amenities-items li > ul').slideDown('fast');
            $(this).addClass('hidden');
            $('.collapse-all').removeClass('hidden');
        });

        $('.collapse-all', amenities).on('click', function(e) {
            e.preventDefault();
            $('.amenities-items li > ul').slideUp('fast');
            $(this).addClass('hidden');
            $('.show-all').removeClass('hidden');
        });
        
    },

    bindFeaturesMoreLess : function() {

        var features = '.y_features',
        slice = 3,
        splitList = $('li', features).slice(slice),
        more = $('.more'),
        less = $('.less');

        splitList.hide();

        if($('.y_features li').length > slice){
            more.removeClass('hidden');
            more.on('click', function(e) {
                e.preventDefault();
                splitList.slideDown('fast');
                $(this).addClass('hidden');
                less.removeClass('hidden');
            });
        }

        less.on('click', function(e) {
            e.preventDefault();
            splitList.slideUp('fast');
            $(this).addClass('hidden');
            more.removeClass('hidden');
        });
        
    }

}
