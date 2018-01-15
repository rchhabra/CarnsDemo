<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ attribute name="originDestinationOfferInfo" required="true" type="de.hybris.platform.commercefacades.travel.ancillary.data.OriginDestinationOfferInfoData"%>
<%@ attribute name="offerPricingInfos" required="true" type="de.hybris.platform.commercefacades.travel.ancillary.data.OfferPricingInfoData"%>
<%@ attribute name="travellers" required="true" type="java.util.List"%>
<%@ attribute name="offerGroupCode" required="true" type="java.lang.String"%>
<%@ attribute name="offerGroupName" required="true" type="java.lang.String"%>
<%@ attribute name="tabIndex" required="true" type="java.lang.Integer"%>
<%@ attribute name="offerGroupIndex" required="true" type="java.lang.Integer"%>
<%@ attribute name="travellerMinOfferGroupQty" required="true" type="java.lang.Integer"%>
<%@ attribute name="travellerMaxOfferGroupQty" required="true" type="java.lang.Integer"%>
<%@ attribute name="offerGroupType" required="true" type="java.lang.String"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<c:set var="offerGroupPriceInfo" value="0.00" />
<div class="row input-row">
	<div class="col-xs-12 passenger-extras">
		<c:if test="${not empty originDestinationOfferInfo && empty offerPricingInfos}">
			<c:set var="offerPricingInfos" value="${originDestinationOfferInfo.offerPricingInfos}" />
			<c:set var="travelSectorCode" value="${originDestinationOfferInfo.transportOfferings[0].sector.origin.code}-${originDestinationOfferInfo.transportOfferings[fn:length(originDestinationOfferInfo.transportOfferings)-1].sector.destination.code}" />
			<c:set var="originDestinationRefNumber" value="${originDestinationOfferInfo.originDestinationRefNumber}" />
			<c:set var="travelRouteCode" value="${originDestinationOfferInfo.travelRouteCode}" />
			<c:set var="transportOfferingCodes" value="" />
			<c:forEach var="transportOffering" items="${originDestinationOfferInfo.transportOfferings}" varStatus="transportOfferingLoopStatus">
				<c:set var="transportOfferingCodes" value="${transportOfferingCodes}${(transportOfferingLoopStatus.first?'':',')}${transportOffering.code}" />
			</c:forEach>
			<c:set var="transportOfferings" value="${originDestinationOfferInfo.transportOfferings}" />
		</c:if>
		<jsp:useBean id="addToCartGroupForms" class="java.util.HashMap" />
		<c:forEach var="offerPricingInfo" items="${offerPricingInfos}">
			<c:set var="productCode" value="${offerPricingInfo.product.code}" />
			<c:set var="travellerMinOfferQty" value="${offerPricingInfo.travelRestriction.travellerMinOfferQty >= 0 ? offerPricingInfo.travelRestriction.travellerMinOfferQty : 1}" />
			<c:set var="travellerMaxOfferQty" value="${offerPricingInfo.travelRestriction.travellerMaxOfferQty >= 0 ? offerPricingInfo.travelRestriction.travellerMaxOfferQty : 1}" />
			<c:set var="passengerTypes" value="${offerPricingInfo.travelRestriction.passengerTypes} " />
			<c:set var="promoDesc" value="" />
			<c:forEach var="promotionData" items="${offerPricingInfo.product.potentialPromotions}">
				<c:set var="promoDesc" value="${promotionData.description}" />
			</c:forEach>
			<c:set var="travellerLabels" value="" />
			<c:choose>
				<c:when test="${empty offerPricingInfo.travellerBreakdowns}">
					<c:set var="priceInfo" value="${offerPricingInfo.bookingBreakdown.passengerFare.baseFare}" />
					<c:set var="quantity" value="${offerPricingInfo.bookingBreakdown.quantity}" />
					<c:set var="travellersCount" value="1" />
					<c:set var="travellerLabels" value="${null}" />
					<c:set var="travellerCodes" value="${null}" />
				</c:when>
				<c:otherwise>
					<c:set var="priceInfo" value="${offerPricingInfo.travellerBreakdowns[0].passengerFare.baseFare}" />
					<c:set var="quantity" value="0" />
					<c:set var="travellersCount" value="0" />
					<c:set var="travellerLabels" value="" />
					<c:set var="travellerCodes" value="" />
					<c:forEach var="travellerBreakdown" items="${offerPricingInfo.travellerBreakdowns}">
						<c:set var="quantitySelected" value="${travellerBreakdown.quantity}" />
						<c:set var="istravellerCheckedIn" value="false" />
						<c:forEach var="transportOffering" items="${transportOfferings}">
							<c:if test="${travellerBreakdown.traveller.travellerStatusInfo[transportOffering.code] == 'CHECKED_IN'}">
								<c:set var="istravellerCheckedIn" value="true" />
							</c:if>
						</c:forEach>
						<c:if test="${!istravellerCheckedIn && quantitySelected > 0}">
							<c:set var="quantity" value="${quantitySelected}" />
							<c:set var="travellersCount" value="${travellersCount + 1}" />
							<c:set var="travellerLabels" value="${travellerLabels}${empty travellerLabels ? '' : ','}${travellerBreakdown.traveller.label}" />
							<c:set var="travellerCodes" value="${travellerCodes}${empty travellerCodes ? '' : ','}'${travellerBreakdown.traveller.label}'" />
						</c:if>
					</c:forEach>
				</c:otherwise>
			</c:choose>
			<c:if test="${quantity > 0}">
				<jsp:useBean id="addToCartGroupForm" class="java.util.HashMap" />
				<c:set target="${addToCartGroupForm}" property="qty" value="${quantity}" />
				<c:set target="${addToCartGroupForm}" property="travellerCodes" value="[${travellerCodes}]" />
				<c:set target="${addToCartGroupForms}" property="${productCode}" value="${fn:replace(addToCartGroupForm, '=', ':')}" />
				<c:set var="offerGroupPriceInfo" value="${offerGroupPriceInfo + (priceInfo.value * quantity * travellersCount)}" />
			</c:if>
			<div class="col-xs-6">
				<div class="row">
					<label for="${offerGroupCode}_${offerGroupIndex}_${tabIndex}" data-offertabid="${offerGroupIndex}_${tabIndex}" data-offertype='single' data-value="${productCode}" data-travellerminofferqty="${travellerMinOfferQty}" data-travellermaxofferqty="${travellerMaxOfferQty}"
						data-passengertypes="${passengerTypes}" data-price="${priceInfo.value}"> ${offerPricingInfo.product.name}&nbsp;${priceInfo.formattedValue}&nbsp;${promoDesc} </label>
				</div>
			</div>
			<div class="col-xs-6">
				<div class="row">
					<div class="col-xs-offset-4 col-xs-8">
						<div class="row">
							<c:choose>
								<c:when test="${offerGroupType eq 'PER_LEG' || offerGroupType eq 'PER_BOOKING'}">
									<input type="checkbox" id="y_travellersSelecter_${offerGroupIndex}_${tabIndex}" data-offertabid="${offerGroupIndex}_${tabIndex}" data-offertype='single' ${quantity > 0 ? 'checked':''} />
								</c:when>
								<c:otherwise>
									<spring:theme var="multiSelectPlaceHolder" code="text.ancillary.offers.travellers.multiSelect" text="Select Passengers" />
									<select class="multi-select" id="y_travellersSelecter_${offerGroupIndex}_${tabIndex}" data-offertabid="${offerGroupIndex}_${tabIndex}" data-offertype='single' multiple placeholder="${multiSelectPlaceHolder}">
										<c:forEach var="traveller" items="${travellers}">
											<c:set var="istravellerSelected" value="false" />
											<c:if test="${fn:contains(travellerLabels, traveller.label)}">
												<c:set var="istravellerSelected" value="true" />
											</c:if>
											<c:set var="istravellerRestricted" value="false" />
											<c:if test="${not empty fn:trim(passengerTypes) && !fn:contains(passengerTypes, traveller.travellerInfo.passengerType.code)}">
												<c:set var="istravellerRestricted" value="true" />
											</c:if>
											<c:set var="istravellerCheckedIn" value="false" />
											<c:forEach var="transportOffering" items="${transportOfferings}">
												<c:if test="${traveller.travellerStatusInfo[transportOffering.code] == 'CHECKED_IN'}">
													<c:set var="istravellerCheckedIn" value="true" />
												</c:if>
											</c:forEach>
											<c:if test="${!istravellerCheckedIn}">
												<option ${istravellerSelected?'selected':''} value="${traveller.label}" data-passengertype="${traveller.travellerInfo.passengerType.code}" ${istravellerRestricted?'disabled':''}>${travellersNamesMap[traveller.travellerInfo.passengerType.code][traveller.label]}</option>
											</c:if>
										</c:forEach>
									</select>
								</c:otherwise>
							</c:choose>
						</div>
					</div>
				</div>
			</div>
		</c:forEach>
	</div>
</div>
<c:if test="${not  empty offerPricingInfos}">
	<div class="add-items col-xs-12">
		<div class="row">
			<div class="col-xs-6">
				<div class="row">
					<div id="ancillaryPriceInfo_${offerGroupIndex}_${tabIndex}" data-current-currency-symbol="${currentCurrency.symbol}">
						<spring:theme var="offerGroupTotalText" code="text.ancillary.offers.group.Total" text="Total:" />
						<label data-offer-group-total-text="${offerGroupTotalText}">
							${offerGroupTotalText}&nbsp;${currentCurrency.symbol}${offerGroupPriceInfo}
						</label>
					</div>
				</div>
			</div>
			<div class="col-xs-6">
				<div class="row">
					<div class="col-xs-offset-4 col-xs-8">
						<div class="row">
							<button class="btn btn-primary btn-block y_applyOffer" id="ancillaryApplyOffer_${offerGroupIndex}_${tabIndex}" data-offertype='single' data-offerindex="${offerGroupIndex}" data-tabindex="${tabIndex}" data-transportofferingcodes="${transportOfferingCodes}" data-travelroute="${travelRouteCode}"
								data-origindestinationrefnumber="${originDestinationRefNumber}" disabled>
								<spring:theme code="text.ancillary.offers.selector.addItems" text="Add Item(s)" />
							</button>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<!-- *********** having a hidden unselected offer pricing *********** -->
	<!-- preparing data for addtocartform -->
	<input type="hidden" class="y_selectedAncillaryInfo" data-offertabid="${offerGroupIndex}_${tabIndex}" value="${fn:replace(addToCartGroupForms, '=', ':')}" />
</c:if>