ACC.seat = {

	seatMapJsonObj: {},
	currentSeatMapIndex: 0,
	isAmend: false,

	_autoloadTracc: [
		"bindSelectSeatButton"
	],

	bindSelectSeatButton: function () {
		$(document).ready(function () {
			$('#y_selectSeat').attr("disabled", false);
		});

		$('#y_selectSeat').on("click", function () {
			if ($('#y_selectSeat').is('[disabled=disabled]')) {
				return;
			}

			if ($.isEmptyObject(ACC.seat.seatMapJsonObj)) {
				ACC.seat.isAmend = $(this).attr("data-amend");
				$.when(ACC.seat.getSeatMapConfig(ACC.seat.isAmend)).then(function (seatMapResponse) {
					if (!seatMapResponse.seatMap) {
						return;
					}
					ACC.seat.seatMapJsonObj = seatMapResponse;
					if (ACC.seat.seatMapJsonObj) {
						ACC.seat.currentSeatMapIndex = 0;

						var seatMap = ACC.seat.seatMapJsonObj.seatMap[ACC.seat.currentSeatMapIndex];
						var vehicleCode = seatMap.transportOffering.transportVehicle.vehicleInfo.code;
						$.when(ACC.seat.setDynamicSVG(vehicleCode, ACC.seat.currentSeatMapIndex)).then(function () {
							$("#y_seatmapModal").modal("show");
							ACC.seat.refreshSeatInfoData();
							ACC.seat.initializeSvgSeatmap(false);
							ACC.seat.initializeJsBinding();
						});
					}
				});
			} else {
				$("#y_seatmapModal").modal("show");
				ACC.seat.refreshSeatInfoData();
				ACC.seat.resetPassengerInfoData();
				ACC.seat.populatePassengerSelectedSeats(false);
				ACC.seat.populateSelectedSeats(false);
			}
		});
	},

	getSeatMapConfig: function (isAmend) {
		if (Boolean(isAmend === "true")) {
			return ACC.services.getAmendSeatMapAjax();
		}
		else {
			return ACC.services.getSeatMapAjax();
		}
	},

	setDynamicSVG: function (vehicleCode, seatMapIndex) {
		return $.when(ACC.services.getSeatMapSvgCss(vehicleCode)).then(function (response) {
			var seatMap = ACC.seat.seatMapJsonObj.seatMap[seatMapIndex];
			var svgWrap = $(".y_svgWrap[data-transportofferingnumber='" + seatMap.transportOffering.code + "']");
			var seatMapSVGWrap = svgWrap.find('.y_seatMapSVGWrap');
			if (seatMap != null) {

				var changeDeckButtons = svgWrap.siblings(".y_changeDeckBtns");
				var changeDeckButton = $(changeDeckButtons.children()[0]);

				var decks = seatMap.seatMapDetail.decks;
				decks.forEach(function (deck, deckIdx) {
					var deckName = ACC.seat.capitalizeLabel(seatMap.seatMapDetail.decks[deckIdx]);

					if (deckIdx === 0) {
						// seatMap SVG
						seatMapSVGWrap.attr("id", seatMapIndex + "-seatmap-" + deckIdx);
						$(seatMapSVGWrap).html(ACC.seat.convertJsonToXml(JSON.parse(response.svg[deckName])));

						// changeDeck button
						changeDeckButton.text(ACC.addons.travelacceleratorstorefront["text.ancillary.seatmap.seating.deck." + deckName.toLowerCase()]);

					} else {
						// seatMap SVG
						var seatMapSVGWrapCloned = seatMapSVGWrap.clone();
						seatMapSVGWrapCloned.hide();
						seatMapSVGWrapCloned.attr("id", seatMapIndex + "-seatmap-" + deckIdx);
						$(seatMapSVGWrapCloned).html(ACC.seat.convertJsonToXml(JSON.parse(response.svg[deckName])));
						svgWrap.append(seatMapSVGWrapCloned);

						// changeDeck button
						var changeDeckButtonCloned = changeDeckButton.clone();
						changeDeckButtonCloned.text(ACC.addons.travelacceleratorstorefront["text.ancillary.seatmap.seating.deck." + deckName.toLowerCase()]);
						changeDeckButtonCloned.removeAttr('hidden');
						changeDeckButtons.append(changeDeckButtonCloned);
					}
				});

				$('head').append("<style>" + response.css + "</style>");

			}
		});
	},

	capitalizeLabel: function (label) {
		return label.charAt(0).toUpperCase() + label.slice(1).toLowerCase();
	},

	convertJsonToXml: function (jsonObject) {
		var xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
		xml += ACC.seat.convertJsonNodeToXml(jsonObject, jsonObject["tagName"], "");
		return xml.replace(/\t|\n/g, "");
	},

	convertJsonNodeToXml: function (nodeObject, nodeName, ind) {
		var xml = "";
		if (nodeObject instanceof Array) {
			for (var i = 0, n = nodeObject.length; i < n; i++) {
				xml += ind + ACC.seat.convertJsonNodeToXml(nodeObject[i], nodeName, ind + "\t") + "\n";
			}
		}
		else if (typeof(nodeObject) === "object") {
			var hasChild = false;
			xml += ind + "<" + nodeName;
			for (var childObject in nodeObject) {
				if (typeof(nodeObject[childObject]) !== "object") {
					if (childObject !== 'tagName') {
						xml += " " + childObject + "=\"" + nodeObject[childObject].toString() + "\"";
					}
				}
				else {
					hasChild = true;
				}
			}
			xml += hasChild ? ">" : "/>";
			if (hasChild) {
				for (var childObject in nodeObject) {
					if (typeof(nodeObject[childObject]) === "object")
						if (nodeObject[childObject].length === 1 && nodeObject[childObject][0]["tagName"] == null) {
							xml += nodeObject[childObject][0];
						}
						else {
							for (var childNode in nodeObject[childObject]) {
								xml += ACC.seat.convertJsonNodeToXml(nodeObject[childObject][childNode], nodeObject[childObject][childNode]["tagName"], ind + "\t");
							}
						}
				}
				xml += (xml.charAt(xml.length - 1) === "\n" ? ind : "") + "</" + nodeName + ">";
			}
		}
		else {
			xml += ind + "<" + nodeName + ">" + nodeObject.toString() + "</" + nodeName + ">";
		}
		return xml;
	},

	refreshSeatInfoData: function () {
		$(".y_travellerIconPanel").each(function () {
			var seatInfoDatas = $(this).find(".y_seatInfoData");
			seatInfoDatas.each(function () {
				$(this).attr("data-accommodationuid", "");
				$(this).attr("data-previousselectedaccommodationuid", "");
				$(this).attr("data-addremoveaction", "");
			});
		});

		if (ACC.seat.seatMapJsonObj.seatMap) {
			var seatMaps = ACC.seat.seatMapJsonObj.seatMap;
			for (var seatMapIdx = 0; seatMapIdx < seatMaps.length; seatMapIdx++) {
				var seatMap = seatMaps[seatMapIdx];
				if (seatMap && seatMap.selectedSeats) {
					var selectedSeats = seatMap.selectedSeats;
					for (var selectedSeatIdx = 0; selectedSeatIdx < selectedSeats.length; selectedSeatIdx++) {
						var selectedSeat = selectedSeats[selectedSeatIdx];
						var travellerCode = selectedSeat.traveller.label;
						var transportOfferingCode = selectedSeat.transportOffering.code;
						var seat = ACC.seat.getSeatInfoData(seatMap, selectedSeat.seatNumber);
						$(".tab-" + seatMapIdx + "-passenger-seat").each(function () {
							if ($(this).data('passengertype') === travellerCode && $(this).data('transportofferingnumber') === transportOfferingCode) {
								$(this).parents('li').attr("data-previousselectedaccommodationuid", seat.seatUid);
							}
						});
					}
				}
			}
		}
	},

	getSeatInfoData: function (seatMap, seatNumber) {
		var seatMapDetail = seatMap.seatMapDetail;
		if (seatMapDetail) {
			var cabins = seatMapDetail.cabin;
			for (var cabinIndex = 0; cabinIndex < cabins.length; cabinIndex++) {
				var rows = cabins[cabinIndex].rowInfo;
				for (var rowIndex = 0; rowIndex < rows.length; rowIndex++) {
					var seats = rows[rowIndex].seatInfo;
					for (var seatIndex = 0; seatIndex < seats.length; seatIndex++) {
						var seat = seats[seatIndex];
						if (seat.seatNumber === seatNumber) {
							return seat;
						}
					}
				}
			}
		}
		return null;
	},

	initializeSvgSeatmap: function (onlyActiveSeatmap) {
		ACC.seat.bindActiveSeatmapSelectSeat();
		ACC.seat.bindDesktopHoverInfo();
		if (onlyActiveSeatmap == "false") {
			ACC.seat.resetPassengerInfoData();
		}
		ACC.seat.populatePassengerSelectedSeats(onlyActiveSeatmap);
		ACC.seat.populateSelectedSeats(onlyActiveSeatmap);
		ACC.seat.disableUnavailableSeat();
	},

	initializeJsBinding: function () {
		ACC.seat.bindDeckChangeButton();
		ACC.seat.bindCarousel();
		ACC.seat.bindPassengerIconChange();
		ACC.seat.bindSeatDeselectionButton();
		ACC.seat.bindConfirmSeatsButtonClick();
	},

	bindActiveSeatmapSelectSeat: function () {
		var activeSeatmaps = $(document).find('.y_transportOffering.item.active').find(".y_seatMapSVGWrap");
		activeSeatmaps.each(function () {
			var seats = $(this).find(".seat");
			seats.each(function () {
				$(this).unbind();
				$(this).on("click", function () {
					if ($('html').hasClass('y_isMobile')) {
						ACC.seat.mobileSelectSeat(this);
					} else {
						ACC.seat.desktopSelectSeat(this);
					}
				});
			});
		});
	},

	bindDesktopHoverInfo: function () {
		$(document).on('mouseenter', '.seat', function () {
			$(document).find('.y_transportOffering.item.active').find(".seatInfo-Box").show();
		});

		$(document).on('mouseleave', '.seat', function () {
			$(document).find('.y_transportOffering.item.active').find(".seatInfo-Box").hide();
			$(this).removeClass("hover");
		});

		$(document).on('mousemove', '.seat', function (e) {
			var seatNumber = $(this).data("seat-id");

			var seatInfoBox = $(document).find('.y_transportOffering.item.active').find(".seatInfo-Box");
			var seatInfoBoxContainer = $(document).find('.y_transportOffering.item.active').find('.seatmapSVG');

			var rect = seatInfoBoxContainer[0].getBoundingClientRect();
			var x = e.clientX + $(seatInfoBoxContainer).scrollLeft() - rect.left + 5;
			var y = e.clientY + $(seatInfoBoxContainer).scrollTop() - rect.top + 10;

			seatInfoBox.css({'top': y, 'left': x});

			seatInfoBox.find("#y_seatNumberOfSelected").text(seatNumber);
			var currentSeatMap = ACC.seat.seatMapJsonObj.seatMap[ACC.seat.currentSeatMapIndex];
			var seat = ACC.seat.getSeatInfoData(currentSeatMap, seatNumber);
			seatInfoBox.find("#y_seatPriceOfSelected").text(ACC.seat.getSeatPriceInfo(seat));
			seatInfoBox.find("#y_seatFeatureOfSelected").html(ACC.seat.getSeatFeatureData(seat));

			var findPassengerID = $(document).find(".y_svgdiv").find("div.item.active").find(".active").parent().attr('id');

			if (!$(this).hasClass(findPassengerID) && $(this).hasClass('selected')) {
				$(this).attr('cursor', 'not-allowed');
			}
			else {
				$(this).attr('cursor', 'pointer');
				$(this).addClass("hover");
			}
		});
	},

	populateSelectedSeats: function (onlyActiveSeatmap) {
		var seatMapTabs;
		if (onlyActiveSeatmap === "true") {
			seatMapTabs = $(document).find('.y_transportOffering.item.active');
		} else {
			seatMapTabs = $(document).find('.y_transportOffering.item');
		}

		seatMapTabs.each(function () {
			var activeItem = $(this);
			var seats = activeItem.find(".seat");
			ACC.seat.resetSeatSelection(activeItem, seats);

			var passengerSeatNumberInfos = activeItem.find('.y_selectedSeatNumber').not(".y_seatIconIsEmpty");
			for (var i = 0; i < passengerSeatNumberInfos.length; i++) {
				var passengerSeatNumberInfo = $(passengerSeatNumberInfos[i]);
				var isActive = passengerSeatNumberInfo.closest('div').parent().find(".y_seatIcon").hasClass('active');
				var passengerSeatNumber = passengerSeatNumberInfo.text();
				var passengerSeatNumberID = passengerSeatNumberInfo.closest('div').parent().attr('id');

				for (var j = 0; j < seats.length; j++) {
					if (passengerSeatNumber === $(seats[j]).data("seat-id")) {
						$(seats[j]).addClass("selected");
						$(seats[j]).addClass(passengerSeatNumberID);
						if (isActive) {
							$(seats[j]).addClass("seatActive");
						}
					}
				}
			}

		});
	},

	resetSeatSelection: function (activeItem, seats) {
		var passengerIds = activeItem.find(".y_seatInfoData").children();
		passengerIds.each(function () {
			seats.removeClass($(this).attr("id"));
		});
		seats.removeClass("selected seatActive");
	},

	populatePassengerSelectedSeats: function (onlyActiveSeatmap) {
		if (ACC.seat.seatMapJsonObj.seatMap) {
			for (var seatMapIndex = 0; seatMapIndex < ACC.seat.seatMapJsonObj.seatMap.length; seatMapIndex++) {
				if (onlyActiveSeatmap === "true" && seatMapIndex !== ACC.seat.currentSeatMapIndex) {
					continue;
				}
				var selectedSeats = ACC.seat.seatMapJsonObj.seatMap[seatMapIndex].selectedSeats;
				for (var selectedSeatCounter in selectedSeats) {
					var selectedSeat = selectedSeats[selectedSeatCounter];
					ACC.seat.setPassengerSeatFromCart(seatMapIndex, selectedSeat);
				}
			}
		}
	},

	setPassengerSeatFromCart: function (seatMapIndex, selectedSeat) {
		var passengerDivs = $(".tab-" + seatMapIndex + "-passenger-seat");
		var seatNumber = selectedSeat.seatNumber;
		passengerDivs.each(function () {
			if ($(this).data('passengertype') == selectedSeat.traveller.label &&
					$(this).data('transportofferingnumber') == selectedSeat.transportOffering.code) {
				var seatIconSeatNumber = $(this).find('.y_seatIconSeatNumber');
				seatIconSeatNumber.text(seatNumber);
				seatIconSeatNumber.removeClass('y_seatIconIsEmpty');

				var currentSeatMap = ACC.seat.seatMapJsonObj.seatMap[seatMapIndex];
				var seat = ACC.seat.getSeatInfoData(currentSeatMap, seatNumber);
				$(this).find('.y_seatIconSeatPrice').text(ACC.seat.getSeatPriceInfo(seat));
				$(this).find('div.seat-icon').addClass("selected");
				var passengerId = $(this).attr('id');
				var activeSeatmap = $(document).find('.y_transportOffering.item.active').find(".y_seatMapSVGWrap").parent();
				var seats = activeSeatmap.find(".seat");
				for (var j = 0; j < seats.length; j++) {
					var seatId = $(seats[j]).data("seat-id");
					if (seatId == seatNumber) {
						$(seats[j]).addClass("selected");
						$(seats[j]).addClass(passengerId);
						if ($(this).find("div.y_seatIcon.active").length > 0) {
							$(seats[j]).addClass("seatActive");
						}
					}
				}
			}
		});
	},

	resetPassengerInfoData: function () {
		$(".y_seatIcon").removeClass("selected");
		var emptySeatText = ACC.addons.travelacceleratorstorefront['ancillary.seatmap.empty.seat.label'];
		$(".y_seatIconSeatNumber").addClass("y_seatIconIsEmpty").text(emptySeatText);
		var emptyPriceText = ACC.addons.travelacceleratorstorefront['ancillary.seatmap.empty.price.label'];
		$(".y_seatIconSeatPrice").text(emptyPriceText);
	},

	disableUnavailableSeat: function () {
		var activeItem = $(document).find('.y_transportOffering.item.active');
		var activeSeatMap = activeItem.find(".y_seatMapSVGWrap").parent();
		var seatMapDetail = ACC.seat.seatMapJsonObj.seatMap[ACC.seat.currentSeatMapIndex].seatMapDetail;
		for (var cabinIdx in seatMapDetail.cabin) {
			var cabin = seatMapDetail.cabin[cabinIdx];
			var unavailableSeats = cabin.seatAvailability;
			if (unavailableSeats) {
				for (var i = 0; i < unavailableSeats.length; i++) {
					var unavailableSeat = unavailableSeats[i];
					var seatNumber = unavailableSeat.seatNumber;
					var seatSVG = activeSeatMap.find(".seat[data-seat-id='" + seatNumber + "']");

					if (unavailableSeat.availabilityIndicator === 'OCCUPIED') {
						var travellerUIDs = activeItem.find(".y_seatInfoData").map(function () {
							return $(this).data("travelleruid");
						}).get();
						if ($.inArray(unavailableSeat.traveller.simpleUID, travellerUIDs) == -1) {
							seatSVG.addClass("unavailable");
						} else {
							seatSVG.addClass("y_previouslyOccupied");
						}
					}
					if (unavailableSeat.availabilityIndicator === 'UNAVAILABLE') {
						seatSVG.addClass("unavailable");
					}
				}
			}

		}
	},

	bindDeckChangeButton: function () {
		$(document).on('click', '.y_deckBtn', function () {
			var activeDeckButtons = $(document).find('.y_transportOffering.item.active').find(".y_deckBtn");
			activeDeckButtons.toggle();
			var seatMapSVGs = $(this).parent().siblings().find(".y_seatMapSVGWrap");
			seatMapSVGs.toggle();
		});
	},

	bindPassengerIconChange: function () {
		$(document).on('click', '.y_seatIcon', function () {
			$(this).closest(".y_travellerIconPanel").find(".y_seatIcon").removeClass("active");
			$(this).addClass("active");
			var activeSeatmap = $(document).find('.y_transportOffering.item.active').find(".y_seatMapSVGWrap").parent();
			var seats = $(activeSeatmap).find(".seat");
			var activeItem = $(document).find(".y_svgdiv").find("div.item.active");
			var findPassengerID = $(activeItem).find(".active").parent().attr('id');
			var seatIcon = $(activeItem).find("div.y_seatIcon");

			for (var j = 0; j < seats.length; j++) {
				var invSeat = $(seats[j]);

				if (invSeat.hasClass(findPassengerID) && seatIcon.hasClass('active')) {
					invSeat.addClass("seatActive");
				}
				else if (!invSeat.hasClass(findPassengerID) && seatIcon.hasClass('active')) {
					invSeat.removeClass("seatActive");
				}
			}
		});
	},

	bindCarousel: function () {
		$('#carousel-generic').on('slid.bs.carousel', function () {
			ACC.seat.currentSeatMapIndex = ++ACC.seat.currentSeatMapIndex % ACC.seat.seatMapJsonObj.seatMap.length;

			var seatMap = ACC.seat.seatMapJsonObj.seatMap[ACC.seat.currentSeatMapIndex];
			var svgWrap = $(".y_svgWrap[data-transportofferingnumber='" + seatMap.transportOffering.code + "']");
			if (svgWrap.find('.y_seatMapSVGWrap').is(":not(:empty)")) {
				return;
			}

			var vehicleCode = seatMap.transportOffering.transportVehicle.vehicleInfo.code;
			$.when(ACC.seat.setDynamicSVG(vehicleCode, ACC.seat.currentSeatMapIndex)).then(function () {
				ACC.seat.initializeSvgSeatmap(true);
			});

		});
	},

	getSeatPriceInfo: function (seat) {
		if (seat && seat.totalFare) {
			return seat.totalFare.totalPrice.formattedValue;
		}
		return ACC.addons.travelacceleratorstorefront['ancillary.seatmap.empty.price.label'];
	},

	getSeatFeatureData: function (seat) {
		var seatFeaturesHtml = "";
		if (seat && seat.seatFeature) {
			var seatFeatures = seat.seatFeature;
			$(seatFeatures).each(function (featureIndex, seatFeature) {
				if (seatFeature.seatFeatureDescription) {
					seatFeaturesHtml = seatFeaturesHtml + "<li>" + seatFeature.seatFeatureDescription + "</li>";
				}
			});
			if (seatFeaturesHtml !== "") {
				return "<ul>" + seatFeaturesHtml + "</ul>";
			}
		}
		return seatFeaturesHtml;
	},

	desktopSelectSeat: function (selectedSeat) {
		var currentSeats = $(selectedSeat).parents('div.y_seatMapSVGWrap').find('.seat');
		var seatID = $(selectedSeat).data("seat-id");
		var currentSeatMap = ACC.seat.seatMapJsonObj.seatMap[ACC.seat.currentSeatMapIndex];
		var seat = ACC.seat.getSeatInfoData(currentSeatMap, seatID);
		var seatUid = seat.seatUid;

		var activeItem = $(document).find(".y_svgdiv").find("div.item.active");
		var seatIconSeatNumber = $(activeItem).find(".active").parent().find('.y_seatIconSeatNumber');
		var seatIconSeatPrice = $(activeItem).find(".active").siblings().find('.y_seatIconSeatPrice');
		var passengerID = $(activeItem).find(".active").parent().attr('id');
		var passengerSeatIcon = $(activeItem).find("div.y_seatIcon.active");
		var addRemoveAction = "";

		if ($(selectedSeat).hasClass('selected')) {
			if (!$(selectedSeat).hasClass(passengerID)) {
				return;
			}
			$(selectedSeat).removeClass("selected");
			$(selectedSeat).removeClass("seatActive");
			$(selectedSeat).removeClass(passengerID);

			passengerSeatIcon.removeClass("selected");
			var emptySeatText = ACC.addons.travelacceleratorstorefront['ancillary.seatmap.empty.seat.label'];
			var emptyPriceText = ACC.addons.travelacceleratorstorefront['ancillary.seatmap.empty.price.label'];
			seatIconSeatNumber.text(emptySeatText);
			seatIconSeatNumber.addClass("y_seatIconIsEmpty");
			seatIconSeatPrice.text(emptyPriceText);
			addRemoveAction = "REMOVE";
		}
		else {
			// deselect previous Seat
			var previousSeat = $(currentSeats).filter("." + passengerID);
			previousSeat.removeClass('selected');
			previousSeat.removeClass("seatActive");
			previousSeat.removeClass(passengerID);

			// select new seat
			seatIconSeatNumber.text(seatID);
			seatIconSeatNumber.removeClass("y_seatIconIsEmpty");
			$(selectedSeat).addClass("selected");
			$(selectedSeat).addClass("seatActive");
			$(selectedSeat).addClass(passengerID);
			passengerSeatIcon.addClass("selected");
			seatIconSeatPrice.text(ACC.seat.getSeatPriceInfo(seat));
			addRemoveAction = "ADD";
		}
		var seatInfoData = $(activeItem).find(".y_travellerIconPanel .y_seatInfoData").find(".active").parents('li');
		ACC.seat.addSelectedSeatInfoData(seatUid, seatInfoData, addRemoveAction);
	},

	mobileSelectSeat: function (selectedSeat) {
		var seatID = $(selectedSeat).data("seat-id");
		var currentSeatMap = ACC.seat.seatMapJsonObj.seatMap[ACC.seat.currentSeatMapIndex];
		var seat = ACC.seat.getSeatInfoData(currentSeatMap, seatID);
		var seatUid = seat.seatUid;

		var activeItem = $(document).find(".y_svgdiv").find("div.item.active");
		var passengerID = $(activeItem).find(".active").parent().attr('id');
		var selectButton = document.getElementsByClassName("y_selectSeatBtn");

		if ($(selectedSeat).hasClass('selected') && !$(selectedSeat).hasClass(passengerID)) {
			return;
		}

		var seatInfoModal = $("#y_seatInfoModal");
		var seatLabel = ACC.addons.travelacceleratorstorefront['ancillary.mobile.view.seat.label'];
		seatInfoModal.find("#y_seatNumberSelectedMobile").text(seatLabel + seatID);
		seatInfoModal.find("#y_seatPriceofSelectedMobile").text(ACC.seat.getSeatPriceInfo(seat));
		seatInfoModal.find("#y_seatFeatureofSelectedMobile").html(ACC.seat.getSeatFeatureData(seat));
		seatInfoModal.modal("show");

		// Toggle between select and deselect text on mobile modal button
		if ($(selectedSeat).hasClass('selected')) {
			$("#y_selectSeatBtnText").hide();
			$("#y_deselectSeatBtnText").show();
			$(selectButton).removeClass("selectBtn");
		}
		else {
			$("#y_selectSeatBtnText").show();
			$("#y_deselectSeatBtnText").hide();
			$(selectButton).addClass("selectBtn");
		}

		$(".y_selectSeatBtn").unbind().click(function () {
			var currentSeats = $(selectedSeat).parents('div.y_seatMapSVGWrap').find('.seat');
			var seatIconSeatNumber = $(activeItem).find(".active").parent().find('.y_seatIconSeatNumber');
			var seatIconSeatPrice = $(activeItem).find(".active").siblings().find('.y_seatIconSeatPrice');
			var passengerSeatIcon = $(activeItem).find("div.y_seatIcon.active");
			var addRemoveAction = "";

			if ($(selectedSeat).hasClass('selected')) {
				if (!$(selectedSeat).hasClass(passengerID)) {
					return;
				}
				$(selectedSeat).removeClass("selected");
				$(selectedSeat).removeClass("seatActive");
				$(selectedSeat).removeClass(passengerID);

				passengerSeatIcon.removeClass("selected");
				var emptySeatText = ACC.addons.travelacceleratorstorefront['ancillary.seatmap.empty.seat.label'];
				var emptyPriceText = ACC.addons.travelacceleratorstorefront['ancillary.seatmap.empty.price.label'];
				seatIconSeatNumber.text(emptySeatText);
				seatIconSeatNumber.addClass("y_seatIconIsEmpty");
				seatIconSeatPrice.text(emptyPriceText);
				addRemoveAction = "REMOVE";
			}
			else {
				// deselect previous seat
				var previousSeat = $(currentSeats).filter("." + passengerID);
				previousSeat.removeClass('selected');
				previousSeat.removeClass("seatActive");
				previousSeat.removeClass(passengerID);

				// select new seat
				seatIconSeatNumber.text(seatID);
				seatIconSeatNumber.removeClass("y_seatIconIsEmpty");
				$(selectedSeat).addClass("selected");
				$(selectedSeat).addClass("seatActive");
				$(selectedSeat).addClass(passengerID);
				passengerSeatIcon.addClass("selected");
				seatIconSeatPrice.text(ACC.seat.getSeatPriceInfo(seat));
				addRemoveAction = "ADD";
			}
			var seatInfoData = $(activeItem).find(".y_travellerIconPanel .y_seatInfoData").find(".active").parents('li');
			ACC.seat.addSelectedSeatInfoData(seatUid, seatInfoData, addRemoveAction);
		});
	},

	addSelectedSeatInfoData: function (seatUid, seatInfoData, addRemoveAction) {
		$(seatInfoData).attr("data-accommodationuid", (addRemoveAction == "ADD") ? seatUid : "");
		$(seatInfoData).attr("data-addremoveaction", addRemoveAction);
	},

	bindConfirmSeatsButtonClick: function () {
		$(document).on('click', '.y_confirmSeatsBtn', function () {
			var addRemoveAccommodations = ACC.seat.getAddRemoveAccommodations();
			if (!$.isEmptyObject(addRemoveAccommodations)) {
				$('#y_selectSeat').attr("disabled", true);

				$.when(ACC.seat.performAddAccommodationsToCart(addRemoveAccommodations)).then(function () {
					return $.when(ACC.seat.getSeatMapConfig(ACC.seat.isAmend)).then(function (seatMapResponse) {
						if (seatMapResponse.seatMap) {
							ACC.seat.seatMapJsonObj = seatMapResponse;
							$('#y_selectSeat').attr("disabled", false);
						}
					});
				});
			}
		});
	},

	getAddRemoveAccommodations: function () {
		var addRemoveAccommodations = [];

		$(".y_travellerIconPanel").each(function (travellerIconPanelIndex, travellerIconPanel) {
			var seatInfoDatas = $(travellerIconPanel).find(".y_seatInfoData");
			seatInfoDatas.each(function (seatInfoDatas, seatInfoData) {
				var accommodationUid = $(seatInfoData).attr("data-accommodationuid");
				var previousSelectedAccommodationUid = $(seatInfoData).attr("data-previousselectedaccommodationuid");

				if ($(seatInfoData).attr("data-addremoveaction").trim() && accommodationUid !== previousSelectedAccommodationUid) {
					var addRemoveAccommodation = {};
					addRemoveAccommodation.transportOfferingCode = $(seatInfoData).attr("data-transportofferingcode");
					addRemoveAccommodation.travellerCode = $(seatInfoData).attr("data-travellercode");
					addRemoveAccommodation.originDestinationRefNo = $(seatInfoData).attr("data-origindestinationrefno");
					addRemoveAccommodation.travelRoute = $(seatInfoData).attr("data-travelroute");
					addRemoveAccommodation.accommodationUid = accommodationUid;
					addRemoveAccommodation.previousSelectedAccommodationUid = previousSelectedAccommodationUid;

					addRemoveAccommodations.push(addRemoveAccommodation);
				}
			});
		});

		return addRemoveAccommodations;
	},

	performAddAccommodationsToCart: function (addRemoveAccommodations) {
		return $.when(ACC.services.addRemoveSeats(addRemoveAccommodations)).then(
				function (response) {
					var jsonData = JSON.parse(response);

					if (jsonData.valid) {
						ACC.reservation.refreshReservationTotalsComponent($("#y_reservationTotalsComponentId").val());
						ACC.reservation.refreshTransportSummaryComponent($("#y_transportSummaryComponentId").val());
					} else {
						var output = "";
						jsonData.errors.forEach(function (error) {
							output = output + "<p>" + error + "</p>";
						});
						$("#y_addProductToCartErrorWithOKModal .y_addProductToCartErrorWithOKBody").html(output);
						$("#y_addProductToCartErrorWithOKModal").modal();
					}
				});
	},

	bindSeatDeselectionButton: function () {
		$(document).on('click', '.y_removeSeatSelection', function () {
			var seatInfoDataDiv = $(this).closest(".y_seatInfoData");
			var selectedSeatNumbers = seatInfoDataDiv.find(".y_selectedSeatNumber").not(".y_seatIconIsEmpty");

			if (selectedSeatNumbers.length === 0) {
				return;
			}

			var seatID = selectedSeatNumbers.first().text();
			var activeSeatmaps = $(document).find('.y_transportOffering.item.active').find(".y_seatMapSVGWrap");
			var seatToDeselect = activeSeatmaps.find(".seat[data-seat-id='" + seatID + "']");

			if (seatToDeselect.length > 0) {
				var currentSeatMap = ACC.seat.seatMapJsonObj.seatMap[ACC.seat.currentSeatMapIndex];
				var seatInfoData = ACC.seat.getSeatInfoData(currentSeatMap, seatID);
				var seatUid = seatInfoData.seatUid;

				$(seatInfoDataDiv).find(".y_seatIcon").removeClass("selected");

				var emptySeatText = ACC.addons.travelacceleratorstorefront['ancillary.seatmap.empty.seat.label'];
				var emptyPriceText = ACC.addons.travelacceleratorstorefront['ancillary.seatmap.empty.price.label'];
				$(seatInfoDataDiv).find(".y_seatIconSeatNumber").text(emptySeatText).addClass("y_seatIconIsEmpty");
				$(seatInfoDataDiv).find(".y_seatIconSeatPrice").text(emptyPriceText);

				$(seatToDeselect).removeClass("selected");
				$(seatToDeselect).removeClass("seatActive");
				var passengerID = $(seatInfoDataDiv).children(":first").attr('id');
				$(seatToDeselect).removeClass(passengerID);

				var seatInfoDataParent = $(this).parents('li');
				ACC.seat.addSelectedSeatInfoData(seatUid, seatInfoDataParent, "REMOVE");
			}
		});
	}

};
