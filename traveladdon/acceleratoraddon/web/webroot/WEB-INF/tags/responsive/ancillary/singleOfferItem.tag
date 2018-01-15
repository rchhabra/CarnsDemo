<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ attribute name="offerGroupCode" required="true" type="java.lang.String"%>
<%@ attribute name="index" required="true" type="java.lang.Integer"%>
<%@ attribute name="tabIndex" required="true" type="java.lang.Integer"%>
<spring:htmlEscape defaultHtmlEscape="true" />

<c:choose>
	<c:when test="${not empty traveller}">
		<c:forEach var="transportOffering" items="${transportOfferings}">
			<c:if test="${traveller.travellerStatusInfo[transportOffering.code] == 'CHECKED_IN'}">
				<c:set var="noAmend" value="true" />
			</c:if>
		</c:forEach>
		<div class="row input-row">
			<div class="col-xs-3 passenger-descriptor">
				<c:set var="travellerCodeMap" value="${travellersNamesMap[traveller.travellerInfo.passengerType.code]}" />
				<label for="${fn:escapeXml(fn:toLowerCase(offerGroupCode))}ancillary${fn:escapeXml(index+1)}">${fn:escapeXml(travellerCodeMap[traveller.label])}</label>
				<input type="hidden" name="travellerCode" class="y_ancillary_travellerCode" value="${fn:escapeXml(traveller.label)}">
			</div>
			<div class="col-xs-9 passenger-extras">
				<c:forEach var="offerPricingInfoData" items="${offerPricingInfos}">
					<c:forEach var="travellerBreakdownData" items="${offerPricingInfoData.travellerBreakdowns}">
						<c:set var="travellerMinOfferQty" value="${offerPricingInfoData.travelRestriction.travellerMinOfferQty > 1 ? offerPricingInfoData.travelRestriction.travellerMinOfferQty : 1}" />
						<c:if test="${travellerBreakdownData.traveller.label == traveller.label}">
							<c:set var="contained" value="false" />
							<c:forEach var="type" items="${offerPricingInfoData.travelRestriction.passengerTypes}">
								<c:if test="${type == travellerBreakdownData.traveller.travellerInfo.passengerType.code}">
									<c:set var="contained" value="true" />
								</c:if>
							</c:forEach>
							<c:choose>
								<c:when test="${empty offerPricingInfoData.travelRestriction.passengerTypes || contained}">
									<div class="col-xs-7 extras-alignment-right">
										<label for="${fn:escapeXml(fn:toLowerCase(offerGroupCode))}${fn:escapeXml(index+1)}-tab${fn:escapeXml(tabIndex+1)}">${fn:escapeXml(offerPricingInfoData.product.name)}</label>
									</div>
									<div class="col-xs-5 extras-alignment-left">
										<label>${fn:escapeXml(travellerBreakdownData.passengerFare.totalFare.formattedValue)}
											<c:choose>
											<c:when test="${travellerBreakdownData.quantity >= 1}">
												<input type="checkbox" id="${fn:escapeXml(fn:toLowerCase(offerGroupCode))}${fn:escapeXml(index+1)}-tab${fn:escapeXml(tabIndex+1)}" checked="checked" value="${fn:escapeXml(offerPricingInfoData.product.code)}" class="y_OfferProductCheckBoxSelection" aria-labelledby="${fn:escapeXml(fn:toLowerCase(offerGroupCode))}" min="${fn:escapeXml(travellerMinOfferQty)}"
													${noAmend ? 'disabled' : '' }>
											</c:when>
											<c:otherwise>
												<input type="checkbox" id="${fn:escapeXml(fn:toLowerCase(offerGroupCode))}${fn:escapeXml(index+1)}-tab${fn:escapeXml(tabIndex+1)}" class="y_OfferProductCheckBoxSelection" value="${fn:escapeXml(offerPricingInfoData.product.code)}" aria-labelledby="${fn:escapeXml(fn:toLowerCase(offerGroupCode))}" min="${fn:escapeXml(travellerMinOfferQty)}" ${fn:escapeXml(noAmend ? 'disabled' : '' )}>
											</c:otherwise>
										</c:choose>
										</label>
									</div>
									<c:set var="travellerMaxOfferQty" value="${offerPricingInfoData.travelRestriction.travellerMaxOfferQty}" />
								</c:when>
								<c:otherwise>
									<div>
										<label>
											<spring:theme code="text.ancillary.offers.product.not.available" text="Not available for this passenger" />
										</label>
									</div>
								</c:otherwise>
							</c:choose>
						</c:if>
					</c:forEach>
				</c:forEach>
			</div>
		</div>
	</c:when>
	<c:otherwise>
		<div class="row input-row">
			<div class="col-xs-12 passenger-extras">
				<c:forEach var="offerPricingInfoData" items="${offerPricingInfos}">
					<c:forEach var="travellerBreakdownData" items="${offerPricingInfoData.travellerBreakdowns}">
						<c:if test="${travellerBreakdownData.traveller.travellerStatusInfo[transportOffering.code] == 'CHECKED_IN'}">
							<c:set var="noAmend" value="${noAmend && true}" />
						</c:if>
					</c:forEach>
					<c:set var="travellerMinOfferQty" value="${offerPricingInfoData.travelRestriction.travellerMinOfferQty > 1 ? offerPricingInfoData.travelRestriction.travellerMinOfferQty : 1}" />
					<c:set var="travellerMaxOfferQty" value="${offerPricingInfoData.travelRestriction.travellerMaxOfferQty}" />
					<label for="${fn:escapeXml(fn:toLowerCase(offerGroupCode))}${fn:escapeXml(index+1)}-tab${fn:escapeXml(tabIndex+1)}">${fn:escapeXml(offerPricingInfoData.product.name)} &nbsp; ${fn:escapeXml(offerPricingInfoData.bookingBreakdown.passengerFare.totalFare.formattedValue)} </label>
					<c:choose>
						<c:when test="${offerPricingInfoData.bookingBreakdown.quantity >= 1}">
							<input type="checkbox" id="${fn:escapeXml(fn:toLowerCase(offerGroupCode))}${fn:escapeXml(index+1)}-tab${fn:escapeXml(tabIndex+1)}" checked="checked" value="${fn:escapeXml(offerPricingInfoData.product.code)}" class="y_OfferProductCheckBoxSelection" aria-labelledby="${fn:escapeXml(fn:toLowerCase(offerGroupCode))}" min="${fn:escapeXml(travellerMinOfferQty)}"
								${noAmend ? 'disabled' : '' }>
						</c:when>
						<c:otherwise>
							<input type="checkbox" id="${fn:escapeXml(fn:toLowerCase(offerGroupCode))}${fn:escapeXml(index+1)}-tab${fn:escapeXml(tabIndex+1)}" class="y_OfferProductCheckBoxSelection" value="${fn:escapeXml(offerPricingInfoData.product.code)}" aria-labelledby="${fn:escapeXml(fn:toLowerCase(offerGroupCode))}" min="${fn:escapeXml(travellerMinOfferQty)}" ${fn:escapeXml(noAmend ? 'disabled' : '' )}>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</div>
		</div>
	</c:otherwise>
</c:choose>
