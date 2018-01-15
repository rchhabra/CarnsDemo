<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ attribute name="passengerTypeQuantityList" required="true" type="java.util.List"%>
<%@ attribute name="formPrefix" required="true" type="java.lang.String"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<c:forEach var="entry" items="${passengerTypeQuantityList}" varStatus="i">
	<div class="col-xs-4">
		<label for="y_${fn:escapeXml(entry.passengerType.code)}">
			<spring:theme var="passengerTypeLabel" code="text.cms.accommodationfinder.passenger.${entry.passengerType.code}" text="Passenger" />
			${fn:escapeXml(passengerTypeLabel)}
		</label>
		<form:select class="form-control ${fn:escapeXml(entry.passengerType.code)} y_${fn:escapeXml(entry.passengerType.code)}Select" id="y_${fn:escapeXml(entry.passengerType.code)}" path="${fn:escapeXml(formPrefix)}passengerTypeQuantityList[${i.index}].quantity" cssErrorClass="fieldError">
			<form:option value="-1" disabled="true"> ${fn:escapeXml(entry.passengerType.name)} </form:option>
			<c:choose>
				<c:when test="${not empty passengerTypeMaxQuantityMap}">
					<c:forEach begin="0" end="${passengerTypeMaxQuantityMap[entry.passengerType.code]}" varStatus="loop">
						<form:option value="${loop.index}"> ${loop.index} </form:option>
					</c:forEach>
				</c:when>
				<c:otherwise>
					<form:options items="${guestQuantity}" htmlEscape="true" />
				</c:otherwise>
			</c:choose>
		</form:select>
		<form:input path="${fn:escapeXml(formPrefix)}passengerTypeQuantityList[${i.index}].passengerType.code" class="y_passengerTypeCode" type="hidden" readonly="true" />
		<form:input path="${fn:escapeXml(formPrefix)}passengerTypeQuantityList[${i.index}].passengerType.name" type="hidden" readonly="true" />
		<form:input path="${fn:escapeXml(formPrefix)}passengerTypeQuantityList[${i.index}].passengerType.minAge" type="hidden" readonly="true" />
		<form:input path="${fn:escapeXml(formPrefix)}passengerTypeQuantityList[${i.index}].passengerType.maxAge" type="hidden" readonly="true" />
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
	</div>
</c:forEach>
