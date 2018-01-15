<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="ancillary" tagdir="/WEB-INF/tags/addons/traveladdon/responsive/ancillary"%>
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
	<c:set var="addAnotherItem" value="false" />
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
	<div class="y_ancillaryOfferItem_${offerGroupCode}_${offerGroupIndex}_${tabIndex} form-inline full-width clearfix">
		<c:choose>
			<c:when test="${not  empty offerPricingInfos}">
				<jsp:useBean id="addToCartGroupForms" class="java.util.HashMap" />
				<c:set var="itemindex" value="0" />
				<c:forEach var="offerPricingInfo" items="${offerPricingInfos}">
					<c:set var="productCode" value="${offerPricingInfo.product.code}" />
					<c:choose>
						<c:when test="${empty offerPricingInfo.travellerBreakdowns}">
							<c:set var="priceInfo" value="${offerPricingInfo.bookingBreakdown.passengerFare.baseFare.value}" />
							<c:set var="quantity" value="${offerPricingInfo.bookingBreakdown.quantity}" />
							<c:set var="travellersCount" value="1" />
							<c:set var="travellerLabels" value="${null}" />
							<c:set var="travellerCodes" value="${null}" />
						</c:when>
						<c:otherwise>
							<c:set var="priceInfo" value="${offerPricingInfo.travellerBreakdowns[0].passengerFare.baseFare.value}" />
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
						<c:set var="offerGroupPriceInfo" value="${offerGroupPriceInfo + (priceInfo * quantity * travellersCount)}" />
						<c:set var="itemindex" value="${itemindex+1}" />
						<ancillary:multipleGroupOfferItem offerGroupCode="${offerGroupCode}" offerGroupIndex="${offerGroupIndex}" tabIndex="${tabIndex}" offerPricingInfos="${offerPricingInfos}" offerPricingInfoSelected="${productCode}" offerPricingInfoSelectedQty="${quantity}"
							offerPricingInfoSelectedTravellers="${travellerLabels}" travellers="${travellers}" transportOfferings="${transportOfferings}" itemindex="${itemindex}" offerGroupType="${offerGroupType}" />
					</c:if>
				</c:forEach>
				<!-- *********** printing an unselected offer pricing *********** -->
				<c:set var="offerPricingInfoDataLength" value="${fn:length(offerPricingInfos)}" />
				<c:if test="${itemindex > 0 && offerPricingInfoDataLength > itemindex}">
					<c:set var="addAnotherItem" value="true" />
				</c:if>
				<c:if test="${itemindex eq 0}">
					<c:set var="itemindex" value="${itemindex+1}" />
					<ancillary:multipleGroupOfferItem offerGroupCode="${offerGroupCode}" offerGroupIndex="${offerGroupIndex}" tabIndex="${tabIndex}" offerPricingInfos="${offerPricingInfos}" travellers="${travellers}" transportOfferings="${transportOfferings}" itemindex="${itemindex}"
						offerGroupType="${offerGroupType}" />
				</c:if>
			</c:when>
			<c:otherwise>
				<div class="col-xs-4 col-sm-5 form-group">
					<select class="form-control" name="${offerGroupCode}-tab${tabIndex}ancillary${offerGroupIndex}" ${noAmend ? 'disabled' : '' }>
						<option class="y_noProductsAvailable" disabled>
							<spring:theme code="text.ancillary.offers.product.not.available" text="Not available for this passenger" />
						</option>
					</select>
				</div>
			</c:otherwise>
		</c:choose>
	</div>
	<c:if test="${not  empty offerPricingInfos}">
		<!-- ************* printing add another item ******* -->
		<div class="col-xs-12 form-inline">
			<a class="link-trigger y_ancillaryAddAnotherItem pull-left" id="y_addAnotherItem_${offerGroupIndex}_${tabIndex}" data-itemindex="${itemindex}" data-target="addAnotherItemDiv_${offerGroupCode}_${offerGroupIndex}_${tabIndex}_0" style="display: ${addAnotherItem?'':'none'};">
				<spring:theme code="text.ancillary.offers.add.another.item" text="+ Add another item" />
			</a>
		</div>
	</c:if>
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
							<button class="btn btn-primary btn-block y_applyOffer" id="ancillaryApplyOffer_${offerGroupIndex}_${tabIndex}" data-offertype='multiple' data-offerindex="${offerGroupIndex}" data-tabindex="${tabIndex}" data-transportofferingcodes="${transportOfferingCodes}"
								data-travelroute="${travelRouteCode}" data-origindestinationrefnumber="${originDestinationRefNumber}" disabled>
								<spring:theme code="text.ancillary.offers.selector.addItems" text="Add Item(s)" />
							</button>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<!-- *********** having a hidden unselected offer pricing *********** -->
	<ancillary:multipleGroupOfferItem offerGroupCode="${offerGroupCode}" offerGroupIndex="${offerGroupIndex}" tabIndex="${tabIndex}" offerPricingInfos="${offerPricingInfos}" travellers="${travellers}" transportOfferings="${transportOfferings}" itemindex="0" offerGroupType="${offerGroupType}"
		display="none" />
	<!-- preparing data for addtocartform -->
	<input type="hidden" class="y_selectedAncillaryInfo" data-offertabid="${offerGroupIndex}_${tabIndex}" value="${fn:replace(addToCartGroupForms, '=', ':')}" />
</c:if>