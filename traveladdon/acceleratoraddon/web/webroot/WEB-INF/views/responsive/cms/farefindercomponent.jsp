<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="farefinder" tagdir="/WEB-INF/tags/addons/traveladdon/responsive/farefinder"%>
<%@ taglib prefix="passenger" tagdir="/WEB-INF/tags/addons/traveladdon/responsive/passenger"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<c:if test="${hideFinderTitle eq true}">
<div role="tabpanel" class="tab-pane active" id="flight">
</c:if>
	<c:url var="fareFinderUrl" value="/view/FareFinderComponentController/search" />
	<form:form commandName="fareFinderForm" action="${fn:escapeXml(fareFinderUrl)}" method="POST" class="fe-validate form-background form-booking-trip" id="y_fareFinderForm">
		<fieldset>
			<c:if test="${hideFinderTitle ne true}">
				<legend class="with-icon heading-booking-trip primary-legend" id="trip-finder-modal-title">
					<c:if test="${!showComponent}">
						<a role="button" data-toggle="collapse" aria-expanded="true" data-target=".panel-modify" class="panel-heading panel-header-link collapsable collapsed">
					</c:if>
					<c:choose>
						<c:when test="${!showComponent}">
							<spring:theme code="text.cms.farefinder.modify.title" text="Modify Booking" />
						</c:when>
						<c:otherwise>
							<spring:theme code="text.cms.farefinder.title" text="Booking Trip" />
						</c:otherwise>
					</c:choose>
					<c:if test="${!showComponent}">
						</a>
					</c:if>
				</legend>
			</c:if>
			<button type="button" class="modal-only close" data-dismiss="modal" aria-label="Close">
				<span aria-hidden="true">&times;</span>
			</button>
			<div class="${ showComponent ? '' : 'panel-modify collapse'} ">
				<div class="fieldset-inner-wrapper">
					<c:set var="formPrefix" value="" />
					<farefinder:transportInfo tripType="${fareFinderForm.tripType}" formPrefix="${formPrefix}" idPrefix="fare" />
					<div class="row input-row less-margin">
						<passenger:passengertypequantity formPrefix="${formPrefix}" passengerTypeQuantityList="${fareFinderForm.passengerTypeQuantityList}" />
						<div class="y_passengerTypeQuantityListError" ></div>
						<div class="col-xs-6 age-info">
							<c:set var="infoText">
								<spring:theme code="text.cms.farefinder.age.info" text="Age refers to the age at the time of arrival date of the last leg of the journey: Adult[16+], Child[2-15], Infant[0-1]" />
							</c:set>
							<a tabindex="0" class="link-trigger" role="button" data-toggle="popover" data-trigger="focus" data-placement="bottom" data-container="body" data-content="${fn:escapeXml(infoText)}">
								<spring:theme code="text.cms.farefinder.ageinfo" text="Age Information" />
								<i class="glyphicon glyphicon-info-sign"></i>
							</a>
						</div>
					</div>
					<%-- CTA (Search Flights) --%>
					<div class="row margin-cancel">
						<div class="col-xs-12 btn-wrapper full-width">
							<form:button class="btn btn-primary col-xs-12 col-sm-6 col-sm-offset-6">
								<spring:theme code="text.cms.farefinder.button.submit" text="Search Flights" />
							</form:button>
						</div>
					</div>
				</div>
				<%--  / CTA (Search Flights) --%>
			</div>
			<%-- / .fieldset-inner-wrapper --%>
		</fieldset>
		<%--  / CTA (Search Flights) --%>
	</form:form>
<c:if test="${hideFinderTitle eq true}">
</div>
</c:if>
