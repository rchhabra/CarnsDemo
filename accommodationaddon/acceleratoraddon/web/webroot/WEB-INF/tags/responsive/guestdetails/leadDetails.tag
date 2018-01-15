<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ attribute name="leadDetailsForm" required="true" type="java.lang.String"%>
<%@ attribute name="roomNum" required="true" type="java.lang.Integer"%>
<%@ attribute name="formValues" required="true" type="de.hybris.platform.accommodationaddon.forms.LeadGuestDetailsForm"%>
<%@ attribute name="index" required="true" type="java.lang.Integer"%>
<%@ attribute name="roomStayRefNumber" required="true" type="java.lang.Integer"%>
<%@ attribute name="checkInDate" required="true" type="java.util.Date"%>
<%@ attribute name="passengerTypeMaxQuantityMap" required="true" type="java.util.Map"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<div class="row">
	<div class="panel panel-default">
		<form:input id="leadGuestForm_${fn:escapeXml(index)}" type="hidden" path="${fn:escapeXml(leadDetailsForm)}.formId" value="${fn:escapeXml(index)}" class="y_leadDetailsFormId_${fn:escapeXml(roomStayRefNumber)}" />
		<form:input id="leadGuestForm_${fn:escapeXml(index)}" type="hidden" path="${fn:escapeXml(leadDetailsForm)}.roomStayRefNumber" value="${fn:escapeXml(roomStayRefNumber)}" class="y_roomStayRefNumber_${fn:escapeXml(roomStayRefNumber)}" />
		<form:input id="leadGuestForm_${fn:escapeXml(index)}" type="hidden" path="${fn:escapeXml(leadDetailsForm)}.notRemoved" value="true" />
		<div class="panel-heading">
			<h3 class="panel-title">
				<spring:theme code="text.page.lead.guest.title" text="Lead Details" />
			</h3>
		</div>
		<div class="panel-body">
			<div class="lead-details">
				<fieldset>
					<legend class="sr-only">
						<spring:theme code="text.guest.details.lead.guest.details.request" text="Lead Guest" />
					</legend>
					<p>
						<spring:theme code="text.page.lead.guest.details.request" text="Please assign a lead guest" />
					</p>
					<div class="row">
						<div class="form-group col-xs-12 col-md-6">
							<label for="rm_${fn:escapeXml(roomNum)}-first-name">
								<spring:theme code="text.page.guestdetails.form.heading.firstname" text="First Name" var="firstnamePlaceholder" />
								${fn:escapeXml(firstnamePlaceholder)}
							</label>
							<form:input type="text" path="${fn:escapeXml(leadDetailsForm)}.guestData.profile.firstName" id="rm_${fn:escapeXml(roomNum)}-first-name" class="col-xs-12 form-control y_guestFirstname" cssErrorClass="fieldError" placeholder="${fn:escapeXml(firstnamePlaceholder)}" readonly="${fn:escapeXml(isAmendExtras)}" />
						</div>
						<div class="form-group col-xs-12 col-md-6">
							<label for="rm_${fn:escapeXml(roomNum)}-last-name">
								<spring:theme code="text.page.guestdetails.form.heading.lastname" text="Last Name" var="lastnamePlaceholder" />
								${fn:escapeXml(lastnamePlaceholder)}
							</label>
							<form:input type="text" path="${fn:escapeXml(leadDetailsForm)}.guestData.profile.lastName" id="rm_${fn:escapeXml(roomNum)}-last-name" class="col-xs-12 form-control y_guestLastname" cssErrorClass="fieldError" placeholder="${fn:escapeXml(lastnamePlaceholder)}" readonly="${fn:escapeXml(isAmendExtras)}" />
						</div>
					</div>
				</fieldset>

				<c:if test="${roomStayRefNumber == 0}">
					<fieldset>
						<legend class="sr-only">
							<spring:theme code="text.page.lead.guest.contact.Number" text="Contact Number" />
						</legend>
						<div class="row">
							<div class="col-xs-12 col-sm-6">
								<div class="row">
									<div class="form-group col-xs-12 col-md-12">
										<label for="contactNumber">
											<spring:theme code="text.page.lead.guest.phone.number" text="Phone number" />
										</label>
										<form:input type="text" name="contact-number" id="y_contactNumber" path="${fn:escapeXml(leadDetailsForm)}.guestData.profile.contactNumber" class="col-xs-12 form-control" readonly="${fn:escapeXml(isAmendExtras)}" />
									</div>
								</div>
							</div>
						</div>
					</fieldset>
				</c:if>
				<fieldset>
					<legend class="sr-only">
						<spring:theme code="text.page.room.guest.numbers" text="Guest numbers" />
					</legend>
					<div class="row">
						<div class="col-xs-12 col-sm-6 guest-types">
							<div class="row">
								<c:forEach var="entry" items="${formValues.passengerTypeQuantityData}" varStatus="i">
									<div class="col-xs-12 col-sm-4">
										<label for="y_${fn:escapeXml(entry.passengerType.code)}">
											<spring:theme var="passengerTypeLabel" code="text.cms.accommodationfinder.passenger.${entry.passengerType.code}" text="Passenger" />
											${fn:escapeXml(passengerTypeLabel)}
										</label>
										<form:select class="form-control" id="y_${fn:escapeXml(entry.passengerType.code)}" path="${fn:escapeXml(leadDetailsForm)}.passengerTypeQuantityData[${fn:escapeXml(i.index)}].quantity" cssErrorClass="fieldError" disabled="${fn:escapeXml(isAmendExtras)}">
											<form:option value="-1" disabled="true"> ${fn:escapeXml(entry.passengerType.name)} </form:option>
											<c:set var="passengerTypeMaxQuantity" value="${passengerTypeMaxQuantityMap[entry.passengerType.code]}" />
											<c:forEach begin="0" end="${passengerTypeMaxQuantityMap[entry.passengerType.code]}" varStatus="loop">
												<form:option value="${fn:escapeXml(loop.index)}" selected="${loop.index == entry.quantity? 'selected':''}"> ${loop.index} </form:option>
											</c:forEach>
										</form:select>
										<c:if test="${isAmendExtras}">
											<form:input path="${fn:escapeXml(leadDetailsForm)}.passengerTypeQuantityData[${fn:escapeXml(i.index)}].quantity" type="hidden" readonly="true" />
										</c:if>
										<form:input path="${fn:escapeXml(leadDetailsForm)}.passengerTypeQuantityData[${fn:escapeXml(i.index)}].passengerType.code" type="hidden" readonly="true" />
										<form:input path="${fn:escapeXml(leadDetailsForm)}.passengerTypeQuantityData[${fn:escapeXml(i.index)}].passengerType.name" type="hidden" readonly="true" />
										<form:input path="${fn:escapeXml(leadDetailsForm)}.passengerTypeQuantityData[${fn:escapeXml(i.index)}].passengerType.minAge" type="hidden" readonly="true" />
										<form:input path="${fn:escapeXml(leadDetailsForm)}.passengerTypeQuantityData[${fn:escapeXml(i.index)}].passengerType.maxAge" type="hidden" readonly="true" />
										<c:set var="label">
											<c:choose>
												<c:when test="${entry.passengerType.code == 'adult'}">
													<span class="age-range">
														(${fn:escapeXml(entry.passengerType.minAge)}+
														<span class="years">
															<spring:theme code="text.cms.accommodationfinder.passengerType.label.years" text="years" />
														</span>
														)
													</span>
												</c:when>
												<c:otherwise>
													<span class="age-range">
														<c:choose>
															<c:when test="${entry.passengerType.code == 'child'}">
                                                            (<spring:theme code="text.cms.accommodationfinder.passengerType.label.up.to" text="up to " />&nbsp${fn:escapeXml(entry.passengerType.maxAge)}
                                                        </c:when>
															<c:otherwise>
                                                            (${fn:escapeXml(entry.passengerType.minAge)}-${fn:escapeXml(entry.passengerType.maxAge)}
                                                        </c:otherwise>
														</c:choose>
														<span class="years">
															<spring:theme code="text.cms.accommodationfinder.passengerType.label.years" text="years" />
														</span>
														)
													</span>
												</c:otherwise>
											</c:choose>
										</c:set>
										<div>${label}</div>
									</div>
								</c:forEach>
							</div>
						</div>
					</div>
				</fieldset>
				<fieldset>
					<legend class="sr-only">
						<spring:theme code="text.page.lead.guest.arrivaltime" text="Arrival time" />
					</legend>
					<p>
						<spring:theme code="text.page.lead.guest.arrivaltime.request" text="Please Specify the time of your arrival to the hotel:" />
					</p>
					<div class="row">
						<div class="col-xs-12 col-sm-6 arrivalTime_rm_${fn:escapeXml(roomNum)}">
							<div class="row">
								<div class="form-group col-xs-12 col-md-6">
									<label for="y_rm_${fn:escapeXml(roomNum)}-accomodation-arrival-hours">
										<spring:theme code="text.page.lead.guest.arrivaltime.hours" text="Hours" />
									</label>
									<div class="col-xs-12">
										<div class="row">
											<select class="form-control" id="y_rm_${fn:escapeXml(roomNum)}-accomodation-arrival-hours" name="hours" ${(not empty isAmendExtras && isAmendExtras) ? 'disabled' : ''}>
												<c:forEach var="entry" items="${hours}">
													<c:choose>
														<c:when test="${entry eq '12'}">
															<option value="${fn:escapeXml(entry)}" selected="selected">${fn:escapeXml(entry)}</option>
														</c:when>
														<c:otherwise>
															<option value="${fn:escapeXml(entry)}">${fn:escapeXml(entry)}</option>
														</c:otherwise>
													</c:choose>
												</c:forEach>
											</select>
										</div>
									</div>
								</div>
								<div class="form-group col-xs-12 col-md-6">
									<label for="y_rm_${fn:escapeXml(roomNum)}-accomodation-arrival-minutes">
										<spring:theme code="text.page.lead.guest.arrivaltime.minutes" text="Minutes" />
									</label>
									<div class="col-xs-12">
										<div class="row">
											<select class="form-control" id="y_rm_${fn:escapeXml(roomNum)}-accomodation-arrival-minutes" name="minutes" ${(not empty isAmendExtras && isAmendExtras) ? 'disabled' : ''}>
												<c:forEach var="entry" items="${minutes}">
													<option value="${fn:escapeXml(entry)}">${fn:escapeXml(entry)}</option>
												</c:forEach>
											</select>
										</div>
									</div>
								</div>
								<form:input type="hidden" id="y_arrivalTime_${fn:escapeXml(roomNum)}" path="${fn:escapeXml(leadDetailsForm)}.arrivalTime" />
								<fmt:formatDate value="${checkInDate}" var="formattedCheckInDate" pattern="${datePattern}" />
								<input type="hidden" id="y_checkInDate" value="${formattedCheckInDate}" />
							</div>
						</div>
					</div>
				</fieldset>
				<c:if test="${roomNum == 1 && (amend eq false || isAmendExtras)}">
					<fieldset>
						<legend class="sr-only">
							<spring:theme code="text.page.lead.guest.details.email.notification" text="Would you like to notify with an email about this room?" />
						</legend>
						<p>
							<spring:theme code="text.page.lead.guest.details.email.notification" text="Would you like to notify with an email about this room?" />
						</p>
						<div class="row">
							<div class="form-group col-xs-12 col-md-6">
								<label for="rm_${fn:escapeXml(roomNum)}-email">
									<spring:theme code="text.page.guestdetails.form.heading.email" text="Email" var="emailPlaceholder" />
									${fn:escapeXml(emailPlaceholder)}
								</label>
								<form:input type="text" path="${fn:escapeXml(leadDetailsForm)}.guestData.profile.email" id="rm_${fn:escapeXml(roomNum)}-email" class="col-xs-12 form-control y_guestEmail" cssErrorClass="fieldError" placeholder="${fn:escapeXml(emailPlaceholder)}" readonly="${fn:escapeXml(isAmendExtras)}" />
							</div>
						</div>
					</fieldset>
				</c:if>
			</div>
		</div>
	</div>
</div>
