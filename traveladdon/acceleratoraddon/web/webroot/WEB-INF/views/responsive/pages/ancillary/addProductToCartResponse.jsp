<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<json:object escapeXml="false">

	<json:property name="valid" value="${addProductToCartResponse.valid}" />
	
	<c:if test="${!addProductToCartResponse.valid}">
		<json:array name="errors">
			<c:forEach var="error" items="${addProductToCartResponse.errors}">
				<json:property><spring:theme code="${error}" /></json:property>
			</c:forEach>
	  	</json:array>
	</c:if>

</json:object>
