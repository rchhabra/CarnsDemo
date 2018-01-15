<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/format"%>
<%@ attribute name="property" required="true" type="de.hybris.platform.commercefacades.accommodation.PropertyData"%>
<%@ attribute name="urlParameters" required="true" type="java.lang.String"%>
<%@ attribute name="stayDateRange" required="true" type="de.hybris.platform.commercefacades.accommodation.search.StayDateRangeData"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<ul class="clearfix accommodation-map accommodation-items" aria-label="hotel results">
	<li class="col-xs-12 ${property.promoted ? 'promoted' : ''}">
		<span class="sr-only">Promoted hotel</span>
		<div class="col-xs-3 accommodation-image">
			<div class="row">
				<c:forEach var="image" items="${property.images}">
					<c:if test="${image.imageType eq 'PRIMARY'}">
						<c:set var="mainImageUrl" value="${image.url}" />
					</c:if>
				</c:forEach>
				<img src="${mainImageUrl}" alt="${fn:escapeXml(property.accommodationOfferingName)}">
			</div>
		</div>
		<c:set var="hasDiscount" value="${property.rateRange.totalDiscount.value gt 0}" />
		<c:if test="${hasDiscount}">
			<c:set var="notRoundedPercentage" value="${property.rateRange.totalDiscount.value * 100 / property.rateRange.wasRate.value}" />
			<fmt:formatNumber var="roundedPercentage" value="${notRoundedPercentage}" maxFractionDigits="0" />
			<div class="ribbon-container">
				<span>
					<spring:theme code="text.accommodation.listing.saving" arguments="${roundedPercentage}" />
				</span>
			</div>
		</c:if>
		<div class="accommodation-details col-xs-9">
			<div class="clearfix">
				<div class="col-xs-12 col-sm-8">
					<div class="row">
						<h3>
							<c:url var="detailsPageUrl" value="/accommodation-details/${property.accommodationOfferingCode}" />
							<a href="${detailsPageUrl}?${urlParameters}">${fn:escapeXml(property.accommodationOfferingName)}</a>
						</h3>
						<c:forEach var="award" items="${property.awards}">
							<c:if test="${award.type eq 'STAR_RATING'}">
								<div class="col-xs-12 accommodation-star-rating">
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
				</div>
				<c:if test="${not empty property.rateRange}">
					<div class="col-xs-12 col-sm-4">
						<div class="price row">
							<c:if test="${hasDiscount}">
								<p class="previous-price">
									<span class="sr-only">
										<spring:message code="text.accommodation.details.roomtype.price.sale.sr" text="Sale price" />
									</span>
									<format:price priceData="${property.rateRange.actualRate}" />
								</p>
							</c:if>
							<p class="sale-price">
								<span class="sr-only">
									<spring:message code="text.accommodation.details.roomtype.price.sale.sr" text="Sale price" />
								</span>
								<format:price priceData="${property.rateRange.actualRate}" />
							</p>
						</div>
					</div>
				</c:if>
			</div>
		</div>
		<div class="col-xs-12 option-select">
			<c:url var="detailsPageUrl" value="/accommodation-details/${property.accommodationOfferingCode}" />
			<a href="${detailsPageUrl}?${urlParameters}" class="btn btn-secondary col-xs-12">
				<spring:theme code="text.accommodation.listing.button.continue" />
			</a>
		</div>
	</li>
</ul>
