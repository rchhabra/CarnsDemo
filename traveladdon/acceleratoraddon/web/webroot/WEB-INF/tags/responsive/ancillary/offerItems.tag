<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ attribute name="offerGroup" required="true" type="de.hybris.platform.commercefacades.travel.ancillary.data.OfferGroupData"%>
<%@ attribute name="itineraries" required="true" type="java.util.List"%>
<%@ attribute name="offerGroupIndex" type="java.lang.Integer"%>
<%@ taglib prefix="ancillary" tagdir="/WEB-INF/tags/addons/traveladdon/responsive/ancillary"%>
<div>
	<h3 class="panel-title title-collapse">
		<a role="button" data-toggle="collapse" aria-expanded="true" aria-controls="panel-${fn:escapeXml(offerGroup.code)}" data-target="#panel-${fn:escapeXml(offerGroup.code)}" class="panel-heading panel-header-link collapsable"> ${fn:escapeXml(offerGroup.name)} </a>
	</h3>
</div>
<div class="panel-body">
	<div class="row">
		<div class="col-sm-2 col-xs-4 icon-big">
			<div class="icon-${fn:escapeXml(fn:toLowerCase(offerGroup.code))}"></div>
		</div>
		<div class="col-sm-10 col-xs-8">
			<span>${fn:escapeXml(offerGroup.description)}</span>
		</div>
	</div>
	<div class="row collapse in" id="panel-${fn:escapeXml(offerGroup.code)}">
		<div class="tab-wrapper left-tabs y_offerGroup">
			<input type="hidden" name="travellerMinOfferGroupQty" value="${fn:escapeXml(offerGroup.travelRestriction.travellerMinOfferQty)}" class="y_travellerMinOfferGroupQty" />
			<input type="hidden" name="travellerMaxOfferGroupQty" value="${fn:escapeXml(offerGroup.travelRestriction.travellerMaxOfferQty)}"
				class="y_travellerMaxOfferGroupQty" />
			<c:if test="${offerGroup.travelRestriction.addToCartCriteria == 'PER_LEG_PER_PAX' || offerGroup.travelRestriction.addToCartCriteria == 'PER_LEG'}">
				<ul class="nav nav-tabs" role="tablist">
					<c:forEach var="originDestinationOfferInfo" items="${offerGroup.originDestinationOfferInfos}" varStatus="idx">
						<li role="presentation" class="${idx.index eq 0 ? 'active' : ''}">
							<a href="#${fn:escapeXml(offerGroup.code)}tab-${fn:escapeXml(idx.index)}" aria-controls="${fn:escapeXml(offerGroup.code)}tab-${fn:escapeXml(idx.index)}" role="tab" data-toggle="tab">
								<c:choose>
									<c:when test="${fn:length(originDestinationOfferInfo.transportOfferings) eq 1}">
										<span class="tab-line-1">
											<c:out value="${originDestinationOfferInfo.transportOfferings[0].sector.origin.code}" /> - <c:out value="${originDestinationOfferInfo.transportOfferings[0].sector.destination.code}" />
									  	</span>
										<span class="tab-line-1">
											<c:out value="${originDestinationOfferInfo.transportOfferings[0].travelProvider.code}${fn:escapeXml(originDestinationOfferInfo.transportOfferings[0].number)}" />
									 	</span>
									</c:when>
									<c:otherwise>
										<c:set var="numberOfTransportOfferings" value="${fn:length(originDestinationOfferInfo.transportOfferings)}" />
										<span class="tab-line-1">
											<c:out value="${originDestinationOfferInfo.transportOfferings[0].sector.origin.code}" /> - <c:out value="${originDestinationOfferInfo.transportOfferings[numberOfTransportOfferings-1].sector.destination.code}" />
										</span>
										<c:forEach begin="0" end="${numberOfTransportOfferings - 1}" varStatus="loop">
											<span class="tab-line-1">
												<c:out value="${originDestinationOfferInfo.transportOfferings[loop.index].travelProvider.code}${fn:escapeXml(originDestinationOfferInfo.transportOfferings[loop.index].number)}" />
											</span>
										</c:forEach>
									</c:otherwise>
								</c:choose>
							</a>
							<input type="hidden" name="travelRouteCode" value="${fn:escapeXml(originDestinationOfferInfo.travelRouteCode)}" />
							<input type="hidden" name="originDestinationRefNumber" value="${fn:escapeXml(originDestinationOfferInfo.originDestinationRefNumber)}" />
						</li>
					</c:forEach>
				</ul>
			</c:if>
			<div class="tab-content group">
				<c:choose>
					<c:when test="${isGroupBooking}">
						<c:if test="${offerGroup.travelRestriction.addToCartCriteria == 'PER_LEG_PER_PAX'}">
							<ancillary:perLegPerPaxGroup offerGroup="${offerGroup}" itineraries="${itineraries}" offerGroupIndex="${offerGroupIndex}" />
						</c:if>
						<c:if test="${offerGroup.travelRestriction.addToCartCriteria == 'PER_LEG'}">
							<ancillary:perLegGroup offerGroup="${offerGroup}" itineraries="${itineraries}" offerGroupIndex="${offerGroupIndex}" />
						</c:if>
						<c:if test="${offerGroup.travelRestriction.addToCartCriteria == 'PER_PAX'}">
							<ancillary:perPaxGroup offerGroup="${offerGroup}" itineraries="${itineraries}" offerGroupIndex="${offerGroupIndex}" />
						</c:if>
						<c:if test="${offerGroup.travelRestriction.addToCartCriteria == 'PER_BOOKING'}">
							<ancillary:perBookingGroup offerGroup="${offerGroup}" itineraries="${itineraries}" offerGroupIndex="${offerGroupIndex}" />
						</c:if>
					</c:when>
					<c:otherwise>
						<c:if test="${offerGroup.travelRestriction.addToCartCriteria == 'PER_LEG_PER_PAX'}">
							<ancillary:perLegPerPax offerGroup="${offerGroup}" itineraries="${itineraries}" />
						</c:if>
						<c:if test="${offerGroup.travelRestriction.addToCartCriteria == 'PER_LEG'}">
							<ancillary:perLeg offerGroup="${offerGroup}" itineraries="${itineraries}" />
						</c:if>
						<c:if test="${offerGroup.travelRestriction.addToCartCriteria == 'PER_PAX'}">
							<ancillary:perPax offerGroup="${offerGroup}" itineraries="${itineraries}" />
						</c:if>
						<c:if test="${offerGroup.travelRestriction.addToCartCriteria == 'PER_BOOKING'}">
							<ancillary:perBooking offerGroup="${offerGroup}" />
						</c:if>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
	</div>
</div>
