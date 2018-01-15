<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<spring:htmlEscape defaultHtmlEscape="true" />

<c:choose>
	<c:when test="${not empty results}">
		<div class="autocomplete-suggestions">
			<ul>
				<c:forEach var="mapEntry" items="${results}">
                    <li class="parent">
                        <span class="title">
                            <a href="" class="autocomplete-suggestion" data-code="${fn:escapeXml(mapEntry.key.code)}" data-suggestiontype="${fn:escapeXml(mapEntry.key.suggestionType)}">${fn:escapeXml(mapEntry.key.name)}</a>
                        </span>
                    </li>
                    <c:forEach var="facility" items="${mapEntry.value}">
                        <li class="child">
                            <a href="" class="autocomplete-suggestion" data-code="${fn:escapeXml(facility.code)}" data-suggestiontype="${fn:escapeXml(facility.suggestionType)}">${fn:escapeXml(facility.name)}</a>
                        </li>
                    </c:forEach>
				</c:forEach>
			</ul>
		</div>
	</c:when>
	<c:otherwise>
		<div class="autocomplete-suggestions no-matching-airports">
			<ul>
				<li><spring:theme code="text.farefinder.autocomplete.no.matching.airports" text="No matching airports" /></li>
			</ul>
		</div>
	</c:otherwise>
</c:choose>
