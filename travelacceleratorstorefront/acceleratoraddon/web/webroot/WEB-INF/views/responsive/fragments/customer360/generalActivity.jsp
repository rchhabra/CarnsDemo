<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:url value="manage-booking/booking-details" var="bookingDetailsUrl" />

<div role="tabpanel" class="tab-pane" id="activitySection">
    <div class="pageable-fragment asm-customer360-tab">
        <div class="asm-customer360-activity-tab">
            <h3><spring:theme code="text.customer360.activity.general" text="General"/></h3>
            <div class="pagerActivity pager hidden-xs"></div>
            <table id="asm-customer360-activity-table" class="table techne-table">
                <thead>
                    <tr class="responsive-table-head hidden-xs">
                        <th class="pointer"><spring:theme code="text.customer360.activity.general.type" /></th>
                        <th class="pointer"><spring:theme code="text.customer360.activity.general.id" /></th>
                        <th class="pointer"><spring:theme code="text.customer360.activity.general.description" /></th>
                        <th class="pointer status"><spring:theme code="text.customer360.activity.general.status" /></th>
                        <th class="pointer"><spring:theme code="text.customer360.activity.general.created" /></th>
                        <th class="headerSortUp pointer"><spring:theme code="text.customer360.activity.general.updated" /></th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${fragmentData}" var="activityData">
                        <tr>
                            <td data-th="Type"><spring:theme code="${activityData.type}" /></td>
                            <c:choose>
                                <c:when test="${not empty activityData.url}">
                                    <spring:url value="${activityData.url}" var="link" htmlEscape="false"/>
                                </c:when>
                                <c:when test="${fn:contains(activityData.type, 'order')}">
                                    <spring:url value="/${bookingDetailsUrl}/${activityData.id}" var="link" htmlEscape="false"/>
                                </c:when>
                                <c:otherwise>
                                    <c:set var="link" value="#" />
                                    <c:set var="y_class" value="y_asmSeeFullReservation" />
                                </c:otherwise>
                            </c:choose>
                            <td data-th="ID"><a href="${link}" class="responsive-table-link text-nowrap ${fn:escapeXml(y_class)}"><c:out value="${(activityData.id)}" /></a></td>
                           <td data-th="Description" class="break-word" title="<spring:theme code='${activityData.description}' arguments='${activityData.descriptionArgs}' htmlEscape='true'/>"><spring:theme code="${activityData.description}" arguments="${activityData.descriptionArgs}" htmlEscape="true"/></td>
                            <td data-th="Status" >
                                <c:choose>
                                    <c:when test="${(not empty activityData.status)}">
                                        <spring:theme code="${activityData.status}"/>
                                    </c:when>
                                    <c:otherwise>
                                        <spring:theme code="text.customer360.activity.general.status.undefined" />
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td data-th="Created" data-text="${fn:escapeXml(activityData.created.time)}" ><fmt:formatDate value="${activityData.created}" pattern="dd-MM-yy hh:mm a" /></td>
                            <td data-th="Updated" data-text="${fn:escapeXml(activityData.updated.time)}" ><fmt:formatDate value="${activityData.updated}" pattern="dd-MM-yy hh:mm a" /></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
            <div class="pagerActivity pager visible-xs"></div>
        </div>
    </div>
    <script>
        $("#asm-customer360-activity-table").tablesorter({headers: { 0: { sorter: "text"}, 1: {sorter: "text"},  2: { sorter: "text"}, 3: {sorter: "text"}, 4: {sorter: "text"}, 5: {sorter: "text"} }})
            .tablesorterPager({container: $(".asm-customer360-activity-tab .pager"), size: 2*getAifTablePageSize()});
    </script>
</div>
