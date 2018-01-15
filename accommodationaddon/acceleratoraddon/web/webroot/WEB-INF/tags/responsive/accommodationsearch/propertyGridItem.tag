<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/format" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ attribute name="property" required="true" type="de.hybris.platform.commercefacades.accommodation.PropertyData" %>
<%@ attribute name="stayDateRange" required="true"
              type="de.hybris.platform.commercefacades.accommodation.search.StayDateRangeData" %>
<spring:htmlEscape defaultHtmlEscape="true" />
<c:set var="maxFractionDigits" value="1"/>
<li class="col-sm-4 ${property.promoted ? 'promoted' : ''}">
    <div class="clearfix">
        <div class="accommodation-image">
            <div class="row">
                <c:forEach var="image" items="${property.images}">
                    <c:if test="${image.imageType eq 'PRIMARY'}">
                        <c:set var="mainImageUrl" value="${image.url}"/>
                    </c:if>
                </c:forEach>
                <img src="${mainImageUrl}" alt="${fn:escapeXml(property.accommodationOfferingName)}">
            </div>
        </div>
        <c:set var="hasDiscount" value="${property.rateRange.totalDiscount.value gt 0}"/>
        <c:if test="${hasDiscount}">
            <c:set var="notRoundedPercentage"
                   value="${property.rateRange.totalDiscount.value * 100 / property.rateRange.wasRate.value}"/>
            <fmt:formatNumber var="roundedPercentage" value="${notRoundedPercentage}" maxFractionDigits="0"/>
            <div class="ribbon-container">
				<span>
					<spring:theme code="text.accommodation.listing.saving" arguments="${roundedPercentage}"/>
				</span>
            </div>
        </c:if>
        <div class="accommodation-details col-xs-12">
            <div class="row">
                <div class="accommodation-name-awards col-xs-12 summary">
                    <div class="clearfix">
                        <div class="col-xs-12">
                            <div class="row">
                                <h3>
                                    <c:url var="detailsPageUrl"
                                           value="/accommodation-details/${property.accommodationOfferingCode}"/>
                                    <a href="${detailsPageUrl}?${urlParameters}">${fn:escapeXml(property.accommodationOfferingName)}</a>
                                </h3>
                                <c:forEach var="award" items="${property.awards}">
                                    <c:if test="${award.type eq 'STAR_RATING'}">
                                        <div class="col-xs-12 accommodation-star-rating">
                                            <div class="row">
                                                <c:set var="rating" value="${award.rating gt 15 ? 15 : award.rating}"/>
                                                <span class="sr-only">${fn:escapeXml(rating)}
													<spring:theme code="text.accommodation.listing.star.rating.stars"/>
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
                        </div>
                        <div class="col-xs-12 hidden-xs">
                            <div class="row">
                                <address>
                                    ${fn:escapeXml(property.address.formattedAddress)} <br>
                                    <span class="sr-only">
										<spring:theme code="text.accommodation.listing.contact.number"/>
									</span>${fn:escapeXml(property.address.phone)}
                                </address>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="accommodation-duration-price col-xs-12">
                    <div class="price">
                        <c:if test="${not empty property.rateRange}">
                            <c:choose>
                                <c:when test="${hasDiscount}">
                                    <p class="previous-price">
                                        <format:price priceData="${property.rateRange.wasRate}"/>
                                    </p>
                                    <p class="sale-price">
                                        <format:price priceData="${property.rateRange.actualRate}"/>
                                    </p>
                                </c:when>
                                <c:otherwise>
                                    <p>
                                        <format:price priceData="${property.rateRange.actualRate}"/>
                                    </p>
                                </c:otherwise>
                            </c:choose>
                            <c:set var="numberOfNights" value="${stayDateRange.lengthOfStay}" />
                            <ul class="room-types">
                                <li>
                                    <c:choose>
                                        <c:when test="${numberOfNights == '1' }">${fn:escapeXml(numberOfNights)}&nbsp;
                                            <spring:theme code="text.cms.accommodationfinder.accommodation.single.night"
                                                          text="Night"/>
                                        </c:when>
                                        <c:otherwise>${fn:escapeXml(numberOfNights)}&nbsp;
                                            <spring:theme code="text.cms.accommodationfinder.accommodation.multiple.nights"
                                                          text="Nights"/>
                                        </c:otherwise>
                                    </c:choose>
                                </li>
                                <c:forEach var="info" items="${property.rateRange.accommodationInfos}">
                                    <li>${fn:escapeXml(info.cardinality)}x${fn:escapeXml(info.accommodationName)}</li>
                                </c:forEach>
                            </ul>
                        </c:if>
                    </div>
                </div>
                <div class="col-xs-12 reviews">
                    <c:set var="ratingMaxValue" value="10"/>
                    <c:set var="isUserRating" value=""/>
                    <c:forEach var="award" items="${property.awards}">
                        <c:if test="${award.type eq 'USER_RATING'}">
                            <c:set var="isUserRating" value="true"/>
                            <p>
                                <a href="#" class="y_customerReview"
                                   data-accommodationofferingcode="${fn:escapeXml(property.accommodationOfferingCode)}">
                                    <spring:theme code="text.accommodation.listing.customer.review.title"/>
                                    <span class="sr-only">
										<spring:theme code="text.accommodation.listing.customer.review.rating"/>
									</span>
                                    <span>
										<fmt:formatNumber value="${award.rating}" maxFractionDigits="${maxFractionDigits}"/>
										/ ${fn:escapeXml(ratingMaxValue)}
									</span>
                                </a>
                            </p>
                        </c:if>
                    </c:forEach>
                    <c:if test="${empty isUserRating}">
                        <p>
                            <spring:theme code="text.accommodation.listing.customer.review.noratings"
                                          text="No user rating for this property."/>
                        </p>
                    </c:if>
                </div>
                <div class="col-xs-12 pull-right">
                    <c:url var="detailsPageUrl" value="/accommodation-details/${property.accommodationOfferingCode}"/>
                    <a href="${detailsPageUrl}?${urlParameters}" class="btn btn-secondary col-xs-12">
                        <spring:theme code="text.accommodation.listing.button.continue"/>
                    </a>
                </div>
            </div>
        </div>
    </div>
</li>
