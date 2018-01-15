(function(){
  // Load the Travel Accelerator Javascript files
  function downloadJSAtOnload() {
        // List of files to import
      var javascriptFiles = [
          '../../common/js/jquery-ui-1.11.2.custom.min.js',
          '../../common/js/jquery-ui-1.11.2.min.js',
          '../../common/js/jquery.blockUI-2.66.js',
          '../../common/js/owl.carousel.custom.js',
          'jquery.ddslick.min.js',
          'jquery.autosuggest.js',
          '../../common/js/bootstrap.min.js',
          '../../common/js/affix-custom.js',
          'jquery.seat-charts.js',
          'jquery.validate.min.js',
          'acc.appmodel.js',
          'acc.travelcommon.js',
          'acc.services.js',
          'acc.ancillary.js',
          'acc.farefinder.js',
          'acc.fareselection.js',
          'acc.itinerary.js',
          'acc.reservation.js',
          'acc.farefinder.js',
          'acc.accommodationfinder.js',
          'acc.travelfinder.js',
          'acc.accommodationselection.js',
          'acc.langcurrencyselector.js',
          'acc.managebookings.js',
          'acc.myaccount.js',
          'acc.mycompany.js',
          'acc.payment.js',
          'acc.travellerdetails.js',
          'acc.transportofferingstatus.js',
          'acc.hotelgallery.js',
          'acc.accommodationdetails.js',
          'acc.dealselection.js',
          '_autoloadtracc.js'
      ];

        // Get the URL path from the current Javascript file
        var scripts = document.getElementsByTagName("script"),
            srcCurrent = scripts[scripts.length-1].src,
            pathCurrent = srcCurrent.substring(0, srcCurrent.lastIndexOf('/') + 1);

        // Load all the files
        javascriptFiles.forEach(function(src) {
          var script = document.createElement('script');
          script.src = pathCurrent+src;
          script.async = false;
          document.body.appendChild(script);
        });
    }
    // run the above function after page load
    if (window.addEventListener)
        window.addEventListener("load", downloadJSAtOnload, false);
    else if (window.attachEvent)
        window.attachEvent("onload", downloadJSAtOnload);
    else window.onload = downloadJSAtOnload;
})()