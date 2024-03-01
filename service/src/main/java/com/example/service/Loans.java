package com.example.service;

class Loans {

	String displayMessageFor(Loan loan) {
		return switch (loan) {
			case SecuredLoan sl -> "good job! ";
			case UnsecuredLoan(var interest) -> "ouch! that " + interest + "% interest rate is going to hurt!";
		};
	}

	@Deprecated
	String badDisplayMessageFor(Loan loan) {
		var message = "";
		if (loan instanceof SecuredLoan) {
			message = "good job! ";
		}
		if (loan instanceof UnsecuredLoan) {
			var usl = (UnsecuredLoan) loan;
			message = "ouch! that " + usl.interest() + "% interest rate is going to hurt!";
		}
		return message;
	}

}
