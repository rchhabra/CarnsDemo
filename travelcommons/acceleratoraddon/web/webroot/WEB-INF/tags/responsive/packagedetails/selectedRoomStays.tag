<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="packageDetails" tagdir="/WEB-INF/tags/addons/travelcommons/responsive/packagedetails"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ attribute name="roomStays" required="true" type="java.util.List"%>
<spring:htmlEscape defaultHtmlEscape="true" />
<c:if test="${not empty roomStays}">
	<div class="panel-accommodation-package">
		<div class="panel panel-accommodation clearfix">
			<div class="panel-heading">
				<h3 class="panel-title">
					<spring:theme code="text.package.details.rooms.included" text="Rooms included in your package" />
				</h3>
			</div>
			<ul class="accommodation-items clearfix group-option">
				<c:forEach var="roomStay" items="${roomStays}" varStatus="idx">
						<c:forEach var="roomType" items="${roomStay.roomTypes}" varStatus="accommodationID">
							<li class="col-xs-12 ${!roomStay.nonModifiable?'y_selectedRoomStayContainer':''}">
								<%--Only populate these details when on package details and no package in the cart--%>
								<c:if test="${not empty isPackageInCart && !isPackageInCart and !roomStay.nonModifiable}">
									<packageDetails:accommodationFormAttributes roomStay="${roomStay}" roomType="${roomType}" />
								</c:if>
								<div class="col-xs-5 col-sm-4 accommodation-image">
									<div class="row">
										<a href="#" data-toggle="modal" data-target="#selected-accommodation-gallery-modal-${idx.index}">
											<c:forEach items="${roomType.images}" var="accommodationImage">
												<c:if test="${accommodationImage.format == 'list'}">
													<img src="${accommodationImage.url}" alt="${fn:escapeXml(roomType.name)}">
												</c:if>
											</c:forEach>
										</a>
									</div>
								</div>
								<div class="accommodation-details col-xs-offset-5 col-xs-7 col-sm-offset-4 col-sm-8">
									<div class="row">
										<div class="col-xs-12">
											<div class="clearfix">
												<div class="col-xs-12">
													<div class="row package-room">
														<h3>${fn:escapeXml(roomType.name)}</h3>
														<c:if test="${!roomStay.nonModifiable && amend}">
															<packageDetails:addNewRoomAlert />
														</c:if>
													</div>
												</div>
												<div class="col-xs-12">
													<div class="row">
														<p>${fn:escapeXml(roomType.description)}</p>
														<p>${fn:escapeXml(roomStay.ratePlans[0].description)}</p>
													</div>
												</div>
											</div>
										</div>
										<div class="col-xs-12 col-sm-4 col-md-5 accommodation-number-people">
											<dl>
												<dt>
													<spring:theme code="text.accommodation.details.accommodation.occupancy" text="Occupancy" />
												</dt>
												<dd>
													<span class="sr-only">
														<c:set var="maxGuests" value="0" />
														<c:forEach var="occupancy" items="${roomType.occupancies}">
															<c:set var="maxGuests" value="${ maxGuests + occupancy.quantityMax }" />
														</c:forEach>
														<spring:theme code="text.accommodation.details.accommodation.max.people.screen.reader" arguments="${maxGuests}" />
													</span>
													<span aria-label="people">
														<c:forEach var="occupancy" items="${roomType.occupancies}" varStatus="guestOccupanciesIdx">
															<c:forEach begin="1" end="${occupancy.quantityMax}">
																<span class="person-icon ${occupancy.passengerType.code == 'child' || occupancy.passengerType.code == 'infant' ? 'child-icon' : ''}">&nbsp;</span>
															</c:forEach>
															<c:if test="${!guestOccupanciesIdx.last}">+ &nbsp;</c:if>
														</c:forEach>
													</span>
												</dd>
											</dl>
										</div>
										<div class="col-xs-12 col-sm-4 col-md-3">
											<p>
												<small><spring:theme code="text.accommodation.details.accommodation.size" text="Size" /><br />${fn:escapeXml(roomType.sizeMeasurement)}</small>
											</p>
										</div>
										<div class="extra-room-details col-xs-12 col-sm-4 col-md-4">
											<p>
												<a href="#" data-toggle="modal" data-target="#selected-extra-room-details-modal-${idx.index}">
													<spring:theme code="text.package.details.room.details.link" text="Room details" />
												</a>
											</p>
										</div>
									</div>
								</div>
								<packageDetails:packageAccommodationGallery roomType="${roomType}" index="${idx.index}" />
								<packageDetails:packageRoomDetails roomType="${roomType}" roomStayIndex="${idx.index}" />
							</li>
						</c:forEach>
				</c:forEach>
			</ul>
		</div>
	</div>
</c:if>
