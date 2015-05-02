package br.com.evcash.dto.request;


public class BaseParamsDTO {

	private final String token;

	private final String locale;

	private final String deviceId;

	public BaseParamsDTO() {
		this.token = null;
		this.locale = null;
		this.deviceId = null;
	}

	/**
	 * Creates a base DTO for passing the parameters to an EvCash web method.
	 * 
	 * @param application
	 *            required. This should be the EvCash application. It is used to
	 *            get the token and IMEI of device
	 */
//	public BaseParamsDTO(Application application) {
//		this.token = ((EvCash) application).getSharedPreferences().getString(
//				Constants.PREFERENCE_TOKEN, null);
//		this.locale = Locale.getDefault().toString();
//		TelephonyManager telephonyManager = (TelephonyManager) application
//				.getSystemService(Context.TELEPHONY_SERVICE);
//		this.deviceId = telephonyManager.getDeviceId();
//	}

	public String getToken() {
		return token;
	}

	public String getLocale() {
		return locale;
	}

	public String getDeviceId() {
		return deviceId;
	}
}
