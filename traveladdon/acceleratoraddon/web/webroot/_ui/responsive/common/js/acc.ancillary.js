ACC.ancillary = {

    _autoloadTracc : [
        [ "updateSelectedOptionsInit", $(".y_ancillarySection").length != 0 ],
        [ "bindAddAnotherItemButton", $(".y_ancillarySection").length != 0 ],
        [ "bindRemoveItemButton", $(".y_ancillarySection").length != 0 ],
        [ "bindQuantityFieldChange", $(".y_ancillarySection").length != 0 ],
        [ "bindCheckboxChange", $(".y_ancillarySection").length != 0 ],
        [ "bindOfferItemChange", $(".y_ancillarySection").length != 0 ],
        [ "bindReservationContinueButton", $(".y_ancillarySection").length != 0 ],
        "bindSelectUpgradeBundleOptions"],

    updateSelectedOptionsInit : function() {
        // Update all the select dropdown to disable already selected
        // options
        $(".y_ancillaryListElement").each(function(index, input) {
            ACC.ancillary.updateSelectOptions($(input));
        });
    },

    // Bind function for 'Add Another Item' Link
    bindAddAnotherItemButton : function() {
        // Toggle class to the parent of the selected/checked radio button
        // Add new item
        $(".y_ancillaryFormAddBlock").on('click keydown', function(e) {
            if ($(':animated').length) {
                return false; // don't respond to clicks until animation completed
            }
            // handle the keypress
            if (e.type === "keydown") {
                var keyCode = e.keyCode || e.which;
                // if it's not the Enter key or Spacebar, do nothing
                if (keyCode != 13 && keyCode != 32) {
                    return true;
                }
                // prevent default for Spacebar
                else if (keyCode == 32) {
                    e.preventDefault();
                }
            }

            var targetName = $(this).attr('data-target'),
                $targetElem = $('.' + targetName),
                num = parseInt($targetElem.last().attr('id').match(/\d+$/)), // index of the greatest "duplicatable" input fields
                newNum = new Number(num + 1), // the numeric ID of the new input field being added
                $newElem = $('#' + targetName + num).clone().attr('id', targetName + newNum).fadeIn('slow'); // create the new element via clone() and manipulate it's ID using newNum value

            // increment the number on ID's, classes, etc... for the new HTML block
            $newElem.find('#' + targetName + num).attr('id', '#' + targetName + newNum);
            // Set the new select field
            $newElem.find("select").each(function(index, input) {
                input.name = input.name.replace(targetName + num, targetName + newNum);
                input.selectedIndex = 0; // Default selected option is the first one
            });
            // Set the new input field
            $newElem.find("input").each(function(index, input) {
                input.name = input.name.replace(targetName + num, targetName + newNum);
                input.value = 0; // Default input value is 0
            });
            // Set the new deleteItemButton
            $newElem.find(".y_ancillaryFormDeleteBlock").each(function(index, input) {
                input.setAttribute('data-target', input.getAttribute('data-target').replace(targetName + num, targetName + newNum));
                $(input).css('display', 'block'); // if the first row doesn't have it visible and we are cloning it, make the button appear
            });

            // Make the new row appear after the last one
            $('#' + targetName + num).after($newElem);

            // Set the new minus/plus button
            $newElem.find("button").each(function(index, input) {
                input.setAttribute('data-field', input.getAttribute('data-field').replace(targetName + num, targetName + newNum));
                ACC.ancillary.showQtyButtons($(input), false); // Hidden by default
            });

            // Hide the "Add another item" link
            var $li = $targetElem.closest('li.y_ancillaryListElement');
            $li.find(".y_ancillaryFormAddBlock").fadeOut('slow');
            $li.find(".y_ancillaryOfferItem").last().find(":tabbable:last").focus();
        });
    },

    // Bind function for deleteItem button
    bindRemoveItemButton : function() {
        // Delete item
        // We need to bind it to document because the delete block can be added dynamically
        $(document).on('click keydown', '.y_ancillaryFormDeleteBlock', function(e) {
            if ($(':animated').length) {
                return false; // don't respond to clicks until animation completed
            }

            // handle the keypress
            if (e.type === "keydown") {
                var keyCode = e.keyCode || e.which;
                // if it's not the Enter key or Spacebar, do nothing
                if (keyCode != 13 && keyCode != 32) {
                    return true;
                }
                // prevent default for Spacebar
                else if (keyCode == 32) {
                    e.preventDefault();
                }
            }
            $(this).blur(); // blur the element so you can continue tabbing where you left off
            $("#" + $(this).data('target')).prev().find(":tabbable:last").focus();

            var targetName = $(this).attr('data-target'), $targetElem = $('#' + targetName), targetGroup = targetName.replace(/\d+$/, ""), num = $('.' + targetGroup).length;

            // Removing item from cart
            var productCode = $targetElem.find("select option:selected").attr('value');
            var qty = $targetElem.find("input.y_inputNumber").attr('value');
            var travellerCode = $targetElem.parent().find('.y_ancillary_travellerCode').attr('value');

            if (productCode && qty > 0) {
                ACC.ancillary.addToCartFormSubmit($targetElem, productCode, travellerCode, -qty, function(addToCartResult){
                    if (addToCartResult) {
                        var $li = $targetElem.closest('li.y_ancillaryListElement');
                        var travellerMinOfferGroupQty = parseInt($(this).closest(".y_offerGroup").find("input.y_travellerMinOfferGroupQty").val());
                        var minQty = (travellerMinOfferGroupQty > 0) ? travellerMinOfferGroupQty : 1;

                        // If number of items (num) is greater than 1, i.e this is not the last item, then we delete the row
                        if ($li.find(".y_ancillaryOfferItem").length > minQty) {

                            // Make the item disappear and remove it
                            $targetElem.fadeOut('slow', function () {
                                $(this).remove();
                                ACC.ancillary.updateSelectOptions($li); // Update select options
                                var allSelected = true;
                                $li.find("select").each(function (index, item) {
                                    if ($(item).prop("selectedIndex") == 0) {
                                        allSelected = false;
                                    }
                                });
                                if (allSelected) {
                                    $li.find(".y_ancillaryFormAddBlock").fadeIn('slow'); // Make the 'Add another item' link appear (if it was hidden)
                                }
                            });
                        }

                        // If this is the last item, we just reset it to the initial status
                        else {
                            $targetElem.find(".y_ancillaryFormDeleteBlock").fadeOut('slow'); // Hide the deleteItem Button
                            $targetElem.find("select").each(function (index, input) {
                                input.selectedIndex = 0; // Default selected option is the first one
                            });
                            $targetElem.find("input").each(function (index, input) {
                                input.value = 0; // Default input value is 0
                            });
                            $targetElem.find("button").each(function (index, input) {
                                ACC.ancillary.showQtyButtons($(input), false); // Hidden by default
                            });
                            ACC.ancillary.updateSelectOptions($li); // Update select options
                            $li.find(".y_ancillaryFormAddBlock").fadeOut('slow');
                        }
                    }
                });
            }
        });
    },

    // Bind function for minus/plus button
    bindQuantityFieldChange : function() {
        $('.y_ancillarySection').on('click', '.y_ancillaryFormQuantityWrapper .y_inputNumberChangeBtn', function(e) {
            var $target = $("#" + $(this).data('field'));
            var type = $(this).attr('data-type');
            var productCode = $target.find("select option:selected").attr('value');
            var travellerCode = $target.parent().find('.y_ancillary_travellerCode').attr('value');
            if (productCode) {
                if (type === 'minus') {
                    ACC.ancillary.addToCartFormSubmit($target, productCode, travellerCode, -1, function(addToCartResult){
                        if (!addToCartResult) {
                            // add to cart returned error, revert the selected option to the original one
                            e.stopPropagation();
                        }
                    });
                } else if (type === 'plus') {
                    ACC.ancillary.addToCartFormSubmit($target, productCode, travellerCode, 1, function(addToCartResult){
                        if (!addToCartResult) {
                            // add to cart returned error, revert the selected option to the original one
                            e.stopPropagation();
                        }
                    });
                }
            }
        });
    },

    addNewOfferItemToCart: function (element, $inputBoxTarget, $target, travellerCode) {
        var $li;
        var minQty;
        var travellerMinOfferGroupQty;

        // input data-min, data-max and data-defaultvalue are set accordingly to the selected product
        var selectedIndex = $(element).prop("selectedIndex");
        if (selectedIndex == 0) {
            // The default option is selected (no product)
            $inputBoxTarget.val(0);
            $target.find("button").each(function (index, input) {
                ACC.ancillary.showQtyButtons($(input), false); // Hidden by default
            });

            travellerMinOfferGroupQty = parseInt($(element).closest(".y_offerGroup").find("input.y_travellerMinOfferGroupQty").val());
            minQty = (travellerMinOfferGroupQty > 0) ? travellerMinOfferGroupQty : 1;
            $li = $target.closest('li.y_ancillaryListElement');
            if ($li.find(".y_ancillaryOfferItem").length == minQty) {
                $target.find(".y_ancillaryFormDeleteBlock").fadeOut('slow');
            }
            $li = $target.closest('li.y_ancillaryListElement');
            $li.find(".y_ancillaryFormAddBlock").fadeOut('slow');

        } else {
            var $selectedOption = $(element).find("option")[selectedIndex];

            // Setting min, max, default value and value in input field
            var min = $($selectedOption).attr("data-min");
            var max = $($selectedOption).attr("data-max");
            $inputBoxTarget.attr("data-min", min);
            $inputBoxTarget.attr("data-max", max);
            var minAsDefault = min > 1;
            var defaultValue = minAsDefault ? min : 1;
            $inputBoxTarget.data('defaultvalue', defaultValue);

            var newVal = $inputBoxTarget.data('defaultvalue');
            $inputBoxTarget.attr('value', newVal);
            $inputBoxTarget.val(newVal);

            // Add new product to the cart
            if (newVal > 0) {
                var productCode = $target.find("select option:selected").attr('value');
                ACC.ancillary.addToCartFormSubmit($target, productCode, travellerCode, newVal, function(newAddToCartResult){
                    if (newAddToCartResult) {
                        // addToCart was successful
                        // Hide minus/plus buttons
                        $(element).closest('.y_ancillaryOfferItem').find("button").each(function (index, input) {
                            // make the decrement button disabled
                            if (input.getAttribute('data-type') === 'minus') {
                                input.setAttribute('disabled', 'disabled');
                            }
                            // make the increment button enabled
                            if (input.getAttribute('data-type') === 'plus') {
                                input.removeAttribute('disabled');
                            }
                            ACC.ancillary.showQtyButtons($(input), !(defaultValue == max && defaultValue == min));
                        });

                        $target.find(".y_ancillaryFormDeleteBlock").fadeIn('slow'); // If the deleteItem button was not visible, make it appear

                        //if possible, make the "add another item" button appear
                        // If the number of row is equal to the number of product that can be selected (size - 1) then hide the "Add another item" link
                        var travellerMaxOfferGroupQty = parseInt($(element).closest(".y_offerGroup").find("input.y_travellerMaxOfferGroupQty").val());
                        var maxQty = (travellerMaxOfferGroupQty != -1 && travellerMaxOfferGroupQty < $target.find("option").length - 1) ? travellerMaxOfferGroupQty : $target.find("option").length - 1;
                        travellerMinOfferGroupQty = parseInt($(element).closest(".y_offerGroup").find("input.y_travellerMinOfferGroupQty").val());
                        minQty = (travellerMinOfferGroupQty > 0) ? travellerMinOfferGroupQty : 1;
                        var $li = $target.closest('li.y_ancillaryListElement');

                        var allSelected = true;
                        $li.find("select").each(function (index, item) {
                            if ($(item).prop("selectedIndex") == 0) {
                                allSelected = false;
                            }
                        });

                        if ($li.find(".y_ancillaryOfferItem").length < maxQty && $li.find(".y_ancillaryOfferItem").length >= minQty && allSelected) {
                            $li.find(".y_ancillaryFormAddBlock").fadeIn('slow');
                        }

                    } else {
                        // addToCart returned error
                        $(element).prop("selectedIndex", 0); // Set the selected option to the default one
                        $target.find("input").each(function (index, input) {
                            input.value = 0; // Default input value is 0
                        });
                        $target.find("button").each(function (index, input) {
                            ACC.ancillary.showQtyButtons($(input), false); // Hidden by default
                        });
                        $li = $target.closest('li.y_ancillaryListElement');
                        travellerMinOfferGroupQty = parseInt($(element).closest(".y_offerGroup").find("input.y_travellerMinOfferGroupQty").val());
                        minQty = (travellerMinOfferGroupQty > 0) ? travellerMinOfferGroupQty : 1;
                        if ($li.find(".y_ancillaryOfferItem").length <= minQty) {
                            $target.find(".y_ancillaryFormDeleteBlock").fadeOut('slow');
                        }

                    }
                });
            }
        }

        // Update select options
        $li = $target.closest('li.y_ancillaryListElement');
        ACC.ancillary.updateSelectOptions($li);
    },

    // Bind function for select when a different option is selected
    bindOfferItemChange : function() {
        var productCodePrevious = "";
        $(document).on('click', '.y_ancillaryOfferItem select', function() {
            var $target = $(this).closest('.y_ancillaryOfferItem');
            productCodePrevious = $target.find("select option:selected").attr('value');
        }).on('change', '.y_ancillaryOfferItem select', function(e) {
            var $inputBoxTarget = $(this).closest('.y_ancillaryOfferItem').find('.y_inputNumber');
            var $target = $(this).closest('.y_ancillaryOfferItem');
            var oldQty = $inputBoxTarget.attr('value');
            var travellerCode = $target.parent().find('.y_ancillary_travellerCode').attr('value');

            // If there was another product, remove it from the cart
            if (productCodePrevious && oldQty > 0) {
                ACC.ancillary.addToCartFormSubmit($target, productCodePrevious, travellerCode, -oldQty, function (addToCartResult) {
                    if (!addToCartResult) {
                        // add to cart returned error, revert the selected option to the original one
                        $(this).find('option[value="' + productCodePrevious + '"]').prop('selected', true);
                    } else {
                        ACC.ancillary.addNewOfferItemToCart(this, $inputBoxTarget, $target, travellerCode);
                    }
                });
            }
            else{
                ACC.ancillary.addNewOfferItemToCart(this, $inputBoxTarget, $target, travellerCode);
            }
        });
    },

    bindCheckboxChange : function() {
        $(document).on('change', '.y_ancillaryFormQuantityWrapper .y_OfferProductCheckBoxSelection', function(e) {
            var $target = $(this);
            var productCode = $target.attr('value');
            var travellerCode = $target.closest('.y_ancillaryListElement').find('.y_ancillary_travellerCode').attr('value');
            var qty = parseInt($target.attr("min"));
            if (productCode) {
                var $checkbox = this;
                if ($checkbox.checked) // if changed state is "CHECKED"
                {
                    ACC.ancillary.addToCartFormSubmit($target, productCode, travellerCode, qty, function(success){
                        if(!success) {
                            $checkbox.checked = false;
                        }
                    });
                } else {
                    ACC.ancillary.addToCartFormSubmit($target, productCode, travellerCode, -qty, function(success){
                        if(!success) {
                            $checkbox.checked = true;
                        }
                    });
                }
            }
        });
    },

    // Bind function for continue button in the itinerary component to perform a validation for travel restriction
    bindReservationContinueButton : function() {
        $(document).on('click', '.y_reservationContinueButton', function(event) {
            var jsonData;
            var url = ACC.config.contextPath + "/ancillary/check-offer-groups-restriction";
            if($(this).attr("data-amend")){
                url = ACC.config.contextPath + "/manage-booking/ancillary/check-offer-groups-restriction";
            }
            $.when(ACC.services.checkOfferGroupsRestriction(url)).then(function(response) {
                jsonData = JSON.parse(response);
                if (jsonData.hasErrorFlag) {
                    event.preventDefault();
                    var output = [];
                    jsonData.errors.forEach(function(error) {
                        output.push("<p>" + error + "</p>");
                    });
                    $("#y_travelRestrictionModal .y_travelRestrictionErrorBody").html(output.join(""));
                    $("#y_travelRestrictionModal").modal();
                }
                // if isValid == true continue with the submit
            });
        });
    },

    addToCartFormSubmit : function($target, productCode, travellerCode, qty, callbackFunction) {

        var travelRouteCode = $target.closest(".tab-wrapper").find(
            "li.active input[name=travelRouteCode]").attr('value');
        var originDestinationRefNumber = $target.closest(".tab-wrapper").find(
            "li.active input[name=originDestinationRefNumber]").attr(
            'value');
        var transportOfferingCodes = [];
        $target.closest(".y_ancillaryFormQuantityWrapper").find(
            "input[name=transportOfferingCodes]").each(function() {
            transportOfferingCodes.push($(this).val());
        });
        $("#addToCartForm #y_productCode").attr('value', productCode);
        $("#addToCartForm #y_transportOfferingCodes").attr('value',
            transportOfferingCodes);
        $("#addToCartForm #y_travelRouteCode").attr('value', travelRouteCode ? travelRouteCode : "");
        $("#addToCartForm #y_travellerCode").attr('value', travellerCode ? travellerCode : "" );
        $("#addToCartForm #y_quantity").attr('value', qty);
        $("#addToCartForm #y_originDestinationRefNumber").attr('value', originDestinationRefNumber ? originDestinationRefNumber : 0);
        var addToCartResult;
        $.when(ACC.services.addProductToCartAjax()).then(
            function(response) {
                var jsonData = JSON.parse(response);

                if(jsonData.valid) {
                    ACC.reservation.refreshReservationTotalsComponent($("#y_reservationTotalsComponentId").val());
                    ACC.reservation.refreshTransportSummaryComponent($("#y_transportSummaryComponentId").val());
                } else {
                    var output = [];
                    jsonData.errors.forEach(function(error) {
                        output.push("<p>" + error + "</p>");
                    });
                    $("#y_addProductToCartErrorModal .y_addProductToCartErrorBody").html(output.join(""));
                    $("#y_addProductToCartErrorModal").modal();
                }
                addToCartResult = jsonData.valid;
                callbackFunction(addToCartResult);
            });
    },

    updateSelectOptions : function($li) {
        // create a list of disabled options
        var disabledOptions = [];
        $li.find('select')
            .each(
                function(index, element) {
                    var selectedIndex = $(element)
                        .prop('selectedIndex');
                    if (selectedIndex != 0
                        && jQuery.inArray(selectedIndex,
                            disabledOptions) == -1) {
                        disabledOptions.push(selectedIndex);
                    }
                })
        // update the list
        $li
            .find('select')
            .each(
                function(index, element) {
                    for (var count = 0; count < element.options.length; count++) {
                        if ($(element).prop('selectedIndex') != count
                            && jQuery.inArray(count,
                                disabledOptions) == -1
                            && $(element.options[count]).attr("class") != "y_noProductsAvailable") {
                            element.options[count]
                                .removeAttribute('disabled');
                        } else {
                            element.options[count].setAttribute(
                                'disabled', 'disabled');
                        }
                    }
                });
    },

    showQtyButtons : function($button, flag) {
        if (flag) {
            $button.show();
        } else {
            $button.hide();
        }
    },

    bindSelectUpgradeBundleOptions : function() {
        $('#y_upgradeBundleOptionsButton').on("click", function() {
            if (!$('#y_panel-upgrade').is(":visible")) {
                $("#y_selectUpgradeSpan").hide();
                $("#y_hideUpgradeSpan").show();
                ACC.ancillary.getUpgradeBundleOptions();
            } else {
                $("#y_selectUpgradeSpan").show();
                $("#y_hideUpgradeSpan").hide();
            }
        });
    },

    getUpgradeBundleOptions : function() {
        var upgradeBundlesData='';
        $.when( ACC.services.getUpgradeBundleOptions()).then(
            function(response){
                if(response.isUpgradeOptionAvailable){
                    var htmlContent = response.htmlContent;
                    $('#y_panel-upgrade').html(htmlContent);
                    ACC.ancillary.bindSelectUpgradeBundle();

                }else{
                    $("#y_noUpgradeAvailableModal").modal();
                    $("#y_selectUpgradeSpan").show();
                    $("#y_hideUpgradeSpan").hide();
                }
            });
    },

    bindSelectUpgradeBundle : function() {
        $('.y_bundleType').on("click", function() {
            var addBundleToCartForm = $(this).closest("#y_upgradeBundleFormHiddenHtml").find('form');
            addBundleToCartForm.submit();
        });
    },

    showNoUpgradeAvailabileModal : function() {
        if($("#y_noAccommodationAvailability"))
        {
            var noAccommodationAvailability = $("#y_noAccommodationAvailability").val();
            if(noAccommodationAvailability == "show"){
                $("#y_noAvailabilityModal").modal();
            }
        }
    }
}
