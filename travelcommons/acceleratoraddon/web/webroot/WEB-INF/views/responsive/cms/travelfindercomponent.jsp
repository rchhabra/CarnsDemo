<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="travelfinder" tagdir="/WEB-INF/tags/addons/travelcommons/responsive/travelfinder"%>
<div role="tabpanel" class="tab-pane active" id="flight-hotel">
	<travelfinder:travelfinderform url="/view/TravelFinderComponentController/search" bookingJourney="TRANSPORT_ACCOMMODATION"/>
</div>
