ACC.ancillaryGroup = {
	
	selectedAncillaryInfos: {},
	
	_autoloadTracc : [
		"bindApplyOfferButton",
		"bindSelectAncillaryProduct",
		"bindAncillaryRemoveLink",
		"bindAddAnotherAncillaryItem",
		"bindTotalPriceReflection",
		"refreshAllSelectAncillaryOptions",
		"evaluateSelectedAncillaryInfo",
		"bindTravellersSelect"
		],

	bindApplyOfferButton : function() {
		$(".y_applyOffer").on('click', function() {
			$(this).attr('disabled',true);
			var offerIndex=$(this).attr("data-offerindex");
			var tabIndex=$(this).attr("data-tabindex");				
			var travelRouteCode = $(this).attr("data-travelroute");
			var originDestinationRefNumber = $(this).attr("data-origindestinationrefnumber");
			var transportOfferingCodes= $(this).attr("data-transportofferingcodes").split(",");
			var offerType=$(this).attr("data-offertype");
			var offerTabId=offerIndex+"_"+tabIndex;
			var selectedAncillaryInfo=ACC.ancillaryGroup.selectedAncillaryInfos[offerTabId];	
			
			var addToCartForms = ACC.ancillaryGroup.getAllAddToCartForms(offerType, offerTabId, travelRouteCode, originDestinationRefNumber, transportOfferingCodes);
			var addToCartFormsWithAdjustQuantity=ACC.ancillaryGroup.createAddToCartFormsWithAdjustQuantity(selectedAncillaryInfo, addToCartForms, travelRouteCode, originDestinationRefNumber, transportOfferingCodes);
			var filteredAddToCartForms=addToCartFormsWithAdjustQuantity.filter(function (addToCartForm) {
				  return addToCartForm.qty!=0;
			});
			
			if(!$.isEmptyObject(filteredAddToCartForms)) {
				$("#y_processingModal").show();
				ACC.ancillaryGroup.selectedAncillaryInfos[offerTabId]=ACC.ancillaryGroup.resetSelectedAncillaryInfo(addToCartForms);
				ACC.ancillaryGroup.performAddToCartForGroup(filteredAddToCartForms,function(addToCartResult) {
					$("#y_processingModal").hide();
				});
			}
		});
	},
	
	getAllAddToCartForms: function(offerType, offerTabId, travelRouteCode, originDestinationRefNumber, transportOfferingCodes) {
		var addToCartForms = [];
		if(offerType=='multiple') {
			var selectItemObjs=$(".y_selectAncillaryProduct[data-offertabid='" + offerTabId + "']:visible");
			selectItemObjs.each(function(index, selectItemObj ) {
				var selectedOption = $(selectItemObj).find(':selected');
				var selectedValue=selectedOption.val().trim();
				if(selectedValue) {
					var uniqueId=offerTabId+"_"+$(selectItemObj).attr("data-itemindex");
					var productQtyTextId="y_ancillaryProductQtyText_"+uniqueId;
					var qty=Number($("input[id='" + productQtyTextId + "']").val());
					var travellersSelecterId="y_travellersSelecter_"+uniqueId;
					var selectedPassengerOptions=$("#"+travellersSelecterId).val();
					addToCartForms=addToCartForms.concat(ACC.ancillaryGroup.createAddToCartForms(selectedValue, selectedPassengerOptions, travelRouteCode, transportOfferingCodes, originDestinationRefNumber, qty));
				}
			});
		} else {
			var labelItemObjs=$("label[data-offertabid='" + offerTabId + "']");
			labelItemObjs.each(function(index, labelItemObj ) {
				var travellersSelecterId="y_travellersSelecter_"+offerTabId;
				var selectedPassengerOptions=$("#"+travellersSelecterId).val();
				var qty=($("#"+travellersSelecterId).prop('type')=="checkbox") ? ($("#"+travellersSelecterId).is(':checked') ? 1 : 0) : 1;
				var productCode=$(labelItemObj).attr("data-value");
				addToCartForms=addToCartForms.concat(ACC.ancillaryGroup.createAddToCartForms(productCode, selectedPassengerOptions, travelRouteCode, transportOfferingCodes, originDestinationRefNumber, qty));
			});
		}
		return addToCartForms;
	},
	
	createAddToCartForms: function(productCode, selectedPassengerOptions, travelRouteCode, transportOfferingCodes, originDestinationRefNumber, qty) {
		var addToCartFormsPerProduct = [];
		if(selectedPassengerOptions==null || selectedPassengerOptions=="on") {
			addToCartFormsPerProduct.push(ACC.ancillaryGroup.createAddToCartForm(productCode, "", travelRouteCode, transportOfferingCodes, originDestinationRefNumber, qty));
		} else {
			$(selectedPassengerOptions).each(function(index, travellerCode ) {
				addToCartFormsPerProduct.push(ACC.ancillaryGroup.createAddToCartForm(productCode, travellerCode, travelRouteCode, transportOfferingCodes, originDestinationRefNumber, qty));
			});
		}
		return addToCartFormsPerProduct;
	},
	
	createAddToCartForm: function(productCode, travellerCode, travelRouteCode, transportOfferingCodes, originDestinationRefNumber, qty) {
		var addToCartForm=new Object();
		addToCartForm.productCode=productCode;
		addToCartForm.travellerCode=travellerCode;
		addToCartForm.travelRouteCode=travelRouteCode;
		addToCartForm.transportOfferingCodes=transportOfferingCodes;
		addToCartForm.originDestinationRefNumber=originDestinationRefNumber;
		addToCartForm.qty=qty;
		return addToCartForm;
		
	},
	
	createAddToCartFormsWithAdjustQuantity: function(selectedAncillaryInfo, addToCartForms, travelRouteCode, originDestinationRefNumber, transportOfferingCodes) {
		var newAddToCartForms = [];
		
		// adjusting quantities related to existing selected products
		for(var productCode in selectedAncillaryInfo) {
			var qty=selectedAncillaryInfo[productCode].qty;
			if(selectedAncillaryInfo[productCode].travellerCodes.length) {
				$(selectedAncillaryInfo[productCode].travellerCodes).each(function(index, travellerCode ) {
					var addToCartForm= ACC.ancillaryGroup.getAddToCartForm(addToCartForms, productCode, travellerCode, travelRouteCode, originDestinationRefNumber, transportOfferingCodes, qty);
					newAddToCartForms.push(addToCartForm);
				});
			} else {
				var addToCartForm= ACC.ancillaryGroup.getAddToCartForm(addToCartForms, productCode, "", travelRouteCode, originDestinationRefNumber, transportOfferingCodes, qty);
				newAddToCartForms.push(addToCartForm);
			}
		}
		
		// adjusting quantities related to new selected products
		$(addToCartForms).each(function(indexA, addToCartForm ) {
			var entryExists=false;
			$(newAddToCartForms).each(function(indexB, newAddToCartForm ) {
				if(newAddToCartForm.productCode==addToCartForm.productCode && newAddToCartForm.travellerCode==addToCartForm.travellerCode) {
					entryExists=true;
					return false;
				}
			});
			if(!entryExists) {
				newAddToCartForms.push(addToCartForm);
			}
		});
		
		return newAddToCartForms;
	},
	
	getAddToCartForm: function(addToCartForms, productCode, travellerCode, travelRouteCode, originDestinationRefNumber, transportOfferingCodes, qty) {
		for(var index in addToCartForms) {
			var addToCartForm=addToCartForms[index];
			if(addToCartForm.productCode==productCode && addToCartForm.travellerCode==travellerCode) {
				var newQty=addToCartForm.qty-qty;
				return ACC.ancillaryGroup.createAddToCartForm(addToCartForm.productCode, addToCartForm.travellerCode, addToCartForm.travelRouteCode, addToCartForm.transportOfferingCodes, addToCartForm.originDestinationRefNumber, newQty)
			}
		}
		return ACC.ancillaryGroup.createAddToCartForm(productCode, travellerCode, travelRouteCode, transportOfferingCodes, originDestinationRefNumber, -qty);
	},
	
	resetSelectedAncillaryInfo: function(addToCartForms) {
		var selectedAncillaryInfo=new Object();
		$(addToCartForms).each(function(index, addToCartForm ) {
			var productCode=addToCartForm.productCode;
			var travellerCode=addToCartForm.travellerCode;
			var qty=addToCartForm.qty;
			
			if(selectedAncillaryInfo[productCode]) {
				selectedAncillaryInfo[productCode].travellerCodes.push(travellerCode);
			} else {
				selectedAncillaryInfo[productCode]=new Object();
				selectedAncillaryInfo[productCode].qty=qty;
				selectedAncillaryInfo[productCode].travellerCodes=[travellerCode];
			}
		});
		return selectedAncillaryInfo;
	},
	
	performAddToCartForGroup: function(addToCartForms,callbackFunction) {
        var addToCartResult;
        $.when(ACC.services.addProductsToCartForGroup(addToCartForms)).then(
            function(response) {
                var jsonData = JSON.parse(response);

                if(jsonData.valid) {
                    ACC.reservation.refreshReservationTotalsComponent($("#y_reservationTotalsComponentId").val());
                    ACC.reservation.refreshTransportSummaryComponent($("#y_transportSummaryComponentId").val());
                } else {
                    var output = "";
                    jsonData.errors.forEach(function(error) {
                        output=output+"<p>" + error + "</p>";
                    });
                    $("#y_addProductToCartErrorWithOKModal .y_addProductToCartErrorWithOKBody").html(output);
                    $("#y_addProductToCartErrorWithOKModal").modal();
                }
                addToCartResult = jsonData.valid;
                callbackFunction(addToCartResult);
            });
	},
	
	bindSelectAncillaryProduct : function() {
		$(".y_selectAncillaryProduct").on('change', function() {
			var uniqueId=$(this).attr("data-offertabid")+"_"+$(this).attr("data-itemindex");
			var inputNumberText=$("input.y_inputNumber#y_ancillaryProductQtyText_"+uniqueId);
			var selectedValue=$(this).val();
			var disableTravellersSelecter=false;
			
			if (selectedValue.trim()) {
				var selectedOption = $(this).find(':selected');
				var travellerMinOfferQty=selectedOption.attr('data-travellerminofferqty');
				var passengerTypesStr=selectedOption.attr('data-passengertypes').trim().replace("[","").replace("]","");
				var passengerTypes=passengerTypesStr?passengerTypesStr.split(","):[];
				var travellerMaxOfferQty=selectedOption.attr('data-travellermaxofferqty');
				inputNumberText.attr("data-min",travellerMinOfferQty);
				inputNumberText.attr("data-max",travellerMaxOfferQty);
				inputNumberText.val(1).change();
			} else {
				inputNumberText.attr("data-min",0);
				inputNumberText.attr("data-max",0);
				inputNumberText.val(0).change();
				disableTravellersSelecter=true;
			}
			
			ACC.ancillaryGroup.refreshAncillaryDeleteBlock($(this));
			ACC.ancillaryGroup.refreshVisibilityAddAnotherItemLink($(this));
			ACC.ancillaryGroup.refreshSelectAncillaryOptions($(this).attr("data-offertabid"));
			ACC.ancillaryGroup.refreshTravellersSelecter(uniqueId, passengerTypes);

			$(".multi-select#y_travellersSelecter_"+uniqueId).attr('disabled',disableTravellersSelecter);
			if(disableTravellersSelecter) {
				$(".multi-select#y_travellersSelecter_"+uniqueId).parent().closest('div').addClass('disabled');
			} else {
				$(".multi-select#y_travellersSelecter_"+uniqueId).parent().closest('div').removeClass('disabled');
			}
			ACC.ancillaryGroup.bindTravellersSelect(); 
		});
	},
	
	refreshAncillaryDeleteBlock: function(selectAncillaryProduct) {
		var itemindex=selectAncillaryProduct.attr("data-itemindex");
		var offerTabId=selectAncillaryProduct.attr("data-offertabid");
		var selectItemNos=$(".y_selectAncillaryProduct[data-offertabid='" + offerTabId + "']:visible").length;
		var removeLink=$(".y_ancillaryRemoveItem[data-offertabid='"+offerTabId+"'][data-itemindex='"+itemindex+"']");
		if(selectAncillaryProduct.val().trim() || selectItemNos > 1) {
			removeLink.show();				
		} else {
			removeLink.hide();
		}
	},
	
	refreshVisibilityAddAnotherItemLink: function(selectItemObj) {
		var offerTabId=selectItemObj.attr("data-offertabid");
		var selectItemObjOptionLength=selectItemObj.find("option").length-1;
		var selectItemNos=$(".y_selectAncillaryProduct[data-offertabid='" + offerTabId + "']:visible").length;
		if(selectItemNos<selectItemObjOptionLength && selectItemObj.val().trim()) {
			$(".y_ancillaryAddAnotherItem#y_addAnotherItem_"+offerTabId).fadeIn('slow');
		} else {
			$(".y_ancillaryAddAnotherItem#y_addAnotherItem_"+offerTabId).fadeOut('slow');
		}
	},
	
	refreshAllSelectAncillaryOptions: function() {
		$(".y_selectAncillaryProduct").each(function(index, selectAncillaryProduct ) {
			var offerTabId=$(selectAncillaryProduct).attr("data-offertabid");
			ACC.ancillaryGroup.refreshSelectAncillaryOptions(offerTabId);
		});
	},
	
	refreshSelectAncillaryOptions: function(offerTabId) {
		var selectItemObjs=$(".y_selectAncillaryProduct[data-offertabid='" + offerTabId + "']");
		selectItemObjs.find("option").attr('disabled',false);
		selectItemObjs.each(function(index, selectItemObj ) {
			var selectedValue=selectItemObj.value;
			if(selectedValue.trim()) {
				selectItemObjs.find("option[value='"+selectedValue+"']").attr('disabled',true);
			}
		});
	},
	
	refreshTravellersSelecter: function(uniqueId, passengerTypes) {
		var travellersSelecter=$(".multi-select#y_travellersSelecter_"+uniqueId);
		if($.isEmptyObject(passengerTypes)) {
			travellersSelecter.find("option").attr('disabled',false);
		} else {			
			travellersSelecter.find("option").attr('disabled',true);
			$(passengerTypes).each(function(index, passengerType) {
				$(".multi-select#y_travellersSelecter_"+uniqueId).find("option[data-passengertype='"+passengerType+"']").attr('disabled',false);
			});
		}
		ACC.ancillaryGroup.sumoUnSelectAll(travellersSelecter);
	},
	
	sumoUnSelectAll: function(travellersSelecter) {
		$(travellersSelecter).each(function(index, travellerSelecter ) {
			travellerSelecter.sumo.unSelectAll();
		});
		travellersSelecter.SumoSelect({selectAll: true, csvDispCount: 1, captionFormatAllSelected: ACC.addons.travelacceleratorstorefront['ancillary.offergroup.travellerselecter.allselectcaption.text']});
	},
	
	bindAncillaryRemoveLink: function() {
		$(".y_ancillaryRemoveItem").on('click', function() {
			var targetId=$(this).attr("data-target");
			var itemindex=$(this).attr("data-itemindex");
			var offerTabId=$(this).attr("data-offertabid");
			var offerType=$(this).attr("data-offertype");
			var ancillaryRemoveLinkLength=$(".y_ancillaryRemoveItem[data-offertabid='"+offerTabId+"']:visible").length;
			var divToRemove=$("div[id='" + targetId + "']");
			if(divToRemove.length && ancillaryRemoveLinkLength) {
				if(ancillaryRemoveLinkLength==1) {
					ACC.ancillaryGroup.resetContainingFields(divToRemove, offerTabId, itemindex);
					$(".y_ancillaryAddAnotherItem#y_addAnotherItem_"+offerTabId).fadeOut('slow');
					$(this).hide();
				} else {
					divToRemove.remove();
					ACC.ancillaryGroup.reflectTotalPriceChanged(offerTabId, offerType);
					$(".y_ancillaryAddAnotherItem#y_addAnotherItem_"+offerTabId).fadeIn('slow');
				}
				ACC.ancillaryGroup.refreshSelectAncillaryOptions($(this).attr("data-offertabid"));
			}
		});
	},
	
	resetContainingFields: function (divToRemove, offerTabId, itemindex) {
		var selectItemObjs=$(".y_selectAncillaryProduct[data-offertabid='" + offerTabId + "'][data-itemindex='"+itemindex+"']");
		selectItemObjs.val("");
		var uniqueId=offerTabId+"_"+itemindex;
		var inputNumberText=$("input.y_inputNumber#y_ancillaryProductQtyText_"+uniqueId);
		inputNumberText.attr("data-min",0);
		inputNumberText.attr("data-max",0);
		inputNumberText.val(0).change();
		var travellersSelecterId="y_travellersSelecter_"+uniqueId;
		ACC.ancillaryGroup.sumoUnSelectAll($("#"+travellersSelecterId));
		$("#"+travellersSelecterId).attr('disabled',true);
		$("#"+travellersSelecterId).parent().closest('div').addClass('disabled');
	},
	
	bindAddAnotherAncillaryItem: function() {
		$(".y_ancillaryAddAnotherItem").on('click', function() {
			var targetId=$(this).attr("data-target");
			var divToClone=$("div[id='" + targetId + "']");
			var itemIndex=Number($(this).attr("data-itemindex"))+1;
			var offerTabIndex=targetId.substring(0,targetId.length-2); 
			
			var newDivId=offerTabIndex+'_'+itemIndex;
			var newDiv = divToClone.clone().attr('id', newDivId);
			ACC.ancillaryGroup.changeChildrenAttributes(newDiv, itemIndex);	
			newDiv.show();
			
			var containerDivId=offerTabIndex.replace("addAnotherItemDiv_","y_ancillaryOfferItem_");
			var containerDiv=$("." + containerDivId);
			containerDiv.append(newDiv);
			
			ACC.ancillaryGroup.bindTravellersSelect();
			ACC.ancillaryGroup.bindSelectAncillaryProduct();
			ACC.ancillaryGroup.bindAncillaryRemoveLink();
			ACC.ancillaryGroup.bindTotalPriceReflection();

			$(this).attr("data-itemindex",itemIndex);
			$(this).fadeOut('slow');
		});
	},
	
	changeChildrenAttributes: function(newDiv, itemIndex) {
		var y_selectAncillaryProduct=newDiv.find(".y_selectAncillaryProduct");
		var offerTabId=y_selectAncillaryProduct.attr('data-offertabid');
		var uniqueId=offerTabId+"_"+y_selectAncillaryProduct.attr('data-itemindex');
		var newUniqueId=offerTabId+"_"+itemIndex;			
		y_selectAncillaryProduct.attr('data-itemindex',itemIndex);
		ACC.ancillaryGroup.refreshSelectAncillaryOptions(y_selectAncillaryProduct.attr("data-offertabid"));
		
		var y_inputNumberChangeBtn=newDiv.find(".y_inputNumberChangeBtn");
		y_inputNumberChangeBtn.attr('data-field',y_inputNumberChangeBtn.attr('data-field').replace(uniqueId,newUniqueId));
		
		var y_inputNumber=newDiv.find(".y_inputNumber");
		y_inputNumber.attr('name', y_inputNumber.attr('name').replace(uniqueId,newUniqueId));
		y_inputNumber.attr('id', y_inputNumber.attr('id').replace(uniqueId,newUniqueId));
		
		var selectpicker=newDiv.find(".multi-select");
		selectpicker.attr('id', selectpicker.attr('id').replace(uniqueId,newUniqueId));
		selectpicker.attr('disabled',true);
		
		newDiv.find(".y_travellersSelecter").html(selectpicker);
		
		var y_ancillaryRemoveItem=newDiv.find(".y_ancillaryRemoveItem");
		y_ancillaryRemoveItem.attr('data-target',y_ancillaryRemoveItem.attr('data-target').replace(uniqueId,newUniqueId));
		y_ancillaryRemoveItem.attr('data-itemindex',itemIndex);
	},
	
	bindTotalPriceReflection: function() {
		$('[data-offertabid]').on('change', function() {
			ACC.ancillaryGroup.reflectTotalPriceChanged($(this).attr('data-offertabid'), $(this).attr('data-offertype'));
		});
	},
	
	reflectTotalPriceChanged: function (offerTabId, offerType) {
		var targetId="ancillaryPriceInfo_"+offerTabId;
		var ancillaryPriceInfoDiv=$("div[id='" + targetId + "']");
		var currentCurrencySymbol=ancillaryPriceInfoDiv.attr("data-current-currency-symbol");
		var priceInfo=0;
		
		if(offerType=='multiple') {
			var selectItemObjs=$(".y_selectAncillaryProduct[data-offertabid='" + offerTabId + "']:visible");
			selectItemObjs.each(function(index, selectItemObj ) {
				var selectedOption = $(selectItemObj).find(':selected');
				var selectedValue=selectedOption.val();
				if(selectedValue.trim()) {
					var price=Number(selectedOption.attr("data-price"));
					var uniqueId=offerTabId+"_"+$(selectItemObj).attr("data-itemindex");
					var productQtyTextId="y_ancillaryProductQtyText_"+uniqueId;
					var qty=Number($("input[id='" + productQtyTextId + "']").val());
					var travellersSelecterId="y_travellersSelecter_"+uniqueId;
					var travellersSelecter=$("#"+travellersSelecterId);
					var selectedPassengerOptionsLength=(travellersSelecter.length==0)?1:travellersSelecter.find("option:selected").length;
					priceInfo=priceInfo+(price*qty*selectedPassengerOptionsLength);
				}
			});
		} else {
			var labelItemObjs=$("label[data-offertabid='" + offerTabId + "']");
			labelItemObjs.each(function(index, labelItemObj ) {
				var price=Number($(labelItemObj).attr("data-price"));
				var travellersSelecterId="y_travellersSelecter_"+offerTabId;
				var travellersSelecter=$("#"+travellersSelecterId);
				var selectedPassengerOptionsLength=($("#"+travellersSelecterId).prop('type')=="checkbox") ? ($("#"+travellersSelecterId).is(':checked') ? 1 : 0) : (travellersSelecter.length==0 ? 1 : travellersSelecter.find("option:selected").length);
				priceInfo=priceInfo+(price*selectedPassengerOptionsLength);
			});
		}
		var label=ancillaryPriceInfoDiv.find("label");
		var offerGroupTotalText=label.attr("data-offer-group-total-text");
		label.text(offerGroupTotalText+" "+currentCurrencySymbol+priceInfo.toFixed(2));
		$("#ancillaryApplyOffer_"+offerTabId).attr('disabled',false);
	},
	
	evaluateSelectedAncillaryInfo: function() {
		$(".y_selectedAncillaryInfo").each(function(index, selectedAncillaryInfo ) {
			var offerTabId=$(selectedAncillaryInfo).attr("data-offertabid");
			ACC.ancillaryGroup.selectedAncillaryInfos[offerTabId]=eval("(" + $(selectedAncillaryInfo).val() + ')');			
		});
	},
	
	bindTravellersSelect: function() {
		$('.multi-select').SumoSelect({selectAll: true, csvDispCount: 1, captionFormatAllSelected: ACC.addons.travelacceleratorstorefront['ancillary.offergroup.travellerselecter.allselectcaption.text']});
	}
}
