<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ attribute name="roomStay" required="true" type="de.hybris.platform.commercefacades.accommodation.RoomStayData" %>
<%@ attribute name="index" required="true" type="java.lang.Integer" %>
<%@ attribute name="roomTypeCode" required="true" type="java.lang.String" %>
<spring:htmlEscape defaultHtmlEscape="true" />

<div class="panel panel-accommodation panel-price-options">

    <a role="button" href="#collapsePriceOptions-${fn:escapeXml(index)}" class="panel-heading panel-header-link collapsable" data-toggle="collapse" aria-expanded="true" aria-controls="collapsePriceOptions-${fn:escapeXml(index)}" id="priceOptions-${fn:escapeXml(index)}">
        <h4 class="panel-title">
            <spring:theme code="text.accommodation.details.accommodation.prices"/>
        </h4>
    </a>

    <div id="collapsePriceOptions-${fn:escapeXml(index)}" class="panel-collapse collapse in" role="tabpanel" aria-labelledby="priceOptions-${fn:escapeXml(index)}">
        <div class="panel-body">
            <ul class="accommodation-items clearfix">
             <c:forEach var="plan" items="${roomStay.ratePlans}" varStatus="planID">
                <li class="row price-option ${plan.availableQuantity gt 0 ? '' : 'fully-booked' }">
                    <div class="col-xs-7 col-md-6">
                       <h5>${fn:escapeXml(plan.description)}</h5>
							<ul class="small feature-list">
								<c:forEach var="guarantee" items="${plan.guarantees}">
								<c:if test="${guarantee.longDescription != null}">
									<li>${fn:escapeXml(guarantee.longDescription)}</li>
								</c:if>
								</c:forEach>
								<c:forEach var="cancelPenalty" items="${plan.cancelPenalties}">
									<c:if test="${cancelPenalty.formattedDescription != null}">
										<li>${fn:escapeXml(cancelPenalty.formattedDescription)}</li>
									</c:if>
								</c:forEach>
								<c:forEach var="ratePlanInclusion" items="${plan.ratePlanInclusions}">
									<li>${fn:escapeXml(ratePlanInclusion.longDescription )}</li>
								</c:forEach>
								<c:forEach var="mealType" items="${plan.mealTypes}">
									<li>${fn:escapeXml(mealType)}</li>
								</c:forEach>
							</ul>
						</div>
                    <div class="col-xs-5 col-md-3">
                        <dl>
                            <dt>
								<spring:theme code="text.accommodation.details.accommodation.occupancy" />
							</dt>
							<dd>
								<span class="sr-only">
									<c:set var="maxGuests" value="0"></c:set>
									<c:forEach var="occupancy" items="${plan.occupancies}">
										<c:set var="maxGuests" value="${ maxGuests + occupancy.quantityMax }"></c:set>
									</c:forEach>
									<spring:theme code="text.accommodation.details.accommodation.max.people.screen.reader" arguments="${maxGuests}" />
								</span>
								<span aria-label="people">
									<c:forEach var="occupancy" items="${plan.occupancies}" varStatus="guestOccupanciesIdx">
											<c:forEach begin="1" end="${occupancy.quantityMax}">
												<span class="person-blue-icon ${occupancy.passengerType.code == 'child' || occupancy.passengerType.code == 'infant' ? 'child-icon' : ''}">&nbsp;</span>
											</c:forEach>
											 <c:if test="${!guestOccupanciesIdx.last}">+ &nbsp;</c:if>
									</c:forEach>
								</span>
							</dd>
                        </dl>
                        <span>
                            <c:set var="stock" value="${plan.availableQuantity}"/>
                            <c:choose>
                                <c:when test="${stock gt 0}">
                                    <c:choose>
                                        <c:when test="${stock > 1}">
                                            <spring:theme code="text.accommodation.details.accommodation.availability.multiple"
                                                          arguments="${stock}"/>
                                        </c:when>
                                        <c:otherwise>
                                            <spring:theme code="text.accommodation.details.accommodation.availability.single"
                                                          arguments="${stock}"/>
                                        </c:otherwise>
                                    </c:choose>
                                </c:when>
                                <c:otherwise>
                                    <spring:theme
                                            code="text.accommodation.details.accommodation.fully.booked"/>
                                </c:otherwise>
                            </c:choose>
                        </span>
                    </div>
                    <div class="col-xs-12 col-md-3 accommodation-details">
                        <c:if test="${stock gt 0}">
                            <c:choose>
                                <c:when test="${plan.totalDiscount.value > 0}">
                                    <div class="ribbon-container">
                                        <c:set var="discountAmount"
                                               value="${plan.totalDiscount.value / plan.wasRate.value * 100}"/>
                                        <fmt:formatNumber var="discountFormatted" value="${discountAmount}"
                                                          maxFractionDigits="0"/>
                                        <span><spring:theme code="text.accommodation.details.roomtype.price.save"
                                                            text="Save"/>&ensp;${discountFormatted}&#37;</span>
                                    </div>
                                    <div class="price">
                                        <p class="previous-price">
                                            <span class="sr-only"><spring:theme
                                                    code="text.accommodation.details.roomtype.price.previous.sr"
                                                    text="Previous price"/></span>${fn:escapeXml(plan.wasRate.formattedValue)}
                                        </p>
                                        <p class="sale-price"><span class="sr-only">
                                            <spring:theme code="text.accommodation.details.roomtype.price.sale.sr"
                                                          text="Sale price"/></span>${fn:escapeXml(plan.actualRate.formattedValue)}
                                        </p>
                                        <ul class="room-types">
                                            <li>
                                                <spring:theme code="text.accommodation.details.price.description"/>&nbsp;
                                                <c:choose>
                                                    <c:when test="${accommodationAvailabilityResponse.lengthOfStay > 1}">
                                                        <spring:theme code="text.accommodation.details.price.description.nights"
                                                                      arguments="${accommodationAvailabilityResponse.lengthOfStay}"/>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <spring:theme code="text.accommodation.details.price.description.night"
                                                                      arguments="${accommodationAvailabilityResponse.lengthOfStay}"/>
                                                    </c:otherwise>
                                                </c:choose>
                                            </li>
                                        </ul>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="price">
                                        <p>${fn:escapeXml(plan.actualRate.formattedValue)}</p>
                                        <ul class="room-types">
                                            <li>
                                                <spring:theme code="text.accommodation.details.price.description"/>&nbsp;
                                                <c:choose>
                                                    <c:when test="${accommodationAvailabilityResponse.lengthOfStay > 1}">
                                                        <spring:theme code="text.accommodation.details.price.description.nights"
                                                                      arguments="${accommodationAvailabilityResponse.lengthOfStay}"/>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <spring:theme code="text.accommodation.details.price.description.night"
                                                                      arguments="${accommodationAvailabilityResponse.lengthOfStay}"/>
                                                    </c:otherwise>
                                                </c:choose>
                                            </li>
                                        </ul>
                                    </div>
                                </c:otherwise>
                            </c:choose>

                            <div class="pull-right">
                                <label class="control-label" for="numberOfAccomodation${fn:escapeXml(index)}${fn:escapeXml(planID.index)}">
                                    <spring:theme code="text.accommodation.details.accommodation.number.of.accommodations"/>
                                </label>
                                <div class="y_accommodationSelect controls">
                                    <input type="hidden" name="ratePlanCode"
                                           value="${fn:escapeXml(plan.code)}" class="y_ratePlanCode"/>
                                    <c:forEach var="roomRate" items="${plan.roomRates}" varStatus="roomRateIdx">
                                        <fmt:formatDate value="${roomRate.stayDateRange.startTime}" var="roomRateDateFormatted"
                                                        pattern="dd/MM/yyyy"/>
                                        <input type="hidden" name="roomRate${fn:escapeXml(roomRateIdx.index)}"
                                               code="${fn:escapeXml(roomRate.code)}" value="${fn:escapeXml(roomRateDateFormatted)}" class="y_roomRate"/>
                                    </c:forEach>

                                    <c:set var="reservedRatePlanQty" value="0"/>
                                    <c:forEach var="reservedRoomStay" items="${accommodationAvailabilityResponse.reservedRoomStays}">
                                        <c:forEach var="reservedRoomType" items="${reservedRoomStay.roomTypes}">
                                            <c:if test="${roomTypeCode == reservedRoomType.code}">
                                                <c:set var="reservedRoomFlag" value="true"/>
                                            </c:if>
                                        </c:forEach>
                                        <c:if test="${reservedRoomFlag}">
                                            <c:forEach var="reservedRatePlan" items="${reservedRoomStay.ratePlans}">
                                                <c:if test="${reservedRatePlan.code == plan.code}">
                                                    <c:set var="reservedRatePlanQty" value="${reservedRatePlanQty + 1}"/>
                                                </c:if>
                                            </c:forEach>
                                        </c:if>
                                        <c:set var="reservedRoomFlag" value="false"/>
                                    </c:forEach>

                                    <c:set var="maxStockQuantity" value="${accommodationMaxStockLevel}"/>
                                    <c:if test="${plan.availableQuantity lt maxStockQuantity}">
                                        <c:set var="maxStockQuantity" value="${plan.availableQuantity}"/>
                                    </c:if>
                                    
                                    <select id="numberOfAccomodation_${fn:escapeXml(index)}_${fn:escapeXml(planID.index)}" class="y_numberOfRooms form-control" aria-invalid="false" name="numberOfRooms" data-plandescription="${fn:escapeXml(plan.description)}" data-maxstockquantity="${fn:escapeXml(maxStockQuantity)}">
                                        <c:forEach begin="0" end="${maxStockQuantity}" varStatus="loop">
                                            <c:choose>
                                                <c:when test="${reservedRatePlanQty == loop.index}">
                                                    <option value="${fn:escapeXml(loop.index)}" selected>${fn:escapeXml(loop.index)}</option>
                                                </c:when>
                                                <c:otherwise>
                                                    <option value="${fn:escapeXml(loop.index)}">${fn:escapeXml(loop.index)}</option>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:forEach>
                                    </select>
                                </div>
                            </div>
                        </c:if>
                    </div>
                </li>
            </c:forEach>
            </ul>
        </div>
    </div>

</div>
