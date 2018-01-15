<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="managebookings" tagdir="/WEB-INF/tags/addons/traveladdon/responsive/managebookings"%>
<%@ taglib prefix="booking" tagdir="/WEB-INF/tags/addons/traveladdon/responsive/bookingdetails"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%-- CHECK IN DETAILS COMPONENT --%>
<c:url var="checkInSubmitUrl" value="/manage-booking/check-in/submit/${bookingReference}/${originDestinationRefNumber}" />
<spring:htmlEscape defaultHtmlEscape="true" />
<style>
.errors {
	color: red;
}
</style>
<h2 class="h2">
	<spring:theme code="text.page.checkin.title" text="Check In" />
</h2>
<%-- Itinerary Details --%>
<booking:itineraryitem reservationItem="${reservationData.reservationItems[originDestinationRefNumber]}" cssClass="panel panel-primary panel-list" />
<%-- /Itinerary Details --%>
<%-- Advance Passenger --%>
<form:form commandName="checkInForm" action="${fn:escapeXml(checkInSubmitUrl)}">
	<fieldset>
		<div class="panel panel-primary panel-list">
			<div class="panel-heading">
				<h3 class="title">
					<spring:theme code="text.page.checkin.api.title" text="Advance Passenger Details" />
				</h3>
			</div>
			<div class="panel-body collapse in" id="advance-passenger">
				<input type="hidden" id="todayDate" />
				<c:forEach var="apiForm" items="${checkInForm.apiFormList}" varStatus="i">
					<div class="panel panel-default my-account-secondary-panel panel-spaced">
						<div class="panel-heading">
							<h3 class="panel-title title-collapse">
								<c:forEach items="${travellers}" var="traveller">
									<c:if test="${traveller.uid eq apiForm.uid}">
	                						${fn:escapeXml(traveller.travellerInfo.title.name)}&nbsp;${fn:escapeXml(traveller.travellerInfo.firstName)}&nbsp;${fn:escapeXml(traveller.travellerInfo.surname)}&nbsp;(${fn:escapeXml(traveller.travellerInfo.gender)})</br>
									</c:if>
								</c:forEach>
							</h3>
						</div>
						<div class="panel-body collapse in" id="passenger-${fn:escapeXml(i.count)}-section">
							<div class="col-xs-12">
								<c:set var="currentApiForm" value="apiFormList[${i.index}]" />
								<div id="y_checkInFormErrors">
									<form:errors path="${fn:escapeXml(currentApiForm)}.documentType" cssClass="errors" element="div" />
									<form:errors path="${fn:escapeXml(currentApiForm)}.documentNumber" cssClass="errors" element="div" />
									<form:errors path="${fn:escapeXml(currentApiForm)}.dateOfBirth" cssClass="errors" element="div" />
									<form:errors path="${fn:escapeXml(currentApiForm)}.documentExpiryDate" cssClass="errors" element="div" />
									<form:errors path="${fn:escapeXml(currentApiForm)}.nationality" cssClass="errors" element="div" />
									<form:errors path="${fn:escapeXml(currentApiForm)}.countryOfIssue" cssClass="errors" element="div" />
								</div>
								<form:input path="apiFormList[${fn:escapeXml(i.index)}].uid" type="hidden" value="${fn:escapeXml(apiForm.uid)}" />
								<div class="row">
									<div class="form-group col-xs-12 col-sm-6">
										<label for="ap1-doctype">
											<spring:theme code="text.page.checkin.api.doctype" text="Document Type" />
										</label>
										<form:select path="apiFormList[${fn:escapeXml(i.index)}].documentType" cssClass="col-xs-12 form-control y_apiFormList${fn:escapeXml(i.index)}documentType">
											<form:option value="PASSPORT">
												<spring:theme code="text.page.checkin.api.doctype.passport" text="Passport" />
											</form:option>
											<form:option value="NATIONAL_ID">
												<spring:theme code="text.page.checkin.api.doctype.nationalId" text="National ID Card" />
											</form:option>
										</form:select>
									</div>
									<div class="form-group col-xs-12 col-sm-6">
										<label for="ap1-docnum">
											<spring:theme code="text.page.checkin.api.docnumber" text="Document Number" />
										</label>
										<form:input path="apiFormList[${fn:escapeXml(i.index)}].documentNumber" type="text" cssClass="col-xs-12 y_apiFormList${fn:escapeXml(i.index)}documentNumber" />
									</div>
								</div>
								<div class="row">
									<div class="form-group col-xs-12 col-sm-6 col-sm-push-6">
										<label for="ap1-docexp">
											<spring:theme code="text.page.checkin.api.docExpDate" text="Document Expiry Date" />
										</label>
										<form:input path="apiFormList[${fn:escapeXml(i.index)}].documentExpiryDate" type="text" cssClass="col-xs-12 datepicker y_checkinApDocExpiry y_apiFormList${fn:escapeXml(i.index)}documentExpiryDate" />
									</div>
									<div class="form-group col-xs-12 col-sm-6 col-sm-pull-6">
										<label for="ap1-dob">
											<spring:theme code="text.page.checkin.api.dob" text="Date Of Birth" />
										</label>
										<form:input path="apiFormList[${fn:escapeXml(i.index)}].dateOfBirth" type="text" cssClass="col-xs-12 datepicker y_checkinApDob y_apiFormList${fn:escapeXml(i.index)}dateOfBirth" />
									</div>
								</div>
								<div class="row">
									<div class="form-group col-xs-12 col-sm-6">
										<label for="ap1-nationality">
											<spring:theme code="text.page.checkin.api.nationality" text="Nationality" />
										</label>
										<form:select path="apiFormList[${fn:escapeXml(i.index)}].nationality" cssClass="col-xs-12 form-control y_apiFormList${fn:escapeXml(i.index)}nationality">
											<form:option value="">
												<spring:theme code="text.page.default.select" text="Please select" />
											</form:option>
											<c:forEach items="${nationalities}" var="nationality">
												<form:option value="${fn:escapeXml(nationality.isocode)}">${fn:escapeXml(nationality.name)}</form:option>
											</c:forEach>
										</form:select>
									</div>
									<div class="form-group col-xs-12 col-sm-6">
										<label for="ap1-issue">
											<spring:theme code="text.page.checkin.api.docCountry" text="Country Of Issue" />
										</label>
										<form:select path="apiFormList[${fn:escapeXml(i.index)}].countryOfIssue" cssClass="col-xs-12 form-control y_apiFormList${fn:escapeXml(i.index)}countryOfIssue">
											<form:option value="">
												<spring:theme code="text.page.default.select" text="Please select" />
											</form:option>
											<c:forEach items="${countries}" var="country">
												<form:option value="${fn:escapeXml(country.isocode)}">${fn:escapeXml(country.name)}</form:option>
											</c:forEach>
										</form:select>
									</div>
								</div>
							</div>
						</div>
					</div>
				</c:forEach>
			</div>
		</div>
		<div class="row bottom-row">
			<div class="col-xs-12 col-sm-4 col-sm-offset-8">
				<button type="submit" class="btn btn-primary btn-block" id="checkin-detail-button">
					<spring:theme code="text.page.checkin.api.continue.btn" text="Continue" />
				</button>
			</div>
		</div>
	</fieldset>
</form:form>
