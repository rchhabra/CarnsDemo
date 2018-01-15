<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="travellerdetails" tagdir="/WEB-INF/tags/addons/traveladdon/responsive/travellerdetails"%>
<%@ attribute name="adultsTitles" required="true" type="java.util.ArrayList"%>
<%@ attribute name="childrenTitles" required="true" type="java.util.ArrayList"%>
<%@ attribute name="reasonFortravel" required="true" type="java.util.ArrayList"%>
<%@ attribute name="savedTravellers" required="true" type="java.util.ArrayList"%>
<%@ attribute name="travellerForms" required="true" type="java.util.ArrayList"%>
<%@ attribute name="reservationData" required="true" type="de.hybris.platform.commercefacades.travel.reservation.data.ReservationData"%>
<%@ attribute name="isCollapsable" required="true" type="java.lang.Boolean"%>
<%@ attribute name="showPhoneNumber" required="true" type="java.lang.Boolean"%>
<%@ attribute name="isCustomer" required="true" type="java.lang.Boolean"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<c:forEach var="formValues" items="${travellerForms}" varStatus="formValuesIdx">
	<c:set var="travellerTitles" scope="page" value="${adultsTitles}" />
	<c:if test="${fn:containsIgnoreCase(formValues.label, 'child')}">
		<c:set var="travellerTitles" scope="page" value="${childrenTitles}" />
	</c:if>
	<c:if test="${fn:containsIgnoreCase(formValues.label, 'infant')}">
		<c:set var="travellerTitles" scope="page" value="${childrenTitles}" />
	</c:if>
	<c:set var="travellerForm" value="travellerForms[${formValuesIdx.index}]" />
	<c:set var="idx" value="${formValuesIdx.index}" />
	<c:set var="formErrors">
		<form:errors path="travellerForms[${fn:escapeXml(idx)}].passengerInformation.*" />
	</c:set>
	<c:if test="${not empty formErrors}">
		<div id="formErrors" class="alert alert-danger">
			<form:errors path="${fn:escapeXml(travellerForm)}.passengerInformation.title" element="div" />
			<form:errors path="${fn:escapeXml(travellerForm)}.passengerInformation.firstname" element="div" />
			<form:errors path="${fn:escapeXml(travellerForm)}.passengerInformation.lastname" element="div" />
			<form:errors path="${fn:escapeXml(travellerForm)}.passengerInformation.gender" element="div" />
			<form:errors path="${fn:escapeXml(travellerForm)}.passengerInformation.frequentFlyerMembershipNumber" element="div" />
			<form:errors path="${fn:escapeXml(travellerForm)}.passengerInformation.reasonForTravel" element="div" />
			<form:errors path="${fn:escapeXml(travellerForm)}.passengerInformation.email" element="div" />
			<form:errors path="${fn:escapeXml(travellerForm)}.passengerInformation.contactNumber" element="div" />
		</div>
	</c:if>
	<div id="passenger-info-${fn:escapeXml(idx)}" class="panel panel-default y_passengerInformationForm">
		<form:hidden path="${fn:escapeXml(travellerForm)}.formId" value="${fn:escapeXml(idx)}" />
		<form:hidden path="${fn:escapeXml(travellerForm)}.uid" value="${fn:escapeXml(formValues.uid)}" />
		<form:hidden path="${fn:escapeXml(travellerForm)}.passengerInformation.passengerTypeCode" value="${fn:escapeXml(formValues.passengerInformation.passengerTypeCode)}" class="y_passengerType" />
		<form:hidden path="${fn:escapeXml(travellerForm)}.passengerInformation.passengerTypeName" value="${fn:escapeXml(formValues.passengerInformation.passengerTypeName)}" />
		<form:hidden path="${fn:escapeXml(travellerForm)}.label" value="${fn:escapeXml(formValues.label)}" />
		<form:hidden path="${fn:escapeXml(travellerForm)}.selectedSavedTravellerUId" class="y_passengerSelectedSavedTravellerUId" />
		<form:hidden path="${fn:escapeXml(travellerForm)}.passengerInformation.selectedSavedTravellerUId" class="y_passengerSelectedSavedTravellerUId" />
		<div class="panel-heading">
			<c:set var="travellerCodeMap" value="${travellersNamesMap[formValues.passengerInformation.passengerTypeCode]}" />
			<h3 class="panel-title ${isCollapsable ? '' :'title-collapse'}">${fn:escapeXml(travellerCodeMap[formValues.label])}</h3>
			<c:if test="${isCollapsable}">
				<button class="show-hide-button" type="button" data-toggle="collapse" data-target="#traveller-details-${fn:escapeXml(idx)}" aria-expanded="true" aria-controls="traveller-details1">Collapse / Expand</button>
			</c:if>
		</div>
		<div id="traveller-details-${fn:escapeXml(idx)}" class="panel-body collapse in">
			<travellerdetails:ancillaries reservationData="${reservationData}" travellerCode="${formValues.label}" />
			<div class="panel-form">
				<div class="fieldset">
					<div class="row">
						<div class="form-group col-sm-6 col-md-2">
							<label>
								<spring:theme code="text.page.travellerdetails.form.heading.title" text="Title" />
							</label>
							<form:select path="${fn:escapeXml(travellerForm)}.passengerInformation.title" multiple="no" class="y_passengerTitle form-control">
								<form:option value="" selected="${empty formValues.passengerInformation.title?'selected':''}">
									<spring:theme code="text.page.travellerdetails.form.option.default" text="Select" />
								</form:option>
								<c:forEach var="travellerTitleItem" items="${travellerTitles}" varStatus="travellerTitleIdx">
									<form:option value="${fn:escapeXml(travellerTitleItem.code)}" selected="${not empty formValues.passengerInformation.title and formValues.passengerInformation.title==travellerTitleItem.name ? 'selected' : ''}"> ${fn:escapeXml(travellerTitleItem.name)} </form:option>
								</c:forEach>
							</form:select>
						</div>
						<div class="form-group col-xs-12 col-sm-6 col-md-4">
							<div id="td-traveller-gender1" class="form-label">
								<spring:theme code="text.page.travellerdetails.form.heading.gender" text="Gender" />
							</div>
							<label class="radio-inline col-xs-3 col-sm-4">
								<form:radiobutton path="${fn:escapeXml(travellerForm)}.passengerInformation.gender" class="y_passengerGender" value="male" checked="${empty formValues.passengerInformation.gender or formValues.passengerInformation.gender=='male'?'checked':''}" />
								<spring:theme code="text.page.travellerdetails.form.heading.gender.male" text="Male" />
							</label>
							<label class="radio-inline col-xs-3 col-sm-4">
								<form:radiobutton path="${fn:escapeXml(travellerForm)}.passengerInformation.gender" class="y_passengerGender" value="female" checked="${formValues.passengerInformation.gender=='female'?'checked':''}" />
								<spring:theme code="text.page.travellerdetails.form.heading.gender.female" text="Female" />
							</label>
						</div>
					</div>
					<div class="row">
						<div class="form-group col-xs-12 col-sm-6 col-md-4">
							<label>
								<spring:theme code="text.page.travellerdetails.form.heading.firstname" text="Firstname" var="firstnamePlaceholder" />
								${fn:escapeXml(firstnamePlaceholder)}
							</label>
							<form:input path="${fn:escapeXml(travellerForm)}.passengerInformation.firstname" id="y_travellerdetails_${fn:escapeXml(idx)}_first_name" type="text" class="y_passengerFirstname form-control ${isCustomer ? 'y_customer' : ''}" placeholder="${fn:escapeXml(firstnamePlaceholder)}" autocomplete="off"/>
							<div id="y_passengerFirstNameSuggestions" class="autocomplete-suggestions-wrapper traveller-autocomplete hidden"></div>
						</div>
						<div class="form-group col-xs-12 col-sm-6 col-md-4">
							<label>
								<spring:theme var="lastnamePlaceholder" code="text.page.travellerdetails.form.heading.lastname" text="Lastname" />
								${fn:escapeXml(lastnamePlaceholder)}
							</label>
							<form:input path="${fn:escapeXml(travellerForm)}.passengerInformation.lastname" id="y_travellerdetails_${fn:escapeXml(idx)}_last_name" type="text" value="${fn:escapeXml(formValues.passengerInformation.lastname)}" class="y_passengerLastname form-control ${isCustomer ? 'y_customer' : ''}" placeholder="${fn:escapeXml(lastnamePlaceholder)}" autocomplete="off"/>
							<div id="y_passengerLastNameSuggestions" class="autocomplete-suggestions-wrapper traveller-autocomplete hidden"></div>
						</div>
						<input type="hidden" id="y_selectedUid" />
					</div>
					<div class="row y_frequentFlyerInfo">
						<div class="form-group col-xs-12 col-md-4">
							<div id="td-traveller-frequent-flyer1" class="form-label">
								<spring:theme code="text.page.travellerdetails.form.heading.frequentflyermember" text="Are you a member of the frequent flyer programme?" />
							</div>
							<label class="radio-inline col-xs-3 col-sm-2 col-md-4">
								<form:radiobutton path="${fn:escapeXml(travellerForm)}.passengerInformation.frequentFlyer" class="y_travellerDetailsMembershipYesBtn" value="yes" selected="${fn:escapeXml(formValues.passengerInformation.frequentFlyer ? 'selected' : '')}" />
								<spring:theme code="text.page.travellerdetails.form.option.yes" text="Yes" />
							</label>
							<label class="radio-inline col-xs-3 col-sm-2 col-md-4">
								<form:radiobutton path="${fn:escapeXml(travellerForm)}.passengerInformation.frequentFlyer" class="y_travellerDetailsMembershipNoBtn" value="no" selected="${formValues.passengerInformation.frequentFlyer ? '' : 'selected'}" />
								<spring:theme code="text.page.travellerdetails.form.option.no" text="No" />
							</label>
							<c:if test="${formValues.passengerInformation.frequentFlyer}">
								<c:set var="disabled" value="false" />
								<c:set var="display" value="display: block;" />
							</c:if>
							<c:if test="${not formValues.passengerInformation.frequentFlyer}">
								<c:set var="disabled" value="true" />
								<c:set var="display" value="display: none;" />
							</c:if>
						</div>
						<div class="form-group col-xs-12 col-sm-6 y_membershipNumber" style="${fn:escapeXml(display)}">
							<spring:theme var="membershipnumberPlaceholder" code="text.page.travellerdetails.form.placeholder.membershipnumber" text="Membership No." />
							<label for="td-traveller-membership1">${fn:escapeXml(membershipnumberPlaceholder)}</label>
							<form:input path="${fn:escapeXml(travellerForm)}.passengerInformation.frequentFlyerMembershipNumber" type="text" value="${fn:escapeXml(formValues.passengerInformation.frequentFlyerMembershipNumber)}" class="y_passengerFrequentFlyerMembershipNumber form-control" placeholder="${fn:escapeXml(membershipnumberPlaceholder)}"
								disabled="${fn:escapeXml(disabled)}" />
						</div>
					</div>
					<div class="row">
						<div class="form-group col-xs-12 col-sm-6 col-md-4">
							<label class="nowrap">
								<spring:theme code="text.page.travellerdetails.form.heading.reasonfortravel" text="What is your reason for travel?" />
							</label>
							<form:select path="${fn:escapeXml(travellerForm)}.passengerInformation.reasonForTravel" class="y_passengerReasonForTravel form-control" multiple="no">
								<form:option value="" selected="${empty formValues.passengerInformation.reasonForTravel?'selected':''}">
									<spring:theme code="text.page.travellerdetails.form.option.default" text="Select" />
								</form:option>
								<c:forEach var="reasonFortravelItem" items="${reasonFortravel}" varStatus="reasonFortravelIdx">
									<form:option value="${fn:escapeXml(reasonFortravelItem.code)}" selected="${not empty formValues.passengerInformation.reasonForTravel and formValues.passengerInformation.reasonForTravel==reasonFortravelItem.code ? 'selected' : ''}"> ${fn:escapeXml(reasonFortravelItem.name)} </form:option>
								</c:forEach>
							</form:select>
						</div>
					</div>
					<div class="row">
						<div class="form-group col-xs-12 col-sm-6 col-md-4">
							<label>
								<spring:theme code="text.page.travellerdetails.form.heading.email" text="email" var="emailPlaceholder" />
								${fn:escapeXml(emailPlaceholder)}
							</label>
							<form:input path="${fn:escapeXml(travellerForm)}.passengerInformation.email" id="y_travellerdetails_${fn:escapeXml(idx)}_email" type="text" value="${fn:escapeXml(formValues.passengerInformation.email)}" class="y_passengerEmail form-control" placeholder="${fn:escapeXml(emailPlaceholder)}" />
						</div>
					</div>
					<c:if test="${idx == 0 && showPhoneNumber}">
						<div class="row y_passengerContactNumber">
							<div class="form-group col-xs-12 col-sm-6 col-md-4">
								<label>
									<spring:theme code="text.page.travellerdetails.form.heading.contactnumber" text="Phone number" var="contactNumberPlaceholder" />
									${fn:escapeXml(contactNumberPlaceholder)}
								</label>
								<form:input path="${fn:escapeXml(travellerForm)}.passengerInformation.contactNumber" id="y_passengerContactNumber" type="text" value="${fn:escapeXml(formValues.passengerInformation.contactNumber)}" class="form-control" placeholder="${fn:escapeXml(contactNumberPlaceholder)}" />
								<form:input path="${fn:escapeXml(travellerForm)}.passengerInformation.validateContactNumber" id="y_validateContactNumber" type="hidden" value="${fn:escapeXml(showPhoneNumber)}" />
							</div>
						</div>
					</c:if>
					<div class="row">
						<div class="form-group col-xs-12 col-sm-12 text-right">
							<label>
								<spring:theme code="text.page.travellerdetails.form.specialassistance" text="Do you require special assistance? " />
								&nbsp;
								<form:checkbox path="${fn:escapeXml(travellerForm)}.specialAssistance" class="y_specialassistance" checked="${formValues.specialAssistance ? 'checked':''}" />
							</label>
						</div>
						<div class="form-group col-xs-12 col-sm-12 text-right">
							<c:if test="${isCustomer}">
								<label>
									<spring:theme code="text.page.travellerdetails.form.savedetails" text="Save these details? " />
									&nbsp;
									<form:checkbox path="${fn:escapeXml(travellerForm)}.passengerInformation.saveDetails" class="y_savedetails" />
								</label>
							</c:if>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</c:forEach>
