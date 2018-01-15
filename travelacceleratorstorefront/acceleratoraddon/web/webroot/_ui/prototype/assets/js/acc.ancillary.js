ACC.ancillary = {

    config: {
        firstSeatLabelDefault: 1
    },

    passengers: [],
    seatChartMain: {},
    currentTab: 0,


    _autoloadTracc : [ 
        [ "init", $(".y_ancillarySection").length != 0 ],
        [ "bindPassengerRadioChange", $(".y_ancillarySection").length != 0 ],
        [ "bindAddLuggageButton", $(".y_ancillarySection").length != 0 ],
        [ "bindRemoveLuggageButton", $(".y_ancillarySection").length != 0 ],
        "bindMultiSelect"
    ],

    init : function() {

            var firstSeatLabel = 1,
                seatChartTemp;

            // initialise passengers
            ACC.ancillary.passengers = [
                {seatNum: ""},
                {seatNum: ""},
                {seatNum: ""}
            ];
            // initialise the seat chart in the first tab
            ACC.ancillary.seatChartMain = $('#y_ancillarySeatMap0').seatCharts({
                map: [
                    // "aaa_aaa",
                    // "aaa_aaa",
                    // "aaa_aaa",
                    // "ww___ww",
                    // "bbb_bbb",
                    // "bbb_bbb",
                    // "bbb_bbb",
                    // "bbb_bbb",
                    // "_______",
                    // "ccc_ccc",
                    // "ccc_ccc",
                    // "ccc_ccc",
                    // "ccc_ccc",
                    // "ccc_ccc",
                    // "ccc_ccc",
                    // "ccc_ccc"
                "aaa_aaaa_aaa",
                "aaa_aaaa_aaa",
                "aaa_aaaa_aaa",
                "ww__aaaa__ww",
                "____________",
                "bbb_bbbb_bbb",
                "bbb_bbbb_bbb",
                "bbb_bbbb_bbb",
                "bbb_bbbb_bbb",
                "____________",
                "ccc_cccc_ccc",
                "ccc_cccc_ccc",
                "ccc_cccc_ccc",
                "ccc_cccc_ccc",
                "ccc_cccc_ccc",
                "ccc_cccc_ccc",
                "ccc_cccc_ccc"
                ],
                seats: {
                            a: {
                                price   : 50,
                                classes : 'economy-lower-deck-class', //your custom CSS class
                                category: 'Economy Class'
                            },
                            b: {
                                price   : 100,
                                classes : 'economy-plus-lower-deck-class', //your custom CSS class
                                category: 'Economy Plus Class'
                            },
                            c: {
                                price   : 200,
                                classes : 'business-lower-deckclass', //your custom CSS class
                                category: 'Business Class'
                            }

                        },
                naming : {
                    top : true,
                    getLabel : function (character, row, column) {
                        return "";
                    },
                    // columns: ['A', 'B', 'C', ' ', 'D', 'E', 'F'],
                    // rows: ['1', '2', '3', ' ', '4', '5', '6', '7', ' ', '8', '9', '10', '11', '12', '13', '14'],
                    columns: ['A', 'B', 'C', ' ', 'D', 'E', 'F', 'G', ' ', 'H', 'I', 'J'],
                    rows: ['1', '2', '3', ' 4', ' ', '5', '6', '7', '8', ' ', '9', '10', '11', '12', '13', '14', '15']
                },
                legend : {
                    node : $('#y_ancillarySeatMapLegend0'),
                    items : [
                        [ 'u', 'unavailable', 'Already Booked'],
                        [ 's', 'unavailable-curr', 'Currently Selected'],
                        [ 'w', 'seatCharts-toilet', 'Toilet'],
                        [ 'a', 'available',   'Business Class' ],
                        [ 'b', 'available',   'Economy Plus Class'],
                        [ 'c', 'available',   'Economy Class']
                    ]
                },
                click: this.handleSeatClick
            });

            // initialise the seat chart in the second tab
            firstSeatLabel = 1;
            seatChartTemp = $('#y_ancillarySeatMap1').seatCharts({
                map: [
                    "bbb_bbb",
                    "bbb_bbb",
                    "bbb_bbb",
                    "bbb_bbb",
                    "_______",
                    "ccc_ccc",
                    "ccc_ccc",
                    "ccc_ccc",
                    "ccc_ccc",
                    "ccc_ccc",
                    "ccc_ccc",
                    "ccc_ccc"
                ],
                seats: {
                            a: {
                                price   : 50,
                                classes : 'economy-class', //your custom CSS class
                                category: 'Economy Class'
                            },
                            b: {
                                price   : 100,
                                classes : 'economy-plus-class', //your custom CSS class
                                category: 'Economy Plus Class'
                            },
                            c: {
                                price   : 200,
                                classes : 'business-class', //your custom CSS class
                                category: 'Business Class'
                            }

                        },
                naming : {
                    top : true,
                    getLabel : function (character, row, column) {
                        return firstSeatLabel++;
                    },
                    columns: ['A', 'B', 'C', ' ', 'D', 'E', 'F'],
                    rows: ['1', '2', '3', ' ', '4', '5', '6', '7', ' ', '8', '9', '10', '11', '12', '13', '14'],
                },
                legend : {
                    node : $('#y_ancillarySeatMapLegend1'),
                    items : [
                        [ 'u', 'unavailable', 'Already Booked'],
                        [ 's', 'unavailable-curr', 'Currently Selected'],
                        [ 'w', 'seatCharts-toilet', 'Toilet'],
                        [ 'a', 'available',   'Business Class' ],
                        [ 'b', 'available',   'Economy Plus Class'],
                        [ 'c', 'available',   'Economy Class']
                    ]
                },
                click: ACC.ancillary.handleSeatClick
            });

            // initialise seat availability
            //let's pretend some seats have already been booked
            ACC.ancillary.seatChartMain.get(['1_A', '1_B', '1_C', '7_A']).status('unavailable');

             $("#y_ancillaryPanelSeatingOptions .nav-tabs > li > a").on('click', function() {

                ACC.ancillary.passengers = [
                    {seatNum: ""},
                    {seatNum: ""},
                    {seatNum: ""}
                ];
                $("label .passenger-selected-seat").html("__");

                ACC.ancillary.currentTab = $(this).attr("href").match(/\d+$/);
                ACC.ancillary.seatChartMain = $('#y_ancillarySeatMap'+ACC.ancillary.currentTab).seatCharts({});
             });

    },

    handleSeatClick : function() {
        // get the number for the currently selected passenger
        var $radioButtons = $("#seating-tab-" + ACC.ancillary.currentTab + " input.y_ancillaryPassengerSeatRadio");
        var $radioButtonCurrent = $radioButtons.filter(':checked');
        var passengerNum = $radioButtons.index($radioButtonCurrent);

        if (this.status() == 'available') {
            // make previously selected seat available
            if(ACC.ancillary.passengers[passengerNum].seatNum!=""){
                ACC.ancillary.seatChartMain.status(ACC.ancillary.passengers[passengerNum].seatNum, 'available'); 
            }

            // set the passenger's seat to the newly selected seat
            ACC.ancillary.passengers[passengerNum].seatNum = this.settings.id;
            $("label[for='"+$radioButtonCurrent.attr('id')+"'] .passenger-selected-seat").html(this.settings.id);

            return 'selected';
        } else if (this.status() == 'selected') {

            //remove the item from our cart
            $('#cart-item-'+this.settings.id).remove();
            // deselect the passenger's seat
            ACC.ancillary.passengers[passengerNum].seatNum="";
            $("label[for='"+$radioButtonCurrent.attr('id')+"'] .passenger-selected-seat").html("__");
        
            //seat has been vacated
            return 'available';
        } else if (this.status() == 'unavailable') {
            //seat has been already booked
            return 'unavailable';
        } else {
            return this.style();
        }
    },

    bindMultiSelect : function() {
        $('.multi-select').SumoSelect({placeholder: 'Add', selectAll: 'true'});
    },

    bindPassengerRadioChange : function() {
      //when you change the currently selected passenger
        $("#y_ancillaryPanelSeatingOptions").on("change", "input.y_ancillaryPassengerSeatRadio", function () {
            // make all currently selected seats have an "unavailable" state
            if(ACC.ancillary.seatChartMain.find('selected').seatIds[0]){
                ACC.ancillary.seatChartMain.status(ACC.ancillary.seatChartMain.find('selected').seatIds[0], 'unavailable-curr');
            }

            // get the number for the currently selected passenger
            var $radioButtons = $("#seating-tab-" + ACC.ancillary.currentTab + " input.y_ancillaryPassengerSeatRadio");
            var passengerNum = $radioButtons.index($(this));
            // make current passenger's seat selected
            if(ACC.ancillary.passengers[passengerNum].seatNum!=""){
                ACC.ancillary.seatChartMain.status(ACC.ancillary.passengers[passengerNum].seatNum, 'selected'); 
            }

            // remove styling classes from all blocks, and the add it only for the selected block
            $("#seating-tab-" + ACC.ancillary.currentTab + ' input.y_ancillaryPassengerSeatRadio').parent().removeClass('current-passenger-select');  // using a CSS class without 'y_' because there is styling associated with the selected passenger
            $(this).parent().addClass('current-passenger-select'); 
        });
    },
    bindAddLuggageButton : function() {
        // Toggle class to the parent of the selected/checked radio button
        // Add new baggage item
        $(".y_ancillaryFormAddBlock").on('click keydown', function(e) {
            if ($(':animated').length) {
                return false; // don't respond to clicks until
                // animation completed
            }
            // handle the keypress
            if(e.type==="keydown"){
                var keyCode = e.keyCode || e.which; 
                // if it's not the Enter key or Spacebar, do nothing
                if(keyCode != 13 && keyCode != 32){
                    return true;
                }
                // prevent default for Spacebar
                else if(keyCode == 32){
                    e.preventDefault();
                }
            }

            var targetName = $(this).attr('data-target'),
                $targetElem = $('.'+targetName),
                num     = parseInt($targetElem.last().attr('id').match(/\d+$/)), // index of the greatest "duplicatable" input fields
                newNum  = new Number(num + 1),      // the numeric ID of the new input field being added
                $newElem = $('#'+targetName + num).clone().attr('id', targetName + newNum).fadeIn('slow'); // create the new element via clone(), and manipulate it's ID using newNum value

            // clone the bottom element
            if($targetElem.is(":visible")==true){
                // increment the number on ID's, classes, etc... for the new HTML block
                $newElem.find('#'+targetName + num).attr('id', '#'+targetName + newNum);
                $newElem.find("select").each(function (index, input) {
                    input.name = input.name.replace(targetName + num, targetName + newNum);
                });
                $newElem.find("input").each(function (index, input) {
                    input.name = input.name.replace(targetName + num, targetName + newNum);
                    
                    // set the input to the default value
                    input.value = input.getAttribute('data-defaultvalue');
                });
                $newElem.find(".y_inputNumberChangeBtn").each(function (index, input) {
                    input.setAttribute('data-field', input.getAttribute('data-field').replace(targetName + num, targetName + newNum));
                    
                    // make the decrement button disabled
                    if(input.getAttribute('data-type') ==='minus'){
                        input.setAttribute('disabled','disabled');
                    }
                    // make the increment button enabled
                    if(input.getAttribute('data-type') ==='plus'){
                        input.removeAttribute('disabled');
                    }
                });
                $newElem.find(".y_ancillaryFormDeleteBlock").each(function (index, input) {
                    input.setAttribute('data-target', input.getAttribute('data-target').replace(targetName + num, targetName + newNum));
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
                    input.removeAttribute('disabled');
                });
                $targetElem.find("button").each(function (index, input) {
                    // make the decrement button disabled
                    if(input.getAttribute('data-type') ==='minus'){
                        input.setAttribute('disabled','disabled');
                    }
                });
            }

            setTimeout(function(){
                $('.multi-select').SumoSelect({placeholder: 'Add'});
                $(".SumoSelect").children(".SumoSelect").unwrap();
                $(".SumoSelect").siblings(".optWrapper").remove();
                $(".SumoSelect").siblings(".SelectBox").remove();    
            }, 100);

            // if the max quantity is reached, hide the add button
            var maxQty = 4;
            var $li = $targetElem
                    .closest('li.y_ancillaryListElement');
            if ($li.find(".y_ancillaryOfferItem").length >= maxQty) {
                $li.find(".y_ancillaryFormAddBlock").fadeOut();
                $li.find(".y_ancillaryOfferItem").last().find(":tabbable:last").focus();
            }

        });
    },
    bindRemoveLuggageButton : function() {
        // Delete baggage item
        // We need to bind it to document because the delete block can be added dynamically
        $(document).on('click keydown','.y_ancillaryFormDeleteBlock', function (e) {
            if ($(':animated').length) {
                return false; // don't respond to clicks until
                // animation completed
            }
            // handle the keypress
            if(e.type==="keydown"){
                var keyCode = e.keyCode || e.which; 
                // if it's not the Enter key or Spacebar, do nothing
                if(keyCode != 13 && keyCode != 32){
                    return true;
                }
                // prevent default for Spacebar
                else if(keyCode == 32){
                    e.preventDefault();
                }
            }
            $(this).blur(); // blur the element so you can continue tabbing where you left off
            $("#"+$(this).data('target')).prev().find(":tabbable:last").focus();

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

            // if its the max quantity, show the add button
            var maxQty = 4;
            var $li = $targetElem.closest('li.y_ancillaryListElement');
            if ($li.find(".y_ancillaryOfferItem").length === maxQty) {
                $li.find(".y_ancillaryFormAddBlock").fadeIn();
            }

        });
    }

    
}
