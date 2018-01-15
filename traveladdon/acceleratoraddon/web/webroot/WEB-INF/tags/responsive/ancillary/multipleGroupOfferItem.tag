<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ attribute name="offerGroupCode" required="true" type="java.lang.String"%>
<%@ attribute name="offerGroupIndex" required="true" type="java.lang.Integer"%>
<%@ attribute name="tabIndex" required="true" type="java.lang.Integer"%>
<%@ attribute name="itemindex" required="true" type="java.lang.Integer"%>
<%@ attribute name="offerPricingInfos" required="true" type="java.util.List"%>
<%@ attribute name="travellers" required="true" type="java.util.List"%>
<%@ attribute name="transportOfferings" required="true" type="java.util.List"%>
<%@ attribute name="offerPricingInfoSelected" type="java.lang.String"%>
<%@ attribute name="offerPricingInfoSelectedQty" type="java.lang.Integer"%>
<%@ attribute name="offerPricingInfoSelectedTravellers" type="java.lang.String"%>
<%@ attribute name="offerGroupType" required="true" type="java.lang.String"%>
<%@ attribute name="display" type="java.lang.String"%>
<c:set var="selectedOfferItemMinQty" value="0" />
<c:set var="selectedOfferItemMaxQty" value="0" />
<c:if test="${empty offerPricingInfoSelectedQty}">
	<c:set var="offerPricingInfoSelectedQty" value="0" />
</c:if>
<c:set var="passengerTypesRestrictionForSelectedOffer" value="" />
<div id="addAnotherItemDiv_${offerGroupCode}_${offerGroupIndex}_${tabIndex}_${itemindex}" style="display: ${display};">
	<div class="col-xs-4 col-sm-5 form-group">
		<select class="form-control y_selectAncillaryProduct" data-offertabid="${offerGroupIndex}_${tabIndex}" data-offertype='multiple' data-itemindex="${itemindex}">
			<option value="">
				<spring:theme code="text.ancillary.offers.select.item" text="Select an Item" />
			</option>
			<c:forEach var="offerPricingInfo" items="${offerPricingInfos}">
				<c:set var="travellerMinOfferQty" value="${offerPricingInfo.travelRestriction.travellerMinOfferQty >= 0 ? offerPricingInfo.travelRestriction.travellerMinOfferQty : 1}" />
				<c:set var="travellerMaxOfferQty" value="${offerPricingInfo.travelRestriction.travellerMaxOfferQty >= 0 ? offerPricingInfo.travelRestriction.travellerMaxOfferQty : 1}" />
				<c:set var="passengerTypes" value="${offerPricingInfo.travelRestriction.passengerTypes} " />
				<c:set var="promoDesc" value="" />
				<c:forEach var="promotionData" items="${offerPricingInfo.product.potentialPromotions}">
					<c:set var="promoDesc" value="${promotionData.description}" />
				</c:forEach>
				<c:set var="isProductSelected" value="false" />
				<c:if test="${not empty offerPricingInfoSelected && offerPricingInfo.product.code==offerPricingInfoSelected}">
					<c:set var="isProductSelected" value="true" />
					<c:set var="selectedOfferItemMinQty" value="${travellerMinOfferQty}" />
					<c:set var="selectedOfferItemMaxQty" value="${travellerMaxOfferQty}" />
					<c:set var="passengerTypesRestrictionForSelectedOffer" value="${passengerTypes}" />
				</c:if>
				<c:choose>
					<c:when test="${empty offerPricingInfo.travellerBreakdowns}">
						<c:set var="priceInfo" value="${offerPricingInfo.bookingBreakdown.passengerFare.baseFare}" />
					</c:when>
					<c:otherwise>
						<c:set var="priceInfo" value="${offerPricingInfo.travellerBreakdowns[0].passengerFare.baseFare}" />
					</c:otherwise>
				</c:choose>
				<option class="y_OfferProductSelectionCode" value="${offerPricingInfo.product.code}" data-travellerminofferqty="${travellerMinOfferQty}" data-travellermaxofferqty="${travellerMaxOfferQty}" data-passengertypes="${passengerTypes}" data-price="${priceInfo.value}" ${isProductSelected?'selected':''}>${offerPricingInfo.product.name}&nbsp;${priceInfo.formattedValue}&nbsp;${promoDesc}</option>
			</c:forEach>
		</select>
	</div>
	<div class="col-xs-3 col-sm-2 form-group">
		<div class="input-group">
			<span class="input-group-btn">
				<button type="button" class="btn btn-default y_inputNumberChangeBtn increment" disabled="disabled" data-type="minus" data-field="y_ancillaryProductQtyText_${offerGroupIndex}_${tabIndex}_${itemindex}">
					<span class="glyphicon glyphicon-minus">
						<span class="sr-only">
							<spring:theme code="text.ancillary.offers.quantity.selector.remove" text="Remove one" />
						</span>
					</span>
				</button>
			</span>
			<input type="text" name="y_ancillaryProductQtyText_${offerGroupIndex}_${tabIndex}_${itemindex}" id="y_ancillaryProductQtyText_${offerGroupIndex}_${tabIndex}_${itemindex}" data-offertabid="${offerGroupIndex}_${tabIndex}" data-offertype='multiple' class="form-control y_inputNumber"
				value="${offerPricingInfoSelectedQty}" data-defaultvalue="0" data-min="${selectedOfferItemMinQty}" data-max="${selectedOfferItemMaxQty}" readonly>
			<span class="input-group-btn">
				<button type="button" class="btn btn-default y_inputNumberChangeBtn increment" disabled="disabled" data-type="plus" data-field="y_ancillaryProductQtyText_${offerGroupIndex}_${tabIndex}_${itemindex}">
					<span class="glyphicon glyphicon-plus">
						<span class="sr-only">
							<spring:theme code="text.ancillary.offers.quantity.selector.add" text="Add one" />
						</span>
					</span>
				</button>
			</span>
		</div>
	</div>
	<c:if test="${offerGroupType!='PER_LEG' && offerGroupType!='PER_BOOKING'}">
		<div class="col-xs-4 form-group y_travellersSelecter">
			<spring:theme var="multiSelectPlaceHolder" code="text.ancillary.offers.travellers.multiSelect" text="Select Passengers" />
			<select class="multi-select" id="y_travellersSelecter_${offerGroupIndex}_${tabIndex}_${itemindex}" data-offertabid="${offerGroupIndex}_${tabIndex}" data-offertype='multiple' multiple ${empty offerPricingInfoSelected ? 'disabled' :''} placeholder="${multiSelectPlaceHolder}">
				<c:forEach var="traveller" items="${travellers}">
					<c:set var="istravellerSelected" value="false" />
					<c:if test="${fn:contains(offerPricingInfoSelectedTravellers, traveller.label)}">
						<c:set var="istravellerSelected" value="true" />
					</c:if>
					<c:set var="istravellerRestricted" value="false" />
					<c:if test="${not empty fn:trim(passengerTypesRestrictionForSelectedOffer) && !fn:contains(passengerTypesRestrictionForSelectedOffer, traveller.travellerInfo.passengerType.code)}">
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
		</div>
	</c:if>
	<div class="col-xs-1 col-sm-1">
		<a class="btn-delete btn y_ancillaryRemoveItem" data-offertabid="${offerGroupIndex}_${tabIndex}" data-offertype='multiple' data-itemindex="${itemindex}" data-target="addAnotherItemDiv_${offerGroupCode}_${offerGroupIndex}_${tabIndex}_${itemindex}" style="display: ${empty offerPricingInfoSelected ? 'none' :''};">
			<span class="glyphicon glyphicon-remove"></span>
			<span class="sr-only">
				<spring:theme code="text.ancillary.offers.selector.remove" text="Remove" />
			</span>
		</a>
	</div>
</div>