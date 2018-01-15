<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="booking" tagdir="/WEB-INF/tags/addons/traveladdon/responsive/bookingdetails"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ attribute name="reservationItem" required="true" type="de.hybris.platform.commercefacades.travel.reservation.data.ReservationItemData"%>
<spring:htmlEscape defaultHtmlEscape="true" />

<c:set var="displaySharedAncillary" value="false" />
<c:forEach var="originDestinationOfferInfo" items="${reservationItem.reservationPricingInfo.originDestinationOfferInfos}">
	<c:forEach var="offerPricingInfo" items="${originDestinationOfferInfo.offerPricingInfos}">
		<c:if test="${not empty offerPricingInfo.bookingBreakdown}">
			<c:set var="displaySharedAncillary" value="true" />
		</c:if>
	</c:forEach>
</c:forEach>

<c:if test="${displaySharedAncillary}">
	<div class="panel panel-default my-account-secondary-panel collapse in" id="ancillary-shared-section-${fn:escapeXml(reservationItem.originDestinationRefNumber)}">
		<div class="panel-heading divided">
			<h3 class="panel-title">
				<spring:theme code="text.page.managemybooking.reservationitem.ancillarysharedsummary" text="Others" />
			</h3>
		</div>
		<div class="panel-body">
			<div class="fieldset">
				<div class="row">
					<div class="col-xs-12 col-sm-6"">
						<div class="col-xs-12 col-sm-6"></div>
						<div class="col-xs-12 col-sm-6 col-md-6">
							<ul class="list-unstyled">
								<c:forEach var="originDestinationOfferInfo" items="${reservationItem.reservationPricingInfo.originDestinationOfferInfos}">
									<c:forEach var="offerPricingInfo" items="${originDestinationOfferInfo.offerPricingInfos}">
										<c:if test="${not empty offerPricingInfo.bookingBreakdown}">
											<c:set var="includedProductText">
												<spring:theme code="text.page.managemybooking.bookingdetails.included.product" text="This product is included in your bundle." />
											</c:set>
											<c:choose>
												<c:when test="${offerPricingInfo.bundleIndicator == 0}">
													<li>${fn:escapeXml(offerPricingInfo.bookingBreakdown.quantity)}&nbsp;x&nbsp;${fn:escapeXml(offerPricingInfo.product.name)}</li>
												</c:when>
												<c:otherwise>
													<li tabindex="0" class="included-product" role="button" data-toggle="popover" data-trigger="focus" data-placement="bottom" data-container="body" data-content="${fn:escapeXml(includedProductText)}">
														${fn:escapeXml(offerPricingInfo.bookingBreakdown.quantity)}&nbsp;x&nbsp;${fn:escapeXml(offerPricingInfo.product.name)} &nbsp;<i class="glyphicon glyphicon-info-sign"></i>
													</li>
												</c:otherwise>
											</c:choose>
										</c:if>
									</c:forEach>
								</c:forEach>
							</ul>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</c:if>
