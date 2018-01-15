<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/format"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="deallisting" tagdir="/WEB-INF/tags/addons/travelcommons/responsive/deallisting"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<spring:htmlEscape defaultHtmlEscape="true" />
<c:if test="${not empty packageResponseData}">
	<c:set var="ratingMaxValue" value="10" />
	<c:set var="isUserRating" value="false" />
	<c:set var="propertyData" value="${packageResponseData.accommodationPackageResponse.accommodationAvailabilityResponse}" />
	<c:set var="transportData" value="${packageResponseData.transportPackageResponse.fareSearchResponse}" />
	<c:forEach var="pricedItinerary" items="${transportData.pricedItineraries}">
		<c:if test="${pricedItinerary.originDestinationRefNumber == 0}">
		    <c:set var="departureRoute" value="${pricedItinerary.itinerary.route}" />
		    <c:if test="${fn:length(pricedItinerary.itinerary.originDestinationOptions[0].transportOfferings) gt 0}">
		    	<c:set var="originLocationCity" value="${pricedItinerary.itinerary.originDestinationOptions[0].transportOfferings[0].originLocationCity}" />
		    	<c:set var="destinationLocationCity" value="${pricedItinerary.itinerary.originDestinationOptions[0].transportOfferings[0].destinationLocationCity}" />
		    </c:if>
		</c:if>
	</c:forEach>
	<fmt:formatDate value="${packageResponseData.accommodationPackageResponse.accommodationAvailabilityResponse.roomStays[0].checkInDate}" pattern="dd/MM/yyyy" var="dealStartDate" />
	<fmt:formatDate value="${packageResponseData.accommodationPackageResponse.accommodationAvailabilityResponse.roomStays[0].checkOutDate}" pattern="dd/MM/yyyy" var="dealEndDate" />
	<li class="col-xs-6 col-sm-3 y_dealComponent_${fn:escapeXml(dealComponentId)}">
		<div class="clearfix">
			<div class="accommodation-image">
				<c:forEach var="image" items="${propertyData.accommodationReference.images}">
					<c:if test="${image.imageType eq 'PRIMARY'}">
						<c:set var="mainImageUrl" value="${image.url}" />
					</c:if>
				</c:forEach>
				<img src="${mainImageUrl}" alt="${fn:escapeXml(propertyData.accommodationReference.accommodationOfferingName)}">
			</div>
			<div class="deal-details col-xs-12">
				<div class="row">
					<div class="col-xs-12 summary">
						<div class="clearfix">
							<div class="col-xs-12">
								<div class="row">
									<h3 class="deal-city">${fn:escapeXml(propertyData.accommodationReference.address.town)}</h3>
									<h3>${fn:escapeXml(propertyData.accommodationReference.accommodationOfferingName)}</h3>
									<c:forEach var="award" items="${propertyData.accommodationReference.awards}">
										<c:if test="${award.type eq 'STAR_RATING'}">
											<div class="col-xs-12 accommodation-star-rating">
												<div class="row">
													<c:set var="rating" value="${award.rating gt 15 ? 15 : award.rating}" />
													<span class="sr-only">${fn:escapeXml(rating)}
														<spring:message code="text.page.deallisting.deal.star.rating.stars" />
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
										<c:if test="${award.type eq 'USER_RATING'}">
											<c:set var="isUserRating" value="true" />
											<c:set var="userRatingValue" value="${award.rating}" />
										</c:if>
									</c:forEach>
								</div>
							</div>
							<div class="col-xs-12">
								<div class="row">
									<ul class="deal-summary">
										<li class="summary-room">${fn:escapeXml(propertyData.roomStays[0].roomTypes[0].name)}</li>
										<c:forEach var="ptcFareBreakdownData" items="${transportData.pricedItineraries[0].itineraryPricingInfos[0].ptcFareBreakdownDatas}">
											<c:if test="${ptcFareBreakdownData.passengerTypeQuantity.quantity > 0}">
												<li class="summary-occupant">
													<spring:message code="text.page.deallisting.deal.passenger.info" arguments="${ptcFareBreakdownData.passengerTypeQuantity.quantity},${ptcFareBreakdownData.passengerTypeQuantity.passengerType.name}" />
												</li>
											</c:if>
										</c:forEach>
										<c:if test="${not empty departureRoute}" >
                                            <li class="summary-depart">${fn:escapeXml(departureRoute.origin.code)}</li>
                                            <li class="summary-arrive">
                                                <span class="glyphicon glyphicon-resize-horizontal" aria-hidden="true"></span>
                                                ${fn:escapeXml(departureRoute.destination.code)}
                                            </li>
										</c:if>
										<c:forEach var="standardPackageResponse" items="${packageResponseData.standardPackageResponses}">
											<c:forEach var="packageProduct" items="${standardPackageResponse.packageProducts}">
												<li class="summary-class">${fn:escapeXml(packageProduct.product.name)}</li>
											</c:forEach>
										</c:forEach>
										<li class="summary-class">${fn:escapeXml(transportData.pricedItineraries[0].itineraryPricingInfos[0].bundleTypeName)}</li>
									</ul>
								</div>
							</div>
						</div>
					</div>
					<div class="col-xs-12 reviews">
						<p>
							<c:choose>
								<c:when test="${isUserRating}">
									<a href="#" data-toggle="modal" data-target="#reviewsModal" class="y_customerReview" data-accommodationofferingcode="${fn:escapeXml(propertyData.accommodationReference.accommodationOfferingCode)}">
										<spring:message code="text.page.deallisting.deal.customer.review.title" />
										<span class="sr-only">
											<spring:message code="text.page.deallisting.deal.customer.review.rating" />
										</span>
										<span>
											<fmt:formatNumber value="${userRatingValue}" maxFractionDigits="1" />
											/ ${fn:escapeXml(ratingMaxValue)}
										</span>
									</a>
								</c:when>
								<c:otherwise>
									<spring:message code="text.page.deallisting.deal.customer.review.noratings" text="No user rating for this property." />
								</c:otherwise>
							</c:choose>
						</p>
					</div>
					<div class="col-xs-12 y_packageSearch package-search" style="display: none;">
						<p>
							<a href="" data-toggle="modal" data-target="#more-packages-modal" class="y_search_more_packages">
								<spring:message code="text.page.deallisting.deal.search.message" text="Search for more packages" />
							</a>
						</p>
						<deallisting:dealfinderattributes propertyData="${propertyData}" transportData="${transportData}" returnRouteLocation="${departureRoute.destination}" departureRouteLocation="${departureRoute.origin}" departureLocationCity="${originLocationCity}" returnLocationCity="${destinationLocationCity}" departureDate="${dealStartDate}" returnDate="${dealEndDate}" />
					</div>
						<div class="col-xs-12">
						    <c:choose>
    						    <c:when test="${packageResponseData.available}">
                                    <div class="price">
                                        <c:if test="${packageResponseData.price.totalDiscount.value gt 0}">
                                            <p class="previous-price">
                                                <span class="sr-only">
                                                    <spring:message code="text.page.deallisting.deal.price.previous.sr" text="Previous price" />
                                                </span>
                                                <format:price priceData="${packageResponseData.price.wasRate}" />
                                            </p>
                                        </c:if>
                                        <p class="h3 sale-price">
                                            <span class="sr-only">
                                                <spring:message code="text.page.deallisting.deal.price.sale.sr" text="Sale price" />
                                            </span>
                                            <format:price priceData="${packageResponseData.price.actualRate}" />
                                        </p>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="price">
                                        <p class="h3 sale-price">
                                            <spring:message code="text.page.deallisting.deal.sold.out" text="Sold Out" />
                                        </p>
                                    </div>
                                </c:otherwise>
                            </c:choose>
						</div>
					<deallisting:addDealToCartFormData dealBundleTemplateId = "${component.dealBundleTemplateId}" departureDate="${dealStartDate}" returnDate="${dealEndDate}"/>
				</div>
			</div>
			<div class="y_customerReviewsModal"></div>
		</div>
	</li>
</c:if>
