<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<c:set value="${component.styleClass}" var="navigationClass" />
<c:if test="${component.visible}">
	<div class="${fn:escapeXml(navigationClass)}">
		<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">${fn:escapeXml(component.navigationNode.title)}</a>
		<ul class="dropdown-menu">
			<c:forEach items="${component.navigationNode.children}" var="topLevelChild">
				<c:forEach items="${topLevelChild.entries}" var="entry">
					<li>
						<cms:component component="${entry.item}" evaluateRestriction="true" />
					</li>
				</c:forEach>
			</c:forEach>
		</ul>
	</div>
</c:if>
