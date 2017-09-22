package com.cinetpay.sdk;

import android.text.TextUtils;

/**
 * 
 * @author Istat: Toukea tatsi Jephte
 * 
 */
public final class MerchantService {

	public final static String PLATFORM_PRODUCTION = "P";
    public final static String PLATFORM_TEST = "T";
	private final static String STATUS_MERCHANT_VALID = "00";

	private String apiKey;
    private String siteId;
    private String logoURL;
    private String platform = PLATFORM_PRODUCTION;
    private String notificationURL;

	public ServiceInfo info = new ServiceInfo();

	public MerchantService(String apikey, String business) {
		init(apikey, business, null, true);
	}

	public MerchantService(String apikey, String business, boolean production) {
		init(apikey, business, null, production);
	}

	public MerchantService(String apikey, String business, String notificationURL,
                           boolean production) {
		init(apikey, business, notificationURL, production);
	}

	private void init(String apikey, String business, String notificationURL, boolean production) {
		this.apiKey = apikey;
		this.siteId = business;
		this.notificationURL = notificationURL;
		setProductionPlatform(production);
	}

	public boolean hasNotificationURLConfigured() {
		return !TextUtils.isEmpty(notificationURL);
	}

	public ServiceInfo getInfo() {
		return info;
	}

	public void setLogoURL(String logoURL) {
		this.logoURL = logoURL;
	}

	public String getLogoURL() {
		return logoURL;
	}

	public String getEmail() {
		return info.email_serv;
	}

	public String getRegion() {
		return info.pays_serv;
	}

	public String getApiKey() {
		return apiKey;
	}

	@Deprecated
	public String getBusinessId() {
		return siteId;
	}

	public String getSiteId() {
		return siteId;
	}

	public String getName() {
		return info.getName();
	}

	public String getNotificationURL() {
		return notificationURL;
	}

	public MerchantService setNotificationURL(String notificationURL) {
		this.notificationURL = notificationURL;
		return this;
	}

	public static class MerchantDescription {
		public String name = "NON", email = "NON";

		public MerchantDescription(String name, String email) {
			this.name = name;
			this.email = email;
		}
	}

	@Override
	public String toString() {
		return "name=" + info.getName() + "::apikey=" + apiKey + "::id=" + siteId;
	}

	public MerchantService setProductionPlatform(boolean production) {
		if (production) {
            platform = PLATFORM_PRODUCTION;
        } else {
            platform = PLATFORM_TEST;
        }
		return this;
	}

	public boolean isProductionPlatform() {
		return platform.equals(PLATFORM_PRODUCTION);
	}

	public boolean isValidMerchant() {
		return info.status_code_message[0].equals("00");
	}

	public String getMerchantStatus() {
		return info.status_code_message[1];
	}

	public String getPlatform() {
		return platform;
	}

	public MerchantService setPlatform(String platform) {
		if (!platform.equals(PLATFORM_PRODUCTION) && !platform.equals(PLATFORM_TEST)) {
            throw new UnsupportedOperationException("mode unsupported: mode supported: " +
                    "MerchanServive.PRODUCTION='P' or MerchandService.TEST='T'");
        }
		this.platform = platform;
		return this;
	}

	public final static class ServiceInfo {

		public String cpm_site_id;
        public String id_serv;
        public String id_clt_fk;
        public String nom_serv = "MARCHANT CINETPAY";
        public String description_serv;
        public String pays_serv;
        public String ville_serv;
        public String email_serv;
        public String created_at;
        public String updated_at;
        public String activated_at;
        public String solde_serv;
        public String credit_serv;
        public String debit_serv;
        public String[] status_code_message = new String[] { "1024", "NOT_VERIFIED" };
		public String syntax_operator_new_om;
        public String syntax_operator_new_momo;
        public String message_operator_om;
        public String message_operator_momo;
        public String message_operator_moov;

		public String getActivated_at() {
			return activated_at;
		}

		public String getSiteId() {
			return cpm_site_id;
		}

		public String getCreated() {
			return created_at;
		}

		public String getCredit() {
			return credit_serv;
		}

		public String getDebit() {
			return debit_serv;
		}

		public String getDescription() {
			return description_serv;
		}

		public String getEmail() {
			return email_serv;
		}

		public String getId_clt_fk() {
			return id_clt_fk;
		}

		public String getServiceId() {
			return id_serv;
		}

		public String getName() {
			return nom_serv;
		}

		public String getCountry() {
			return pays_serv;
		}

		public String getSolde() {
			return solde_serv;
		}

		public String getUpdated_at() {
			return updated_at;
		}

		public String getRegion() {
			return ville_serv;
		}

		public ServiceAccount getAccount() {
			double solde = 0, credit = 0, debit = 0;
			try {
				solde = Double.valueOf(solde_serv);
				credit = Double.valueOf(credit_serv);
				debit = Double.valueOf(debit_serv);
			} catch (Exception e) {

			}
			return new ServiceAccount(solde, credit, debit);
		}

		public boolean isValidMerchant() {
			return status_code_message[0].equals(STATUS_MERCHANT_VALID);
		}

		public String getMerchantStatus() {
			return status_code_message[1];
		}
	}

}
