<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ attribute name="actionData" required="true" type="de.hybris.platform.commercefacades.travel.AccommodationBookingActionData"%>
<%@ attribute name="roomStay" type="de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData"%>
<%@ attribute name="accommodationOffering" required="true" type="java.lang.String"%>
<spring:htmlEscape defaultHtmlEscape="true" />

<c:set var="roomStayRefNumber" value="${roomStay.roomStayRefNumber}" />
<c:url var="submitReviewUrl" value="${actionData.actionUrl}" />

<section class="review">
	<div class="row">
		<div class="panel-heading">
			<h3 class="panel-title title-collapse">
			    <spring:theme code="accommodation.booking.details.page.review" />
			</h3>
		</div>
		<div id="review${fn:escapeXml(roomStayRefNumber)}" class="panel-body collapse in">
			<form:form id="y_accommodationReviewForm_rs${fn:escapeXml(roomStayRefNumber)}" modelAttribute="accommodationReviewForm" action="${submitReviewUrl}" method="POST" class="y_addReviewForm">
				<fieldset class="fieldset">
					<legend class="sr-only">
						<spring:theme code="accommodation.booking.details.page.review.comments" />
					</legend>
					<div class="form-group col-xs-12">
						<div class="row">
							<p>
								<spring:theme code="accommodation.booking.details.page.review.intro" arguments="${roomStay.roomTypes[0].name}" />
							</p>
						</div>
						<div class="row">
							<label for="review-title">Title of your review</label>
							<form:input class="col-xs-12 form-control" id="review-title${fn:escapeXml(roomStayRefNumber)}" path="headline" type="text" />
						</div>
						<div class="row">
							<label for="review-details">Your review</label>
							<form:textarea class="col-xs-12 form-control" id="review-details${fn:escapeXml(roomStayRefNumber)}" path="comment" placeholder="Please let us know what you think"/>
						</div>
						<form:input type="hidden" path="roomStayRefNumber" value="${roomStayRefNumber}" />
						<form:input type="hidden" path="accommodationCode" value="${roomStay.roomTypes[0].code}" />
						<form:input type="hidden" path="accommodationOfferingCode" value="${accommodationOffering}" />
					</div>
				</fieldset>
				<fieldset class="fieldset">
					<legend class="sr-only">
						<spring:theme code="accommodation.booking.details.page.review.score" />
					</legend>
					<div class="row">
						<ul class="col-xs-12 col-sm-6 review-score">
							<c:forEach varStatus="status" begin="0" end="10">
								<li><form:radiobutton path="rating" id="review-score_${roomStayRefNumber}_${status.index}" value="${status.index}" /><label for="review-score_${roomStayRefNumber}_${status.index}">${status.index}</label></li>
							</c:forEach>
						</ul>
						<div class="form-group col-xs-12 col-sm-offset-2 col-sm-4">
							<button type="submit" class="btn btn-primary btn-block">
								<spring:theme code="accommodation.booking.details.page.review.submit" />
							</button>
						</div>
					</div>
				</fieldset>
			</form:form>
		</div>
	</div>
</section>
