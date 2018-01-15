<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="accommodationDetails" tagdir="/WEB-INF/tags/addons/accommodationaddon/responsive/accommodationdetails"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ attribute name="property" required="true" type="de.hybris.platform.commercefacades.accommodation.PropertyData"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<div class="tab-wrapper full-tabs">
	<ul class="nav nav-tabs" role="tablist">
		<li role="presentation" class="active">
			<a href="#tab-01" aria-controls="tab-01" role="tab" data-toggle="tab">
				<spring:theme code="text.accommodation.details.property.overview" text="Overview" />
			</a>
		</li>
		<li role="presentation">
			<a href="#tab-02" aria-controls="tab-02" role="tab" data-toggle="tab">
				<spring:theme code="text.accommodation.details.property.reviews" text="Reviews" />
			</a>
		</li>
		<c:if test="${not empty googleAPIKey}">
			<li role="presentation">
				<a href="#tab-03" class="y_propertyMapTab" aria-controls="tab-03" role="tab" data-toggle="tab">
					<spring:theme code="text.accommodation.details.property.map" text="Map" />
				</a>
			</li>
		</c:if>
	</ul>
	<div class="tab-content">
		<div role="tabpanel" class="tab-pane fade in active" id="tab-01">
			<accommodationDetails:propertyOverview property="${property}" />
		</div>
		<div role="tabpanel" class="tab-pane fade" id="tab-02">
			<div class="row">
				<div class="col-xs-12">
					<c:set var="totalNumberOfResults" value="${customerReviewSearchPageData.pagination.totalNumberOfResults}" />
					<accommodationDetails:customerReviewProgressBar accommodationCustomerReviews="${customerReviewSearchPageData.results}" property="${property}" totalNumberOfReviews="${totalNumberOfResults}" />
					<div class="reviews-container">
						<div class="col-xs-12">
							<div class="row">
								<accommodationDetails:propertyReview accommodationCustomerReviews="${customerReviewSearchPageData.results}" property="${property}" totalNumberOfReviews="${totalNumberOfResults}" />
							</div>
						</div>
					</div>
					<span class="scroll-fade"></span>
				</div>
			</div>
			<c:if test="${customerReviewSearchPageData.pagination.currentPage < customerReviewSearchPageData.pagination.numberOfPages - 1}">
				<div class="row">
					<div class="test col-xs-12">
						<div class="col-sm-5 col-sm-offset-7">
							<a href="#" class="col-xs-12 btn btn-primary y_accommodationCustomerReviewShowMore" data-pagenumber="0" data-accommodationofferingcode="${fn:escapeXml(property.accommodationOfferingCode)}">
								<spring:theme code="text.accommodation.details.customerreview.showmore.button" />
							</a>
						</div>
					</div>
				</div>
			</c:if>
		</div>
		<c:if test="${not empty googleAPIKey}">
			<div role="tabpanel" class="tab-pane fade" id="tab-03">
				<div class="row">
					<div class="col-xs-12">
						<div class="map-wrap y_propertyPositionMap" data-latitude="${fn:escapeXml(property.position.latitude)}" data-longitude="${fn:escapeXml(property.position.longitude)}" data-accommodationofferingname="${fn:escapeXml(property.accommodationOfferingName)}" data-googleapi="${fn:escapeXml(googleAPIKey)}"></div>
					</div>
				</div>
			</div>
		</c:if>
	</div>
</div>
