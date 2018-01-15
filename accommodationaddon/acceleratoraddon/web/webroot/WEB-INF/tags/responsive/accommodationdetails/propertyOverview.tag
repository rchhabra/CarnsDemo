<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ attribute name="property" required="true" type="de.hybris.platform.commercefacades.accommodation.PropertyData" %>
<spring:htmlEscape defaultHtmlEscape="true" />

<div class="row">
    <div class="col-xs-12 col-sm-4 y_amenities-list">
        <p>
            <strong><spring:theme code="text.accommodation.details.main.amenities"
                                  text="Main amenities"/></strong>
        </p>
        <ul class="amenities-items">
            <c:forEach var="amenity" items="${property.amenities}" varStatus="loop">
            <c:choose>
            <c:when test="${loop.first}">
            <c:set var="amenityType" value="${amenity.facilityType}"/>
            <li><a href="#">${fn:escapeXml(amenity.facilityType)}</a>
                <ul>
                    <li>${fn:escapeXml(amenity.description)}</li>
                    </c:when>
                    <c:otherwise>
                    <c:choose>
                    <c:when test="${amenityType!=amenity.facilityType}">
                    <c:set var="amenityType" value="${amenity.facilityType}"/>
                </ul>
            </li>
            <li><a href="#">${fn:escapeXml(amenity.facilityType)}</a>
                <ul>
                    <li>${fn:escapeXml(amenity.description)}</li>
                    </c:when>
                    <c:otherwise>
                        <li>${fn:escapeXml(amenity.description)}</li>
                    </c:otherwise>
                    </c:choose>
                    </c:otherwise>
                    </c:choose>
                    </c:forEach>
                </ul></li>
        </ul>
        <p><a href="#" class="show-all">Show all</a></p>
        <p><a href="#" class="collapse-all hidden">Collapse all</a></p>
    </div>
    <div class="col-xs-12 col-sm-8">
        <p>${property.description}</p>
    </div>
</div>
