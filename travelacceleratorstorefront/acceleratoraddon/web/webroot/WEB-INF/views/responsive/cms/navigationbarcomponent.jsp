<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>

<li class="<c:if test="${not empty component.navigationNode.children}">dropdown</c:if>">
	<cms:component component="${component.link}" evaluateRestriction="true" />
	<c:if test="${not empty component.navigationNode.children}">
		<c:forEach items="${component.navigationNode.children}" var="child">
			<c:if test="${child.visible}">
				<ul class="dropdown-menu">
					<c:forEach items="${child.links}" var="childlink" varStatus="i">
						<cms:component component="${childlink}" evaluateRestriction="true" element="li" />
					</c:forEach>
				</ul>
			</c:if>
		</c:forEach>
	</c:if>
</li>