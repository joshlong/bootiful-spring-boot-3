package com.example.service;

class Loans {

	sealed interface Loan permits SecuredLoan, UnsecuredLoan {

	}

	record UnsecuredLoan(float interest) implements Loan {
	}

	final class SecuredLoan implements Loan {

	}

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
