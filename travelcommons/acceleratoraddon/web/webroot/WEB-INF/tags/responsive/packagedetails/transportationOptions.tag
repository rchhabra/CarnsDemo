<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="packageDetails" tagdir="/WEB-INF/tags/addons/travelcommons/responsive/packagedetails"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ attribute name="transportPackageResponse" required="true" type="de.hybris.platform.commercefacades.packages.response.TransportPackageResponseData" %>
<spring:htmlEscape defaultHtmlEscape="true" />

<c:if test="${not empty transportPackageResponse.fareSearchResponse && not empty transportPackageResponse.fareSearchResponse.pricedItineraries}">
    <div class="row form-group y_packageFareSelection">
        <div class="col-xs-12 col-sm-offset-8 col-sm-offset-8 col-sm-4">
            <button class="y_flightOptionsCollapse btn btn-primary col-xs-12" type="button" data-toggle="collapse" data-target="#flightOptionsCollapse" aria-expanded="false" aria-controls="flightOptionsCollapse">
                <span class="show-text">
                    <spring:theme code="text.package.details.button.show.transportation.options" text="Show more flight options" />
                </span>
                <span class="hide-text hidden">
                    <spring:theme code="text.package.details.button.hide.transportation.options" text="Hide flight options" />
                </span>
            </button>
        </div>
    </div>
    <div id="flightOptionsCollapse" class="collapse y_fareSelectionSection fare-table-inner-wrap">
        <input type="hidden" id="y_tripType" value="${fn:escapeXml(tripType)}"/>
        <%-- Outbound offerings table --%>
        <div class="panel panel-accommodation panel-journey-choices">
            <div class="panel-heading">
                <h3 class="panel-title">
                    ${fn:escapeXml(origin)}&nbsp;
                    <span aria-hidden="true" class="glyphicon glyphicon-arrow-right"></span>
                    ${fn:escapeXml(destination)}
                </h3>
            </div>

            <div id="y_outbound" class="y_fareResultTabWrapper clearfix">
                <packageDetails:packageOfferingList fareSelection="${transportPackageResponse.fareSearchResponse}" refNumber="${outboundRefNumber}"/>
            </div>
        </div>
        <c:if test="${tripType == 'RETURN'}">
            <%-- Return offerings table --%>
            <div class="panel panel-accommodation panel-journey-choices">
                <div class="panel-heading">
                    <h3 class="panel-title">
                        ${fn:escapeXml(destination)}&nbsp;
                        <span aria-hidden="true" class="glyphicon glyphicon-arrow-right"></span>
                        ${fn:escapeXml(origin)}
                    </h3>
                </div>

                <div id="y_inbound" class="y_fareResultTabWrapper clearfix">
                    <packageDetails:packageOfferingList fareSelection="${transportPackageResponse.fareSearchResponse}" refNumber="${inboundRefNumber}"/>
                </div>
            </div>
        </c:if>
    </div>
</c:if>
