<%@ page trimDirectiveWhitespaces="true" contentType="application/json" %>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json" %>
<%@ taglib prefix="tripFinder" tagdir="/WEB-INF/tags/addons/traveladdon/responsive/tripFinder"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<json:object escapeXml="false">
	<c:choose>
		<c:when test="${not empty errorMsg}">
			<json:property name="errorMsg" value="${errorMsg}"/>
		</c:when>
		<c:otherwise>
			<json:property name="successMsg">
				<c:if test="${redeemSuccess}">
					<spring:theme code="voucher.add.success" text="Voucher redeemed successfully"/>
				</c:if>
				<c:if test="${releaseSuccess}">
					<spring:theme code="voucher.delete.success" text="Voucher released successfully"/>
				</c:if>
			</json:property>
		</c:otherwise>
	</c:choose>
</json:object>
