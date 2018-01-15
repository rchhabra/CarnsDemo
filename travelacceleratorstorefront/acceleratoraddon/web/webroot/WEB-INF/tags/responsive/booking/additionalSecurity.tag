<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ attribute name="manageMyBookingUrl" required="true" type="java.lang.String"%>
<%@ attribute name="bookingReference" required="true" type="java.lang.String"%>
<%@ attribute name="lastName" required="true" type="java.lang.String"%>
<%@ attribute name="errorMessage" required="false" type="java.lang.String"%>
<%@ attribute name="passengerReference" required="false" type="java.lang.String"%>
<spring:htmlEscape defaultHtmlEscape="true" />

<div class="modal-header">
    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
        <span aria-hidden="true">&times;</span>
    </button>
    <h4 class="modal-title" id="manageBookingLabel">
        <spring:theme code="text.manage.booking.modal.title" text="Manage Booking" />
    </h4>
</div>
<form:form modelAttribute="additionalSecurityForm" method="post" action="${manageMyBookingUrl}" id="y_additionalSecurityForm">
    <form:input path="bookingReference" type="hidden" value="${bookingReference}" />
    <form:input path="lastName" type="hidden" value="${lastName}" />
    <div class="modal-body">
        <c:set var="additionalSecurityTooltip">
            <spring:theme code="text.page.manage.booking.additionalsecurity.tooltip" text="You will find your passenger reference in the booking confirmation email. If you have not receive any email contact the person who placed the booking to find out your passenger reference." />
        </c:set>
        <p>
            <a tabindex="0" class="form-label y_additional-security-popover" role="button" data-toggle="popover" data-trigger="focus" data-placement="bottom" data-container="body" data-content="${fn:escapeXml(additionalSecurityTooltip)}">
                <spring:theme code="text.manage.booking.modal.body" text="Please provide the passenger reference to proceed" />
                &nbsp;<i class="glyphicon glyphicon-info-sign"></i>
            </a>
        </p>
        <div class="row input-row less-margin">
            <form:input path="passengerReference" type="text" value="${passengerReference}" class="col-xs-12 input-grid form-control" placeholder="Passenger reference" />
            <c:if test="${not empty errorMessage}">
                <span class="fe-error">
                    <spring:theme code="${errorMessage}" text="Please enter a valid credentials." />
                </span>
            </c:if>
        </div>
    </div>
    <div class="modal-footer">
        <div class="row">
            <div class="col-xs-12 col-md-6 button-block full-width">
                <div class="form-group col-xs-12 col-sm-6 col-sm-offset-6">
                    <button type="submit" class="btn btn-primary col-xs-12 y_manageBookingSubmit">
                        <spring:theme code="text.manage.booking.modal.button" text="Manage Booking" />
                    </button>
                </div>
            </div>
        </div>
    </div>
</form:form>
