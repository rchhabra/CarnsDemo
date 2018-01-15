<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ attribute name="visible" required="false" type="java.lang.Boolean"%>
<spring:htmlEscape defaultHtmlEscape="true" />

<%-- Session Expiration Validation Modal --%>
<div class="session-expiration-modal">
<div class="modal ${visible ? 'show' : ''}" id="y_sessionExpirationPreWarningModal" tabindex="-1" aria-labelledby="y_sessionExpirationPreWarningModal">
	<div class="modal-dialog" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title" id="sessionExpirationPreWarningModal">
					<spring:theme code="session.expiration.prewarning.header" />
				</h4>
			</div>
			<div class="modal-body">
				<p>
					<spring:theme code="text.session.expiration.prewarning" />
				</p>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-primary" id = "y_sessionContinue">
					<span aria-hidden="true"><spring:theme code="session.expiration.prewarning.continue.button" /></span>
				</button>
			</div>
		</div>
	</div>
</div>
</div>
