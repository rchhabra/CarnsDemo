<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="offerBreakdowns" required="true" type="java.util.List" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<spring:htmlEscape defaultHtmlEscape="true" />
<c:if test="${not empty offerBreakdowns}">
    <div class="responsive-table booking-table">
        <table class="table">
            <thead>
            <tr>
                <th class="pull-left"><spring:theme code="reservation.journey.extras" text="Your Extras:" /></th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${offerBreakdowns}" var="offerBreakdown" varStatus="idx">
                <tr class="responsive-table-item">
                    <c:if test="${offerBreakdown.product.productType != 'ACCOMMODATION' || offerBreakdown.totalFare.basePrice.value.intValue() != 0 }">

                        <td class="responsive-table-cell">${fn:escapeXml(offerBreakdown.quantity)}&nbsp;x&nbsp;${fn:escapeXml(offerBreakdown.product.name)}</td>
                        <td class="status">
                            <c:choose>
                                <c:when test="${offerBreakdown.included}">
                                    <c:choose>
                                        <c:when test="${offerBreakdown.totalFare.totalPrice.value.intValue() == 0}">
                                            <spring:theme code="reservation.extras.free" text="Free" />
                                        </c:when>
                                        <c:otherwise>
                                            <span>
                                            ${fn:escapeXml(offerBreakdown.totalFare.totalPrice.formattedValue)} (<spring:theme code="reservation.extras.included" text="Included" />)
                                            </span>
                                        </c:otherwise>
                                    </c:choose>
                                </c:when>
                                <c:otherwise>
                                    ${fn:escapeXml(offerBreakdown.totalFare.totalPrice.formattedValue)}
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </c:if>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</c:if>
