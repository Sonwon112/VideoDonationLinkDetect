package com.niya.model;

public class VideoDonation {
	private String donationName;
	private String donationURL;
	
	public VideoDonation() {}
	public VideoDonation(String donationName, String donationURL) {
		this.donationName = donationName;
		this.donationURL = donationURL;
	}
	
	public String getDonationName() {
		return donationName;
	}
	public void setDonationName(String donationName) {
		this.donationName = donationName;
	}
	public String getDonationURL() {
		return donationURL;
	}
	public void setDonationURL(String donationURL) {
		this.donationURL = donationURL;
	}
	
}
