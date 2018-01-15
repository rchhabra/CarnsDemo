<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/format"%>
<%@ attribute name="packageData" required="true" type="de.hybris.platform.commercefacades.packages.PackageData"%>
<%@ attribute name="urlParameters" required="true" type="java.lang.String"%>
<%@ attribute name="stayDateRange" required="true" type="de.hybris.platform.commercefacades.accommodation.search.StayDateRangeData"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<ul class="clearfix deal-items deal-map" aria-label="package search results">
	<li class="col-xs-12 ${packageData.promoted ? 'promoted' : ''}">
		<span class="sr-only">Promoted hotel</span>
		<div class="col-xs-3 accommodation-image">
			<div class="row">
				<c:forEach var="image" items="${packageData.images}">
					<c:if test="${image.imageType eq 'PRIMARY'}">
						<c:set var="mainImageUrl" value="${image.url}" />
					</c:if>
				</c:forEach>
				<img src="${mainImageUrl}" alt="${fn:escapeXml(packageData.accommodationOfferingName)}">
			</div>
		</div>
		<div class="deal-details col-xs-9">
			<div class="clearfix">
				<div class="col-xs-12 col-sm-8">
					<div class="row">
						<h3>
							${fn:escapeXml(packageData.accommodationOfferingName)}
						</h3>
						<c:forEach var="award" items="${packageData.awards}">
							<div class="col-xs-12 accommodation-star-rating">
								<div class="row">
									<c:if test="${award.type eq 'STAR_RATING'}">
										<c:set var="rating" value="${award.rating gt 15 ? 15 : award.rating}" />
										<span class="sr-only">${fn:escapeXml(rating)}
											<spring:theme code="text.accommodation.listing.star.rating.stars" />
										</span>
										<c:if test="${rating gt 0}">
											<span aria-label="stars">
												<c:forEach begin="0" end="${rating -1}" varStatus="loop">
													<span>&#9733;</span>
												</c:forEach>
											</span>
										</c:if>
									</c:if>
								</div>
							</div>
						</c:forEach>
						<div class="col-xs-12">
							<div class="row">
								<dl>
									<c:forEach var="pricedItinerary" items="${packageData.fareSelectionData.pricedItineraries}">
										<c:set var="journeyType"
                                           value="${(pricedItinerary.originDestinationRefNumber eq 0) ? 'outbound' : 'inbound'}"/>

	                                    <dt class="sr-only"><spring:theme code="text.package.details.journey.${journeyType}.sr"/></dt>
	                                    <dd class="travel-class">${fn:escapeXml(pricedItinerary.itineraryPricingInfos[0].bundleTypeName)}<span class="glyphicon ${(pricedItinerary.originDestinationRefNumber eq 0) ? 'glyphicon-arrow-right' : 'glyphicon-arrow-left'}" aria-hidden="true"></span></dd>
									</c:forEach>
								</dl>
							</div>
						</div>
					</div>
				</div>
				<div class="col-xs-12 col-sm-4">
					<c:if test="${not empty packageData.totalPackagePrice}">
						<div class="price row">
							<p class="h3">
								<span class="sr-only"><spring:theme code="text.page.deallisting.deal.price.sale.sr"/></span>
								<format:price priceData="${packageData.totalPackagePrice}" />
							</p>
						</div>
					</c:if>
				</div>
			</div>
		</div>
		<div class="col-xs-12 option-select">
			<c:url var="detailsPageUrl" value="/package-details/${packageData.accommodationOfferingCode}" />
			<a href="${detailsPageUrl}?${urlParameters}" class="btn btn-secondary col-xs-12">
				<spring:theme code="text.package.listing.button.continue" />
			</a>
		</div>
	</li>
</ul>
