<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fareselection" tagdir="/WEB-INF/tags/addons/traveladdon/responsive/fareselection"%>
<%@ attribute name="tabId" required="true" type="java.lang.Integer"%>
<%@ attribute name="upgradableOptionsPerLeg" required="true" type="de.hybris.platform.commercefacades.travel.PricedItineraryData"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<c:forEach items="${upgradableOptionsPerLeg.itineraryPricingInfos}" var="itineraryInfoData" varStatus="index">
	<c:if test="${!index.first}">
		<li class="list-group-item row">
			<div class="row input-row">
				<c:choose>
					<c:when test="${index.first}">
						<div class="col-xs-12 passenger-extras">
							<div id="y_upgradeBundleFormHiddenHtml" class="clearfix">
								<input type="hidden" id="y_refNumber" value="${fn:escapeXml(upgradableOptionsPerLeg.originDestinationRefNumber)}" />
								<fareselection:addBundleToCartForm itineraryPricingInfo="${itineraryInfoData}" pricedItinerary="${upgradableOptionsPerLeg}" />
								<input type="checkbox" id="${fn:escapeXml(itineraryInfoData.bundleType)}-tab${fn:escapeXml(tabId)}" value="${fn:escapeXml(itineraryInfoData.bundleType)}" class="y_bundleType" />
								<label class="extra-select col-xs-12 col-xs-offset-0 col-sm-8 col-sm-offset-2 col-md-7 col-md-offset-2 col-lg-6 col-lg-offset-3" for="${fn:escapeXml(itineraryInfoData.bundleType)}-tab${fn:escapeXml(tabId)}">
									<span class="col-xs-6">${fn:escapeXml(itineraryInfoData.bundleTypeName)}</span>
									<span class="col-xs-6">
										<span class="selected">
											<span class="glyphicon glyphicon-ok" aria-hidden="true"></span>
										</span>
										<span class="select">
											<spring:theme code="text.accommodation.details.accommodation.select.price" text="select" />
										</span>
										<span>${fn:escapeXml(itineraryInfoData.totalFare.totalPrice.formattedValue)}</span>
									</span>
								</label>
							</div>
						</div>
					</c:when>
					<c:otherwise>
						<div class="col-xs-12 passenger-extras">
							<div id="y_upgradeBundleFormHiddenHtml" class="clearfix">
								<input type="checkbox" id="${fn:escapeXml(itineraryInfoData.bundleType)}-tab${fn:escapeXml(tabId)}" value="${fn:escapeXml(itineraryInfoData.bundleType)}" class="y_bundleType" /> <input type="hidden" id="y_refNumber" value="${fn:escapeXml(upgradableOptionsPerLeg.originDestinationRefNumber)}" />
								<fareselection:addBundleToCartForm itineraryPricingInfo="${itineraryInfoData}" pricedItinerary="${upgradableOptionsPerLeg}" />
								<label class="extra-select col-xs-12 col-xs-offset-0 col-sm-8 col-sm-offset-2 col-md-7 col-md-offset-2 col-lg-6 col-lg-offset-3" for="${fn:escapeXml(itineraryInfoData.bundleType)}-tab${fn:escapeXml(tabId)}">
									<span class="col-xs-6">${fn:escapeXml(itineraryInfoData.bundleTypeName)}</span>
									<span class="col-xs-6">
										<span class="selected">
											<span class="glyphicon glyphicon-ok" aria-hidden="true"></span>
										</span>
										<span class="select">
											<spring:theme code="text.accommodation.details.accommodation.select.price" text="select" />
										</span>
										<span>${fn:escapeXml(itineraryInfoData.totalFare.totalPrice.formattedValue)}</span>
									</span>
								</label>
							</div>
					</c:otherwise>
				</c:choose>
			</div>
		</li>
	</c:if>
</c:forEach>
