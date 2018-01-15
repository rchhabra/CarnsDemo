<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<c:if test="${not empty packageDetailsChangedFlag && packageDetailsChangedFlag}">
	<div class="row">
		<div class="col-xs-12">
			<div class="alert alert-info alert-dismissible y_cancellationResult" role="alert">
				<button type="button" class="close" data-dismiss="alert" aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
				<p class="y_cancellationResultContent">
					<spring:theme code="text.package.details.changed.message" text="The details of your package have changed. Please review the details before proceeding."/>
				</p>
			</div>
		</div>
	</div>
</c:if>
