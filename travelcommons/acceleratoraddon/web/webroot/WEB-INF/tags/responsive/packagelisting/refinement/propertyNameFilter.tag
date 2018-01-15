<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ attribute name="filterPropertyName" required="true" type="java.lang.String" %>
<spring:htmlEscape defaultHtmlEscape="true" />

<li class="dropdown">
    <a class="dropdown-toggle" data-toggle="dropdown" href="#" role="button"
       aria-haspopup="true" aria-expanded="false">
        <spring:theme code="text.cms.accommodationrefinement.propertyname.title" text="Name of Property"/>
        <span class="caret"></span>
    </a>
    <ul class="dropdown-menu">
        <form class="y_filterNameForm" method="GET">
            <legend class="sr-only">
                <spring:theme code="text.cms.accommodationrefinement.propertyname.property" text="Property"/>
            </legend>
            <c:forEach items="${accommodationSearchParams}" var="paramDetail">
                <c:if test="${paramDetail.key != 'propertyName'}">
                    <input type="hidden" name="${fn:escapeXml(paramDetail.key)}" value="${fn:escapeXml(paramDetail.value)}" />
                </c:if>
            </c:forEach>
            <input type="hidden" name="q" value="${fn:escapeXml(packageSearchResponse.criterion.query)}" />
            <input type="hidden" name="priceRange" value="${fn:escapeXml(priceRange)}" />
            <input id="y_resultsViewTypeForPropertyFilter" type="hidden" name="resultsViewType" value="${fn:escapeXml( resultsViewType)}"/>
            <li class="clearfix">
                <div class="form-group col-xs-12">
                    <label for="y_propertyName" class="sr-only"><spring:theme
                            code="text.cms.accommodationrefinement.propertyname.sr.title" text="Search Property"/></label>
                    <input type="text" class="form-control" id="y_propertyName"
                           name="propertyName"
                           placeholder="<spring:theme code="text.cms.accommodationrefinement.propertyname.placeholder" text="Property Name"/>"
                           value="${fn:escapeXml(filterPropertyName)}"/>
                    <div class="bottom-align-parent">
                        <button class="btn btn-primary col-xs-12 bottom-align" type="submit">
                            <spring:theme code="text.cms.accommodationrefinement.propertyname.button.search" text="Search"/>
                        </button>
                    </div>
                </div>
            </li>
        </form>
    </ul>
</li>
