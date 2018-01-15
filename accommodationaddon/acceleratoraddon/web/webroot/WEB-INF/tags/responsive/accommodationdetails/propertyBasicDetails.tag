<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ attribute name="property" required="true" type="de.hybris.platform.commercefacades.accommodation.PropertyData" %>
<spring:htmlEscape defaultHtmlEscape="true" />

<div class="panel-heading clearfix">
    <h3 class="panel-title pull-left">${fn:escapeXml(property.accommodationOfferingName)}</h3>
    <c:forEach var="award" items="${property.awards}">
        <c:if test="${award.type eq 'STAR_RATING'}">
            <div class="col-xs-12 col-sm-4 accommodation-star-rating">
                <div class="row">
                	<c:set var="rating" value="${award.rating gt 15 ? 15 : award.rating}" />
                    <span class="sr-only">
                        ${fn:escapeXml(rating)}<spring:theme code="text.accommodation.listing.star.rating.stars"/>
                    </span>
                    <span aria-label="stars">
					    <c:forEach begin="0" end="${rating -1}" varStatus="loop">
                            <span>&#9733;</span>
                        </c:forEach>
                    </span>
                </div>
            </div>
        </c:if>
    </c:forEach>
    <c:set var="addressDetail" value=""/>
    <c:if test="${not empty property.address.line1}">
        <c:set var="addressDetail" value="${property.address.line1}"/>
    </c:if>
    <c:if test="${not empty property.address.line2}">
        <c:choose>
            <c:when test="${not empty addressDetail}">
                <c:set var="addressDetail" value="${addressDetail}, ${property.address.line2}"/>
            </c:when>
            <c:otherwise>
                <c:set var="addressDetail" value="${property.address.line2}"/>
            </c:otherwise>
        </c:choose>
    </c:if>
    <c:if test="${not empty property.address.town}">
        <c:choose>
            <c:when test="${not empty addressDetail}">
                <c:set var="addressDetail" value="${addressDetail}, ${property.address.town}"/>
            </c:when>
            <c:otherwise>
                <c:set var="addressDetail" value="${property.address.town}"/>
            </c:otherwise>
        </c:choose>
    </c:if>
    <c:if test="${not empty property.address.region && not empty property.address.region.name}">
        <c:choose>
            <c:when test="${not empty addressDetail}">
                <c:set var="addressDetail" value="${addressDetail}, ${property.address.region.name}"/>
            </c:when>
            <c:otherwise>
                <c:set var="addressDetail" value="${property.address.region.name}"/>
            </c:otherwise>
        </c:choose>
    </c:if>
    <c:if test="${not empty property.address.postalCode}">
        <c:choose>
            <c:when test="${not empty addressDetail}">
                <c:set var="addressDetail" value="${addressDetail}, ${property.address.postalCode}"/>
            </c:when>
            <c:otherwise>
                <c:set var="addressDetail" value="${property.address.postalCode}"/>
            </c:otherwise>
        </c:choose>
    </c:if>
    <c:if test="${not empty property.address.country && not empty property.address.country.name}">
        <c:choose>
            <c:when test="${not empty addressDetail}">
                <c:set var="addressDetail" value="${addressDetail}, ${property.address.country.name}"/>
            </c:when>
            <c:otherwise>
                <c:set var="addressDetail" value="${property.address.country.name}"/>
            </c:otherwise>
        </c:choose>
    </c:if>
    <c:if test="${not empty property.address.phone}">
        <c:choose>
            <c:when test="${not empty addressDetail}">
                <c:set var="addressDetail" value="${addressDetail}, ${property.address.phone}"/>
            </c:when>
            <c:otherwise>
                <c:set var="addressDetail" value="${property.address.phone}"/>
            </c:otherwise>
        </c:choose>
    </c:if>
    <small class="panel-subtitle">${fn:escapeXml(addressDetail)}</small>
</div>
