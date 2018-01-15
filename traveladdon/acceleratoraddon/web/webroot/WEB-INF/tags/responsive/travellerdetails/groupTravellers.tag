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
<%@ attribute name="travellerForms" required="true" type="java.util.ArrayList"%>
<%@ attribute name="reservationData" required="true" type="de.hybris.platform.commercefacades.travel.reservation.data.ReservationData"%>
<%@ attribute name="isCollapsable" required="true" type="java.lang.Boolean"%>
<%@ attribute name="showPhoneNumber" required="true" type="java.lang.Boolean"%>
<%@ attribute name="relativeIndex" required="true" type="java.lang.Integer"%>
<%@ attribute name="isCustomer" required="true" type="java.lang.Boolean"%>
<%@ attribute name="currentIndex" required="true" type="java.lang.Integer"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<div id="traveller-details-${fn:escapeXml(currentIndex)}" class="panel-body y_travellerDetailsParentDiv">
	<div class="panel-form">
		<fieldset class="fieldset">
			<c:forEach var="formValues" items="${travellerForms}" varStatus="formValuesIdx">
				<c:set var="travellerTitles" scope="page" value="${adultsTitles}" />
				<c:if test="${fn:containsIgnoreCase(formValues.label, 'child')}">
					<c:set var="travellerTitles" scope="page" value="${childrenTitles}" />
				</c:if>
				<c:if test="${fn:containsIgnoreCase(formValues.label, 'infant')}">
					<c:set var="travellerTitles" scope="page" value="${childrenTitles}" />
				</c:if>
				<c:set var="travellerForm" value="travellerForms[${formValuesIdx.index + relativeIndex}]" />
				<c:set var="actualIdx" value="${formValuesIdx.index}" />
				<c:set var="idx" value="${formValuesIdx.index + relativeIndex}" />
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
				<div class="row">
					<div id="passenger-info-${fn:escapeXml(idx)}" class="y_passengerInformationForm">
						<form:hidden path="${fn:escapeXml(travellerForm)}.formId" value="${fn:escapeXml(idx)}" />
						<form:hidden path="${fn:escapeXml(travellerForm)}.uid" value="${fn:escapeXml(formValues.uid)}" />
						<form:hidden path="${fn:escapeXml(travellerForm)}.passengerInformation.passengerTypeCode" value="${fn:escapeXml(formValues.passengerInformation.passengerTypeCode)}" class="y_passengerType" />
						<form:hidden path="${fn:escapeXml(travellerForm)}.passengerInformation.passengerTypeName" value="${fn:escapeXml(formValues.passengerInformation.passengerTypeName)}" />
						<form:hidden path="${fn:escapeXml(travellerForm)}.label" value="${fn:escapeXml(formValues.label)}" />
						<form:hidden path="${fn:escapeXml(travellerForm)}.selectedSavedTravellerUId" class="y_passengerSelectedSavedTravellerUId" />
						<form:hidden path="${fn:escapeXml(travellerForm)}.passengerInformation.selectedSavedTravellerUId" class="y_passengerSelectedSavedTravellerUId" />
						<div class="form-group col-xs-3 col-sm-4 col-md-2 title">
							<label class="${actualIdx > 0 ? 'sr-only' : '' }">
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
						<div class="form-group col-xs-12 col-sm-4 col-md-3">
							<label class="${actualIdx > 0 ? 'sr-only' : '' }">
								<spring:theme code="text.page.travellerdetails.form.heading.firstname" text="Firstname" var="firstnamePlaceholder" />
								${fn:escapeXml(firstnamePlaceholder)}
							</label>
							<form:input path="${fn:escapeXml(travellerForm)}.passengerInformation.firstname" id="y_travellerdetails_${fn:escapeXml(idx)}_first_name" type="text" class="y_passengerFirstname form-control ${isCustomer ? 'y_customer' : ''}" placeholder="${fn:escapeXml(firstnamePlaceholder)}"
								autocomplete="off" />
							<div id="y_passengerFirstNameSuggestions" class="autocomplete-suggestions-wrapper traveller-autocomplete hidden"></div>
						</div>
						<div class="form-group col-xs-12 col-sm-4 col-md-3">
							<label class="${actualIdx > 0 ? 'sr-only' : '' }">
								<spring:theme var="lastnamePlaceholder" code="text.page.travellerdetails.form.heading.lastname" text="Lastname" />
								${fn:escapeXml(lastnamePlaceholder)}
							</label>
							<form:input path="${fn:escapeXml(travellerForm)}.passengerInformation.lastname" id="y_travellerdetails_${fn:escapeXml(idx)}_last_name" type="text" value="${fn:escapeXml(formValues.passengerInformation.lastname)}" class="y_passengerLastname form-control ${isCustomer ? 'y_customer' : ''}"
								placeholder="${fn:escapeXml(lastnamePlaceholder)}" autocomplete="off" />
							<div id="y_passengerLastNameSuggestions" class="autocomplete-suggestions-wrapper traveller-autocomplete hidden"></div>
						</div>
						<input type="hidden" id="y_selectedUid" />
						<div class="form-group col-xs-3 col-sm-4 col-md-2 gender">
							<label class="${actualIdx > 0 ? 'sr-only' : '' }">
								<spring:theme code="text.page.travellerdetails.form.heading.gender" text="Gender" />
							</label>
							<form:select path="${fn:escapeXml(travellerForm)}.passengerInformation.gender" multiple="no" class="form-control y_groupPassengerGender">
								<form:option value="male" selected="${empty formValues.passengerInformation.gender or formValues.passengerInformation.gender=='male'?'selected':''}">
									<spring:theme code="text.page.travellerdetails.form.heading.gender.male" text="Male" />
								</form:option>
								<form:option value="female" selected="${formValues.passengerInformation.gender=='female'?'selected':''}">
									<spring:theme code="text.page.travellerdetails.form.heading.gender.female" text="Female" />
								</form:option>
							</form:select>
						</div>
						<div class="form-group col-xs-9 col-sm-8 col-md-2 additional-information">
							<div class="pull-left">
								<div class="row">
									<a href="#" data-toggle="modal" data-target="#additionalInformationModal_${fn:escapeXml(idx)}" data-backdrop="static">
										<span class="glyphicon glyphicon-plus-sign" aria-hidden="true"></span>
									</a>
									<span class="sr-only">
										<spring:theme code="text.page.travellerdetails.form.additional.information" text="Additional information" />
									</span>
									<span class="y_checkedIcon glyphicon glyphicon-ok-sign hidden" aria-hidden="true">
										<span class="sr-only">
											<spring:theme code="text.page.travellerdetails.form.additional.information.added" text="Additional information added" />
										</span>
									</span>
									<span class="y_errorIcon glyphicon glyphicon-exclamation-sign hidden" aria-hidden="true">
										<span class="sr-only">
											<spring:theme code="text.page.travellerdetails.form.additional.information.added" text="Additional information added" />
										</span>
									</span>

								</div>
							</div>
							<div class="form-group pull-right save">
								<c:if test="${isCustomer}">
									<label class="${actualIdx > 0 ? 'sr-only' : '' }">
										<spring:theme code="text.page.travellerdetails.form.group.save" text="Save" />
									</label>
									<form:checkbox path="${fn:escapeXml(travellerForm)}.passengerInformation.saveDetails" class="y_savedetails pull-right" />
								</c:if>
							</div>
						</div>

						<!-- Modal tag -->
						<travellerdetails:additionalInformationTravellers idx="${idx}" reservationData="${reservationData}" travellerForm="${travellerForm}" formValues="${formValues}" reasonFortravel="${reasonFortravel}"/>
						
					</div>
				</div>
			</c:forEach>
			<c:if test="${isCustomer}">
				<div class="form-group pull-right save-all">
					<label for="td-traveller-save-details_all" class="pull-left">
						<spring:theme code="text.page.travellerdetails.form.save.all" text="Save All" />
					</label>
					<input id="td-traveller-save-details_all" name="td-traveller-save-details_all" type="checkbox" class="pull-right y_saveAllTravellers" />
				</div>
			</c:if>
		</fieldset>
	</div>
</div>
