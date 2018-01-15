<%@ page trimDirectiveWhitespaces="true" contentType="application/json" %>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<json:object escapeXml="false">

	<json:property name="valid" value="${addBundleToCartResponse.valid}" />
	
	<c:if test="${!addBundleToCartResponse.valid}">
		<json:array name="errors">
			<c:forEach var="error" items="${addBundleToCartResponse.errors}">
				<json:property><spring:theme code="${error}" /></json:property>
			</c:forEach>
	  	</json:array>
	</c:if>

	<c:if test="${addBundleToCartResponse.minOriginDestinationRefNumber != null }">
		<json:property name="minOriginDestinationRefNumber" value="${addBundleToCartResponse.minOriginDestinationRefNumber}" />
	</c:if>

</json:object>
