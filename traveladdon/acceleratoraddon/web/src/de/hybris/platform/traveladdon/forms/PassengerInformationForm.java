/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2017 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */

package de.hybris.platform.traveladdon.forms;

public class PassengerInformationForm
{
	private String passengerTypeCode;
	private String passengerTypeName;
	private String frequentFlyerMembershipNumber;
	private String reasonForTravel;
	private boolean frequentFlyer;
	private String selectedSavedTravellerUId;
	private boolean saveDetails;
	private String email;
	private String contactNumber;
	private boolean validateContactNumber;

	private String title;
	private String firstname;
	private String lastname;
	private String gender;

	public String getTitle()
	{
		return title;
	}

	public void setTitle(final String title)
	{
		this.title = title;
	}

	public String getFirstname()
	{
		return firstname;
	}

	public void setFirstname(final String firstname)
	{
		this.firstname = firstname;
	}

	public String getLastname()
	{
		return lastname;
	}

	public void setLastname(final String lastname)
	{
		this.lastname = lastname;
	}

	public String getGender()
	{
		return gender;
	}

	public void setGender(final String gender)
	{
		this.gender = gender;
	}

	public boolean isFrequentFlyer()
	{
		return frequentFlyer;
	}

	public void setFrequentFlyer(final boolean frequentFlyer)
	{
		this.frequentFlyer = frequentFlyer;
	}

	public String getFrequentFlyerMembershipNumber()
	{
		return frequentFlyerMembershipNumber;
	}

	public void setFrequentFlyerMembershipNumber(final String frequentFlyerMembershipNumber)
	{
		this.frequentFlyerMembershipNumber = frequentFlyerMembershipNumber;
	}

	public boolean isSaveDetails()
	{
		return saveDetails;
	}

	public void setSaveDetails(final boolean saveDetails)
	{
		this.saveDetails = saveDetails;
	}

	public String getReasonForTravel()
	{
		return reasonForTravel;
	}

	public void setReasonForTravel(final String reasonForTravel)
	{
		this.reasonForTravel = reasonForTravel;
	}

	public String getPassengerTypeName()
	{
		return passengerTypeName;
	}

	public void setPassengerTypeName(final String passengerTypeName)
	{
		this.passengerTypeName = passengerTypeName;
	}

	public String getPassengerTypeCode()
	{
		return passengerTypeCode;
	}

	public void setPassengerTypeCode(final String passengerTypeCode)
	{
		this.passengerTypeCode = passengerTypeCode;
	}

	public String getSelectedSavedTravellerUId()
	{
		return selectedSavedTravellerUId;
	}

	public void setSelectedSavedTravellerUId(final String selectedSavedTravellerUId)
	{
		this.selectedSavedTravellerUId = selectedSavedTravellerUId;
	}

	public void setEmail(final String email)
	{
		this.email = email;
	}

	public String getEmail()
	{
		return email;
	}

	/**
	 * @return the contactNumber
	 */
	public String getContactNumber()
	{
		return contactNumber;
	}

	/**
	 * @return the validateContactNumber
	 */
	public boolean isValidateContactNumber()
	{
		return validateContactNumber;
	}

	/**
	 * @param contactNumber
	 *           the contactNumber to set
	 */
	public void setContactNumber(final String contactNumber)
	{
		this.contactNumber = contactNumber;
	}

	/**
	 * @param validateContactNumber
	 *           the validateContactNumber to set
	 */
	public void setValidateContactNumber(final boolean validateContactNumber)
	{
		this.validateContactNumber = validateContactNumber;
	}
}
