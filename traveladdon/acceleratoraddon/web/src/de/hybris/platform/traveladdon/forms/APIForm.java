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

public class APIForm
{
	private String uid;
	private String APIType;
	private String documentExpiryDate;
	private String countryOfIssue;
	private String nationality;
	private String documentType;
	private String documentNumber;
	private String dateOfBirth;
	private String title;
	private String firstname;
	private String lastname;
	private String gender;

	/**
	 * @return
	 */
	public String getUid()
	{
		return uid;
	}

	/**
	 * @param uid
	 */
	public void setUid(final String uid)
	{
		this.uid = uid;
	}

	public String getAPIType()
	{
		return APIType;
	}

	public void setAPIType(final String APIType)
	{
		this.APIType = APIType;
	}

	public String getDocumentExpiryDate()
	{
		return documentExpiryDate;
	}

	public void setDocumentExpiryDate(final String documentExpiryDate)
	{
		this.documentExpiryDate = documentExpiryDate;
	}

	public String getCountryOfIssue()
	{
		return countryOfIssue;
	}

	public void setCountryOfIssue(final String countryOfIssue)
	{
		this.countryOfIssue = countryOfIssue;
	}

	public String getNationality()
	{
		return nationality;
	}

	public void setNationality(final String nationality)
	{
		this.nationality = nationality;
	}

	public String getDocumentType()
	{
		return documentType;
	}

	public void setDocumentType(final String documentType)
	{
		this.documentType = documentType;
	}

	public String getDocumentNumber()
	{
		return documentNumber;
	}

	public void setDocumentNumber(final String documentNumber)
	{
		this.documentNumber = documentNumber;
	}

	/**
	 * @return
	 */
	public String getDateOfBirth()
	{
		return dateOfBirth;
	}

	/**
	 * @param dateOfBirth
	 */
	public void setDateOfBirth(final String dateOfBirth)
	{
		this.dateOfBirth = dateOfBirth;
	}

	/**
	 *
	 * @return
	 */
	public String getFirstname()
	{
		return firstname;
	}

	/**
	 *
	 * @param firstname
	 */
	public void setFirstname(final String firstname)
	{
		this.firstname = firstname;
	}

	/**
	 *
	 * @return
	 */
	public String getLastname()
	{
		return lastname;
	}

	/**
	 *
	 * @param lastname
	 */
	public void setLastname(final String lastname)
	{
		this.lastname = lastname;
	}

	/**
	 *
	 * @return
	 */
	public String getGender()
	{
		return gender;
	}

	/**
	 *
	 * @param gender
	 */
	public void setGender(final String gender)
	{
		this.gender = gender;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(final String title)
	{
		this.title = title;
	}
}
