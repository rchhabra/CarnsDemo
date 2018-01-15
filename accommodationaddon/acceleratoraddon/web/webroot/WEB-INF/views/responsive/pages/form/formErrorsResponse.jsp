<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<json:object escapeXml="false">
	<json:property name="hasErrorFlag" value="${hasErrorFlag}" />
	<json:object name="errors">
		<c:forEach items="${fieldErrors}" var="error">
		
			<c:set var="isNumberException" value="false" />
			<c:if test="${fn:contains(error.defaultMessage, 'NumberFormatException')}">
				<c:set var="isNumberException" value="true" />
			</c:if>
		
			<c:set var="isIndexOutOfBoundException" value="false" />
			<c:if test="${fn:contains(error.defaultMessage, 'IndexOutOfBoundsException')}">
				<c:set var="isIndexOutOfBoundException" value="true" />
			</c:if>
			
			<c:choose>
				<c:when test="${isNumberException}">
					<json:property name="${error.field}">
						<spring:theme code="InvalidQuantity.accommodationFinderForm.numberOfRooms" />
					</json:property>
				</c:when>
				<c:when test="${isIndexOutOfBoundException}">
					<json:property name="${error.field}">
						<spring:theme code="IndexOutOfBound.accommodationFinderForm" />
					</json:property>
				</c:when>
				<c:when test="${not empty error.defaultMessage}">
					<json:property name="${error.field}" value="${error.defaultMessage}" />
				</c:when>
				<c:otherwise>
					<json:property name="${error.field}">
						<spring:theme code="${error.code}" />
					</json:property>
				</c:otherwise>
			</c:choose>
			
		</c:forEach>
	</json:object>
</json:object>
