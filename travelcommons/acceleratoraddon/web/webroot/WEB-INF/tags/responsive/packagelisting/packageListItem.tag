<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/format"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ attribute name="packageData" required="true" type="de.hybris.platform.commercefacades.packages.PackageData"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<%@ attribute name="stayDateRange" required="true" type="de.hybris.platform.commercefacades.accommodation.search.StayDateRangeData"%>
<c:set var="maxFractionDigits" value="1" />
<li class="col-xs-12 ${packageData.promoted ? 'promoted' : ''}">
	<div class="col-xs-5 col-sm-3 accommodation-image">
		<div class="row">
			<c:forEach var="image" items="${packageData.images}">
				<c:if test="${image.imageType eq 'PRIMARY'}">
					<c:set var="mainImageUrl" value="${image.url}" />
				</c:if>
			</c:forEach>
			<img src="${mainImageUrl}" alt="${fn:escapeXml(packageData.accommodationOfferingName)}">
		</div>
	</div>
	<div class="deal-details col-xs-offset-5 col-xs-7 col-sm-offset-3 col-sm-9">
		<div class="row">
			<div class="col-xs-12 col-sm-8">
				<div class="row">
					<div class="clearfix">
						<div class="col-xs-12">
							<h3>${fn:escapeXml(packageData.accommodationOfferingName)}</h3>
							<c:forEach var="award" items="${packageData.awards}">
								<c:if test="${award.type eq 'STAR_RATING'}">
									<div class="col-xs-12 col-sm-4 accommodation-star-rating">
										<div class="row">
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
										</div>
									</div>
								</c:if>
							</c:forEach>
						</div>
						<div class="col-xs-12">
							<div class="row">
								<ul class="deal-summary">
									<c:forEach var="info" items="${packageData.rateRange.accommodationInfos}" varStatus="loop">
									<li class="deal-room clearfix">
										<div class="col-xs-12">
											<span class="accommodation-name">${fn:escapeXml(info.accommodationName)}</span>
											<c:set var="roomStayCandidate"
                                               value="${packageSearchResponse.criterion.roomStayCandidates[loop.index]}"/>
											<ul class="clearfix">
												<c:forEach var="passengerTypeQuantity" items="${roomStayCandidate.passengerTypeQuantityList}" varStatus="idx">
													<c:if test="${passengerTypeQuantity.quantity ne 0}">
														<li>
															<span class="sr-only">${fn:escapeXml(passengerTypeQuantity.quantity)}&nbsp;${fn:escapeXml(passengerTypeQuantity.passengerType.name)}</span>
															<span aria-label="people">
	                                                            <c:if test="${!idx.first}">+ &nbsp;</c:if>
	                                                            <c:forEach begin="1" end="${passengerTypeQuantity.quantity}">
	                                                                <span class="person-blue-icon ${passengerTypeQuantity.passengerType.code == 'child' || passengerTypeQuantity.passengerType.code == 'infant' ? 'child-icon' : ''}">&nbsp;</span>
	                                                            </c:forEach>
	                                                        </span>
														</li>
													</c:if>
												</c:forEach>
											</ul>
										</div>
									</li>
									</c:forEach>
								</ul>						
							</div>
						</div>
					</div>
					<div class="col-xs-12">
						<div class="row">								
							<c:forEach var="pricedItinerary" items="${packageData.fareSelectionData.pricedItineraries}">
							<dl class="col-xs-6">
								<c:set var="itinerary" value="${pricedItinerary.itinerary}" />
								<c:set var="transportOfferings" value="${itinerary.originDestinationOptions[0].transportOfferings}" />
								<c:set var="originTO" value="${transportOfferings[0]}" />
								<c:set var="itineraryPricingInfoData" value="${pricedItinerary.itineraryPricingInfos[0]}" />
								<c:set var="bundleTypeName" value="${itineraryPricingInfoData.bundleTypeName}" />
								<c:set var="journeyType" value="${(pricedItinerary.originDestinationRefNumber eq 0) ? 'fareselection.outbound' : 'fareselection.inbound'}" />
								<c:set var="noOfStops" value="${(fn:length(transportOfferings)- 1)}" />
								<c:choose>
									<c:when test="${noOfStops eq 0}">
										<c:set var="stopsInfo" value="Non-Stop" />
									</c:when>
									<c:when test="${noOfStops eq 1}">
										<c:set var="stopsInfo" value="${noOfStops} Stop" />
									</c:when>
									<c:otherwise>${noOfStops}
										<c:set var="stopsInfo" value="${noOfStops} Stops" />
									</c:otherwise>
								</c:choose>
								<dt>
									<spring:theme code="${journeyType}" />
								</dt>
								<fmt:formatDate value="${originTO.departureTime}" var="formattedDepartureDate" type="date" pattern="dd MMM yyyy" />
								<fmt:formatDate value="${originTO.departureTime}" var="formattedDepartureTime" type="time" pattern="HH:mm" />
								<dd class="date">${fn:escapeXml(formattedDepartureDate)}</dd>
								<dd>${fn:escapeXml(formattedDepartureTime)}</dd>
								<dd class="origin">${fn:escapeXml(itinerary.route.origin.name)}</dd>
								<dd class="destination">${fn:escapeXml(itinerary.route.destination.name)}</dd>
								<dd>${fn:escapeXml(stopsInfo)}</dd>
								<dt class="sr-only"><spring:theme code="text.package.details.journey.${journeyType}.sr"/></dt>
								<dd class="travel-class">${fn:escapeXml(bundleTypeName)}</dd>
								</dl>
							</c:forEach>
						</div>
					</div>
				</div>
			</div>
			<div class="col-xs-12 col-sm-4 flex-wrap">
				<div class="reviews text-right">
					<c:set var="ratingMaxValue" value="10" />
					<c:set var="isUserRating" value="" />
					<c:forEach var="award" items="${packageData.awards}">
						<c:if test="${award.type eq 'USER_RATING'}">
							<c:set var="isUserRating" value="true" />
							<p>
								<a href="#" class="y_packageCustomerReview" data-accommodationofferingcode="${packageData.accommodationOfferingCode}">
									<spring:theme code="text.accommodation.listing.customer.review.title" />
									<span class="sr-only">
										<spring:theme code="text.accommodation.listing.customer.review.rating" />
									</span>
									<span>
										<fmt:formatNumber value="${award.rating}" maxFractionDigits="${maxFractionDigits}" />
										/ ${fn:escapeXml(ratingMaxValue)}
									</span>
								</a>
							</p>
						</c:if>
					</c:forEach>
					<c:if test="${empty isUserRating}">
						<p>
							<spring:theme code="text.accommodation.listing.customer.review.noratings" text="No user rating for this property." />
						</p>
					</c:if>
				</div>
				<c:if test="${not empty packageData.totalPackagePrice}">
					<div class="price">
						<p class="h3">
							<span class="sr-only"><spring:theme code="text.page.deallisting.deal.price.sale.sr" text="Sale price" /></span>
							<format:price priceData="${packageData.totalPackagePrice}" />
						</p>
						<p class="total">
							<spring:theme code="text.package.listing.total.price" text="Total price (Flight, hotel, taxes and fees included)" />
						</p>
					</div>
				</c:if>
			</div>
			<div class="col-xs-12 col-sm-4 option-select">
				<div class="row">
					<div class="col-xs-12">
						<c:url var="detailsPageUrl" value="/package-details/${packageData.accommodationOfferingCode}" />
						<a href="${detailsPageUrl}?${urlParameters}" class="btn btn-secondary col-xs-12">
							<spring:theme code="text.package.listing.button.continue" />
						</a>
					</div>
				</div>
			</div>
		</div>
	</div>
</li>
