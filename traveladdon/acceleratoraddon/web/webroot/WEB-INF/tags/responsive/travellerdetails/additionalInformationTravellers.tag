<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="travellerdetails" tagdir="/WEB-INF/tags/addons/traveladdon/responsive/travellerdetails"%>
<%@ attribute name="reasonFortravel" required="true" type="java.util.ArrayList"%>
<%@ attribute name="reservationData" required="true" type="de.hybris.platform.commercefacades.travel.reservation.data.ReservationData"%>
<%@ attribute name="formValues" required="true" type="de.hybris.platform.traveladdon.forms.TravellerForm"%>
<%@ attribute name="idx" required="true" type="java.lang.Integer"%>
<%@ attribute name="travellerForm" required="true" type="java.lang.String"%>
<div class="modal fade y_additionalInformationModal" id="additionalInformationModal_${fn:escapeXml(idx)}" tabindex="-1" role="dialog" aria-labelledby="additionalInformationModalLabel_${fn:escapeXml(idx)}">
	<div class="modal-dialog" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
				<h4 class="modal-title" id="additionalInformationModalLabel_${fn:escapeXml(idx)}">
					<spring:theme code="text.page.additional.information.modal.heading" text="Additional information" />
				</h4>
			</div>
			<div class="modal-body">
				<div class="row">
					<travellerdetails:ancillaries reservationData="${reservationData}" travellerCode="${formValues.label}" />
				</div>
				<div class="row">
					<div class="form-group col-xs-12 col-sm-6">
						<label>
							<spring:theme code="text.page.travellerdetails.form.heading.email" text="email" var="emailPlaceholder" />
							${fn:escapeXml(emailPlaceholder)}
						</label>
						<form:input path="${fn:escapeXml(travellerForm)}.passengerInformation.email" id="y_travellerdetails_${fn:escapeXml(idx)}_email" type="text" value="${fn:escapeXml(formValues.passengerInformation.email)}" class="y_passengerEmail form-control" placeholder="${fn:escapeXml(emailPlaceholder)}" />
					</div>
					<div class="form-group col-xs-12 col-sm-6">
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
				<!-- / .row -->
				<div class="row y_frequentFlyerInfo">
					<div class="form-group col-xs-12 col-sm-6">
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
						<form:input path="${fn:escapeXml(travellerForm)}.passengerInformation.frequentFlyerMembershipNumber" type="text" value="${fn:escapeXml(formValues.passengerInformation.frequentFlyerMembershipNumber)}" class="y_passengerFrequentFlyerMembershipNumber form-control"
							placeholder="${fn:escapeXml(membershipnumberPlaceholder)}" disabled="${fn:escapeXml(disabled)}" />
					</div>
				</div>
				<!-- / .row -->
				<div class="row">
					<div class="form-group col-xs-12">
						<label class="pull-left">
							<spring:theme code="text.page.travellerdetails.form.specialassistance" text="Do you require special assistance? " />
							&nbsp;
							<form:checkbox path="${fn:escapeXml(travellerForm)}.specialAssistance" class="y_specialassistance" checked="${formValues.specialAssistance ? 'checked':''}" />
						</label>
					</div>
				</div>
				<!-- / .row -->
				<div class="row">
					<div class="form-group col-xs-12">
						<button type="button" class="btn btn-primary col-xs-offset-0 col-sm-offset-6 col-xs-12 col-sm-6 y_saveAdditionalChanges">
							<spring:theme code="text.page.travellerdetails.form.modal.confirm" text="Confirm" />
						</button>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
