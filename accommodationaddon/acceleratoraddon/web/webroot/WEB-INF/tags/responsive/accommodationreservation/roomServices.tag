<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ attribute name="roomStayServices" required="true" type="java.util.List"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<c:forEach items="${roomStayServices}" var="service">
	<tr class="responsive-table-item">
		<td class="hidden-sm hidden-md hidden-lg"></td>
		<td class="responsive-table-cell">${fn:escapeXml(service.quantity)}x${fn:escapeXml(service.serviceDetails.product.name)}</td>
		<td class="hidden-sm hidden-md hidden-lg"><spring:theme code="text.cms.accommodationbreakdown.basePrice" text="Base Price" /></td>
		<td class="responsive-table-cell">${fn:escapeXml(service.price.basePrice.formattedValue)}</td>
		<td class="hidden-sm hidden-md hidden-lg"><spring:theme code="text.cms.accommodationbreakdown.taxes" text="Taxes" /></td>
		<td class="responsive-table-cell">---</td>
		<td class="hidden-sm hidden-md hidden-lg"><spring:theme code="text.cms.accommodationbreakdown.fees" text="Fees" /></td>
		<td class="responsive-table-cell">---</td>
		<td class="hidden-sm hidden-md hidden-lg"><spring:theme code="text.cms.accommodationbreakdown.discounts" text="Discounts" /></td>
		<td class="responsive-table-cell">---</td>
		<td class="hidden-sm hidden-md hidden-lg"><spring:theme code="text.cms.accommodationbreakdown.total" text="Total" /></td>
		<td class="status">${fn:escapeXml(service.price.total.formattedValue)}</td>
	</tr>
</c:forEach>
