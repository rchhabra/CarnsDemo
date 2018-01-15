<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ attribute name="accommodationCustomerReviews" required="true" type="java.util.List"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<c:set var="maxFractionDigits" value="1" />
<c:forEach var="review" items="${accommodationCustomerReviews}">
	<div class="row">
		<div class="col-xs-8 col-md-10">
			<h3>${fn:escapeXml(review.headline)}</h3>
		</div>
		<div class="col-xs-4 col-md-2 text-right review-score">
			<p>
				<fmt:formatNumber value="${review.rating}" maxFractionDigits="${maxFractionDigits}" />
				<span>
					<span>
						<spring:theme code="text.accommodation.details.customerreview.reviewed" text="Reviewed" />
					</span>
					<c:choose>
						<c:when test="${review.numOfDays == 0}">
							<spring:theme code="text.accommodation.details.customerreview.today" text="today" />
						</c:when>
						<c:when test="${review.numOfDays > 0 and review.numOfDays <= 7}">
							${fn:escapeXml(review.numOfDays)}
							<c:choose>
								<c:when test="${review.numOfDays > 1}">
									<spring:theme code="text.accommodation.details.customerreview.days" text="days" /> 
								</c:when>
								<c:otherwise>
									<spring:theme code="text.accommodation.details.customerreview.day" text="day" /> 
								</c:otherwise>
							</c:choose>
							<spring:theme code="text.accommodation.details.customerreview.ago" text="ago" /> 
						</c:when>
						<c:otherwise>
							<spring:theme code="text.accommodation.details.customerreview.on" text="on" />
							<fmt:formatDate value="${review.date}" var="reviewDateFormatted" pattern="dd/MM/yyyy" />
							${fn:escapeXml(reviewDateFormatted)}
						</c:otherwise>
					</c:choose>
				</span>
			</p>
		</div>
		<div class="col-xs-12">
			<p>${fn:escapeXml(review.comment)}</p>
		</div>
	</div>
</c:forEach>
