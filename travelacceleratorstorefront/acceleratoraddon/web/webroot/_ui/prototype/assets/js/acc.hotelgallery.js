ACC.hotelgallery = {

	_autoloadTracc : [ 
		"init",
		"bindThumbnailClick"
	],

	mainCarousel: ".main-image-carousel",
	thumbnailCarousel: ".thumbnail-carousel",
	mainCarouselPanel: ".panel-accommodation .main-image-carousel",
	thumbnailCarouselPanel: ".panel-accommodation .thumbnail-carousel",

	init : function() {

		// main page carousel, for multiple carousels on the same page initialise 
		// using .eq() method eg .eq(0) & .eq(1) if two carousels exist on page so carousel 
		// controls are independent of eachother.

		$(ACC.hotelgallery.mainCarouselPanel).eq(0).owlCarousel({
	        singleItem : true,
	        slideSpeed : 1000,
	        navigation: true,
	        pagination:false,
	        afterAction : ACC.hotelgallery.syncPosition,
	        responsiveRefreshRate : 200,
	      });

  		$(ACC.hotelgallery.thumbnailCarouselPanel).eq(0).owlCarousel({
	        items : 10,
	        itemsDesktop      : [1199,8],
	        itemsDesktopSmall : [979,8],
	        itemsTablet       : [768,6],
	        itemsMobile       : [479,4],
	        navigation: true,
	        pagination:false,
	        responsiveRefreshRate : 100,
	        afterInit : function(el){
	          el.find(".owl-item", $(this)).eq(0).addClass("synced");
	        }
	      });

  		// all modal carousels

		  $('.modal').on('shown.bs.modal', function (e) {
  			$this = $(this);
  			$('.owl-carousel.thumbnail-carousel', $this).addClass('modalThumbnails');

  			$('.owl-carousel.main-image-carousel', $this).owlCarousel({
		        singleItem : true,
		        slideSpeed : 1000,
		        navigation: true,
		        pagination:false,
		        afterAction : ACC.hotelgallery.syncPositionModal,
		        responsiveRefreshRate : 200,
		      });

  			$('.owl-carousel.thumbnail-carousel', $this).owlCarousel({
		        items : 10,
		        itemsDesktop      : [1199,8],
		        itemsDesktopSmall : [979,8],
		        itemsTablet       : [768,6],
		        itemsMobile       : [479,4],
		        navigation: true,
		        pagination:false,
		        rewindNav:false,
		        responsiveRefreshRate : 100,
		        afterInit : function(el){
		          el.find(".owl-item", $(this)).eq(0).addClass("synced");
		        }
		      });
		});

		$('.modal').on('hide.bs.modal', function (e) {
  			$this = $(this);
  			$('.owl-carousel.thumbnail-carousel', $this).removeClass('modalThumbnails');
		});
  
	},
   
	syncPosition: function(el){
		var current = this.currentItem;

		$(ACC.hotelgallery.thumbnailCarouselPanel)
		  .find(".owl-item", $(this))
		  .removeClass("synced")
		  .eq(current)
		  .addClass("synced");

		if($(this).data("owlCarousel") !== undefined){
		  ACC.hotelgallery.centerThumbnails(current)
		}
	},

	syncPositionModal: function(el){
		var current = this.currentItem;

		$('.modalThumbnails')
		  .find(".owl-item", $(this))
		  .removeClass("synced")
		  .eq(current)
		  .addClass("synced");
		if($(this).data("owlCarousel") !== undefined){
		  ACC.hotelgallery.centerThumbnails(current)
		}
	},

	centerThumbnails: function(number){
		var sync2visible = $(ACC.hotelgallery.thumbnailCarousel).eq(0).data("owlCarousel").owl.visibleItems;
		var sync2visibleModal = $('.modalThumbnails').data("owlCarousel").owl.visibleItems;
		var num = number;
		var found = false;

		for(var i in sync2visible){
		  if(num === sync2visible[i]){
		    var found = true;
		  }
		}

		if(found===false){
		  if(num>sync2visible[sync2visible.length-1]){
		    $(this).trigger("owl.goTo", num - sync2visible.length+2)
		  }else{
		    if(num - 1 === -1){
		      num = 0;
		    }
		    $(this).trigger("owl.goTo", num);
		  }
		} else if(num === sync2visible[sync2visible.length-1]){
		  $(this).trigger("owl.goTo", sync2visible[1])
		} else if(num === sync2visible[0]){
		  $(this).trigger("owl.goTo", num-1)
		}

		// thumbnails within a modal

		for(var i in sync2visibleModal){
		  if(num === sync2visibleModal[i]){
		    var found = true;
		  }
		}

		if(found===false){
		  if(num>sync2visibleModal[sync2visibleModal.length-1]){
		    $(this).trigger("owl.goTo", num - sync2visibleModal.length+2)
		  }else{
		    if(num - 1 === -1){
		      num = 0;
		    }
		    $(this).trigger("owl.goTo", num);
		  }
		} else if(num === sync2visibleModal[sync2visibleModal.length-1]){
		  $(this).trigger("owl.goTo", sync2visibleModal[1])
		} else if(num === sync2visibleModal[0]){
		  $(this).trigger("owl.goTo", num-1)
		}
		
	},

	bindThumbnailClick: function() {

		// main page thumbnails, if multiple carousels exist we need to make the
		// thumbnail selection independent any other thumbnails by init the thumbnails when the modal is shown
		
		$(ACC.hotelgallery.thumbnailCarousel).eq(0).each(function(){
			$(".owl-item", $(this)).on("click", function(e){
		        e.preventDefault();
		        var number = $(this).data("owlItem");
		        $(this).parents('.thumbnail-carousel-wrapper').siblings().trigger("owl.goTo",number);
		    });
		});

		$('.modal').on('shown.bs.modal', function (e) {
			$(".modalThumbnails").each(function(){
				$(".owl-item", $(this)).on("click", function(e){
			        e.preventDefault();
			        var number = $(this).data("owlItem");
			        $(this).parents('.thumbnail-carousel-wrapper').siblings().trigger("owl.goTo",number);
			    });
			});
		});

	}
}
