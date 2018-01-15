<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ attribute name="leadDetailsForm" required="true" type="java.lang.String"%>
<%@ attribute name="roomStayIndex" required="true" type="java.lang.Integer"%>
<spring:htmlEscape defaultHtmlEscape="true" />

<c:if test="${not empty accommodationRoomPreferenceMap}">
    <c:set var="roomPreferences" value="${accommodationRoomPreferenceMap[roomStayIndex]}" />
	<li class="list-group-item">
		<div class="row input-row">
			<div class="col-xs-6 extra-item">
				<span>
					<spring:theme code="accommodation.room.preference.title" text="Room Preferences" />
				</span>
			</div>
			<div class="col-xs-6 extra-item-selection">
				<label for="rm_1-extra-item-04" class="sr-only">
					<spring:theme code="accommodation.room.preference.title" text="Room Preferences" />
				</label>
				<form:select class="form-control room-preferences" id="y_roomPreference" path="${leadDetailsForm}.roomPreferenceCodes[0]">
					<form:option value="">
						<spring:theme code="accommodation.room.preference.default" text="No Room Preference" />
					</form:option>
					<c:forEach items="${roomPreferences}" var="roomPreference">
					
						<c:if test="${not empty roomPreference.value }">
							<form:option value="${roomPreference.code}" htmlEscape="true">${fn:escapeXml(roomPreference.value)}</form:option>
						</c:if>
					</c:forEach>
				</form:select>
			</div>
		</div>
	</li>
</c:if>
