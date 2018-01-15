<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<spring:htmlEscape defaultHtmlEscape="true" />
<c:url var="bookingDetailsUrl" value="/manage-booking/booking-details/${checkInResponse.bookingReference}" />
<%-- CHECK IN FAILED COMPONENT --%>
<h2 class="h2"><spring:theme code="checkin.failed.header" text="Check In Failed" /></h2>
<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="title"><spring:theme code="checkin.failed.error.header" text="Check In Errors" /></h3>
	</div>
	<div class="panel-body">
	    <div class="row">
			<div class="col-xs-12">
       			<c:forEach items="${checkInResponse.errors}" var="errorMessage">
       				<p> <spring:theme code="${fn:escapeXml(errorMessage.key)}" text="Error Message" arguments="${fn:escapeXml(errorMessage.value)}"/></p>
    			</c:forEach>
			</div>						
			<div class="col-xs-12">
				<a href="${fn:escapeXml(bookingDetailsUrl)}" class="btn btn-primary"><spring:theme code="checkin.please.try.again.button" text="Please Try Again" /></a>
		    </div>
		</div>
	</div>
</div>

    
