ACC.myaccount = {

     _autoloadTracc : [ 
        "init",
        "bindAddPassengerButton",
        "bindEditPassengerButton",
        "bindRemovePassengerButton",
        "bindCollapseOutboundInbound"
    ],
    init : function() {
        $('.y_checkinApDob').datepicker({ 
            changeMonth: true,
            changeYear: true,
            yearRange: '1900:2016'
        });  
        $('.y_checkinApDocExpiry').datepicker();  
        $('.y_myAccountDob').datepicker();
    },

    bindAddPassengerButton : function() {
        // Toggle class to the parent of the selected/checked radio button
        // Add new baggage item
        $(".y_myAccountAddPassenger").on('click', function() {
            var $addedElem = $("#y_myAccountAddPassengerWrapper"),
                targetName = $(this).attr('data-target'),
                $targetElem = $('.'+targetName),
                num     = parseInt($targetElem.last().attr('id').match(/\d+$/)), // index of the greatest "duplicatable" input fields
                newNum  = new Number(num + 1),      // the numeric ID of the new input field being added
                $newElem = $('#'+targetName + num).clone().attr('id', targetName + newNum).fadeIn('slow'), // create the new element via clone(), and manipulate it's ID using newNum value
                namePrefix = "";

            // clone the bottom element
            if($targetElem.is(":visible")==true){
                // increment the number on ID's, classes, etc... for the new HTML block
                $newElem.find('#'+targetName + num).attr('id', '#'+targetName + newNum);
                

                $newElem.find("select").each(function (index, input) {
                    input.id = input.id.replace(/\d+$/, '') + newNum;
                    input.name = input.name.replace(/\d+$/, '') + newNum;
                    input.setAttribute('disabled','disabled');
                    input.selectedIndex = $addedElem.find("select option:selected").index();
                });
                $newElem.find("input").each(function (index, input) {
                    input.id = input.id.replace(/\d+$/, '') + newNum;
                    namePrefix = input.name.replace(/\d+$/, '');
                    input.setAttribute('disabled','disabled');
                    input.name = namePrefix + newNum;
                    
                    // set the input to the default value
                    input.value = $addedElem.find("#"+namePrefix+"add").val();
                });
                
                $newElem.find("button").each(function (index, input) {
                    input.setAttribute('data-target', input.getAttribute('data-target').replace(/\d+$/, '')+ newNum);
                    input.innerHTML='Edit';
                });
                $newElem.find(".y_myAccountDeleteBlock").each(function (index, input) {
                    input.setAttribute('data-target', input.getAttribute('data-target').replace(/\d+$/, '')+ newNum);
                });


                $('#'+targetName + num).after($newElem);
            }
            // if there is nothing left to clone, we just show the hidden element
            else{
                $targetElem.fadeIn('slow');

                $targetElem.find("select").each(function (index, input) {
                    input.removeAttribute('disabled');
                });
                $targetElem.find("input").each(function (index, input) {
                    input.removeAttribute('value');
                });

            }

        });
    },
     bindEditPassengerButton : function() {
        // Toggle class to the parent of the selected/checked radio button
        // Add new baggage item
        $(document).on('click', '.y_myAccountEditPassenger', function(e) {
            e.preventDefault();
            var targetName = $(this).attr('data-target'),
                $targetElem = $('#'+targetName);

            // edit mode
            if($(this).html()==='Edit'){
                $targetElem.find('select').removeAttr('disabled');
                $targetElem.find('input').removeAttr('disabled');
                $(this).html("Save");
            }
            // save mode
            else{
                $targetElem.find('select').attr('disabled','disabled');
                $targetElem.find('input').attr('disabled','disabled');
                $(this).html("Edit");
            }
            
        });
    },
    bindRemovePassengerButton : function() {
        // Delete baggage item
        // We need to bind it to document because the delete block can be added dynamically
        $(document).on('click','.y_myAccountDeleteBlock', function () {
            if ($(':animated').length) {
                return false;   /* don't respond to clicks until animation completed */
            }

            var targetName = $(this).attr('data-target'),
                $targetElem = $('#'+targetName ),
                targetGroup = targetName.replace(/\d+$/, ""),
                num = $('.'+targetGroup).length;

            if(num > 1){
                $targetElem.fadeOut('slow', function() {$(this).remove() }); 
            }
            // if it's the last item, we just hide the block
            else{
                $targetElem.fadeOut('slow', function() {
                   $targetElem.find("select").each(function (index, input) {
                        input.disabled = true;
                        // set the select to the default value
                        input.selectedIndex = 0;
                    });
                    $targetElem.find("input").each(function (index, input) {
                        input.disabled = true;
                        // set the input to the default value
                        input.value = input.getAttribute('data-defaultvalue');
                    });
                });
            }

        });
    },
    bindCollapseOutboundInbound : function() {
        // Outbound
        $("button[data-target='#outbound-section']").on('click', function() {
            if ($(this).hasClass('collapsed')) {
                $('#passenger-summary-section-1').collapse('show');
            } else {
                $('#passenger-summary-section-1').collapse('hide');
            }
        })

        // Inbound
        $("button[data-target='#inbound-section']").on('click', function() {
            if ($(this).hasClass('collapsed')) {
                $('#passenger-summary-section-2').collapse('show');
            } else {
                $('#passenger-summary-section-2').collapse('hide');
            }
        })
    }
    
}