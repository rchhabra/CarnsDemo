<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<div class="panel-heading">
	<h3 class="title title-collapse">
		<spring:theme code="text.account.disabilityandmobility" text="Disability And Mobility" />
	</h3>
	<button class="show-hide-button" type="button" data-toggle="collapse" data-target="#preferences" aria-expanded="true" aria-controls="preferences">
		<spring:theme code="text.account.disabilityandmobility.button.collapseExpand" text="Collapse / Expand" />
	</button>
</div>
<div class="panel-body collapse in" id="preferences">
	<form:form action="disability-and-mobility" method="POST">
		<div class="fieldset">
			<div class="row">
				<div class="col-xs-12 col-sm-12 col-md-5 checkbox-label-align">
					<label for="specialAssistance">
						<spring:theme code="text.account.disabilityandmobility.specialassistancerequired" text="Special assistance required?" />
					</label>
					<c:choose>
						<c:when test="${not empty specialServiceRequests}">
							<input name="specialAssistance" type="checkbox" value="true" checked="checked">
						</c:when>
						<c:otherwise>
							<input name="specialAssistance" type="checkbox" value="true">
						</c:otherwise>
					</c:choose>
				</div>
				<div class="form-group col-xs-6 col-sm-6 col-md-3 col-md-offset-1 bottom-align-parent">
					<button type="submit" class="btn btn-primary btn-block bottom-align">
						<spring:theme code="text.profile.preferences.button.save" text="Save" />
					</button>
				</div>
				<div class="form-group col-xs-6 col-sm-6 col-md-3 bottom-align-parent">
					<button type="button" class="btn btn-default btn-block bottom-align backToHome">
						<spring:theme code="text.profile.preferences.button.cancel" text="Cancel" />
					</button>
				</div>
			</div>
		</div>
	</form:form>
</div>
