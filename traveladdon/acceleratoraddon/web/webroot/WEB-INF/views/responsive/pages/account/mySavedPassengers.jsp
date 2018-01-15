<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<div class="panel-heading">
	<h3 class="title title-collapse">
		<spring:theme code="text.account.mysavedpassengers" text="My Saved Passengers" />
	</h3>
	<button class="show-hide-button" type="button" data-toggle="collapse" data-target="#saved-passengers" aria-expanded="true" aria-controls="saved-passengers">
		<spring:theme code="text.account.mysavedpassengers.button.collapseExpand" text="Collapse / Expand" />
	</button>
</div>
<div class="panel-body collapse in" id="saved-passengers">
	<div class="fieldset">
		<c:if test="${not empty error}">
			<p>
				<spring:theme code="text.account.mysavedpassengers.error" text="An error occurred while trying to remove Traveller" />
			</p>
		</c:if>
		<c:if test="${not empty removedTraveller}">
			<p>
				<spring:theme code="text.account.mysavedpassengers.traveller" text="Traveller" />
				<strong>${' '}${fn:escapeXml(removedTraveller)}${' '}</strong>
				<spring:theme code="text.account.mysavedpassengers.hasbeenremoved" text="has been removed" />
			</p>
		</c:if>
		<div class="saved-passenger-wrapper">
			<c:choose>
				<c:when test="${not empty savedTravellers}">
					<c:forEach var="savedTraveller" items="${savedTravellers}" varStatus="index">
						<div class="row divided y_myAccountSavedPassenger" id="y_myAccountSavedPassenger${fn:escapeXml(index.count)}">
							<div class="form-group col-xs-12 col-sm-2">
								<label for="sp-title-${fn:escapeXml(index.count)}">
									<spring:theme code="text.account.mysavedpassengers.tableheading.title" text="Title" />
								</label>
								<select name="sp-title-${fn:escapeXml(index.count)}" id="sp-title-${fn:escapeXml(index.count)}" class="col-xs-12 form-control" disabled>
									<option value="${fn:escapeXml(savedTraveller.travellerInfo.title.code)}" selected>${fn:escapeXml(savedTraveller.travellerInfo.title.name)}</option>
								</select>
							</div>
							<div class="form-group col-xs-12 col-sm-3">
								<label for="sp-first-name-${fn:escapeXml(index.count)}">
									<spring:theme code="text.account.mysavedpassengers.tableheading.firstname" text="First Name" />
								</label>
								<input type="text" name="sp-first-name-${fn:escapeXml(index.count)}" id="sp-first-name-${fn:escapeXml(index.count)}" class="col-xs-12 form-control" value="${fn:escapeXml(savedTraveller.travellerInfo.firstName)}" disabled />
							</div>
							<div class="form-group col-xs-12 col-sm-3">
								<label for="sp-last-name-${fn:escapeXml(index.count)}">
									<spring:theme code="text.account.mysavedpassengers.tableheading.lastname" text="Last Name" />
								</label>
								<input type="text" name="sp-last-name-${fn:escapeXml(index.count)}" id="sp-last-name-${fn:escapeXml(index.count)}" class="col-xs-12 form-control" value="${fn:escapeXml(savedTraveller.travellerInfo.surname)}" disabled />
							</div>
							<div class="form-group col-xs-12 col-sm-4 bottom-align-parent">
								<a href="remove-saved-passenger/${fn:escapeXml(savedTraveller.uid)}" class="btn btn-default btn-block bottom-align y_myAccountDeleteBlock" data-target="y_myAccountSavedPassenger2">
									<spring:theme code="sr.text.account.mysavedpassengers.remove" text="Remove" />
									<span class="sr-only"> <spring:theme code="sr.text.account.mysavedpassengers.remove" text="Remove" />
									</span>
								</a>
							</div>
						</div>
					</c:forEach>
				</c:when>
				<c:otherwise>
					<p>
						<spring:theme code="text.account.mysavedpassengers.nosavedpassengers" text="You have no saved passengers" />
					</p>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
</div>
