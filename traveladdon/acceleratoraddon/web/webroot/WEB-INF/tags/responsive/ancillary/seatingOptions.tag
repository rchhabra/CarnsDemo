<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ attribute name="itineraries" required="true" type="java.util.List"%>
<%@ taglib prefix="ancillary" tagdir="/WEB-INF/tags/addons/traveladdon/responsive/ancillary"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<div id="y_itineraryContainer" class="hidden">${fn:escapeXml(itinerariesJson)}</div>
<div class="panel-heading">
	<h3 class="title">
		<spring:theme code="text.ancillary.seatmap.seating.options" />
	</h3>
</div>
<div class="panel-body">
	<div class="row">
		<div class="col-sm-2 col-xs-4 icon-big">
			<div class="icon-seats"></div>
		</div>
		<div class="col-sm-6 col-xs-8">
			<span>
				<spring:theme code="text.ancillary.seatmap.seating.options.description" text="Select a seat to enjoy additional features or to make sure you stay close to your friends and family." />
			</span>
		</div>
		<div class="col-xs-12 col-sm-4">
			<a id="y_selectSeat" disabled href="#y_ancillaryPanelSeatingOptions" data-toggle="collapse" data-target="#y_ancillaryPanelSeatingOptions" aria-expanded="false" aria-controls="y_ancillaryPanelSeatingOptions" class="btn btn-primary btn-block" data-amend="${fn:escapeXml(amend)}">
				<span id="y_selectSeatSpan">
					<spring:theme code="text.ancillary.seatmap.seating.options.button.selectSeat" text="Select Seat" />
				</span>
				<span id="y_hideSeatSpan" hidden="true">
					<spring:theme code="text.ancillary.seatmap.seating.options.button.hideSelectSeat" text="Hide Seat Selection" />
				</span>
			</a>
		</div>
	</div>
	<!-- seatmap modal -->
	<div class="modal fade modal-extra-details seatmap-modal" id="y_seatmapModal" tabindex="-1" role="dialog" data-backdrop="static" data-keyboard="false">
		<div class="modal-dialog modal-lg" role="document">
			<div class="modal-content tab-wrapper">
				<div id="carousel-generic" class="carousel" data-ride="carousel" data-interval="false">
					<!-- Indicators -->
					<!-- Wrapper for slides -->
					<div class="carousel-inner y_svgdiv">
						<c:set var="transportOfferingIndex" value="0" />
						<c:set var="passengerTabId" value="0"></c:set>
						<c:set var="firstItinerary" value="true"></c:set>
						<c:forEach var="itinerary" items="${itineraries}" varStatus="itineraryIndex">
							<c:forEach var="originDestinationOption" items="${itinerary.originDestinationOptions}">
								<c:if test="${originDestinationOption.active}">
									<c:forEach var="transportOffering" items="${originDestinationOption.transportOfferings}">
										<c:choose>
											<c:when test="${transportOfferingIndex eq 0}">
												<c:set var="active" value="active"></c:set>
											</c:when>
											<c:otherwise>
												<c:set var="active" value=""></c:set>
											</c:otherwise>
										</c:choose>
										<div class="y_transportOffering item ${active}" data-tabid="${transportOfferingIndex}">
											<div class="modal-header">
												<div class="title">
													<h3>${fn:escapeXml(transportOffering.sector.origin.location.name)}&nbsp;${fn:escapeXml(transportOffering.sector.origin.name)}-${fn:escapeXml(transportOffering.sector.destination.location.name)}&nbsp;${fn:escapeXml(transportOffering.sector.destination.name)}</h3>
													<h4>${fn:escapeXml(transportOffering.travelProvider.code)}${fn:escapeXml(transportOffering.number)}</h4>
												</div>
											</div>
											<div class="modal-body">
												<div class="row seatmapContent">
													<div class="info-column col-xs-12 col-sm-4 col-sm-push-8">
														<c:if test="${fn:length(itineraries) > 0}">
															<c:if test="${originDestinationOption.active}">
																<c:set var="adultNum" value="1" />
																<c:set var="childNum" value="1" />
																<c:set var="infantNum" value="1" />
																<c:set var="checked" value="false" />
																<div role="tabpanel" class='tab-pane fade in' id="seating-tab-${fn:escapeXml(passengerTabId)}">
																	<ul class="seatIcon-panel y_travellerIconPanel">
																		<div class="seatIcon-group">
																			<c:if test="${fn:length(itinerary.travellers) > 0}">
																				<c:forEach var="traveller" items="${itinerary.travellers}" varStatus="travellerIndex">
																					<c:set var="travellerCodeMap" value="${travellersNamesMap[traveller.travellerInfo.passengerType.code]}" />
																					<c:set var="disabled" value="${traveller.travellerStatusInfo[transportOffering.code] == 'CHECKED_IN'}" />
																					<c:if test="${traveller.travellerInfo.passengerType.code == 'adult'}">
																						<c:if test="${adultNum==1 && not disabled}">
																							<c:set var="checked" value="true" />
																						</c:if>
																						<ancillary:seatingTravellerDetails transportOfferingCode="${transportOffering.code}" passengerNum="${fn:escapeXml(travellerIndex.index+'')}" tabId="tab-${passengerTabId}-radio-adult-${adultNum}-seat-${traveller.label}" tabName="tab-${passengerTabId}-passenger-seat"
																							travellerData="${traveller}" passengerType="${travellerCodeMap[traveller.label]}" originDestinationRefNumber="${originDestinationOption.originDestinationRefNumber}" travelRoute="${itinerary.route.code}" checked="${checked}" disabled="${disabled}" />
																						<c:if test="${not disabled}">
																							<c:set var="adultNum" value="${adultNum+1}" />
																						</c:if>
																						<c:set var="checked" value="false" />
																					</c:if>
																					<c:if test="${traveller.travellerInfo.passengerType.code == 'child'}">
																						<ancillary:seatingTravellerDetails transportOfferingCode="${transportOffering.code}" passengerNum="${fn:escapeXml(travellerIndex.index+'')}" tabId="tab-${passengerTabId}-radio-child-${childNum}-seat-${traveller.label}" tabName="tab-${passengerTabId}-passenger-seat"
																							travellerData="${traveller}" passengerType="${travellerCodeMap[traveller.label]}" originDestinationRefNumber="${originDestinationOption.originDestinationRefNumber}" travelRoute="${itinerary.route.code}" checked="${checked}" disabled="${disabled}" />
																						<c:set var="childNum" value="${childNum+1}" />
																					</c:if>
																					<c:if test="${traveller.travellerInfo.passengerType.code == 'infant'}">
																						<ancillary:seatingTravellerDetails transportOfferingCode="${transportOffering.code}" passengerNum="${fn:escapeXml(travellerIndex.index+'')}" tabId="tab-${passengerTabId}-radio-infant-${infantNum}-seat-${traveller.label}" tabName="tab-${passengerTabId}-passenger-seat"
																							travellerData="${traveller}" passengerType="${travellerCodeMap[traveller.label]}" originDestinationRefNumber="${originDestinationOption.originDestinationRefNumber}" travelRoute="${itinerary.route.code}" checked="${checked}" disabled="${disabled}" />
																						<c:set var="infantNum" value="${infantNum+1}" />
																					</c:if>
																				</c:forEach>
																			</c:if>
																		</div>
																	</ul>
																	<div class="seatLegend-panel hidden-xs col-sm-12">
																		<div class="col-xs-12">
																			<h1 class="legendTitle">
																				<spring:theme code="text.ancillary.seatmap.legend.title" text="Legend" />
																			</h1>
																		</div>
																		<div class="col-xs-12">
																			<div class="seat-icon-legend available"></div>
																			<div class="seat-icon-legend unavailable"></div>
																			<div class="seat-icon-legend selected"></div>
																			<div class="seat-icon-legend selected"></div>
																		</div>
																	</div>
																</div>
																<c:set var="passengerTabId" value="${passengerTabId+1}"></c:set>
																<c:set var="firstItinerary" value="false"></c:set>
															</c:if>
														</c:if>
													</div>
													<div class="seatmapSVG col-xs-12 col-sm-8 col-sm-pull-4">
														<div hidden class="seatInfo-Box hidden-xs">
															<ul class="seatInfoBoxList">
																<li id="y_seatNumberOfSelected" class="seatInfo"></li>
																<li id="y_seatPriceOfSelected"></li>
																<li id="y_seatFeatureOfSelected"></li>
															</ul>
														</div>
														<div class="changeDeckBtns y_changeDeckBtns">
															<button class="DeckBtn y_deckBtn" hidden>
															</button>
														</div>
														<div class="y_svgWrap" data-transportOfferingNumber="${transportOffering.code}">
															<div class="y_seatMapSVGWrap seatMapSVGWrap"></div>
														</div>
													</div>
												</div>
											</div>
											<c:set var="transportOfferingIndex" value="${transportOfferingIndex+1}" />
										</div>
									</c:forEach>
								</c:if>
							</c:forEach>
						</c:forEach>
					</div>
					<div>
						<a class="left carousel-control y_itemChange" href="#carousel-generic" role="button" data-slide="prev">
							<span class="glyphicon glyphicon-chevron-left"></span>
						</a>
						<a class="right carousel-control y_itemChange" href="#carousel-generic" role="button" data-slide="next">
							<span class="glyphicon glyphicon-chevron-right"></span>
						</a>
						<!-- Controls -->
						<ol class="carousel-indicators hidden-xs">
							<c:forEach begin="0" end="${transportOfferingIndex-1}" varStatus="count">
								<li data-target="#carousel-generic" data-slide-to="${count.index}" class="${count.first ? 'active' : ''} y_carouselIndicator"></li>
							</c:forEach>
						</ol>
					</div>
				</div>
				<div class="modal-footer">
					<div class="seatButton-panel col-xs-12">
						<div class="modalButton col-xs-12 col-sm-4">
							<button type="submit" class="btn btn-primary btn-block y_confirmSeatsBtn" data-dismiss="modal" aria-label="Confirm">
								<spring:theme code="text.ancillary.seatmap.seating.options.button.confirm.selection" text="Confirm" />
							</button>
						</div>
						<div class="modalButton col-xs-12 col-sm-4">
							<button type="submit" class="btn btn-default btn-block" data-dismiss="modal" aria-label="Close">
								<spring:theme code="text.ancillary.seatmap.seating.options.button.cancel.selection" text="Cancel" />
							</button>
						</div>
					</div>
				</div>
				<!-- Carousel -->
			</div>
		</div>
	</div>
	<div id="y_seatInfoModal" class="seatInfoModal modal fade" role="dialog">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">&times;</button>
					<h4 class="modal-title" id="y_seatNumberSelectedMobile">
						<spring:theme code="text.ancillary.seatmap.seating.options.mobile.overlay.title" text="Seat Info" />
					</h4>
				</div>
				<div class="modal-body">
					<ul class="seatInfoList">
						<li id="y_seatPriceofSelectedMobile"></li>
						<li id="y_seatFeatureofSelectedMobile"></li>
					</ul>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default y_selectSeatBtn selectBtn" data-dismiss="modal">
						<span id="y_selectSeatBtnText">
							<spring:theme code="text.ancillary.seatmap.seating.options.button.select" text="Select" />
						</span>
						<span id="y_deselectSeatBtnText" hidden="true">
							<spring:theme code="text.ancillary.seatmap.seating.options.button.deselect" text="Deselect" />
						</span>
					</button>
				</div>
			</div>
		</div>
	</div>
</div>
