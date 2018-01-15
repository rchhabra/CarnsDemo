<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:if test="${not empty suggestionResult}">
	<div class="autocomplete-suggestions clearfix">
		<ul>
			<c:forEach var="result" items="${suggestionResult}">
				<li>
					<a href="" class="autocomplete-suggestion" data-code="${fn:escapeXml(result.code)}" data-suggestiontype="${fn:escapeXml(result.suggestionType)}" data-latitude="${fn:escapeXml(result.latitude)}" data-longitude="${fn:escapeXml(result.longitude)}" data-radius="${fn:escapeXml(result.radius)}">${fn:escapeXml(result.name)}</a>
				</li>
			</c:forEach>
		</ul>
		<c:if test="${isGoogleResult=='true'}">
			<div class="google-power pull-right">
				<img src="${pageContext.request.contextPath}/_ui/addons/travelacceleratorstorefront/responsive/common/images/googleLogo.png">
			</div>
		</c:if>
	</div>
</c:if>
