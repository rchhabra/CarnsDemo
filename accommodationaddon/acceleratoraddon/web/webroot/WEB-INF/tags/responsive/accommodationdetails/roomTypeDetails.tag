<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ attribute name="roomType" required="true" type="de.hybris.platform.commercefacades.accommodation.RoomTypeData" %>
<%@ attribute name="index" required="true" type="java.lang.Integer" %>
<%@ attribute name="fromPrice" required="true" type="de.hybris.platform.commercefacades.product.data.PriceData" %>
<spring:htmlEscape defaultHtmlEscape="true" />

<div class="panel panel-accommodation details">
    <ul class="accommodation-items clearfix no-padding">
        <li class="col-xs-12">
            <div class="col-xs-5 col-sm-4 accommodation-image">
                <div class="row">
                    <a href="#" data-toggle="modal" data-target="#accommodation-gallery-modal-${fn:escapeXml(index)}">
                        <c:forEach items="${roomType.images}" var="accommodationImage">
                            <c:if test="${accommodationImage.format == 'list'}">
                                <img src="${accommodationImage.url}" alt="${fn:escapeXml(roomType.name)}">
                            </c:if>
                        </c:forEach>
                    </a>
                </div>
            </div>
            <div class="accommodation-details col-xs-offset-5 col-xs-7 col-sm-offset-4 col-sm-8">
                <div class="row">
                    <div class="col-xs-12 col-sm-8">
                        <div class="col-xs-12">
                            <div class="row">
                                <h3>
                                    ${fn:escapeXml(roomType.name)}
                                </h3>
                            </div>
                        </div>
                        <div class="col-xs-12">
                            <div class="row">
                                <p>${fn:escapeXml(roomType.description)}</p>
                            </div>
                        </div>
                    </div>
                    <div class="col-xs-12 col-sm-4">
                        <div class="price">
                            <c:if test="${not empty fromPrice}">
                                <p><spring:theme code="text.accommodation.details.roomstay.from" text="From"/>&ensp;${fromPrice.formattedValue}</p>
                                <ul class="room-types">
                                    <li>
                                        <spring:theme code="text.accommodation.details.price.description"/>&nbsp;
                                        <c:choose>
                                            <c:when test="${accommodationAvailabilityResponse.lengthOfStay > 1}">
                                                <spring:theme code="text.accommodation.details.price.description.nights" arguments="${accommodationAvailabilityResponse.lengthOfStay}"/>
                                            </c:when>
                                            <c:otherwise>
                                                <spring:theme code="text.accommodation.details.price.description.night" arguments="${accommodationAvailabilityResponse.lengthOfStay}"/>
                                            </c:otherwise>
                                        </c:choose>
                                    </li>
                                </ul>
                            </c:if>
                        </div>
                    </div>
                    <div class="clearfix"></div>
                    <div class="col-xs-12 col-sm-5">
                        <p>
                            <small><spring:theme code="text.accommodation.details.accommodation.occupancy"/>
                                <br />
                                <c:forEach var="guestOccupancy" items="${roomType.occupancies}" varStatus="guestOccupanciesIdx">
                                    ${fn:escapeXml(guestOccupancy.quantityMax)}&nbsp;${fn:escapeXml(guestOccupancy.passengerType.name)}&nbsp;
                                    <c:if test="${guestOccupancy.passengerType.code ne 'adult'}">
                                        <spring:theme code="text.accommodation.details.guest.max.age" arguments="${guestOccupancy.passengerType.maxAge}"/>
                                    </c:if>
                                    <c:if test="${!guestOccupanciesIdx.last}">,&nbsp;</c:if>
                                </c:forEach>
                            </small>
                        </p>
                    </div>
                    <div class="col-xs-12 col-sm-3">
                        <p>
                            <small><spring:theme code="text.accommodation.details.accommodation.size"/><br/>${fn:escapeXml(roomType.sizeMeasurement)}</small>
                        </p>
                    </div>
                </div>
            </div>
        </li>
    </ul>
</div>
<c:if test="${not empty roomType.facilities}">
    <div class="panel panel-accommodation details features">
        <a role="button" href="#collapseOverview-${fn:escapeXml(index)}" class="panel-heading panel-header-link collapsable" data-toggle="collapse"
           aria-expanded="true" aria-controls="collapseOverview-${fn:escapeXml(index)}" id="headingOverview-${fn:escapeXml(index)}">
            <h4 class="panel-title"><spring:theme code="text.accommodation.details.accommodation.overview" text="Overview" /></h4>
        </a>
        <div id="collapseOverview-${fn:escapeXml(index)}" class="collapse-overview panel-collapse collapse in" role="tabpanel" aria-labelledby="headingOverview-${fn:escapeXml(index)}">
            <div class="panel-body">
                <div class="col-xs-12">
                    <div class="row">
                        <p>
                            <strong><spring:theme code="text.accommodation.details.accommodation.features"/></strong>
                        </p>
                        <ul class="small clearfix y_features">
                            <c:forEach var="facility" items="${roomType.facilities}">
                                <li><span class="glyphicon glyphicon-ok" aria-hidden="true"></span>${fn:escapeXml(facility.shortDescription)}</li>
                            </c:forEach>
                        </ul>
                        <a href="#" class="more hidden"><spring:theme code="text.accommodation.details.roomstay.features.more"/></a>
                        <a href="#" class="less hidden"><spring:theme code="text.accommodation.details.roomstay.features.less"/></a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</c:if>
