<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ attribute name="roomStay" required="true" type="de.hybris.platform.commercefacades.accommodation.ReservedRoomStayData"%>
<spring:htmlEscape defaultHtmlEscape="true" />

<c:if test="${not empty roomStay.roomPreferences }">
	<div class="row facilities-options">
		<div class="panel panel-default">
			<div class="panel-heading divided">
				<h3 class="panel-title"><spring:theme code="text.cms.accommodation.selected.room.preferences.title" text="Your Preferences" /></h3>
			</div>
			<div class="panel-body">
				<ul class="selected-facilities clearfix">
					<c:forEach var="roomPreference" items="${roomStay.roomPreferences}">
						<li class="col-xs-6 col-sm-4">
							<span class="glyphicon glyphicon-ok" aria-hidden="true"></span>
							${fn:escapeXml(roomPreference.value)}
						</li>
					</c:forEach>
				</ul>
			</div>
		</div>
	</div>
</c:if>
