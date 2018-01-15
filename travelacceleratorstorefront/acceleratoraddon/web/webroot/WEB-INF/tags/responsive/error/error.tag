<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ attribute name="errorMessage" required="true" type="java.lang.String" %>
<spring:htmlEscape defaultHtmlEscape="true"/>


<div class="modal-header">
    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
        <span aria-hidden="true">&times;</span>
    </button>
    <h4 class="modal-title" id="errorModalLabel">
        <spring:theme code="text.error.modal.title" text="Invalid credentials"/>
    </h4>
</div>
<div class="modal-body">
    <spring:theme code="${errorMessage}" text="Please enter a valid BookingReference number and Last Name." />
</div>
