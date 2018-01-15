<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="b2b-order" tagdir="/WEB-INF/tags/addons/travelacceleratorstorefront/responsive/order"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<ycommerce:testId code="orderDetail_overview_section">
	<b2b-order:accountOrderDetailsOverview order="${orderData}" />
</ycommerce:testId>
