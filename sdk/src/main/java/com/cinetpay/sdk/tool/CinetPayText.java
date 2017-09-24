package com.cinetpay.sdk.tool;

/**
 *
 * @author Istat: Toukea tatsi Jephte
 *
 */
public class CinetPayText {
	public static String SUCCES_P = "Votre paiement de $amount à $service a été effectué avec succès.\nCinetPay vous remercie";
	public static String SUCCES_P_WITH_NAME = "$name, votre paiement de $amount à $service a été effectué avec succès.\nCinetPay vous remercie";
	public static String PAYMENT_FAILED = "Désolé, Votre paiement à échoué.\nCinetPay vous remercie";
	public static String MERCHANT_NOT_FOUND = "Désolé, ce marchand n'existe pas chez CinetPay.\nCinetPay vous remercie";
	public static String INSUFFICIENT_BALANCE = "Désolé, votre Solde est insuffisant.\nCinetPay vous remercie";
	public static String OTP_CODE_ERROR = "Votre paiement a échoué. Code d'autorisation invalide. Merci de réessayer.";
	public static String WAITING_CUSTOMER_TO_VALIDATE = "$type en attente de validation. Veuillez effectuer la validation sur votre mobile.\nCinetPay vous remercie";
	public static String ERROR_SITE_ID_NOTVALID = "Désolé, ce service n'existe pas chez CinetPay.\nCinetPay vous remercie";
	public static String GATEWAY_TIMEOUT = "Désolé, temps d'attente de la passerelle CinetPay écoulé.\nCinetPay vous remercie";
	public static String UNKNOWN_ERROR = "Désolé, une erreur inattendue est survenue durant le traitement. Veillez réessayer plus tard.\nCinetPay vous remercie";
	public static String NETWORK_ERROR = "Désolé, impossible de joindre la plateforme CinetPay.Merci de vérifier votre connexion internet...";
	public static String NETWORK_ERROR_EXPLICIT = "Désolé, pas de connexion internet. Connectez vous à internet et réessayez.\nCinetPay vous remercie";
	public static String ERROR_MOMOPAY_UNAVAILABLE = "Désolé, impossible de joindre la plateforme de paiement MTN mobile Money.\nCinetPay vous remercie";
	public static String ERROR_OMPAY_UNAVAILABLE = "Désolé, impossible de joindre la plateforme de paiement Orange Money.\nCinetPay vous remercie";
	public static String pay_in_process = "Paiement en cours...";
	public static String cinetpay_goodies = "Developpeurs"
			+ "\n-----------------" + "\nAndroid Mobile"
			+ "\n_________________" + "\n1)**************" + "\nToukea tatsi"
			+ "\nttjsoftz@gmail.com" + "\n40101383" + "\n-----------------"
			+ "\nPHP/HTML Serveur" + "\n_________________"
			+ "\n1)**************" + "\nDaniel Dindji"
			+ "\ndaniel.dindji@gmail.com" + "\n_________________"
			+ "\nversion 2.0" + "\n2015/02/18 10:33";

	public static String ORANGE_MONEY_LABEL = "Orange Money";
	public static String MOBILE_MONEY_LABEL = "Mobile Money";
	public static String MOOV_FLOOZ_LABEL = "Flooz";
	public static String LABEL_OK = "OK";
	public static String LABEL_OUPS = "Oups!!!";
	public static String PHONE_NOT_SUPPORTED = "Le numéro de téléphone que vous avez saisi n'est pas pris en charge par CinetPay. les numéros pris en charge par CinetPay sont exclusivement Orange, MTN et Moov côte d'ivoire.\nCinetPay vous remerci.";

}
