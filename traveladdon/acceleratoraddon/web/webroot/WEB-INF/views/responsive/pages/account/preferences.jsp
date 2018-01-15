<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<div class="panel-heading">
	<h3 class="title title-collapse">
		<spring:theme code="text.account.preferences" text="Preferences" />
	</h3>
	<button class="show-hide-button" type="button" data-toggle="collapse" data-target="#preferences" aria-expanded="true" aria-controls="preferences">
		<spring:theme code="text.account.preferences.button.collapseExpand" text="Collapse / Expand" />
	</button>
</div>
<div class="panel-body collapse in" id="preferences">
	<form:form action="save-preferences" method="POST">
		<fieldset class="fieldset">
			<div class="row">
				<div class="form-group col-xs-12 col-md-5">
					<%-- Language Preference --%>
					<c:if test="${not empty languages}">
						<label for="preferredLanguage">
							<spring:theme code="text.account.preferences.preferredlanguage" text="Preferred Language" />
						</label>
						<select name="preferredLanguage" id="pref-lang" class="col-xs-12 form-control">
							<option value=""><spring:theme code="text.page.travellerdetails.form.option.default" text="Select" /></option>
							<c:forEach items="${languages}" var="language">
								<c:choose>
									<c:when test="${not empty LANGUAGE and LANGUAGE eq language.isocode}">
										<option value="${fn:escapeXml(language.isocode)}" selected="selected">${fn:escapeXml(language.name)}</option>
									</c:when>
									<c:otherwise>
										<option value="${fn:escapeXml(language.isocode)}">${fn:escapeXml(language.name)}</option>
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</select>
					</c:if>
				</div>
			</div>
			<%-- buttons! --%>
			<div class="row">
				<div class="form-group col-xs-12 col-md-5">
					<%-- Airport Preference --%>
					<label for="y_originLocation">
						<spring:theme code="text.account.preferences.preferredairport" text="Preferred Airport" />
					</label>
					<input id="y_originLocation" cssErrorClass="fieldError" class="y_originLocation input-grid col-xs-12 form-control" placeholder="Enter Aiport name, code or location" autocomplete="off" value="${fn:escapeXml(TRANSPORT_FACILITY)}" />
					<input type="hidden" class="y_originLocationCode" name="transportFacilityCode" value="${fn:escapeXml(TRANSPORT_FACILITY_CODE)}" />
					<div id="y_originLocationSuggestions" class="autocomplete-suggestions-wrapper hidden"></div>
				</div>
				<div class="form-group col-xs-6 col-sm-6 col-md-3 col-md-offset-1 bottom-align-parent">
					<button type="submit" class="btn btn-primary btn-block bottom-align">
						<spring:theme code="text.account.preferences.button.save" text="Save" />
					</button>
				</div>
				<div class="form-group col-xs-6 col-sm-6 col-md-3 bottom-align-parent">
					<button type="button" class="btn btn-default btn-block bottom-align backToHome">
						<spring:theme code="text.account.preferences.button.cancel" text="Cancel" />
					</button>
				</div>
			</div>
		</fieldset>
	</form:form>
</div>
