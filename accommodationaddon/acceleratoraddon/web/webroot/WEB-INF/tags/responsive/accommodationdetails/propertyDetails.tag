<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="accommodationDetails" tagdir="/WEB-INF/tags/addons/accommodationaddon/responsive/accommodationdetails"%>
<%@ attribute name="property" required="true" type="de.hybris.platform.commercefacades.accommodation.PropertyData" %>
<div class="panel panel-accommodation">
	<accommodationDetails:propertyBasicDetails property="${property}" />
	<accommodationDetails:propertyGallery property="${property}" />
	<accommodationDetails:propertyInfoTabs property="${property}" />
</div>
