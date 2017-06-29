package com.cinetpay.sdk;
/**
 * 
 * @author Istat: Toukea tatsi Jephte
 *
 */
public final class ServiceAccount {

    private double solde = 0;
    private double credit = 0;
    private double debit = 0;

    public ServiceAccount(){}

    public ServiceAccount(double solde, double credit, double debit) {
        this.solde = solde;
        this.credit = credit;
        this.debit = debit;
    }

    public void setCredit(double credit) {
        this.credit = credit;
    }

    public void setDebit(double debit) {
        this.debit = debit;
    }

    public void setSolde(double solde) {
        this.solde = solde;
    }

    public double getCredit() {
        return credit;
    }

    public double getDebit() {
	    return debit;
    }

    public double getSolde() {
	    return solde;
    }

}
