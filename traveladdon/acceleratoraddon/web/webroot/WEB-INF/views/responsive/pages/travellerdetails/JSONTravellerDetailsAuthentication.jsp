<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json"%>

<c:if test="${not empty travellerData}">
	<json:object>
		<json:property name="isAuthenticated" value="${isAuthenticated}" />
		<json:object name="travellerInfo">
			<json:property name="uid" value="${travellerData.uid}" />
			<json:property name="title" value="${travellerData.travellerInfo.title.code}" />
			<json:property name="firstName" value="${travellerData.travellerInfo.firstName}" />
			<json:property name="surname" value="${travellerData.travellerInfo.surname}" />
			<json:property name="reasonForTravel" value="${travellerData.travellerInfo.reasonForTravel}" />
			<json:object name="passengerType">
				<json:property name="code" value="${travellerData.travellerInfo.passengerType.code}" />
			</json:object>
			<json:property name="gender" value="${travellerData.travellerInfo.gender}" />
			<json:property name="membershipNumber" value="${travellerData.travellerInfo.membershipNumber}" />
			<json:property name="email" value="${travellerData.travellerInfo.email}" />
			<json:object name="specialRequestDetail">
				<json:array name="specialServiceRequests" items="${travellerData.specialRequestDetail.specialServiceRequests}" var="specialServiceRequest">
					${specialServiceRequest.name}
				</json:array>
			</json:object>
			
		</json:object>
	</json:object>
</c:if>
